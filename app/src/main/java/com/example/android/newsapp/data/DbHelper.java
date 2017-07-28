package com.example.android.newsapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Vahedi on 7/23/17.
 */
//creating DbHelper class for facilitating database interactions
public class DbHelper extends SQLiteOpenHelper {
    //indicate databse changes version
    public static final int DATABASE_VERSION = 1;
    //indicate databse name
    public static final String DATABASE_NAME = "news.db";
    public static final String TAG = "dbhelper";

    public DbHelper(Context context){
        super(context, DATABASE_NAME , null, DATABASE_VERSION);

    }

    //on creation of DbHelper if the table didn't exist , it will execute the table generation script
    @Override
    public void onCreate(SQLiteDatabase db) {

        String queryString = "CREATE TABLE "+ Contract.TABLE_NEWS.TABLE_NAME + " ( "+
                Contract.TABLE_NEWS._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.TABLE_NEWS.CONLUMN_NAME_URL + " TEXT NOT NULL, "+
                Contract.TABLE_NEWS.COLUMN_NAME_AUTHOR + " TEXT NOT NULL, "+
                Contract.TABLE_NEWS.COLUMN_NAME_TITLE + " TEXT NOT NULL, "+
                Contract.TABLE_NEWS.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL, "+
                Contract.TABLE_NEWS.COLUMN_NAME_URL_TO_IMAGE + " TEXT NULL, " +
                Contract.TABLE_NEWS.COLUMN_NAME_SOURCE + " TEXT NULL, " +
                Contract.TABLE_NEWS.COLUMN_NAME_PUBLISHED_AT + " DATE );";

        Log.d( TAG , queryString );
        db.execSQL( queryString );

    }


    //on database changes this method decides how those changes should be implemented in existing database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + Contract.TABLE_NEWS.TABLE_NAME + " if exists;");
    }
}
