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
import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.bean.MyRewardBean;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 我的打赏页面
 * The type My reward activity.
 */
public class MyRewardActivity extends BaseActivity {

    private ArrayList<MyRewardBean.DataBean> mSearchList;
    private SmartRefreshLayout smartRefreshLayout;

    private int mPerItemNum;   //每一页 多少条
    private int mCurrPageNum;  //当前页数
    private int mTotalItemNum;  //当前条数
    private BaseRecyclerAdapter rewardAdapter;
    private View mNoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reward);
        initToolbar(true, R.string.my_reward);
        mSearchList = new ArrayList<>();

        mCurrPageNum = 1;
        mPerItemNum = 10;
        mTotalItemNum = 0;

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyRewardDta(IRefreshTag.IS_LOAD_MORE_DATA);
    }


    private void initUI() {
        mNoLayout = findViewById(R.id.no_data_view);
        smartRefreshLayout = findViewById(R.id.refresh_layout);
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getMyRewardDta(IRefreshTag.IS_LOAD_MORE_DATA);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getMyRewardDta(IRefreshTag.IS_REFRESH_DATA);
            }
        });

        RecyclerView xrvList = findViewById(R.id.recycler_list);
        xrvList.setLayoutManager(new LinearLayoutManager(this));
        xrvList.addItemDecoration(new RecycleItemDecoration(this, RecycleItemDecoration.VERTICAL, 30, R.color.default_view));
        rewardAdapter = new BaseRecyclerAdapter<MyRewardBean.DataBean>(R.layout.item_my_reward, mSearchList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, MyRewardBean.DataBean itemData) {
                ImageView courseImg = (ImageView) viewHolder.findViewById(R.id.img_course_image);
                TextView payModes = (TextView) viewHolder.findViewById(R.id.pay_modes);
                TextView presentName = (TextView) viewHolder.findViewById(R.id.present_name);
                TextView tvCounts = (TextView) viewHolder.findViewById(R.id.tv_counts);
                TextView courseNames = (TextView) viewHolder.findViewById(R.id.tv_course_names);
                TextView tvMoney = (TextView) viewHolder.findViewById(R.id.tv_money);
                TextView tvTime = (TextView) viewHolder.findViewById(R.id.tv_time);
                TextView tvMainTeacher = (TextView) viewHolder.findViewById(R.id.tv_teach_person);

                ImageUtils.GlideUtil(MyRewardActivity.this, itemData.getCoverImgUrl(), courseImg);
                if ("1".equals(itemData.getPayType())) {
                    payModes.setText(getString(R.string.alipay_pay));
                } else if ("2".equals(itemData.getPayType())) {
                    payModes.setText(getString(R.string.wechat_pay));
                }
                presentName.setText(String.format(getString(R.string.give_who), itemData.getTeacherName()));
                tvCounts.setText(String.format(getString(R.string.x_count), itemData.getGiftNum()));
                courseNames.setText(String.format(getString(R.string.course_name_code), itemData.getCourseName()));
                tvMoney.setText(String.format(getString(R.string.number_yuan), FormatNumberUtil.doubleFormat(itemData.getTotalAmount())));
                tvTime.setText(itemData.getPaytime());
                tvMainTeacher.setText(String.format(getString(R.string.main_teacher_with_name), itemData.getMainTeacherName()));
            }
        };
        xrvList.setAdapter(rewardAdapter);

        rewardAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mSearchList.size() == 0) {
                    mNoLayout.setVisibility(View.VISIBLE);
                    smartRefreshLayout.setVisibility(View.GONE);
                } else {
                    mNoLayout.setVisibility(View.GONE);
                    smartRefreshLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getMyRewardDta(final int tag) {
        OkHttpUtils.get()
                .url(HttpConstant.MY_REWARD_LIST)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<MyRewardBean>(MyRewardBean.class, false, MyRewardActivity.this) {
                    @Override
                    public void onResponse(MyRewardBean response, int id) {
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
                                smartRefreshLayout.finishRefresh();
                                mCurrPageNum = 2;
                                break;
                            case IRefreshTag.IS_LOAD_MORE_DATA:
                                if (mTotalItemNum > mCurrPageNum * mPerItemNum) {
                                    ++mCurrPageNum;
                                }
                                if (response.getData() != null && mTotalItemNum > mSearchList.size()) {
                                    mSearchList.addAll(response.getData());
                                }
                                smartRefreshLayout.finishLoadMore();
                                break;
                        }
                        rewardAdapter.notifyDataSetChanged();
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
