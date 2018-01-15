package com.tamlove.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tamlove.popularmovies.movie_model.MovieTrailer;
import com.tamlove.popularmovies.R;

import java.util.List;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerViewHolder>{

    private List<MovieTrailer> mMoviesTrailerData;
    private Context mContext;

    /*
     * An on-click handler that's defined to make it easy for an Activity to interface with
     * the RecyclerView.
     */
    private final MovieTrailerAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieTrailerAdapterOnClickHandler {
        void onClick(MovieTrailer movieItem);
    }

    /**
     * Creates a MovieTrailerAdapter.
     * @param context      Context for the adapter
     * @param movieTrailer        A list of movie trailers
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieTrailerAdapter(Context context, List<MovieTrailer> movieTrailer, MovieTrailerAdapterOnClickHandler clickHandler){
        mMoviesTrailerData = movieTrailer;
        mContext = context;
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a movie trailer list item.
     */
    public class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTrailerTextView;

        public MovieTrailerViewHolder(View view){
            super(view);
            mTrailerTextView = view.findViewById(R.id.trailer_name);
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
            MovieTrailer currentTrailer = mMoviesTrailerData.get(adapterPosition);
            mClickHandler.onClick(currentTrailer);
        }
    }

    @Override
    public MovieTrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieTrailerAdapter.MovieTrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerViewHolder holder, int position) {
        MovieTrailer thisMovieTrailer = mMoviesTrailerData.get(position);
        String trailerName = thisMovieTrailer.getMovieTrailerName();
        holder.mTrailerTextView.setText(trailerName);
    }

    @Override
    public int getItemCount() {
        if(mMoviesTrailerData == null) return 0;
        return mMoviesTrailerData.size();
    }

    /**
     * This method is used to set the movie trailer on a MovieTrailerAdapter if there
     * is one already created. This is handy when new data is fetched from the
     * web but we don't want to create a new MovieTrailerAdapter to display it.
     *
     * @param movieTrailersData The new movie trailer data to be displayed.
     */
    public void setMovieTrailersData(List<MovieTrailer> movieTrailersData) {
        mMoviesTrailerData = movieTrailersData;
        notifyDataSetChanged();
    }
}
