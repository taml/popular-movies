package com.tamlove.popularmovies.movie_model;

import java.util.List;

/* Helper class to return both a list of Movie trailers and Movie reviews from one AsyncTask */
public class MovieExtra {

    private List<MovieTrailer> mMovieTrailer;
    private List<MovieReview> mMovieReview;

    public MovieExtra(List<MovieTrailer> movieTrailer, List<MovieReview> movieReview){
        mMovieTrailer = movieTrailer;
        mMovieReview = movieReview;
    }

    public List<MovieTrailer> getMovieTrailer(){
        return mMovieTrailer;
    }

    public List<MovieReview> getMovieReview() {
        return mMovieReview;
    }

}
