package education.juxin.com.educationpro.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 忘记密码 从新设定密码
 * The type Forget psd step two activity.
 */
public class ForgetPsdStepTwoActivity extends BaseActivity implements View.OnClickListener {

    private EditText etForgetPsd;
    private EditText etConfigForgetPsd;

    private String phoneNumStr = "";
    private String authCodeStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_02);

        phoneNumStr = getIntent().getStringExtra("phone");
        authCodeStr = getIntent().getStringExtra("authCode");

        initToolbar(true, R.string.forget_password);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.next_step_btn).setOnClickListener(this);
        etForgetPsd = findViewById(R.id.et_forget_psd);
        etConfigForgetPsd = findViewById(R.id.et_config_forget_psd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step_btn:
                String etForgetPsdStr = String.valueOf(etForgetPsd.getText());
                String etConfigForgetPsdStr = String.valueOf(etConfigForgetPsd.getText());

                if (StringUtils.isEmpty(etForgetPsdStr)) {
                    ToastManager.showShortToast("密码不能为空！");
                    return;
                }
                if (!StringUtils.checkPassword(etForgetPsdStr)) {
                    ToastManager.showShortToast("请输入正确格式的密码！");
                    return;
                }

                if (StringUtils.isEmpty(etConfigForgetPsdStr)) {
                    ToastManager.showShortToast("确认密码不能为空！");
                    return;
                }
                if (!StringUtils.checkPassword(etConfigForgetPsdStr)) {
                    ToastManager.showShortToast("请输入正确格式的密码！");
                    return;
                }

                if (!etConfigForgetPsdStr.equals(etForgetPsdStr)) {
                    ToastManager.showShortToast("密码不一致！");
                    return;
                }

                reqForgetPsw(phoneNumStr, etForgetPsdStr, authCodeStr);
                break;

            default:
                break;
        }

    }

    private void reqForgetPsw(String phoneNum, String password, String authCode) {
        OkHttpUtils.post()
                .url(HttpConstant.USER_FORGET_PSW)
                .addParams("phone", phoneNum)
                .addParams("password", password)
                .addParams("authCode", authCode)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        ToastManager.showShortToast("密码修改成功 请重新登录");

                        startActivity(new Intent(ForgetPsdStepTwoActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }
}
