package news.agoda.com.sample.view;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import news.agoda.com.sample.R;
import news.agoda.com.sample.databinding.FragmentMainBinding;

/**
 * The main fragment shows the latest news list.
 */
public class FragmentMain extends Fragment {

    private FragmentMainBinding mFragmentMainBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Initialize data binding and inflate the fragment.
         */
        mFragmentMainBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_main, container, false);

        return mFragmentMainBinding.getRoot();
    }

    /**
     * Obtain the data binding instance. This is needed by the main activity to update the UI.
     * @return    The main fragment binding object.
     */
    public FragmentMainBinding getFragmentMainBinding() {
        return mFragmentMainBinding;
    }
}
