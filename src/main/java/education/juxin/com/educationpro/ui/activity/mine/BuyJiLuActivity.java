package education.juxin.com.educationpro.ui.activity.mine;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.bean.OrderListBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.util.ScreenUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 购买记录页面
 * The type Ji lu activity.
 */
public class BuyJiLuActivity extends BaseActivity {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mNoDataLayout;

    private ArrayList<OrderListBean.DataBean> mSearchList;
    private BaseRecyclerAdapter collectAdapter;

    private int mPerItemNum;
    private int mCurrPageNum;
    private int mTotalItemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ji_lu);
        initToolbar(true, R.string.buy_notes);

        mSearchList = new ArrayList<>();

        mPerItemNum = 10;
        mCurrPageNum = 1;
        mTotalItemNum = 0;

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        getOrderData(IRefreshTag.IS_LOAD_MORE_DATA);
    }

    private void initUI() {
        mNoDataLayout = findViewById(R.id.no_data_view);

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getOrderData(IRefreshTag.IS_REFRESH_DATA);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getOrderData(IRefreshTag.IS_LOAD_MORE_DATA);
            }
        });

        mRecyclerView = findViewById(R.id.recycler_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecycleItemDecoration(this, RecycleItemDecoration.VERTICAL, 30, R.color.default_view));
        collectAdapter = new BaseRecyclerAdapter<OrderListBean.DataBean>(R.layout.item_person_space, mSearchList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, OrderListBean.DataBean itemData) {
                ImageView headUrl = (ImageView) viewHolder.findViewById(R.id.civ_space_person_head);
                ImageView imgQRCode = (ImageView) viewHolder.findViewById(R.id.img_qr_code);
                TextView courseName = (TextView) viewHolder.findViewById(R.id.course_name);
                TextView price = (TextView) viewHolder.findViewById(R.id.price);
                TextView time = (TextView) viewHolder.findViewById(R.id.tv_time);
                Button btnQRCode = (Button) viewHolder.findViewById(R.id.btn_qr_code);

                ImageUtils.GlideUtil(BuyJiLuActivity.this, itemData.getCoverImgUrl(), headUrl);
                Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                Bitmap bitmapQRCode = QRCodeEncoder.syncEncodeQRCode(
                        itemData.getCode(), ScreenUtils.dp2px(BuyJiLuActivity.this, 150), getResources().getColor(R.color.not_psd), logoBitmap);
                imgQRCode.setImageBitmap(bitmapQRCode);
                courseName.setText(itemData.getCourseName());
                price.setText(String.format(getString(R.string.number_yuan), FormatNumberUtil.doubleFormat(itemData.getTotalAmount())));
                time.setText(itemData.getPaytime());
                btnQRCode.setText(itemData.getCode());
                btnQRCode.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cm != null) {
                            cm.setPrimaryClip(ClipData.newPlainText("qr_code", itemData.getCode()));
                            ToastManager.showShortToast("复制成功！");
                        }
                        return false;
                    }
                });
            }
        };
        collectAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                if (mSearchList.size() == 0) {
                    mNoDataLayout.setVisibility(View.VISIBLE);
                    mRefreshLayout.setVisibility(View.GONE);
                } else {
                    mNoDataLayout.setVisibility(View.GONE);
                    mRefreshLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        mRecyclerView.setAdapter(collectAdapter);
    }

    private void getOrderData(final int tag) {
        OkHttpUtils.get()
                .url(HttpConstant.ORDER_LIST)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<OrderListBean>(OrderListBean.class, false, BuyJiLuActivity.this) {
                    @Override
                    public void onResponse(OrderListBean response, int id) {
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
                                if (mTotalItemNum > mPerItemNum * mCurrPageNum) {
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
