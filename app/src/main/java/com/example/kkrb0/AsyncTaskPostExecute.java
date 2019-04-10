package com.example.kkrb0;

interface AsyncTaskPostExecute {
    /**
     * Called from HttpRequest onPostExecute
     * Typical use: Pass the async task result to the calling activity
     *
     * @param result String
     */
    void onTaskCompleted(String result);
}
