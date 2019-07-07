package education.juxin.com.educationpro.party3.ijkplayer.interfaces;

import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public interface IRenderView {

    int AR_ASPECT_FIT_PARENT = 0; // without clip
    int AR_ASPECT_FILL_PARENT = 1; // may clip
    int AR_ASPECT_WRAP_CONTENT = 2;
    int AR_MATCH_PARENT = 3;
    int AR_16_9_FIT_PARENT = 4;
    int AR_4_3_FIT_PARENT = 5;

    /**
     * 获取外层界面
     */
    View getView();

    /**
     * 是否需要等待重置大小
     */
    boolean shouldWaitForResize();

    /**
     * 设置视频界面大小
     */
    void setVideoSize(int videoWidth, int videoHeight);

    /**
     * 设置视频裁剪方式
     */
    void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen);

    /**
     * 设置视频旋转角度
     */
    void setVideoRotation(int degree);

    /**
     * 设置视频裁剪方式
     */
    void setAspectRatio(int aspectRatio);

    /**
     * 添加视频渲染回调
     */
    void addRenderCallback(IRenderCallback callback);

    /**
     * 移除视频渲染回调
     */
    void removeRenderCallback(IRenderCallback callback);

    interface ISurfaceHolder {

        /**
         * surface界面绑定到mediaplay上
         */
        void bindToMediaPlayer(IMediaPlayer mp);

        /**
         * 获取渲染的view
         */
        @NonNull
        IRenderView getRenderView();

        /**
         * 获取渲染使用的具体view surface
         */
        @Nullable
        SurfaceHolder getSurfaceHolder();

        /**
         * 打开surface界面
         */
        @Nullable
        Surface openSurface();

        /**
         * 获取渲染使用的具体view texture
         */
        @Nullable
        SurfaceTexture getSurfaceTexture();
    }

    interface IRenderCallback {
        /**
         * 创建surface界面大小
         */
        void onSurfaceCreated(ISurfaceHolder holder, int width, int height);

        /**
         * surface界面大小改变监听
         */
        void onSurfaceChanged(ISurfaceHolder holder, int format, int width, int height);

        /**
         * 界面回收
         */
        void onSurfaceDestroyed(ISurfaceHolder holder);
    }
}
