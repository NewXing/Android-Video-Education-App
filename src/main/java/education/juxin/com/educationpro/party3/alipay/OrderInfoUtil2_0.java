package education.juxin.com.educationpro.party3.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import education.juxin.com.educationpro.ui.activity.home.OrderActivity;
import education.juxin.com.educationpro.ui.activity.home.PayModeActivity;
import education.juxin.com.educationpro.util.ActivityCollector;
import education.juxin.com.educationpro.util.ApkInfoUtil;
import education.juxin.com.educationpro.util.LogManager;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.view.ToastManager;

public class OrderInfoUtil2_0 {

    private static final String ALGORITHM = "RSA";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

    private static final int SDK_PAY_FLAG = 0x111;
    private static AliPayHandler mAliPayHandler;

    private static class AliPayHandler extends Handler {

        private Activity activity;

        AliPayHandler(Activity activity) {
            this.activity = activity;
        }

        public void handleMessage(android.os.Message msg) {
            if (msg.what == SDK_PAY_FLAG) {
                PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                ActivityCollector.finishActivity(PayModeActivity.class);
                ActivityCollector.finishActivity(OrderActivity.class);

                if (TextUtils.equals("9000", payResult.getResultStatus())) {
                    ToastManager.showShortToast("支付成功");
                    SPHelper.setSimpleKeyValue(activity, "pay_success_flag", "1");
                } else {
                    ToastManager.showShortToast("支付失败");
                    SPHelper.setSimpleKeyValue(activity, "pay_success_flag", "0");
                }
            }
        }
    }

    public static void aliPayV2(Activity activity, Map<String, String> params) {
        mAliPayHandler = new AliPayHandler(activity);

        List<String> keys = new ArrayList<>(params.keySet());

        StringBuilder orderInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = params.get(key);
            orderInfo.append(buildKeyValue(key, value, true));
            orderInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = params.get(tailKey);
        orderInfo.append(buildKeyValue(tailKey, tailValue, true));

        LogManager.e("alipay params orderInfo=" + orderInfo);

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                if (ApkInfoUtil.isApkDebug(activity)) {
                    EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
                    LogManager.e("Alipay Mode: Debug");
                } else {
                    LogManager.e("Alipay Mode: Release");
                }

                PayTask payTask = new PayTask(activity);
                Map<String, String> result = payTask.payV2(orderInfo.toString(), true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mAliPayHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 构造支付订单参数列表
     */
    private static Map<String, String> buildOrderParamMap(String app_id, String biz_content, String timestamp) {
        Map<String, String> keyValues = new HashMap<>();

        keyValues.put("app_id", app_id);
        keyValues.put("biz_content", biz_content);
        keyValues.put("charset", "utf-8");
        keyValues.put("format", "json");
        keyValues.put("method", "alipay.trade.app.pay");
        keyValues.put("sign_type", "RSA2");
        keyValues.put("timestamp", timestamp);
        keyValues.put("version", "1.0");

        return keyValues;
    }

    /**
     * 构造支付订单参数信息
     *
     * @param map 支付订单参数
     */
    private static String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }

    /**
     * 对支付参数信息进行签名
     *
     * @param map 待签名授权信息
     */
    private static String getSign(Map<String, String> map, String rsaKey) {
        List<String> keys = new ArrayList<>(map.keySet());
        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            authInfo.append(buildKeyValue(key, value, false));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, false));

        String oriSign = sign(authInfo.toString(), rsaKey);
        String encodedSign = "";

        try {
            encodedSign = URLEncoder.encode(oriSign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "sign=" + encodedSign;
    }

    /**
     * 获取支付宝SDK版本号
     */
    private static String getSDKVersion(Activity activity) {
        PayTask payTask = new PayTask(activity);
        return payTask.getVersion();
    }

    /**
     * 拼接键值对
     */
    private static String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * 支付参数签名
     */
    private static String sign(String content, String privateKey) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, "BC");
            PrivateKey priKey = keyFactory.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));
            byte[] signed = signature.sign();
            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
