package com.example.kkrb0;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class DescriptionActivity extends AppCompatActivity {

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

        this.setBookCover();
        this.setFieldsData();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void handleReadButtonClick(View target) {
        Intent intent = new Intent(this, ReaderActivity.class);
        startActivity(intent);
    }

    /**
     * set data in text fields of the page
     */
    public void setFieldsData() {
        textView = findViewById(R.id.toolbar_title);
        textView.setText(currentBook.name);

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

    public void setBookCover() {
    }

}
