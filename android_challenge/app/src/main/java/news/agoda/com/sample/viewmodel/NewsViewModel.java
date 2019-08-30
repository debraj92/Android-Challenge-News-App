package news.agoda.com.sample.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

import news.agoda.com.sample.AppConstants;
import news.agoda.com.sample.R;
import news.agoda.com.sample.model.DataSource;
import news.agoda.com.sample.model.DataSourceFactory;

import static org.junit.Assert.assertNotNull;

/**
 * The viewmodel class for the MainActivity (view). This is responsible for relaying most of the business
 * logic to appropriate action dispatchers. It is also responsible for communicating with the model for
 * fetching data from the server or the cache using background threads.
 *
 * Notice this class extends from the AndroidViewModel and hence has access to the application context.
 * This is important because we don't want to hold a reference of the MainActivity in the viewmodel as
 * it can leak memory.
 *
 * This class also implements the FetchListener and hence gets callbacks when a data fetch is completed
 * and also when a data fetch has started. On a data fetch start the view model displays the progress bar.
 */
public class NewsViewModel extends AndroidViewModel implements FetchListener {

    private static final String TAG = AppConstants.APP_TAG +"."+NewsViewModel.class.getSimpleName();

    /**
     * The list of news fetched from the model. This is live data and thus the view (MainActivity) would
     * receive callbacks (onChange) when the list is updated.
     */
    private MutableLiveData<ArrayList<NewsEntity>> newsItemList = new MutableLiveData<>();

    /**
     * Indicates if the indeterminate progress bar should be displayed or not. It is displayed when a
     * data fetch is in progress.
     */
    private MutableLiveData<Boolean> mProgressBarStatus = new MutableLiveData<>();

    /**
     * Indicates if the device is connected to the internet.
     */
    private MutableLiveData<Boolean> mOffline = new MutableLiveData<>();

    /**
     * The data source or the model
     */
    private DataSource mSource;

    /**
     * This is set to indicate if a data fetch failed. For a cache fetch fail we ask the user to connect
     * to the internet and for a server fetch fail we display - invalid server response or network error.
     * Notice this value is set in the layout using data binding.
     */
    public String mReadErrorMessage;

    /**
     * Indicates if a data fetch is in progress.
     */
    private boolean mIsFetchActive =false;

    /**
     * An instance of a async task which will be used to fetch data using a background thread (not main
     * thread).
     */
    private FetchNewsTask mFetchTask;

    /**
     * Constructor
     * @param application    The application context
     */
    public NewsViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Get the live list of news for setting up the LiveData observer pattern
     * @return    livedata list of news
     */
    public LiveData<ArrayList<NewsEntity>> getListOfNews() {
        return newsItemList;
    }

    /**
     * Get the progress status for setting up the LiveData observer pattern
     * @return    live progress bar status object
     */
    public LiveData<Boolean> getProgressBarStatus () {
        return mProgressBarStatus;
    }

    /**
     * Get the offline status object for setting up the LiveData observer pattern
     * @return    live offline status object
     */
    public LiveData<Boolean> getOfflineStatus () {return mOffline;}

    /**
     * Close connection to the model and stop the cache db controller. We also unregister background data
     * fetch callback.
     */
    public void cleanUpNewsDB() {
        if(mSource != null) {
            mSource.close();
        }
        if(mFetchTask != null) {
            mFetchTask.unregisterFetchCompleteListener();
        }
    }

    /**
     * This is used to fetch the list of news on a worker thread.
     * @param isStartAfterDestroy    indicates whether the view (MainActivity) is recreated as a result
     *                               of screen orientation change. If so, we fetch the data from the cache.
     */
    public void fetchNewsList(boolean isStartAfterDestroy) {
        Log.d(TAG,"fetchNewsList");
        if(!isStartAfterDestroy) {
            // Fresh start of activity
            /**
             * if a fetch is currently in progress we can ignore multiple requests to the server/cache.
             * Notice the mIsFetchActive member variable is thread safe as it is read and set only by a
             * single thread - main thread. It is not shared across threads.
             */
            if(!mIsFetchActive) {
                mIsFetchActive = true;
                /**
                 * If network is available fetch from server, otherwise from the cache.
                 */
                if (isInternetAccessAvailable()) {
                    fetchNewsListInternal(DataSourceFactory.Sources.SERVER);
                } else {
                    fetchNewsListInternal(DataSourceFactory.Sources.CACHE);
                }
            }
        } else {
            if(!mIsFetchActive) {
                mIsFetchActive = true;
                // configuration change. Fetch from cache
                fetchNewsListInternal(DataSourceFactory.Sources.CACHE);
            }
        }

    }

    /**
     * Checks if internet is available.
     * @return    true if internet is up.
     */
    public boolean isInternetAccessAvailable() {
        boolean isInternetUp = Util.isInternetAccessAvailable(getApplication());
        mOffline.setValue(!isInternetUp);
        return isInternetUp;
    }

    /**
     * Fetches the list of news using an async task on a worker thread.
     * @param SOURCE_TYPE    Either Server or Cache
     */
    private void fetchNewsListInternal(DataSourceFactory.Sources SOURCE_TYPE) {
        /**
         * The util method uses the DataSourceFactory and the Source type to return the appropriate
         * data source. The DataSources uses the Factory pattern.
         */
        mSource = Util.getDataSource(getApplication(),SOURCE_TYPE);
        // we don't expect the data source to be null.
        assertNotNull(mSource);
        // create a new async task
        mFetchTask = new FetchNewsTask(mSource);
        // register callbacks (observer pattern).
        mFetchTask.registerFetchCompleteListener(this);
        // start the thread.
        mFetchTask.execute();
    }

    /**
     * Callback when the fetch has started and we need to show the progress bar.
     */
    @Override
    public void showProgress() {
        mProgressBarStatus.setValue(true);
    }

    /**
     * Callback when a list of news is fetched successfully.
     * @param newsEntities     list of news fetched.
     */
    @Override
    public void onFetchSuccess(ArrayList<NewsEntity> newsEntities) {
        Log.d(TAG,"onFetchSuccess");
        // remove the progress bar
        mProgressBarStatus.setValue(false);
        // toggle fetch active to false so that another fetch request can be made.
        mIsFetchActive = false;
        // update the live news list so that it shows up on the UI.
        newsItemList.setValue(newsEntities);
        mFetchTask.unregisterFetchCompleteListener();
    }

    /**
     * Callback when an error occurred during fetching of news
     */
    @Override
    public void onError() {
        Log.d(TAG,"onError");
        // remove the progress bar
        mProgressBarStatus.setValue(false);
        // display error message (specific to data source) to the user.
        // Notice these values are updated using data binding.
        if(mSource.getType() == DataSourceFactory.Sources.SERVER) {
            mReadErrorMessage = getApplication().getApplicationContext()
                    .getString(R.string.read_error_server);
        } else {
            mReadErrorMessage = getApplication().getApplicationContext()
                    .getString(R.string.read_error_cache);
        }
        // set fetch active to false so that another fetch request can be made.
        mIsFetchActive = false;
        // Set empty list on failure
        newsItemList.setValue(new ArrayList<NewsEntity>());
        mFetchTask.unregisterFetchCompleteListener();
    }

    /**
     * Indicates if a fetch is currently in progress.
     * @return    if fetch is in progress.
     */
    public boolean isFetchInProgress() {
        return mIsFetchActive;
    }

}
