package com.foodhub.utils;

import javafx.beans.NamedArg;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class SingleResourceBundle extends ResourceBundle {

    private Object resource;
    private String key;

    public SingleResourceBundle(@NamedArg(value = "key") String key, @NamedArg(value = "resource") Object resource) {

        this.resource = resource;
        this.key = key;

    }

    @Override
    protected Object handleGetObject(String key) {

        if(this.key.equals(key))
            return resource;
        else return null;

    }

    @Override
    public Enumeration<String> getKeys() {

        return new Enumeration<>() {

            private int index = 0;

            @Override
            public boolean hasMoreElements() {

                return index == 0;

            }

            @Override
            public String nextElement() {

                return index++ == 0 ? key : null;

            }

        };

    }

}
