package education.juxin.com.educationpro.ui.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.bean.AlipayBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 管理支付宝页面
 * The type Ai pay activity.
 */
public class BindAliPayActivity extends BaseActivity implements View.OnClickListener {

    private EditText etAccount;
    private EditText etName;
    private Button btnConfig;

    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_pay);
        initToolbar(true, R.string.manage_alipay);
        initUI();
    }

    private void initUI() {
        etAccount = findViewById(R.id.et_account);
        etName = findViewById(R.id.et_user_name);
        btnConfig = findViewById(R.id.btn_config);
        btnConfig.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        getAlipayData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_config:
                updateAlipayData();
                break;
        }
    }

    private void updateAllUI(AlipayBean bean) {
        if (bean != null && bean.getData() != null) {
            id = bean.getData().getId();

            etAccount.setText(bean.getData().getAccount());
            etName.setText(bean.getData().getName());
            btnConfig.setText("修改");
        } else {
            id = "";

            btnConfig.setText("确定");
        }
    }

    // 获取支付宝账号信息
    private void getAlipayData() {
        OkHttpUtils.post()
                .url(HttpConstant.GET_AIPAY_ACCOUNT)
                .build()
                .execute(new HttpCallBack<AlipayBean>(AlipayBean.class, false, this) {
                    @Override
                    public void onResponse(AlipayBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                        updateAllUI(response);
                    }
                });
    }

    // 进行更改支付宝账号数据
    private void updateAlipayData() {
        String etAccountStr = etAccount.getText().toString().trim();
        String etNameStr = etName.getText().toString().trim();

        if (StringUtils.isEmpty(etAccountStr)) {
            ToastManager.showShortToast("支付宝账号不能为空！");
            return;
        }
        if (StringUtils.isEmpty(etNameStr)) {
            ToastManager.showShortToast("支付宝姓名不能为空！");
            return;
        }

        if (!StringUtils.isPhoneNumber(etAccountStr) && !StringUtils.checkoutEmailAddress(etAccountStr)) {
            ToastManager.showShortToast("支付宝账号格式不正确！");
            return;
        }

        OkHttpUtils.post()
                .url(HttpConstant.UPDATE_AIPAY)
                .addParams("id", id)
                .addParams("account", etAccountStr)
                .addParams("name", etNameStr)
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
