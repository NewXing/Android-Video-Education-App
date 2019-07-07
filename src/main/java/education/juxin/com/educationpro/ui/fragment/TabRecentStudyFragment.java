package education.juxin.com.educationpro.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.adapter.recyclerview.TabCourseAdapter;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.base.BaseFragment;
import education.juxin.com.educationpro.bean.CourseLookRecordBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseDetailActivity;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 我的学习页面
 * Created by Administrator on 2018/3/9.
 */
public class TabRecentStudyFragment extends BaseFragment implements View.OnClickListener {

    private SmartRefreshLayout mRefreshLayout;
    private ArrayList<CourseLookRecordBean.CourseLookRecordData> mList = new ArrayList<>();

    private View mView;
    private View mNoDataLayout;
    private LinearLayout mDeleteLayout;
    private TabCourseAdapter mAdapter;

    private int mPerItemNum;
    private int mCurrPageNum;
    private int mTotalItemNum;

    public static TabRecentStudyFragment newInstance(String title) {
        TabRecentStudyFragment fragment = new TabRecentStudyFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab_recent_study, null);

        mPerItemNum = 10;
        mCurrPageNum = 1;
        mTotalItemNum = 0;

        initView();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrPageNum = 1;
        reqRecentData(IRefreshTag.IS_REFRESH_DATA);
    }

    private void initView() {
        mDeleteLayout = mView.findViewById(R.id.delete_selected_data_layout);

        mDeleteLayout.setClickable(true);
        mDeleteLayout.setOnClickListener(this);

        mNoDataLayout = mView.findViewById(R.id.no_data_view);

        mRefreshLayout = mView.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mCurrPageNum = 1;
                reqRecentData(IRefreshTag.IS_REFRESH_DATA);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                reqRecentData(IRefreshTag.IS_LOAD_MORE_DATA);
            }
        });

        RecyclerView recyclerView = mView.findViewById(R.id.recent_study_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new RecycleItemDecoration(getContext()));
        mAdapter = new TabCourseAdapter(getContext(), mList);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                if (mList.size() == 0) {
                    mNoDataLayout.setVisibility(View.VISIBLE);
                    mRefreshLayout.setVisibility(View.GONE);
                    mDeleteLayout.setVisibility(View.GONE);
                } else {
                    mNoDataLayout.setVisibility(View.GONE);
                    mRefreshLayout.setVisibility(View.VISIBLE);
                    mDeleteLayout.setVisibility(View.VISIBLE);
                }

            }
        });
        mAdapter.setOnItemClickListener(new TabCourseAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int test = 0;
                CourseDetailActivity.checkCourseValid(getActivity(), mList.get(position).getCourseId(), mList.get(position).getLessonId(),
                        new ICheckCourseValid() {
                            @Override
                            public void isValid() {
                                Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                intent.putExtra("id_course_detail", mList.get(position).getCourseId());
                                intent.putExtra("lesson_id", mList.get(position).getLessonId());
                                startActivity(intent);
                            }

                            @Override
                            public void isInvalid() {
                            }
                        });
            }

            @Override
            public void onLongClick(int position) {
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_selected_data_layout:
                clearLookRecord();
                break;

            default:
                break;
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

                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        mRefreshLayout.finishRefresh();
                        mRefreshLayout.finishLoadMore();
                    }
                });
    }

    //清除观看记录
    private void clearLookRecord() {
        OkHttpUtils.get()
                .url(HttpConstant.CLEAR_LOOK_COURSE_RECORD)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, getActivity()) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        ToastManager.showShortToast("清除成功！");

                        mList.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }
}
