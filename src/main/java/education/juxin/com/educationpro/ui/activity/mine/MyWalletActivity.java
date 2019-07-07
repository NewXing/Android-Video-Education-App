package education.juxin.com.educationpro.ui.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.bean.CashRecordBean;
import education.juxin.com.educationpro.bean.UserBalanceBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 我的钱包页面
 * The type My wallet activity.
 */
public class MyWalletActivity extends BaseActivity implements View.OnClickListener {

    private TextView userBalanceTv;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;

    private List<CashRecordBean.DataBean> mList = new ArrayList<>();
    private BaseRecyclerAdapter myWalletAdapter;

    private int mPerItemNum; //每一页多少条
    private int mCurrPageNum; //当前页数
    private int mTotalItemNum; //当前条数

    private boolean isCashRecordDataUpdated;
    private boolean isUserBalanceUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        mPerItemNum = 10;
        mCurrPageNum = 1;
        mTotalItemNum = 0;

        initView();
    }

    @Override
    public void onResume() {
        super.onResume();

        getCashRecordData(IRefreshTag.IS_LOAD_MORE_DATA);
        reqUserBalance();
    }

    private void initView() {
        TextView titleTv = findViewById(R.id.toolbar_title);
        titleTv.setText(R.string.my_wallet);
        Toolbar toolbar = findViewById(R.id.id_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.head_right_tv).setOnClickListener(this);

        smartRefreshLayout = findViewById(R.id.SmartRefreshLayout);
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getCashRecordData(IRefreshTag.IS_LOAD_MORE_DATA);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getCashRecordData(IRefreshTag.IS_REFRESH_DATA);
                reqUserBalance();
            }
        });

        recyclerView = findViewById(R.id.rl_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecycleItemDecoration(this, RecycleItemDecoration.VERTICAL, 20, R.color.default_view));
        myWalletAdapter = new BaseRecyclerAdapter<CashRecordBean.DataBean>(R.layout.item_my_wallet, mList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, CashRecordBean.DataBean itemData) {
                ImageView userHeadImg = (ImageView) viewHolder.findViewById(R.id.img_view_wallet);
                TextView userNameTv = (TextView) viewHolder.findViewById(R.id.my_wallet_name);
                ImageView courseImg = (ImageView) viewHolder.findViewById(R.id.wallet_book_img);
                TextView courseNameTv = (TextView) viewHolder.findViewById(R.id.wallet_book_name);
                TextView courseTimeTv = (TextView) viewHolder.findViewById(R.id.wallet_time);
                TextView coursePriceTv = (TextView) viewHolder.findViewById(R.id.tv_wallet_price);

                ImageUtils.GlideUtil(MyWalletActivity.this, itemData.getMainUserHeadImgUrl(), userHeadImg);
                userNameTv.setText(itemData.getMainUserNickname());
                ImageUtils.GlideUtil(MyWalletActivity.this, itemData.getCourseCoverImgUrl(), courseImg);
                courseNameTv.setText(itemData.getCourseName());
                courseTimeTv.setText(itemData.getBuyTime());
                coursePriceTv.setText(String.format(getString(R.string.cash_number_yuan), FormatNumberUtil.doubleFormat(itemData.getReturnPrice())));
            }
        };
        recyclerView.setAdapter(myWalletAdapter);

        initHeader(recyclerView);
    }

    private void initHeader(ViewGroup headerViewRoot) {
        View header = LayoutInflater.from(this).inflate(R.layout.item_header_my_wallet, headerViewRoot, false);

        header.findViewById(R.id.alipay_btn).setOnClickListener(this);
        header.findViewById(R.id.we_chat_btn).setOnClickListener(this);
        header.findViewById(R.id.deposit_btn).setOnClickListener(this);

        myWalletAdapter.setHeaderView(header);

        userBalanceTv = header.findViewById(R.id.user_balance_tv);
        userBalanceTv.setText("");
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.head_right_tv:
                intent = new Intent(this, WithdrawActivity.class);
                startActivity(intent);
                break;

            case R.id.alipay_btn:
                intent = new Intent(this, BindAliPayActivity.class);
                startActivity(intent);
                break;

            case R.id.we_chat_btn:
                intent = new Intent(this, BindWeChatActivity.class);
                startActivity(intent);
                break;

            case R.id.deposit_btn:
                intent = new Intent(this, DepositActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    //获取用户返回记录列表
    private void getCashRecordData(final int tag) {
        OkHttpUtils.post()
                .url(HttpConstant.WITHDRAW_CASH_LIST)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<CashRecordBean>(CashRecordBean.class, false, MyWalletActivity.this) {
                    @Override
                    public void onResponse(CashRecordBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        mTotalItemNum = response.getTotal() != null ? Integer.parseInt(response.getTotal()) : 0;

                        switch (tag) {
                            case IRefreshTag.IS_REFRESH_DATA:
                                if (response.getData() != null && response.getData().size() != 0) {
                                    mList.clear();
                                    mList.addAll(response.getData());
                                }

                                isCashRecordDataUpdated = true;
                                stopRefreshLayout();
                                break;
                            case IRefreshTag.IS_LOAD_MORE_DATA:
                                if (mTotalItemNum > mCurrPageNum * mPerItemNum) {
                                    ++mCurrPageNum;
                                }

                                if (response.getData() != null && mTotalItemNum > mList.size()) {
                                    mList.addAll(response.getData());
                                }
                                smartRefreshLayout.finishLoadMore();
                                break;
                        }

                        myWalletAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        smartRefreshLayout.finishRefresh();
                        smartRefreshLayout.finishLoadMore();
                    }
                });
    }

    private void reqUserBalance() {
        OkHttpUtils.get()
                .url(HttpConstant.USER_BALANCE)
                .build()
                .execute(new HttpCallBack<UserBalanceBean>(UserBalanceBean.class, false, this) {
                    @Override
                    public void onResponse(UserBalanceBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        userBalanceTv.setText(FormatNumberUtil.doubleFormat(response.getData().getBalance()));

                        isUserBalanceUpdated = true;
                        stopRefreshLayout();
                    }
                });
    }

    private void stopRefreshLayout() {
        if (isCashRecordDataUpdated && isUserBalanceUpdated) {
            smartRefreshLayout.finishRefresh();

            isCashRecordDataUpdated = false;
            isUserBalanceUpdated = false;
        }
    }
}
