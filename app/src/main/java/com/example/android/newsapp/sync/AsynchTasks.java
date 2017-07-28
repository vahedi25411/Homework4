package com.example.android.newsapp.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.util.Log;

import com.example.android.newsapp.data.Contract;
import com.example.android.newsapp.data.DbHelper;
import com.example.android.newsapp.model.NewsItem;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import com.example.android.newsapp.utilities.NetworkUtils;

import org.json.JSONException;


/**
 * Created by Vahedi on 7/23/17.
 */

public class AsynchTasks {

    public static final String ACTION_FETCH_DATA = "fetch-data";
    public static final String TAG = "AsyncTasks";


    //This method specify which task has been requested to be executed and call the related private method
    public static void executeTask(Context context, String action){

        if (ACTION_FETCH_DATA.equals(action)){
            fetchData(context);
        }
    }

    private static void fetchData(Context context) {
        ArrayList<NewsItem> results = null;
        DbHelper helper = new DbHelper(context);


        //this line of code build url according to parameters in NetworkUtil class and return appropriate URL
        URL url = NetworkUtils.buildUrl();

        try{

            //this line get the response from the specified URL and return the result as string
            String json = NetworkUtils.getResponseFromHttpUrl(url);
            //this line parse the returned string as json object and return an arraylist of NewsItems
            results = NetworkUtils.parseJSON(json);

            //using this loop we can check newsItem in the list and check if it is not in database
            // it is inserted in database otherwise it is neglected
            for(int i=0;i<results.size();i++){

                NewsItem ni = results.get(i);

                if (!checkNewsItemExistInTable(helper,ni)){

                    insertNewsItem(helper, ni);
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    //this method insert newsitem into the database
    private static void insertNewsItem(DbHelper helper, NewsItem item){

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_AUTHOR,item.getAuthor());
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_TITLE, item.getTitle());
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_DESCRIPTION, item.getDescription());
        cv.put(Contract.TABLE_NEWS.CONLUMN_NAME_URL, item.getUrl());
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_URL_TO_IMAGE, item.getUrlToImage());
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_PUBLISHED_AT, item.getPublishedAt());
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_SOURCE, item.getSource());

        //insert ContentValues into the table
        db.insert(Contract.TABLE_NEWS.TABLE_NAME, null, cv);

        db.close();

    }

    //this method look for a newsitem in table according to URL which is a unique proprty
    //and will return true if find the record otherwise return false
    private static boolean checkNewsItemExistInTable(DbHelper helper,NewsItem item){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        boolean result;

        String[] args = { item.getUrl() };

        //run the query to search a for a specified news' URL
        cursor = db.query(Contract.TABLE_NEWS.TABLE_NAME ,
                            null,
                            Contract.TABLE_NEWS.CONLUMN_NAME_URL + " = ?",
                            args,
                            null,
                            null,
                            null,
                            null);



        if (!(cursor.moveToFirst()) || cursor.getCount() ==0)
            result = false;
        else
            result = true;

        db.close();
        cursor.close();

        return result;
    }
}
