package education.juxin.com.educationpro.party3.ijkplayer.player;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import education.juxin.com.educationpro.party3.ijkplayer.interfaces.IMediaController;
import education.juxin.com.educationpro.party3.ijkplayer.interfaces.IRenderView;
import education.juxin.com.educationpro.party3.ijkplayer.utils.FileMediaDataSource;
import education.juxin.com.educationpro.party3.ijkplayer.utils.PlayStateParams;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

public class CusIjkView extends FrameLayout implements MediaController.MediaPlayerControl {
    // 打印日志的TAG
    private String TAG = this.getClass().getSimpleName();
    // mCurrentState is a  object's current state.
    private int mCurrentState = PlayStateParams.STATE_IDLE;
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state, calling pause()
    // intends to bring the object to a target state of STATE_PAUSED.
    private int mTargetState = PlayStateParams.STATE_IDLE;
    // 播放地址通过客户端可设置
    private Uri mUri;
    // 播放器的一些基本配置
    private Map<String, String> mHeaders;
    // 所有我们需要播放和显示视频的东西
    private IRenderView.ISurfaceHolder mSurfaceHolder = null;
    // 视频宽度
    private int mVideoWidth;
    // 视频高度
    private int mVideoHeight;
    // 窗口宽度
    private int mSurfaceWidth;
    // 窗口高度
    private int mSurfaceHeight;
    // 视频旋转角度
    private int mVideoRotationDegree;
    // 媒体播放器
    private IMediaPlayer mMediaPlayer;
    // 媒体控制器
    private IMediaController mMediaController;
    // 播放完成监听
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    // 播放准备监听
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    // 播放缓冲监听
    private int mCurrentBufferPercentage;
    // 播放错误监听
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    // 播放其他信息监听
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    // 为记录seek位置而做准备
    private long mSeekWhenPrepared;
    private Context mAppContext;
    private IRenderView mRenderView;
    private int mVideoSarNum;
    private int mVideoSarDen;
    // Extend: Aspect Ratio
    private static final int[] s_allAspectRatio = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};
    private int mCurrentAspectRatioIndex = 0;
    private int mCurrentAspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex];
    // Extend: Render
    public static final int RENDER_SURFACE_VIEW = 1;
    public static final int RENDER_TEXTURE_VIEW = 2;
    private List<Integer> mAllRenders = new ArrayList<>();
    // 使用编解码器硬编码还是软编码，true 硬编码 false 为软编码
    private boolean usingMediaCodec;
    // 使用编解码是否自转
    private boolean usingMediaCodecAutoRotate;
    private boolean usingAndroidPlayer;
    private boolean usingOpenSLES;
    private boolean enableBackgroundPlay;

    public CusIjkView(Context context) {
        super(context);
        initVideoView(context);
    }

    public CusIjkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public CusIjkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    /**
     * 初始化视频view
     */
    private void initVideoView(Context context) {
        mAppContext = context.getApplicationContext();

        initRenders();

        mVideoWidth = 0;
        mVideoHeight = 0;

        // 设置为可触摸并且获取焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        mCurrentState = PlayStateParams.STATE_IDLE;
        mTargetState = PlayStateParams.STATE_IDLE;
    }

    /**
     * 设置渲染器
     */
    public void setRenderView(IRenderView renderView) {
        if (mRenderView != null) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }

            View renderUIView = mRenderView.getView();
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
            removeView(renderUIView);
        }

        if (renderView == null)
            return;

        mRenderView = renderView;
        renderView.setAspectRatio(mCurrentAspectRatio);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        if (mVideoSarNum > 0 && mVideoSarDen > 0)
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

        View renderUIView = mRenderView.getView();

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        addView(renderUIView);

        mRenderView.addRenderCallback(mSHCallback);
        mRenderView.setVideoRotation(mVideoRotationDegree);
    }

    /**
     * 设置旋转角度
     */
    public void setPlayerRotation(int rotation) {
        mVideoRotationDegree = rotation;
        if (mRenderView != null) {
            mRenderView.setVideoRotation(mVideoRotationDegree);
        }
    }

    /**
     * 旋转渲染器
     */
    public void setRender(int render) {
        switch (render) {
            case RENDER_TEXTURE_VIEW: {
                TextureRenderView renderView = new TextureRenderView(getContext());
                if (mMediaPlayer != null) {
                    renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer);
                    renderView.setVideoSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                    renderView.setVideoSampleAspectRatio(mMediaPlayer.getVideoSarNum(), mMediaPlayer.getVideoSarDen());
                    renderView.setAspectRatio(mCurrentAspectRatio);
                }
                setRenderView(renderView);
                break;
            }
            case RENDER_SURFACE_VIEW: {
                SurfaceRenderView renderView = new SurfaceRenderView(getContext());
                setRenderView(renderView);
                break;
            }
            default:
                Log.e(TAG, String.format(Locale.getDefault(), "invalid render %d\n", render));
                break;
        }
    }

    /**
     * 设置视频路径
     */
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    /**
     * 设置视频URI
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
    private void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    /**
     * 停止视频
     */
    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;

            mCurrentState = PlayStateParams.STATE_IDLE;
            mTargetState = PlayStateParams.STATE_IDLE;

            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                am.abandonAudioFocus(null);
            }
        }
    }

    /**
     * 打开视频
     */
    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            return;
        }

        release(false);

        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        try {
            if (usingAndroidPlayer) {
                mMediaPlayer = new AndroidMediaPlayer();
            } else {
                IjkMediaPlayer ijkMediaPlayer = null;
                if (mUri != null) {
                    ijkMediaPlayer = new IjkMediaPlayer();
                    IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
                    if (usingMediaCodec) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
                        if (usingMediaCodecAutoRotate) {
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
                        } else {
                            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
                        }
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
                    }

                    if (usingOpenSLES) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
                    }

                    // Auto Select=,RGB 565=fcc-rv16,RGB 888X=fcc-rv32,YV12=fcc-yv12,默认为RGB 888X
                    String pixelFormat = "";
                    if (TextUtils.isEmpty(pixelFormat)) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
                    }
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 10000000);
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);

                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
                }
                mMediaPlayer = ijkMediaPlayer;
            }

            if (enableBackgroundPlay) {
                mMediaPlayer = new TextureMediaPlayer(mMediaPlayer);
            }

            if (mMediaPlayer == null) {
                return;
            }

            // 视频准备播放监听
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            // 视频播放完成监听
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            // 视频错误监听
            mMediaPlayer.setOnErrorListener(mErrorListener);
            // 视频其他信息监听
            mMediaPlayer.setOnInfoListener(mInfoListener);
            // 视频界面大小改变监听
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            // 视频缓冲监听
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            String scheme = mUri.getScheme();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && usingAndroidPlayer && (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
                mMediaPlayer.setDataSource(dataSource);
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
            } else {
                mMediaPlayer.setDataSource(mUri.toString());
            }
            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            // 这里不设置目标状态,但保护的目标状态
            mCurrentState = PlayStateParams.STATE_PREPARING;
            attachMediaController();
        } catch (IOException | IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = PlayStateParams.STATE_ERROR;
            mTargetState = PlayStateParams.STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }
    }

    public void setMediaController(IMediaController controller) {
        if (mMediaController != null) {
            mMediaController.hide();
        }
        mMediaController = controller;
        attachMediaController();
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());
        }
    }

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            mVideoSarNum = mp.getVideoSarNum();
            mVideoSarDen = mp.getVideoSarDen();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                if (mRenderView != null) {
                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                }
                requestLayout();
            }
        }
    };

    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            // 这里不设置目标状态,但保护的目标状态
            mCurrentState = PlayStateParams.STATE_PREPARED;

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mMediaPlayer, mCurrentState, 0);
            }

            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            long seekToPosition = mSeekWhenPrepared;
            if (seekToPosition != 0) {
                seekTo((int) seekToPosition);
            }

            if (mVideoWidth != 0 && mVideoHeight != 0) {
                if (mRenderView != null) {
                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                    if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                        if (mTargetState == PlayStateParams.STATE_PLAYING) {
                            start();
                            if (mMediaController != null) {
                                mMediaController.show();
                            }
                        } else if (!isPlaying() &&
                                (seekToPosition != 0 || getCurrentPosition() > 0)) {
                            if (mMediaController != null) {
                                mMediaController.show(0);
                            }
                        }
                    }
                }
            } else {
                if (mTargetState == PlayStateParams.STATE_PLAYING) {
                    start();
                }
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
        public void onCompletion(IMediaPlayer mp) {
            mCurrentState = PlayStateParams.STATE_COMPLETED;
            mTargetState = PlayStateParams.STATE_COMPLETED;
            if (mMediaController != null) {
                mMediaController.hide();
            }
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mMediaPlayer, PlayStateParams.STATE_COMPLETED, 0);
            }
        }
    };

    private IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
        public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mp, arg1, arg2);
            }
            switch (arg1) {
                case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                    break;
                case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                    Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                    break;
                case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                    break;
                case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    mVideoRotationDegree = arg2;
                    Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                    if (mRenderView != null)
                        mRenderView.setVideoRotation(arg2);
                    break;
                case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                    break;
            }
            return true;
        }
    };

    private IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {
        public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
            Log.d(TAG, "Error: " + framework_err + "," + impl_err);
            mCurrentState = PlayStateParams.STATE_ERROR;
            mTargetState = PlayStateParams.STATE_ERROR;
            if (mMediaController != null) {
                mMediaController.hide();
            }

            if (mOnErrorListener != null) {
                mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err);
            }
            if (mOnInfoListener != null) {
                if (mOnInfoListener.onInfo(mMediaPlayer, framework_err, 0)) {
                    return true;
                }
            }

            if (getWindowToken() != null) {
                String message = "Unknown error";

                if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                    message = "Invalid progressive playback";
                }

                new AlertDialog.Builder(getContext())
                        .setMessage(message)
                        .setPositiveButton("error", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (mOnCompletionListener != null) {
                                    mOnCompletionListener.onCompletion(mMediaPlayer);
                                }
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };

    /**
     * Register a callback to be invoked when the media file is loaded and ready to go.
     */
    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    /**
     * Register a callback to be invoked when the end of a media file has been reached during playback.
     */
    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs during playback or setup. If no listener
     * is specified, or if the listener returned false, VideoView will inform the user of any errors.
     */
    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * Register a callback to be invoked when an informational event occurs during playback or setup.
     */
    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }

    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }

            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == PlayStateParams.STATE_PLAYING);
            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo((int) mSeekWhenPrepared);
                }
                start();
            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }

            mSurfaceHolder = holder;
            if (mMediaPlayer != null)
                bindSurfaceHolder(mMediaPlayer, holder);
            else
                openVideo();
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }
            mSurfaceHolder = null;
            releaseWithoutStop();
        }
    };

    public void releaseWithoutStop() {
        if (mMediaPlayer != null)
            mMediaPlayer.setDisplay(null);
    }

    /**
     * release the media player in any state
     */
    public void release(boolean clearTargetState) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = PlayStateParams.STATE_IDLE;

            if (clearTargetState) {
                mTargetState = PlayStateParams.STATE_IDLE;
            }

            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                am.abandonAudioFocus(null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisibility();
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisibility();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP
                && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                && keyCode != KeyEvent.KEYCODE_VOLUME_MUTE
                && keyCode != KeyEvent.KEYCODE_MENU
                && keyCode != KeyEvent.KEYCODE_CALL
                && keyCode != KeyEvent.KEYCODE_ENDCALL;

        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!mMediaPlayer.isPlaying()) {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                }
                return true;
            } else {
                toggleMediaControlsVisibility();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisibility() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = PlayStateParams.STATE_PLAYING;
        }
        mTargetState = PlayStateParams.STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = PlayStateParams.STATE_PAUSED;
            }
        }
        mTargetState = PlayStateParams.STATE_PAUSED;
    }

    public void onPause() {
        release(false);
    }

    public void onResume() {
        openVideo();
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != PlayStateParams.STATE_ERROR &&
                mCurrentState != PlayStateParams.STATE_IDLE &&
                mCurrentState != PlayStateParams.STATE_PREPARING);
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void toggleAspectRatio() {
        mCurrentAspectRatioIndex++;
        mCurrentAspectRatioIndex %= s_allAspectRatio.length;
        mCurrentAspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex];
        if (mRenderView != null) {
            mRenderView.setAspectRatio(mCurrentAspectRatio);
        }
    }

    /**
     * 初始化渲染器
     */
    private void initRenders() {
        mAllRenders.clear();
        mAllRenders.add(RENDER_SURFACE_VIEW); // 添加surface渲染
        int mCurrentRenderIndex;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mAllRenders.add(RENDER_TEXTURE_VIEW); // 添加texture渲染
            mCurrentRenderIndex = 1;
        } else {
            mCurrentRenderIndex = 0;
        }
        int mCurrentRender = mAllRenders.get(mCurrentRenderIndex);
        setRender(mCurrentRender);
    }

    /**
     * 设置播放区域拉伸类型
     */
    public void setAspectRatio(int aspectRatio) {
        for (int i = 0; i < s_allAspectRatio.length; i++) {
            if (s_allAspectRatio[i] == aspectRatio) {
                mCurrentAspectRatioIndex = i;
                if (mRenderView != null) {
                    mRenderView.setAspectRatio(mCurrentAspectRatio);
                }
                break;
            }
        }
    }
}
