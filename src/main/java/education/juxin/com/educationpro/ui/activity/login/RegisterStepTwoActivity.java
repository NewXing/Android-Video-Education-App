package education.juxin.com.educationpro.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import cn.jpush.android.api.JPushInterface;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.bean.UserLoginBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.ui.activity.home.HomeActivity;
import education.juxin.com.educationpro.util.ActivityCollector;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.ToastManager;


/**
 * 账号注册页面
 * The type Register step two activity.
 */
public class RegisterStepTwoActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText etPassword;
    private String phone;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiter_02);
        initToolbar(true, R.string.new_user_register);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        code = intent.getStringExtra("code");

        initUI();
    }

    private void initUI() {
        findViewById(R.id.next_step_btn).setOnClickListener(this);
        etPassword = findViewById(R.id.input_password_edit);
        ((CheckBox) findViewById(R.id.show_password_checkbox)).setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step_btn:
                getDataRegister();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.show_password_checkbox:
                if (b) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;

            default:
                break;
        }
    }

    // 注册接口
    private void getDataRegister() {
        final String editTextStr = String.valueOf(etPassword.getText());

        if (StringUtils.isEmpty(editTextStr)) {
            ToastManager.showShortToast("密码不能为空！");
            return;
        }
        if (!StringUtils.checkPassword(editTextStr)) {
            ToastManager.showShortToast("请输入正确格式的密码！");
            return;
        }

        OkHttpUtils.post()
                .url(HttpConstant.USER_REGISTER)
                .addParams("phone", phone)
                .addParams("password", editTextStr)
                .addParams("authCode", code)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        ToastManager.showShortToast("注册成功");
                        reqLoginData(phone, editTextStr);
                    }
                });
    }

    //登录获取数据
    private void reqLoginData(String phoneNum, String password) {
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

                        SPHelper.setSimpleKeyValue(RegisterStepTwoActivity.this, "token", response.getData().getToken());
                        SPHelper.setSimpleKeyValue(RegisterStepTwoActivity.this, "phone", phoneNum);
                        SPHelper.setSimpleKeyValue(RegisterStepTwoActivity.this, "password", password);

                        MobclickAgent.onProfileSignIn(phoneNum); // 友盟统计：用户登录统计

                        JPushInterface.setAlias(getApplicationContext(), 0, phoneNum);

                        ActivityCollector.finishAll();
                        Intent intent = new Intent(RegisterStepTwoActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
