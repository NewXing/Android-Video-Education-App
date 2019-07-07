package education.juxin.com.educationpro.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 忘记密码 手机码验证
 * The type Forget psd step one activity.
 */
public class ForgetPsdStepOneActivity extends BaseActivity implements View.OnClickListener {

    private Button sendSmsBtn;
    private EditText etForgetPsd;
    private EditText etAuthCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initToolbar(true, R.string.forget_password);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.next_step_btn).setOnClickListener(this);
        sendSmsBtn = findViewById(R.id.send_sms_btn);
        sendSmsBtn.setOnClickListener(this);
        etForgetPsd = findViewById(R.id.et_forget_psd);
        etAuthCode = findViewById(R.id.auto_code_et);
    }

    @Override
    public void onClick(View v) {
        String phoneNumStr = etForgetPsd.getText().toString();
        String authCodeStr = etAuthCode.getText().toString();

        switch (v.getId()) {
            case R.id.next_step_btn:
                if (StringUtils.isEmpty(phoneNumStr)) {
                    ToastManager.showShortToast("电话号码不能为空");
                    return;
                }
                if (!StringUtils.isPhoneNumber(phoneNumStr)) {
                    ToastManager.showShortToast("请输入正确格式的电话号码！");
                    return;
                }
                if (StringUtils.isEmpty(authCodeStr)) {
                    ToastManager.showShortToast("请输入验证码！");
                    return;
                }
                getVerifyData(phoneNumStr,authCodeStr);

                break;

            case R.id.send_sms_btn:
                if (StringUtils.isEmpty(phoneNumStr)) {
                    ToastManager.showShortToast("电话号码不能为空");
                    return;
                }
                if (!StringUtils.isPhoneNumber(phoneNumStr)) {
                    ToastManager.showShortToast("请输入正确格式的电话号码！");
                    return;
                }

                CusCountDown cusCountDown = new CusCountDown(this, 60);
                cusCountDown.start();

                sendSmsHttpReq(phoneNumStr);
                break;

            default:
                break;
        }
    }
        /*验证*/
    private void getVerifyData(String phone,String code) {
        OkHttpUtils.post()
                .url(HttpConstant.NEXT_VERFIY_CODE)
                .addParams("phone",phone)
                .addParams("code",code)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class,false,ForgetPsdStepOneActivity.this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                    if(response.getCode()!=0){
                        ToastManager.showShortToast(response.getMsg());
                        return;
                    }
                        Intent intent = new Intent(ForgetPsdStepOneActivity.this, ForgetPsdStepTwoActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("authCode", code);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }
                });

    }

    private void sendSmsHttpReq(String phoneNumStr) {
        OkHttpUtils.get()
                .url(HttpConstant.SEND_SMS + phoneNumStr)
                .addParams("type", String.valueOf(2))
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        ToastManager.showShortToast(response.getCode() != 0 ? response.getMsg() : getString(R.string.send_sms_success_tip));
                    }
                });
    }

    public class CusCountDown extends CountDownTimer {

        private static final int TIME = 1000;
        private Context context;

        CusCountDown(Context context, long millisInFuture) {
            super(millisInFuture * 1000, TIME);
            this.context = context;
        }

        @Override
        public void onFinish() {
            sendSmsBtn.setText(context.getString(R.string.send_sms));
            sendSmsBtn.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendSmsBtn.setText(String.format(context.getString(R.string.count_down), millisUntilFinished / TIME));
            sendSmsBtn.setClickable(false);
        }
    }

}
