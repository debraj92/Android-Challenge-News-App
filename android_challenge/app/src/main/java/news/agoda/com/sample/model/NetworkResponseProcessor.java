package news.agoda.com.sample.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import news.agoda.com.sample.AppConstants;
import news.agoda.com.sample.viewmodel.NewsEntity;

/**
 * Parse the json string obtained from the server and convert it into a list of news entity.
 */
class NetworkResponseProcessor implements NetworkProcessor{
    private static final String TAG = AppConstants.APP_TAG + "." +
            NetworkResponseProcessor.class.getSimpleName();

    private NewsDataBaseController mNewsDB;

    NetworkResponseProcessor(NewsDataBaseController newsdb) {
        mNewsDB = newsdb;
    }

    @Override
    public void setNext(NetworkProcessor next) {
        // stub
    }

    /**
     * Process the response from the server.
     * @param responseFromServer    json string from the server.
     *
     * @return    The list of news entity. Null if error.
     */
    @Override
    public ArrayList<NewsEntity> execute(String responseFromServer) {
        Log.d(TAG,"execute "+responseFromServer);
        ArrayList<NewsEntity> newsList;
        JSONArray results;
        try {
            JSONObject serverJson = new JSONObject(responseFromServer);
            /**
             * If data is missing or server returns error code, we cannot proceed.
             */
            if(! serverJson.getString("status").equals("OK") ) {
                // server response is an error
                return null;
            }
            results = serverJson.getJSONArray("results");
            fixServerResponse(results);
            Gson gson = new Gson();
            newsList =gson.fromJson(results.toString(),
                    new TypeToken<ArrayList<NewsEntity>>(){}.getType());

            // save response from server into DB for caching
            mNewsDB.writeToDB(results.toString());

        } catch (Exception e) {
            Log.e(TAG,"JSON parse failed "+e.getMessage());
            return null;
        }
        return newsList;
    }

    /**
     * The server JSON response is buggy. A multimedia is sometimes send as list (when it is not empty)
     * and empty-string when it is empty (instead of an empty list). This will cause gson to throw exception
     * when we try to convert the data into NewsEntity and MediaEntity objects (inconsistent types).
     * To fix this, we are going to change all the multimedia string values to empty array for consistency.
     */
    private void fixServerResponse(JSONArray responses) throws JSONException {
        for(int i=0; i<responses.length(); i++) {
            JSONObject response = responses.getJSONObject(i);
            if( response.get("multimedia") instanceof String) {
                response.put("multimedia",new JSONArray());
            }
        }
    }
}
