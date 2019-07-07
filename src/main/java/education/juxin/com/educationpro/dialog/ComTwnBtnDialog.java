package education.juxin.com.educationpro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import education.juxin.com.educationpro.R;

/**
 * 带有两个按钮的dialog
 * Created on 2018/3/21.
 */
public class ComTwnBtnDialog extends Dialog implements View.OnClickListener {

    public static final int DIALOG_CLEAR_CACHE = 1;
    public static final int DIALOG_VERSION_UPDATE = 2;
    public static final int DIALOG_EXIT_LOGIN = 3;
    public static final int DIALOG_CARE = 4;
    public static final int DIALOG_COLLECT_TYPE = 5;
    public static final int DIALOG_CLEAR_MY_CACHE = 6;
    public static final int DIALOG_4G_PLAY = 7;
    public static final int DIALOG_4G_DOWNLOAD = 8;

    private Activity activity;

    private TextView titleTv;
    private Button leftBtn;
    private Button rightBtn;

    private String titleStr;

    private String leftBtnStr;
    private int leftTxtColor;
    private Drawable leftBtnColor;

    private String rightBtnStr;
    private int rightTxtColor;
    private Drawable rightBtnColor;

    private IDialogBtnClickListener dialogBtnClickListener;

    public interface IDialogBtnClickListener {
        void onDialogLeftBtnClick();

        void onDialogRightBtnClick();
    }

    public ComTwnBtnDialog(@NonNull Activity context, int dialogType) {
        this(context, R.style.BottomPopupDialog, dialogType);
    }

    public ComTwnBtnDialog(@NonNull Activity context, int themeResId, int dialogType) {
        super(context, themeResId);

        this.activity = context;

        Resources res = activity.getResources();
        switch (dialogType) {
            case DIALOG_CLEAR_CACHE:
                titleStr = res.getString(R.string.clear_video_cache);

                leftBtnStr = res.getString(R.string.cancel);
                leftTxtColor = res.getColor(R.color.not_psd);
                leftBtnColor = res.getDrawable(R.drawable.code_left_bottom);

                rightBtnStr = res.getString(R.string.confirm);
                rightTxtColor = res.getColor(R.color.bg_white);
                rightBtnColor = res.getDrawable(R.drawable.code_right_red_bottom);
                break;

            case DIALOG_CLEAR_MY_CACHE:
                titleStr = "确认清除我的缓存视频";

                leftBtnStr = res.getString(R.string.cancel);
                leftTxtColor = res.getColor(R.color.not_psd);
                leftBtnColor = res.getDrawable(R.drawable.code_left_bottom);

                rightBtnStr = res.getString(R.string.confirm);
                rightTxtColor = res.getColor(R.color.bg_white);
                rightBtnColor = res.getDrawable(R.drawable.code_right_red_bottom);
                break;

            case DIALOG_VERSION_UPDATE:
                titleStr = res.getString(R.string.new_version_update);

                leftBtnStr = res.getString(R.string.cancel);
                leftTxtColor = res.getColor(R.color.not_psd);
                leftBtnColor = res.getDrawable(R.drawable.code_left_bottom);

                rightBtnStr = res.getString(R.string.confirm);
                rightTxtColor = res.getColor(R.color.bg_white);
                rightBtnColor = res.getDrawable(R.drawable.code_right_green_bottom);
                break;

            case DIALOG_EXIT_LOGIN:
                titleStr = res.getString(R.string.exit_login);

                leftBtnStr = res.getString(R.string.cancel);
                leftTxtColor = res.getColor(R.color.not_psd);
                leftBtnColor = res.getDrawable(R.drawable.code_left_bottom);

                rightBtnStr = res.getString(R.string.confirm);
                rightTxtColor = res.getColor(R.color.bg_white);
                rightBtnColor = res.getDrawable(R.drawable.code_right_green_bottom);
                break;

            case DIALOG_CARE:
                titleStr = res.getString(R.string.user_cate);

                leftBtnStr = res.getString(R.string.cancel);
                leftTxtColor = res.getColor(R.color.not_psd);
                leftBtnColor = res.getDrawable(R.drawable.code_left_bottom);

                rightBtnStr = res.getString(R.string.confirm);
                rightTxtColor = res.getColor(R.color.bg_white);
                rightBtnColor = res.getDrawable(R.drawable.code_right_green_bottom);
                break;

            case DIALOG_COLLECT_TYPE:
                titleStr = res.getString(R.string.user_collect);

                leftBtnStr = res.getString(R.string.cancel);
                leftTxtColor = res.getColor(R.color.not_psd);
                leftBtnColor = res.getDrawable(R.drawable.code_left_bottom);

                rightBtnStr = res.getString(R.string.confirm);
                rightTxtColor = res.getColor(R.color.bg_white);
                rightBtnColor = res.getDrawable(R.drawable.code_right_green_bottom);
                break;

            case DIALOG_4G_PLAY:
                titleStr = "您正在使用流量,确认继续播放";

                leftBtnStr = res.getString(R.string.cancel);
                leftTxtColor = res.getColor(R.color.not_psd);
                leftBtnColor = res.getDrawable(R.drawable.code_left_bottom);

                rightBtnStr = res.getString(R.string.confirm);
                rightTxtColor = res.getColor(R.color.bg_white);
                rightBtnColor = res.getDrawable(R.drawable.code_right_green_bottom);
                break;

            case DIALOG_4G_DOWNLOAD:
                titleStr = "您正在使用流量,确认继续下载";

                leftBtnStr = res.getString(R.string.cancel);
                leftTxtColor = res.getColor(R.color.not_psd);
                leftBtnColor = res.getDrawable(R.drawable.code_left_bottom);

                rightBtnStr = res.getString(R.string.confirm);
                rightTxtColor = res.getColor(R.color.bg_white);
                rightBtnColor = res.getDrawable(R.drawable.code_right_green_bottom);
                break;
            //...有其他类似Dialog在这里添加样式设置
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_com_two_btn);

        initUI();
    }

    private void initUI() {
        titleTv = findViewById(R.id.tip_tv);
        leftBtn = findViewById(R.id.left_btn);
        rightBtn = findViewById(R.id.right_btn);

        titleTv.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        setTitleContent(titleStr);
        setLeftBtnStyles(true, leftBtnStr, leftTxtColor, leftBtnColor);
        setRightBtnStyles(true, rightBtnStr, rightTxtColor, rightBtnColor);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                dialogBtnClickListener.onDialogLeftBtnClick();
                dismiss();
                break;

            case R.id.right_btn:
                dialogBtnClickListener.onDialogRightBtnClick();
                dismiss();
                break;

            default:
                break;
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

    public void setTitleContent(String titleStr) {
        if (titleTv != null) {
            titleTv.setText(titleStr);
        }
    }

    public void setLeftBtnStyles(boolean needShow, String tipStr, int txtColorId, Drawable btnColorId) {
        if (leftBtn != null) {
            leftBtn.setVisibility(needShow ? View.VISIBLE : View.GONE);
            leftBtn.setText(tipStr);
            leftBtn.setTextColor(txtColorId);
            leftBtn.setBackground(btnColorId);
        }
    }

    public void setRightBtnStyles(boolean needShow, String tipStr, int txtColorId, Drawable btnColorId) {
        if (rightBtn != null) {
            rightBtn.setVisibility(needShow ? View.VISIBLE : View.GONE);
            rightBtn.setText(tipStr);
            rightBtn.setTextColor(txtColorId);
            rightBtn.setBackground(btnColorId);
        }
    }

    public void setDialogBtnClickListener(IDialogBtnClickListener dialogBtnClickListener) {
        this.dialogBtnClickListener = dialogBtnClickListener;
    }
}