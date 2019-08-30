package news.agoda.com.sample.model;

import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import news.agoda.com.sample.AppConstants;
import news.agoda.com.sample.viewmodel.NewsEntity;

/**
 * The server data source fetches data from the server using the provided urls. It first attempts to fetch
 * the data using the URL_1. If that does not work, it tries to fetch it from an alternate URL - URL_2.
 */
class ServerDataSource implements DataSource {

    private static final String TAG = AppConstants.APP_TAG + "." + ServerDataSource.class.getSimpleName();
    private static final String URL_1 = "https://api.myjson.com/bins/nl6jh";
    // The URL_2 is a backup url if URL_1 fails to respond
    private static final String URL_2 = "http://www.mocky.io/v2/573c89f31100004a1daa8adb";

    /**
     * All requests from the server are handled using this controller.
     */
    private NewsDataBaseController mNewsDB;

    ServerDataSource (NewsDataBaseController newsDB) {
        mNewsDB = newsDB;
    }

    @Override
    @Nullable
    public ArrayList<NewsEntity> fetchNewsList() {
        Log.d(TAG,"fetchNewsList");
        ArrayList<NewsEntity> newsEntityList;
        /**
         * Fetch using the first URL
         */
        newsEntityList = fetchNewsListInternal(URL_1);
        if(newsEntityList == null) {
            /**
             * If unsuccessful, retry using the second url.
             */
            newsEntityList = fetchNewsListInternal(URL_2);
        }

        return newsEntityList;
    }

    /**
     * The processing of data from the server is broken into two parts. The first task is fetch from
     * the server and the second task is to parse the response of the server and create list of news
     * entity.
     * For this we have used the chain of responsibility design pattern.
     *
     * Also note, once a data is fetched from the server we will have to update the cache so that it
     * always contains the latest news. The newsdb controller is used for this purpose.
     *
     * @param url    The url from which data needs to be sourced.
     * @return    The list of news entity which is needed by the viewmodel.
     */
    private ArrayList<NewsEntity> fetchNewsListInternal(String url) {
        ArrayList<NewsEntity> newsEntityList;

        // chain of responsibility pattern
        NetworkProcessor networkProcessor = new NetworkRequestProcessor(mNewsDB);
        networkProcessor.setNext(new NetworkResponseProcessor(mNewsDB));
        newsEntityList = networkProcessor.execute(url);

        return newsEntityList;
    }

    /**
     * clean up the cache db controller
     */
    @Override
    public void close() {
        mNewsDB.cleanUp();
    }

    /**
     * Get type of data source
     * @return    SERVER
     */
    @Override
    public DataSourceFactory.Sources getType() {
        return DataSourceFactory.Sources.SERVER;
    }


}
