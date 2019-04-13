package com.example.kkrb0;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BooksAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Book> books;

    // 1
    public BooksAdapter(Context context, ArrayList<Book> books) {
        this.mContext = context;
        this.books = books;
    }

    // 2
    @Override
    public int getCount() {
        return books.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return Integer.valueOf(books.get(position).id);
    }

    // 4
    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Book book = this.books.get(position);

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.book_thumb, null);
        }

        // 3
        final ImageView imageView = convertView.findViewById(R.id.imageview_cover_art);
        final TextView nameTextView = convertView.findViewById(R.id.textview_book_name);
        final TextView authorTextView = convertView.findViewById(R.id.textview_book_author);

        // 4
        imageView.setLayoutParams(new FrameLayout.LayoutParams(640, 720));
        Glide
                .with(mContext)
                .load(Utils.geteBookImageUrl(book.cover))
                .fitCenter()
                .into(imageView);
        nameTextView.setText(book.name);
        authorTextView.setText(book.author);

        return convertView;
    }

}
