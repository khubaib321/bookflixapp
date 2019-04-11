package com.example.kkrb0;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class DescriptionActivity extends AppCompatActivity implements AsyncTaskPostExecute {

    private Book currentBook = null;
    private TextView textView = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                return true;
            case R.id.navigation_dashboard:
                return true;
            case R.id.navigation_notifications:
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Intent intent = getIntent();
        currentBook = (Book) intent.getSerializableExtra("BOOK");

        String urlParams[] = {
                "mode=view",
                "book_id=" + currentBook.id
        };
        String preparedURL = Utils.getPreparedApiUrl(urlParams);
        new HttpRequest(this).execute(preparedURL);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Snackbar.make(findViewById(R.id.description_activity_main_content), preparedURL, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void handleReadButtonClick(View target) {
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra("BOOK", currentBook);
        startActivity(intent);
    }

    /**
     * set data in text fields of the page
     */
    public void setFieldsData() {
        textView = findViewById(R.id.toolbar_title);
        textView.setText(currentBook.name);

        textView = findViewById(R.id.category_text);
        textView.setText(currentBook.category);

        textView = findViewById(R.id.author_text);
        textView.setText(currentBook.author);

        textView = findViewById(R.id.publisher_text);
        textView.setText(currentBook.publisher);

        textView = findViewById(R.id.year_text);
        textView.setText(currentBook.year);

        textView = findViewById(R.id.no_of_pages_text);
        textView.setText(currentBook.no_of_pages);

        textView = findViewById(R.id.description_text);
        textView.setText(currentBook.description);
    }

    @Override
    public void onTaskCompleted(String result) {
        try {
            JSONObject bookInfo = new JSONObject(result);
            JSONObject foundBook = bookInfo.getJSONArray("body").getJSONObject(0);
            currentBook.id = foundBook.getString("id");
            currentBook.name = foundBook.getString("name");
            currentBook.year = foundBook.getString("year");
            currentBook.author = foundBook.getString("author");
            currentBook.category = foundBook.getString("category");
            currentBook.publisher = foundBook.getString("publisher");
            currentBook.description = foundBook.getString("description");
            currentBook.no_of_pages = foundBook.getString("no_of_pages");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setFieldsData();
    }
}
