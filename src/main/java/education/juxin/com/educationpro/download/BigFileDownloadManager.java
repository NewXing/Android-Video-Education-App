package education.juxin.com.educationpro.download;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import java.io.File;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.interfaces.IRefreshUI;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 大文件下载 线程管理
 */
public class BigFileDownloadManager implements BigFileDownloadListener, BigFileDownloadDialog.DownDialogListener {

    private static final int NOTIFY_ID = 0x333;

    private static final int DOWN_START = 0;
    private static final int DOWN_PROGRESS = 1;
    private static final int DOWN_SUCCESS = 2;
    private static final int DOWN_FAILED = 3;

    public static IRefreshUI tabMyCacheFragmentRefreshUI;

    private String downUrl;
    private String downPath;
    private BigFileDownloadUtil downUtil;
    private DownHandler handler;
    private BigFileDownloadDialog downDialog;
    private Context context;
    private VideoCacheInfoData data;
    private boolean isDownloading;
    private View mDownloadBtn;

    private NotificationManager notificationManager;
    private Notification notification;

    public BigFileDownloadManager(Context context, VideoCacheInfoData data, View downloadBtn) {
        downUtil = new BigFileDownloadUtil(this);
        handler = new DownHandler();
        downDialog = new BigFileDownloadDialog(context);
        mDownloadBtn = downloadBtn;
        downDialog.setOnDialogClickListener(this);
        downDialog.setOnBtnClickListener(new BigFileDownloadDialog.OnBtnClickListener() {
            @Override
            public void onNoBtnClick() {
                mDownloadBtn.setEnabled(true);
            }

            @Override
            public void onYesBtnClick() {
            }
        });
        this.context = context;
        this.data = data;

        notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    private class DownHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWN_START:
                    downDialog.dismiss();
                    ToastManager.showShortToast("开始下载");
                    isDownloading = false;
                    showNotification();
                    break;

                case DOWN_PROGRESS:
                    updateProgress(msg.arg1);
                    break;

                case DOWN_SUCCESS:
                    ToastManager.showShortToast("下载完成");
                    isDownloading = true;
                    mDownloadBtn.setEnabled(true);
                    updateOnDownloadComplete();

                    File file = new File(downPath);
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

                    if (data != null) {
                        VideoCacheManager.writeVideoInfo(context, data);
//                        try {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    FileDES des = null;
//                                    try {
//                                        des = new FileDES("wangxing");
//                                        des.doEncryptFile(downPath, downPath);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).start();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }
                    if (tabMyCacheFragmentRefreshUI != null) {
                        tabMyCacheFragmentRefreshUI.onRefreshUI();
                    }
                    break;

                case DOWN_FAILED:
                    downDialog.updateView(0, "下载异常", 0);
                    break;
            }
        }
    }

    /***
     * 开始下载
     * @param downUrl 下载地址
     * @param downPath 保存的地址
     * @param desc dialog显示的描述
     */
    public void downStart(String downUrl, String downPath, String desc) {
        this.downUrl = downUrl;
        this.downPath = downPath;
        downDialog.show(desc);
    }

    @Override
    public void downStart() {
        handler.sendEmptyMessage(DOWN_START);
    }

    @Override
    public void downProgress(long progress, long speed) {
        Message msg = new Message();
        msg.what = DOWN_PROGRESS;
        msg.arg1 = (int) progress;
        msg.obj = speed;
        handler.sendMessage(msg);
    }

    @Override
    public void downSuccess(String downUrl) {
        handler.sendEmptyMessage(DOWN_SUCCESS);
    }

    @Override
    public void downFailed(String failedDesc) {
        handler.sendEmptyMessage(DOWN_FAILED);
    }

    @Override
    public void sure() {
        downUtil.downFile(downUrl, downPath);
    }

    @Override
    public void noSure() {
        downUtil.cancelDown();
    }

    private void showNotification() {
        notification = new Notification();

        notification.tickerText = "下载中";
        notification.when = System.currentTimeMillis();
        notification.icon = R.mipmap.ic_launcher;
        notification.flags = Notification.FLAG_AUTO_CANCEL; // 通知被点击后自动消失

        notification.contentView = new RemoteViews(context.getPackageName(), R.layout.notify_layout);

        notificationManager.notify(NOTIFY_ID, notification);
    }

    private void updateProgress(int progress) {
        if (null != notification) {
            notification.contentView.setProgressBar(R.id.pBar, 100, progress, false);
            notification.contentView.setTextViewText(R.id.progress_tv, "下载" + progress + "%");
            notificationManager.notify(NOTIFY_ID, notification);
        }
    }

    private void updateOnDownloadComplete() {
        if (null != notification) {
            notification.contentView.setProgressBar(R.id.pBar, 100, 100, false);
            notification.contentView.setTextViewText(R.id.progress_tv, "下载完成");
            notificationManager.notify(NOTIFY_ID, notification);
            notificationManager.cancel(NOTIFY_ID);
        }
    }

    public boolean isDownloading() {
        return isDownloading;
    }
}
