package com.example.kkrb0;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncTaskPostExecute {
    private static final int RC_SIGN_IN = 1;
    private User currentUser = null;
    private GoogleSignInClient mGoogleSignInClient;
    private ArrayList<Book> books = new ArrayList<>();

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

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleSignOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    postSignOut();
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name_offical));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        GridView gridview = findViewById(R.id.gridview);
        gridview.setAdapter(new BooksAdapter(this, books));
        gridview.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, DescriptionActivity.class);
            intent.putExtra("BOOK", books.get(position));
            intent.putExtra("USER", currentUser);
            startActivity(intent);
        });

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String urlParams[] = {
                "mode=list"
        };
        String preparedURL = Utils.getPreparedApiUrl(Utils.READ_BOOK, urlParams);
        Utils.newHttpRequest(this, Utils.READ_BOOK, "GET", preparedURL);

        setNavigationHeaderData(getResources().getString(R.string.app_name_offical), "by kitabkhana.com.pk", null);
        initiateGoogleSignIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        postSignIn(account, false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.open_kitabkhana) {
            String kitabkhana_url = getResources().getString(R.string.kitabkhana_website);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(kitabkhana_url));
            startActivity(browserIntent);

        } else if (id == R.id.nav_send) {
            googleSignOut();
            showSignInButton();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTaskCompleted(String result, Integer requestType) {
        if (requestType == Utils.READ_BOOK) {
            handleReadBookApiResponse(result);
        } else if (requestType == Utils.USER_LOGIN) {
            handleUserLoginApiResponse(result);
        }
    }

    public void handleReadBookApiResponse(String result) {

        try {
            JSONObject bookInfo = new JSONObject(result);
            JSONArray foundBooks = bookInfo.getJSONArray("body");
            for (int i = 0; i < foundBooks.length(); ++i) {
                JSONObject book = foundBooks.getJSONObject(i);
                Book b = new Book();
                b.id = book.getString("id");
                b.name = book.getString("name");
                b.year = book.getString("year");
                b.author = book.getString("author");
                b.category = book.getString("category");
                b.publisher = book.getString("publisher");
                b.description = book.getString("description");
                b.no_of_pages = book.getString("no_of_pages");
                b.cover = book.getString("cover");
                books.add(b);
            }

        } catch (JSONException e) {
            Log.e("MA::JSONException", e.getMessage());
        }
    }

    public void handleUserLoginApiResponse(String result) {

    }

    public void initiateGoogleSignIn() {
        // Set the dimensions of the sign-in button.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(view -> {
            googleSignIn();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            postSignIn(account, true);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("Sign In Api Exception", "Status Code " + String.valueOf(e.getStatusCode()));
        }
    }

    private void postSignIn(GoogleSignInAccount account, boolean syncToServer) {
        if (account == null) {
            return;
        }
        hideSignInButton();
        setNavigationHeaderData(account.getDisplayName(), account.getEmail(), account.getPhotoUrl());

        if (syncToServer) {
            // done this way to avoid sending requests on changing activity / switching app etc
            String urlParams[] = {
                    "email=" + account.getEmail(),
                    "name=" + account.getDisplayName(),
                    "phone1=0",
                    "phone2=0",
            };
            String preparedURL = Utils.getPreparedApiUrl(Utils.USER_LOGIN, urlParams);
            Utils.newHttpRequest(this, Utils.USER_LOGIN, "GET", preparedURL);
            Snackbar.make(findViewById(R.id.main_activity_main_content), "Signed in as: " + account.getDisplayName() + " (" + account.getEmail() + ").", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }

        currentUser = new User();
        currentUser.id = null;
        currentUser.email = account.getEmail();
        currentUser.name = account.getDisplayName();
        currentUser.phone1 = "0";
        currentUser.phone2 = "0";
    }

    private void postSignOut() {
        currentUser = null;
        showSignInButton();
        setNavigationHeaderData(getResources().getString(R.string.app_name_offical), "by kitabkhana.com.pk", null);
        Snackbar.make(findViewById(R.id.main_activity_main_content), "Sign out successful.", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private void hideSignInButton() {
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setVisibility(View.GONE);
    }

    private void showSignInButton() {
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setVisibility(View.VISIBLE);
    }

    private void setNavigationHeaderData(String mainText, String subText, Uri imageUrl) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nameView = headerView.findViewById(R.id.nav_header_name);
        nameView.setText(mainText);

        TextView emailView = headerView.findViewById(R.id.nav_header_email);
        emailView.setText(subText);

        Glide
                .with(this)
                .load(imageUrl)
                .fitCenter()
                .into((ImageView) headerView.findViewById(R.id.nav_header_photo));

    }
}
