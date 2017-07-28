package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.newsapp.data.Contract;
import com.example.android.newsapp.data.DbHelper;
import com.example.android.newsapp.model.NewsItem;
import com.example.android.newsapp.sync.AsynchTasks;
import com.example.android.newsapp.sync.RefreshNewsDataUtility;

import java.util.ArrayList;

//we implement LoaderManager.LoaderCallbacks<Void> to use the loader features
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Void> {

    static final String TAG="mainactivity";
    private RecyclerView newsRecyclerView;
    private ProgressBar progress;
    private NewsAdapter newsAdapter;
    private TextView errorMessageDisplay;
    //creating a new parameter for DbHelper
    private DbHelper helper;
    //SQLiteDatabase variable for working with table
    private SQLiteDatabase db;
    //creating a cursor for keeping the result of queries
    private Cursor cursor;

    private boolean isFirstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RefreshNewsDataUtility.scheduleRefreshNewsData(this);

        //showDataTextView = (TextView) findViewById(R.id.new .id.news_api_data);
        newsRecyclerView = (RecyclerView) findViewById(R.id.news_recycler_view);

        errorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this , LinearLayoutManager.VERTICAL, false);
        newsRecyclerView.setLayoutManager(layoutManager);
        newsRecyclerView.setHasFixedSize(true);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        //this line initialize the helper object
        helper = new DbHelper(this);

        //intitialize a SharedPreferences object to check whether it is the first run of the application or not
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        //read the isFirstRun with the default value of true which means if this variable didn't
        // have value return true
        isFirstRun = sharedPref.getBoolean(getString(R.string.is_first_run),true);

        //if this is a first run of the application we should use loader to fiil the database in background thread
        if (isFirstRun){
            //create the loader object
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.restartLoader(1, null, this).forceLoad();

            //set the preferences value to false to be used for the next runs of the application
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.is_first_run), false);
            editor.commit();
        }


        loadData();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //close the SQLiteDatabase and Cursor objects
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            loadData();
        }
        return true;
    }

    //changed to load data from database
    private void loadData(){

        //connect to the database
        db = helper.getReadableDatabase();
        // read the data from database
        cursor = getNewsFromDatabase(db);

        //initialize a NewsAdapter object with the new cursor object
        newsAdapter = new NewsAdapter(cursor, new NewsAdapter.ItemClickListener() {
            @Override
            public void onListItemClick(int clickedItemIndex) {
                ListItemClickOperation(clickedItemIndex);
            }
        });

        errorMessageDisplay.setVisibility(View.INVISIBLE);
        newsRecyclerView.setVisibility(View.VISIBLE);


        //Set the RecyclerView's Adapter to show the data
        newsRecyclerView.setAdapter(newsAdapter);




    }

    //return news from database as Cursor
    private Cursor getNewsFromDatabase(SQLiteDatabase db){

        //running query to get the news from database
        return cursor = db.query(Contract.TABLE_NEWS.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Contract.TABLE_NEWS._ID + " DESC",
                null);



    }
    //Override onCreateLoader method of LoaderManager.LoaderCallbacks<Void>
    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {

        //Initialize an anonymous AsyncTaskLoader<Void> object to do the task in background thread
        return new android.support.v4.content.AsyncTaskLoader<Void>(this) {


            @Override
            public Void loadInBackground() {
                progress.setVisibility(View.VISIBLE);
                //Execute the refresh task in background
                AsynchTasks.executeTask(MainActivity.this, AsynchTasks.ACTION_FETCH_DATA);
                return null;
            }
        };
    }

    //Override onLoadFinished method of LoaderManager.LoaderCallbacks<Void>
    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {

        progress.setVisibility(View.GONE);

        if (cursor.getCount()!=0){


            //Refresh the data in recyclerView using the database data

            NewsAdapter newsAdapter = new NewsAdapter(cursor, new NewsAdapter.ItemClickListener() {
                @Override
                public void onListItemClick(int clickedItemIndex) {
                    ListItemClickOperation(clickedItemIndex);
                }
            });

            newsRecyclerView.setAdapter(newsAdapter);
        }
        else
        {
            newsRecyclerView.setVisibility(View.INVISIBLE);
            errorMessageDisplay.setVisibility(View.VISIBLE);
        }
        loadData();
    }

    //Override onLoaderReset method of LoaderManager.LoaderCallbacks<Void>
    @Override
    public void onLoaderReset(Loader<Void> loader) {
        return;
    }

    private void ListItemClickOperation(int listItemPosition){
        cursor.moveToPosition(listItemPosition);
        String url = cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.CONLUMN_NAME_URL));

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


/*
    public class FetchDataTask extends AsyncTask<String, Void, ArrayList<NewsItem>> implements NewsAdapter.ItemClickListener{

        ArrayList<NewsItem> data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<NewsItem> doInBackground(String... params) {

            ArrayList<NewsItem> result = null;
            URL url = NetworkUtils.buildUrl();
            Log.d(TAG,"URL :"+url.toString());

            try{
                String json = NetworkUtils.getResponseFromHttpUrl(url);
                result = NetworkUtils.parseJSON(json);
            }catch (IOException e){
                e.printStackTrace();
            }
            catch (JSONException e){
                e.printStackTrace();
            }

            return result;

        }

        @Override
        protected void onPostExecute(ArrayList<NewsItem> newsItems) {
            super.onPostExecute(newsItems);

            this.data = newsItems;
            progress.setVisibility(View.GONE);
            if (newsItems!=null){
                NewsAdapter newsAdapter = new NewsAdapter(newsItems,this);
                newsRecyclerView.setAdapter(newsAdapter);
            }
            else
            {
                newsRecyclerView.setVisibility(View.INVISIBLE);
                errorMessageDisplay.setVisibility(View.VISIBLE);
            }
            //showDataTextView.setText("Sorry, no data was received!");
        }

        @Override
        public void onListItemClick(int clickedItemIndex) {
            String pageUrl = data.get(clickedItemIndex).getUrl();

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }

        }

    }
    */

}
