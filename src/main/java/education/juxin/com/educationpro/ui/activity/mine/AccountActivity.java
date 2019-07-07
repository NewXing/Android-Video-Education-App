package education.juxin.com.educationpro.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;

/**
 * 账号安全页面 短信验证
 * The type account activity.
 */
public class AccountActivity extends BaseActivity implements View.OnClickListener {

    private TextView phoneEt;
    private EditText authCodeEt;
    private Button sendSmsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhanghao);
        initToolbar(true, R.string.safe_account);
        initUI();
    }

    private void initUI() {
        phoneEt = findViewById(R.id.phone_et);
        phoneEt.setText((String) SPHelper.getSimpleParam(this, "phone", ""));

        authCodeEt = findViewById(R.id.auth_code_et);
        sendSmsBtn = findViewById(R.id.get_auth_code_btn);

        findViewById(R.id.next_step_btn).setOnClickListener(this);
        sendSmsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String phoneStr = phoneEt.getText().toString();
        String authCodeStr = authCodeEt.getText().toString();

        switch (v.getId()) {
            case R.id.next_step_btn:
                if (phoneStr.trim().isEmpty()) {
                    ToastManager.showShortToast("手机号不能为空！");
                    return;
                }

                if (!StringUtils.isPhoneNumber(phoneStr)) {
                    ToastManager.showShortToast("请输入正确格式的电话号码！");
                    return;
                }

                if (authCodeStr.trim().isEmpty()) {
                    ToastManager.showShortToast("验证码不能为空！");
                    return;
                }
                getVerifyData(phoneStr, authCodeStr);

                break;

            case R.id.get_auth_code_btn:
                if (phoneStr.trim().isEmpty()) {
                    ToastManager.showShortToast("手机号不能为空！");
                    return;
                }

                if (!StringUtils.isPhoneNumber(phoneStr)) {
                    ToastManager.showShortToast("请输入正确格式的电话号码！");
                    return;
                }
                CusCountDown cusCountDown = new CusCountDown(this, 60);
                cusCountDown.start();

                sendSmsHttpReq(phoneStr);
                break;

            default:
                break;
        }
    }

    /*验证*/
    private void getVerifyData(String phone, String code) {
        OkHttpUtils.post()
                .url(HttpConstant.NEXT_VERFIY_CODE)
                .addParams("phone", phone)
                .addParams("code", code)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, false, AccountActivity.this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                        Intent intent = new Intent(AccountActivity.this, SetAccountActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("authCode", code);
                        startActivity(intent);
                        finish();
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
