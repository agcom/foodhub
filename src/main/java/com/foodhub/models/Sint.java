package com.foodhub.models;

/**
 * a simple class to save an integer beside a string
 * sample usage: {@link Query} to save column index beside column name
 */
public class Sint {

    public final String str;
    public final int index;

    public Sint(String str, int index) {

        this.str = str;
        this.index = index;

    }

}
