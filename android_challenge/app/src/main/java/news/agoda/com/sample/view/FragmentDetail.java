package news.agoda.com.sample.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;

import news.agoda.com.sample.AppConstants;
import news.agoda.com.sample.R;
import news.agoda.com.sample.databinding.FragmentDetailBinding;

/**
 * The details fragment shows more information and a short summary of the news the user clicked.
 */
public class FragmentDetail extends Fragment {

    private static final String TAG = AppConstants.APP_TAG + "." + FragmentDetail.class.getSimpleName();

    // title of the news.
    public String mTitle;

    // abstract of the news.
    public String mSummary;

    // url of the full story.
    private String mStoryURL = "";

    /**
     * Data binding instance of the detail fragment
     */
    private FragmentDetailBinding mFragmentDetailBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        /**
         * Initialize data binding and inflate the fragment.
         */
        mFragmentDetailBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_detail, container, false);
        /**
         * On click of the Full Story button the user will launch the web browser.
         */
        mFragmentDetailBinding.fullStoryLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFullStoryClicked();
            }
        });
        return mFragmentDetailBinding.getRoot();
    }

    /**
     * Show the details of the news on the UI.
     *
     * @param extras      Contains the information to be populated on the UI.
     */
    public void updateUI(Bundle extras) {
        Log.d(TAG, "updateUI");
        mStoryURL = extras.getString("storyURL");
        mTitle = extras.getString("title");
        mSummary = extras.getString("summary");

        mFragmentDetailBinding.setDetailView(this);

        String imageURL = extras.getString("imageUrl");
        if(imageURL == null) {
            /**
             * If URL is unavailable use an empty string. This will ensure that the DraweeController
             * displays the place holder image for this case.
             */
            imageURL = "";
        }
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(ImageRequest.fromUri(Uri.parse(imageURL)))
                .setOldController(mFragmentDetailBinding.newsImage.getController()).build();

        mFragmentDetailBinding.newsImage.setController(draweeController);

        makeAllViewsVisible();
    }

    /**
     * Show all views on the details fragment. This occurs when an user selects a news item.
     */
    private void makeAllViewsVisible() {
        Log.d(TAG, "makeAllViewsVisible");
        mFragmentDetailBinding.fullStoryLink.setVisibility(View.VISIBLE);
        mFragmentDetailBinding.newsImage.setVisibility(View.VISIBLE);
        mFragmentDetailBinding.summaryContent.setVisibility(View.VISIBLE);
        mFragmentDetailBinding.title.setVisibility(View.VISIBLE);
    }

    /**
     * Open the web browser.
     */
    public void onFullStoryClicked() {
        Log.d(TAG, "onFullStoryClicked");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mStoryURL));
        startActivity(intent);
    }
}
