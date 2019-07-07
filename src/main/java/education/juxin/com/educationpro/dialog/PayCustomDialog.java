package education.juxin.com.educationpro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.bean.GiftDetailBean;
import education.juxin.com.educationpro.bean.PreOrderBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.ui.activity.home.PayModeActivity;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.util.LogManager;
import education.juxin.com.educationpro.bean.ProOrderParam;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.RoundImageView;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.MediaType;

/**
 * 支付订单的详情页  dialog
 * Created by Administrator on 2017/12/1.
 */
public class PayCustomDialog extends Dialog {

    private Activity activity;
    private String mCourseId;
    private String mLessonId;
    private String mGiftId;
    private String mSendWords;

    private PayCustomDialog(Activity activity) {
        this(activity, R.style.BottomPopupDialog);
    }

    private PayCustomDialog(Activity activity, int theme) {
        super(activity, theme);
        this.activity = activity;
    }

    public String getCourseId() {
        return mCourseId;
    }

    public void setCourseId(String mCourseId) {
        this.mCourseId = mCourseId;
    }

    public String getLessonId() {
        return mLessonId;
    }

    public void setLessonId(String lessonId) {
        this.mLessonId = lessonId;
    }

    public String getGiftId() {
        return mGiftId;
    }

    public void setGiftId(String mGiftId) {
        this.mGiftId = mGiftId;
    }

    public String getSendWords() {
        return mSendWords;
    }

    public void setSendWords(String mSendWords) {
        this.mSendWords = mSendWords;
    }

    public static class Builder implements View.OnClickListener {
        private PayCustomDialog dialog;
        private Activity activity;

        private TextView inputSendWordTv;
        private TextView inputWordsNumTv;
        private EditText inputNumEdit;
        private RoundImageView giftImg;
        private TextView perPriceTv;
        private TextView payPriceTv;

        private int number = 1;
        private double totalPrices;
        private double perPrice;

        public Builder(Activity activity) {
            this.activity = activity;
            dialog = new PayCustomDialog(activity);
        }

        public void setGiftId(String id) {
            dialog.setGiftId(id);
        }

        private String getGiftId() {
            return dialog.getGiftId();
        }

        public void setCourseId(String courseId) {
            dialog.setCourseId(courseId);
        }

        private String getCourseId() {
            return dialog.getCourseId();
        }

        public void setLessonId(String lessonId) {
            dialog.setLessonId(lessonId);
        }

        private String getLessonId() {
            return dialog.getLessonId();
        }

        public void setSendWord(String sendWord) {
            dialog.setSendWords(sendWord);
        }

        private String getSendWord() {
            return dialog.getSendWords();
        }

        public PayCustomDialog create() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_pay_layout);

            dialog.findViewById(R.id.im_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Custom Views
            initCustomUI(dialog);
            getGiftInfoData(getGiftId());
            return dialog;
        }

        private void initCustomUI(final Dialog dialog) {
            inputSendWordTv = dialog.findViewById(R.id.input_send_word_tv);
            inputWordsNumTv = dialog.findViewById(R.id.input_word_num_tv);
            inputNumEdit = dialog.findViewById(R.id.input_number_edit);
            giftImg = dialog.findViewById(R.id.img_gifts);
            perPriceTv = dialog.findViewById(R.id.each_money_tv);
            payPriceTv = dialog.findViewById(R.id.pay_total_money_tv);

            inputSendWordTv.setText(getSendWord());

            inputNumEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (StringUtils.isNumeric(s)) {
                        number = Integer.valueOf(s.toString().trim());
                        payPriceTv.setText(String.format(activity.getString(R.string.number_yuan), FormatNumberUtil.doubleFormat(number * perPrice)));
                        totalPrices = perPrice * number;
                    }
                }
            });

            dialog.findViewById(R.id.minus_btn).setOnClickListener(this);
            dialog.findViewById(R.id.add_btn).setOnClickListener(this);
            dialog.findViewById(R.id.btn_pay).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_pay:
                    reqProOrder();
                    break;

                case R.id.minus_btn:
                    if (number > 1) {
                        --number;
                    }
                    inputNumEdit.setText(String.valueOf(number));

                    totalPrices = perPrice * number;
                    payPriceTv.setText(String.format(activity.getString(R.string.number_yuan), FormatNumberUtil.doubleFormat(totalPrices)));
                    break;

                case R.id.add_btn:
                    ++number;
                    inputNumEdit.setText(String.valueOf(number));

                    totalPrices = perPrice * number;
                    payPriceTv.setText(String.format(activity.getString(R.string.number_yuan), FormatNumberUtil.doubleFormat(totalPrices)));
                    break;
            }
        }

        private void getGiftInfoData(String id) {
            OkHttpUtils.get()
                    .url(HttpConstant.USER_GIFT_DETAIL_INFO + id)
                    .build()
                    .execute(new HttpCallBack<GiftDetailBean>(GiftDetailBean.class, false, activity) {
                        @Override
                        public void onResponse(GiftDetailBean response, int id) {
                            if (response.getCode() != 0) {
                                ToastManager.showShortToast(response.getMsg());
                                return;
                            }

                            ImageUtils.GlideUtil(activity, response.getData().getImgUrl(), giftImg);
                            perPrice = Double.valueOf(response.getData().getPrice());
                            totalPrices = perPrice;
                            perPriceTv.setText(String.format(activity.getString(R.string.gift_num), FormatNumberUtil.doubleFormat(perPrice)));
                            payPriceTv.setText(String.format(activity.getString(R.string.number_yuan), FormatNumberUtil.doubleFormat(totalPrices)));
                        }
                    });
        }

        private void reqProOrder() {
            String currPriceStr = String.valueOf(totalPrices);
            String numberStr = inputNumEdit.getText().toString().trim();
            if (numberStr.isEmpty()) {
                ToastManager.showShortToast("数量不能为空！");
                return;
            }
            if ("0".equals(numberStr)) {
                ToastManager.showShortToast("数量不能为0！");
                return;
            }

            dialog.dismiss();

            ProOrderParam param = new ProOrderParam();
            param.setOrderType("2");
            param.setTotalAmount(currPriceStr);
            param.setGiftNum(numberStr);
            param.setGiftId(getGiftId());
            param.setCourseId(getCourseId());
            param.setLessonId(getLessonId());
            param.setPassiveInvitationCode("");

            String jsonStr = new Gson().toJson(param);
            LogManager.e("jsonStr=" + jsonStr);

            OkHttpUtils.postString()
                    .url(HttpConstant.CREATE_PRE_ORDER)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonStr)
                    .build()
                    .execute(new HttpCallBack<PreOrderBean>(PreOrderBean.class, true, activity) {
                        @Override
                        public void onResponse(PreOrderBean response, int id) {
                            if (response.getCode() != 0) {
                                ToastManager.showShortToast(response.getMsg());
                                return;
                            }

                            Intent intent = new Intent(activity, PayModeActivity.class);
                            intent.putExtra("total_amount", currPriceStr);
                            intent.putExtra("order_id", response.getData().getOrderId());
                            activity.startActivity(intent);
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