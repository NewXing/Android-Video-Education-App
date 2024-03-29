package education.juxin.com.educationpro.party3.ijkplayer.utils;

public class PlayStateParams {

    // ======================================== 视频裁剪比例 ======================================== //

    public static final int FIT_PARENT = 0; // 可能会剪裁，保持原视频大小，显示在中心，当原视频大小超过View大小，超过部分裁剪
    public static final int FILL_PARENT = 1; // 可能会剪裁，等比例放大视频，直到填满View为止，超过View的部分裁剪
    public static final int WRAP_CONTENT = 2; // 视频完整居中显示，如果视频大于View，则按比例收缩直到完全显示在View中
    public static final int FIT_XY = 3; // 不剪裁，非等比例拉伸画面填满整个View
    public static final int FIT_16_9 = 4; // 不剪裁，非等比例拉伸画面到16:9，并完全显示在View中
    public static final int FIT_4_3 = 5; // 不剪裁，非等比例拉伸画面到4:3，并完全显示在View中

    // ======================================= 进度条显示样式 ======================================= //

    public static final int PROCESS_PORTRAIT = 0; // 上下样式
    public static final int PROCESS_LANDSCAPE = 1; //左右样式
    public static final int PROCESS_CENTER = 2; //中间两边样式

    // ==================================== 播放器中使用的播放状态 ==================================== //

    public static final int STATE_IDLE = 330; // 空闲
    public static final int STATE_ERROR = 331; // 播放出错
    public static final int STATE_PREPARING = 332; // 准备中/加载中
    public static final int STATE_PREPARED = 333; // 准备完成
    public static final int STATE_PLAYING = 334; // 播放中
    public static final int STATE_PAUSED = 335; // 暂停
    public static final int STATE_COMPLETED = 336; // 播放完成

    // ===================================== ijkPlayer状态码表 ===================================== //

    public static final int MEDIA_INFO_UNKNOWN = 1; // 未知信息
    public static final int MEDIA_INFO_STARTED_AS_NEXT = 2; // 播放下一条
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3; // 视频开始整备中
    public static final int MEDIA_ERROR_SERVER_DIED = 100; // 服务挂掉
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200; // 数据错误没有有效的回收
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700; // 视频日志跟踪
    public static final int MEDIA_INFO_BUFFERING_START = 701; // 开始缓冲中
    public static final int MEDIA_INFO_BUFFERING_END = 702; // 缓冲结束
    public static final int MEDIA_INFO_BUFFERING_BYTES_UPDATE = 503; // 网速方面
    public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703; // 网络带宽，网速方面
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801; // 不可设置播放位置，直播方面
    public static final int MEDIA_INFO_METADATA_UPDATE = 802;
    public static final int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901; // 不支持字幕
    public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902; // 字幕超时
    public static final int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001; // 视频方向改变
    public static final int MEDIA_INFO_AUDIO_RENDERING_START = 10002; // 音频开始整备中
    public static final int MEDIA_ERROR_TIMED_OUT = -110; // 数据超时
    public static final int MEDIA_ERROR_IO = -1004; // IO错误
    public static final int MEDIA_ERROR_MALFORMED = -1007;
    public static final int MEDIA_ERROR_UNSUPPORTED = -1010;
    public static final int MEDIA_INFO_VIDEO_INTERRUPT = -10000; // 数据连接中断
}
