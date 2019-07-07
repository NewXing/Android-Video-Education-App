package education.juxin.com.educationpro.ui.activity.home;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import education.juxin.com.educationpro.bean.SearchResultBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseDetailActivity;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 搜索结果页面
 * Created on 2018/3/19.
 */
public class CourseTypeResultActivity extends BaseActivity {

    private ArrayList<SearchResultBean.DataBean> mSearchList;
    private BaseRecyclerAdapter adapter;
    private int mPerItemNum;
    private int mCurrPageNum;
    private int mTotalItemNum;
    private String searchContent;
    private SmartRefreshLayout smartRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_type_result);
        initToolbar(true, R.string.search_result);

        mPerItemNum = 10;
        mCurrPageNum = 1;
        mTotalItemNum = 0;

        mSearchList = new ArrayList<>();
        searchContent = getIntent().getStringExtra("search_content");

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        getSearchListData(IRefreshTag.IS_LOAD_MORE_DATA);
    }
 
    private void initUI() {
        smartRefreshLayout = findViewById(R.id.refresh_layout);
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getSearchListData(IRefreshTag.IS_LOAD_MORE_DATA);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getSearchListData(IRefreshTag.IS_REFRESH_DATA);
            }
        });
        // Search Result List
        RecyclerView searchRecyclerView = findViewById(R.id.result_recyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.addItemDecoration(new RecycleItemDecoration(this));
        adapter = new BaseRecyclerAdapter<SearchResultBean.DataBean>(R.layout.item_quality_course, mSearchList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, SearchResultBean.DataBean itemData) {
                ImageView coursePictureImg = (ImageView) viewHolder.findViewById(R.id.course_picture_img);
                TextView courseTitleTv = (TextView) viewHolder.findViewById(R.id.course_title_tv);
                TextView courseTeacherTv = (TextView) viewHolder.findViewById(R.id.course_teacher_tv);
                TextView courseTimeTv = (TextView) viewHolder.findViewById(R.id.course_time_tv);
                TextView oldPriceTv = (TextView) viewHolder.findViewById(R.id.old_price_tv);
                TextView newPriceTv = (TextView) viewHolder.findViewById(R.id.new_price_tv);

                ImageUtils.GlideUtil(CourseTypeResultActivity.this, itemData.getCoverImgUrl(), coursePictureImg);

                courseTitleTv.setText(Html.fromHtml(itemData.getName()));
                courseTeacherTv.setText(String.format(getString(R.string.main_teacher_with_name), itemData.getMainTeacherName()));
                courseTimeTv.setText(String.format(getString(R.string.course_time_with_num), itemData.getClassNum()));
                oldPriceTv.setText(FormatNumberUtil.doubleFormat(itemData.getOriginalPrice()));
                oldPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                String originalPrice = FormatNumberUtil.doubleFormat(itemData.getCurrentPrice());
                newPriceTv.setText(String.format(getString(R.string.money_only_with_number), originalPrice));
            }
        };
        searchRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                CourseDetailActivity.checkCourseValid(CourseTypeResultActivity.this, mSearchList.get(position).getId(), "",
                        new ICheckCourseValid() {
                            @Override
                            public void isValid() {
                                Intent intent = new Intent(CourseTypeResultActivity.this, CourseDetailActivity.class);
                                intent.putExtra("id_course_detail", mSearchList.get(position).getId());
                                startActivity(intent);
                            }

                            @Override
                            public void isInvalid() {
                            }
                        });
            }
        });
    }

    //搜索过课程的列表
    private void getSearchListData(final int tag) {
        OkHttpUtils.get()
                .url(HttpConstant.SEARCH_COURSE_LIST)
                .addParams("content", searchContent)
                .addParams("page", String.valueOf(tag == IRefreshTag.IS_REFRESH_DATA ? 1 : mCurrPageNum))
                .addParams("rows", String.valueOf(mPerItemNum))
                .build()
                .execute(new HttpCallBack<SearchResultBean>(SearchResultBean.class, false, CourseTypeResultActivity.this) {
                    @Override
                    public void onResponse(SearchResultBean response, int id) {
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
                                mCurrPageNum = 2;
                                smartRefreshLayout.finishRefresh();
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
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        smartRefreshLayout.finishLoadMore();
                        smartRefreshLayout.finishRefresh();
                    }
                });
    }
}
