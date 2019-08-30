package news.agoda.com.sample.model;

import java.util.ArrayList;

import news.agoda.com.sample.viewmodel.NewsEntity;

/**
 * All data sources must implement this interface. This ensures that different data sources are following a
 * standard and the clients can expect to get well-defined behaviours from various data sources.
 */
public interface DataSource {

    /**
     * Fetch a list of news from the data source. Note this method is blocking and should be called from
     * a background thread. For this application, we are calling it from the async task (FetchNewsTask)
     * @return    list of news entities.
     */
    ArrayList<NewsEntity> fetchNewsList();

    /**
     * Close connection to the data source.
     */
    void close();

    /**
     * Get the type of the data source.
     * @return    Type of the data source.
     */
    DataSourceFactory.Sources getType();

}
