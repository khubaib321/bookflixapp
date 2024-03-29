package com.example.kkrb0;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpRequest extends AsyncTask<String, Void, String> {
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;
    protected static String REQUEST_METHOD = "GET";
    protected Integer REQUEST_TYPE = 0;
    protected AsyncTaskPostExecute context;

    public HttpRequest(AsyncTaskPostExecute _context) {
        context = _context;
    }

    @Override
    protected String doInBackground(String... params) {
        String stringUrl = params[0];
        String result = "";

        try {
            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);
            //Create a connection
            HttpURLConnection connection = (HttpURLConnection)
                    myUrl.openConnection();
            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to our url
            connection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            String inputLine = "";
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
        } catch (ProtocolException e) {
            Log.e("Protocol Ex", e.getMessage());
        } catch (MalformedURLException e) {
            Log.e("MalformedURL Ex", e.getMessage());
        } catch (IOException e) {
            Log.e("IO Ex", e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        context.onTaskCompleted(result, REQUEST_TYPE);
    }

    public void setRequestType(final Integer type) {
        REQUEST_TYPE = type;
    }

    public void setRequestMethod(final String method) {
        REQUEST_METHOD = method;
    }
}
