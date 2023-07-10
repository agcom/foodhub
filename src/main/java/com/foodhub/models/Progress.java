package com.foodhub.models;

/**
 * a max, a min + a current value
 * @param <T>
 */
public class Progress<T extends Comparable<T>> extends Range<T> {

    protected T value;

    public Progress(T min, T value, T max) {

        super(min, max);

        this.value = value;

    }

    /**
     * sets the value; also cares about out of bound values
     * @param value
     */
    public void setValue(T value) {

        if(value.compareTo(max) > 0) {//bigger than max

            this.value = max;

        } else if(value.compareTo(min) < 0) {//smaller than min

            this.value = min;

        } else this.value = value;

    }

}
