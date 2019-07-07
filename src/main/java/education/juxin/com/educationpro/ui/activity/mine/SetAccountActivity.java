package education.juxin.com.educationpro.ui.activity.mine;

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
 * 设置账号安全 页面
 * The type Set Account activity.
 */
public class SetAccountActivity extends BaseActivity {

    private String phoneStr = "";
    private String authCodeStr = "";

    private EditText passwordEt;
    private EditText passwordSureEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_zhanghao);
        initToolbar(true, R.string.account_safe);

        phoneStr = getIntent().getStringExtra("phone");
        authCodeStr = getIntent().getStringExtra("authCode");

        initUI();
    }

    private void initUI() {
        passwordEt = findViewById(R.id.input_password_et);
        passwordSureEt = findViewById(R.id.input_password_sure_et);
        findViewById(R.id.sure_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqResetPwd();
            }
        });
    }

    private void reqResetPwd() {
        String passwordStr = passwordEt.getText().toString();
        String passwordSureStr = passwordSureEt.getText().toString();

        if (StringUtils.isEmpty(passwordStr)) {
            ToastManager.showShortToast("密码不能为空！");
            return;
        }

        if (StringUtils.isEmpty(passwordSureStr)) {
            ToastManager.showShortToast("密码二次确认为空！");
            return;
        }

        if (!passwordSureStr.equals(passwordStr)) {
            ToastManager.showShortToast("两次输入的密码不一致！");
            return;
        }

        OkHttpUtils.post()
                .url(HttpConstant.USER_FORGET_PSW_AUTH)
                .addParams("phone", phoneStr)
                .addParams("password", passwordStr)
                .addParams("authCode", authCodeStr)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                        ToastManager.showShortToast("修改成功");
                        finish();
                    }
                });
    }

}
