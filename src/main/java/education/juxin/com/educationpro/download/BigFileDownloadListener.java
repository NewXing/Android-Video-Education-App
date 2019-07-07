package education.juxin.com.educationpro.download;

/**
 * 下载状态监听
 */
public interface BigFileDownloadListener {
    /**
     * 开始下载
     */
    void downStart();

    /**
     * 下载的进度和速度
     */
    void downProgress(long progress, long speed);

    /**
     * 下载完成
     */
    void downSuccess(String downUrl);

    /**
     * 下载失败
     */
    void downFailed(String failedDesc);
}
