package com.tamlove.popularmovies.ui;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tamlove.popularmovies.movie_model.Movie;
import com.tamlove.popularmovies.R;
import com.tamlove.popularmovies.adapters.MovieAdapter;
import com.tamlove.popularmovies.adapters.MovieCursorAdapter;
import com.tamlove.popularmovies.data.MovieContract;
import com.tamlove.popularmovies.utilities.MovieUtils;
import com.tamlove.popularmovies.utilities.QueryUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final int GRID_SPAN  = 2;
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final int FAVOURITE_MOVIE_LOADER_ID = 5;
    // Save App state
    public static final String GRID_STATE = "grid_state";
    public static final String LAYOUT_ID = "layout_id";
    private static Bundle mStateBundle;
    private int layoutToDisplay = 1;

    private GridLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorTextView;
    private ProgressBar mProgressBar;
    private MovieCursorAdapter mMovieCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mErrorTextView = findViewById(R.id.error_textview);
        mProgressBar = findViewById(R.id.progress_bar);

        layoutManager = new GridLayoutManager(MainActivity.this,GRID_SPAN);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(MainActivity.this, new ArrayList<Movie>());
        mMovieCursorAdapter = new MovieCursorAdapter(this);

        if(savedInstanceState != null){
            layoutToDisplay = savedInstanceState.getInt(LAYOUT_ID);
            if(layoutToDisplay == 1){
                displayPopular();
            } else if(layoutToDisplay == 2){
                displayTopRated();
            } else {
                displayFavourites();
            }
        } else {
            mRecyclerView.setAdapter(mMovieAdapter);
            sortMovieBy(POPULAR);
            showMovieDataView();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAYOUT_ID, layoutToDisplay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStateBundle = new Bundle();
        Parcelable currentGridState = layoutManager.onSaveInstanceState();
        mStateBundle.putParcelable(GRID_STATE, currentGridState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mStateBundle != null){
            Parcelable currentGridState = mStateBundle.getParcelable(GRID_STATE);
            layoutManager.onRestoreInstanceState(currentGridState);
        }
    }

    private void displayPopular(){
        mRecyclerView.setAdapter(mMovieAdapter);
        sortMovieBy(POPULAR);
    }

    private void displayTopRated(){
        mRecyclerView.setAdapter(mMovieAdapter);
        sortMovieBy(TOP_RATED);
    }

    private void displayFavourites(){
        mRecyclerView.setAdapter(mMovieCursorAdapter);
        getSupportLoaderManager().initLoader(FAVOURITE_MOVIE_LOADER_ID, null, this);
    }

    /**
     * This method starts async task
     *
     * @param sort Pass in a sort type so the data can be loaded according to the sort type
     */
    private void sortMovieBy(String sort){
        new FetchMovieDataTask().execute(sort);
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     */
    private void showMovieDataView(){
        /* First, make sure the error is invisible */
        mErrorTextView.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie data
     * View.
     */
    private void showErrorMessage(){
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    public class FetchMovieDataTask extends AsyncTask<String, Void, List<Movie>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            // If there's no sort type, don't look anything up
            if (params.length == 0) {
                return null;
            }

            String sortBy = params[0];
            URL movieDatabaseUrl = QueryUtils.buildURL(sortBy);

            try {
                String jsonMovieResponse = QueryUtils.getResponseFromHttpUrl(movieDatabaseUrl);
                List<Movie> jsonMovieData = MovieUtils.getMoviesFromJSON(jsonMovieResponse);
                return jsonMovieData;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movieData) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if(movieData != null){
                showMovieDataView();
                mMovieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS,
                MovieContract.MovieEntry.COLUMN_MOVIE_URL,
                MovieContract.MovieEntry.COLUMN_MOVIE_DATE,
                MovieContract.MovieEntry.COLUMN_MOVIE_RATING };
        return new CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.action_popular:
                displayPopular();
                layoutToDisplay = 1;
                return true;
            case R.id.action_top_rated:
                displayTopRated();
                layoutToDisplay = 2;
                return true;
            case R.id.action_favourite:
                displayFavourites();
                layoutToDisplay = 3;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
