package com.foodhub.utils;

import javafx.scene.Node;

/**
 * determines that the data can be showed (can turn into view)
 */
public interface Graphical {

    Node graphic();

    default Node rawGraphic() {

        return graphic();

    }

    default boolean updateGraphic(Node node) {

        return false;

    }

}
