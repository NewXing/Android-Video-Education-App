package education.juxin.com.educationpro.ui.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.bean.WithdrawListBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 提现记录页面
 * The type Ti xian activity.
 */
public class WithdrawActivity extends BaseActivity {

    private SmartRefreshLayout mRefreshLayout;
    private View mNoDataLayout;

    private int mPerItemNum; // 每一页多少条
    private int mCurrPageNum; // 当前页数
    private int mTotalItemNum; // 当前条数

    private ArrayList<WithdrawListBean.WithdrawData> mSearchList;
    private BaseRecyclerAdapter<WithdrawListBean.WithdrawData> withDrawAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        initToolbar(true, R.string.withdraw_notes);

        mCurrPageNum = 1;
        mTotalItemNum = 0;
        mPerItemNum = 10;
        mSearchList = new ArrayList<>();

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        WithDrawData(IRefreshTag.IS_LOAD_MORE_DATA);
    }

    private void initUI() {
        //在没有数据时显示的界面
        mNoDataLayout = findViewById(R.id.no_data_view);
        //存在数据时显示的界面
        mRefreshLayout = findViewById(R.id.refresh_layout);
        //在上拉加载 下拉刷新时的操作
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                WithDrawData(IRefreshTag.IS_REFRESH_DATA);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                WithDrawData(IRefreshTag.IS_LOAD_MORE_DATA);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecycleItemDecoration(this));
        withDrawAdapter = new BaseRecyclerAdapter<WithdrawListBean.WithdrawData>(R.layout.item_ti_xian, mSearchList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, WithdrawListBean.WithdrawData itemData) {
                TextView priceTv = (TextView) viewHolder.findViewById(R.id.tv_ti_price);
                TextView timeTv = (TextView) viewHolder.findViewById(R.id.ti_time);
                TextView withdrawToTv = (TextView) viewHolder.findViewById(R.id.tv_ti_where);

                priceTv.setText(String.format(getString(R.string.number_yuan_minus), FormatNumberUtil.doubleFormat(itemData.getPrice())));
                timeTv.setText(itemData.getCreateTime());
                if ("1".equals(itemData.getType())) {
                    withdrawToTv.setText("提现至：支付宝");
                } else if ("2".equals(itemData.getType())) {
                    withdrawToTv.setText("提现至：微信");
                }
            }
        };
        recyclerView.setAdapter(withDrawAdapter);
        withDrawAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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

    // 提现记录列表 数据接口
    private void WithDrawData(final int tag) {
        OkHttpUtils.post()
                .url(HttpConstant.WITHDRAW_LIST)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<WithdrawListBean>(WithdrawListBean.class, false, WithdrawActivity.this) {
                    @Override
                    public void onResponse(WithdrawListBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        mTotalItemNum = response.getTotal() != null ? Integer.parseInt(response.getTotal()) : 0;

                        switch (tag) {
                            case IRefreshTag.IS_REFRESH_DATA:
                                if (response.getData() != null && response.getData().size() != 0) {
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
                        withDrawAdapter.notifyDataSetChanged();
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
