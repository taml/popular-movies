package com.tamlove.popularmovies.utilities;

import android.text.TextUtils;
import android.util.Log;

import com.tamlove.popularmovies.movie_model.Movie;
import com.tamlove.popularmovies.movie_model.MovieExtra;
import com.tamlove.popularmovies.movie_model.MovieReview;
import com.tamlove.popularmovies.movie_model.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieUtils {

    private static final String LOG_TAG = MovieUtils.class.getSimpleName();

    public static List<Movie> getMoviesFromJSON(String movieJSONString) {
        // If the JSON string is empty or null, then return early.
        if(TextUtils.isEmpty(movieJSONString)){
            return null;
        }

        // Create an empty ArrayList that movies can be added to it
        List<Movie> movies = new ArrayList<>();
        final String TMDB_RESULT = "results";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "title";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RATING = "vote_average";
        final String TMDB_DATE = "release_date";
        final String TMDB_POSTER = "poster_path";

        String id = "";
        String title = "";
        String synopsis = "";
        double rating = 0.0;
        String reversedDate = "";
        String fullPosterPath = "";

        try {
            // Create a base JSONObject
            JSONObject movieJSON = new JSONObject(movieJSONString);
            // Extract the JSONArray associated with the key called "results"
            JSONArray movieResults = movieJSON.getJSONArray(TMDB_RESULT);
            // For each movie in the movieResults array, create an {@link Movie} object
            for(int i = 0; i < movieResults.length(); i++){
                // Get a single movie at position i within the list of movies
                JSONObject currentMovie = movieResults.getJSONObject(i);
                // Extract the value for the key called "id"
                if(currentMovie.has(TMDB_ID)) {
                    id = currentMovie.getString(TMDB_ID);
                }
                // Extract the value for the key called "title"
                if(currentMovie.has(TMDB_TITLE)){
                    title = currentMovie.getString(TMDB_TITLE);
                }
                // Extract the value for the key called "overview"
                if(currentMovie.has(TMDB_OVERVIEW)){
                    synopsis = currentMovie.getString(TMDB_OVERVIEW);
                }
                // Extract the value for the key called "vote_average"
                if(currentMovie.has(TMDB_RATING)){
                    rating = currentMovie.getDouble(TMDB_RATING);
                }
                // Extract the value for the key called "release_date"
                if(currentMovie.has(TMDB_DATE)){
                    String date = currentMovie.getString(TMDB_DATE);
                    String splitDateAt = "-";
                    reversedDate = date;
                    if(date.contains(splitDateAt)){
                        String[] sections = date.split(splitDateAt);
                        if(sections.length == 3) {
                            reversedDate = sections[2] + " " + sections[1] + " " + sections[0];
                        }
                    }
                }
                // Extract the value for the key called "poster_path"
                if(currentMovie.has(TMDB_POSTER)){
                    String poster = currentMovie.getString(TMDB_POSTER);
                    fullPosterPath = "http://image.tmdb.org/t/p/w185//" + poster;
                }

                Log.i(LOG_TAG, "Id: " + id + " Title: " + title + " Overview: " + synopsis +
                        " Rating: " + rating + " Date: " + reversedDate + " Poster Path: " + fullPosterPath);
                // Create a new {@link Movie} object with the id, title, synopsis, rating and date
                // from the JSON response.
                Movie movie = new Movie(id, title, synopsis, rating, reversedDate, fullPosterPath);
                // Add the new {@link Movie} to the list of movies.
                movies.add(movie);
            }

        } catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG_TAG, "Problem printing the movie JSON results ", e);
        }

        // Return the list of movies
        return movies;
    }



    public static MovieExtra getSingleMovieFromJSON(String singleMovieJSONString) {
        // If the JSON string is empty or null, then return early.
        if(TextUtils.isEmpty(singleMovieJSONString)){
            return null;
        }

        // Create an empty ArrayList so that movie trailers can be added to it
        List<MovieTrailer> singleMovieTrailers = new ArrayList<>();
        // Create an empty ArrayList so that movie reviews can be added to it
        List<MovieReview> singleMovieReviews = new ArrayList<>();
        final String TMDB_RESULTS = "results";

        final String TMDB_VIDEOS = "videos";
        final String TMDB_KEY = "key";
        final String TMDB_SITE = "site";
        final String TMDB_NAME = "name";
        final String TMDB_TYPE = "type";

        final String TMDB_REVIEWS = "reviews";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";

        String movieTrailerKey = "";
        String fullTrailerPath = "";
        String movieTrailerSite = "";
        String movieTrailerName = "";
        String movieTrailerType = "";
        String movieReviewAuthor = "";
        String movieReviewContent = "";

        try {
            // Create a base JSONObject
            JSONObject movieJSON = new JSONObject(singleMovieJSONString);

            //Get the movie trailers JSON
            // Extract the JSONObject associated with the key "videos"
            JSONObject movieVideos = movieJSON.getJSONObject(TMDB_VIDEOS);
            // Extract the JSONArray associated with the key called "results"
            JSONArray movieTrailerResults = movieVideos.getJSONArray(TMDB_RESULTS);
            // For each extra in the movieResults array, create an {@link MovieTrailer} object
            for(int i = 0; i < movieTrailerResults.length(); i++){
                // Get a single movie trailer at position i within the list of movie trailers / videos
                JSONObject currentVideo = movieTrailerResults.getJSONObject(i);
                // Extract the value for the key called "key"
                if(currentVideo.has(TMDB_KEY)){
                    movieTrailerKey = currentVideo.getString(TMDB_KEY);
                    fullTrailerPath = "https://youtu.be/" + movieTrailerKey;
                }
                // Extract the value for the key called "site"
                if(currentVideo.has(TMDB_SITE)){
                    movieTrailerSite = currentVideo.getString(TMDB_SITE);
                }
                // Extract the value for the key called "name"
                if(currentVideo.has(TMDB_NAME)){
                    movieTrailerName = "Watch " + currentVideo.getString(TMDB_NAME);
                }
                // Extract the value for the key called "type"
                if(currentVideo.has(TMDB_TYPE)) {
                    movieTrailerType = currentVideo.getString(TMDB_TYPE);
                }

                if(!(movieTrailerSite.equals("YouTube") && movieTrailerType.equals("Trailer"))){
                    continue;
                }

                Log.i(LOG_TAG, "Key: " + fullTrailerPath + " Site: " + movieTrailerSite + " Name: " + movieTrailerName +
                        " Type: " + movieTrailerType);
                // Create a new {@link MovieTrailer} object with the key / url, site, name and type
                // from the JSON response.
                MovieTrailer movieTrailer = new MovieTrailer(fullTrailerPath, movieTrailerName);
                // Add the new {@link MovieTrailer} to the list of movie trailers.
                singleMovieTrailers.add(movieTrailer);
            }

            // Get the movie review JSON
            // Extract the JSONObject associated with the key "reviews"
            JSONObject movieReviews = movieJSON.getJSONObject(TMDB_REVIEWS);
            // Extract the JSONArray associated with the key called "results"
            JSONArray movieReviewResults = movieReviews.getJSONArray(TMDB_RESULTS);
            for(int j = 0; j < movieReviewResults.length(); j++) {
                // Get a single movie review at position j within the list of movie reviews
                JSONObject currentReview = movieReviewResults.getJSONObject(j);
                // Extract the value for the key called "author"
                if(currentReview.has(TMDB_AUTHOR)){
                    movieReviewAuthor = currentReview.getString(TMDB_AUTHOR);
                }
                // Extract the value for the key called "content"
                if(currentReview.has(TMDB_CONTENT)){
                    movieReviewContent = currentReview.getString(TMDB_CONTENT);
                }

                Log.i(LOG_TAG, "Review - Author: " + movieReviewAuthor + " Content: " + movieReviewContent);
                // Create a new {@link MovieReview} object with the review author and review content
                // from the JSON response.
                MovieReview movieReview = new MovieReview(movieReviewContent, movieReviewAuthor);
                // Add the new {@link MovieReview} to the list of movie reviews.
                singleMovieReviews.add(movieReview);
            }

        } catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG_TAG, "Problem printing the single movie video JSON results ", e);
        }

        // Return the list of movie trailers and movie reviews
        return new MovieExtra(singleMovieTrailers, singleMovieReviews);
    }

}
