package com.foodhub;

import com.foodhub.models.*;
import com.foodhub.utils.Utils;
import javafx.application.Application;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * global resources like executors and database model
 * use singleton instance to access cached data
 */
public class Global {
	
	public static ExecutorService daemonExecutor = Executors.newCachedThreadPool(Utils::newDaemonThread);
	public static ScheduledExecutorService scheduledDaemonExecutor = Executors.newScheduledThreadPool(0, Utils::newDaemonThread);
	
	/**
	 * current database model
	 */
	public static final class FoodHub {
		
		public static final Database DB;
		
		static {
			final var dbPathStr = Global.instance().appInstance.getParameters().getNamed().get("db");
			final Path dbPath;
			if (dbPathStr == null) {
				dbPath = Path.of("data", "foodhub.db");
				try {
					Files.createDirectories(dbPath.getParent());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				dbPath = Path.of(dbPathStr);
			}
			
			DB = new Database.DatabaseModel("foodhub.db", dbPath.toAbsolutePath().toString()).database();
		}
		
		static {
			List.of(
					"""
							CREATE TABLE [orderItems](
							  [orderId] INTGER NOT NULL,
							  [foodId] INTEGER NOT NULL,
							  [count] INTEGER NOT NULL)""", """
							CREATE TABLE [foods](
							  [id] INTEGER PRIMARY KEY,
							  [restaurantId] INTEGER NOT NULL,
							  [name] VARCHAR NOT NULL,
							  [type] VARCHAR NOT NULL,
							  [price] FLOAT NOT NULL,
							  [imageUrl] VARCHAR)""", """
							CREATE TABLE [restaurants](
							   [id] INTEGER PRIMARY KEY,
							   [name] VARCHAR NOT NULL,
							   [address] VARCHAR NOT NULL,
							   [logoUrl] VARCHAR,
							   [imageUrl] VARCHAR,
							   [deliveryTime] TIME,
							   [openHours] VARCHAR)""", """
							CREATE TABLE [orders](
							  [id] INTEGER PRIMARY KEY,
							  [email] VARCHAR NOT NULL,
							  [date] TIMESTAMP NOT NULL,
							  [deliveryAddress] VARCHAR NOT NULL)""", """
							CREATE TABLE [votes](
							  [email] VARCHAR NOT NULL,
							  [restaurantId] INTEGER NOT NULL,
							  [foodId] INTEGER,
							  [rating] TINYINT NOT NULL)"""
			).forEach(tableSql -> Utils.DatabaseHelper.ensureTableExists(DB.model(), tableSql));
		}
		
		public static final Path relativeImagesDir;
		
		static {
			final var relativeImagesDirStr = Global.instance().appInstance.getParameters().getNamed().get("relative-images-dir");
			
			if (relativeImagesDirStr != null)
				relativeImagesDir = Path.of(relativeImagesDirStr).toAbsolutePath();
			else relativeImagesDir = Path.of(DB.model().PATH).getParent().resolve("images").toAbsolutePath();
		}
		
		public static final class FOODS {
			
			public static final Database.DatabaseModel.Table T = DB.model().new Table("foods");
			
			public static final Database.DatabaseModel.Table.Column FOOD_ID = T.new Column("foodId", Types.INTEGER, 1);
			public static final Database.DatabaseModel.Table.Column RESTAURANT_ID = T.new Column("restaurantId", Types.INTEGER, 2);
			public static final Database.DatabaseModel.Table.Column NAME = T.new Column("name", Types.VARCHAR, 3);
			public static final Database.DatabaseModel.Table.Column TYPE = T.new Column("type", Types.VARCHAR, 4);
			public static final Database.DatabaseModel.Table.Column PRICE = T.new Column("price", Types.FLOAT, 5);
			public static final Database.DatabaseModel.Table.Column IMAGE_URL = T.new Column("imageUrl", Types.VARCHAR, 6);
			
			/**
			 * make sure restaurant id already set
			 *
			 * @param restaurant
			 * @return
			 */
			public static List<Restaurant.Food> loadRestaurantFoods(Restaurant restaurant) {
				
				return Utils.DatabaseHelper.select(row -> restaurant.new Food(row), new ColumnValue(RESTAURANT_ID, String.valueOf(restaurant.getId())));
				
			}
			
			public static Restaurant.Food getFood(int foodId) {
				
				return foodRestaurant(foodId).getFoods().stream().filter(food -> food.getId() == foodId).findFirst().orElse(null);
				
			}
			
			public static Restaurant foodRestaurant(int foodId) {
				
				return RESTAURANTS.all().stream().filter(restaurant -> restaurant.getFoods().stream().map(Restaurant.Food::getId).anyMatch(id -> id == foodId)).findFirst().orElse(null);
				
			}
			
		}
		
		public static final class ORDER_ITEMS {
			
			public static final Database.DatabaseModel.Table T = DB.model().new Table("orderItems");
			
			public static final Database.DatabaseModel.Table.Column ORDER_ID = T.new Column("orderId", Types.INTEGER, 1);
			public static final Database.DatabaseModel.Table.Column FOOD_ID = T.new Column("foodId", Types.INTEGER, 2);
			public static final Database.DatabaseModel.Table.Column COUNT = T.new Column("count", Types.INTEGER, 3);
			
			public static List<Order.OrderItem> loadOrderItems(Order order) {
				
				return Utils.DatabaseHelper.select(row -> order.new OrderItem(row.getInt(COUNT), FOODS.getFood(row.getInt(FOOD_ID))), new ColumnValue(ORDER_ID, String.valueOf(order.getId())));
				
			}
			
		}
		
		public static final class ORDERS {
			
			public static final Database.DatabaseModel.Table T = DB.model().new Table("orders");
			
			public static final Database.DatabaseModel.Table.Column ID = T.new Column("id", Types.INTEGER, 1);
			public static final Database.DatabaseModel.Table.Column EMAIL = T.new Column("email", Types.VARCHAR, 2);
			public static final Database.DatabaseModel.Table.Column DATE = T.new Column("date", Types.TIMESTAMP, 3);
			public static final Database.DatabaseModel.Table.Column DELIVERY_ADDRESS = T.new Column("deliveryAddress", Types.VARCHAR, 4);
			
			public static List<Order> getUserOrders(UserData user) {
				
				return Utils.DatabaseHelper.select(Order::new, new ColumnValue(EMAIL, user.getEmail()));
				
			}
			
			public static void insertOrder(Order order) {
				
				StringBuilder sb = new StringBuilder();
				sb.append("INSERT INTO ").append(T.NAME).append("(").append(EMAIL.NAME).append(", ").append(DATE.NAME).append(", ").append(DELIVERY_ADDRESS.NAME).append(") VALUES ('").append(order.getEmail()).append("', CURRENT_TIMESTAMP, '").append(order.getDeliveryAddress().getAddress()).append("'); ");
				DB.execute(sb.toString());
				sb.delete(0, sb.length());
				
				sb.append("SELECT * FROM ").append(T.NAME).append(" WHERE ").append(ID.NAME).append(" = (SELECT MAX(").append(ID.NAME).append(") FROM ").append(T.NAME).append(");");
				int orderId = DB.executeQuery(sb.toString(), ID).rows().get(0).getInt(ID);
				sb.delete(0, sb.length());
				
				for (int i = 0; i < order.getItems().size(); i++) {
					
					sb.append("INSERT INTO ").append(ORDER_ITEMS.T.NAME).append(" (").append(ORDER_ITEMS.ORDER_ID.NAME).append(", ").append(ORDER_ITEMS.FOOD_ID.NAME).append(", ").append(ORDER_ITEMS.COUNT.NAME).append(") VALUES (").append(orderId).append(", ").append(order.getItems().get(i).getFood().getId()).append(", ").append(order.getItems().get(i).getCount()).append("); ");
					DB.execute(sb.toString());
					sb.delete(0, sb.length());
					
				}
				
			}
			
		}
		
		public static final class RESTAURANTS {
			
			public static final Database.DatabaseModel.Table T = DB.model().new Table("restaurants");
			
			public static final Database.DatabaseModel.Table.Column ID = T.new Column("id", Types.INTEGER, 1);
			public static final Database.DatabaseModel.Table.Column NAME = T.new Column("name", Types.VARCHAR, 2);
			public static final Database.DatabaseModel.Table.Column ADDRESS = T.new Column("address", Types.VARCHAR, 3);
			public static final Database.DatabaseModel.Table.Column LOGO_URL = T.new Column("logoUrl", Types.VARCHAR, 4);
			public static final Database.DatabaseModel.Table.Column IMAGE_URL = T.new Column("imageUrl", Types.VARCHAR, 5);
			public static final Database.DatabaseModel.Table.Column DELIVERY_TIME = T.new Column("deliveryTime", Types.TIME, 6);
			public static final Database.DatabaseModel.Table.Column OPEN_HOURS = T.new Column("openHours", Types.VARCHAR, 7);
			
			private static List<Restaurant> restaurants;
			
			public static List<Restaurant> all() {
				
				if (restaurants == null) restaurants = Utils.DatabaseHelper.selectAll(Restaurant::new, T);
				
				return restaurants;
				
			}
			
		}
		
		public static final class VOTES {
			
			public static final Database.DatabaseModel.Table T = DB.model().new Table("votes");
			
			public static final Database.DatabaseModel.Table.Column USER_NAME = T.new Column("userName", Types.VARCHAR, 1);
			public static final Database.DatabaseModel.Table.Column RESTAURANT_ID = T.new Column("restaurantId", Types.INTEGER, 2);
			public static final Database.DatabaseModel.Table.Column FOOD_ID = T.new Column("foodId", Types.INTEGER, 3);
			public static final Database.DatabaseModel.Table.Column RATING = T.new Column("rating", Types.TINYINT, 4);
			
			public static List<Vote> all() {
				
				return Utils.DatabaseHelper.selectAll(Vote::new, T);
				
			}
			
			public static Rating restaurantRating(int restaurantId) {
				
				List<Vote> votes = Utils.DatabaseHelper.select(Vote::new, new ColumnValue(RESTAURANT_ID, String.valueOf(restaurantId)), new ColumnValue(FOOD_ID, null));
				
				int sum = votes.stream().map(Vote::rating).reduce(Integer::sum).orElse(0);
				
				return new Rating((float) sum / votes.size(), votes.size());
				
			}
			
			public static Rating foodRating(int restaurantId, int foodId) {
				
				List<Vote> votes = Utils.DatabaseHelper.select(Vote::new, new ColumnValue(RESTAURANT_ID, String.valueOf(restaurantId)), new ColumnValue(FOOD_ID, String.valueOf(foodId)));
				
				int sum = votes.stream().map(Vote::rating).reduce(Integer::sum).orElse(0);
				
				return new Rating((float) sum / votes.size(), votes.size());
				
			}
			
		}
		
	}
	
	public static final class USER {
		
		public static final Database DB;
		
		static {
			final var dbPathStr = Global.instance().appInstance.getParameters().getNamed().get("userDb");
			final Path dbPath;
			if (dbPathStr == null) {
				dbPath = Path.of("data", "user.db");
				try {
					Files.createDirectories(dbPath.getParent());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				dbPath = Path.of(dbPathStr);
			}
			
			DB = new Database.DatabaseModel("user.db", dbPath.toAbsolutePath().toString()).database();
		}
		
		static {
			
			Utils.DatabaseHelper.ensureDatabaseExists(DB.model(), "CREATE TABLE [preferences] (" +
					"[key] VARCHAR PRIMARY KEY NOT NULL, " +
					"[value] VARCHAR) WITHOUT ROWID;");
			
		}
		
		public static final class PREFERENCES {
			
			public static final Database.DatabaseModel.Table T = DB.model().new Table("preferences");
			
			public static final Database.DatabaseModel.Table.Column KEY = T.new Column("key", Types.VARCHAR, 1);
			public static final Database.DatabaseModel.Table.Column VALUE = T.new Column("value", Types.VARCHAR, 2);
			
			public static String getSavedEmail() {
				
				String savedEmail = null;
				
				try {
					
					savedEmail = Utils.DatabaseHelper.select(row -> row.getString(VALUE), new ColumnValue(KEY, "savedEmail")).get(0);
					
				} catch (Exception ignored) {
				}
				
				return savedEmail;
				
			}
			
			public static void replaceSavedEmail(String email) {
				
				Utils.DatabaseHelper.replace(new ColumnValue(KEY, "savedEmail"), new ColumnValue(VALUE, email));
				
			}
			
		}
		
	}
	
	public static class BPServerMock {
		
		private static final BPServerMock server = new BPServerMock();
		
		public static BPServerMock instance() {
			
			return server;
		}
		
		private BPServerMock() {
		}
		
		private final Map<String, UserData> users = new ConcurrentHashMap<>();
		
		public Response signUpUser(UserData user) throws IOException {
			try {
				Thread.sleep((long) (Math.random() * 7000));
			} catch (InterruptedException e) {
				return new Response("Something went wrong with the network delay simulation.");
			}
			
			if (users.putIfAbsent(user.getEmail(), user) != null) {
				return new Response("A user with the given email already exists.");
			} else {
				return new Response(user);
			}
		}
		
		public Response loginUser(UserData user) throws IOException {
			
			return loginUser(user.getEmail(), user.getPassword());
			
		}
		
		public Response loginUser(String username, String password) throws IOException {
			try {
				Thread.sleep((long) (Math.random() * 7000));
			} catch (InterruptedException e) {
				return new Response("Something went wrong with the network delay simulation.");
			}
			
			final var user = users.get(username);
			if (user != null && user.getPassword().equals(password))
				return new Response(user);
			else return new Response("Wrong username or password.");
		}
		
		public static class Response {
			
			private final boolean status;
			private String error;
			private UserData userData;
			
			private Response(String error) {
				status = false;
				this.error = error;
			}
			
			private Response(UserData userData) {
				
				this.status = true;
				this.userData = userData;
				
			}
			
			public boolean getStatus() {
				return status;
			}
			
			public UserData getUserData() {
				return userData;
			}
			
			public String getError() {
				return error;
			}
			
			@Override
			public String toString() {
				return status ? String.format("UserData: %s", userData) : String.format("Error: %s", error);
			}
			
		}
		
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	private static final Global globalCache = new Global();
	
	public Application appInstance = null;
	
	private Global() {
	}
	
	public static Global instance() {
		return globalCache;
	}
	
	private final String COMMON_PATH = "/com/foodhub/";
	
	/**
	 * for ease of use
	 *
	 * @param url
	 * @return group folders + url
	 */
	public URL url(String url) {
		
		return getClass().getResource(COMMON_PATH + url);
		
	}
	
	public InputStream urlStream(String url) {
		
		return getClass().getResourceAsStream(COMMON_PATH + url);
		
	}
	
}
