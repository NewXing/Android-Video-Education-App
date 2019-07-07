package education.juxin.com.educationpro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseRecyclerAdapter;
import education.juxin.com.educationpro.base.BaseRecyclerHolder;
import education.juxin.com.educationpro.bean.GiftBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 打赏礼品的选择 dialog  对打赏的礼品进行选择
 * Created by Administrator on 2017/12/1.
 */
public class SelectRewardDialog extends Dialog {

    private Activity activity;
    private String mCourseId;
    private String mLessonId;

    private SelectRewardDialog(Activity activity) {
        this(activity, R.style.BottomPopupDialog);
    }

    private SelectRewardDialog(Activity activity, int theme) {
        super(activity, theme);
        this.activity = activity;
    }

    public String getCourseId() {
        return mCourseId;
    }

    public void setCourseId(String courseId) {
        this.mCourseId = courseId;
    }

    public String getLessonId() {
        return mLessonId;
    }

    public void setLessonId(String lessonId) {
        this.mLessonId = lessonId;
    }

    private static List<GiftBean.DataBean> giftList = new ArrayList<>();

    public static class Builder {
        private Activity activity;
        private SelectRewardDialog dialog;
        private BaseRecyclerAdapter giftAdapter;

        public Builder(Activity activity) {
            this.activity = activity;
            dialog = new SelectRewardDialog(activity);
        }

        public void setCourseId(String courseId) {
            dialog.setCourseId(courseId);
        }

        private String getCourseId() {
            return dialog.getCourseId();
        }

        public void setLesson(String lessonId) {
            dialog.setLessonId(lessonId);
        }

        private String getLesson() {
            return dialog.getLessonId();
        }

        public SelectRewardDialog create() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_present_layout);
            dialog.findViewById(R.id.im_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Custom Views
            initCustomUI(dialog);
            getGiftListData();

            return dialog;
        }

        private void initCustomUI(final Dialog dialog) {
            RecyclerView rvView = dialog.findViewById(R.id.rv_view);
            rvView.setLayoutManager(new GridLayoutManager(activity, 3));
            giftAdapter = new BaseRecyclerAdapter<GiftBean.DataBean>(R.layout.dialog_gift_item, giftList) {
                @Override
                public void itemViewBindData(BaseRecyclerHolder viewHolder, int position, GiftBean.DataBean itemData) {
                    ImageView imgGift = (ImageView) viewHolder.findViewById(R.id.img_gift);
                    TextView giftPrice = (TextView) viewHolder.findViewById(R.id.gift_price);

                    ImageUtils.GlideUtil(activity, itemData.getImgUrl(), imgGift);
                    giftPrice.setText(String.format(activity.getString(R.string.gift_num), itemData.getPrice()));
                }
            };
            rvView.setAdapter(giftAdapter);
            giftAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    dialog.dismiss();

                    PayCustomDialog.Builder builder = new PayCustomDialog.Builder(activity);
                    builder.setGiftId(giftList.get(position).getId());
                    builder.setCourseId(getCourseId());
                    builder.setLessonId(getLesson());
                    builder.setSendWord(giftList.get(position).getContent());
                    builder.create().show();
                }
            });
        }

        //获取礼品的列表
        private void getGiftListData() {
            OkHttpUtils.get()
                    .url(HttpConstant.USER_GIFT_LIST)
                    .build()
                    .execute(new HttpCallBack<GiftBean>(GiftBean.class, true, activity) {
                        @Override
                        public void onResponse(GiftBean response, int id) {
                            if (response.getCode() != 0) {
                                ToastManager.showShortToast(response.getMsg());
                                return;
                            }

                            if (response.getData() != null) {
                                giftList.clear();
                                giftList.addAll(response.getData());
                                giftAdapter.notifyDataSetChanged();
                            }
                        }
                    });
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
