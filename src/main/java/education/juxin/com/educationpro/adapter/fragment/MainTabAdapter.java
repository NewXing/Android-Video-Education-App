package education.juxin.com.educationpro.adapter.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 课程模块的适配器 为Fragment展示 viewPager
 * Created on 2016/12/1.
 */
public class MainTabAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mList;
    private String mTabTitle[] = new String[]{"最近学习", "已购课程", "我的缓存"};

    public MainTabAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
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
        return mTabTitle[position];
    }

}
