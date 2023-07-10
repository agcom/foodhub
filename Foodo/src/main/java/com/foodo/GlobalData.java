package com.foodo;

import com.foodo.models.*;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Types;
import java.util.Map;
import java.util.concurrent.*;

public class GlobalData {
	
	private static final ThreadFactory daemonThreadFactory = r -> {
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		
		return thread;
	};
	private static final ThreadFactory nonDaemonThreadFactory = r -> {
		Thread thread = new Thread(r);
		thread.setDaemon(false);
		
		return thread;
	};
	
	public static final ExecutorService globalDaemonExecutor = Executors.unconfigurableExecutorService(Executors.newCachedThreadPool(daemonThreadFactory));
	public static final ExecutorService globalNonDaemonExecutor = Executors.unconfigurableExecutorService(Executors.newCachedThreadPool(nonDaemonThreadFactory));
	
	public static final ScheduledExecutorService globalScheduledDaemonExecutor = Executors.unconfigurableScheduledExecutorService(Executors.newScheduledThreadPool(0, daemonThreadFactory));
	public static final ScheduledExecutorService globalScheduledNonDaemonExecutor = Executors.unconfigurableScheduledExecutorService(Executors.newScheduledThreadPool(0, nonDaemonThreadFactory));
	
	/**
	 * Current database model
	 */
	public static final class Foodo {
		
		public static final DbModel M = new DbModel("foodo.db", "./foodo.db");
		public static final Database DB = new Database(false, M);
		
		public static final class COVERED_ADDRESSES {
			
			public static final DbModel.Table T = M.new Table("coveredAddresses");
			
			/**
			 * {@link com.foodo.models.Address}
			 */
			public static final DbModel.Table.Column ADDRESS = T.new Column("address", Types.VARCHAR, 1);
			
		}
		
		public static final class PHRASES {
			
			public static final DbModel.Table T = M.new Table("phrases");
			
			public static final DbModel.Table.Column GROUP = T.new Column("groupp", Types.VARCHAR, 1);
			public static final DbModel.Table.Column PHRASE = T.new Column("phrase", Types.VARCHAR, 2);
			
			
		}
		
	}
	
	public static class BPServerMock {
		
		private static final BPServerMock server = new BPServerMock();
		
		public static BPServerMock getServer() {
			
			return server;
		}
		
		private BPServerMock() {
		}
		
		private final Map<String, User> users = new ConcurrentHashMap<>();
		
		public Response signUpUser(User user) throws IOException {
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
		
		public Response loginUser(User user) throws IOException {
			
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
		
		private JSONObject getUserSignUpFieldsAsJson(User user) {
			
			JSONObject jObj = new JSONObject();
			
			jObj.put("first_name", user.getFirstName());
			jObj.put("last_name", user.getLastName());
			jObj.put("email", user.getEmail());
			jObj.put("phone", user.getPhone());
			jObj.put("password", user.getPassword());
			
			return jObj;
			
		}
		
		private JSONObject gerUserLoginFieldsAsJson(User user) {
			
			JSONObject jObj = new JSONObject();
			
			jObj.put("username", user.getEmail());
			jObj.put("password", user.getPassword());
			
			return jObj;
			
		}
		
		public static class Response {
			
			private final boolean status;
			private String error;
			private User userData;
			
			private Response(String error) {
				status = false;
				this.error = error;
			}
			
			private Response(User userData) {
				
				this.status = true;
				this.userData = userData;
				
			}
			
			public boolean getStatus() {
				return status;
			}
			
			public User getUserData() {
				return userData;
			}
			
			public String getError() {
				return error;
			}
			
			@Override
			public String toString() {
				return String.format("status=%s, error=%s, userData=%s", status, error, userData);
			}
			
		}
		
	}
	
	////////////////////////////cached rowData part
	
	private static final GlobalData instance = new GlobalData();
	
	public static GlobalData getInstance() {
		
		return instance;
	}
	
	private GlobalData() {
	}
	
	public Phrase loadPhrase(String group) throws Exception {
		
		Query.Row phraseRow = Foodo.DB.executeQuery(String.format("SELECT * FROM %s WHERE %s = '%s'", Foodo.PHRASES.T.NAME, Foodo.PHRASES.GROUP.NAME, group)).getRows().get(0);
		
		return new Phrase((String) phraseRow.getObjectHolder(Foodo.PHRASES.GROUP.INDEX).obj, (String) phraseRow.getObjectHolder(Foodo.PHRASES.PHRASE.INDEX).obj);
		
	}
}
