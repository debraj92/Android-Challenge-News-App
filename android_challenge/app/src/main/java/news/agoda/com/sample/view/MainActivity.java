package news.agoda.com.sample.view;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import news.agoda.com.sample.AppConstants;
import news.agoda.com.sample.NewsListAdapter;
import news.agoda.com.sample.R;
import news.agoda.com.sample.databinding.FragmentMainBinding;
import news.agoda.com.sample.viewmodel.MediaEntity;
import news.agoda.com.sample.viewmodel.NewsEntity;
import news.agoda.com.sample.viewmodel.NewsViewModel;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

/**
 * The launch activity that displays the list of news. It is responsible for setting the UI components
 * and managing the fragments - list of news fragment and details fragment. It holds the viewmodel -
 * NewsViewModel object and inform it about UI interactions. It also listens to changes by the viewmodel
 * using LiveData objects (observer pattern).
 *
 * The entire interaction follows the MVVM pattern (model-view-viewmodel).
 *
 * Please Note: We are following the convention to name member variables as starting with m. Static
 * member variables will start with s. Constants are all capitals.
 */
public class MainActivity extends AppCompatActivity implements LifecycleOwner,
        Observer<ArrayList<NewsEntity>> {

    private static final String TAG = AppConstants.APP_TAG + "." + MainActivity.class.getSimpleName();

    /**
     * Indicates if a dual pane layout is setup. We use dual pane for tablets in landscape mode, otherwise
     * single pane layout. In a dual pane layout the news list and the details view are shown side by side.
     */
    private boolean mIsDualPaneLayout;

    /**
     * We have used data binding to connect code with layout xml files. This ensures our code stays concise
     * with less boilerplates.
     */
    private FragmentMainBinding mFragmentMainBinding;

    /**
     * Front end (or UI) updates are relayed to the backend business logic using the viewmodel.
     */
    private NewsViewModel mNewsViewModel;

    /**
     * Instance of the list of news fragment
     */
    private FragmentMain mMain;

    /**
     * Instance of the details view fragment.
     */
    private FragmentDetail mDetail;

    /**
     * For Android lifecycle callbacks
     */
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);

        setContentView(R.layout.activity_main);

        mMain = (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.fragmentMain);
        mDetail = (FragmentDetail) getSupportFragmentManager().findFragmentById(R.id.fragmentDetails);
        mFragmentMainBinding = mMain.getFragmentMainBinding();

        /**
         * Check if we are on a dual pane layout (Tablet).
         */
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE
                && mDetail != null) {
            Log.d(TAG, "dual pane layout");
            // dual pane
            mIsDualPaneLayout = true;
        } else {
            Log.d(TAG, "single pane layout");
            // single pane - smart phone
            mIsDualPaneLayout = false;
        }


        /**
         * The MainActivity will observe changes to the list of news and update the UI accordingly.
         * We are going to register and set up the callbacks from the viewmodel.
         */
        mNewsViewModel = ViewModelProviders.of(MainActivity.this).get(NewsViewModel.class);
        mNewsViewModel.getListOfNews().observe(MainActivity.this, this);

        /**
         * The progress bar indicates if we are still fetching data. For its status, the main activity
         * should observe Live data of the progress bar.
         */
        mNewsViewModel.getProgressBarStatus().observe(MainActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isVisible) {
                progressBarStatusChange(isVisible);
            }
        });

        /**
         * Observe for change is internet connection. If disconnected, we would show to the user -
         * he is presently offline.
         */
        mNewsViewModel.getOfflineStatus().observe(MainActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isOffline) {
                if (isOffline) {
                    mFragmentMainBinding.offline.setVisibility(View.VISIBLE);
                } else {
                    mFragmentMainBinding.offline.setVisibility(View.GONE);
                }
            }
        });

        /**
         * set the viewmodel to the layout for data binding
         */
        mFragmentMainBinding.setNewsViewModel(mNewsViewModel);

        // remove previous read error messages as we are retrying fetch
        mFragmentMainBinding.ReadError.setVisibility(View.GONE);
        /**
         * Fetch the list of news from the server if internet is available, otherwise fetch it from
         * the cache (file storage in internal memory). Also note, in case of configuration change we
         * will fetch the data always from the cache so that the refresh can happen quickly (data
         * persistence across configuration changes).
         */
        // if configuration changed then fetch from cache. (server calls are expensive)
        mNewsViewModel.fetchNewsList(savedInstanceState != null);

    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        /**
         * Update the UI to show if we are still connected to the internet.
         */
        mNewsViewModel.isInternetAccessAvailable();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        /**
         * If the user is leaving the activity clean up the cache manager instance (newsDB object).
         * However, if a configuration change occurs we will not destroy the cache manager as right
         * after a configuration change we will fetch the list of news from the cache (instead of the
         * server) for quick UI update.
         */
        if (!isChangingConfigurations()) {
            Log.d(TAG, "Cleaning up NewsDB");
            // clean up if the user is leaving this activity but not on screen rotations
            mNewsViewModel.cleanUpNewsDB();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            Log.d(TAG, "Refresh icon clicked");

            // remove previous read error messages as we are retrying fetch
            mFragmentMainBinding.ReadError.setVisibility(View.GONE);
            /**
             * On refresh we are going to fetch the data again (either from the server or from the cache)
             */
            mNewsViewModel.fetchNewsList(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    /**
     * Called after a list of news is fetched from the server or the cache.
     *
     * @param newsEntities    The list of news
     */
    @Override
    public void onChanged(final ArrayList<NewsEntity> newsEntities) {
        Log.d(TAG, "onChanged");
        if(mNewsViewModel.isFetchInProgress()) {
            /**
             * While a fetch is still in progress we may get a call to onChange. This could happen from
             * previous undelivered callbacks to a live data object. We can ignore this as we are expecting
             * fresh data.
             */
            Log.d(TAG,"Ignoring onChanged call");
            return;
        }
        mFragmentMainBinding.ReadError.setVisibility(View.GONE);
        NewsListAdapter mNewsAdapter = new NewsListAdapter(MainActivity.this,
                R.layout.list_item_news, newsEntities);
        /**
         * Refresh the list view with the latest news.
         */
        mFragmentMainBinding.listview.setAdapter(mNewsAdapter);

        if (newsEntities.size() == 0) {
            Log.d(TAG,"Fetch failed");
            /**
             * If the fetch failed we show a read message error.
             */
            mFragmentMainBinding.listview.setVisibility(View.GONE);
            mFragmentMainBinding.ReadError.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG,"Fetch is successful");
            /**
             * If the fetch is successful we show the list of news and set an on click listener for
             * each news item on the list.
             */
            mFragmentMainBinding.ReadError.setVisibility(View.GONE);
            mFragmentMainBinding.listview.setVisibility(View.VISIBLE);
            mFragmentMainBinding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!mIsDualPaneLayout) {
                        /**
                         * If we are not supporting a dual pane layout, we will start the next activity
                         * for the details.
                         */
                        startNextActivity(newsEntities.get(position));
                    } else {
                        /**
                         * For dual pane layout, we will just update the details fragment with the information
                         * from the clicked news item.
                         */
                        updateDetailsFragment(newsEntities.get(position));
                    }

                }
            });
        }
        /**
         * Refresh the data binding for the UI changes to be visible.
         */
        mFragmentMainBinding.invalidateAll();
        mFragmentMainBinding.setNewsViewModel(mNewsViewModel);
    }

    /**
     * Show or remove progress bar from the UI
     * @param isVisible    if true we will show the progress bar, otherwise remove it.
     */
    private void progressBarStatusChange(Boolean isVisible) {
        Log.d(TAG, "progressBarStatusChange to " + isVisible);
        if (isVisible) {
            mFragmentMainBinding.progressBar.setVisibility(View.VISIBLE);
            mFragmentMainBinding.FetchDataText.setVisibility(View.VISIBLE);
            mFragmentMainBinding.listview.setVisibility(View.GONE);
        } else {
            mFragmentMainBinding.progressBar.setVisibility(View.GONE);
            mFragmentMainBinding.FetchDataText.setVisibility(View.GONE);
            mFragmentMainBinding.listview.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Start the DetailsActivity
     * @param newsEntity    Information about the news the user clicked.
     */
    private void startNextActivity(NewsEntity newsEntity) {
        Log.d(TAG, "startNextActivity");
        Intent intent = new Intent(this, DetailViewActivity.class);
        intent.putExtra("title", newsEntity.getTitle());
        intent.putExtra("summary", newsEntity.getSummary());
        intent.putExtra("storyURL", newsEntity.getUrl());
        List<MediaEntity> mediaList = newsEntity.getMediaEntityList();
        if (mediaList.size() > 0) {
            intent.putExtra("imageUrl", mediaList.get(mediaList.size() - 1).getUrl());
        }
        startActivity(intent);
    }

    /**
     * Update the Details fragment for a dual pane layout of a tablet.
     * @param newsEntity    Information about the news the user clicked.
     */
    private void updateDetailsFragment(NewsEntity newsEntity) {
        Log.d(TAG, "updateDetailsFragment");
        Bundle bundle = new Bundle();
        bundle.putString("title", newsEntity.getTitle());
        bundle.putString("summary", newsEntity.getSummary());
        bundle.putString("storyURL", newsEntity.getUrl());
        List<MediaEntity> mediaList = newsEntity.getMediaEntityList();
        if (mediaList.size() > 0) {
            bundle.putString("imageUrl", mediaList.get(mediaList.size() - 1).getUrl());
        }
        mDetail.updateUI(bundle);
    }

}
