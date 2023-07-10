package com.foodo.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DbModel {

    public final String DB_NAME, DB_PATH;
    public final List<Table> tables;

    public DbModel(String DB_NAME, String DB_PATH) {
        this.DB_NAME = DB_NAME;
        this.DB_PATH = DB_PATH;
        this.tables = new ArrayList<>();
    }

    @Override
    public String toString() {
        return DB_NAME;
    }

    public class Table {

        public final String NAME;
        public final List<Column> columns;

        public Table(String NAME) {

            this.NAME = NAME;
            this.columns = new ArrayList<>();
            DbModel.this.tables.add(this);

        }

        @Override
        public String toString() {
            return NAME;
        }

        public class Column {

            public final String NAME;
            public final int TYPE, INDEX;

            public Column(String NAME, int TYPE, int INDEX) {
                this.NAME = NAME;
                this.TYPE = TYPE;
                this.INDEX = INDEX;
                Table.this.columns.add(this);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Column column = (Column) o;
                return TYPE == column.TYPE &&
                        INDEX == column.INDEX &&
                        Objects.equals(NAME, column.NAME);
            }

            @Override
            public int hashCode() {
                return Objects.hash(NAME, TYPE, INDEX);
            }

            @Override
            public String toString() {
                return NAME;
            }
        }

    }

}
