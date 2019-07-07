package education.juxin.com.educationpro.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import education.juxin.com.educationpro.ProApplication;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.bean.UserLoginBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.ui.activity.login.LoginActivity;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.OkHttpClient;

/**
 * 启动页
 */
public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startApp();
    }

    private void startApp() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Object result = SPHelper.getSimpleParam(StartActivity.this, "not_first_entry_app", false);

                if ((boolean) result) {
                    String token = String.valueOf(SPHelper.getSimpleParam(StartActivity.this, "token", ""));
                    startActivity(new Intent(StartActivity.this, HomeActivity.class));
                    // 有token的时候则更新token
                    if (token != null && !token.trim().isEmpty()) {
                        reqData();
                    }
                } else {
                    startActivity(new Intent(StartActivity.this, WelcomeActivity.class));
                    SPHelper.setSimpleKeyValue(StartActivity.this, "not_first_entry_app", true);
                }
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 2000);
    }

    private void reqData() {
        String phoneNum = (String) SPHelper.getSimpleParam(StartActivity.this, "phone", "");
        String password = (String) SPHelper.getSimpleParam(StartActivity.this, "password", "");

        if (phoneNum == null || phoneNum.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return;
        }

        try {
            OkHttpUtils.get()
                    .url(HttpConstant.USER_LOGIN)
                    .addParams("phone", phoneNum)
                    .addParams("password", password)
                    .build()
                    .execute(new HttpCallBack<UserLoginBean>(UserLoginBean.class, true, ProApplication.mApplicationContext) {
                        @Override
                        public void onResponse(UserLoginBean response, int id) {
                            if (response.getCode() != 0) {
                                ToastManager.showShortToast(response.getMsg());
                                return;
                            }

                            SPHelper.setSimpleKeyValue(StartActivity.this, "token", response.getData().getToken());

                            MobclickAgent.onProfileSignIn(phoneNum);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
