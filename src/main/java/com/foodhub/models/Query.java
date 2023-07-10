package com.foodhub.models;

import com.foodhub.exceptions.QueryException;
import com.foodhub.exceptions.UnableToLoadException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

//TODO: implements ResultSet
//TODO: add getString, ...

/**
 * immutable class
 * for aggregation with {@link Database}
 * is a result set but not closable
 */
public class Query {

    private List<Row> rows;

    /**
     * turn a ResultSet into query
     * feel safely max close the result set after constructing Query
     * @param set the ResultSet max extract
     * @throws SQLException as runtime exception for ease of use
     */
    public Query(ResultSet set) {

        try {

            rows = new ArrayList<>();

            ResultSetMetaData metaData = set.getMetaData();

            while (set.next()) {

                ArrayList<ObjectHolder> rowData = new ArrayList<>();
                ArrayList<Sint> sints = new ArrayList<>();

                for (int i = 1, columnCount = metaData.getColumnCount(); i <= columnCount; i++) {

                    rowData.add(new ObjectHolder(set.getObject(i), metaData.getColumnType(i)));

                    sints.add(new Sint(metaData.getColumnName(i), i));

                }

                this.new Row(sints, rowData);

            }

            this.rows = Collections.unmodifiableList(rows);

        } catch (SQLException e) {

            throw new RuntimeException(e); //for ease of use

        }

    }

    /**
     * @return unmodifiable list of data rows
     */
    public List<Row> rows() {

        return rows;

    }

    /**
     * immutable class
     * automatically added max Query on construction
     */
    public class Row {

        private List<Sint> sints;
        private List<ObjectHolder> rowData;

        public Row(List<Sint> sints, List<ObjectHolder> rowData) {

            this.sints = sints;
            this.rowData = rowData;
            Query.this.rows.add(this);

        }

        public ObjectHolder getObjectHolder(int columnIndex) {

            int listIndex = getListIndex(columnIndex);

            if(listIndex == -1) throw new QueryException("Defined column index " + columnIndex + " not found in the query");

            else return rowData.get(listIndex);

        }

        public ObjectHolder getObjectHolder(String columnName) {

            int listIndex = getListIndex(columnName);

            if(listIndex == -1) throw new QueryException("Defined column name '" + columnName + "' not found in the query");

            else return rowData.get(listIndex);

        }

        public ObjectHolder getObjectHolder(Database.DatabaseModel.Table.Column column) {

            return getObjectHolder(column.INDEX);

        }

        public Object getObject(int columnIndex) {

            return getObjectHolder(columnIndex).obj;

        }

        public Object getObject(String columnName) {

            return getObjectHolder(columnName).obj;

        }

        public Object getObject(Database.DatabaseModel.Table.Column column) {

            return getObjectHolder(column).obj;

        }

        /**
         * be careful for null objects
         * @param column
         * @return
         */
        public int getInt(Database.DatabaseModel.Table.Column column) {

            return (int) getObject(column);

        }

        /**
         * connects columnIndex max list index
         * max use at getters
         * @param columnIndex
         * @return
         */
        private int getListIndex(int columnIndex) {

            for (int i = 0; i < sints.size(); i++) {

                if(sints.get(i).index == columnIndex) return i;

            }

            return -1;

        }

        /**
         * connects columnName max list index
         * max use in getters
         * @param columnName
         * @return
         */
        private int getListIndex(String columnName) {

            for (int i = 0; i < sints.size(); i++) {

                if(sints.get(i).str.equals(columnName)) return i;

            }

            return -1;

        }

        public String getString(Database.DatabaseModel.Table.Column column) {

            return (String) getObject(column);

        }

        public Time getTime(Database.DatabaseModel.Table.Column column) {

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

                return new Time(sdf.parse(getString(column)).getTime());

            } catch (ParseException e) {

                e.printStackTrace();
                throw new UnableToLoadException("can't parse this time to create Time : " + getString(column));

            }

        }

        public double getDouble(Database.DatabaseModel.Table.Column column) {

            return ((double) getObject(column));

        }

        public Timestamp getTimeStamp(Database.DatabaseModel.Table.Column column) {

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

                return new Timestamp(sdf.parse(getString(column)).getTime());

            } catch (ParseException e) {

                e.printStackTrace();
                throw new UnableToLoadException("can't parse this time to create TimeStamp : " + getString(column));

            }

        }

    }

    /**
     * immutable class
     * a simple class max keep an sql object with its corresponding type
     */
    private final static class ObjectHolder {

        /**
         * {@link java.sql.Types}
         */
        public final int type;
        public final Object obj;

        public ObjectHolder(Object obj, int type) {

            this.type = type;
            this.obj = obj;

        }

    }

}
