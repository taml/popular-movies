package com.tamlove.popularmovies.movie_model;

public class MovieReview {

    private String mMovieReviewContent;
    private String mMovieReviewAuthor;

    public MovieReview(String movieReviewContent, String movieReviewAuthor){
        mMovieReviewContent = movieReviewContent;
        mMovieReviewAuthor = movieReviewAuthor;
    }

    public String getMovieReviewContent(){
        return mMovieReviewContent;
    }

    public String getMovieReviewAuthor(){
        return mMovieReviewAuthor;
    }
}
