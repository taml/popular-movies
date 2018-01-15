package com.tamlove.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tamlove.popularmovies.movie_model.Movie;
import com.tamlove.popularmovies.R;
import com.tamlove.popularmovies.data.MovieContract;
import com.tamlove.popularmovies.ui.MovieActivity;

public class MovieCursorAdapter extends RecyclerView.Adapter<MovieCursorAdapter.MovieFavViewHolder>{

    public static final String MOVIE_ID = "id";
    public static final String MOVIE_TITLE = "title";
    public static final String MOVIE_SYNOPSIS = "synopsis";
    public static final String MOVIE_URL = "url";
    public static final String MOVIE_RATING = "rating";
    public static final String MOVIE_DATE = "date";
    public static final int MOVIE_POSTER_WIDTH = 360;
    public static final int MOVIE_POSTER_HEIGHT = 520;

    // Class variables for the Cursor that holds movie data and the Context
    private Cursor mCursor;
    private Context mContext;

    /**
     * Constructor for the MovieCursorAdapter that initialises the Context.
     *
     * @param context the current Context
     */
    public MovieCursorAdapter(Context context){
        mContext = context;
    }

    // Inner class for creating ViewHolders
    class MovieFavViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mPosterImageView;

        /**
         * Constructor for the MovieFavViewHolder
         *
         * @param view The view inflated in onCreateViewHolder
         */
        public MovieFavViewHolder(View view) {
            super(view);
            mPosterImageView = view.findViewById(R.id.movie_poster);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            Movie favourite = favouriteMovieCursor();
            Class destinationClass = MovieActivity.class;
            Intent movieActivity = new Intent(mContext, destinationClass);
            movieActivity.putExtra(MOVIE_ID, favourite.getMovieId());
            movieActivity.putExtra(MOVIE_TITLE, favourite.getTitle());
            movieActivity.putExtra(MOVIE_SYNOPSIS, favourite.getSynopsis());
            movieActivity.putExtra(MOVIE_URL, favourite.getPoster());
            movieActivity.putExtra(MOVIE_RATING, favourite.getRating());
            movieActivity.putExtra(MOVIE_DATE, favourite.getDate());
            mContext.startActivity(movieActivity);
        }
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new MovieFavViewHolder that holds the view for each movie
     */
    @Override
    public MovieFavViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_grid_item, viewGroup, false);
        return new MovieFavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieFavViewHolder holder, int position) {
        int moviePosterUrlIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_URL);

        // get to the right location in the cursor
        mCursor.moveToPosition(position);

        // Determine the values of the wanted data
        String moviePosterUrl = mCursor.getString(moviePosterUrlIndex);

        //Set values
        displayMoviePoster(holder, moviePosterUrl);
    }

    /**
     * Returns the number of movie items to display.
     */
    @Override
    public int getItemCount() {
        if(mCursor == null) return 0;
        return mCursor.getCount();
    }

    private Movie favouriteMovieCursor(){
        // Set the favourite movie details from the cursor
        Movie currentFavMovie = new Movie();
        currentFavMovie.setMovieId(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
        currentFavMovie.setTitle(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));
        currentFavMovie.setSynopsis(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS)));
        currentFavMovie.setPoster(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_URL)));
        currentFavMovie.setDate(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DATE)));
        currentFavMovie.setRating(mCursor.getDouble(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RATING)));
        return currentFavMovie;
    }

    /**
     * displayMoviePoster is called to display a movie poster
     *
     * @param movieViewHolder A ViewHolder for a movie item
     * @param moviePosterUrl A string url to display the poster using Picasso
     */
    private void displayMoviePoster(MovieCursorAdapter.MovieFavViewHolder movieViewHolder, String moviePosterUrl){
        if(!TextUtils.isEmpty(moviePosterUrl)) {
            Picasso.with(mContext).load(moviePosterUrl)
                    .resize(MOVIE_POSTER_WIDTH, MOVIE_POSTER_HEIGHT)
                    .centerCrop().placeholder(R.drawable.poster)
                    .error(R.drawable.poster).into(movieViewHolder.mPosterImageView);
        } else {
            movieViewHolder.mPosterImageView.setImageResource(R.drawable.poster);
        }
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c){
        // Check if this cursor is the same as the previous cursor (mCursor)
        if(mCursor == c) return null;
        Cursor temporary = mCursor;
        // New cursor value assigned
        this.mCursor = c;
        //check if this is a valid cursor, then update the cursor
        if(c != null) this.notifyDataSetChanged();
        return temporary;
    }

}
