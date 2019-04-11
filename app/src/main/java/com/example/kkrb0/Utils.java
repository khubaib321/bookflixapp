package com.example.kkrb0;

public class Utils {

    private static String serverEmulator = "10.0.2.2";
    private static String serverLocalhost = "localhost";
    private static String serverProduction = "kitabkhana.com.pk";

    public static String apiBaseURL = "http://" + serverEmulator + "/bookflixapi/books/read.php";

    public static String getPreparedApiUrl(String urlParams[]) {
        String preparedUrl = apiBaseURL;
        if (apiBaseURL.length() == 0) {
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
}
