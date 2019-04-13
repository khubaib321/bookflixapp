package com.example.kkrb0;

interface AsyncTaskPostExecute {
    /**
     * Called from HttpRequest onPostExecute
     * Typical use: Pass the async task result to the calling activity
     *
     * @param result      String
     * @param requestType Integer passed so that implementing classes can differentiate betwen different type of requests
     */
    void onTaskCompleted(String result, Integer requestType);
}
