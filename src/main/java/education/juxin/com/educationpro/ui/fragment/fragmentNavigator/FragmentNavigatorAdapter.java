package education.juxin.com.educationpro.ui.fragment.fragmentNavigator;

import android.support.v4.app.Fragment;

/**
 * Created on 16/3/30.
 */
public interface FragmentNavigatorAdapter {

    Fragment onCreateFragment(int position);

    String getTag(int position);

    int getCount();
}
