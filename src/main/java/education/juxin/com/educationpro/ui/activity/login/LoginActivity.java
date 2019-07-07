package education.juxin.com.educationpro.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import cn.jpush.android.api.JPushInterface;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.bean.UserLoginBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.ui.activity.home.HomeActivity;
import education.juxin.com.educationpro.util.ActivityCollector;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 登录页面
 * <p>
 * The type Login activity.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText etPhone;
    private EditText etPsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolbar(true, R.string.login, new ToolBarBtnClickListener() {
            @Override
            public void onToolBarBtnClick() {
                finishThisActivity();
            }
        });
        initUI();
    }

    private void initUI() {
        TextView titleRight = findViewById(R.id.head_right_tv);
        titleRight.setText(R.string.forget_password);
        titleRight.setOnClickListener(this);

        findViewById(R.id.login_btn).setOnClickListener(this);
        findViewById(R.id.register_tv).setOnClickListener(this);
        etPhone = findViewById(R.id.et_phone);
        etPsd = findViewById(R.id.et_psd);
        String phone = String.valueOf(SPHelper.getSimpleParam(this, "phone", ""));
        String password = String.valueOf(SPHelper.getSimpleParam(this, "password", ""));
        etPhone.setText(phone);
        etPsd.setText(password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                String etPhoneStr = String.valueOf(etPhone.getText());
                String etPsdStr = String.valueOf(etPsd.getText());
                if (StringUtils.isEmpty(etPhoneStr)) {
                    ToastManager.showShortToast("电话号码不能为空");
                    return;
                }
                if (!StringUtils.isPhoneNumber(etPhoneStr)) {
                    ToastManager.showShortToast("请输入正确格式的电话号码！");
                    return;
                }

                if (StringUtils.isEmpty(etPsdStr)) {
                    ToastManager.showShortToast("密码不能为空！");
                    return;
                }
                if (!StringUtils.checkPassword(etPsdStr)) {
                    ToastManager.showShortToast("请输入正确格式的密码！");
                    return;
                }
                reqData();
                break;

            case R.id.register_tv:
                startActivity(new Intent(this, RegisterStepOneActivity.class));
                break;

            case R.id.head_right_tv:
                startActivity(new Intent(this, ForgetPsdStepOneActivity.class));
                break;

            default:
                break;
        }
    }

    private void finishThisActivity() {
        ActivityCollector.finishAll();
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishThisActivity();
    }

    //登录获取数据
    private void reqData() {
        final String phoneNum = etPhone.getText().toString();
        final String password = etPsd.getText().toString();

        OkHttpUtils.get()
                .url(HttpConstant.USER_LOGIN)
                .addParams("phone", phoneNum)
                .addParams("password", password)
                .build()
                .execute(new HttpCallBack<UserLoginBean>(UserLoginBean.class, true, this) {
                    @Override
                    public void onResponse(UserLoginBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        SPHelper.setSimpleKeyValue(LoginActivity.this, "token", response.getData().getToken());
                        SPHelper.setSimpleKeyValue(LoginActivity.this, "phone", phoneNum);
                        SPHelper.setSimpleKeyValue(LoginActivity.this, "password", password);

                        MobclickAgent.onProfileSignIn(phoneNum); // 友盟统计：用户登录统计

                        JPushInterface.setAlias(getApplicationContext(), 0, phoneNum);

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

}
