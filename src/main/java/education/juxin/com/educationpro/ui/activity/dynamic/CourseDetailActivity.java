package education.juxin.com.educationpro.ui.activity.dynamic;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import education.juxin.com.educationpro.ProConstant;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.bean.CourseDetailBean;
import education.juxin.com.educationpro.bean.IsCateBean;
import education.juxin.com.educationpro.bean.IsCourseCollectBean;
import education.juxin.com.educationpro.bean.QnVideoUrlBean;
import education.juxin.com.educationpro.bean.ReviewBean;
import education.juxin.com.educationpro.bean.TeacherBaseInfoBean;
import education.juxin.com.educationpro.dialog.ComTwnBtnDialog;
import education.juxin.com.educationpro.dialog.GoUpPopupDialog;
import education.juxin.com.educationpro.dialog.InvitationCodeDialog;
import education.juxin.com.educationpro.dialog.SelectRewardDialog;
import education.juxin.com.educationpro.dialog.SharedRuleDialog;
import education.juxin.com.educationpro.download.BigFileDownloadManager;
import education.juxin.com.educationpro.download.VideoCacheInfoData;
import education.juxin.com.educationpro.download.VideoCacheManager;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.http.NetworkHelper;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.interfaces.IRefreshUI;
import education.juxin.com.educationpro.party3.ijkplayer.interfaces.OnShowThumbnailListener;
import education.juxin.com.educationpro.party3.ijkplayer.player.PlayerView;
import education.juxin.com.educationpro.party3.ijkplayer.utils.PlayStateParams;
import education.juxin.com.educationpro.party3.umeng.ShareUtils;
import education.juxin.com.educationpro.ui.activity.home.OrderActivity;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.FormatTimeUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.CircleImageView;
import education.juxin.com.educationpro.view.CusWebView;
import education.juxin.com.educationpro.view.RecycleItemDecoration;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import static education.juxin.com.educationpro.http.NetworkHelper.NETWORK_MOBILE;
import static education.juxin.com.educationpro.http.NetworkHelper.NETWORK_WIFI;

/**
 * 课程的详情页面
 * create time 2018-3-23
 */
public class CourseDetailActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private final String[] permissionArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private final int STATE_PLAYER_VIDEO = 0;
    private final int STATE_DOWNLOAD_VIDEO = 1;

    private ArrayList<ReviewBean.ReviewData> mReviewHistoryList;
    private ArrayList<ReviewBean.ReviewData> mReviewAllList;
    private BaseRecyclerAdapter mReviewHistoryAdapter;
    private BaseRecyclerAdapter mReviewAllAdapter;

    private PowerManager.WakeLock wakeLock;
    private PlayerView mIjkPlayer;

    private GoUpPopupDialog mShareDialog;
    private SmartRefreshLayout refreshLayout;
    private View mTitleView;
    private Button mBtnCourseBuy;
    private TextView mCourseDescTv;
    private TextView mCoursePriceTv;
    private TextView mTeacherNameTv;
    private TextView mCourseTimeTv;
    private TextView mCourseEndTimeTv;
    private CusWebView mWebView;
    private Button mCareBtn;
    private Button mCollectBtn;
    private ImageView mPlayEndImg;
    private ImageButton headCache;
    private TextView backMoneyTv;
    private LinearLayout backMoneyLayout;
    private CircleImageView imgTeacherImg;
    private TextView tvTeacherName;
    private LinearLayout llLayout;
    private Button mShareBtn;
    private ImageButton headRewardBtn;

    private String mVideoUrl = "";
    private String mVideoTokenUrl = "";
    private String mCourseId = "";
    private String mIsCharges = "";
    private String mWhetherBuy = "";
    private String mCurCourseId = "";
    private String mTeacherId = "";
    private String mLessonId = "";
    private String mCourseEndData = "";
    private String mCourseTitle = "";
    private String mCoursePrice = "";
    private String mCourseCoverUrl = "";
    private String mCourseTotalNum = "";
    private String mWhetherLook = "";
    private String mTeacherName = "";
    private String htmlStr = "";

    private int mLoadMorePerNum;
    private int mLoadMorePageNum;
    private int mLoadMoreTotalNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        String courseId = getIntent().getStringExtra("id_course_detail");
        if (courseId != null && !courseId.isEmpty() && StringUtils.isNumeric(courseId)) {
            mCurCourseId = courseId;
        }
        String lessonId = getIntent().getStringExtra("lesson_id");
        if (lessonId != null && !lessonId.isEmpty() && StringUtils.isNumeric(lessonId)) {
            mLessonId = lessonId;
        }

        mReviewHistoryList = new ArrayList<>();
        mReviewAllList = new ArrayList<>();

        mLoadMorePerNum = 10;
        mLoadMorePageNum = 1;
        mLoadMoreTotalNum = 0;

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "VideoPlayLock");
        }

        initUI();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String courseId = getIntent().getStringExtra("id_course_detail");
        if (courseId != null && !courseId.isEmpty() && StringUtils.isNumeric(courseId)) {
            mCurCourseId = courseId;
        }
        String lessonId = getIntent().getStringExtra("lesson_id");
        if (lessonId != null && !lessonId.isEmpty() && StringUtils.isNumeric(lessonId)) {
            mLessonId = lessonId;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateCourseData();
        updateCourseAllHistoryList();

        // 只用在支付成功之后的传值
        String paySuccessFlag = (String) SPHelper.getSimpleParam(this, "pay_success_flag", "");
        if ("1".equals(paySuccessFlag)) {
            SPHelper.setSimpleKeyValue(this, "pay_success_flag", "0");

            String code = (String) SPHelper.getSimpleParam(this, "pay_success_invite_code", "");
            if (code != null && !code.trim().isEmpty()) {
                SPHelper.setSimpleKeyValue(this, "pay_success_invite_code", "");

                InvitationCodeDialog.Builder builder = new InvitationCodeDialog.Builder(this);
                builder.create(code, mCourseTitle, mCourseId, mLessonId).show();
            }
        }

        if (mIjkPlayer != null) {
            mIjkPlayer.onResume();
        }

        if (wakeLock != null && mIjkPlayer != null && mIjkPlayer.isPlaying()) {
            wakeLock.acquire();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mIjkPlayer != null) {
            mIjkPlayer.onPause();
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mIjkPlayer != null) {
            mIjkPlayer.onDestroy();
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (mTitleView != null) {
                mTitleView.setVisibility(View.VISIBLE);
            }
            if (mIjkPlayer != null) {
                mIjkPlayer.hideHideTopBar(true);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mTitleView != null) {
                mTitleView.setVisibility(View.GONE);
            }
            if (mIjkPlayer != null) {
                mIjkPlayer.hideHideTopBar(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mIjkPlayer != null && mIjkPlayer.onBackPressed()) {
            return;
        }
        super.onBackPressed();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void initUI() {
        // Title
        mTitleView = findViewById(R.id.head_course_detail);

        headRewardBtn = findViewById(R.id.head_reward);
        headRewardBtn.setEnabled(false);
        headRewardBtn.setOnClickListener(this);

        findViewById(R.id.head_back).setOnClickListener(this);

        headCache = findViewById(R.id.head_cache);
        headCache.setEnabled(false);
        headCache.setOnClickListener(this);

        mPlayEndImg = findViewById(R.id.play_end_img);
        mPlayEndImg.setEnabled(false);
        mPlayEndImg.setOnClickListener(this);

        backMoneyTv = findViewById(R.id.back_money_tv);
        backMoneyLayout = findViewById(R.id.back_money_root_layout);
        backMoneyLayout.setOnClickListener(this);

        mBtnCourseBuy = findViewById(R.id.btn_buy_course);
        mBtnCourseBuy.setEnabled(false);
        mBtnCourseBuy.setOnClickListener(this);

        initVideo();

        // SmartRefreshLayout
        refreshLayout = findViewById(R.id.root_refresh);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                updateCourseAllHistoryList();
            }
        });

        // 底部的“往期课程回顾”列表
        RecyclerView reviewAllHistoryRecycle = findViewById(R.id.review_all_recycler);
        LinearLayoutManager reviewAllManager = new LinearLayoutManager(this);
        reviewAllHistoryRecycle.addItemDecoration(new RecycleItemDecoration(this));
        reviewAllManager.setOrientation(LinearLayoutManager.VERTICAL);
        reviewAllHistoryRecycle.setLayoutManager(reviewAllManager);
        mReviewAllAdapter = new BaseRecyclerAdapter<ReviewBean.ReviewData>(R.layout.item_review_all_course, mReviewAllList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, ReviewBean.ReviewData itemData) {
                ImageView coursePictureImg = (ImageView) viewHolder.findViewById(R.id.course_picture_img);
                TextView courseTitleTv = (TextView) viewHolder.findViewById(R.id.course_title_tv);
                TextView teacherNameTv = (TextView) viewHolder.findViewById(R.id.course_teacher_tv);
                TextView courseTimeTv = (TextView) viewHolder.findViewById(R.id.course_time_tv);
                TextView timeTv = (TextView) viewHolder.findViewById(R.id.time_tv);

                ImageUtils.GlideUtil(CourseDetailActivity.this, itemData.getCoverImgUrl(), coursePictureImg);
                courseTitleTv.setText(itemData.getName());
                teacherNameTv.setText(String.format(getString(R.string.main_teacher_with_name), itemData.getMainTeacherName()));
                courseTimeTv.setText(String.format(getString(R.string.course_time_with_num), itemData.getCountLessonNum()));
                timeTv.setText(itemData.getUploadingTime());
            }
        };
        reviewAllHistoryRecycle.setAdapter(mReviewAllAdapter);
        mReviewAllAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (mReviewAllList.get(position - 1) != null) {
                    CourseDetailActivity.checkCourseValid(CourseDetailActivity.this,
                            mReviewAllList.get(position - 1).getCourseId(),
                            mReviewAllList.get(position - 1).getId(),
                            new ICheckCourseValid() {
                                @Override
                                public void isValid() {
                                    Intent intent = new Intent(CourseDetailActivity.this, CourseDetailActivity.class);
                                    intent.putExtra("id_course_detail", mReviewAllList.get(position - 1).getCourseId());
                                    intent.putExtra("lesson_id", mReviewAllList.get(position - 1).getId());
                                    startActivity(intent);
                                }

                                @Override
                                public void isInvalid() {
                                }
                            });
                }
            }
        });

        // 以下是RecyclerView Header
        View header = LayoutInflater.from(this).inflate(R.layout.item_header_course_detail, reviewAllHistoryRecycle, false);
        mReviewAllAdapter.setHeaderView(header);

        // 关注按钮
        mCareBtn = header.findViewById(R.id.care_btn);
        mCareBtn.setEnabled(false);
        mCareBtn.setTag(false);
        mCareBtn.setOnClickListener(this);

        // 收藏按钮
        mCollectBtn = header.findViewById(R.id.collect_btn);
        mCollectBtn.setEnabled(false);
        mCollectBtn.setTag(false);
        mCollectBtn.setOnClickListener(this);

        // 分享按钮
        mShareBtn = header.findViewById(R.id.share_btn);
        mShareBtn.setEnabled(false);
        mShareBtn.setTag(false);
        mShareBtn.setOnClickListener(this);

        mCourseDescTv = header.findViewById(R.id.course_info_tv);
        mCoursePriceTv = header.findViewById(R.id.price_tv);
        mTeacherNameTv = header.findViewById(R.id.teacher_name_tv);
        mCourseTimeTv = header.findViewById(R.id.course_time);
        mCourseEndTimeTv = header.findViewById(R.id.time_tv);
        llLayout = header.findViewById(R.id.ll_layout);
        mWebView = header.findViewById(R.id.course_introduce_web);
        imgTeacherImg = header.findViewById(R.id.img_teacher);
        tvTeacherName = header.findViewById(R.id.tv_teacher_name);

        // 中间的“最近更新”列表
        RecyclerView reviewHistoryRecycle = header.findViewById(R.id.review_recycler);
        LinearLayoutManager reviewManager = new LinearLayoutManager(this);
        reviewManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        reviewHistoryRecycle.setLayoutManager(reviewManager);
        mReviewHistoryAdapter = new BaseRecyclerAdapter<ReviewBean.ReviewData>(R.layout.item_review, mReviewHistoryList) {
            @Override
            public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, ReviewBean.ReviewData itemData) {
                ImageView coursePictureImg = (ImageView) viewHolder.findViewById(R.id.course_picture_img);
                TextView courseTitleTv = (TextView) viewHolder.findViewById(R.id.course_title_tv);

                ImageUtils.GlideUtil(CourseDetailActivity.this, itemData.getCoverImgUrl(), coursePictureImg);
                courseTitleTv.setText(itemData.getName());
            }
        };
        reviewHistoryRecycle.setAdapter(mReviewHistoryAdapter);
        mReviewHistoryAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (mReviewHistoryList.get(position) != null) {
                    CourseDetailActivity.checkCourseValid(CourseDetailActivity.this,
                            mReviewHistoryList.get(position).getCourseId(),
                            mReviewHistoryList.get(position).getId(),
                            new ICheckCourseValid() {
                                @Override
                                public void isValid() {
                                    Intent intent = new Intent(CourseDetailActivity.this, CourseDetailActivity.class);
                                    intent.putExtra("id_course_detail", mReviewHistoryList.get(position).getCourseId());
                                    intent.putExtra("lesson_id", mReviewHistoryList.get(position).getId());
                                    startActivity(intent);
                                }

                                @Override
                                public void isInvalid() {
                                }
                            });
                }
            }
        });
    }

    private void initVideo() {
        mIjkPlayer = new PlayerView(this, null);
        mIjkPlayer.setScaleType(PlayStateParams.FIT_PARENT);
        mIjkPlayer.hideHideTopBar(true);
        mIjkPlayer.forbidTouch(true);
        mIjkPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        String contentStr = "【" + mCourseTitle + "】，复制这条信息<￥>" + mCourseId + "&&" + mLessonId + "<￥>后打开→→乾途App←← App下载地址: " + ProConstant.APP_DOWNLOAD_URL;

        switch (v.getId()) {
            case R.id.head_back:
                if (mIjkPlayer != null) {
                    mIjkPlayer.backClick();
                } else {
                    finish();
                }
                break;

            case R.id.head_reward:
                SelectRewardDialog.Builder builder = new SelectRewardDialog.Builder(this);
                builder.setCourseId(mCourseId != null ? mCourseId : "");
                builder.setLesson(mLessonId != null ? mLessonId : "");
                builder.create().show();
                break;

            case R.id.head_cache:
                String token2 = String.valueOf(SPHelper.getSimpleParam(this, "token", ""));
                if (token2 == null || token2.trim().isEmpty()) {
                    ToastManager.showShortToast("请先登录！");
                    return;
                }
                if (NETWORK_MOBILE == NetworkHelper.getNetWorkState(CourseDetailActivity.this)) {
                    if ("0".equals(SPHelper.getSimpleParam(CourseDetailActivity.this, "noWifi", "0"))) {
                        checkDownloadPermissions();
                    } else {
                        show4gDownloadTipDialog();
                    }
                } else if (NETWORK_WIFI == NetworkHelper.getNetWorkState(CourseDetailActivity.this)) {
                    checkDownloadPermissions();
                }
                break;

            case R.id.btn_buy_course:
                Intent intent = new Intent(this, OrderActivity.class);
                intent.putExtra("course_id", mCourseId);
                intent.putExtra("course_if_charges", mIsCharges);
                intent.putExtra("course_cover_img_url", mCourseCoverUrl);
                intent.putExtra("course_title", mCourseTitle);
                intent.putExtra("curr_price", mCoursePrice);
                intent.putExtra("course_total_num", mCourseTotalNum);
                intent.putExtra("course_end_time", mCourseEndData);
                startActivity(intent);
                break;

            case R.id.play_end_img:
                if ("2".equals(mWhetherLook)) {
                    if (NETWORK_MOBILE == NetworkHelper.getNetWorkState(CourseDetailActivity.this)) {
                        if ("0".equals(SPHelper.getSimpleParam(CourseDetailActivity.this, "noWifi", "0"))) {
                            String token = String.valueOf(SPHelper.getSimpleParam(this, "token", ""));
                            if (token != null && !token.trim().isEmpty()) {
                                reqVideoUrl(STATE_PLAYER_VIDEO);
                            }
                        } else {
                            show4gPlayTipDialog();
                        }
                    } else if (NETWORK_WIFI == NetworkHelper.getNetWorkState(CourseDetailActivity.this)) {
                        String token = String.valueOf(SPHelper.getSimpleParam(this, "token", ""));
                        if (token != null && !token.trim().isEmpty()) {
                            reqVideoUrl(STATE_PLAYER_VIDEO);
                        }
                    }
                } else if ("1".equals(mWhetherLook)) {
                    String token = String.valueOf(SPHelper.getSimpleParam(this, "token", ""));
                    if (token != null && !token.trim().isEmpty()) {
                        ToastManager.showShortToast("请先购买课程！");
                    } else {
                        ToastManager.showShortToast("请先登录！");
                    }
                }
                break;

            case R.id.share_btn:
                View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_shared_ways, null);

                inflate.findViewById(R.id.tv_wechat).setOnClickListener(this);
                inflate.findViewById(R.id.tv_friends_circle).setOnClickListener(this);
                inflate.findViewById(R.id.tv_qq).setOnClickListener(this);
                inflate.findViewById(R.id.tv_sina).setOnClickListener(this);
                inflate.findViewById(R.id.share_cancel_btn).setOnClickListener(this);

                mShareDialog = new GoUpPopupDialog(this, inflate);
                break;

            case R.id.tv_friends_circle:
                ShareUtils.shareText(this, contentStr, SHARE_MEDIA.WEIXIN_CIRCLE);
                if (mShareDialog != null) {
                    mShareDialog.dismiss();
                }
                break;

            case R.id.tv_qq:
                ShareUtils.shareTxtToQQ(this, contentStr);
                if (mShareDialog != null) {
                    mShareDialog.dismiss();
                }
                break;

            case R.id.tv_sina:
                ShareUtils.shareText(this, contentStr, SHARE_MEDIA.SINA);
                if (mShareDialog != null) {
                    mShareDialog.dismiss();
                }
                break;

            case R.id.tv_wechat:
                android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    cm.setPrimaryClip(ClipData.newPlainText("sharedContentStr", contentStr));
                    ToastManager.showShortToast("复制成功！");

                    try {
                        ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                        Intent intentToWx = new Intent();
                        intentToWx.setAction(Intent.ACTION_MAIN);
                        intentToWx.addCategory(Intent.CATEGORY_LAUNCHER);
                        intentToWx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentToWx.setComponent(cmp);
                        startActivity(intentToWx);
                    } catch (ActivityNotFoundException e) {
                        ToastManager.showShortToast("您的手机上没有微信客户端");
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (mShareDialog != null) {
                    mShareDialog.dismiss();
                }
                break;

            case R.id.share_cancel_btn:
                if (mShareDialog != null) {
                    mShareDialog.dismiss();
                }
                break;

            case R.id.care_btn:
                if (!(boolean) mCareBtn.getTag()) {
                    userCare();
                } else {
                    userCancelCare();
                }
                break;

            case R.id.collect_btn:
                if (!(boolean) mCollectBtn.getTag()) {
                    courseAddCollect();
                } else {
                    courseCancelCollect();
                }
                break;

            case R.id.back_money_root_layout:
                SharedRuleDialog.Builder sharedRuleBuilder = new SharedRuleDialog.Builder(this);
                sharedRuleBuilder.create().show();
                break;

            default:
                break;
        }
    }

    private void setCareBtnState(boolean isChecked) {
        mCareBtn.setTag(isChecked);
        if ((boolean) mCareBtn.getTag()) {
            mCareBtn.setText("已关注");
            mCareBtn.setTextColor(getResources().getColor(R.color.bg_white));
            mCareBtn.setBackgroundResource(R.drawable.login);
        } else {
            mCareBtn.setText("+关注");
            mCareBtn.setTextColor(getResources().getColor(R.color.green_bg));
            mCareBtn.setBackgroundResource(R.drawable.concern_green_bg);
        }
    }

    private void setCollectBtnState(boolean isChecked) {
        mCollectBtn.setTag(isChecked);
        if ((boolean) mCollectBtn.getTag()) {
            mCollectBtn.setBackgroundResource(R.drawable.collect_selected);
        } else {
            mCollectBtn.setBackgroundResource(R.drawable.collect);
        }
    }

    private void downloadVideoCache() {
        String cacheName = "qt_" + mCourseId + "_" + mLessonId;
        String downPath = VideoCacheManager.checkCacheVideoPath(cacheName);

        if (VideoCacheManager.isExistsVideo(this, downPath)) {
            ToastManager.showShortToast("课程已下载");
            return;
        }

        VideoCacheInfoData data = new VideoCacheInfoData();
        data.setCacheFileName(downPath);
        data.setCourseCoverImg(mCourseCoverUrl);
        data.setTitle(mCourseTitle);
        data.setMainTeacherName(mTeacherName);
        data.setCourseEndDate(mCourseEndData);
        data.setCurrentLessonNum(mCourseTotalNum);

        new BigFileDownloadManager(this, data, headCache).downStart(mVideoTokenUrl, downPath, "确认下载");
    }

    private void show4gPlayTipDialog() {
        final ComTwnBtnDialog collectDialog = new ComTwnBtnDialog(CourseDetailActivity.this, ComTwnBtnDialog.DIALOG_4G_PLAY);
        collectDialog.setDialogBtnClickListener(new ComTwnBtnDialog.IDialogBtnClickListener() {
            @Override
            public void onDialogLeftBtnClick() {
            }

            @Override
            public void onDialogRightBtnClick() {
                String token = String.valueOf(SPHelper.getSimpleParam(CourseDetailActivity.this, "token", ""));
                if (token != null && !token.trim().isEmpty()) {
                    reqVideoUrl(STATE_PLAYER_VIDEO);
                }
            }
        });
        collectDialog.show();
    }

    private void show4gDownloadTipDialog() {
        final ComTwnBtnDialog collectDialog = new ComTwnBtnDialog(CourseDetailActivity.this, ComTwnBtnDialog.DIALOG_4G_DOWNLOAD);
        collectDialog.setDialogBtnClickListener(new ComTwnBtnDialog.IDialogBtnClickListener() {
            @Override
            public void onDialogLeftBtnClick() {
            }

            @Override
            public void onDialogRightBtnClick() {
                checkDownloadPermissions();
            }
        });
        collectDialog.show();
    }

    private void startPlayVideo() {
        mIjkPlayer.setPlaySource("标清", mVideoTokenUrl);
        mIjkPlayer.startPlay();
        mIjkPlayer.forbidTouch(false);

        mPlayEndImg.setVisibility(View.GONE);
        backMoneyLayout.setVisibility(View.GONE);

        if (wakeLock != null) {
            wakeLock.acquire();
        }

        reqLookCourseSave();
    }

    private void checkDownloadPermissions() {
        if (EasyPermissions.hasPermissions(this, permissionArr)) {
            headCache.setEnabled(false);
            reqVideoUrl(STATE_DOWNLOAD_VIDEO);
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.get_photo_permission_tip), 0x11, permissionArr);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        headCache.setEnabled(false);
        reqVideoUrl(STATE_DOWNLOAD_VIDEO);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).setRationale(R.string.get_permission_tip).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // ======================================== 后台接口部分 ======================================== //

    /**
     * 进入前先检查课程有效性，然后再进来
     */
    public static void checkCourseValid(Activity activity, String courseId, String lessonId, ICheckCourseValid isValidCallback) {
        OkHttpUtils.get()
                .url(HttpConstant.COURSE_DETAILS + courseId)
                .addParams("lessonId", lessonId)
                .build()
                .execute(new HttpCallBack<CourseDetailBean>(CourseDetailBean.class, true, activity) {
                    @Override
                    public void onResponse(CourseDetailBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());

                            if (isValidCallback != null) {
                                isValidCallback.isInvalid();
                            }
                            return;
                        }

                        if (isValidCallback != null) {
                            isValidCallback.isValid();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        if (isValidCallback != null) {
                            isValidCallback.isInvalid();
                        }
                    }
                });
    }

    /**
     * 课程信息相关接口
     */
    private void updateCourseData() {
        OkHttpUtils.get()
                .url(HttpConstant.COURSE_DETAILS + mCurCourseId)
                .addParams("lessonId", mLessonId)
                .build()
                .execute(new HttpCallBack<CourseDetailBean>(CourseDetailBean.class, true, this) {
                    @Override
                    public void onResponse(CourseDetailBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        CourseDetailBean.CourseDetailData detailData = response.getData();

                        //课程标题
                        mCourseTitle = detailData.getTitle();
                        mCourseDescTv.setText(mCourseTitle);
                        //课程封面图
                        mIjkPlayer.showThumbnail(new OnShowThumbnailListener() {
                            @Override
                            public void onShowThumbnail(ImageView ivThumbnail) {
                                ImageUtils.GlideUtil(CourseDetailActivity.this, detailData.getCourseCoverImg(), ivThumbnail);
                            }
                        });
                        //课程结束日期
                        String courseEndDate = detailData.getCourseEndDate();
                        String courseTimeStamp = FormatTimeUtil.formatDateStr2TimeStamp(courseEndDate, "yyyy-MM-dd HH:mm:ss");
                        String yearMonthTime = FormatTimeUtil.formatTimeStamp2DateStr(courseTimeStamp, "yyyy-MM-dd");
                        mCourseEndTimeTv.setText(String.format(getString(R.string.effective_deadline), yearMonthTime));
                        //主讲老师名称
                        mTeacherName = detailData.getMainTeacherName();
                        mTeacherNameTv.setText(String.format(getString(R.string.main_teacher_name), mTeacherName));
                        //价格
                        mCoursePriceTv.setText(String.format(getString(R.string.money_only_with_number), FormatNumberUtil.doubleFormat(detailData.getPrice())));
                        //当前课时 | 总课节
                        mCourseTimeTv.setText(String.format(getString(R.string.course_time_with_number), Integer.valueOf(detailData.getCurrentLessonNum()), Integer.valueOf(detailData.getCountLesson())));
                        //课程详情
                        htmlStr = detailData.getCourseDetail();
                        mWebView.loadHtmlString(htmlStr != null && !htmlStr.trim().isEmpty() ? htmlStr : "暂无");
                        //是否收费
                        mIsCharges = detailData.getIfcharges();
                        //获取老师ID
                        mTeacherId = detailData.getTeacherId();
                        //课节
                        mLessonId = detailData.getLessonId();
                        //截止日期
                        mCourseEndData = detailData.getCourseEndDate();
                        //价格
                        mCoursePrice = detailData.getPrice();
                        //封面图
                        mCourseCoverUrl = detailData.getCourseCoverImg();
                        //总课节
                        mCourseTotalNum = detailData.getCountLesson();
                        //课程id
                        mCourseId = detailData.getId();
                        //是否已经购买该课程
                        mWhetherBuy = detailData.getWhetherBuy();
                        //是否可以观看视频
                        mWhetherLook = detailData.getWhetherLook();
                        //返回金额
                        backMoneyTv.setText(String.format(getString(R.string.number_yuan), FormatNumberUtil.doubleFormat(detailData.getReturnPrice())));
                        //videoUrl（区别于：videoTokenUrl）
                        mVideoUrl = detailData.getVideoUrl();

                        mCareBtn.setEnabled(true);
                        mCollectBtn.setEnabled(true);
                        mBtnCourseBuy.setEnabled(true);
                        mPlayEndImg.setEnabled(true);
                        headRewardBtn.setEnabled(true);
                        headCache.setEnabled(true);
                        mShareBtn.setEnabled(true);

                        if ("0".equals(mIsCharges)) {
                            mBtnCourseBuy.setVisibility(View.GONE);
                        } else {
                            mBtnCourseBuy.setVisibility(View.VISIBLE);
                        }

                        if ("2".equals(mWhetherBuy)) {
                            mBtnCourseBuy.setBackgroundColor(getResources().getColor(R.color.grey));
                            mBtnCourseBuy.setText("课程已购买");
                            mBtnCourseBuy.setEnabled(false);
                        } else {
                            mBtnCourseBuy.setBackgroundColor(getResources().getColor(R.color.green_bg));
                            mBtnCourseBuy.setText("系列课程购买");
                        }

                        if ("0".equals(mIsCharges) || "2".equals(mWhetherBuy)) {
                            headCache.setVisibility(View.VISIBLE);
                        } else {
                            headCache.setVisibility(View.GONE);
                        }

                        if ("0".equals(mIsCharges) && "1".equals(SPHelper.getSimpleParam(CourseDetailActivity.this, "autoPlay", "0"))) {
                            if (NETWORK_MOBILE == NetworkHelper.getNetWorkState(CourseDetailActivity.this)) {
                                if ("0".equals(SPHelper.getSimpleParam(CourseDetailActivity.this, "noWifi", "0"))) {
                                    String token = String.valueOf(SPHelper.getSimpleParam(CourseDetailActivity.this, "token", ""));
                                    if (token != null && !token.trim().isEmpty()) {
                                        reqVideoUrl(STATE_PLAYER_VIDEO);
                                    }
                                } else {
                                    //show4gPlayTipDialog();
                                }
                            } else if (NETWORK_WIFI == NetworkHelper.getNetWorkState(CourseDetailActivity.this)) {
                                String token = String.valueOf(SPHelper.getSimpleParam(CourseDetailActivity.this, "token", ""));
                                if (token != null && !token.trim().isEmpty()) {
                                    reqVideoUrl(STATE_PLAYER_VIDEO);
                                }
                            }
                        }

                        /*
                         * 继续请求接口
                         */
                        updateCourseHistoryList();
                        updateTeacherData();

                        String token = String.valueOf(SPHelper.getSimpleParam(CourseDetailActivity.this, "token", ""));
                        if (token != null && !token.trim().isEmpty()) {
                            isCareData();
                            isCollect();
                        }
                    }
                });
    }

    /**
     * 底部往期课程回顾接口
     */
    private void updateCourseAllHistoryList() {
        OkHttpUtils.get()
                .url(HttpConstant.COURSE_DETAILS_ALL_LIST)
                .addParams("courseId", mCurCourseId)
                .addParams("page", String.valueOf(mLoadMorePageNum))
                .addParams("rows", String.valueOf(mLoadMorePerNum))
                .build()
                .execute(new HttpCallBack<ReviewBean>(ReviewBean.class, false, this) {
                    @Override
                    public void onResponse(ReviewBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        mLoadMoreTotalNum = response.getTotal() != null ? Integer.valueOf(response.getTotal()) : 0;
                        if (mLoadMoreTotalNum > mLoadMorePageNum * mLoadMorePerNum) {
                            ++mLoadMorePageNum;
                        }

                        if (response.getData() != null && mLoadMoreTotalNum > mReviewAllList.size()) {
                            mReviewAllList.addAll(response.getData());
                            mReviewAllAdapter.notifyDataSetChanged();
                        }

                        refreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadMore();
                    }
                });
    }

    /**
     * 中间的最近更新接口，策划规定最多显示10个
     */
    private void updateCourseHistoryList() {
        OkHttpUtils.get()
                .url(HttpConstant.COURSE_DETAILS_HISTORY_LIST)
                .addParams("courseId", mCurCourseId)
                .build()
                .execute(new HttpCallBack<ReviewBean>(ReviewBean.class, true, this) {
                    @Override
                    public void onResponse(ReviewBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData() != null) {
                            mReviewHistoryList.clear();
                            mReviewHistoryList.addAll(response.getData());
                        }

                        mReviewHistoryAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 老师基本信息
     */
    private void updateTeacherData() {
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

                        ImageUtils.GlideUtil(CourseDetailActivity.this, response.getData().getHeadImgUrl(), imgTeacherImg);
                        tvTeacherName.setText(response.getData().getName());
                        llLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intents = new Intent(CourseDetailActivity.this, SpaceMainActivity.class);
                                intents.putExtra("teacherId", response.getData().getId());
                                startActivity(intents);
                            }
                        });
                    }
                });
    }

    /**
     * 判断该课程是否被关注
     */
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

    /**
     * 添加关注接口信息
     */
    private void userCare() {
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

    /**
     * 取消关注接口信息
     */
    private void userCancelCare() {
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

    /**
     * 判断该课程是否被收藏
     */
    private void isCollect() {
        OkHttpUtils.get()
                .url(HttpConstant.IS_COURSE_COLLECT)
                .addParams("courseId", mCurCourseId)
                .build()
                .execute(new HttpCallBack<IsCourseCollectBean>(IsCourseCollectBean.class, true, this) {
                    @Override
                    public void onResponse(final IsCourseCollectBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        setCollectBtnState(response.isData());
                    }
                });
    }

    /**
     * 添加收藏接口
     */
    private void courseAddCollect() {
        OkHttpUtils.post()
                .url(HttpConstant.COURSE_COLLECT)
                .addParams("courseId", mCurCourseId)
                .build()
                .execute(new HttpCallBack<IsCourseCollectBean>(IsCourseCollectBean.class, true, this) {
                    @Override
                    public void onResponse(IsCourseCollectBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                        ToastManager.showShortToast("收藏成功！");
                        setCollectBtnState(true);
                    }
                });
    }

    /**
     * 取消收藏接口
     */
    private void courseCancelCollect() {
        OkHttpUtils.post()
                .url(HttpConstant.COURSE_CANCEL_COLLECT)
                .addParams("courseId", mCurCourseId)
                .build()
                .execute(new HttpCallBack<IsCourseCollectBean>(IsCourseCollectBean.class, true, this) {
                    @Override
                    public void onResponse(IsCourseCollectBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                        ToastManager.showShortToast("已取消收藏！");
                        setCollectBtnState(false);
                    }
                });
    }

    /**
     * 保存观看记录接口
     */
    private void reqLookCourseSave() {
        OkHttpUtils.get()
                .url(HttpConstant.LOOK_COURSE_SAVE)
                .addParams("courseId", mCurCourseId)
                .addParams("lessonId", mLessonId)
                .addParams("wachTime", String.valueOf(System.currentTimeMillis() / 1000))
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                    }
                });
    }

    /**
     * 获取视频加密连接地址
     */
    private void reqVideoUrl(int state) {
        OkHttpUtils.get()
                .url(HttpConstant.ENCRYPT_VIDEO_URL)
                .addParams("url", mVideoUrl)
                .build()
                .execute(new HttpCallBack<QnVideoUrlBean>(QnVideoUrlBean.class, true, this) {
                    @Override
                    public void onResponse(QnVideoUrlBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                        mVideoTokenUrl = response.getData();

                        switch (state) {
                            case STATE_PLAYER_VIDEO:
                                startPlayVideo();
                                break;

                            case STATE_DOWNLOAD_VIDEO:
                                downloadVideoCache();
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        headCache.setEnabled(true);
                    }
                });
    }
}
