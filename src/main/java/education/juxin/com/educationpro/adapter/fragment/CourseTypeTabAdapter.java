package education.juxin.com.educationpro.adapter.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程类型适配器 fragment的展示 viewPager
 * Created by on 2016/12/1.
 */
public class CourseTypeTabAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mList;
    private ArrayList<String> mTabTitle;

    public CourseTypeTabAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        mTabTitle = new ArrayList<>();
        mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitle.get(position);
    }

    public void setTabTitle(ArrayList<String> mTabTitle) {
        this.mTabTitle = mTabTitle;
    }
}
