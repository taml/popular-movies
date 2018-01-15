package com.tamlove.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    // The authority
    public static final String AUTHORITY = "com.tamlove.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "movies" directory
    public static final String PATH_MOVIES = "movies";

    // MovieEntry is an inner class that defines the contents of the movies table
    public static final class MovieEntry implements BaseColumns {

        // Full content Uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        // Movies table name
        public static final String TABLE_NAME = "movies";

        // Movies table column names, implementing BaseColumns automatically creates an "_ID" column
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movie_synopsis";
        public static final String COLUMN_MOVIE_URL = "movie_url";
        public static final String COLUMN_MOVIE_DATE = "movie_date";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";

    }

}
