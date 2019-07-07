package education.juxin.com.educationpro.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tencent.mm.opensdk.constants.Build;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Map;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.bean.AppPayParamBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.party3.alipay.OrderInfoUtil2_0;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.ToastManager;
import education.juxin.com.educationpro.wxapi.WxApiUtils;
import me.shihao.library.XRadioGroup;

/**
 * 收银台 支付方式
 * The type Pay mode activity.
 */
public class PayModeActivity extends BaseActivity implements XRadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private CheckBox agreeProtocolChk;

    private long preTime = 0;
    private String payType = "2";
    private String totalAmount;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_mode);
        initToolbar(true, R.string.check_stand);

        totalAmount = getIntent().getStringExtra("total_amount");
        orderId = getIntent().getStringExtra("order_id");

        initUI();
    }

    private void initUI() {
        TextView tvCoursePrice = findViewById(R.id.course_price_tv);
        tvCoursePrice.setText(String.format(getString(R.string.money_only_with_number), FormatNumberUtil.doubleFormat(totalAmount)));

        agreeProtocolChk = findViewById(R.id.agree_protocol_check);
        agreeProtocolChk.setChecked(true);

        TextView tvPayProtocol = findViewById(R.id.pay_protocol_tv);
        tvPayProtocol.setText(StringUtils.setUnderlineSpan("同意《用户购买协议》"));
        tvPayProtocol.setOnClickListener(this);

        XRadioGroup xRadioGroup = findViewById(R.id.pay_radioGroup);
        xRadioGroup.setOnCheckedChangeListener(this);

        findViewById(R.id.sure_pay_btn).setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(XRadioGroup xRadioGroup, int id) {
        switch (id) {
            case R.id.we_chat_pay_rb:
                payType = "2";
                break;

            case R.id.ali_pay_rb:
                payType = "1";
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_pay_btn:
                long nowTime = System.currentTimeMillis();
                if (nowTime - preTime < 1000) {
                    preTime = nowTime;
//                    ToastManager.showShortToast("请稍后点击");
                    return;
                }
                preTime = nowTime;

                if (!agreeProtocolChk.isChecked()) {
                    ToastManager.showShortToast("请同意用户协议！");
                    return;
                }
                reqPay();
                break;

            case R.id.pay_protocol_tv:
                Intent intent = new Intent(PayModeActivity.this, BuyProtocolActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void reqPay() {
        OkHttpUtils.post()
                .url(HttpConstant.GET_PAY_PARAMS)
                .addParams("orderId", orderId)
                .addParams("payType", payType)
                .build()
                .execute(new HttpCallBack<AppPayParamBean>(AppPayParamBean.class, true, this) {
                    @Override
                    public void onResponse(AppPayParamBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        AppPayParamBean.AppPayParamData.WechatPayResult wxPayData = response.getData().getWechatPayResult();
                        if (wxPayData != null) {
                            if (response.getData().getCode() != null) {
                                SPHelper.setSimpleKeyValue(PayModeActivity.this, "pay_success_invite_code", response.getData().getCode());
                            }
                            if (WxApiUtils.getApi().getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
                                ToastManager.showShortToast("微信版本过低，不支持支付！");
                                return;
                            }
                            WxApiUtils.reqWxPayApi(wxPayData);
                        }

                        Map<String, String> aliPayData = response.getData().getAliPayResult();
                        if (aliPayData != null) {
                            if (response.getData().getCode() != null) {
                                SPHelper.setSimpleKeyValue(PayModeActivity.this, "pay_success_invite_code", response.getData().getCode());
                            }
                            OrderInfoUtil2_0.aliPayV2(PayModeActivity.this, aliPayData);
                        }
                    }
                });
    }

}
