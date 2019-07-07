package education.juxin.com.educationpro.download;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import education.juxin.com.educationpro.R;

public class BigFileDownloadDialog {

    private DownDialogListener dialogListener;

    private Dialog dialog;
    private ProgressBar downloadProgress;
    private TextView titleTv;
    private TextView downloadSpeedTv;
    private TextView descUpdateTv;
    private Button dialogYesBtn;
    private Button dialogNoBtn;

    private OnBtnClickListener onBtnClickListener;

    interface OnBtnClickListener {
        void onNoBtnClick();

        void onYesBtnClick();
    }

    public void setOnBtnClickListener(OnBtnClickListener onBtnClickListener) {
        this.onBtnClickListener = onBtnClickListener;
    }

    /**
     * 刷新dialog
     *
     * @param progress 进度
     * @param desc     下载秒速
     * @param speed    下载速度
     */
    public void updateView(int progress, String desc, long speed) {
        downloadProgress.setProgress(progress);
        titleTv.setTextColor(0xff1fbaf3);
        titleTv.setText("文件下载(" + desc + ")");
        downloadSpeedTv.setText(speed + " kb/s");
        if (desc.contains("异常")) {
            titleTv.setTextColor(Color.RED);
            titleTv.setText("下载异常，请重新启动程序");
        }
    }

    public BigFileDownloadDialog(final Context context) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.BottomPopupDialog);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.dialog_download);

        titleTv = dialog.findViewById(R.id.dialog_title);
        descUpdateTv = dialog.findViewById(R.id.tv_desc_update);
        downloadSpeedTv = dialog.findViewById(R.id.tv_speed);
        downloadSpeedTv.setVisibility(View.INVISIBLE);
        dialogNoBtn = dialog.findViewById(R.id.btn_dialog_no);
        dialogYesBtn = dialog.findViewById(R.id.btn_dialog_yes);
        downloadProgress = dialog.findViewById(R.id.update_progress);

        dialogYesBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                downloadProgress.setVisibility(View.VISIBLE);
                descUpdateTv.setVisibility(View.INVISIBLE);
                dialogYesBtn.setVisibility(View.GONE);
                downloadSpeedTv.setVisibility(View.VISIBLE);
                dialogListener.sure();
                if (onBtnClickListener != null) {
                    onBtnClickListener.onYesBtnClick();
                }
            }
        });

        dialogNoBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.noSure();
                    dismiss();
                    if (onBtnClickListener != null) {
                        onBtnClickListener.onNoBtnClick();
                    }
                }
            }
        });
    }

    public void show(String desc) {
        downloadProgress.setVisibility(View.INVISIBLE);
        descUpdateTv.setVisibility(View.VISIBLE);
        if (desc != null && desc.length() > 4) {
            descUpdateTv.setText(desc);
        }
        downloadSpeedTv.setText("0kb/s");
        dialog.show();
    }

    public void dismiss() {
        if (dialog == null) {
            return;
        }
        dialogYesBtn.setVisibility(View.VISIBLE);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setOnDialogClickListener(DownDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public interface DownDialogListener {
        void sure();

        void noSure();
    }

}
