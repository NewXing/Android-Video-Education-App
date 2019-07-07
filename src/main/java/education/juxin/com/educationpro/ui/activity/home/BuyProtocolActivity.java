package education.juxin.com.educationpro.ui.activity.home;

import android.os.Bundle;

import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.bean.BuyProtocolBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.view.CusWebView;
import education.juxin.com.educationpro.view.ToastManager;

public class BuyProtocolActivity extends BaseActivity {

    private CusWebView cusWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_protocol);
        initToolbar(true, "用户购买协议", null);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBuyProtocolData();
    }

    private void initUI() {
        cusWebView = findViewById(R.id.content_web_view);
    }

    private void getBuyProtocolData() {
        OkHttpUtils.get()
                .url(HttpConstant.GET_BUY_PROTOCOL)
                .build()
                .execute(new HttpCallBack<BuyProtocolBean>(BuyProtocolBean.class, true, BuyProtocolActivity.this) {

                    @Override
                    public void onResponse(BuyProtocolBean response, int id) {
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
