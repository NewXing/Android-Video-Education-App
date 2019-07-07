package education.juxin.com.educationpro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import education.juxin.com.educationpro.ProConstant;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.party3.umeng.ShareUtils;
import education.juxin.com.educationpro.util.ScreenUtils;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 支付之后 弹出的邀请码 dialog
 * Created by Administrator on 2017/12/1.
 */
public class InvitationCodeDialog extends Dialog {

    private Activity activity;
    private String mQRCodeStr;

    public String getQRCodeStr() {
        return mQRCodeStr;
    }

    public void setQRCodeStr(String QRCodeStr) {
        this.mQRCodeStr = QRCodeStr;
    }

    private InvitationCodeDialog(Activity activity) {
        this(activity, R.style.BottomPopupDialog);
    }

    private InvitationCodeDialog(Activity activity, int theme) {
        super(activity, theme);
        this.activity = activity;
    }

    public static class Builder implements View.OnClickListener {
        private Activity activity;
        private InvitationCodeDialog dialog;
        private TextView showCodeTv;
        private String mContentStr;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public InvitationCodeDialog create(String codeStr, String CourseTitle, String CourseId, String LessonId) {
            dialog = new InvitationCodeDialog(activity);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_code_layout);

            dialog.findViewById(R.id.im_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            // Custom Views
            initCustomUI(dialog, codeStr, CourseTitle, CourseId, LessonId);

            return dialog;
        }

        private void initCustomUI(final InvitationCodeDialog dialog, String codeStr, String courseTitle, String courseId, String lessonId) {
            showCodeTv = dialog.findViewById(R.id.show_code_tv);
            showCodeTv.setText(codeStr);

            dialog.findViewById(R.id.img_wechat).setOnClickListener(this);
            dialog.findViewById(R.id.img_qq).setOnClickListener(this);
            dialog.findViewById(R.id.copy_code_btn).setOnClickListener(this);

            ImageView imageView = dialog.findViewById(R.id.img_qr_code);
            Bitmap bitmapQRCode = QRCodeEncoder.syncEncodeQRCode(codeStr, ScreenUtils.dp2px(activity, 150));
            imageView.setImageBitmap(bitmapQRCode);

            mContentStr = "【" + courseTitle + "】，复制这条信息<￥>" + courseId + "&&" + lessonId + "<￥>后打开→→乾途App←← 邀请码: " + codeStr + " App下载地址: " + ProConstant.APP_DOWNLOAD_URL;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_wechat:
                    android.content.ClipboardManager cm1 = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (cm1 != null) {
                        cm1.setPrimaryClip(ClipData.newPlainText("sharedContentStr", mContentStr));
                        ToastManager.showShortToast("复制成功！");

                        ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                        Intent intentToWx = new Intent();
                        intentToWx.setAction(Intent.ACTION_MAIN);
                        intentToWx.addCategory(Intent.CATEGORY_LAUNCHER);
                        intentToWx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentToWx.setComponent(cmp);
                        activity.startActivity(intentToWx);
                    }
                    break;

                case R.id.img_qq:
                    ShareUtils.shareTxtToQQ(activity, mContentStr);
                    break;

                case R.id.copy_code_btn:
                    android.content.ClipboardManager cm2 = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (cm2 != null) {
                        cm2.setPrimaryClip(ClipData.newPlainText("qr_code", showCodeTv.getText()));
                        ToastManager.showShortToast("复制成功！");
                    }
                    break;
            }
        }
    }

    @Override
    public void show() {
        super.show();

        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        layoutParams.alpha = 0.5f;
        layoutParams.dimAmount = 0.5f;
        activity.getWindow().setAttributes(layoutParams);
    }

    @Override
    public void dismiss() {
        super.dismiss();

        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        layoutParams.alpha = 1.0f;
        layoutParams.dimAmount = 1.0f;
        activity.getWindow().setAttributes(layoutParams);
    }
}