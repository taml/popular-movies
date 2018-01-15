package com.tamlove.popularmovies.movie_model;

public class Movie {

    private String mId;
    private String mTitle;
    private String mSynopsis;
    private double mRating;
    private String mDate;
    private String mPoster;

    // Empty constructor for setting the movie details when getting them from the database
    public Movie(){
    }

    public Movie(String id, String title, String synopsis, double rating, String date, String poster){
        mId = id;
        mTitle = title;
        mSynopsis = synopsis;
        mRating = rating;
        mDate = date;
        mPoster = poster;
    }

    // Getter methods
    public String getMovieId() {
        return mId;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getSynopsis(){
        return mSynopsis;
    }

    public double getRating(){
        return mRating;
    }

    public String getDate(){
        return mDate;
    }

    public String getPoster(){
        return mPoster;
    }

    // Setter methods
    public void setMovieId(String id){
        mId = id;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public void setSynopsis(String synopsis){
        mSynopsis = synopsis;
    }

    public void setRating(double rating){
        mRating = rating;
    }

    public void setDate(String date){
        mDate = date;
    }

    public void setPoster(String poster){
        mPoster = poster;
    }

}
