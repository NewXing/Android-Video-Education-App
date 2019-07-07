package education.juxin.com.educationpro.ui.activity.dynamic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashSet;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.adapter.recyclerview.DynamicAllAdapter;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.bean.IsCateBean;
import education.juxin.com.educationpro.bean.TeacherBaseInfoBean;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.util.ScreenUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.bean.TeacherBannerBean;
import education.juxin.com.educationpro.bean.TeacherHomePageBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.view.CircleImageView;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 个人空间的页面
 * <p>
 * The type Space activity.
 * create time 2018-3-22
 */
public class SpaceMainActivity extends BaseActivity {

    private SmartRefreshLayout smartRefresh;
    private RecyclerView rvList;
    private Banner banner;
    private CircleImageView teacherHeaderImg;
    private TextView teacherNameTv;
    private DynamicAllAdapter dynamicAdapter;
    private TextView tvConcerns;

    private ArrayList<ArrayList<TeacherHomePageBean.TeacherHomePageData.TeacherHomePageItemData>> mGroupList;
    private ArrayList<TeacherBannerBean.DataBean> mBannerDataList;

    private String mTeacherId = "";
    private int mSelectNum;

    private boolean isTeacherHomePageOver;
    private boolean isBannerListOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_actvity);
        initToolbar(true, R.string.space_page);

        mTeacherId = getIntent().getStringExtra("teacherId");
        mSelectNum = 2;

        mGroupList = new ArrayList<>();
        mBannerDataList = new ArrayList<>();

        initUI();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mTeacherId = getIntent().getStringExtra("teacherId");
    }

    @Override
    public void onResume() {
        super.onResume();

        reqTeacherBaseInfo();
        reqTeacherHomePageData();
        reqBannerList();

        String token = String.valueOf(SPHelper.getSimpleParam(this, "token", ""));
        if (token != null && !token.trim().isEmpty()) {
            isCareData();
        }
    }

    private void initUI() {
        initRecycler();
        initHeader();
    }

    private void initRecycler() {
        smartRefresh = findViewById(R.id.smart_refresh);
        smartRefresh.setEnableLoadMore(false);
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                reqTeacherBaseInfo();
                reqTeacherHomePageData();
                reqBannerList();
            }
        });

        rvList = findViewById(R.id.recycler_list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new RecycleItemDecoration(this, RecycleItemDecoration.VERTICAL, 30, R.color.default_view));
        dynamicAdapter = new DynamicAllAdapter(this, mGroupList, mTeacherId);
        rvList.setAdapter(dynamicAdapter);
        dynamicAdapter.setOnItemClickListener(new DynamicAllAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
            }

            @Override
            public void onLongClick(int position) {
            }
        });
        dynamicAdapter.setListViewItemClickListener(new DynamicAllAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
            }

            @Override
            public void onLongClick(int position) {
            }
        });
    }

    private void initHeader() {
        View header = LayoutInflater.from(this).inflate(R.layout.item_header_space, rvList, false);
        dynamicAdapter.setHeaderView(header);

        banner = header.findViewById(R.id.banner);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) banner.getLayoutParams();
        layoutParams.height = ScreenUtils.getScreenWidth(this) * 2 / 3;
        banner.setLayoutParams(layoutParams);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        try {
            banner.setImageLoader(new ImageLoader() {
                @Override
                public void displayImage(Context context, Object path, ImageView imageView) {
                    if (context != null && imageView != null) {
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                        RequestOptions options = new RequestOptions();
                        options.error(R.drawable.load_error);

                        Glide.with(context)
                                .load(ImageUtils.buildGlideUrl((String) path))
                                .apply(options)
                                .into(imageView);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        banner.setBannerAnimation(Transformer.ZoomOutSlide);
        banner.isAutoPlay(true);
        banner.setDelayTime(2500);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                try {
                    if (mBannerDataList != null) {
                        CourseDetailActivity.checkCourseValid(SpaceMainActivity.this, mBannerDataList.get(position).getCourseId(), "", new ICheckCourseValid() {
                            @Override
                            public void isValid() {
                                Intent intent = new Intent(SpaceMainActivity.this, CourseDetailActivity.class);
                                intent.putExtra("id_course_detail", mBannerDataList.get(position).getCourseId());
                                startActivity(intent);
                            }

                            @Override
                            public void isInvalid() {
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        teacherHeaderImg = header.findViewById(R.id.teacher_header_img);
        teacherNameTv = header.findViewById(R.id.teacher_header_name);

        tvConcerns = header.findViewById(R.id.tv_concerns);
        tvConcerns.setTag(false);
        tvConcerns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(boolean) view.getTag()) {
                    userCare();
                } else {
                    userCancelCare();
                }
            }
        });
        setCareBtnState(false);
    }

    private void setCareBtnState(boolean isChecked) {
        tvConcerns.setTag(isChecked);

        if ((boolean) tvConcerns.getTag()) {
            tvConcerns.setText("已关注");
            tvConcerns.setTextColor(getResources().getColor(R.color.bg_white));
            tvConcerns.setBackgroundResource(R.drawable.login);
        } else {
            tvConcerns.setText("+关注");
            tvConcerns.setTextColor(getResources().getColor(R.color.green_bg));
            tvConcerns.setBackgroundResource(R.drawable.concern_green_bg);
        }
    }

    private void reqTeacherBaseInfo() {
        OkHttpUtils.get()
                .url(HttpConstant.TEACHER_BASE_INFO + String.valueOf(mTeacherId))
                .build()
                .execute(new HttpCallBack<TeacherBaseInfoBean>(TeacherBaseInfoBean.class, true, this) {
                    @Override
                    public void onResponse(TeacherBaseInfoBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData() == null) {
                            return;
                        }

                        ImageUtils.GlideUtil(SpaceMainActivity.this, response.getData().getHeadImgUrl(), teacherHeaderImg);
                        teacherNameTv.setText(response.getData().getName());
                    }
                });
    }

    private void reqTeacherHomePageData() {
        OkHttpUtils.get()
                .url(HttpConstant.TEACHER_HOME_PAGE)
                .addParams("teacherId", mTeacherId)
                .addParams("selectNum", String.valueOf(mSelectNum))
                .build()
                .execute(new HttpCallBack<TeacherHomePageBean>(TeacherHomePageBean.class, false, this) {
                    @Override
                    public void onResponse(TeacherHomePageBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData() == null) {
                            smartRefresh.finishRefresh();
                            return;
                        }

                        HashSet<String> set = new HashSet<>();
                        for (TeacherHomePageBean.TeacherHomePageData.TeacherHomePageItemData data : response.getData().getCourseList()) {
                            set.add(data.getClassificationId());
                        }

                        mGroupList.clear();
                        for (String typeIdStr : set) {
                            ArrayList<TeacherHomePageBean.TeacherHomePageData.TeacherHomePageItemData> itemList = new ArrayList<>();

                            for (TeacherHomePageBean.TeacherHomePageData.TeacherHomePageItemData data : response.getData().getCourseList()) {
                                if (typeIdStr.equals(data.getClassificationId())) {
                                    itemList.add(data);
                                }
                            }

                            mGroupList.add(itemList);
                        }

                        dynamicAdapter.notifyDataSetChanged();

                        isBannerListOver = true;
                        stopRefreshLayout();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        smartRefresh.finishRefresh();
                    }
                });
    }

    private void reqBannerList() {
        OkHttpUtils.get()
                .url(HttpConstant.ACCOUNT_TEACHER)
                .addParams("teacherId", mTeacherId)
                .build()
                .execute(new HttpCallBack<TeacherBannerBean>(TeacherBannerBean.class, false, this) {
                    @Override
                    public void onResponse(TeacherBannerBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData() != null) {
                            mBannerDataList.clear();
                            mBannerDataList.addAll(response.getData());
                        }

                        ArrayList<String> mImgList = new ArrayList<>();
                        for (TeacherBannerBean.DataBean data : mBannerDataList) {
                            mImgList.add(data.getImgUrl());
                        }
                        try {
                            if (mImgList.size() > 0) {
                                banner.setImages(mImgList);
                                banner.start();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        isTeacherHomePageOver = true;
                        stopRefreshLayout();
                    }
                });
    }

    private void stopRefreshLayout() {
        if (isTeacherHomePageOver && isBannerListOver) {
            smartRefresh.finishRefresh();

            isTeacherHomePageOver = false;
            isBannerListOver = false;
        }
    }

    // 判断是否被关注
    private void isCareData() {
        OkHttpUtils.get()
                .url(HttpConstant.IS_CATE)
                .addParams("teacherId", String.valueOf(mTeacherId))
                .build()
                .execute(new HttpCallBack<IsCateBean>(IsCateBean.class, true, this) {
                    @Override
                    public void onResponse(final IsCateBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        setCareBtnState(response.isData());
                    }
                });
    }

    // 添加关注接口信息
    public void userCare() {
        OkHttpUtils.post()
                .url(HttpConstant.USER_CATE)
                .addParams("teacherId", String.valueOf(mTeacherId))
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                        ToastManager.showShortToast("关注成功！");
                        setCareBtnState(true);
                    }
                });
    }

    // 取消关注接口信息
    public void userCancelCare() {
        OkHttpUtils.post()
                .url(HttpConstant.USER_CANCEL_CATE)
                .addParams("teacherId", String.valueOf(mTeacherId))
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                        ToastManager.showShortToast("已取消关注！");
                        setCareBtnState(false);
                    }
                });
    }

}
