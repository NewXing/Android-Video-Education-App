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

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.dialog.ComTwnBtnDialog;
import education.juxin.com.educationpro.ui.activity.dynamic.SpaceMainActivity;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.bean.UserCateListBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 我的关注页面
 * The type Concern activity.
 */
public class ConcernActivity extends BaseActivity {

    private SmartRefreshLayout mRefreshLayout;
    private View mNoDataLayout;

    private ArrayList<UserCateListBean.CateData> mSearchList;

    private int mPerItemNum;
    private int mCurrPageNum;
    private int mTotalItemNum;
    private BaseRecyclerAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concern);
        initToolbar(true, R.string.my_care);

        mSearchList = new ArrayList<>();

        mPerItemNum = 10;
        mCurrPageNum = 1;
        mTotalItemNum = 0;

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        reqCareData(IRefreshTag.IS_LOAD_MORE_DATA);
    }

    private void initUI() {
        mNoDataLayout = findViewById(R.id.no_data_view);

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                reqCareData(IRefreshTag.IS_REFRESH_DATA);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                reqCareData(IRefreshTag.IS_LOAD_MORE_DATA);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecycleItemDecoration(this));
        adapter = new BaseRecyclerAdapter<UserCateListBean.CateData>(R.layout.item_concern, mSearchList) {

            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, UserCateListBean.CateData itemData) {
                ImageView iconImage = (ImageView) viewHolder.findViewById(R.id.img_name);
                TextView courseName = (TextView) viewHolder.findViewById(R.id.tv_course_name);
                TextView isConcern = (TextView) viewHolder.findViewById(R.id.concern_check_box);
                ImageUtils.GlideUtil(ConcernActivity.this, itemData.getHeadImgUrl(), iconImage);
                courseName.setText(itemData.getName());
                isConcern.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ComTwnBtnDialog careUserDialog = new ComTwnBtnDialog(ConcernActivity.this, ComTwnBtnDialog.DIALOG_CARE);
                        careUserDialog.setDialogBtnClickListener(new ComTwnBtnDialog.IDialogBtnClickListener() {
                            @Override
                            public void onDialogLeftBtnClick() {
                            }

                            @Override
                            public void onDialogRightBtnClick() {
                                reqCancelCareData(Integer.valueOf(itemData.getTeacherId()), position);
                            }

                        });
                        careUserDialog.show();
                    }
                });
            }

            //取消关注接口信息
            private void reqCancelCareData(int teacherId, int position) {
                OkHttpUtils.post()
                        .url(HttpConstant.USER_CANCEL_CATE)
                        .addParams("teacherId", String.valueOf(teacherId))
                        .build()
                        .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, ConcernActivity.this) {
                            @Override
                            public void onResponse(BaseBean response, int id) {
                                if (response.getCode() != 0) {
                                    ToastManager.showShortToast(response.getMsg());
                                    return;
                                }

                                ToastManager.showShortToast("已取消关注！");

                                mSearchList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        });
            }
        };
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ConcernActivity.this, SpaceMainActivity.class);
                intent.putExtra("teacherId", mSearchList.get(position).getTeacherId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
    }

    // 获取关注信息列表
    private void reqCareData(final int tag) {
        OkHttpUtils.get()
                .url(HttpConstant.CATE_LIST)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<UserCateListBean>(UserCateListBean.class, false, this) {
                    @Override
                    public void onResponse(UserCateListBean response, int id) {
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


}
