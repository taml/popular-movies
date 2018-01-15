package com.tamlove.popularmovies.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tamlove.popularmovies.movie_model.Movie;
import com.tamlove.popularmovies.R;
import com.tamlove.popularmovies.ui.MovieActivity;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    public static final String MOVIE_ID = "id";
    public static final String MOVIE_TITLE = "title";
    public static final String MOVIE_SYNOPSIS = "synopsis";
    public static final String MOVIE_URL = "url";
    public static final String MOVIE_RATING = "rating";
    public static final String MOVIE_DATE = "date";
    public static final int MOVIE_POSTER_WIDTH = 360;
    public static final int MOVIE_POSTER_HEIGHT = 520;

    private List<Movie> mMovieData;
    private Context mContext;

    /**
     * Creates a MovieAdapter.
     * @param context      Context for the adapter
     * @param movie        A list of movies
     */
    public MovieAdapter(Context context, List<Movie> movie){
        mMovieData = movie;
        mContext = context;
    }


    /**
     * Cache of the children views for a movie list item.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mPosterImageView;

        public MovieViewHolder(View view){
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
            int adapterPosition = getAdapterPosition();
            Movie currentMovie = mMovieData.get(adapterPosition);
            Class destinationClass = MovieActivity.class;
            Intent movieActivity = new Intent(mContext, destinationClass);
            movieActivity.putExtra(MOVIE_ID, currentMovie.getMovieId());
            movieActivity.putExtra(MOVIE_TITLE, currentMovie.getTitle());
            movieActivity.putExtra(MOVIE_SYNOPSIS, currentMovie.getSynopsis());
            movieActivity.putExtra(MOVIE_URL, currentMovie.getPoster());
            movieActivity.putExtra(MOVIE_RATING, currentMovie.getRating());
            movieActivity.putExtra(MOVIE_DATE, currentMovie.getDate());
            mContext.startActivity(movieActivity);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If RecyclerView has more than one type of item viewType
     *                  integer can be used to provide a different layout.
     * @return A new MovieViewHolder that holds the View for each list item
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, the contents of the ViewHolder are updated to display the
     * movie details for this particular position, using the "position" argument.
     *
     * @param movieViewHolder The ViewHolder which should be updated to represent the
     *                        contents of the item at the given position in the data set.
     * @param position        The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieViewHolder movieViewHolder, int position) {
        Movie thisMovie = mMovieData.get(position);
        String poster = thisMovie.getPoster();
        displayMoviePoster(movieViewHolder,poster);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of movie items available
     */
    @Override
    public int getItemCount() {
        if (mMovieData == null) return 0;
        return mMovieData.size();
    }

    /**
     * displayMoviePoster is called to display a movie poster
     *
     * @param movieViewHolder A ViewHolder for a movie item
     * @param moviePosterUrl A string url to display the poster using Picasso
     */
    private void displayMoviePoster(MovieViewHolder movieViewHolder, String moviePosterUrl){
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
     * This method is used to set the movie on a MovieAdapter if there is
     * one already created. This is handy when new data is fetched from the
     * web but we don't want to create a new MovieAdapter to display it.
     *
     * @param movieData The new movie data to be displayed.
     */
    public void setMovieData(List<Movie> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }
}
