package news.agoda.com.sample.model;

import android.util.Log;

import org.jetbrains.annotations.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import news.agoda.com.sample.AppConstants;
import news.agoda.com.sample.viewmodel.NewsEntity;

/**
 * Makes a request to the server and passes the response to the NetworkResponse processor.
 */
class NetworkRequestProcessor implements NetworkProcessor{

    private static final String TAG = AppConstants.APP_TAG + "." +
            NetworkRequestProcessor.class.getSimpleName();

    /**
     * The next stage in the network processing pipeline is the network response processor.
     */
    private NetworkProcessor mNextNextworkProcessor;

    NetworkRequestProcessor(NewsDataBaseController newsdb) {
        // stub
    }

    @Override
    public void setNext(NetworkProcessor next) {
        mNextNextworkProcessor = next;
    }

    /**
     * Start fetching data from the server
     * @param url    URL to fetch data from the server.
     *
     * @return    The list of news.
     */
    @Override
    @Nullable
    public ArrayList<NewsEntity> execute(String url) {
        Log.d(TAG,"execute");
        String responseFromServer = fetchFromURL(url);
        return responseFromServer == null ? null :
                mNextNextworkProcessor.execute(responseFromServer);
    }

    @Nullable
    public String fetchFromURL(String url) {
        String responseFromServer;
        try {
            URL urlObj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
            responseFromServer = readStream(con.getInputStream());
        } catch (MalformedURLException e) {
            Log.e(TAG,"MalFormedURL "+e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG,"Error "+e.getMessage());
            return null;
        }
        return responseFromServer;
    }

    private String readStream(InputStream in) {
        Log.d(TAG,"readStream");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {

            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (IOException e) {
            Log.e(TAG,"Error "+e.getMessage());
        }
        return sb.toString();
    }
}
