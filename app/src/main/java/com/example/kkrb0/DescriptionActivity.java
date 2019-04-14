package com.example.kkrb0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class DescriptionActivity extends AppCompatActivity implements AsyncTaskPostExecute {

    private Integer pageNumber = 0;
    private Book currentBook = null;
    private User currentUser = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                navigateUpTo(new Intent(getBaseContext(), MainActivity.class));
                return true;
            case R.id.navigation_search:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("USER", currentUser);
                intent.putExtra("PAGE_BO", pageNumber);
                startActivityForResult(intent, 1);
                return true;
            case R.id.navigation_settings:
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
        currentUser = (User) intent.getSerializableExtra("USER");

        String urlParams[] = {
                "mode=view",
                "book_id=" + currentBook.id,
                "user_email=" + (currentUser == null ? "" : currentUser.email),
        };
        String preparedURL = Utils.getPreparedApiUrl(Utils.READ_BOOK, urlParams);
        Utils.newHttpRequest(this, Utils.READ_BOOK, "GET", preparedURL, getBaseContext(), findViewById(R.id.description_activity_main_content));

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void handleReadButtonClick(View target) {
        if (currentUser == null || currentUser.email.length() == 0) {
            Snackbar.make(findViewById(R.id.description_activity_main_content), getResources().getString(R.string.sign_in_books_msg), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra("BOOK", currentBook);
        intent.putExtra("USER", currentUser);
        intent.putExtra("PAGE_NO", pageNumber);
        startActivity(intent);
    }

    /**
     * set data in text fields of the page
     */
    public void setFieldsData() {
        Glide
                .with(this)
                .load(Utils.geteBookImageUrl(currentBook.cover))
                .into((ImageView) findViewById(R.id.book_cover));

        TextView textView;
        textView = findViewById(R.id.name_text);
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

        Button readButton = findViewById(R.id.read_button);
        if (pageNumber == 0) {
            readButton.setText("START READING");
        } else {
            readButton.setText("CONTINUE READING (Page " + String.valueOf(pageNumber) + ")");
        }
    }

    @Override
    public void onTaskCompleted(String result, Integer requestType) {
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

            pageNumber = bookInfo.getInt("page_no");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setFieldsData();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("Page", pageNumber);
        savedInstanceState.putSerializable("Book", currentBook);
        savedInstanceState.putSerializable("User", currentUser);
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        pageNumber = savedInstanceState.getInt("Page");
        currentBook = (Book) savedInstanceState.getSerializable("Book");
        currentUser = (User) savedInstanceState.getSerializable("User");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                pageNumber = Integer.valueOf(data.getStringExtra("PAGE_NO"));
                Button readButton = findViewById(R.id.read_button);
                readButton.setText("CONTINUE READING (Page " + String.valueOf(pageNumber) + ")");
            }
        }
    }
}
