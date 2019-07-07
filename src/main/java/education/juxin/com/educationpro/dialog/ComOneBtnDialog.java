package education.juxin.com.educationpro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import education.juxin.com.educationpro.R;

/**
 * 带有一个按钮的dialog
 * Created by Administrator on 2018/3/21 0021.
 */
public class ComOneBtnDialog extends Dialog implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    public static final int DIALOG_SEX_SELECT = 1;
    public static final int DIALOG_PAY_FAIL = 2;

    private Activity activity;
    private int dialogType;
    private int selectedId;

    private String tipStr;
    public static String sexTypeStr = "";

    private IDialogBtnClickListener dialogBtnClickListener;

    public interface IDialogBtnClickListener {
        void onDialogBtnClick();
    }

    private IDialogSureSelectListener dialogSureSelectListener;

    public interface IDialogSureSelectListener {
        void onDialogSureSelect(int selected);
    }

    public ComOneBtnDialog(@NonNull Activity context, int dialogType) {
        this(context,R.style.BottomPopupDialog, dialogType);
    }

    public ComOneBtnDialog(@NonNull Activity context, int themeResId, int dialogType) {
        super(context, themeResId);

        this.activity = context;
        this.dialogType = dialogType;

        Resources res = activity.getResources();
        switch (dialogType) {
            case DIALOG_SEX_SELECT:
                tipStr = "";
                break;

            case DIALOG_PAY_FAIL:
                tipStr = res.getString(R.string.pay_success);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_com_one_btn);

        initUI();
    }

    private void initUI() {
        TextView tipText = findViewById(R.id.tip_tv);
        Button sureBtn = findViewById(R.id.sure_btn);
        RadioGroup sexRG = findViewById(R.id.sex_radio_group);
        RadioButton manRB = findViewById(R.id.man_radio_btn);
        RadioButton womanRB = findViewById(R.id.woman_radio_btn);

        sureBtn.setOnClickListener(this);
        sexRG.setOnCheckedChangeListener(this);

        tipText.setText(tipStr);

        switch (dialogType) {
            case DIALOG_SEX_SELECT:
                sexRG.setVisibility(View.VISIBLE);
                switch (sexTypeStr) {
                    case "男":
                        manRB.setChecked(true);
                        womanRB.setChecked(false);
                        break;
                    case "女":
                        manRB.setChecked(false);
                        womanRB.setChecked(true);
                        break;
                    case "":
                        manRB.setChecked(false);
                        womanRB.setChecked(false);
                        break;
                }
                tipText.setVisibility(View.GONE);
                break;

            case DIALOG_PAY_FAIL:
                sexRG.setVisibility(View.GONE);
                tipText.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sure_btn:
                switch (dialogType) {
                    case DIALOG_SEX_SELECT:
                        dialogSureSelectListener.onDialogSureSelect(selectedId);
                        break;

                    case DIALOG_PAY_FAIL:
                        dialogBtnClickListener.onDialogBtnClick();
                        break;

                    default:
                        break;
                }
                dismiss();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        this.selectedId = id;
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

    public void setDialogBtnClickListener(IDialogBtnClickListener dialogBtnClickListener) {
        this.dialogBtnClickListener = dialogBtnClickListener;
    }

    public void setDialogSureSelectListener(IDialogSureSelectListener dialogSureSelectListener) {
        this.dialogSureSelectListener = dialogSureSelectListener;
    }
}
