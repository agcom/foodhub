package com.foodhub.utils;

import com.foodhub.models.ColumnValue;
import com.foodhub.models.Database;
import com.foodhub.models.Query;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * set of static methods and classes for ease of use
 */
public class Utils {

    /**
     * daemon thread factory
     *
     * @param runnable
     * @return a daemon thread
     */
    public static Thread newDaemonThread(Runnable runnable) {

        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;

    }

    /**
     * kills the node by managed = false and visible = false
     *
     * @param node   to kill
     * @param deploy kill or redo kill
     */
    public static void setDeploy(Node node, boolean deploy) {

        if (deploy) {

            node.setVisible(false);
            node.setManaged(false);

        } else {

            node.setManaged(true);
            node.setVisible(true);

        }

    }

    /**
     * average color of an image by turning it into a 1 pixel image
     *
     * @param image
     * @return color of the one pixel
     */
    public static Color performanceAverageColor(Image image) {

        Image px = new Image(image.getUrl(), 1, 1, false, false);

        return px.getPixelReader().getColor(0, 0);

    }

    /**
     * average color of an image
     * calculates average of all pixels color
     *
     * @param image
     * @return actual average color
     */
    public static Color actualAverageColor(Image image) {

        double redBucket = 0, greenBucket = 0, blueBucket = 0, alphaBucket = 0;
        long pixelCount = 0;

        PixelReader reader = image.getPixelReader();

        for (int i = 0; i < image.getHeight(); i++) {

            for (int j = 0; j < image.getWidth(); j++) {

                Color color = reader.getColor(j, i);
                redBucket += color.getRed();
                greenBucket += color.getGreen();
                blueBucket += color.getBlue();
                alphaBucket += color.getOpacity();

                pixelCount++;

            }

        }

        return Color.color(redBucket / pixelCount, greenBucket / pixelCount, blueBucket / pixelCount, alphaBucket / pixelCount);

    }

    /**
     * @param color
     * @return hex format of the color; sample = "#123456FF"
     */
    public static String toWeb(Color color) {

        return "#" + Integer.toString((int) Math.round(color.getRed() * 255), 16) + Integer.toString((int) Math.round(color.getGreen() * 255), 16) + Integer.toString((int) Math.round(color.getBlue() * 255), 16) + Integer.toString((int) Math.round(color.getOpacity() * 255), 16);

    }

    public static final class DatabaseHelper {

        public static <T> List<T> selectAll(Function<Query.Row, T> rowFactory, Database.DatabaseModel.Table table) {

            Query query = table.model().database().executeQuery("SELECT * FROM " + table.NAME);

            ArrayList<T> items = new ArrayList<>(query.rows().size());

            query.rows().stream().map(rowFactory).forEach(items::add);

            return items;

        }

        public static <T> List<T> select(Function<Query.Row, T> rowFactory, ColumnValue... specifiers) {

            if (specifiers.length == 0) throw new UnsupportedOperationException("No specifiers chosen");

            Database.DatabaseModel.Table table = specifiers[0].column.getTable();

            if (!Arrays.stream(specifiers).allMatch(columnValue -> columnValue.column.getTable() == table))
                throw new IllegalArgumentException("columns specifiers from different tables");

            StringBuilder sb = new StringBuilder();

            sb.append("SELECT * FROM ").append(table.NAME).append(" WHERE ");

            for (int i = 0; i < specifiers.length; i++) {

                sb.append(specifiers[i].column.NAME);

                if (specifiers[i].value == null) sb.append(" IS NULL");
                else sb.append(" = '").append(specifiers[i].value).append("'");

                if (i != specifiers.length - 1) sb.append(" AND ");

            }

            Query query = specifiers[0].column.getTable().model().database().executeQuery(sb.toString());

            ArrayList<T> items = new ArrayList<>(query.rows().size());

            for (Query.Row row : query.rows()) {

                items.add(rowFactory.apply(row));

            }

            return items;

        }

        public static void ensureDatabaseExists(Database.DatabaseModel model, String initSql) {

            if (Files.notExists(Paths.get(model.PATH))) {

                try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + model.PATH);
                     PreparedStatement statement = connection.prepareStatement(initSql)) {

                    statement.execute();

                } catch (SQLException e) {
                    // NOP
                }

            }

        }

        public static void ensureTableExists(Database.DatabaseModel model, String tableSql) {
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + model.PATH);
                 PreparedStatement statement = connection.prepareStatement(tableSql)) {
                statement.execute();
            } catch (SQLException e) {
                // NOP
            }

        }

        public static void replace(ColumnValue... data) {

            if (data == null || data.length == 0) return;

            Database.DatabaseModel.Table table = data[0].column.getTable();

            if (!Arrays.stream(data).skip(1).allMatch(cv -> cv.column.getTable().equals(table)))
                throw new IllegalArgumentException("Columns from different tables");

            StringBuilder sb = new StringBuilder("REPLACE INTO ");
            sb.append(table.NAME).append("(");

            for (int i = 0; i < data.length; i++) {

                sb.append(data[i].column.NAME);

                if (i != data.length - 1) sb.append(", ");

            }

            sb.append(") VALUES (");

            for (int i = 0; i < data.length; i++) {

                sb.append("'").append(data[i].value).append("'");

                if (i != data.length - 1) sb.append(", ");

            }

            sb.append(");");

            table.model().database().execute(sb.toString());

        }

    }

    public static <T> void fill(T[] array, Supplier<T> supplier) {

        for (int i = 0; i < array.length; i++) {

            array[i] = supplier.get();

        }

    }

    public static Stream<Boolean> stream(boolean[] array) {

        Boolean[] streamableArray = new Boolean[array.length];

        for (int i = 0; i < array.length; i++) {

            streamableArray[i] = array[i];

        }

        return Arrays.stream(streamableArray);

    }

    public static int treeItemDepth(TreeItem<?> item) {

        int depth = 0;

        while ((item = item.getParent()) != null) {

            depth++;

        }

        return depth;

    }

    public static int treeItemIndex(TreeItem<?> item) {

        int index = 0;

        while ((item = item.previousSibling()) != null) {

            index++;

        }

        return index;

    }

}
