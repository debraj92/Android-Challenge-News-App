package news.agoda.com.sample.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import news.agoda.com.sample.AppConstants;
import news.agoda.com.sample.R;

/**
 * The detail view activity is launched in from the single pane layout of the main activity. It uses
 * the detail fragment to show the details of the selected news.
 */
public class DetailViewActivity extends AppCompatActivity {
    private static final String TAG = AppConstants.APP_TAG +"."+DetailViewActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"OnCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        FragmentDetail detail = (FragmentDetail) getSupportFragmentManager().findFragmentById(R.id.fragmentDetails);

        Intent intent = getIntent();
        detail.updateUI(intent.getExtras());
    }

}
