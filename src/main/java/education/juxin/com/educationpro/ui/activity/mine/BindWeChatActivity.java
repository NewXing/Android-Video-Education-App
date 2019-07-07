package education.juxin.com.educationpro.ui.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.bean.UserWxInfoBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.CircleImageView;
import education.juxin.com.educationpro.view.ToastManager;
import education.juxin.com.educationpro.wxapi.WxApiUtils;

/**
 * 管理微信页面
 * The type We chat activity.
 */
public class BindWeChatActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNameEdit;
    private EditText userPhoneEdit;
    private CircleImageView authorizeImg;
    private TextView authorizeTv;

    private String id = "";
    private String realName = "";
    private String phone = "";
    private String openId = "";
    private String nickName = "";
    private String headImgUrl = "";
    private String unionId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat);
        initToolbar(true, R.string.manage_we_chat);
        reqUserWxInfo();
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        openId = (String) SPHelper.getSimpleParam(this, "openId", "");
        nickName = (String) SPHelper.getSimpleParam(this, "nickName", "");
        headImgUrl = (String) SPHelper.getSimpleParam(this, "headImgUrl", "");
        unionId = (String) SPHelper.getSimpleParam(this, "unionId", "");

        updateWxAuthUI();
    }

    private void initUI() {
        userNameEdit = findViewById(R.id.input_user_name_edit);
        userPhoneEdit = findViewById(R.id.input_phone_num_edit);
        authorizeImg = findViewById(R.id.authorization_success_img);
        authorizeTv = findViewById(R.id.to_grant_authorization_tv);

        findViewById(R.id.authorization_layout).setOnClickListener(this);
        findViewById(R.id.sure_btn).setOnClickListener(this);
    }

    private void updateAllUI(UserWxInfoBean.UserWxInfoData data) {
        if (data != null) {
            id = data.getId();
            realName = data.getRealName();
            phone = data.getPhone();
            headImgUrl = data.getHeadimgurl();
            nickName = data.getNickname();

            userNameEdit.setText(realName);
            userPhoneEdit.setText(phone);
            authorizeImg.setVisibility(View.VISIBLE);
            ImageUtils.GlideUtil(this,headImgUrl,authorizeImg);
            authorizeTv.setText(nickName);
        } else {
            userNameEdit.setText("");
            userPhoneEdit.setText("");
            authorizeImg.setVisibility(View.GONE);
            authorizeTv.setText("去授权");
        }
    }

    private void updateWxAuthUI() {
        if (!"".equals(headImgUrl.trim())) {
            authorizeImg.setVisibility(View.VISIBLE);
            ImageUtils.GlideUtil(this,headImgUrl,authorizeImg);
        }
        if (!"".equals(nickName.trim())) {
            authorizeTv.setText(nickName);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.authorization_layout:
                WxApiUtils.reqWxLogin();
                break;

            case R.id.sure_btn:
                reqSaveWxInfo();
                break;

            default:
                break;
        }
    }

    private void reqUserWxInfo() {
        OkHttpUtils.get()
                .url(HttpConstant.USER_WX_INFO_GET)
                .build()
                .execute(new HttpCallBack<UserWxInfoBean>(UserWxInfoBean.class, true, this) {
                    @Override
                    public void onResponse(UserWxInfoBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        updateAllUI(response.getData());
                    }
                });
    }

    private void reqSaveWxInfo() {
        realName = userNameEdit.getText().toString();
        phone = userPhoneEdit.getText().toString();

        if (realName == null || realName.trim().isEmpty()) {
            ToastManager.showShortToast("请输入您的真实姓名！");
            return;
        }
        if (phone == null || phone.trim().isEmpty()) {
            ToastManager.showShortToast("请输入您的手机号！");
            return;
        }
        if (!StringUtils.isPhoneNumber(phone)) {
            ToastManager.showShortToast("手机号格式不正确！");
            return;
        }
        if ("".equals(openId.trim())) {
            ToastManager.showShortToast("请您完成微信授权！");
            return;
        }

        OkHttpUtils.post()
                .url(HttpConstant.USER_WX_INFO_SAVE)
                .addParams("id", id)
                .addParams("realName", realName)
                .addParams("phone", phone)
                .addParams("openid", openId)
                .addParams("nickname", nickName)
                .addParams("headimgurl", headImgUrl)
                .addParams("unionid", unionId)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        ToastManager.showShortToast("保存成功");
                        finish();
                    }
                });
    }

}
