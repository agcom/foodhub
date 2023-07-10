package com.foodo;

import com.foodo.exceptions.UnloadedResourcesException;
import com.foodo.models.*;
import com.foodo.utils.Utils;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class GlobalData {

    private static final ThreadFactory daemonThreadFactory = r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);

        return thread;
    };
    private static final ThreadFactory nonDaemonThreadFactory = r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(false);

        return thread;
    };

    public static final ExecutorService globalDaemonExecutor = Executors.unconfigurableExecutorService(Executors.newCachedThreadPool(daemonThreadFactory));
    public static final ExecutorService globalNonDaemonExecutor = Executors.unconfigurableExecutorService(Executors.newCachedThreadPool(nonDaemonThreadFactory));

    public static final ScheduledExecutorService globalScheduledDaemonExecutor = Executors.unconfigurableScheduledExecutorService(Executors.newScheduledThreadPool(0, daemonThreadFactory));
    public static final ScheduledExecutorService globalScheduledNonDaemonExecutor = Executors.unconfigurableScheduledExecutorService(Executors.newScheduledThreadPool(0, nonDaemonThreadFactory));

    /**
     * Current database model
     */
    public static final class Foodo {

        public static final DbModel M = new DbModel("foodo.db", "./foodo.db");
        public static final Database DB = new Database(false, M);

        public static final class COVERED_ADDRESSES {

            public static final DbModel.Table T = M.new Table("coveredAddresses");

            /**
             * {@link com.foodo.models.Address}
             */
            public static final DbModel.Table.Column ADDRESS = T.new Column("address", Types.VARCHAR, 1);

        }

        public static final class PHRASES {

            public static final DbModel.Table T = M.new Table("phrases");

            public static final DbModel.Table.Column GROUP = T.new Column("groupp", Types.VARCHAR, 1);
            public static final DbModel.Table.Column PHRASE = T.new Column("phrase", Types.VARCHAR, 2);


        }

    }

    public static class BPServer {

        private static BPServer server = new BPServer();

        public static BPServer getServer() {

            return server;
        }

        private BPServer() {
        }

        private final String BASE_URL = "http://188.40.255.172:2001/api/user_api";
        private final String LOGIN_FUNCTION = "login_user";
        private final String SIGN_UP_FUNCTION = "signup_user";

        public BPServerResponse singUpUser(User user) throws IOException {

            HttpURLConnection connection = getConnection(SIGN_UP_FUNCTION);

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.addRequestProperty("Content-type", "Application/json");

            OutputStream output = connection.getOutputStream();

            output.write(getUserSignUpFieldsAsJson(user).toString().getBytes());
            output.close();

            Scanner inputScanner = new Scanner(connection.getInputStream());
            BPServerResponse response = new BPServerResponse(new JSONObject(inputScanner.useDelimiter("\\A").next()));
            inputScanner.close();

            return response;

        }

        public BPServerResponse loginUser(User user) throws IOException {

            return loginUser(user.getEmail(), user.getPassword());

        }

        public BPServerResponse loginUser(String username, String password) throws IOException {

            HttpURLConnection connection = getConnection(LOGIN_FUNCTION);

            connection.setRequestMethod("POST");
            connection.setDoOutput(true );
            connection.setDoInput(true);
            connection.addRequestProperty("Content-Type", "Application/json");

            OutputStream output = connection.getOutputStream();

            output.write(gerUserLoginFieldsAsJson(new User(username, password)).toString().getBytes());
            output.close();

            Scanner inputScanner = new Scanner(connection.getInputStream());
            BPServerResponse response = new BPServerResponse(new JSONObject(inputScanner.useDelimiter("\\A").next()));
            inputScanner.close();

            return response;


        }

        private HttpURLConnection getConnection(String function) throws IOException {

            URL url = null;

            try {

                switch (function) {

                    case SIGN_UP_FUNCTION:
                        url = new URL(BASE_URL + "/" + SIGN_UP_FUNCTION);
                        break;

                    case LOGIN_FUNCTION:
                        url = new URL(BASE_URL + "/" + LOGIN_FUNCTION);
                        break;

                }

            } catch (MalformedURLException e) {

                Utils.Logger.log(e);

            }

            return (HttpURLConnection) url.openConnection();

        }

        private JSONObject getUserSignUpFieldsAsJson(User user) {

            JSONObject jObj = new JSONObject();

            jObj.put("first_name", user.getFirstName());
            jObj.put("last_name", user.getLastName());
            jObj.put("email", user.getEmail());
            jObj.put("phone", user.getPhone());
            jObj.put("password", user.getPassword());

            return jObj;

        }

        private JSONObject gerUserLoginFieldsAsJson(User user) {

            JSONObject jObj = new JSONObject();

            jObj.put("username", user.getEmail());
            jObj.put("password", user.getPassword());

            return jObj;

        }

        public static class BPServerResponse {

            private final boolean status;
            private String error;
            private User userData;

            private BPServerResponse(JSONObject response) {

                this.status = response.getBoolean("status");

                if (!status) this.error = response.getString("error");
                else this.userData = new User(response.getJSONObject("UserData"));

            }

            public boolean getStatus() {
                return status;
            }

            public User getUserData() {
                return userData;
            }

            public String getError() {
                return error;
            }

            @Override
            public String toString() {
                return String.format("status=%s, error=%s, userData=%s", status, error, userData);
            }

        }

    }

    ////////////////////////////cached rowData part

    private static GlobalData instance = new GlobalData();

    public static GlobalData getInstance() {

        return instance;
    }

    private GlobalData() {
    }

    public Phrase loadPhrase(String group) throws Exception {

        Query.Row phraseRow = Foodo.DB.executeQuery(String.format("SELECT * FROM %s WHERE %s = '%s'", Foodo.PHRASES.T.NAME, Foodo.PHRASES.GROUP.NAME, group)).getRows().get(0);

        return new Phrase((String) phraseRow.getObjectHolder(Foodo.PHRASES.GROUP.INDEX).obj, (String) phraseRow.getObjectHolder(Foodo.PHRASES.PHRASE.INDEX).obj);

    }
}
