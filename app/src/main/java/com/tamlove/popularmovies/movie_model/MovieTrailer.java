package com.tamlove.popularmovies.movie_model;


public class MovieTrailer {

    private String mMovieTrailerKey;
    private String mMovieTrailerName;

    public MovieTrailer(String movieTrailerKey, String movieTrailerName){
        mMovieTrailerKey = movieTrailerKey;
        mMovieTrailerName = movieTrailerName;
    }


    public String getMovieTrailerKey(){
        return mMovieTrailerKey;
    }

    public String getMovieTrailerName(){
        return mMovieTrailerName;
    }
}
