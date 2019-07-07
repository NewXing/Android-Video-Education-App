package education.juxin.com.educationpro.ui.fragment;

import android.content.Intent;
import android.graphics.Paint;
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
import education.juxin.com.educationpro.bean.QualityCourseBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseDetailActivity;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

public class ClassifyCourseFragment extends BaseFragment {

    private int mPerItemNum;
    private int mCurrPageNum;
    private int mTotalItemNum;

    private View rootView;
    private SmartRefreshLayout smartRefreshLayout;
    private BaseRecyclerAdapter adapter;
    private ArrayList<QualityCourseBean.DataBean> mQualityCourseList = new ArrayList<>();

    public static ClassifyCourseFragment newInstance(String title, String id) {
        ClassifyCourseFragment fragment = new ClassifyCourseFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_course_type, container, false);

        mCurrPageNum = 1;
        mPerItemNum = 10;
        mTotalItemNum = 0;

        initUI();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        String id = getArguments().getString("id");
        getCourseData(IRefreshTag.IS_LOAD_MORE_DATA, id);
    }

    private void initUI() {
        smartRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                String id = getArguments().getString("id");
                getCourseData(IRefreshTag.IS_LOAD_MORE_DATA, id);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                String id = getArguments().getString("id");
                getCourseData(IRefreshTag.IS_REFRESH_DATA, id);
            }
        });

        RecyclerView qualityCourseRView = rootView.findViewById(R.id.course_type_recyclerView);
        qualityCourseRView.setLayoutManager(new LinearLayoutManager(getContext()));
        qualityCourseRView.addItemDecoration(new RecycleItemDecoration(getContext()));
        adapter = new BaseRecyclerAdapter<QualityCourseBean.DataBean>(R.layout.item_quality_course, mQualityCourseList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, QualityCourseBean.DataBean itemData) {
                ImageView coursePictureImg = (ImageView) viewHolder.findViewById(R.id.course_picture_img);
                TextView courseTitleTv = (TextView) viewHolder.findViewById(R.id.course_title_tv);
                TextView courseTeacherTv = (TextView) viewHolder.findViewById(R.id.course_teacher_tv);
                TextView courseTimeTv = (TextView) viewHolder.findViewById(R.id.course_time_tv);
                TextView oldPriceTv = (TextView) viewHolder.findViewById(R.id.old_price_tv);
                TextView newPriceTv = (TextView) viewHolder.findViewById(R.id.new_price_tv);

                ImageUtils.GlideUtil(getActivity(), itemData.getCoverImgUrl(), coursePictureImg);
                courseTitleTv.setText(itemData.getName());
                courseTeacherTv.setText(String.format(getContext().getString(R.string.main_teacher_with_name), itemData.getMainTeacherName()));
                courseTimeTv.setText(String.format(getContext().getString(R.string.course_time_with_num), itemData.getClassNum()));
                oldPriceTv.setText(FormatNumberUtil.doubleFormat(itemData.getOriginalPrice()));
                oldPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                newPriceTv.setText(String.format(getContext().getString(R.string.money_only_with_number), FormatNumberUtil.doubleFormat(itemData.getCurrentPrice())));
            }
        };
        qualityCourseRView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int test = 0;
                CourseDetailActivity.checkCourseValid(getActivity(), mQualityCourseList.get(position).getId(), "", new ICheckCourseValid() {
                    @Override
                    public void isValid() {
                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                        intent.putExtra("id_course_detail", mQualityCourseList.get(position).getId());
                        startActivity(intent);
                    }

                    @Override
                    public void isInvalid() {
                    }
                });
            }
        });
    }

    private void getCourseData(final int tag, String id) {
        OkHttpUtils.get()
                .url(HttpConstant.GET_COURSE_LIST + id)
                .addParams("id", id)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<QualityCourseBean>(QualityCourseBean.class, false, getActivity()) {
                    @Override
                    public void onResponse(QualityCourseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        mTotalItemNum = response.getTotal() != null ? Integer.parseInt(response.getTotal()) : 0;

                        switch (tag) {
                            case IRefreshTag.IS_REFRESH_DATA:
                                if (response.getData() != null) {
                                    mQualityCourseList.clear();
                                    mQualityCourseList.addAll(response.getData());
                                }
                                smartRefreshLayout.finishRefresh();
                                mCurrPageNum = 2;
                                break;

                            case IRefreshTag.IS_LOAD_MORE_DATA:
                                if (mTotalItemNum > mCurrPageNum * mPerItemNum) {
                                    ++mCurrPageNum;
                                }

                                if (response.getData() != null && mTotalItemNum > mQualityCourseList.size()) {
                                    mQualityCourseList.addAll(response.getData());
                                }
                                smartRefreshLayout.finishLoadMore();
                                break;
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        smartRefreshLayout.finishRefresh();
                        smartRefreshLayout.finishLoadMore();
                    }
                });
    }
}
