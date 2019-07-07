package education.juxin.com.educationpro.ui.activity.mine;

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
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.bean.MessageCountListBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 消息通知界面
 */

public class MessageNotifyActivity extends BaseActivity {

    private ArrayList<MessageCountListBean.DataBean> mList = new ArrayList<>();
    private SmartRefreshLayout smartRefreshLayout;

    private int mPerItemNum; //每一页 多少条
    private int mCurrPageNum; //当前页数
    private int mTotalItemNum;  //当前条数
    private BaseRecyclerAdapter concernAdapter;
    private View noLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_notify);
        initToolbar(true, R.string.message_notify);

        mCurrPageNum = 1;
        mPerItemNum = 10;
        mTotalItemNum = 0;

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        getMessageCountList(IRefreshTag.IS_LOAD_MORE_DATA);
    }

    private void initUI() {
        noLayout = findViewById(R.id.no_data_view);
        RecyclerView recyclerView = findViewById(R.id.message_recycler);
        smartRefreshLayout = findViewById(R.id.message_smart_refresh);
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getMessageCountList(IRefreshTag.IS_LOAD_MORE_DATA);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getMessageCountList(IRefreshTag.IS_REFRESH_DATA);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecycleItemDecoration(this));
        concernAdapter = new BaseRecyclerAdapter<MessageCountListBean.DataBean>(R.layout.item_message_notify, mList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, MessageCountListBean.DataBean itemData) {
                ImageView imageMessageIcon = (ImageView) viewHolder.findViewById(R.id.img_message_icon);
                TextView tvMessageTitle = (TextView) viewHolder.findViewById(R.id.tv_message_title);
                TextView tvMessageContent = (TextView) viewHolder.findViewById(R.id.tv_message_content);
                View view = viewHolder.findViewById(R.id.is_read_view);
                switch (itemData.getState()) {
                    case "2":
                        view.setVisibility(View.GONE);
                        break;
                    case "1":
                        view.setVisibility(View.VISIBLE);
                        break;
                }

                switch (itemData.getMessageType()) {
                    case "1":
                        ImageUtils.GlideUtil(MessageNotifyActivity.this, itemData.getTeacherHeadImgUrl(), imageMessageIcon);
                        tvMessageContent.setText(getString(R.string.up_load_video));
                        tvMessageTitle.setText(itemData.getTeacherName());
                        break;
                    case "2":
                        ImageUtils.GlideUtil(MessageNotifyActivity.this, itemData.getCourseCoverImgUrl(), imageMessageIcon);
                        tvMessageContent.setText("更新了");
                        tvMessageTitle.setText(itemData.getName());
                        break;
                    case "3":
                        ImageUtils.GlideUtil(MessageNotifyActivity.this, itemData.getCourseCoverImgUrl(), imageMessageIcon);
                        tvMessageContent.setText(String.format(getString(R.string.course_return_price), FormatNumberUtil.doubleFormat(itemData.getReturnPrice())));
                        tvMessageTitle.setText(itemData.getUserNickName());
                        break;
                }
            }
        };
        recyclerView.setAdapter(concernAdapter);
        concernAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mList.size() == 0) {
                    noLayout.setVisibility(View.VISIBLE);
                    smartRefreshLayout.setVisibility(View.GONE);
                } else {
                    noLayout.setVisibility(View.GONE);
                    smartRefreshLayout.setVisibility(View.VISIBLE);
                }

            }
        });
        concernAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
            }
        });
    }

    //获取未读消息列表
    private void getMessageCountList(final int tag) {
        OkHttpUtils.get()
                .url(HttpConstant.MESSAGE_COUNT_LIST)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<MessageCountListBean>(MessageCountListBean.class, true, MessageNotifyActivity.this) {
                    @Override
                    public void onResponse(MessageCountListBean response, int id) {
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
                                smartRefreshLayout.finishRefresh();
                                mCurrPageNum = 2;
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

                        concernAdapter.notifyDataSetChanged();

                        updateMessageData();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        smartRefreshLayout.finishRefresh();
                        smartRefreshLayout.finishLoadMore();
                    }
                });
    }

    //更新消息为已读
    private void updateMessageData() {
        OkHttpUtils.get()
                .url(HttpConstant.UPDATE_MESSAGE_READ)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, false, MessageNotifyActivity.this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                    }
                });
    }

}
