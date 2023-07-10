package com.foodhub.views;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Slider extends StackPane {

    private ObservableList<Region> nodes = FXCollections.observableArrayList();
    private IntegerProperty currentSlideIndex = new SimpleIntegerProperty(this, "currentSlideIndex", -1);
    private ObjectProperty<Duration> duration = new SimpleObjectProperty<>(this, "duration", Duration.seconds(0.2));
    private TranslateTransition inSlide = new TranslateTransition(getDuration());
    private TranslateTransition outSlide = new TranslateTransition(getDuration());
    private ObjectProperty<Interpolator> interpolator = new SimpleObjectProperty<>(this, "interpolator", Interpolator.LINEAR);

    public Slider() {

        currentSlideIndex.addListener((observable, oldValue, newValue) -> slide(oldValue.intValue() >= 0 ? nodes.get(oldValue.intValue()) : null, newValue.intValue() >= 0 ? nodes.get(newValue.intValue()) : null, newValue.intValue() > oldValue.intValue()));
        duration.addListener(observable -> {

            inSlide.setDuration(getDuration());
            outSlide.setDuration(getDuration());

        });

        interpolator.addListener(observable -> {
            inSlide.setInterpolator(getInterpolator());
            outSlide.setInterpolator(getInterpolator());
        });

    }

    private void slide(Region out, Region in, boolean left) {

        resetSlideAnimations();

        if (in != null) inSlide.setNode(in);
        if (out != null) outSlide.setNode(out);

        if (left) {

            if (in != null) {

                inSlide.setFromX(super.getWidth() / 2 + in.getWidth() / 2);
                inSlide.setToX(0);
                in.setTranslateX(inSlide.getFromX());

            }

            if (out != null) {

                outSlide.setFromX(0);
                outSlide.setToX(-super.getWidth() / 2 - out.getWidth() / 2);

            }


        } else {

            if (in != null) {

                inSlide.setFromX(-super.getWidth() / 2 - in.getWidth() / 2);
                inSlide.setToX(0);
                in.setTranslateX(inSlide.getFromX());

            }

            if (out != null) {

                outSlide.setFromX(0);
                outSlide.setToX(super.getWidth() / 2 + out.getWidth() / 2);

            }

        }

        if(in != null) {

            super.getChildren().add(in);
            inSlide.setOnFinished(event -> in.setTranslateX(0));
            inSlide.playFromStart();

        }

        if(out != null) {

            outSlide.setOnFinished(event -> {

                super.getChildren().remove(out);
                out.setTranslateX(0);

            });
            outSlide.playFromStart();

        }

    }

    private void resetSlideAnimations() {

        inSlide.stop();
        if(inSlide.getOnFinished() != null) inSlide.getOnFinished().handle(null);
        outSlide.stop();
        if(outSlide.getOnFinished() != null) outSlide.getOnFinished().handle(null);

    }

    public ObservableList<Region> getNodes() {

        return nodes;

    }

    public int getCurrentSlideIndex() {
        return currentSlideIndex.get();
    }

    public IntegerProperty currentSlideIndexProperty() {
        return currentSlideIndex;
    }

    public void setCurrentSlideIndex(int currentSlideIndex) {
        this.currentSlideIndex.set(currentSlideIndex);
    }

    public Duration getDuration() {
        return duration.get();
    }

    public ObjectProperty<Duration> durationProperty() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration.set(duration);
    }

    public Interpolator getInterpolator() {
        return interpolator.get();
    }

    public ObjectProperty<Interpolator> interpolatorProperty() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator.set(interpolator);
    }
}
