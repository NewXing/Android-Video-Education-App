package education.juxin.com.educationpro.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import education.juxin.com.educationpro.ProApplication;
import education.juxin.com.educationpro.util.SPHelper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 判断网络状态
 * Created on 2018/4/16.
 */
public class NetworkHelper {

    public static final int NETWORK_NONE = -1;     // 没有连接到网络
    public static final int NETWORK_MOBILE = 0;    // 移动网络
    public static final int NETWORK_WIFI = 1;      // Wifi网络

    public static int getNetWorkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager == null) {
            return NETWORK_NONE;
        }

        NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    public static void initOkHttp() {
        OkHttpUtils.initClient(new OkHttpClient.Builder()
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(NetworkHelper.createSSLSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .addInterceptor(new LoggerInterceptor()) // 自定义拦截器：打印日志
                .addInterceptor(new Interceptor() { // 自定义拦截器：添加Token
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        String token = (String) SPHelper.getSimpleParam(ProApplication.mApplicationContext, "token", "");

                        return chain.proceed(chain.request()
                                .newBuilder()
                                .addHeader("authorization", token != null && !token.isEmpty() ? token : "")
                                .build());
                    }
                })
                .build());
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }
}
