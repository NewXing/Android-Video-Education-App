package education.juxin.com.educationpro.party3.umeng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import education.juxin.com.educationpro.util.LogManager;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 友盟分享工具类
 */
public class ShareUtils {

    public static class DefaultContent {
        public static String url = "https://www.baidu.com/";
        public static String text = "你点击之后应该会到百度的页面";
        public static String title = "我就是想试一试";
        public static String imageUrl = "http://dev.umeng.com/images/tab2_1.png";
    }

    /**
     * 分享链接
     */
    public static void shareWeb(Activity activity, String WebUrl, String title, String description, String imageUrl, int imageID, SHARE_MEDIA platform) {
        UMWeb web = new UMWeb(WebUrl); // 连接地址
        web.setTitle(title); // 标题
        web.setDescription(description); // 描述
        if (TextUtils.isEmpty(imageUrl)) {
            web.setThumb(new UMImage(activity, imageID)); // 本地缩略图
        } else {
            web.setThumb(new UMImage(activity, imageUrl)); // 网络缩略图
        }

        new ShareAction(activity)
                .setPlatform(platform)
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                    }

                    @Override
                    public void onResult(final SHARE_MEDIA share_media) {
                        ToastManager.showShortToast(share_media + " 分享成功");
                    }

                    @Override
                    public void onError(final SHARE_MEDIA share_media, final Throwable throwable) {
                        if (throwable != null) {
                            String meg = throwable.getMessage();
                            LogManager.e("ShareAction onError Message" + meg);
                        }
                        ToastManager.showShortToast(share_media + " 分享失败");
                    }

                    @Override
                    public void onCancel(final SHARE_MEDIA share_media) {
                        ToastManager.showShortToast(share_media + " 分享取消");
                    }
                })
                .share();
    }


    /**
     * 分享文本
     */
    public static void shareText(Activity activity, String contentStr, SHARE_MEDIA platform) {
        new ShareAction(activity)
                .setPlatform(platform)
                .withText(contentStr)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        ToastManager.showShortToast(share_media + " 分享成功");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        if (throwable != null) {
                            String meg = throwable.getMessage();
                            LogManager.e("ShareAction onError Message" + meg);
                        }
                        ToastManager.showShortToast(share_media + " 分享失败");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        ToastManager.showShortToast(share_media + " 分享取消");
                    }
                })
                .share();
    }

    /**
     * QQ纯文本分享，官方正式Api不支持
     */
    public static void shareTxtToQQ(Context context, String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        try {
            sendIntent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
            Intent chooserIntent = Intent.createChooser(sendIntent, "选择分享途径");
            if (chooserIntent == null) {
                return;
            }
            context.startActivity(chooserIntent);
        } catch (Exception e) {
            context.startActivity(sendIntent);
        }
    }

    /**
     * 微信纯文本分享，官方Api不支持
     */
    public static void shareTxtToWx(Context context, String content) {
        Intent intent = new Intent();
        intent.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setAction("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastManager.showShortToast("未安装微信,或微信版本过低！");
        }
    }

}
