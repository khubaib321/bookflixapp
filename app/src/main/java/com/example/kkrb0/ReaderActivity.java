package com.example.kkrb0;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class ReaderActivity extends AppCompatActivity implements AsyncTaskPostExecute {

    private static ArrayList<byte[]> pdfPages = new ArrayList<>();
    private Book currentBook = null;
    private User currentUser = null;
    private Integer safetyGap = 3;  // no of pages
    private Integer currentPageIndex = 0;
    private boolean firstLoad = true;
    private boolean requestInProgress = false;
    private Integer currentPageIndexRetreiving = 0;
    private LinkedList<HttpRequest> requestQueue = new LinkedList<>();
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

        pdfPages.clear();

        Intent intent = getIntent();
        currentBook = (Book) intent.getSerializableExtra("BOOK");
        toolbar.setTitle(currentBook.name);

        for (int i = 0; i < Integer.valueOf(currentBook.no_of_pages); ++i) {
            byte[] temp = {};
            pdfPages.add(temp);
        }

        currentUser = (User) intent.getSerializableExtra("USER");
        currentPageIndex = intent.getIntExtra("PAGE_NO", 1);
        if (currentPageIndex > 0) {
            currentPageIndex--;
        }
        // set pager data and position
        managePdf(true);
    }

    @Override
    public void onTaskCompleted(String result, Integer requestType) {
        if (requestType == Utils.READ_BOOK) {
            handleReadBooksApiResponse(result);
        } else if (requestType == Utils.SAVE_PROGRESS) {
            handleSaveProgressApiResponse(result);
        }
        requestInProgress = false;
    }

    public void handleReadBooksApiResponse(String result) {
        try {
            JSONObject jObj = new JSONObject(result);
            int bookPages = jObj.getInt("count");
            JSONObject pdfContents = jObj.getJSONObject("content");
            JSONArray pdfChuck = pdfContents.getJSONArray(currentBook.id);
            for (int i = 0; i < pdfChuck.length(); ++i) {
                String base64Content = pdfChuck.getJSONObject(i).getString("pageData");
                Integer pageIndex = Integer.valueOf(pdfChuck.getJSONObject(i).getString("pageIndex"));
                byte[] pageData = Base64.decode(base64Content, Base64.DEFAULT);
                pdfPages.set(pageIndex, pageData);
            }
        } catch (JSONException e) {
            Log.e("RA::JSONException", e.getMessage());
        }

        if (mSectionsPagerAdapter == null) {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(currentPageIndex, true);

            mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                public void onPageSelected(int position) {
                    if (position > currentPageIndex) {
                        // only if going to next page
                        handlePageChangeNext();
                    } else {
                        handlePageChangePrev();
                    }
                }
            });
        } else {
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
        firstLoad = false;
    }

    public void handleSaveProgressApiResponse(String result) {

    }

    public void handlePageChangeNext() {
        currentPageIndex++; // swiped right to update page index we are pointing to
        managePdf(true);
    }

    public void handlePageChangePrev() {
        currentPageIndex--;
        managePdf(false);
    }

    /**
     * Handles pdf progress saving and pdf forward navigation
     * When called for the first time during activity creation 'position' will be 0 and it will force
     * to get the initial pdf page set. At this time 'currentPageIndex' will also be 0 because it is
     * updated when initial pdf page set is retrieved in 'getPdfChunk' method and that synchronizes
     * the 'currentPageIndex' with user's saved progress.
     */
    public void managePdf(Boolean upcoming) {
        if (upcoming) {
            // only saving progression
            saveProgress(currentPageIndex + 1); // +1 because we save page number, not index
        }
        getPdfPage(currentPageIndex, upcoming);  // Check if close to completing current set of pages then get more
    }

    public void enQueueRequest() {

    }

    /**
     * Check if close to completing current set of pages and get more
     *
     * @param pageNo Integer
     */
    public void getPdfPage(Integer pageNo, Boolean upcoming) {
        if (upcoming) {
            currentPageIndexRetreiving = pageNo + safetyGap;
        } else {
            currentPageIndexRetreiving = pageNo - safetyGap;
        }
        if (currentPageIndexRetreiving >= Integer.valueOf(currentBook.no_of_pages)) {
            // point to last page
            currentPageIndexRetreiving = Integer.valueOf(currentBook.no_of_pages) - 1;
        }
        if (currentPageIndexRetreiving < 0 || pdfPages.get(currentPageIndexRetreiving).length != 0) {
            return;
        }
        requestInProgress = true;
        String urlParams[] = {
                "mode=read",
                "book_id=" + currentBook.id,
                "user_email=" + (currentUser == null ? "" : currentUser.email),
                "first_load=" + String.valueOf(firstLoad),
                "gap_to_safety=" + String.valueOf(safetyGap),
                "retrieve_page=" + String.valueOf(currentPageIndexRetreiving + 1),
        };
        String preparedURL = Utils.getPreparedApiUrl(Utils.READ_BOOK, urlParams);
        Utils.newHttpRequest(this, Utils.READ_BOOK, "GET", preparedURL, getBaseContext(), findViewById(R.id.reader_activity_main_content));
    }

    /**
     * Save current reading progress
     *
     * @param pageNumber Integer
     */
    public void saveProgress(Integer pageNumber) {
        if (currentUser == null || currentUser.email.length() == 0) {
            return;
        }
        requestInProgress = true;
        String urlParams[] = {
                "book_id=" + currentBook.id,
                "page_no=" + pageNumber,
                "user_email=" + currentUser.email,
        };
        String preparedURL = Utils.getPreparedApiUrl(Utils.SAVE_PROGRESS, urlParams);
        Utils.newHttpRequest(this, Utils.SAVE_PROGRESS, "GET", preparedURL, getBaseContext(), findViewById(R.id.reader_activity_main_content));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("PAGE_NO", currentPageIndex + 1);
        setResult(RESULT_OK, intent);
        finish();
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
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

            if (pdfPages.get(sectionNumber - 1).length > 0) {
                PDFView pdfView = rootView.findViewById(R.id.pdfView);
                pdfView.fromBytes(pdfPages.get(sectionNumber - 1)).enableAnnotationRendering(true).load();
            }

            return rootView;
        }
    }
}
