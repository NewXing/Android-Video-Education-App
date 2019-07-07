package education.juxin.com.educationpro.wxapi;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;

import education.juxin.com.educationpro.ProApplication;
import education.juxin.com.educationpro.ProConstant;
import education.juxin.com.educationpro.bean.AppPayParamBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.util.LogManager;
import education.juxin.com.educationpro.wxapi.bean.WxBaseBean;
import education.juxin.com.educationpro.wxapi.bean.WxBeanAccessToken;
import education.juxin.com.educationpro.wxapi.bean.WxBeanRefreshToken;
import education.juxin.com.educationpro.wxapi.bean.WxBeanUserInfo;

public class WxApiUtils {

    private static IWXAPI api;

    public static void initWxApi(Context context) {
        api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(ProConstant.WX_APP_ID);
    }

    public static IWXAPI getApi() {
        return api;
    }

    // 应用授权登录
    public static void reqWxLogin() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }

    // 请求授权凭证（access_token）
    public static void reqWxAccessToken(Context context, String appSecret, String code, final IWxApiCallback utils) {
        OkHttpUtils.get()
                .url(ProConstant.WX_ACCESS_TOKEN_URL)
                .addParams("appid", ProConstant.WX_APP_ID)
                .addParams("secret", appSecret)
                .addParams("code", code)
                .addParams("grant_type", "authorization_code")
                .build()
                .execute(new HttpCallBack<WxBeanAccessToken>(WxBeanAccessToken.class, true, context) {
                    @Override
                    public void onResponse(WxBeanAccessToken response, int id) {
                        LogManager.e(ProConstant.WX_TAG, "reqWxAccessToken response=" + response.toString());
                        if (response.getErrcode() != null) {
                            return;
                        }

                        ProApplication.setWxBeanAccessToken(response);

                        if (utils != null) {
                            utils.onWxApiUtilsCallback(response);
                        }
                    }
                });
    }

    // 刷新或续期授权凭证（access_token）使用
    public static void reqWxRefreshToken(Context context, final IWxApiCallback utils) {
        if (ProApplication.getWxBeanAccessToken() == null) {
            return;
        }

        OkHttpUtils.get()
                .url(ProConstant.WX_REFRESH_TOKEN_URL)
                .addParams("appid", ProConstant.WX_APP_ID)
                .addParams("grant_type", "refresh_token")
                .addParams("refresh_token", ProApplication.getWxBeanAccessToken().getRefresh_token())
                .build()
                .execute(new HttpCallBack<WxBeanRefreshToken>(WxBeanRefreshToken.class, true, context) {
                    @Override
                    public void onResponse(WxBeanRefreshToken response, int id) {
                        LogManager.e(ProConstant.WX_TAG, "reqWxRefreshToken response=" + response.toString());
                        if (response.getErrcode() != null) {
                            return;
                        }

                        if (utils != null) {
                            utils.onWxApiUtilsCallback(response);
                        }
                    }
                });
    }

    // 检验授权凭证（access_token）是否有效
    public static void reqWxAuth(Context context, final IWxApiCallback utils) {
        if (ProApplication.getWxBeanAccessToken() == null) {
            return;
        }

        OkHttpUtils.get()
                .url(ProConstant.WX_AUTH_URL)
                .addParams("access_token", ProApplication.getWxBeanAccessToken().getAccess_token())
                .addParams("openid", ProApplication.getWxBeanAccessToken().getOpenid())
                .build()
                .execute(new HttpCallBack<WxBaseBean>(WxBaseBean.class, true, context) {
                    @Override
                    public void onResponse(WxBaseBean response, int id) {
                        LogManager.e(ProConstant.WX_TAG, "reqWxAuth response=" + response.toString());

                        if (response.getErrcode() != null) {
                            return;
                        }

                        if (utils != null) {
                            utils.onWxApiUtilsCallback(response);
                        }
                    }
                });
    }

    // 获取用户个人信息（UnionID机制）
    public static void reqWxUserInfo(Context context, final IWxApiCallback utils) {
        if (ProApplication.getWxBeanAccessToken() == null) {
            return;
        }

        OkHttpUtils.get()
                .url(ProConstant.WX_USER_INFO_URL)
                .addParams("access_token", ProApplication.getWxBeanAccessToken().getAccess_token())
                .addParams("openid", ProApplication.getWxBeanAccessToken().getOpenid())
                .addParams("lang", "zh_CN")
                .build()
                .execute(new HttpCallBack<WxBeanUserInfo>(WxBeanUserInfo.class, true, context) {
                    @Override
                    public void onResponse(WxBeanUserInfo response, int id) {
                        LogManager.e(ProConstant.WX_TAG, "reqWxUserInfo response=" + response.toString());
                        if (response.getErrcode() != null) {
                            return;
                        }

                        if (utils != null) {
                            utils.onWxApiUtilsCallback(response);
                        }
                    }
                });
    }

    // 调起微信支付，商户服务器生成支付订单，先调用统一下单API生成预付单，获取到prepay_id后将参数再次签名传输给APP发起支付
    public static void reqWxPayApi(AppPayParamBean.AppPayParamData.WechatPayResult data) {
        PayReq req = new PayReq();

        req.appId = data.getAppId();
        req.partnerId = data.getPartnerId();
        req.prepayId = data.getPrepayId();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = data.getNonceStr();
        req.timeStamp = data.getTimeStamp();
        req.sign = data.getSign();

        LogManager.e("Pay_req={"
                + "\n\treq.appId=" + req.appId
                + "\n\treq.partnerId=" + req.partnerId
                + "\n\treq.prepayId=" + req.prepayId
                + "\n\treq.packageValue=" + req.packageValue
                + "\n\treq.nonceStr=" + req.nonceStr
                + "\n\treq.timeStamp=" + req.timeStamp
                + "\n\treq.sign=" + req.sign + "\n}");

        api.sendReq(req);
    }
}
