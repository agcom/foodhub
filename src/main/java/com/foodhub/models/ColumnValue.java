package com.foodhub.models;

import java.util.Objects;

public class ColumnValue {

    public final Database.DatabaseModel.Table.Column column;
    public final String value;

    public ColumnValue(Database.DatabaseModel.Table.Column column, String value) {

        this.column = Objects.requireNonNull(column);
        this.value = value;

    }

}
