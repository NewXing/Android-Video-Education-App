package education.juxin.com.educationpro.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseFragment;
import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.bean.BugCourseRecordBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseDetailActivity;
import education.juxin.com.educationpro.util.FormatTimeUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 已购课程页面
 * Created by Administrator on 2018/3/9.
 */

public class TabBuyFragment extends BaseFragment {

    private SmartRefreshLayout mRefreshLayout;
    private View mNoDataLayout;
    private ArrayList<BugCourseRecordBean.DataBean> mList = new ArrayList<>();

    private View mView;
    //每一页 多少条
    private int mPerItemNum;
    //当前页数
    private int mCurrPageNum;
    //当前条数
    private int mTotalItemNum;
    private RecyclerView recyclerView;
    private BaseRecyclerAdapter adapter;

    public static TabBuyFragment newInstance(String title) {
        TabBuyFragment fragment = new TabBuyFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab_bought, null);

        initView();
        mPerItemNum = 10;
        mCurrPageNum = 1;
        mTotalItemNum = 0;

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrPageNum = 1;
        getOrderList(IRefreshTag.IS_REFRESH_DATA);
    }

    private void initView() {
        mNoDataLayout = mView.findViewById(R.id.no_data_view);

        mRefreshLayout = mView.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mCurrPageNum = 1;
                getOrderList(IRefreshTag.IS_REFRESH_DATA);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getOrderList(IRefreshTag.IS_LOAD_MORE_DATA);
            }
        });

        recyclerView = mView.findViewById(R.id.bought_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new RecycleItemDecoration(getContext()));
        adapter = new BaseRecyclerAdapter<BugCourseRecordBean.DataBean>(R.layout.item_course_tab, mList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, BugCourseRecordBean.DataBean itemData) {
                ImageView coursePictureImg = (ImageView) viewHolder.findViewById(R.id.course_picture_img);
                TextView courseTitleTv = (TextView) viewHolder.findViewById(R.id.course_title_tv);
                TextView courseTeacherTv = (TextView) viewHolder.findViewById(R.id.course_teacher_tv);
                TextView courseTimeTv = (TextView) viewHolder.findViewById(R.id.course_time_tv);
                TextView validityTimeTv = (TextView) viewHolder.findViewById(R.id.validity_time_tv);
                TextView courseTypeTv = (TextView) viewHolder.findViewById(R.id.course_type_tv);

                ImageUtils.GlideUtil(getActivity(), itemData.getCoverImgUrl(), coursePictureImg);
                courseTitleTv.setText(itemData.getName());
                courseTeacherTv.setText(String.format(getContext().getString(R.string.main_teacher_with_name), itemData.getMainTeacherName()));
                courseTimeTv.setText(itemData.getClassNum());
                String endTime = itemData.getEndTime();
                String courseTimeStamp = FormatTimeUtil.formatDateStr2TimeStamp(endTime, "yyyy-MM-dd HH:mm:ss");
                String yearMounthTime = FormatTimeUtil.formatTimeStamp2DateStr(courseTimeStamp, "yyyy-MM-dd");
                validityTimeTv.setText(String.format(getContext().getString(R.string.end_time_with_param), yearMounthTime));
                courseTypeTv.setVisibility(View.GONE);
            }
        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mList.size() == 0) {
                    mNoDataLayout.setVisibility(View.VISIBLE);
                    mRefreshLayout.setVisibility(View.GONE);
                } else {
                    mNoDataLayout.setVisibility(View.GONE);
                    mRefreshLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int test = 0;
//                CourseDetailActivity.checkCourseValid(getActivity(), mList.get(position).getId(), "", new ICheckCourseValid() {
//                    @Override
//                    public void isValid() {
                Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                intent.putExtra("id_course_detail", mList.get(position).getId());
                startActivity(intent);
//                    }
//
//                    @Override
//                    public void isInvalid() {
//                    }
//                });
            }
        });
        recyclerView.setAdapter(adapter);
    }

    //获取用户购买的课程列表
    private void getOrderList(final int tag) {
        OkHttpUtils.get()
                .url(HttpConstant.BUY_COURSE_LIST)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<BugCourseRecordBean>(BugCourseRecordBean.class, false, getContext()) {
                    @Override
                    public void onResponse(BugCourseRecordBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                        mTotalItemNum = response.getTotal() != null ? Integer.valueOf(response.getTotal()) : 0;

                        switch (tag) {
                            case IRefreshTag.IS_REFRESH_DATA:
                                if (response.getData() != null) {
                                    mList.clear();
                                    mList.addAll(response.getData());
                                }
                                mRefreshLayout.finishRefresh();
                                mCurrPageNum = 2;
                                break;
                            case IRefreshTag.IS_LOAD_MORE_DATA:
                                if (mTotalItemNum > mCurrPageNum * mPerItemNum) {
                                    ++mCurrPageNum;
                                }
                                if (response.getData() != null && mTotalItemNum > mList.size()) {
                                    mList.addAll(response.getData());
                                }
                                mRefreshLayout.finishLoadMore();
                                break;
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        mRefreshLayout.finishRefresh();
                        mRefreshLayout.finishLoadMore();
                    }
                });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        mCurrPageNum = 1;
        getOrderList(IRefreshTag.IS_REFRESH_DATA);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        mCurrPageNum = 1;
        getOrderList(IRefreshTag.IS_REFRESH_DATA);
    }
}
