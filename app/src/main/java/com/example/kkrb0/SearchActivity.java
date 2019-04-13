package com.example.kkrb0;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AsyncTaskPostExecute {

    private ListView mListView;
    private SearchView mSearchView;
    private User currentUser = null;
    private BooksAdapter booksAdapter;
    private boolean requestInProgress = false;
    private ArrayList<Book> books = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                finish();
                return true;
            case R.id.navigation_search:
                return true;
            case R.id.navigation_settings:
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("USER");

        mSearchView = findViewById(R.id.searchview);

        mListView = findViewById(R.id.search_listview);
        booksAdapter = new BooksAdapter(this, books, true);
        mListView.setAdapter(booksAdapter);

        mListView.setTextFilterEnabled(true);
        setupSearchView();

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent2 = new Intent(this, DescriptionActivity.class);
            intent2.putExtra("BOOK", books.get(position));
            intent2.putExtra("USER", currentUser);
            startActivity(intent2);
        });

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setupSearchView() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (requestInProgress) {
            return false;
        }
        requestInProgress = true;
        String urlParams[] = {
                "text=" + newText,
        };
        String preparedURL = Utils.getPreparedApiUrl(Utils.SEARCH_BOOK, urlParams);
        Utils.newHttpRequest(this, Utils.SEARCH_BOOK, "GET", preparedURL);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onQueryTextChange(query);
        return false;
    }

    @Override
    public void onTaskCompleted(String result, Integer requestType) {
        if (requestType == Utils.SEARCH_BOOK) {
            handleSearchBookApiResponse(result);
        }
        requestInProgress = false;
    }

    public void handleSearchBookApiResponse(String result) {
        books = new ArrayList<>();
        try {
            JSONObject bookInfo = new JSONObject(result);
            JSONArray foundBooks = bookInfo.getJSONArray("body");
            for (int i = 0; i < foundBooks.length(); ++i) {
                JSONObject book = foundBooks.getJSONObject(i);
                Book b = new Book();
                b.id = book.getString("id");
                b.name = book.getString("name");
                b.cover = book.getString("cover");
                b.author = book.getString("author");
                b.category = book.getString("category");
                books.add(b);
            }
            booksAdapter.refreshList(books);

        } catch (JSONException e) {
            Log.e("SA::JSONException", e.getMessage());
        }
    }
}
