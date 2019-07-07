package education.juxin.com.educationpro.ui.activity.login;

import android.os.Bundle;

import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.bean.UserAgreementBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.view.CusWebView;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 注册协议界面
 */
public class RegisterProtocolActivity extends BaseActivity {

    private CusWebView cusWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_protocol);
        initToolbar(true, R.string.register_protocol);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        reqData();
    }

    //初始化数据
    private void initUI() {
        cusWebView = findViewById(R.id.content_web_view);
    }

    //获取接口解析数据
    private void reqData() {
        OkHttpUtils.get()
                .url(HttpConstant.USER_AGREEMENT)
                .build()
                .execute(new HttpCallBack<UserAgreementBean>(UserAgreementBean.class, true, this) {
                    @Override
                    public void onResponse(UserAgreementBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        String name = response.getData().getName();
                        String content = response.getData().getContent();
                        cusWebView.loadHtmlString("<h3 style='color:red'>" + name + "</h3><h5>" + content + "</h5>");
                    }
                });
    }
}
