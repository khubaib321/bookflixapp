package com.example.kkrb0;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ReaderActivity extends AppCompatActivity implements AsyncTaskPostExecute {

    private Book currentBook = null;
    private static ArrayList<byte[]> pdfPages = new ArrayList<>();
    private boolean requestInProgress = false;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter = null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        currentBook = (Book) intent.getSerializableExtra("BOOK");
        toolbar.setTitle(currentBook.name);

        pdfPages.clear();

        getPdfChunk(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskCompleted(String result) {
        try {
            JSONObject jObj = new JSONObject(result);
            int bookPages = jObj.getInt("count");
            JSONObject pdfContents = jObj.getJSONObject("content");
            JSONArray pdfChuck = pdfContents.getJSONArray(currentBook.id);
            for (int i = 0; i < pdfChuck.length(); ++i) {
                String pageLength = pdfChuck.getJSONObject(i).getString("pageLength");
                String base64Content = pdfChuck.getJSONObject(i).getString("pageData");
                byte[] pageData = Base64.decode(base64Content, Base64.DEFAULT);
                pdfPages.add(pageData);
                String text = new String(pageData, StandardCharsets.ISO_8859_1);
//                Log.e(pageLength, String.valueOf(text.length()));
            }
        } catch (JSONException e) {
            Log.e("RA::JSONException", e.getMessage());
//        } catch (UnsupportedEncodingException e) {
//            Log.e("RA::UnsupportedEncoding", e.getMessage());
//        }
        }

        if (mSectionsPagerAdapter == null) {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                public void onPageSelected(int position) {
                    Log.e("POSITION", String.valueOf(position));
                    if ((position + 2) > pdfPages.size()) {
                        getPdfChunk(position);
                    }
                }
            });
        } else {
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
        requestInProgress = false;
    }

    public void getPdfChunk(int offset) {
        if (requestInProgress) {
            return;
        }
        requestInProgress = true;
        String urlParams[] = {
                "mode=read",
                "book_id=" + currentBook.id,
                "user_id=1",
                "offset=" + String.valueOf(offset)
        };
        String preparedURL = Utils.getPreparedApiUrl(urlParams);
        new HttpRequest(this).execute(preparedURL);

        Snackbar.make(findViewById(R.id.main_content), preparedURL, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static Integer CURRENT_SECTION = 1;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            CURRENT_SECTION = sectionNumber;
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_reader, container, false);
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            if (pdfPages.size() > 0 && pdfPages.size() > (sectionNumber - 1)) {
                PDFView pdfView = rootView.findViewById(R.id.pdfView);
                pdfView.fromBytes(pdfPages.get(sectionNumber - 1)).enableAnnotationRendering(true).load();
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Number of pages to show
            return pdfPages.size();
        }
    }
}
