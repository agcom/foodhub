package com.foodhub.utils;

import java.util.*;

public class MapResourceBundle extends ResourceBundle {

    private Map<String, Object> map;

    public MapResourceBundle(Map<String, Object> map) {

        this.map = map;

    }

    public MapResourceBundle() {

        this.map = new HashMap<>();

    }

    public void closeMap() {

        this.map = Collections.unmodifiableMap(map);

    }

    public Map<String, Object> map() {
        return map;
    }

    @Override
    protected Object handleGetObject(String key) {

        return map.get(key);

    }

    @Override
    public Enumeration<String> getKeys() {

        return new Enumeration<>() {

            private Iterator<String> keysIterator = map.keySet().iterator();

            @Override
            public boolean hasMoreElements() {

                return keysIterator.hasNext();

            }

            @Override
            public String nextElement() {

                return keysIterator.next();

            }

        };

    }

}
