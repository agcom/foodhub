package com.foodhub.models;

/**
 * immutable class
 * rating from 0 to 5 only integers
 * to associate with restaurants and foodsList
 * saves votes count and the average rating
 */
public class Rating extends Progress<Float> {

    public static final Rating ZERO = new Rating(0);

    private int votes;

    /**
     * @param average average value of all votes
     * @param votes count of votes influenced average (value)
     */
    public Rating(float average, int votes) {

        super(0F, average, 5F);
        this.votes = votes;

    }

    /**
     * single rating vote
     * @param value
     */
    public Rating(int value) {

        super(0F, (float) value, 5F);
        this.votes = 1;

    }

    public int votes() {

        return votes;

    }

    public float rating() {

        return value;

    }

}
