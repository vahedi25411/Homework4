package com.example.android.newsapp.data;

import android.provider.BaseColumns;

/**
 * Created by Vahedi on 7/23/17.
 */
//creating a Contract class that simplifies database interaction in <code></code>
public class Contract {

    //creating a class for news table that implements BaseColumns and we can define our static parameters in this class
    public static class TABLE_NEWS implements BaseColumns {
        public static final String TABLE_NAME = "news";

        public static final String CONLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_URL_TO_IMAGE = "urlToImage";
        public static final String COLUMN_NAME_PUBLISHED_AT = "publishedAt";
        public static final String COLUMN_NAME_SOURCE = "source";
    }

}
