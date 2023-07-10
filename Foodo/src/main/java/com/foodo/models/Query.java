package com.foodo.models;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * to save ResultSet object after closing the statement
 */
public class Query {
	
	private List<Row> rows;
	
	public Query(ResultSet set) throws SQLException {
		
		rows = new ArrayList<>();
		
		ResultSetMetaData metaData = set.getMetaData();
		
		while (set.next()) {
			
			List<ObjectHolder> rowData = new ArrayList<>();
			List<Integer> types = new ArrayList<>();
			List<Sint> sints = new ArrayList<>();
			
			for (int i = 1, columnCount = metaData.getColumnCount(); i <= columnCount; i++) {
				
				rowData.add(new ObjectHolder(set.getObject(i), metaData.getColumnType(i)));
				
				sints.add(new Sint(metaData.getColumnName(i), i));
				
			}
			
			this.new Row(sints, rowData, types);
			
		}
		
		rows = Collections.unmodifiableList(rows);
		
	}
	
	public List<Row> getRows() {
		
		return rows;
		
	}
	
	public class Row {
		
		private final List<Sint> sints;
		private final List<ObjectHolder> rowData;
		
		public Row(List<Sint> sints, List<ObjectHolder> rowData, List<Integer> types) {
			this.sints = sints;
			this.rowData = rowData;
			Query.this.rows.add(this);
		}
		
		public ObjectHolder getObjectHolder(int columnIndex) throws SQLException {
			
			int listIndex = getListIndex(columnIndex);
			
			if (listIndex == -1)
				throw new SQLException("Defined column index " + columnIndex + " not found in the query");
			
			else return rowData.get(listIndex);
			
		}
		
		public ObjectHolder getObjectHolder(String columnName) throws SQLException {
			
			int listIndex = getListIndex(columnName);
			
			if (listIndex == -1)
				throw new SQLException("Defined column name '" + columnName + "' not found in the query");
			
			else return rowData.get(listIndex);
			
		}
		
		private int getListIndex(int columnIndex) {
			
			for (int i = 0; i < sints.size(); i++) {
				
				if (sints.get(i).index == columnIndex) return i;
				
			}
			
			return -1;
			
		}
		
		private int getListIndex(String columnName) {
			
			for (int i = 0; i < sints.size(); i++) {
				
				if (sints.get(i).str.equals(columnName)) return i;
				
			}
			
			return -1;
			
		}
		
	}
	
	public final static class ObjectHolder {
		
		public final int type;
		public final Object obj;
		
		public ObjectHolder(Object obj, int type) {
			this.type = type;
			this.obj = obj;
		}
	}
	
}
