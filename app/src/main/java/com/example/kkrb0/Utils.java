package com.example.kkrb0;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

public class Utils {

    public static final Integer SEARCH_BOOK = 4;
    public static final Integer READ_BOOK = 1;
    public static final Integer READ_BOOK_PAGES = 5;
    public static final Integer USER_LOGIN = 2;
    private static final Integer ENV_LOCAL = 1;
    public static final Integer SAVE_PROGRESS = 3;


    private static String serverEmulator = "10.0.2.2";
    private static String serverLocalhost = "localhost";
    private static String serverProduction = "kitabkhana.com.pk";

    private static final Integer ENV_EMULATOR = 2;
    private static final Integer ENV_PRODUCTION = 3;
    private static final Integer CURRENT_ENV = ENV_PRODUCTION;

    private static String readBookURL = "http://" + getHostname() + "/bookflixapi/books/read.php";
    private static String searchBookUrl = "http://" + getHostname() + "/bookflixapi/books/search.php";
    private static String userLoginURL = "http://" + getHostname() + "/bookflixapi/users/login.php";
    private static String saveProgressURL = "http://" + getHostname() + "/bookflixapi/users/user_reading.php";

    private static String getApiUrl(final int type) {
        if (type == READ_BOOK || type == READ_BOOK_PAGES) {
            return readBookURL;
        } else if (type == USER_LOGIN) {
            return userLoginURL;
        } else if (type == SAVE_PROGRESS) {
            return saveProgressURL;
        } else if (type == SEARCH_BOOK) {
            return searchBookUrl;
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

    public static boolean newHttpRequest(AsyncTaskPostExecute context, Integer type, String method, String url, Context activityContext, View view) {
        if (isNetworkOnline(activityContext)) {
            HttpRequest req = new HttpRequest(context);
            req.setRequestType(type);
            req.setRequestMethod(method);
            req.execute(url);
//            Log.e("API REQUEST", String.valueOf(type) + " - " + method + " - " + url);
            return true;
        } else {
            Snackbar.make(view, "No internet connection.", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        }
    }

    public static boolean isNetworkOnline(Context con) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);

            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);

                status = netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return status;
    }
}
