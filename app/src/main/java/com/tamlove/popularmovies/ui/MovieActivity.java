package com.tamlove.popularmovies.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tamlove.popularmovies.movie_model.MovieExtra;
import com.tamlove.popularmovies.movie_model.MovieReview;
import com.tamlove.popularmovies.movie_model.MovieTrailer;
import com.tamlove.popularmovies.R;
import com.tamlove.popularmovies.adapters.MovieTrailerAdapter;
import com.tamlove.popularmovies.adapters.MovieTrailerAdapter.MovieTrailerAdapterOnClickHandler;
import com.tamlove.popularmovies.adapters.MovieReviewAdapter;
import com.tamlove.popularmovies.data.MovieContract;
import com.tamlove.popularmovies.utilities.MovieUtils;
import com.tamlove.popularmovies.utilities.QueryUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity implements MovieTrailerAdapterOnClickHandler {

    public static final String MOVIE_ID = "id";
    public static final String MOVIE_TITLE = "title";
    public static final String MOVIE_SYNOPSIS = "synopsis";
    public static final String MOVIE_URL = "url";
    public static final String MOVIE_RATING = "rating";
    public static final String MOVIE_DATE = "date";
    public static final int MOVIE_POSTER_WIDTH = 360;
    public static final int MOVIE_POSTER_HEIGHT = 520;
    public static final String MOVIE_URL_PATH_TRAILERS = "videos";
    public static final String MOVIE_URL_PATH_REVIEWS = "reviews";

    private TextView mTitleTextView;
    private TextView mSynopsisHeadingTextView;
    private TextView mSynopsisTextView;
    private TextView mTrailersHeadingTextView;
    private TextView mReviewsHeadingTextView;
    private ImageView mPosterImageViewDetail;
    private TextView mDateTextView;
    private ImageView mRatingImageView;
    private String movieId = "";
    private String movieTitle = "";
    private String movieSynopsis = "";
    private String moviePosterUrl = "";
    private String movieReleaseDate = "";
    private double movieRating = 0.0;
    private double movieRatingHalved = 0.0;
    private RecyclerView mMovieExtrasRecyclerView;
    private MovieTrailerAdapter movieTrailerAdapter;
    private MovieReviewAdapter movieReviewAdapter;
    private TextView mMovieExtrasErrorTextView;
    private boolean isTrailerError = false;
    private boolean isReviewError = false;
    private FloatingActionButton mfavFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mTitleTextView = findViewById(R.id.movie_title);
        mSynopsisHeadingTextView = findViewById(R.id.movie_synopsis_heading);
        mSynopsisTextView = findViewById(R.id.movie_synopsis);
        mTrailersHeadingTextView = findViewById(R.id.movie_trailers_heading);
        mReviewsHeadingTextView = findViewById(R.id.movie_reviews_heading);
        mPosterImageViewDetail = findViewById(R.id.movie_poster_detail);
        mDateTextView = findViewById(R.id.movie_date);
        mRatingImageView = findViewById(R.id.movie_star_rating);
        mMovieExtrasRecyclerView = findViewById(R.id.movie_extra_recycler_view);
        mMovieExtrasErrorTextView = findViewById(R.id.movie_extras_error);
        mfavFab = findViewById(R.id.favourite_fab);

        // LayoutManager, RecyclerView and Adapter for trailers list
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(MovieActivity.this);
        mMovieExtrasRecyclerView.setLayoutManager(trailerLayoutManager);
        mMovieExtrasRecyclerView.setHasFixedSize(true);
        movieTrailerAdapter = new MovieTrailerAdapter(MovieActivity.this, new ArrayList<MovieTrailer>(), this);
        movieReviewAdapter = new MovieReviewAdapter(MovieActivity.this, new ArrayList<MovieReview>());

        Intent movieItemIntent = getIntent();

        if (movieItemIntent != null) {

            if (movieItemIntent.hasExtra(MOVIE_ID)) {
                movieId = movieItemIntent.getStringExtra(MOVIE_ID);
            }

            if (movieItemIntent.hasExtra(MOVIE_TITLE)) {
                movieTitle = movieItemIntent.getStringExtra(MOVIE_TITLE);
                mTitleTextView.setText(movieTitle);
                setTitle(movieTitle);
            }

            if (movieItemIntent.hasExtra(MOVIE_SYNOPSIS)) {
                movieSynopsis = movieItemIntent.getStringExtra(MOVIE_SYNOPSIS);
                mSynopsisTextView.setText(movieSynopsis);
            }

            if (movieItemIntent.hasExtra(MOVIE_URL)) {
                moviePosterUrl = movieItemIntent.getStringExtra(MOVIE_URL);
                displayMoviePoster(moviePosterUrl);
            }

            if (movieItemIntent.hasExtra(MOVIE_DATE)) {
                movieReleaseDate = movieItemIntent.getStringExtra(MOVIE_DATE);
                mDateTextView.setText(movieReleaseDate);
            }

            if (movieItemIntent.hasExtra(MOVIE_RATING)) {
                movieRating = movieItemIntent.getDoubleExtra(MOVIE_RATING, 0.0);
                /* Divide rating by 2, so it can be out of 5 stars instead of 10 */
                movieRatingHalved = movieRating;
                movieRatingHalved /= 2;
                RATING individualMovieRating = RATING.getRating(movieRatingHalved);
                displayRatingStars(individualMovieRating);
            }
        }

        /* Add an underline to Synopsis heading when the layout loads */
        mSynopsisHeadingTextView.setPaintFlags(mSynopsisHeadingTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        /* Hide the movie extras RecyclerView and error text when the layout loads */
        mMovieExtrasRecyclerView.setVisibility(View.GONE);
        mMovieExtrasErrorTextView.setVisibility(View.GONE);
        /* Call the AsyncTask to fetch trailers and reviews for a single movie */
        new FetchMovieExtrasTask().execute(movieId);

        if(movieMarkedAsFavourite()){
            mfavFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPanelBackground)));
            mfavFab.setImageResource(R.drawable.favourite_solid);
        }

        mfavFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieMarkedAsFavourite()) {
                    deleteMovieFromFavourites();
                } else {
                    addMovieToFavourites();
                }
            }
        });
    }

    /**
     * displayMoviePoster is called to display a movie poster
     *
     * @param moviePosterUrl A string url to display the poster using Picasso
     */
    public void displayMoviePoster(String moviePosterUrl) {
        if (!TextUtils.isEmpty(moviePosterUrl)) {
            Picasso.with(MovieActivity.this).load(moviePosterUrl)
                    .resize(MOVIE_POSTER_WIDTH, MOVIE_POSTER_HEIGHT)
                    .centerCrop().placeholder(R.drawable.poster)
                    .error(R.drawable.poster).into(mPosterImageViewDetail);
        } else {
            mPosterImageViewDetail.setImageResource(R.drawable.poster);
        }
    }

    /* Play movie trailers in YouTube App or Browser if either are available */
    @Override
    public void onClick(MovieTrailer movieItem) {
        Uri movieTrailerUrl = Uri.parse(movieItem.getMovieTrailerKey());
        Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW);
        playTrailerIntent.setData(movieTrailerUrl);
        if (playTrailerIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(playTrailerIntent);
        }
    }

    /* When Synopsis TextView is selected only underline the Synopsis heading and hide Trailer and Review content */
    public void synopsisSelected(View view) {
        mSynopsisHeadingTextView.setPaintFlags(mSynopsisHeadingTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mSynopsisTextView.setVisibility(View.VISIBLE);
        mTrailersHeadingTextView.setPaintFlags(0);
        mMovieExtrasErrorTextView.setVisibility(View.GONE);
        mMovieExtrasRecyclerView.setVisibility(View.GONE);
        mReviewsHeadingTextView.setPaintFlags(0);
    }

    /* When Trailers TextView is selected only underline the Trailers heading and hide Review and Synopsis content */
    public void trailersSelected(View view) {
        mTrailersHeadingTextView.setPaintFlags(mTrailersHeadingTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (isTrailerError) {
            mMovieExtrasErrorTextView.setText(getString(R.string.trailer_error_message));
            mMovieExtrasErrorTextView.setVisibility(View.VISIBLE);
            mMovieExtrasRecyclerView.setVisibility(View.GONE);
        } else {
            mMovieExtrasErrorTextView.setVisibility(View.GONE);
            mMovieExtrasRecyclerView.setAdapter(movieTrailerAdapter);
            mMovieExtrasRecyclerView.setVisibility(View.VISIBLE);
        }
        mReviewsHeadingTextView.setPaintFlags(0);
        mSynopsisHeadingTextView.setPaintFlags(0);
        mSynopsisTextView.setVisibility(View.GONE);
    }

    /* When Reviews TextView is selected only underline the Reviews heading and hide Trailers and Synopsis content */
    public void reviewsSelected(View view) {
        mReviewsHeadingTextView.setPaintFlags(mReviewsHeadingTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (isReviewError) {
            mMovieExtrasErrorTextView.setText(getString(R.string.review_error_message));
            mMovieExtrasErrorTextView.setVisibility(View.VISIBLE);
            mMovieExtrasRecyclerView.setVisibility(View.GONE);
        } else {
            mMovieExtrasErrorTextView.setVisibility(View.GONE);
            mMovieExtrasRecyclerView.setAdapter(movieReviewAdapter);
            mMovieExtrasRecyclerView.setVisibility(View.VISIBLE);
        }
        mSynopsisHeadingTextView.setPaintFlags(0);
        mSynopsisTextView.setVisibility(View.GONE);
        mTrailersHeadingTextView.setPaintFlags(0);
    }

    /**
     * Set right image based on RATING value
     *
     * @param rating A RATING obtained from checking a double rating against an enum RATING
     */
    private void displayRatingStars(RATING rating) {
        switch (rating) {
            case ONESTAR:
                mRatingImageView.setImageResource(R.drawable.onestar);
                break;
            case TWOSTARS:
                mRatingImageView.setImageResource(R.drawable.twostars);
                break;
            case THREESTARS:
                mRatingImageView.setImageResource(R.drawable.threestars);
                break;
            case FOURSTARS:
                mRatingImageView.setImageResource(R.drawable.fourstars);
                break;
            case FIVESTARS:
                mRatingImageView.setImageResource(R.drawable.fivestars);
                break;
        }
    }

    /* Add a movie to favourites database */
    private void addMovieToFavourites() {
        if (!(movieId.equals("") || movieTitle.equals(""))) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, movieSynopsis);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_URL, moviePosterUrl);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DATE, movieReleaseDate);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, movieRating);

            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            if (uri != null) {
                mfavFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPanelBackground)));
                mfavFab.setImageResource(R.drawable.favourite_solid);
                Toast.makeText(getBaseContext(), getString(R.string.favourite_added, movieTitle), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.favourite_adding_error), Toast.LENGTH_LONG).show();
        }
    }

    /* Delete a movie from favourites database */
    private void deleteMovieFromFavourites() {
        if (!(movieId.equals(""))) {
            String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
            String[] selectionArgs = {movieId};
            int rowsDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, selection, selectionArgs);
            if (rowsDeleted != 0) {
                mfavFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                mfavFab.setImageResource(R.drawable.favourite);
                Toast.makeText(getBaseContext(), getString(R.string.favourite_removed, movieTitle), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.favourite_removing_error), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.favourite_removing_error), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method checks whether a movie exists in the favourites database by querying the movie ID (not database row ID)
     *
     * @return true or false depending on if the movie is already in the database
     */
    private boolean movieMarkedAsFavourite() {
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        String favouriteMovieId = "";
        if (cursor == null) {
            return false;
        } else if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        }
        while (cursor.moveToNext()) {
            favouriteMovieId = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            if (favouriteMovieId.equals(movieId) && !favouriteMovieId.equals("")) {
                return true;
            }
        }
        return false;
    }

    /* enum RATING to apply the star rating a double should have. If rating is 3.6 rating will be
     * rounded up so that a 4 star image can be displayed. If rating is 3.4 rating will be rounded
     * down so that a 3 star image can be displayed. */
    public enum RATING {
        ONESTAR(0.0), TWOSTARS(1.5), THREESTARS(2.5), FOURSTARS(3.5), FIVESTARS(4.5);

        private final double mRatingValue;

        RATING(double starRating) {
            mRatingValue = starRating;
        }

        public static RATING getRating(double movieRating) {
            RATING rated = ONESTAR;
            for (RATING rating : values()) {
                if (rating.mRatingValue <= movieRating) {
                    rated = rating;
                }
            }
            return rated;
        }
    }

    /* Fetch trailers and reviews data */
    public class FetchMovieExtrasTask extends AsyncTask<String, Void, MovieExtra> {

        @Override
        protected MovieExtra doInBackground(String... params) {
            // If there's no single movie Id, don't look anything up
            if (params.length == 0) {
                return null;
            }

            String singleMovieId = params[0];
            URL movieTrailersUrl = QueryUtils.buildSingleMovieURL(singleMovieId, MOVIE_URL_PATH_TRAILERS);
            URL movieReviewsUrl = QueryUtils.buildSingleMovieURL(singleMovieId, MOVIE_URL_PATH_REVIEWS);

            try {
                String jsonMovieTrailersResponse = QueryUtils.getResponseFromHttpUrl(movieTrailersUrl);
                String jsonMovieReviewsResponse = QueryUtils.getResponseFromHttpUrl(movieReviewsUrl);
                List<MovieTrailer> jsonMovieTrailerData = MovieUtils.getMovieTrailersFromJSON(jsonMovieTrailersResponse);
                List<MovieReview> jsonMovieReviewData = MovieUtils.getMovieReviewsFromJSON(jsonMovieReviewsResponse);
                return new MovieExtra(jsonMovieTrailerData, jsonMovieReviewData);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieExtra movieExtras) {
            if (movieExtras.getMovieTrailer() == null || movieExtras.getMovieTrailer().isEmpty()) {
                isTrailerError = true;
            } else {
                movieTrailerAdapter.setMovieTrailersData(movieExtras.getMovieTrailer());
            }
            if (movieExtras.getMovieReview() == null || movieExtras.getMovieReview().isEmpty()) {
                isReviewError = true;
            } else {
                movieReviewAdapter.setMoviesReviewData(movieExtras.getMovieReview());
            }
        }
    }

}
