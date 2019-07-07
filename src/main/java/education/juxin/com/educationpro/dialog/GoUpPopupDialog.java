package education.juxin.com.educationpro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import education.juxin.com.educationpro.R;

/**
 * 创建者：王兴
 * 创建时间：2015/12/19 20:39
 * 类说明：GoUpPopupDialog - 从屏幕底部向上升起的Dialog
 */
public class GoUpPopupDialog {
    private Activity activity;
    private View childView;
    private Dialog dialog;

    /**
     * @param activity  上下文
     * @param childView 加载Dialog布局上子布局
     */
    public GoUpPopupDialog(Activity activity, View childView) {
        this.activity = activity;
        this.childView = childView;
        initDialog();
        dialog.show();
    }

    private void initDialog() {
        dialog = new Dialog(activity, R.style.BottomPopupDialog);

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        ViewGroup decorView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        View dialogRootView = layoutInflater.inflate(R.layout.dialog_root_layout, decorView, false);
        dialog.setContentView(dialogRootView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        FrameLayout contentView = dialogRootView.findViewById(R.id.content_frameLayout);
        if (childView != null) {
            contentView.addView(childView);
        }

        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.AnimBottomPopupDialog);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = activity.getWindowManager().getDefaultDisplay().getHeight();
            dialog.onWindowAttributesChanged(wl);

            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        dialog.setCanceledOnTouchOutside(true);
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
