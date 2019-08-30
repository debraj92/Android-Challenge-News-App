package news.agoda.com.sample.model;

import org.jetbrains.annotations.Nullable;

/**
 * This factory creates and returns the data source that the viewmodel (newsviewmodel) has requested.
 * It uses Factory pattern.
 */
public class DataSourceFactory {

    /**
     * The types of data sources available.
     */
    public enum Sources {
      SERVER,
      CACHE
    }

    /**
     * Create and obtain a data source.
     * @param type    The type of data source needed.
     * @param filesDirPath    The path to the files directory which is needed by the cache db controller.
     * @return
     */
    @Nullable
    public static DataSource getDataSource(Sources type, String filesDirPath) {
        /**
         * We first need access to the cache db controller.
         */
        NewsDataBaseController newsDB = NewsDataBaseController.getInstance(filesDirPath);
        switch (type) {
            case SERVER:
                /**
                 * create the server data source. This fetches data from the server and uses the internet.
                 */
                return new ServerDataSource(newsDB);
            case CACHE:
                /**
                 *  create the cache data source. This fetches data from the cache which is present locally
                 *  in the internal storage of this app. It can be removed by clearing data from settings.
                 */
                return new CacheDataSource(newsDB);

                default:
                    // Don't expect null to be returned
                    return null;
        }
    }
}
