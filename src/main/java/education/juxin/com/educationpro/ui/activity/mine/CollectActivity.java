package education.juxin.com.educationpro.ui.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.bean.IsCourseCollectBean;
import education.juxin.com.educationpro.dialog.ComTwnBtnDialog;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseDetailActivity;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.bean.CollectBean;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 我的收藏页面
 * The type Collect activity.
 */
public class CollectActivity extends BaseActivity {

    private List<CollectBean.DataBean> mSearchList;
    private BaseRecyclerAdapter collectAdapter;

    private View mNoDataLayout;
    private SmartRefreshLayout mRefreshLayout;

    private int mPerItemNum;
    private int mCurrPageNum;
    private int mTotalItemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        initToolbar(true, R.string.my_collect);

        mSearchList = new ArrayList<>();

        mPerItemNum = 10;
        mCurrPageNum = 1;
        mTotalItemNum = 0;

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        reqCollectData(IRefreshTag.IS_LOAD_MORE_DATA);
    }

    private void initUI() {
        mNoDataLayout = findViewById(R.id.no_data_view);
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                reqCollectData(IRefreshTag.IS_LOAD_MORE_DATA);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                reqCollectData(IRefreshTag.IS_REFRESH_DATA);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecycleItemDecoration(this));
        collectAdapter = new BaseRecyclerAdapter<CollectBean.DataBean>(R.layout.item_collect, mSearchList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, CollectBean.DataBean itemData) {
                ImageView collectBookImg = (ImageView) viewHolder.findViewById(R.id.collect_book_img);
                TextView collectBookName = (TextView) viewHolder.findViewById(R.id.collect_book_name);
                TextView collectTeacher = (TextView) viewHolder.findViewById(R.id.collect_teacher);
                TextView timeTv = (TextView) viewHolder.findViewById(R.id.tv_time_long);
                TextView deleteCollectTv = (TextView) viewHolder.findViewById(R.id.delete_collect_tv);

                ImageUtils.GlideUtil(CollectActivity.this, itemData.getCoverImgUrl(), collectBookImg);
                collectBookName.setText(itemData.getCourseName());
                collectTeacher.setText(String.format(getString(R.string.main_teacher_with_name), itemData.getMainTeacherName()));
                timeTv.setText(itemData.getClassNum());
                deleteCollectTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ComTwnBtnDialog collectDialog = new ComTwnBtnDialog(CollectActivity.this, ComTwnBtnDialog.DIALOG_COLLECT_TYPE);
                        collectDialog.setDialogBtnClickListener(new ComTwnBtnDialog.IDialogBtnClickListener() {
                            @Override
                            public void onDialogLeftBtnClick() {
                            }

                            @Override
                            public void onDialogRightBtnClick() {
                                reqCancelCollectData(Integer.valueOf(itemData.getCourseId()), position);
                            }
                        });
                        collectDialog.show();
                    }
                });
            }

            // 取消收藏接口
            private void reqCancelCollectData(int courseId, int position) {
                OkHttpUtils.post()
                        .url(HttpConstant.COURSE_CANCEL_COLLECT)
                        .addParams("courseId", String.valueOf(courseId))
                        .build()
                        .execute(new HttpCallBack<IsCourseCollectBean>(IsCourseCollectBean.class, true, CollectActivity.this) {
                            @Override
                            public void onResponse(IsCourseCollectBean response, int id) {
                                if (response.getCode() != 0) {
                                    ToastManager.showShortToast(response.getMsg());
                                    return;
                                }

                                ToastManager.showShortToast("已取消收藏！");

                                mSearchList.remove(position);
                                notifyDataSetChanged();
                            }
                        });
            }
        };
        collectAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                if (mSearchList.size() == 0) {
                    mRefreshLayout.setVisibility(View.GONE);
                    mNoDataLayout.setVisibility(View.VISIBLE);
                } else {
                    mRefreshLayout.setVisibility(View.VISIBLE);
                    mNoDataLayout.setVisibility(View.GONE);
                }
            }
        });
        recyclerView.setAdapter(collectAdapter);
        collectAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int test = 0;
                CourseDetailActivity.checkCourseValid(CollectActivity.this, mSearchList.get(position).getCourseId(), "",
                        new ICheckCourseValid() {
                            @Override
                            public void isValid() {
                                Intent intent = new Intent(CollectActivity.this, CourseDetailActivity.class);
                                intent.putExtra("id_course_detail", mSearchList.get(position).getCourseId());
                                startActivity(intent);
                            }

                            @Override
                            public void isInvalid() {
                            }
                        });
            }
        });
    }

    // 获取收藏列表数据
    private void reqCollectData(final int tag) {
        OkHttpUtils.get()
                .url(HttpConstant.COURSE_COLLECT_LIST)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<CollectBean>(CollectBean.class, false, this) {
                    @Override
                    public void onResponse(CollectBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        mTotalItemNum = response.getTotal() != null ? Integer.valueOf(response.getTotal()) : 0;

                        switch (tag) {
                            case IRefreshTag.IS_REFRESH_DATA:
                                if (response.getData() != null) {
                                    mSearchList.clear();
                                    mSearchList.addAll(response.getData());
                                }
                                mRefreshLayout.finishRefresh();
                                mCurrPageNum = 2;
                                break;

                            case IRefreshTag.IS_LOAD_MORE_DATA:
                                if (mTotalItemNum > mCurrPageNum * mPerItemNum) {
                                    ++mCurrPageNum;
                                }
                                if (response.getData() != null && mTotalItemNum > mSearchList.size()) {
                                    mSearchList.addAll(response.getData());
                                }
                                mRefreshLayout.finishLoadMore();
                                break;
                        }
                        collectAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        mRefreshLayout.finishRefresh();
                        mRefreshLayout.finishLoadMore();
                    }
                });
    }

}
