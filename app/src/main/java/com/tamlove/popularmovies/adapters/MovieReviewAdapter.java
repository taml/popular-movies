package com.tamlove.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tamlove.popularmovies.movie_model.MovieReview;
import com.tamlove.popularmovies.R;

import java.util.List;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder>{

    private List<MovieReview> mMoviesReviewData;
    private Context mContext;

    /**
     * Creates a MovieTrailerAdapter.
     * @param context      Context for the adapter
     * @param movieReview        A list of movie reviews
     */
    public MovieReviewAdapter(Context context, List<MovieReview> movieReview){
        mMoviesReviewData = movieReview;
        mContext = context;
    }

    /**
     * Cache of the children views for a movie trailer list item.
     */
    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        public final TextView mReviewAuthorTextView;
        public final TextView mReviewContentTextView;

        public MovieReviewViewHolder(View view) {
            super(view);
            mReviewAuthorTextView = view.findViewById(R.id.review_author);
            mReviewContentTextView = view.findViewById(R.id.review_content);
        }
    }

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieReviewAdapter.MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        MovieReview thisMovieReview = mMoviesReviewData.get(position);
        String reviewAuthor = thisMovieReview.getMovieReviewAuthor();
        holder.mReviewAuthorTextView.setText(reviewAuthor);
        String reviewContent = thisMovieReview.getMovieReviewContent();
        holder.mReviewContentTextView.setText(reviewContent);
    }

    @Override
    public int getItemCount() {
        if(mMoviesReviewData == null) return 0;
        return mMoviesReviewData.size();
    }

    /**
     * This method is used to set the movie review on a MovieReviewAdapter if there
     * is one already created. This is handy when new data is fetched from the
     * web but we don't want to create a new MovieReviewAdapter to display it.
     *
     * @param movieReviewsData The new movie review data to be displayed.
     */
    public void setMoviesReviewData(List<MovieReview> movieReviewsData){
        mMoviesReviewData = movieReviewsData;
        notifyDataSetChanged();
    }
}
