package education.juxin.com.educationpro.ui.activity.mine;

import android.os.Bundle;


import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.bean.AboutUsBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.view.CusWebView;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 关于我们的页面
 * The type About me activity.
 */
public class AboutUsActivity extends BaseActivity {

    private CusWebView cusWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        initToolbar(true, R.string.about_me_title);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateData();
    }

    private void initUI() {
        cusWebView = findViewById(R.id.content_web_view);
    }

    private void updateData() {
        OkHttpUtils.get()
                .url(HttpConstant.ABOUT_US)
                .build()
                .execute(new HttpCallBack<AboutUsBean>(AboutUsBean.class, true, this) {
                    @Override
                    public void onResponse(AboutUsBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        String title = response.getData().getTitle();
                        String content = response.getData().getContent();

                        cusWebView.loadHtmlString("<h3 style='color:red'>" + title + "</h3><h5>" + content + "</h5>");
                    }
                });
    }
}
