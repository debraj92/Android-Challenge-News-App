package news.agoda.com.sample.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import news.agoda.com.sample.AppConstants;
import news.agoda.com.sample.viewmodel.NewsEntity;

/**
 * The cache data source fetches the news from the locally stored cache. In this application, our cache
 * is just a simple file stored in the internal storage of the application.
 */
class CacheDataSource implements DataSource {

    private static final String TAG = AppConstants.APP_TAG + "." + CacheDataSource.class.getSimpleName();

    private static final DataSourceFactory.Sources TYPE = DataSourceFactory.Sources.CACHE;

    /**
     * The controller of the cache. All read and write into the cache are carefully handled using this
     * controller.
     */
    private NewsDataBaseController mNewsDB;

    CacheDataSource (@NotNull NewsDataBaseController newsDB) {
        mNewsDB = newsDB;
    }

    @Override
    @Nullable
    public ArrayList<NewsEntity> fetchNewsList() {
        /**
         * The list of news is stored in the cache as json string. We will first get this list using the
         * newsdb cache controller and then convert the json to a list of POJO (plain old java object).
         */
        Log.d(TAG,"fetchNewsList");
        ArrayList<NewsEntity> newsList;

        JSONArray results;
        try {
            // This call is blocking.
            String storedResponse = mNewsDB.readFromDB();
            if(storedResponse == null) {
                // if read failed, let the caller know by sending null
                return null;
            }
            // the read is successful. Parse using json using gson library.
            results = new JSONArray(storedResponse);
            Gson gson = new Gson();
            newsList =gson.fromJson(results.toString(),
                    new TypeToken<ArrayList<NewsEntity>>(){}.getType());
        } catch (JSONException e) {
            Log.e(TAG,"JSON parse error "+e.getMessage());
            return null;
        }

        return newsList;
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
     * @return    CACHE
     */
    @Override
    public DataSourceFactory.Sources getType() {
        return DataSourceFactory.Sources.CACHE;
    }

}
