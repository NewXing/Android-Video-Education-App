package education.juxin.com.educationpro.ui.activity.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.adapter.recyclerview.TabAllCourseAdapter;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.bean.SpaceCourseBean;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 空间详情页面
 * The type Space detail activity.
 * create time 2018 -3-12
 */
public class SpaceAllCourseActivity extends BaseActivity {

    private TabAllCourseAdapter tabCourseAdapter;
    private ArrayList<SpaceCourseBean.SpaceCourseData> beanList;
    private SmartRefreshLayout refreshLayout;

    private String teacherId = "";
    private String classificationId = "";
    private int mPerItemNum;
    private int mCurrPageNum;
    private int mTotalItemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_detail);
        initToolbar(true, R.string.course_or_course_list);

        teacherId = getIntent().getStringExtra("teacher_id");
        classificationId = getIntent().getStringExtra("classification_id");

        beanList = new ArrayList<>();

        mPerItemNum = 10;
        mCurrPageNum = 1;
        mTotalItemNum = 0;

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        reqAllCourseList(IRefreshTag.IS_LOAD_MORE_DATA);
    }

    private void initUI() {
        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                reqAllCourseList(IRefreshTag.IS_REFRESH_DATA);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                reqAllCourseList(IRefreshTag.IS_LOAD_MORE_DATA);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecycleItemDecoration(this));
        tabCourseAdapter = new TabAllCourseAdapter(this, beanList);
        recyclerView.setAdapter(tabCourseAdapter);
        tabCourseAdapter.setOnItemClickListener(new TabAllCourseAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int test = 0;
                CourseDetailActivity.checkCourseValid(SpaceAllCourseActivity.this, beanList.get(position).getId(), "",
                        new ICheckCourseValid() {
                            @Override
                            public void isValid() {
                                Intent intent = new Intent(SpaceAllCourseActivity.this, CourseDetailActivity.class);
                                intent.putExtra("id_course_detail", beanList.get(position).getId());
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
    }

    private void reqAllCourseList(final int tag) {
        OkHttpUtils.get()
                .url(HttpConstant.ALL_COURSE_LIST)
                .addParams("teacherId", teacherId)
                .addParams("classificationId", classificationId)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<SpaceCourseBean>(SpaceCourseBean.class, false, this) {
                    @Override
                    public void onResponse(SpaceCourseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        mTotalItemNum = response.getTotal() != null ? Integer.valueOf(response.getTotal()) : 0;

                        switch (tag) {
                            case IRefreshTag.IS_REFRESH_DATA:
                                if (response.getData() != null) {
                                    beanList.clear();
                                    beanList.addAll(response.getData());
                                }
                                refreshLayout.finishRefresh();
                                mCurrPageNum = 2;
                                break;

                            case IRefreshTag.IS_LOAD_MORE_DATA:
                                if (mTotalItemNum > mCurrPageNum * mPerItemNum) {
                                    ++mCurrPageNum;
                                }
                                if (response.getData() != null && mTotalItemNum > beanList.size()) {
                                    beanList.addAll(response.getData());
                                }
                                refreshLayout.finishLoadMore();
                                break;
                        }

                        tabCourseAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadMore();
                    }
                });
    }

}
