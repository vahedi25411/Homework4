package com.example.android.newsapp.utilities;

import android.net.Uri;

import com.example.android.newsapp.model.NewsItem;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Vahedi on 6/20/17.
 */

public final class NetworkUtils {

    private static final String NEWS_BASE_URL = "https://newsapi.org/v1/articles";

    private static final String SOURCE = "the-washington-post";
    private static final String SORT_BY = "top";
    private static final String API_KEY = "e8f430f46de449ca96c16c741fcdb646";//API Key goes here

    final static String SOURCE_PARAM = "source";
    final static String SORT_PARAM = "sortBy";
    final static String API_KEY_PARAM = "apiKey";

    //build url according to specified parameters
    public static URL buildUrl(){
        Uri builtUri = Uri.parse(NEWS_BASE_URL).buildUpon()
                .appendQueryParameter(SOURCE_PARAM, SOURCE)
                .appendQueryParameter(SORT_PARAM, SORT_BY)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    //thid method calls the input url and returns its result as string
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);

            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }
            else {
                return null;
            }

        }
        finally{
            urlConnection.disconnect();
        }
    }

    //this method parse the input string and return the json objects in ArrayList
    public static ArrayList<NewsItem> parseJSON(String json) throws JSONException {
        ArrayList<NewsItem> result = new ArrayList<>();
        JSONObject main = new JSONObject(json);
        JSONArray items = main.getJSONArray("articles");

        String source = main.getString("source");
        String author;
        String title;
        String description;
        String url;
        String urlToImage;
        String publishedAt;

        for(int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);

            author = item.getString("author");
            title = item.getString("title");
            description = item.getString("description");
            url = item.getString("url");
            urlToImage = item.getString("urlToImage");
            publishedAt = item.getString("publishedAt");

            NewsItem newsItem = new NewsItem(source, author, title, description, url, urlToImage, publishedAt);
            result.add(newsItem);
        }

        return result;
    }

}
