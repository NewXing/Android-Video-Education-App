package education.juxin.com.educationpro.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
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

import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.adapter.listview.CourseTypeGridAdapter;
import education.juxin.com.educationpro.base.BaseFragment;
import education.juxin.com.educationpro.bean.GetMessageCountBean;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.ui.activity.dynamic.SpaceMainActivity;
import education.juxin.com.educationpro.ui.activity.login.LoginActivity;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.bean.AbsBean;
import education.juxin.com.educationpro.bean.CourseCategoryBean;
import education.juxin.com.educationpro.bean.ExcellentClassBean;
import education.juxin.com.educationpro.bean.QualityCourseBean;
import education.juxin.com.educationpro.bean.RecommendTeacherBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.http.NetworkHelper;
import education.juxin.com.educationpro.ui.activity.home.CourseTypeActivity;
import education.juxin.com.educationpro.ui.activity.home.HomeActivity;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseDetailActivity;
import education.juxin.com.educationpro.ui.activity.mine.MessageNotifyActivity;
import education.juxin.com.educationpro.util.ScreenUtils;
import education.juxin.com.educationpro.view.RedPointView;
import education.juxin.com.educationpro.view.RoundImageView;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * tab 首页 页面
 * The type Home pager fragment.
 */
public class HomePagerFragment extends BaseFragment implements View.OnClickListener {

    private ArrayList<AbsBean.AbsData> mAbsDataList;
    private ArrayList<QualityCourseBean.DataBean> mQualityCourseList;
    private ArrayList<RecommendTeacherBean.RecommendTeacherData> mTeacherRecommendList;
    private ArrayList<ExcellentClassBean.ExcellentClassData> mExcellentClassList;
    private ArrayList<CourseCategoryBean.CourseCategoryData> mTypeCategoryDataList;

    private BaseRecyclerAdapter teacherRecommendAdapter;
    private BaseRecyclerAdapter qualityCourseAdapter;
    private BaseRecyclerAdapter excellentClassAdapter;
    private CourseTypeGridAdapter typeCategoryGridAdapter;

    private View rootView;
    private View headerView;
    private View footerView;
    private Banner banner;
    private SmartRefreshLayout refreshLayout;
    private RelativeLayout netErrLayout;
    private GridView typeCategoryGridView;

    private boolean isAbsUpdated;
    private boolean isQualityUpdated;
    private boolean isRecommendUpdated;
    private boolean isExcellentUpdated;
    private boolean isCategoryUpdated;
    private RedPointView redPointView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_root, null);

        mAbsDataList = new ArrayList<>();
        mQualityCourseList = new ArrayList<>();
        mTeacherRecommendList = new ArrayList<>();
        mExcellentClassList = new ArrayList<>();
        mTypeCategoryDataList = new ArrayList<>();

        initUI();

        updateData();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        String tokenStr = (String) SPHelper.getSimpleParam(getContext(), "token", "");
        if (tokenStr != null && !tokenStr.isEmpty()) {
            getMessageCountData();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            String tokenStr = (String) SPHelper.getSimpleParam(getContext(), "token", "");
            if (tokenStr != null && !tokenStr.isEmpty()) {
                getMessageCountData();
            }
        }
    }

    private void initUI() {
        // Title
        rootView.findViewById(R.id.main_search_edit).setOnClickListener(this);

        ImageButton messageBtn = rootView.findViewById(R.id.message_btn);
        messageBtn.setOnClickListener(this);
        redPointView = new RedPointView(getActivity());
        redPointView.setTargetView(messageBtn);

        LinearLayout linearLayout = rootView.findViewById(R.id.ll_layout);
        linearLayout.setFocusable(true);
        linearLayout.requestFocus();
        linearLayout.setFocusableInTouchMode(true);
        linearLayout.requestFocusFromTouch();

        // Net Err Layout
        netErrLayout = rootView.findViewById(R.id.error_relative);
        netErrLayout.setVisibility(View.GONE);
        netErrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
                String tokenStr = (String) SPHelper.getSimpleParam(getContext(), "token", "");
                if (tokenStr != null && !tokenStr.isEmpty()) {
                    getMessageCountData();
                }
            }
        });

        // Root Recycler
        initRootRecycler();
        initHeaderViews();
        initFooterViews();
    }

    private void initRootRecycler() {
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                updateData();
            }
        });

        RecyclerView qualityCourseRView = rootView.findViewById(R.id.quality_course_recyclerView);
        qualityCourseRView.setLayoutManager(new LinearLayoutManager(getContext()));
        qualityCourseRView.addItemDecoration(new RecycleItemDecoration(getContext()));
        qualityCourseAdapter = new BaseRecyclerAdapter<QualityCourseBean.DataBean>(R.layout.item_quality_course, mQualityCourseList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, QualityCourseBean.DataBean itemData) {
                ImageView coursePictureImg = (ImageView) viewHolder.findViewById(R.id.course_picture_img);
                TextView courseTitleTv = (TextView) viewHolder.findViewById(R.id.course_title_tv);
                TextView courseTeacherTv = (TextView) viewHolder.findViewById(R.id.course_teacher_tv);
                TextView courseTimeTv = (TextView) viewHolder.findViewById(R.id.course_time_tv);
                TextView oldPriceTv = (TextView) viewHolder.findViewById(R.id.old_price_tv);
                TextView newPriceTv = (TextView) viewHolder.findViewById(R.id.new_price_tv);

                ImageUtils.GlideUtil(getActivity(), itemData.getCoverImgUrl(), coursePictureImg);
                courseTitleTv.setText(itemData.getName());
                courseTeacherTv.setText(String.format(getContext().getString(R.string.main_teacher_with_name), itemData.getMainTeacherName()));
                courseTimeTv.setText(String.format(getContext().getString(R.string.course_time_with_num), itemData.getClassNum()));
                oldPriceTv.setText(FormatNumberUtil.doubleFormat(itemData.getOriginalPrice()));
                oldPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                newPriceTv.setText(String.format(getContext().getString(R.string.money_only_with_number), FormatNumberUtil.doubleFormat(itemData.getCurrentPrice())));
            }
        };
        qualityCourseRView.setAdapter(qualityCourseAdapter);
        qualityCourseAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                CourseDetailActivity.checkCourseValid(getActivity(), mQualityCourseList.get(position - 1).getId(), "", new ICheckCourseValid() {
                    @Override
                    public void isValid() {
                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                        intent.putExtra("id_course_detail", mQualityCourseList.get(position - 1).getId());
                        startActivity(intent);
                    }

                    @Override
                    public void isInvalid() {
                    }
                });
            }
        });

        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_header_home, qualityCourseRView, false);
        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_footer_home, qualityCourseRView, false);
        qualityCourseAdapter.setHeaderView(headerView);
        qualityCourseAdapter.setFooterView(footerView);
    }

    private void initHeaderViews() {
        // Banner
        banner = headerView.findViewById(R.id.banner);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) banner.getLayoutParams();
        layoutParams.height = ScreenUtils.getScreenWidth(getContext()) * 7 / 18;
        banner.setLayoutParams(layoutParams);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        try {
            banner.setImageLoader(new ImageLoader() {
                @Override
                public void displayImage(Context context, Object path, ImageView imageView) {
                    if (context != null && imageView != null) {
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                        RequestOptions options = new RequestOptions();
                        options.error(R.drawable.load_err_home);

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
        banner.setDelayTime(3000);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                try {
                    AbsBean.AbsData data = mAbsDataList.get(position);
                    switch (data.getType()) {
                        case "1": // 公众号
                            Intent intent1 = new Intent(getActivity(), SpaceMainActivity.class);
                            intent1.putExtra("teacherId", data.getFkId());
                            startActivity(intent1);
                            break;

                        case "2": // 课程
                            CourseDetailActivity.checkCourseValid(getActivity(), data.getFkId(), "", new ICheckCourseValid() {
                                @Override
                                public void isValid() {
                                    Intent intent2 = new Intent(getActivity(), CourseDetailActivity.class);
                                    intent2.putExtra("id_course_detail", data.getFkId());
                                    startActivity(intent2);
                                }

                                @Override
                                public void isInvalid() {
                                }
                            });
                            break;

                        case "3": // 课节
                            CourseDetailActivity.checkCourseValid(getActivity(), data.getCourseId(), data.getFkId(), new ICheckCourseValid() {
                                @Override
                                public void isValid() {
                                    Intent intent3 = new Intent(getActivity(), CourseDetailActivity.class);
                                    intent3.putExtra("id_course_detail", data.getCourseId());
                                    intent3.putExtra("lesson_id", data.getFkId());
                                    startActivity(intent3);
                                }

                                @Override
                                public void isInvalid() {
                                }
                            });
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        typeCategoryGridView = headerView.findViewById(R.id.course_type_grid_view);
        typeCategoryGridAdapter = new CourseTypeGridAdapter(getActivity(), mTypeCategoryDataList);
        typeCategoryGridView.setAdapter(typeCategoryGridAdapter);

        typeCategoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> allTypeArr = new ArrayList<>();
                ArrayList<String> allTypeName = new ArrayList<>();
                for (int i = 0; i < mTypeCategoryDataList.size(); ++i) {
                    allTypeArr.add(mTypeCategoryDataList.get(i).getId());
                    allTypeName.add(mTypeCategoryDataList.get(i).getName());
                }
                Intent intent = new Intent(getActivity(), CourseTypeActivity.class);
                intent.putExtra("all_type_arr", allTypeArr);
                intent.putExtra("all_type_name_arr", allTypeName);
                intent.putExtra("curr_type_id", position);
                startActivity(intent);
            }
        });
    }

    private void initFooterViews() {
        // 底部两个 RecyclerView
        RecyclerView teacherRecommendRView = footerView.findViewById(R.id.teacher_recommend_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        teacherRecommendRView.setLayoutManager(linearLayoutManager);

        teacherRecommendAdapter = new BaseRecyclerAdapter<RecommendTeacherBean.RecommendTeacherData>(R.layout.item_teacher_recommend, mTeacherRecommendList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, RecommendTeacherBean.RecommendTeacherData itemData) {
                ImageView teacherHeaderImg = (ImageView) viewHolder.findViewById(R.id.teacher_header_img);
                TextView teacherNameTv = (TextView) viewHolder.findViewById(R.id.teacher_name_tv);
                TextView teacherCharacterTv = (TextView) viewHolder.findViewById(R.id.teacher_character_tv);
                TextView teacherNicknameTv = (TextView) viewHolder.findViewById(R.id.teacher_nickname_tv);

                ImageUtils.GlideUtil(getActivity(), itemData.getHeadImgUrl(), teacherHeaderImg);
                teacherNameTv.setText(itemData.getName());
                teacherCharacterTv.setText("");
                teacherNicknameTv.setText("");
            }
        };
        teacherRecommendRView.setAdapter(teacherRecommendAdapter);

        teacherRecommendAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(), SpaceMainActivity.class);
                intent.putExtra("teacherId", mTeacherRecommendList.get(position).getId());
                getContext().startActivity(intent);
            }
        });

        RecyclerView excellentClassRView = footerView.findViewById(R.id.excellent_class_recyclerView);
        excellentClassRView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        excellentClassAdapter = new BaseRecyclerAdapter<ExcellentClassBean.ExcellentClassData>(R.layout.item_excellent_class, mExcellentClassList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, ExcellentClassBean.ExcellentClassData itemData) {
                RoundImageView coursePictureImg = (RoundImageView) viewHolder.findViewById(R.id.course_picture_img);
                TextView courseTitleTv = (TextView) viewHolder.findViewById(R.id.course_title_tv);
                TextView priceTv = (TextView) viewHolder.findViewById(R.id.price_tv);
                TextView courseTimeTv = (TextView) viewHolder.findViewById(R.id.course_time_tv);

                ImageUtils.GlideUtil(getContext(), itemData.getCoverImgUrl(), coursePictureImg);
                courseTitleTv.setText(itemData.getName());
                String courseTimeStr = String.format(getString(R.string.course_time_num), itemData.getCurrentNum());
                courseTimeTv.setText(StringUtils.setColorSpan(courseTimeStr, getResources().getColor(R.color.red),
                        2, courseTimeStr.length() - 2));
                priceTv.setText(String.format(getString(R.string.money_only_with_number), FormatNumberUtil.doubleFormat(itemData.getCurrentPrice())));
            }
        };
        excellentClassRView.setAdapter(excellentClassAdapter);
        excellentClassAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int test = 0;
                CourseDetailActivity.checkCourseValid(getActivity(), mExcellentClassList.get(position).getCourseId(), mExcellentClassList.get(position).getId(),
                        new ICheckCourseValid() {
                            @Override
                            public void isValid() {
                                Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                intent.putExtra("id_course_detail", mExcellentClassList.get(position).getCourseId());
                                intent.putExtra("lesson_id", mExcellentClassList.get(position).getId());
                                startActivity(intent);
                            }

                            @Override
                            public void isInvalid() {
                            }
                        });
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_search_edit:
                ArrayList<String> allTypeArr = new ArrayList<>();
                ArrayList<String> allTypeName = new ArrayList<>();
                for (int i = 0; i < mTypeCategoryDataList.size(); ++i) {
                    allTypeArr.add(mTypeCategoryDataList.get(i).getId());
                    allTypeName.add(mTypeCategoryDataList.get(i).getName());
                }
                Intent intent = new Intent(getActivity(), CourseTypeActivity.class);
                intent.putExtra("all_type_arr", allTypeArr);
                intent.putExtra("all_type_name_arr", allTypeName);
                intent.putExtra("curr_type_id", 0);
                startActivity(intent);

                break;

            case R.id.message_btn:
                String tokenStr = (String) SPHelper.getSimpleParam(getContext(), "token", "");
                if (tokenStr != null && !tokenStr.isEmpty()) {
                    startActivity(new Intent(getActivity(), MessageNotifyActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;

            default:
                break;
        }
    }

    public void updateData() {
        // 首页轮播图接口
        OkHttpUtils.get()
                .url(HttpConstant.ABS_BANNER_URL)
                .build()
                .execute(new HttpCallBack<AbsBean>(AbsBean.class, false, getActivity()) {
                    @Override
                    public void onResponse(AbsBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData() != null) {
                            mAbsDataList.clear();
                            mAbsDataList.addAll(response.getData());
                        }

                        ArrayList<String> urlList = new ArrayList<>();
                        for (AbsBean.AbsData data : mAbsDataList) {
                            urlList.add(data.getThumbnails());
                        }
                        try {
                            if (urlList.size() > 0) {
                                banner.setImages(urlList);
                                banner.start();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        isAbsUpdated = true;
                        stopRefreshLayout();
                    }
                });

        // 课程分类相关接口
        OkHttpUtils.get()
                .url(HttpConstant.COURSE_CATEGORY)
                .build()
                .execute(new HttpCallBack<CourseCategoryBean>(CourseCategoryBean.class, false, getActivity()) {
                    @Override
                    public void onResponse(CourseCategoryBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                        if (response.getData() != null) {
                            mTypeCategoryDataList.clear();
                            mTypeCategoryDataList.addAll(response.getData());
                        }

                        int columnWidth = ScreenUtils.getScreenWidth(getContext()) / 4;
                        ViewGroup.LayoutParams params = typeCategoryGridView.getLayoutParams();
                        params.width = columnWidth * mTypeCategoryDataList.size();
                        typeCategoryGridView.setLayoutParams(params);
                        typeCategoryGridView.setColumnWidth(columnWidth);
                        typeCategoryGridView.setNumColumns(mTypeCategoryDataList.size());

                        typeCategoryGridAdapter.notifyDataSetChanged();

                        isCategoryUpdated = true;
                        stopRefreshLayout();
                    }
                });

        // 精品课程
        OkHttpUtils.get()
                .url(HttpConstant.QUALITY_COURSE_URL)
                .build()
                .execute(new HttpCallBack<QualityCourseBean>(QualityCourseBean.class, false, getActivity()) {
                    @Override
                    public void onResponse(QualityCourseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData() != null) {
                            mQualityCourseList.clear();
                            mQualityCourseList.addAll(response.getData());
                        }

                        qualityCourseAdapter.notifyDataSetChanged();

                        isQualityUpdated = true;
                        stopRefreshLayout();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        refreshLayout.finishRefresh();
                    }
                });

        // 名师推荐接口
        OkHttpUtils.get()
                .url(HttpConstant.RECOMMEND_TEACHER_URL)
                .build()
                .execute(new HttpCallBack<RecommendTeacherBean>(RecommendTeacherBean.class, false, getActivity()) {
                    @Override
                    public void onResponse(RecommendTeacherBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData() != null) {
                            mTeacherRecommendList.clear();
                            mTeacherRecommendList.addAll(response.getData());
                        }

                        teacherRecommendAdapter.notifyDataSetChanged();

                        isRecommendUpdated = true;
                        stopRefreshLayout();
                    }
                });

        // 精品课堂接口
        OkHttpUtils.get()
                .url(HttpConstant.EXCELLENT_CLASS_URL)
                .build()
                .execute(new HttpCallBack<ExcellentClassBean>(ExcellentClassBean.class, false, getActivity()) {
                    @Override
                    public void onResponse(ExcellentClassBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData() != null) {
                            mExcellentClassList.clear();
                            mExcellentClassList.addAll(response.getData());
                        }

                        excellentClassAdapter.notifyDataSetChanged();

                        isExcellentUpdated = true;
                        stopRefreshLayout();
                    }
                });
    }

    private void stopRefreshLayout() {
        if (isAbsUpdated && isQualityUpdated && isRecommendUpdated && isExcellentUpdated && isCategoryUpdated) {
            refreshLayout.finishRefresh();

            isAbsUpdated = false;
            isQualityUpdated = false;
            isRecommendUpdated = false;
            isExcellentUpdated = false;
            isCategoryUpdated = false;
        }
    }

    //获取消息个数
    private void getMessageCountData() {
        OkHttpUtils.get()
                .url(HttpConstant.GET_MESSAGE_COUNT)
                .build()
                .execute(new HttpCallBack<GetMessageCountBean>(GetMessageCountBean.class, false, getActivity()) {
                    @Override
                    public void onResponse(GetMessageCountBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        int num = 0;
                        if (StringUtils.isNumeric(response.getData())) {
                            num = Integer.parseInt(response.getData());
                        }

                        redPointView.setRedPointCount(num);
                        if (HomeActivity.messageCount != null) {
                            HomeActivity.messageCount.onMessageCountChange(num);
                        }
                    }
                });
    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);

        if (netMobile == NetworkHelper.NETWORK_NONE) {
            netErrLayout.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);

//            mAbsDataList.clear();
//            mQualityCourseList.clear();
//            mTeacherRecommendList.clear();
//            mExcellentClassList.clear();
//            mTypeCategoryDataList.clear();
//
//            teacherRecommendAdapter.notifyDataSetChanged();
//            qualityCourseAdapter.notifyDataSetChanged();
//            excellentClassAdapter.notifyDataSetChanged();
//            typeCategoryGridAdapter.notifyDataSetChanged();
        } else {
            netErrLayout.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);

            //updateData();
        }
    }
}
