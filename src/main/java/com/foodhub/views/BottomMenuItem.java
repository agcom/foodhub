package com.foodhub.views;

import javafx.beans.NamedArg;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import javax.swing.event.HyperlinkEvent;
import java.util.Arrays;

//TODO: animate image changes

public class BottomMenuItem extends NodeSwitcher {

    private ObjectProperty<Node> rawNode = new SimpleObjectProperty<>(this, "rawNode"), boldNode = new SimpleObjectProperty<>(this, "boldNode");
    private ObjectProperty<Runnable> onAction = new SimpleObjectProperty<>(this, "onAction");
    boolean isGroupCall = false; //TODO: a property with ability to disable listeners for a moment
    private BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);
    private final BottomMenu group;

    public BottomMenuItem(@NamedArg(value = "rawNode") Node rawNode, @NamedArg(value = "boldNode") Node boldNode, @NamedArg(value = "group") BottomMenu group) {

        super(Arrays.asList(rawNode, boldNode), 0);

        super.getStyleClass().add("bottom-menu-item");

        this.group = group;
        setRawNode(rawNode);
        setBoldNode(boldNode);

        group.getItems().add(this);
        selected.addListener((observable, oldValue, newValue) -> {

            if(isGroupCall || newValue == oldValue) return;

            if(newValue) {

                group.setSelectedItemIndex(group.getItems().indexOf(this));

            } else {

                group.setSelectedItemIndex(-1);

            }

        });
        super.setOnMouseClicked(event -> group.setSelectedItemIndex(group.getItems().indexOf(this)));

        super.currentNodeIndexProperty().bind(Bindings.when(selected).then(1).otherwise(0));

    }

    public Node getRawNode() {
        return rawNode.get();
    }

    public ObjectProperty<Node> rawNodeProperty() {
        return rawNode;
    }

    public void setRawNode(Node rawNode) {
        this.rawNode.set(rawNode);
    }

    public Node getBoldNode() {
        return boldNode.get();
    }

    public ObjectProperty<Node> boldNodeProperty() {
        return boldNode;
    }

    public void setBoldNode(Node boldNode) {
        this.boldNode.set(boldNode);
    }

    public Runnable getOnAction() {
        return onAction.get();
    }

    public ObjectProperty<Runnable> onActionProperty() {
        return onAction;
    }

    public void setOnAction(Runnable onAction) {
        this.onAction.set(onAction);
    }

    public boolean isSelected() {

        return selected.get();

    }

    public BooleanProperty selectedProperty() {

        return selected;

    }

    public void setSelected(boolean selected) {

        this.selected.set(selected);

    }

}
