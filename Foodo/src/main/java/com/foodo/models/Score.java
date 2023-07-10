package com.foodo.models;

public class Score {

    /**
     * min and max are inclusive
     */
    private int min, max, score;

    public Score(int min, int max) {

        this.min = min;
        this.max = max;

    }

    public void setScore(int score) {

        if (score > max) this.score = max;
        else if (score < min) this.score = min;
        else this.score = score;
    }

    public int getScore() {

        return score;

    }

    public float getScoreFraction() {

        return ((float) score) / (max - min);

    }

    public int getScorePercent() {

        return Math.round(getScoreFraction() * 100);

    }

}
