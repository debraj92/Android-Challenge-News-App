package news.agoda.com.sample.viewmodel;

import java.util.ArrayList;

/**
 * A callback for listening to data fetch complete. The caller must implement this interface
 */
public interface FetchListener {
    /**
     * Show progress bar
     */
    void showProgress();

    /**
     * Called if data fetch is successful.
     * @param newsEntities     list of news fetched.
     */
    void onFetchSuccess(ArrayList<NewsEntity> newsEntities);

    /**
     * Called if data fetch failed.
     */
    void onError();
}
