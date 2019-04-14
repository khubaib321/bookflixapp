package com.example.kkrb0;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BooksAdapter extends BaseAdapter implements Filterable {

    private boolean lv;
    private Context mContext;
    private ArrayList<Book> books;

    // 1
    public BooksAdapter(Context context, ArrayList<Book> _books, Boolean listview) {
        lv = listview;
        books = _books;
        mContext = context;
    }

    // 2
    @Override
    public int getCount() {
        return books.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Book book = books.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            if (lv) {
                convertView = layoutInflater.inflate(R.layout.listview_items, null);
            } else {
                convertView = layoutInflater.inflate(R.layout.book_thumb, null);
            }
        }

        final ImageView imageView = convertView.findViewById(R.id.imageview_cover_art);
        final TextView nameTextView = convertView.findViewById(R.id.textview_book_name);
        final TextView authorTextView = convertView.findViewById(R.id.textview_book_author);

        Integer width = 640;
        Integer height = 720;
        imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));

        if (lv) {
            // different settings if list view
            width = 320;
            height = 380;
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        }

        Glide
                .with(mContext)
                .load(Utils.geteBookImageUrl(book.cover))
                .fitCenter()
                .into(imageView);
        nameTextView.setText(book.name);
        authorTextView.setText(book.author);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public void refreshList(ArrayList<Book> newBooks) {
        books = newBooks;
        notifyDataSetChanged();
    }
}
