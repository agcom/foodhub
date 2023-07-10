package com.foodo.models;


import com.foodo.exceptions.UnableToLoad;
import com.foodo.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class Database {
	
	private boolean keepConnection = false;
	private Connection connection;
	private final DbModel model;
	
	/**
	 * @param keepConnection whether to keep the connection open for further calls or close immediately after work
	 */
	public Database(boolean keepConnection, DbModel model) {
		this.keepConnection = keepConnection;
		this.model = model;
	}
	
	public Query executeQuery(String sql, DbModel.Table.Column... columns) throws Exception {
		
		return dbConnectionWork(() -> {
			
			if (columns == null || columns.length == 0) {
				
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					
					return new Query(statement.executeQuery());
					
				}
				
			} else {
				
				try (PreparedStatement statement = connection.prepareStatement(sql, Arrays.stream(columns).mapToInt(column -> column.INDEX).toArray())) {
					
					return new Query(statement.executeQuery());
					
				}
				
			}
			
		});
		
	}
	
	public void execute(String sql) throws Exception {
		
		dbConnectionWork((Callable<Void>) () -> {
			
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				
				statement.execute();
				
			}
			
			return null;
			
		});
		
	}
	
	private <V> V dbConnectionWork(Callable<V> work) throws Exception {
		
		try {
			
			if (connection == null || connection.isClosed()) {
				
				connection = DriverManager.getConnection("jdbc:sqlite:" + model.DB_PATH);
				
			}
			
		} catch (SQLException e) {
			
			Utils.Logger.log(e);
			
			throw new UnableToLoad("Could not initialize the database connection");
			
		}
		
		V result = work.call();
		
		if (!keepConnection) {
			
			try {
				
				connection.close();
				
			} catch (SQLException e) {
				
				e.printStackTrace();
				Utils.Logger.log(e);
				
				throw new UnsupportedOperationException("Unable to close database connection");
				
			}
			
		}
		
		return result;
		
	}
	
	public int tableRows(DbModel.Table table) throws Exception {
		
		return ((int) executeQuery(String.format("SELECT COUNT(*) FROM %s", table.NAME)).getRows().get(0).getObjectHolder(1).obj);
		
	}
	
}
