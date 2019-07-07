package education.juxin.com.educationpro.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.adapter.fragment.MainTabAdapter;
import education.juxin.com.educationpro.base.BaseFragment;
import education.juxin.com.educationpro.bean.CourseLookRecordBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.http.NetworkHelper;
import education.juxin.com.educationpro.interfaces.IRefreshUI;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * tab 课程碎片页面
 * Created by Administrator on 2018/3/7.
 */

public class CourseFragment extends BaseFragment {

    private View mView;
    private RelativeLayout titleBarLayout;
    private ViewPager vpContent;
    private RelativeLayout noNetLayout;

    private int mPerItemNum;
    private int mCurrPageNum;
    private int mTotalItemNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab_course, container, false);
        initToolbar(mView, false, R.string.course);
        initUI();

        mPerItemNum = 10;
        mCurrPageNum = 1;
        mTotalItemNum = 0;

        return mView;
    }

    private void initUI() {
        TabLayout tab_title = mView.findViewById(R.id.tab_title);
        vpContent = mView.findViewById(R.id.content_viewpager);
        List<Fragment> fragmentList = new ArrayList<>();

        TabRecentStudyFragment tabStudy = TabRecentStudyFragment.newInstance("Equipment");
        TabBuyFragment tabBug = TabBuyFragment.newInstance("Science");
        TabMyCacheFragment tabCache = TabMyCacheFragment.newInstance("Game");

        fragmentList.add(tabStudy);
        fragmentList.add(tabBug);
        fragmentList.add(tabCache);

        MainTabAdapter adapter = new MainTabAdapter(getActivity().getSupportFragmentManager(), fragmentList);
        vpContent.setAdapter(adapter);
        vpContent.setOffscreenPageLimit(3);
        tab_title.setupWithViewPager(vpContent);

        titleBarLayout = mView.findViewById(R.id.title_bar_layout);
        noNetLayout = mView.findViewById(R.id.error_relative);
        noNetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqRecentData(IRefreshTag.IS_LOAD_MORE_DATA);
            }
        });
    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);

        if (netMobile == NetworkHelper.NETWORK_NONE) {
            noNetLayout.setVisibility(View.VISIBLE);
            titleBarLayout.setVisibility(View.GONE);
            vpContent.setVisibility(View.GONE);
        } else {
            noNetLayout.setVisibility(View.GONE);
            titleBarLayout.setVisibility(View.VISIBLE);
            vpContent.setVisibility(View.VISIBLE);
        }
    }

    private void reqRecentData(final int tag) {
        OkHttpUtils.get()
                .url(HttpConstant.LOOK_COURSE_RECORD)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<CourseLookRecordBean>(CourseLookRecordBean.class, false, getContext()) {
                    @Override
                    public void onResponse(CourseLookRecordBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        noNetLayout.setVisibility(View.GONE);
                        titleBarLayout.setVisibility(View.VISIBLE);
                        vpContent.setVisibility(View.VISIBLE);
                    }
                });
    }
}
