package education.juxin.com.educationpro.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.umeng.socialize.weixin.view.WXCallbackActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.ProConstant;
import education.juxin.com.educationpro.bean.WxParamsBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.LogManager;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.view.ToastManager;
import education.juxin.com.educationpro.wxapi.bean.WxBeanUserInfo;

public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WxApiUtils.getApi();
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (baseResp instanceof SendMessageToWX.Resp) {
                    ToastManager.showShortToast("分享成功！");
                    finish();
                } else if (baseResp instanceof SendAuth.Resp) {
                    // 用户同意，授权成功
                    String code = ((SendAuth.Resp) baseResp).code;
                    OkHttpUtils.post()
                            .url(HttpConstant.GET_WX_PARAMS)
                            .build()
                            .execute(new HttpCallBack<WxParamsBean>(WxParamsBean.class, true, this) {
                                @Override
                                public void onResponse(WxParamsBean response, int id) {
                                    if (response.getCode() != 0) {
                                        ToastManager.showShortToast(response.getMsg());
                                        return;
                                    }

                                    WxApiUtils.reqWxAccessToken(WXEntryActivity.this, response.getData().getSecret(), code, new IWxApiCallback() {
                                        @Override
                                        public void onWxApiUtilsCallback(Object response) {
                                            LogManager.e(ProConstant.WX_TAG, "reqWxAccessToken onWxApiUtilsCallback=" + response);

                                            WxApiUtils.reqWxUserInfo(WXEntryActivity.this, new IWxApiCallback() {
                                                @Override
                                                public void onWxApiUtilsCallback(Object response) {
                                                    LogManager.e(ProConstant.WX_TAG, "reqWxUserInfo onWxApiUtilsCallback=" + response);

                                                    SPHelper.setSimpleKeyValue(WXEntryActivity.this, "openId", ((WxBeanUserInfo) response).getOpenid());
                                                    SPHelper.setSimpleKeyValue(WXEntryActivity.this, "nickName", ((WxBeanUserInfo) response).getNickname());
                                                    SPHelper.setSimpleKeyValue(WXEntryActivity.this, "headImgUrl", ((WxBeanUserInfo) response).getHeadimgurl());
                                                    SPHelper.setSimpleKeyValue(WXEntryActivity.this, "unionId", ((WxBeanUserInfo) response).getUnionid());
                                                }
                                            });
                                        }
                                    });

                                    finish();
                                }
                            });
                }
                break;

            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ToastManager.showShortToast("授权取消");
                finish();
                // 用户取消
                break;

            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                // 用户拒绝
                ToastManager.showShortToast("授权被拒绝");
                finish();
                break;

            default:
                LogManager.e(ProConstant.WX_TAG, "errCode=" + baseResp.errCode + ", errStr=" + baseResp.errStr);
                finish();
                break;
        }
    }

}
