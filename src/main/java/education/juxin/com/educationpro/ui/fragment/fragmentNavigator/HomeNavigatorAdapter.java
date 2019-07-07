package education.juxin.com.educationpro.ui.fragment.fragmentNavigator;

import android.support.v4.app.Fragment;

import education.juxin.com.educationpro.ui.fragment.CourseFragment;
import education.juxin.com.educationpro.ui.fragment.DynamicFragment;
import education.juxin.com.educationpro.ui.fragment.HomePagerFragment;
import education.juxin.com.educationpro.ui.fragment.MimeFragment;

/**
 * 首页 fragment
 * Created on 2016/5/16.
 */
public class HomeNavigatorAdapter implements FragmentNavigatorAdapter {

    public HomeNavigatorAdapter() {
    }

    @Override
    public Fragment onCreateFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new HomePagerFragment();
                break;
            case 1:
                fragment = new DynamicFragment();
                break;
            case 2:
                fragment = new CourseFragment();
                break;
            case 3:
                fragment = new MimeFragment();
                break;

            default:
                fragment = new HomePagerFragment();
                break;
        }

        return fragment;
    }

    @Override
    public String getTag(int position) {
        String tag;
        switch (position) {
            case 0:
                tag = HomePagerFragment.class.getSimpleName();
                break;
            case 1:
                tag = DynamicFragment.class.getSimpleName();
                break;
            case 2:
                tag = CourseFragment.class.getSimpleName();
                break;
            case 3:
                tag = MimeFragment.class.getSimpleName();
                break;

            default:
                tag = HomePagerFragment.class.getSimpleName();
                break;
        }
        return tag;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
