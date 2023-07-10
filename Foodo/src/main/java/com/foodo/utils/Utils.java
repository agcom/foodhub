package com.foodo.utils;

import javafx.application.Platform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

public class Utils {

    public static class Logger {

        private static FileWriter logger;
        public final static String LOG_PATH = "C:/Users/Agcom/Documents/IdeaProjects/Foodo/foodoAppLogs.log";
        static {

            try {

                logger = new FileWriter(new File(LOG_PATH), true);

            } catch (IOException e) {

                e.printStackTrace();
                Platform.exit();

            }

        }
        static {

            log("New Session");

        }


        public static String log(String log) {

            try {

                logger.write("\n" + new Date(System.currentTimeMillis()).toString() + ": " + log);
                logger.flush();

            } catch (IOException e) {

                e.printStackTrace();

            }

            return log;

        }

        public static String log(Exception e) {

            return log(e, true);

        }

        public static String log(Exception e, boolean important) {

            e.printStackTrace();

            return log(e.getClass().getSimpleName() + ", " + e.getMessage() + (important ? "; Important" : ";"));

        }

    }

    public static boolean isBlank(String str) {

        return str == null || str.length() == 0 || str.matches("\\s*");

    }

    public static Stream<Boolean> stream(boolean[] array) {

        Boolean[] streamableArray = new Boolean[array.length];

        for (int i = 0; i < array.length; i++) {

            streamableArray[i] = array[i];

        }

        return Arrays.stream(streamableArray);

    }

}
