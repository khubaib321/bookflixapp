package com.example.kkrb0;

public class Utils {

    public static final Integer READ_BOOK = 1;
    public static final Integer USER_LOGIN = 2;
    public static final Integer SAVE_PROGRESS = 3;
    private static final Integer ENV_LOCAL = 1;

    private static String serverEmulator = "10.0.2.2";
    private static String serverLocalhost = "localhost";
    private static String serverProduction = "kitabkhana.com.pk";
    private static final Integer ENV_EMULATOR = 2;
    private static final Integer ENV_PRODUCTION = 3;
    private static final Integer CURRENT_ENV = ENV_EMULATOR;
    private static String readBookURL = "http://" + getHostname() + "/bookflixapi/books/read.php";
    private static String userLoginURL = "http://" + getHostname() + "/bookflixapi/users/login.php";
    private static String saveProgressURL = "http://" + getHostname() + "/bookflixapi/users/user_reading.php";

    private static String getApiUrl(final int type) {
        if (type == READ_BOOK) {
            return readBookURL;
        } else if (type == USER_LOGIN) {
            return userLoginURL;
        } else if (type == SAVE_PROGRESS) {
            return saveProgressURL;
        } else {
            return "";
        }
    }

    public static String getPreparedApiUrl(int type, String urlParams[]) {
        String preparedUrl = getApiUrl(type);
        if (preparedUrl.length() == 0) {
            return preparedUrl;
        }
        String separator;
        for (int i = 0; i < urlParams.length; ++i) {
            if (i == 0) {
                separator = "?";
            } else {
                separator = "&";
            }
            preparedUrl += separator + urlParams[i];
        }
        return preparedUrl;

    }

    public static String getHostname() {
        if (CURRENT_ENV == ENV_LOCAL) {
            return serverLocalhost;
        } else if (CURRENT_ENV == ENV_EMULATOR) {
            return serverEmulator;
        } else if (CURRENT_ENV == ENV_PRODUCTION) {
            return serverProduction;
        } else {
            return "";
        }

    }

    public static String geteBookImageUrl(String cover) {
        String eBookUrl = "http://" + getHostname() + "/bookflixapi/" + cover;
        return eBookUrl;
    }

    public static void newHttpRequest(AsyncTaskPostExecute context, Integer type, String method, String url) {
        HttpRequest req = new HttpRequest(context);
        req.setRequestType(type);
        req.setRequestMethod(method);
//        Log.e("API REQUEST", String.valueOf(type) + " - " + method + " - " + url);
        req.execute(url);
    }
}
