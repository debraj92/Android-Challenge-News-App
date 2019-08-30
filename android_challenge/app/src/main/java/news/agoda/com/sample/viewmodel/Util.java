package news.agoda.com.sample.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import news.agoda.com.sample.model.DataSource;
import news.agoda.com.sample.model.DataSourceFactory;

import static org.junit.Assert.assertNotNull;

/**
 * An Utility class
 */
class Util {

    /**
     * Checks if internet is connected
     * @param application    Application object for getting the application context
     * @return    true if internet is connected otherwise false.
     */
    static boolean isInternetAccessAvailable(Application application) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) application.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    /**
     * Returns the appropriate data source that will be used by the view model to initiate fetch of news
     * list from the server.
     * @param application    Application context to get the files directory which is used by the cache.
     * @param SOURCE_TYPE    The type of data source needed by the view model.
     * @return    The Data source object.
     */
    static DataSource getDataSource(Application application, DataSourceFactory.Sources SOURCE_TYPE) {
        return DataSourceFactory.getDataSource(SOURCE_TYPE,
                application.getApplicationContext().getFilesDir().toString());
    }
}
