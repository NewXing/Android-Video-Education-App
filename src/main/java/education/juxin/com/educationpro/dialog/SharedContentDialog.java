package education.juxin.com.educationpro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.bean.CourseDetailBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.interfaces.ICheckCourseValid;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseDetailActivity;
import education.juxin.com.educationpro.ui.activity.mine.CollectActivity;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 创建者：王兴
 * 创建时间：2015/12/19 20:39
 * 类说明：GoUpPopupDialog - 从屏幕底部向上升起的Dialog
 */
public class SharedContentDialog implements View.OnClickListener {

    private Dialog dialog;
    private Activity activity;
    private ImageView courseImage;
    private TextView courseMoney;
    private TextView courseName;
    private String courseId;
    private String lessonId;

    public SharedContentDialog(Activity activity, String courseId, String lessonId) {
        this.activity = activity;
        this.courseId = courseId;
        this.lessonId = lessonId;

        initDialog();
        dialog.show();
    }

    private void initDialog() {
        dialog = new Dialog(activity, R.style.BottomPopupDialog);

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        ViewGroup decorView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        View dialogRootView = layoutInflater.inflate(R.layout.dialog_share_content, decorView, false);
        dialog.setContentView(dialogRootView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.AnimBottomPopupDialogBig);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = activity.getWindowManager().getDefaultDisplay().getHeight();
            dialog.onWindowAttributesChanged(wl);

            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        dialog.setCanceledOnTouchOutside(true);

        dialog.findViewById(R.id.turn_activity_btn).setOnClickListener(this);
        dialog.findViewById(R.id.close_dialog_btn).setOnClickListener(this);
        courseImage = dialog.findViewById(R.id.course_img);
        courseMoney = dialog.findViewById(R.id.course_money_tv);
        courseName = dialog.findViewById(R.id.course_name_tv);

        updateData(courseId, lessonId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_dialog_btn:
                dismiss();
                break;

            case R.id.turn_activity_btn:
                dismiss();
                CourseDetailActivity.checkCourseValid(activity, courseId, lessonId,
                        new ICheckCourseValid() {
                            @Override
                            public void isValid() {
                                Intent intent = new Intent(activity, CourseDetailActivity.class);
                                intent.putExtra("id_course_detail", courseId);
                                intent.putExtra("lesson_id", lessonId);
                                activity.startActivity(intent);
                            }

                            @Override
                            public void isInvalid() {
                            }
                        });
                break;
        }
    }

    private void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void updateUI(CourseDetailBean detailBean) {
        ImageUtils.GlideUtil(activity, detailBean.getData().getCourseCoverImg(), courseImage);
        courseMoney.setText(String.format(activity.getString(R.string.money_only_with_number), FormatNumberUtil.doubleFormat(detailBean.getData().getPrice())));
        courseName.setText(detailBean.getData().getTitle());
    }

    private void updateData(String courseId, String lessonId) {
        OkHttpUtils.get()
                .url(HttpConstant.COURSE_DETAILS + courseId)
                .addParams("lessonId", lessonId)
                .build()
                .execute(new HttpCallBack<CourseDetailBean>(CourseDetailBean.class, true, activity) {
                    @Override
                    public void onResponse(CourseDetailBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData() != null) {
                            updateUI(response);
                        } else {
                            dismiss();
                        }
                    }
                });
    }
}
