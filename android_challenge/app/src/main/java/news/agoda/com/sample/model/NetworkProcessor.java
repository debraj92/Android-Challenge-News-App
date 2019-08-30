package news.agoda.com.sample.model;

import java.util.ArrayList;

import news.agoda.com.sample.viewmodel.NewsEntity;

/**
 * A network processor is part of stage in processing server requests. It is used for implementing the
 * chain of responsibility design pattern.
 */
interface NetworkProcessor {

    /**
     * Set the network processor for the next stage of the processing pipeline.
     * @param next    A network processor.
     */
    void setNext(NetworkProcessor next);

    /**
     * The final result of the network processing would produce a list of news entities which is required
     * by the view model.
     * @param args    Any string data that once stage of the pipeline wants to pass to the next step.
     *
     * @return    The list of news entities.
     */
    ArrayList<NewsEntity> execute(String args);

}
