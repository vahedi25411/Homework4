package com.example.android.newsapp;

import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.newsapp.data.Contract;
import com.example.android.newsapp.model.NewsItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Vahedi on 6/29/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ItemViewHolder>{

    //ArrayList variable replaced with Cursor
    private Cursor cursor;
    private ItemClickListener listener;
    private Context context;

    //this method modified to accept Cursor instead of ArrayList
    public NewsAdapter(Cursor cursor, ItemClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    public interface ItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shoudAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.news_list_item,  parent, shoudAttachToParentImmediately );
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bind(position);
    }


    //this method changed to return cursor's count
    @Override
    public int getItemCount() {
        if(cursor == null) {
            return 0;
        }
        else{
            return cursor.getCount();
        }
    }

    //this method changed to accept Cursor
    public void setData(Cursor newCursor){
        this.cursor = newCursor;
        notifyDataSetChanged();
    }

    //change the cursor to a new one and force the RecyclerView to refresh
    public void swapCursor(Cursor newCursor){
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        TextView description;
        TextView date;
        //image added to show the news' picture
        ImageView image;

        ItemViewHolder(View view){
            super(view);

            title = (TextView) view.findViewById(R.id.news_title);
            description = (TextView) view.findViewById(R.id.news_description);
            date = (TextView) view.findViewById(R.id.news_date);
            //initialize the image object
            image = (ImageView) view.findViewById(R.id.news_image);

            view.setOnClickListener(this);

        }

        public void bind(int position)
        {
            cursor.moveToPosition(position);
            NewsItem newsItem = new NewsItem(cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_SOURCE)),
                    cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_AUTHOR)),
                    cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.CONLUMN_NAME_URL)),
                    cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_URL_TO_IMAGE)),
                    cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_PUBLISHED_AT)));

            title.setText(newsItem.getTitle());
            description.setText(newsItem.getDescription());
            date.setText(newsItem.getPublishedAt());

            String imageUrl = newsItem.getUrlToImage();

            //show the news' image using the Picasso
            if(imageUrl != null) {
                Picasso.with(context)
                        .load(imageUrl)
                        .into(image);
            }

        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onListItemClick(pos);
        }
    }
}
