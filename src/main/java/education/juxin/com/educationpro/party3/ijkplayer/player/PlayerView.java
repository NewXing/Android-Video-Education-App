package education.juxin.com.educationpro.party3.ijkplayer.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.party3.ijkplayer.interfaces.OnControlVisibilityChangeListener;
import education.juxin.com.educationpro.party3.ijkplayer.interfaces.OnPlayerBackListener;
import education.juxin.com.educationpro.party3.ijkplayer.interfaces.OnShowThumbnailListener;
import education.juxin.com.educationpro.party3.ijkplayer.utils.LayoutQuery;
import education.juxin.com.educationpro.party3.ijkplayer.utils.NetworkUtils;
import education.juxin.com.educationpro.party3.ijkplayer.utils.PlayStateParams;
import education.juxin.com.educationpro.party3.ijkplayer.utils.VideoIjkBean;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerView {
    // 通知使用的flags
    private static final int MESSAGE_SHOW_PROGRESS = 1; // 同步进度
    private static final int MESSAGE_SEEK_NEW_POSITION = 3; // 设置新位置
    private static final int MESSAGE_HIDE_CENTER_BOX = 4; // 隐藏提示的box
    private static final int MESSAGE_RESTART_PLAY = 5; // 重新播放

    // 控件部分
    private final Activity mActivity; // 依附的容器Activity
    private final LayoutQuery mLayoutQuery; // 界面的中布局的查询器
    private final CusIjkView mIjkView; // 原生IjkPlayer
    private final View mTopBarView; // 播放器顶部控制bar
    private final View mBottomBarView; // 播放器底部控制bar
    private final ImageView mThumbnailImg; // 播放前的封面或缩略图
    private final ImageView mBackImage; // 视频返回按钮
    private final ImageView mBottomBarPlayerImage; // 视频bottomBar的播放按钮
    private final ImageView mCenterPlayerImage; // 视频中间的播放按钮
    private final ImageView mFullScreenImage; // 视频全屏按钮
    private final TextView mPlayerSpeedTv; // 视频加载速度
    private final SeekBar mProgressSeekBar; // 视频播放进度条

    // 视频相关参数
    private List<VideoIjkBean> mListVideos = new ArrayList<>(); // 码流列表
    private int mCurrStatus = PlayStateParams.STATE_IDLE; // 当前状态
    private int mCurrShowType = PlayStateParams.FIT_PARENT; // 视频显示比例,默认保持原视频的大小
    private final AudioManager mAudioManager; // 音频管理器
    private String mCurrVideoUrl; // 当前播放地址
    private int mCurrPlayPosition; // 当前播放位置
    private long mNewPosition = -1; // 滑动进度条得到的新位置，和当前播放位置是有区别的,mNewPosition =0也会调用设置的，故初始化值为-1
    private int mCurrVolume; // 当前声音大小
    private int mMaxVolume = 0; // 设备最大音量
    private final int mScreenWidthPix; // 获取当前设备的宽度
    private final int mPlayerViewInitHeight; // 记录播放器竖屏时的高度
    private float mCurrBrightness; // 当前亮度大小
    private int mBgState; // 记录进行后台时的播放状态0为播放，1为暂停
    private int mAutoConnectTime = 5000; // 自动重连的时间
    private boolean mIsPlayerSupport; // 第三方so是否支持，默认不支持，true为支持
    private boolean mIsLive; // 是否是直播，默认为非直播，true为直播false为点播。mIsLive()方法判断直播的方式比较片面
    private boolean mIsForbidTouch; // 禁止触摸，默认可以触摸，true为禁止false为可触摸
    private boolean mIsForbidHideControlPanel; // 禁止收起控制面板，默认可以收起，true为禁止false为可触摸
    private boolean mIsForbidDoubleUp; // 是否禁止双击，默认不禁止，true为禁止，false为不禁止
    private boolean mIsDragging; // 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动
    private boolean mIsErrorStop = true; // 是否出错停止播放，默认是出错停止播放，true出错停止播放,false为用户点击停止播放
    private boolean mIsOnlyFullScreen; // 是否只有全屏，默认非全屏，true为全屏，false为非全屏
    private boolean mIsPortrait = true; // 是否是竖屏，默认为竖屏，true为竖屏，false为横屏
    private boolean mIsAutoReConnect = true; // 是否自动重连，默认5秒重连，true为重连，false为不重连
    private boolean mIsHideCenterPlayer; // 是否隐藏中间播放按钮，默认不隐藏，true为隐藏，false为不隐藏
    private boolean mIsHideTopBar; // 是否隐藏topBar，true为隐藏，false为不隐藏
    private boolean mIsHideBottomBar; // 是否隐藏bottomBar，true为隐藏，false为不隐藏
    private boolean mIsShowControlPanel; // 是否显示控制面板，默认为隐藏，true为显示false为隐藏
    private boolean mIsCharge; // 是否免费观看
    private int mMaxPlaytime; // 设置最大播放时间
    private boolean mIsHasSwitchStream; // 当前是否切换视频流，默认为否，true是切换视频流，false没有切换
    private boolean mIsNetWorkTip; // 播放时是否有网络提示，true为显示网络提示，false不显示网络提示
    private boolean mIsFirstPlayer = true; // 是否是第一次播放

    // 视频和UI的回调监听
    private Handler mHandler = new CusUiHandler();
    private AutoPlayRunnable mAutoPlayRunnable = new AutoPlayRunnable(); // 控制面板收起或者显示的轮询监听
    private OnPlayerBackListener mPlayerBack; // 视频的返回键监听
    private IMediaPlayer.OnInfoListener mOnInfoListener; // 视频播放时信息回调
    private OrientationEventListener mOrientationEventListener; // Activity界面方向监听
    private OnControlVisibilityChangeListener mOnControlVisibilityChangeListener; // 控制面板显示或隐藏监听

    // ========================================== 初始化 ========================================== //

    public PlayerView(Activity activity, View rootView) {
        this.mActivity = activity;

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            mIsPlayerSupport = true;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            int e = Settings.System.getInt(mActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            float progress = 1.0F * (float) e / 255.0F;
            WindowManager.LayoutParams layout = this.mActivity.getWindow().getAttributes();
            layout.screenBrightness = progress;
            mActivity.getWindow().setAttributes(layout);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }

        // 播放器整个界面
        View rootBoxRL;
        if (rootView == null) {
            mLayoutQuery = new LayoutQuery(mActivity);
            rootBoxRL = mActivity.findViewById(R.id.app_video_box);
            mIjkView = mActivity.findViewById(R.id.video_view);

            mTopBarView = mActivity.findViewById(R.id.app_video_top_box);
            mBottomBarView = mActivity.findViewById(R.id.ll_bottom_bar);
            mThumbnailImg = mActivity.findViewById(R.id.iv_trumb);
            mBackImage = mActivity.findViewById(R.id.app_video_finish);
            mBottomBarPlayerImage = mActivity.findViewById(R.id.app_video_play);
            mCenterPlayerImage = mActivity.findViewById(R.id.play_icon);
            mFullScreenImage = mActivity.findViewById(R.id.app_video_fullscreen);
            mPlayerSpeedTv = mActivity.findViewById(R.id.app_video_speed);
            mProgressSeekBar = mActivity.findViewById(R.id.app_video_seekBar);
            mProgressSeekBar.setMax(1000);
        } else {
            mLayoutQuery = new LayoutQuery(mActivity, rootView);
            rootBoxRL = rootView.findViewById(R.id.app_video_box);
            mIjkView = rootView.findViewById(R.id.video_view);

            mTopBarView = rootView.findViewById(R.id.app_video_top_box);
            mBottomBarView = rootView.findViewById(R.id.ll_bottom_bar);
            mThumbnailImg = rootView.findViewById(R.id.iv_trumb);
            mBackImage = rootView.findViewById(R.id.app_video_finish);
            mBottomBarPlayerImage = rootView.findViewById(R.id.app_video_play);
            mCenterPlayerImage = rootView.findViewById(R.id.play_icon);
            mFullScreenImage = rootView.findViewById(R.id.app_video_fullscreen);
            mPlayerSpeedTv = rootView.findViewById(R.id.app_video_speed);
            mProgressSeekBar = rootView.findViewById(R.id.app_video_seekBar);
            mProgressSeekBar.setMax(1000);
        }

        mProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long duration = getDuration();
                    int position = (int) ((duration * progress * 1.0) / 1000);
                    String time = generateTime(position);
                    mLayoutQuery.id(R.id.app_video_currentTime).text(time);
                    mLayoutQuery.id(R.id.app_video_currentTime_full).text(time);
                    mLayoutQuery.id(R.id.app_video_currentTime_left).text(time);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsDragging = true;
                mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                long duration = getDuration();
                mIjkView.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
                mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
                mIsDragging = false;
                mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.app_video_fullscreen) {
                    toggleFullScreen();
                } else if (v.getId() == R.id.app_video_play || v.getId() == R.id.play_icon) {
                    if (mIjkView.isPlaying()) {
                        if (mIsLive) {
                            mIjkView.stopPlayback();
                        } else {
                            pausePlay();
                        }
                    } else {
                        startPlay();
                        if (mIjkView.isPlaying()) {
                            // TODO ijkPlayer内部的监听没有回调，只能手动修改状态
                            mCurrStatus = PlayStateParams.STATE_PREPARING;
                            hideStatusUI();
                        }
                    }
                    updatePausePlay();
                } else if (v.getId() == R.id.app_video_finish) {
                    backClick();
                } else if (v.getId() == R.id.app_video_netTie_icon) {
                    // 使用移动网络提示继续播放
                    mIsNetWorkTip = false;
                    hideStatusUI();
                    startPlay();
                    updatePausePlay();
                } else if (v.getId() == R.id.app_video_replay_icon) {
                    // 重新播放
                    mCurrStatus = PlayStateParams.STATE_ERROR;
                    hideStatusUI();
                    startPlay();
                    updatePausePlay();
                }
            }
        };

        mBottomBarPlayerImage.setOnClickListener(onClickListener);
        mCenterPlayerImage.setOnClickListener(onClickListener);
        mFullScreenImage.setOnClickListener(onClickListener);
        mBackImage.setOnClickListener(onClickListener);
        mLayoutQuery.id(R.id.app_video_netTie_icon).clicked(onClickListener);
        mLayoutQuery.id(R.id.app_video_replay_icon).clicked(onClickListener);

        mIjkView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                if (what == PlayStateParams.MEDIA_INFO_NETWORK_BANDWIDTH || what == PlayStateParams.MEDIA_INFO_BUFFERING_BYTES_UPDATE) {
                    if (mPlayerSpeedTv != null) {
                        mPlayerSpeedTv.setText(getFormatSize(extra));
                    }
                }
                statusChange(what);
                if (mOnInfoListener != null) {
                    mOnInfoListener.onInfo(mp, what, extra);
                }
                if (mIsCharge && mMaxPlaytime < getCurrPlayPosition()) {
                    mLayoutQuery.id(R.id.app_video_freeTie).visible();
                    pausePlay();
                }
                return true;
            }
        });

        final GestureDetector detector = new GestureDetector(mActivity, new PlayerGestureListener());
        rootBoxRL.setClickable(true);
        rootBoxRL.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (mAutoPlayRunnable != null) {
                            mAutoPlayRunnable.stop();
                        }
                        break;
                }
                if (detector.onTouchEvent(motionEvent)) {
                    return true;
                }
                // 处理手势结束
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mOrientationEventListener = new OrientationEventListener(mActivity) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
                    if (mIsPortrait) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        mOrientationEventListener.disable();
                    }
                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
                    if (!mIsPortrait) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        mOrientationEventListener.disable();
                    }
                }
            }
        };

        if (mIsOnlyFullScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        mIsPortrait = (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPlayerViewInitHeight = rootBoxRL.getLayoutParams().height;
        mScreenWidthPix = mActivity.getResources().getDisplayMetrics().widthPixels;

        hideAllUI();

        if (!mIsPlayerSupport) {
            showStatus(mActivity.getResources().getString(R.string.not_support));
        } else {
            mLayoutQuery.id(R.id.ll_bg).visible();
        }
    }

    // ========================================= 生命周期 ========================================= //

    public void onPause() {
        mBgState = (mIjkView.isPlaying() ? 0 : 1);
        getCurrPlayPosition();
        mIjkView.onPause();
    }

    public void onResume() {
        mIjkView.onResume();
        if (mIsLive) {
            mIjkView.seekTo(0);
        } else {
            mIjkView.seekTo(mCurrPlayPosition);
        }
        if (mBgState != 0) {
            pausePlay();
        }
    }

    public void onDestroy() {
        mOrientationEventListener.disable();
        mHandler.removeMessages(MESSAGE_RESTART_PLAY);
        mHandler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
        mIjkView.stopPlayback();
    }

    public void onConfigurationChanged(final Configuration newConfig) {
        mIsPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(mIsPortrait);
    }

    public boolean onBackPressed() {
        if (!mIsOnlyFullScreen && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            doOnNoAllScreen();
            return true;
        }
        return false;
    }

    // ========================================= 对外接口 ========================================= //

    /**
     * 设置播放信息监听回调
     */
    public void setOnInfoListener(IMediaPlayer.OnInfoListener onInfoListener) {
        this.mOnInfoListener = onInfoListener;
    }

    /**
     * 设置播放器中的返回键监听
     */
    public void setPlayerBackListener(OnPlayerBackListener listener) {
        this.mPlayerBack = listener;
    }

    /**
     * 设置控制面板显示隐藏监听
     */
    public void setOnControlPanelVisibilityChangListenter(OnControlVisibilityChangeListener listener) {
        this.mOnControlVisibilityChangeListener = listener;
    }

    /**
     * 显示缩略图
     */
    public void showThumbnail(OnShowThumbnailListener onShowThumbnailListener) {
        if (onShowThumbnailListener != null && mThumbnailImg != null) {
            onShowThumbnailListener.onShowThumbnail(mThumbnailImg);
        }
    }

    /**
     * 设置播放地址
     */
    public void setPlaySource(String stream, String url) {
        VideoIjkBean mVideoIjkBean = new VideoIjkBean();
        mVideoIjkBean.setStream(stream);
        mVideoIjkBean.setUrl(url);
        setPlaySource(mVideoIjkBean);
    }

    /**
     * 设置播放地址
     */
    public void setPlaySource(VideoIjkBean videoIjkBean) {
        mListVideos.clear();
        if (videoIjkBean != null) {
            mListVideos.add(videoIjkBean);

            if (mListVideos.size() > 0) {
                mCurrVideoUrl = mListVideos.get(0).getUrl();
                mListVideos.get(0).setSelect(true);
                isLive();
                if (mIjkView.isPlaying()) {
                    getCurrPlayPosition();
                    mIjkView.release(false);
                }
                mIsHasSwitchStream = true;
            }
        }
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        if (mIsLive) {
            mIjkView.setVideoPath(mCurrVideoUrl);
            mIjkView.seekTo(0);
        } else {
            if (mIsHasSwitchStream || mCurrStatus == PlayStateParams.STATE_ERROR) {
                // TODO 换源之后声音可播，画面卡住，主要是渲染问题，目前只是提供了软解方式，后期提供设置方式
                mIjkView.setRender(CusIjkView.RENDER_TEXTURE_VIEW);
                mIjkView.setVideoPath(mCurrVideoUrl);
                mIjkView.seekTo(mCurrPlayPosition);
                mIsHasSwitchStream = false;
            }
        }
        hideStatusUI();
        if (mIsNetWorkTip && (NetworkUtils.getNetworkType(mActivity) == 4
                || NetworkUtils.getNetworkType(mActivity) == 5
                || NetworkUtils.getNetworkType(mActivity) == 6)) {
            mLayoutQuery.id(R.id.app_video_netTie).visible();
        } else {
            if (mIsCharge && mMaxPlaytime < getCurrPlayPosition()) {
                mLayoutQuery.id(R.id.app_video_freeTie).visible();
            } else {
                if (mIsPlayerSupport) {
                    mIjkView.start();
                    if (mIsFirstPlayer) {
                        mLayoutQuery.id(R.id.app_video_loading).visible();
                        mIsFirstPlayer = false;
                    }
                } else {
                    showStatus(mActivity.getResources().getString(R.string.not_support));
                }
            }
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        mCurrStatus = PlayStateParams.STATE_PAUSED;
        getCurrPlayPosition();
        mIjkView.pause();
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        mIjkView.stopPlayback();
        mIsErrorStop = true;
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_RESTART_PLAY);
        }
    }

    public boolean isPlaying() {
        return mIjkView.isPlaying();
    }

    public void backClick() {
        if (!mIsOnlyFullScreen && !mIsPortrait) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            doOnNoAllScreen();
        } else {
            if (mPlayerBack != null) {
                mPlayerBack.onPlayerBack();
            } else {
                mActivity.finish();
            }
        }
    }

    /**
     * 设置播放位置
     */
    public void seekTo(int playtime) {
        mIjkView.seekTo(playtime);
    }

    /**
     * 获取当前播放位置
     */
    public int getCurrPlayPosition() {
        if (!mIsLive) {
            mCurrPlayPosition = mIjkView.getCurrentPosition();
        } else {
            mCurrPlayPosition = -1;
        }
        return mCurrPlayPosition;
    }

    /**
     * 获取视频播放总时长
     */
    public long getDuration() {
        return (long) mIjkView.getDuration();
    }

    /**
     * 百分比显示切换
     */
    public void toggleAspectRatio() {
        if (mIjkView != null) {
            mIjkView.toggleAspectRatio();
        }
    }

    /**
     * 设置视频名称
     */
    public void setTitle(String title) {
        mLayoutQuery.id(R.id.app_video_title).text(title);
    }

    /**
     * 设置最大观看时长
     *
     * @param isCharge    true为收费 false为免费即不做限制
     * @param maxPlaytime 最大能播放时长，单位秒
     */
    public void setChargeState(boolean isCharge, int maxPlaytime) {
        this.mIsCharge = isCharge;
        this.mMaxPlaytime = maxPlaytime * 1000;
    }

    /**
     * 设置播放区域拉伸类型
     */
    public void setScaleType(int showType) {
        mCurrShowType = showType;
        mIjkView.setAspectRatio(mCurrShowType);
    }

    /**
     * 旋转指定角度
     */
    public void setPlayerRotation(int rotation) {
        if (mIjkView != null) {
            mIjkView.setPlayerRotation(rotation);
            mIjkView.setAspectRatio(mCurrShowType);
        }
    }

    /**
     * 设置自动重连的模式或者重连时间，isAuto true 出错重连，false出错不重连，connectTime重连的时间
     */
    public void setAutoReConnect(boolean isAuto, int connectTime) {
        this.mIsAutoReConnect = isAuto;
        this.mAutoConnectTime = connectTime;
    }

    /**
     * 是否仅仅为全屏
     */
    public void setOnlyFullScreen(boolean isFull) {
        this.mIsOnlyFullScreen = isFull;
        tryFullScreen(mIsOnlyFullScreen);
        if (mIsOnlyFullScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    /**
     * 设置是否禁止双击
     */
    public void setForbidDoubleUp(boolean flag) {
        this.mIsForbidDoubleUp = flag;
    }

    /**
     * 设置是否禁止隐藏bar
     */
    public void setForbidHideControlPanel(boolean flag) {
        this.mIsForbidHideControlPanel = flag;
    }

    /**
     * 是否禁止触摸
     */
    public void forbidTouch(boolean forbidTouch) {
        this.mIsForbidTouch = forbidTouch;
    }

    /**
     * 隐藏返回键，true隐藏，false为显示
     */
    public void hideBack(boolean isHide) {
        mBackImage.setVisibility(isHide ? View.GONE : View.VISIBLE);
    }

    /**
     * 隐藏全屏按钮，true隐藏，false为显示
     */
    public void hideFullscreen(boolean isHide) {
        mFullScreenImage.setVisibility(isHide ? View.GONE : View.VISIBLE);
    }

    /**
     * 隐藏中间播放按钮,ture为隐藏，false为不做隐藏处理，但不是显示
     */
    public void hideCenterPlayer(boolean isHide) {
        mIsHideCenterPlayer = isHide;
        mCenterPlayerImage.setVisibility(mIsHideCenterPlayer ? View.GONE : View.VISIBLE);
    }

    /**
     * 是否隐藏topbar，true为隐藏，false为不隐藏，但不一定是显示
     */
    public void hideHideTopBar(boolean isHide) {
        mIsHideTopBar = isHide;
        mTopBarView.setVisibility(mIsHideTopBar ? View.GONE : View.VISIBLE);
    }

    /**
     * 是否隐藏bottonbar，true为隐藏，false为不隐藏，但不一定是显示
     */
    public void hideBottomBar(boolean isHide) {
        mIsHideBottomBar = isHide;
        mBottomBarView.setVisibility(mIsHideBottomBar ? View.GONE : View.VISIBLE);
    }

    /**
     * 是否隐藏上下bar，true为隐藏，false为不隐藏，但不一定是显示
     */
    public void hideControlPanl(boolean isHide) {
        hideBottomBar(isHide);
        hideHideTopBar(isHide);
    }

    /**
     * 显示菜单设置
     */
    public void showMenu() {
        if (!mIsForbidHideControlPanel) {
            mTopBarView.setVisibility(View.GONE);
            mBottomBarView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置显示加载网速或者隐藏
     */
    public void setShowSpeed(boolean isShow) {
        mPlayerSpeedTv.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    // ========================================= 内部实现 ========================================= //

    /**
     * 当前播放的是否是直播
     */
    private void isLive() {
        mIsLive = mCurrVideoUrl != null
                && (mCurrVideoUrl.startsWith("rtmp://")
                || (mCurrVideoUrl.startsWith("http://") && mCurrVideoUrl.endsWith(".m3u8"))
                || (mCurrVideoUrl.startsWith("http://") && mCurrVideoUrl.endsWith(".flv")));
    }

    /**
     * 隐藏所有界面
     */
    private void hideAllUI() {
        if (!mIsForbidHideControlPanel) {
            mTopBarView.setVisibility(View.GONE);
            mBottomBarView.setVisibility(View.GONE);
        }
        hideStatusUI();
    }

    /**
     * 显示或隐藏操作面板
     */
    private void operatorPanel() {
        mIsShowControlPanel = !mIsShowControlPanel;
        if (mIsShowControlPanel) {
            mTopBarView.setVisibility(mIsHideTopBar ? View.GONE : View.VISIBLE);
            mBottomBarView.setVisibility(mIsHideBottomBar ? View.GONE : View.VISIBLE);
            if (mIsLive) {
                mLayoutQuery.id(R.id.app_video_process_panl).invisible();
            } else {
                mLayoutQuery.id(R.id.app_video_process_panl).visible();
            }
            if (mIsOnlyFullScreen || mIsForbidDoubleUp) {
                mFullScreenImage.setVisibility(View.GONE);
            } else {
                mFullScreenImage.setVisibility(View.VISIBLE);
            }
            if (mOnControlVisibilityChangeListener != null) {
                mOnControlVisibilityChangeListener.change(true);
            }
            // 显示面板的时候再根据状态显示播放按钮
            if (mCurrStatus == PlayStateParams.STATE_PLAYING
                    || mCurrStatus == PlayStateParams.STATE_PREPARED
                    || mCurrStatus == PlayStateParams.STATE_PREPARING
                    || mCurrStatus == PlayStateParams.STATE_PAUSED) {
                if (mIsHideCenterPlayer) {
                    mCenterPlayerImage.setVisibility(View.GONE);
                } else {
                    mCenterPlayerImage.setVisibility(mIsLive ? View.GONE : View.VISIBLE);
                }
            } else {
                mCenterPlayerImage.setVisibility(View.GONE);
            }
            updatePausePlay();
            mHandler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
            mAutoPlayRunnable.start();
        } else {
            if (mIsHideTopBar) {
                mTopBarView.setVisibility(View.GONE);
            } else {
                mTopBarView.setVisibility(mIsForbidHideControlPanel ? View.VISIBLE : View.GONE);

            }
            if (mIsHideBottomBar) {
                mBottomBarView.setVisibility(View.GONE);
            } else {
                mBottomBarView.setVisibility(mIsForbidHideControlPanel ? View.VISIBLE : View.GONE);

            }
            if (!mIsLive && mCurrStatus == PlayStateParams.STATE_PAUSED && !mIjkView.isPlaying()) {
                if (mIsHideCenterPlayer) {
                    mCenterPlayerImage.setVisibility(View.GONE);
                } else {
                    // 暂停时一直显示按钮
                    mCenterPlayerImage.setVisibility(View.VISIBLE);
                }
            } else {
                mCenterPlayerImage.setVisibility(View.GONE);
            }
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            if (mOnControlVisibilityChangeListener != null) {
                mOnControlVisibilityChangeListener.change(false);
            }
            mAutoPlayRunnable.stop();
        }
    }

    /**
     * 全屏切换
     */
    private void toggleFullScreen() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            doOnNoAllScreen();
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            doOnAllScreen();
        }
        updateFullScreenButton();
    }

    /**
     * 进度条和时长显示的方向切换
     */
    private void toggleProcessDurationOrientation() {
        setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT);
    }

    /**
     * 设置进度条和时长显示的方向，默认为上下显示，true为上下显示false为左右显示
     */
    private void setProcessDurationOrientation(int portrait) {
        if (portrait == PlayStateParams.PROCESS_CENTER) {
            mLayoutQuery.id(R.id.app_video_currentTime_full).gone();
            mLayoutQuery.id(R.id.app_video_endTime_full).gone();
            mLayoutQuery.id(R.id.app_video_center).gone();
            mLayoutQuery.id(R.id.app_video_lift).visible();
        } else if (portrait == PlayStateParams.PROCESS_LANDSCAPE) {
            mLayoutQuery.id(R.id.app_video_currentTime_full).visible();
            mLayoutQuery.id(R.id.app_video_endTime_full).visible();
            mLayoutQuery.id(R.id.app_video_center).gone();
            mLayoutQuery.id(R.id.app_video_lift).gone();
        } else {
            mLayoutQuery.id(R.id.app_video_currentTime_full).gone();
            mLayoutQuery.id(R.id.app_video_endTime_full).gone();
            mLayoutQuery.id(R.id.app_video_center).visible();
            mLayoutQuery.id(R.id.app_video_lift).gone();
        }
    }

    /**
     * 获取界面方向
     */
    private int getScreenOrientation() {
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }
        return orientation;
    }

    /**
     * 状态改变同步UI
     */
    private void statusChange(int newStatus) {
        if (newStatus == PlayStateParams.STATE_COMPLETED) {
            mCurrStatus = PlayStateParams.STATE_COMPLETED;
            mCurrPlayPosition = 0;
            hideAllUI();
            showStatus("播放结束");
        } else if (newStatus == PlayStateParams.STATE_PREPARING || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_START) {
            mCurrStatus = PlayStateParams.STATE_PREPARING;
            hideStatusUI(); // 视频缓冲
            mLayoutQuery.id(R.id.app_video_loading).visible();
        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_RENDERING_START
                || newStatus == PlayStateParams.STATE_PLAYING
                || newStatus == PlayStateParams.STATE_PREPARED
                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_END
                || newStatus == PlayStateParams.STATE_PAUSED) {
            if (mCurrStatus == PlayStateParams.STATE_PAUSED) {
                mCurrStatus = PlayStateParams.STATE_PAUSED;
            } else {
                mCurrStatus = PlayStateParams.STATE_PLAYING;
            }
            // 视频缓冲结束后隐藏缩列图
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideStatusUI();
                    // 显示控制bar
                    mIsShowControlPanel = false;
                    if (!mIsForbidTouch) {
                        operatorPanel();
                    }
                    mLayoutQuery.id(R.id.ll_bg).gone(); // 延迟0.5秒隐藏视频封面隐藏
                }
            }, 500);
        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_INTERRUPT) {
            // 直播停止推流
            mCurrStatus = PlayStateParams.STATE_ERROR;
            if (!(mIsNetWorkTip && (NetworkUtils.getNetworkType(mActivity) == 4
                    || NetworkUtils.getNetworkType(mActivity) == 5
                    || NetworkUtils.getNetworkType(mActivity) == 6))) {
                if (mIsCharge && mMaxPlaytime < getCurrPlayPosition()) {
                    mLayoutQuery.id(R.id.app_video_freeTie).visible();
                } else {
                    hideAllUI();
                    if (mIsLive) {
                        showStatus("获取不到直播源");
                    } else {
                        showStatus(mActivity.getResources().getString(R.string.small_problem));
                    }
                    // 5秒尝试重连
                    if (!mIsErrorStop && mIsAutoReConnect) {
                        mHandler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY, mAutoConnectTime);
                    }

                }
            } else {
                mLayoutQuery.id(R.id.app_video_netTie).visible();
            }
        } else if (newStatus == PlayStateParams.STATE_ERROR
                || newStatus == PlayStateParams.MEDIA_INFO_UNKNOWN
                || newStatus == PlayStateParams.MEDIA_ERROR_IO
                || newStatus == PlayStateParams.MEDIA_ERROR_MALFORMED
                || newStatus == PlayStateParams.MEDIA_ERROR_UNSUPPORTED
                || newStatus == PlayStateParams.MEDIA_ERROR_TIMED_OUT
                || newStatus == PlayStateParams.MEDIA_ERROR_SERVER_DIED) {
            mCurrStatus = PlayStateParams.STATE_ERROR;
            if (!(mIsNetWorkTip && (NetworkUtils.getNetworkType(mActivity) == 4
                    || NetworkUtils.getNetworkType(mActivity) == 5
                    || NetworkUtils.getNetworkType(mActivity) == 6))) {
                if (mIsCharge && mMaxPlaytime < getCurrPlayPosition()) {
                    mLayoutQuery.id(R.id.app_video_freeTie).visible();
                } else {
                    hideStatusUI();
                    showStatus(mActivity.getResources().getString(R.string.small_problem));
                    // 秒尝试重连
                    if (!mIsErrorStop && mIsAutoReConnect) {
                        mHandler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY, mAutoConnectTime);
                    }
                }
            } else {
                mLayoutQuery.id(R.id.app_video_netTie).visible();
            }
        }
    }

    /**
     * 显示视频播放状态提示
     */
    private void showStatus(String statusText) {
        mLayoutQuery.id(R.id.app_video_replay).visible();
        mLayoutQuery.id(R.id.app_video_status_text).text(statusText);
    }

    /**
     * 界面方向改变时刷新界面
     */
    private void doOnConfigurationChanged(final boolean portrait) {
        if (mIjkView != null && !mIsOnlyFullScreen) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    tryFullScreen(!portrait);
                    if (portrait) {
                        mLayoutQuery.id(R.id.app_video_box).height(mPlayerViewInitHeight, false);
                    } else {
                        int heightPixels = mActivity.getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = mActivity.getResources().getDisplayMetrics().widthPixels;
                        mLayoutQuery.id(R.id.app_video_box).height(Math.min(heightPixels, widthPixels), false);
                    }
                    updateFullScreenButton();
                }
            });
            mOrientationEventListener.enable();
        }
    }

    private void doOnAllScreen() {
        setFullScreen(true);
        if (mIjkView != null && !mIsOnlyFullScreen) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIsPortrait = false;
                    int heightPixels = mActivity.getResources().getDisplayMetrics().heightPixels;
                    int widthPixels = mActivity.getResources().getDisplayMetrics().widthPixels;
                    mLayoutQuery.id(R.id.app_video_box).height(Math.min(heightPixels, widthPixels), false);
                }
            });
        }
    }

    private void doOnNoAllScreen() {
        setFullScreen(false);
        if (mIjkView != null && !mIsOnlyFullScreen) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIsPortrait = true;
                    mLayoutQuery.id(R.id.app_video_box).height(mPlayerViewInitHeight, false);
                }
            });
        }
    }

    /**
     * 设置界面方向
     */
    private void setFullScreen(boolean fullScreen) {
        if (mActivity != null) {
            WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
            toggleProcessDurationOrientation();
        }
    }

    /**
     * 设置界面方向带隐藏actionbar
     */
    private void tryFullScreen(boolean fullScreen) {
        if (mActivity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) mActivity).getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
        }
        setFullScreen(fullScreen);
    }

    /**
     * 隐藏状态界面
     */
    private void hideStatusUI() {
        mCenterPlayerImage.setVisibility(View.GONE);
        mLayoutQuery.id(R.id.app_video_replay).gone();
        mLayoutQuery.id(R.id.app_video_netTie).gone();
        mLayoutQuery.id(R.id.app_video_freeTie).gone();
        mLayoutQuery.id(R.id.app_video_loading).gone();
        if (mOnControlVisibilityChangeListener != null) {
            mOnControlVisibilityChangeListener.change(false);
        }
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mCurrVolume = -1;
        mCurrBrightness = -1f;
        if (mNewPosition >= 0) {
            mHandler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
            mHandler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
        }
        mHandler.removeMessages(MESSAGE_HIDE_CENTER_BOX);
        mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_CENTER_BOX, 500);
        if (mAutoPlayRunnable != null) {
            mAutoPlayRunnable.start();
        }
    }

    /**
     * 同步进度
     */
    private long syncProgress() {
        if (mIsDragging) {
            return 0;
        }
        long position = mIjkView.getCurrentPosition();
        long duration = mIjkView.getDuration();
        if (mProgressSeekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgressSeekBar.setProgress((int) pos);
            }
            int percent = mIjkView.getBufferPercentage();
            mProgressSeekBar.setSecondaryProgress(percent * 10);
        }

        if (mIsCharge && mMaxPlaytime + 1000 < getCurrPlayPosition()) {
            mLayoutQuery.id(R.id.app_video_freeTie).visible();
            pausePlay();
        } else {
            mLayoutQuery.id(R.id.app_video_currentTime).text(generateTime(position));
            mLayoutQuery.id(R.id.app_video_currentTime_full).text(generateTime(position));
            mLayoutQuery.id(R.id.app_video_currentTime_left).text(generateTime(position));
            mLayoutQuery.id(R.id.app_video_endTime).text(generateTime(duration));
            mLayoutQuery.id(R.id.app_video_endTime_full).text(generateTime(duration));
            mLayoutQuery.id(R.id.app_video_endTime_left).text(generateTime(duration));
        }
        return position;
    }

    /**
     * 更新播放、暂停和停止按钮
     */
    private void updatePausePlay() {
        if (mIjkView.isPlaying()) {
            if (mIsLive) {
                mBottomBarPlayerImage.setImageResource(R.drawable.simple_player_stop_white_24dp);
            } else {
                mBottomBarPlayerImage.setImageResource(R.drawable.simple_player_icon_media_pause);
                mCenterPlayerImage.setImageResource(R.drawable.simple_player_center_pause);
            }
        } else {
            mBottomBarPlayerImage.setImageResource(R.drawable.simple_player_arrow_white_24dp);
            mCenterPlayerImage.setImageResource(R.drawable.simple_player_center_play);
        }
    }

    /**
     * 更新全屏和半屏按钮
     */
    private void updateFullScreenButton() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mFullScreenImage.setImageResource(R.drawable.simple_player_icon_fullscreen_shrink);
        } else {
            mFullScreenImage.setImageResource(R.drawable.simple_player_icon_fullscreen_stretch);
        }
    }

    /**
     * 滑动改变声音大小
     */
    private void onVolumeSlide(float percent) {
        if (mCurrVolume == -1) {
            mCurrVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mCurrVolume < 0) {
                mCurrVolume = 0;
            }
        }
        int index = (int) (percent * mMaxVolume) + mCurrVolume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }
        // 显示
        mLayoutQuery.id(R.id.app_video_volume_icon).image(i == 0
                ? R.drawable.simple_player_volume_off_white_36dp : R.drawable.simple_player_volume_up_white_36dp);
        mLayoutQuery.id(R.id.app_video_brightness_box).gone();
        mLayoutQuery.id(R.id.app_video_volume_box).visible();
        mLayoutQuery.id(R.id.app_video_volume_box).visible();
        mLayoutQuery.id(R.id.app_video_volume).text(s).visible();
    }

    /**
     * 快进或者快退滑动改变进度
     */
    private void onProgressSlide(float percent) {
        int position = mIjkView.getCurrentPosition();
        long duration = mIjkView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);
        mNewPosition = delta + position;
        if (mNewPosition > duration) {
            mNewPosition = duration;
        } else if (mNewPosition <= 0) {
            mNewPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
            mLayoutQuery.id(R.id.app_video_fastForward_box).visible();
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
            mLayoutQuery.id(R.id.app_video_fastForward).text(text + "s");
            mLayoutQuery.id(R.id.app_video_fastForward_target).text(generateTime(mNewPosition) + "/");
            mLayoutQuery.id(R.id.app_video_fastForward_all).text(generateTime(duration));
        }
    }

    /**
     * 亮度滑动改变亮度
     */
    private void onBrightnessSlide(float percent) {
        if (mCurrBrightness < 0) {
            mCurrBrightness = mActivity.getWindow().getAttributes().screenBrightness;
            if (mCurrBrightness <= 0.00f) {
                mCurrBrightness = 0.50f;
            } else if (mCurrBrightness < 0.01f) {
                mCurrBrightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "mCurrBrightness:" + mCurrBrightness + ",percent:" + percent);
        mLayoutQuery.id(R.id.app_video_brightness_box).visible();
        WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
        lpa.screenBrightness = mCurrBrightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        mLayoutQuery.id(R.id.app_video_brightness).text(((int) (lpa.screenBrightness * 100)) + "%");
        mActivity.getWindow().setAttributes(lpa);
    }

    /**
     * 时长格式化显示
     */
    @SuppressLint("DefaultLocale")
    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 下载速度格式化显示
     */
    private String getFormatSize(int size) {
        long fileSize = (long) size;
        String showSize = "";
        if (fileSize >= 0 && fileSize < 1024) {
            showSize = fileSize + "Kb/s";
        } else if (fileSize >= 1024 && fileSize < (1024 * 1024)) {
            showSize = Long.toString(fileSize / 1024) + "KB/s";
        } else if (fileSize >= (1024 * 1024) && fileSize < (1024 * 1024 * 1024)) {
            showSize = Long.toString(fileSize / (1024 * 1024)) + "MB/s";
        }
        return showSize;
    }

    // ========================================== 内部类 ========================================== //

    @SuppressLint("HandlerLeak")
    private class CusUiHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 滑动完成，隐藏滑动提示的box
                case MESSAGE_HIDE_CENTER_BOX:
                    mLayoutQuery.id(R.id.app_video_volume_box).gone();
                    mLayoutQuery.id(R.id.app_video_brightness_box).gone();
                    mLayoutQuery.id(R.id.app_video_fastForward_box).gone();
                    break;

                // 滑动完成，设置播放进度
                case MESSAGE_SEEK_NEW_POSITION:
                    if (!mIsLive && mNewPosition >= 0) {
                        mIjkView.seekTo((int) mNewPosition);
                        mNewPosition = -1;
                    }
                    break;

                // 滑动中，同步播放进度
                case MESSAGE_SHOW_PROGRESS:
                    long pos = syncProgress();
                    if (!mIsDragging && mIsShowControlPanel) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;

                // 重新去播放
                case MESSAGE_RESTART_PLAY:
                    mCurrStatus = PlayStateParams.STATE_ERROR;
                    startPlay();
                    updatePausePlay();
                    break;
            }
        }
    }

    /**
     * 收起控制面板轮询，默认5秒无操作，收起控制面板
     */
    private class AutoPlayRunnable implements Runnable {
        private int AUTO_PLAY_INTERVAL = 5000;
        private boolean mShouldAutoPlay;

        AutoPlayRunnable() {
            mShouldAutoPlay = false;
        }

        void start() {
            if (!mShouldAutoPlay) {
                mShouldAutoPlay = true;
                mHandler.removeCallbacks(this);
                mHandler.postDelayed(this, AUTO_PLAY_INTERVAL);
            }
        }

        void stop() {
            if (mShouldAutoPlay) {
                mHandler.removeCallbacks(this);
                mShouldAutoPlay = false;
            }
        }

        @Override
        public void run() {
            if (mShouldAutoPlay) {
                mHandler.removeCallbacks(this);
                if (!mIsForbidTouch) {
                    operatorPanel();
                }
            }
        }
    }

    /**
     * 播放器的手势监听
     */
    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean isDownTouch; // 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
        private boolean isVolume; // 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
        private boolean isLandscape; // 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动

        /**
         * 双击，视频视窗双击事件
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            if (!mIsForbidTouch && !mIsOnlyFullScreen && !mIsForbidDoubleUp) {
//                toggleFullScreen();
//            }
            return true;
        }

        /**
         * 按下
         */
        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return super.onDown(e);
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mIsForbidTouch) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                float deltaY = mOldY - e2.getY();
                float deltaX = mOldX - e2.getX();
                if (isDownTouch) {
                    isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                    isVolume = mOldX > mScreenWidthPix * 0.5f;
                    isDownTouch = false;
                }

                if (isLandscape) {
                    if (!mIsLive) {
                        // 进度设置
                        onProgressSlide(-deltaX / mIjkView.getWidth());
                    }
                } else {
                    float percent = deltaY / mIjkView.getHeight();
                    if (isVolume) {
                        // 声音设置
                        onVolumeSlide(percent);
                    } else {
                        // 亮度设置
                        onBrightnessSlide(percent);
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         * 单击，视频视窗单击事件
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!mIsForbidTouch) {
                operatorPanel();
            }
            return true;
        }
    }
}
