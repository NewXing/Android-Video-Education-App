package education.juxin.com.educationpro.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.http.NetworkHelper;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.ui.activity.dynamic.SpaceMainActivity;
import education.juxin.com.educationpro.util.FormatTimeUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.bean.DynamicBean;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseDetailActivity;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * tab 动态碎片页面
 * Created by Administrator on 2018/3/7.
 */
public class DynamicFragment extends BaseFragment {

    private View mView;
    private LinearLayout mNoDataLayout;
    private RelativeLayout noNetLayout;
    private SmartRefreshLayout refreshLayout;
    private BaseRecyclerAdapter dynamicAdapter;

    private ArrayList<DynamicBean.DynamicData> mBeanList;

    private int mPerItemNum;
    private int mCurrPageNum;
    private int mTotalItemNum;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tab_dynamic, null);
        initToolbar(mView, false, R.string.dynamic);

        mBeanList = new ArrayList<>();
        mPerItemNum = 10;
        mCurrPageNum = 1;
        mTotalItemNum = 0;

        initUI();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrPageNum = 1;
        reqData(IRefreshTag.IS_REFRESH_DATA);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mCurrPageNum = 1;
            reqData(IRefreshTag.IS_REFRESH_DATA);
        }
    }

    private void initUI() {
        mNoDataLayout = mView.findViewById(R.id.no_data_layout);
        noNetLayout = mView.findViewById(R.id.error_relative);
        noNetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrPageNum = 1;
                reqData(IRefreshTag.IS_REFRESH_DATA);
            }
        });

        refreshLayout = mView.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mCurrPageNum = 1;
                reqData(IRefreshTag.IS_REFRESH_DATA);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                reqData(IRefreshTag.IS_LOAD_MORE_DATA);
            }
        });

        RecyclerView rvList = mView.findViewById(R.id.xrv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.addItemDecoration(new RecycleItemDecoration(getActivity(), RecycleItemDecoration.VERTICAL, 40, R.color.default_view));
        dynamicAdapter = new BaseRecyclerAdapter<DynamicBean.DynamicData>(R.layout.item_dynamic, mBeanList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, DynamicBean.DynamicData itemData) {
                ImageView imgView = (ImageView) viewHolder.findViewById(R.id.img_view);
                TextView tvTeacherName = (TextView) viewHolder.findViewById(R.id.tv_dynamic_name);
                LinearLayout llCollect = (LinearLayout) viewHolder.findViewById(R.id.ll_collect);
                TextView courseTitleTv = (TextView) viewHolder.findViewById(R.id.course_title_tv);
                ImageView courseImg = (ImageView) viewHolder.findViewById(R.id.course_picture_img);
                ImageView isNewImg = (ImageView) viewHolder.findViewById(R.id.is_new_img);
                TextView teacherNameTv = (TextView) viewHolder.findViewById(R.id.teacher_name_tv);
                TextView courseTimeTv = (TextView) viewHolder.findViewById(R.id.course_time_tv);

                ImageUtils.GlideUtil(getContext(), itemData.getTeacherHeadImg(), imgView);
                tvTeacherName.setText(itemData.getTeacherName());
                courseTitleTv.setText(itemData.getCourseName());
                ImageUtils.GlideUtil(getContext(), itemData.getCourseCoverImgUrl(), courseImg);
                isNewImg.setVisibility(itemData.isNewCourse() ? View.VISIBLE : View.GONE);
                teacherNameTv.setText(String.format(getContext().getString(R.string.main_teacher_with_name), itemData.getCourseMainTeacherName()));
                courseTimeTv.setText(itemData.getCurrentLesson());

                llCollect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), SpaceMainActivity.class);
                        intent.putExtra("teacherId", itemData.getTeacherId());

                        getContext().startActivity(intent);
                    }
                });
            }
        };
        rvList.setAdapter(dynamicAdapter);
        dynamicAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int test = 0;
                CourseDetailActivity.checkCourseValid(getActivity(), mBeanList.get(position).getCourseId(), "", new ICheckCourseValid() {
                    @Override
                    public void isValid() {
                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                        intent.putExtra("id_course_detail", mBeanList.get(position).getCourseId());
                        startActivity(intent);
                    }

                    @Override
                    public void isInvalid() {
                    }
                });
            }
        });
        dynamicAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mBeanList.size() == 0) {
                    mNoDataLayout.setVisibility(View.VISIBLE);
                    refreshLayout.setVisibility(View.GONE);
                } else {
                    mNoDataLayout.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void reqData(final int tag) {
        OkHttpUtils.get()
                .url(HttpConstant.DYNAMIC_LIST)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<DynamicBean>(DynamicBean.class, false, getActivity()) {
                    @Override
                    public void onResponse(DynamicBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        mTotalItemNum = response.getTotal() != null ? Integer.valueOf(response.getTotal()) : 0;

                        switch (tag) {
                            case IRefreshTag.IS_REFRESH_DATA:
                                if (response.getData() != null) {
                                    mBeanList.clear();
                                    mBeanList.addAll(response.getData());
                                }
                                refreshLayout.finishRefresh();
                                mCurrPageNum = 2;
                                break;

                            case IRefreshTag.IS_LOAD_MORE_DATA:
                                if (mTotalItemNum > mCurrPageNum * mPerItemNum) {
                                    ++mCurrPageNum;
                                }
                                if (response.getData() != null && mTotalItemNum > mBeanList.size()) {
                                    mBeanList.addAll(response.getData());
                                }
                                refreshLayout.finishLoadMore();
                                break;
                        }

                        isNewCourse();

                        dynamicAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadMore();
                    }
                });
    }

    // 根据时间戳处理“New”标签的显示
    private void isNewCourse() {
        try {
            String spMaxTime = (String) SPHelper.getSimpleParam(getContext(), "dynamic_max_time", "");
            if (spMaxTime == null || "".equals(spMaxTime)) {
                spMaxTime = "0";
            }

            for (DynamicBean.DynamicData data : mBeanList) {
                String serviceTime = FormatTimeUtil.formatDateStr2TimeStamp(data.getCreatetime(), "yyyy-MM-dd HH:mm:ss");
                boolean test = Long.valueOf(serviceTime) > Long.valueOf(spMaxTime);
                data.setNewCourse(Long.valueOf(serviceTime) > Long.valueOf(spMaxTime));
            }

            if (mBeanList.size() > 0) {
                String serviceMaxTime = FormatTimeUtil.formatDateStr2TimeStamp(mBeanList.get(0).getCreatetime(), "yyyy-MM-dd HH:mm:ss");
                if (Long.valueOf(serviceMaxTime) > Long.valueOf(spMaxTime)) {
                    String time = FormatTimeUtil.formatDateStr2TimeStamp(mBeanList.get(0).getCreatetime(), "yyyy-MM-dd HH:mm:ss");
                    SPHelper.setSimpleKeyValue(getContext(), "dynamic_max_time", time);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);

        if (netMobile == NetworkHelper.NETWORK_NONE) {
            noNetLayout.setVisibility(View.VISIBLE);
            mNoDataLayout.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.GONE);
        } else {
            noNetLayout.setVisibility(View.GONE);
            mNoDataLayout.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }
}
