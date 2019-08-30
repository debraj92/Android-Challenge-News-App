package news.agoda.com.sample;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import news.agoda.com.sample.databinding.ListItemNewsBinding;
import news.agoda.com.sample.viewmodel.MediaEntity;
import news.agoda.com.sample.viewmodel.NewsEntity;

/**
 * The list adapter which will be used to show the list of news on the UI. Note, it will use data binding
 * for loading data in the layout.
 */
public class NewsListAdapter extends ArrayAdapter {

    private static final String TAG = AppConstants.APP_TAG +"."+NewsListAdapter.class.getSimpleName();

    public NewsListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsEntity newsEntity = (NewsEntity) getItem(position);
        List<MediaEntity> mediaEntityList = newsEntity.getMediaEntityList();

        /**
         * Get the thumbnail URL if multimedia list is available.
         */
        String thumbnailURL = "";
        MediaEntity mediaEntity;
        if(mediaEntityList.size() != 0) {
            mediaEntity = mediaEntityList.get(0);
            thumbnailURL = mediaEntity.getUrl();
        }

        // The data binding instance for the list_item_news layout.
        ListItemNewsBinding listBinding;
        /**
         * View Holder pattern and data binding
         */
        if(convertView == null) {
            // inflate the view with data binding
            Log.d(TAG,"Inflating news view");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_news, null);
            listBinding = DataBindingUtil.bind(convertView);
            convertView.setTag(listBinding);
        } else {
            // reuse existing views.
            Log.d(TAG,"Reusing existing views");
            listBinding = (ListItemNewsBinding) convertView.getTag();
        }

        /**
         * Set the thumbnail.
         */
        DraweeController draweeController = Fresco.newDraweeControllerBuilder().setImageRequest(ImageRequest.fromUri
                (Uri.parse(thumbnailURL))).setOldController(listBinding.newsItemImage.getController()).build();
        listBinding.setNewsEntity(newsEntity);
        listBinding.newsItemImage.setController(draweeController);

        return listBinding.getRoot();
    }
}
