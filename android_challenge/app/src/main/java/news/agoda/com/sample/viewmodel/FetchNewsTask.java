package news.agoda.com.sample.viewmodel;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import news.agoda.com.sample.AppConstants;
import news.agoda.com.sample.model.DataSource;


/**
 * This class is responsible for fetching the data from the repository (server/cache) in a background
 * thread.
 *
 */
class FetchNewsTask extends AsyncTask<Void, Void, ArrayList<NewsEntity>> {


    private final String TAG = AppConstants.APP_TAG + "." +
            FetchNewsTask.class.getSimpleName();

    /**
     * The data source object to fetch the data.
     */
    private DataSource mSource;

    /**
     * Will be used to relay fetch complete events to the caller.
     */
    private FetchListener mFetchListener;


    /**
     * Constructor
     * @param source    The data source object for fetching the data
     */
    FetchNewsTask(DataSource source) {
        mSource = source;
    }

    /**
     * Register listener for events
     * @param listener    Observer who implements the FetchListener interface.
     */
    void registerFetchCompleteListener(FetchListener listener) {
        Log.d(TAG,"registerFetchCompleteListener");
        mFetchListener = listener;
    }

    /**
     * Unregister event listener.
     */
    void unregisterFetchCompleteListener() {
        Log.d(TAG,"unregisterFetchCompleteListener");
        mFetchListener = null;
    }

    @Override
    protected ArrayList<NewsEntity> doInBackground(Void... voids) {
        Log.d(TAG,"Fetching News List in background");

        return mSource.fetchNewsList();
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG,"onPreExecute - Main Thread");
        if(mFetchListener != null) {
            mFetchListener.showProgress();
        }

    }

    @Override
    protected void onPostExecute(ArrayList<NewsEntity> newsEntities) {
        Log.d(TAG,"onPostExecute - Main Thread");

        if(mFetchListener != null) {
            if (newsEntities == null) {
                // if the list object is null it indicates an error during fetch.
                mFetchListener.onError();
            } else {
                // fetch is successful
                mFetchListener.onFetchSuccess(newsEntities);
            }
        }

    }
}
