package education.juxin.com.educationpro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import education.juxin.com.educationpro.R;

/**
 * 分享模式的页  dialog
 * Created by Administrator on 2017/12/1.
 */
public class SharedRuleDialog extends Dialog {

    private Activity activity;

    SharedRuleDialog(Activity activity) {
        this(activity, R.style.BottomPopupDialog);
    }

    private SharedRuleDialog(Activity activity, int theme) {
        super(activity, theme);
        this.activity = activity;
    }

    public static class Builder {
        private Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public SharedRuleDialog create() {
            final SharedRuleDialog dialog = new SharedRuleDialog(activity);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_share_rule_layout);

            dialog.findViewById(R.id.im_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Custom Views
            initCustomUI(dialog);

            return dialog;
        }

        private void initCustomUI(final Dialog dialog) {
            TextView contentTv = dialog.findViewById(R.id.tv_shared_content);

            contentTv.setMovementMethod(ScrollingMovementMethod.getInstance());
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