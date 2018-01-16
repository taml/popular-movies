package com.tamlove.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with themoviedb API.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String MOVIE_DATABASE_BASE_URL = "http://api.themoviedb.org/3/movie/";

    /* Please add TMDB API key here */
    private static final String key = "";
    private static final String API_KEY = "api_key";

    /**
     * Builds the URL used to talk to themoviedb using a sort type.
     *
     * @param sortBy The sort method that will be queried, either popular or top_rated.
     * @return The URL to use to query themoviedb API.
     */
    public static URL buildURL(String sortBy){
        Uri builtUri = Uri.parse(MOVIE_DATABASE_BASE_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(API_KEY, key).build();
        URL url = null;
        try{
           url = new URL(builtUri.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "Built Url: " + url);
        return url;
    }

    /**
     * Builds a single movie URL used to get trailers and reviews for a specific movie.
     *
     * @param id The id of the single movie.
     * @return The URL to use to query themoviedb API for single movie details.
     */
    public static URL buildSingleMovieURL(String id, String extra){
        Uri builtUri = Uri.parse(MOVIE_DATABASE_BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(extra)
                .appendQueryParameter(API_KEY, key)
                .build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "Built Single Movie Url: " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        String next = null;
        try {
            // Check the response is ok before getting the input stream
            if (urlConnection.getResponseCode() == 200) {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    next = scanner.next();
                }
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }  catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            urlConnection.disconnect();
        }
        return next;
    }
}
