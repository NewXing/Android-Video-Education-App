package education.juxin.com.educationpro.http;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import education.juxin.com.educationpro.bean.RespErrBean;
import education.juxin.com.educationpro.util.FormatTimeUtil;
import education.juxin.com.educationpro.util.LogManager;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 网络请求日志
 * The type Logger interceptor.
 */
public class LoggerInterceptor implements Interceptor {

    private static final String TAG = "OkHttpUtils";

    private RespErrBean currRespErrBean;

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        printRequestLog(request);
        Response response = chain.proceed(request);
        return printResponseLog(response);
    }

    private void printRequestLog(Request request) {
        try {
            LogManager.e(TAG, "======== request start ========");

            LogManager.e(TAG, "url : " + request.url());
            LogManager.e(TAG, "method : " + request.method());
            LogManager.e(TAG, "isHttps : " + request.isHttps());

            Headers headers = request.headers();
            if (headers != null && headers.size() > 0) {
                LogManager.e(TAG, "headers : " + headers.toString());
            }

            RequestBody body = request.body();
            if (body != null) {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                LogManager.e(TAG, "requestBody's content : " + buffer.readUtf8());

                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    LogManager.e(TAG, "requestBody's contentType : " + mediaType.toString());
                } else {
                    LogManager.e(TAG, "requestBody's contentType == null");
                }
            } else {
                LogManager.e(TAG, "requestBody == null");
            }

            LogManager.e(TAG, "======== request end ========");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response printResponseLog(Response response) {
        Response resultResponse = response;
        try {
            LogManager.e(TAG, "======== response start ========");

            Response cloneResponse = response.newBuilder().build();

            LogManager.e(TAG, "url : " + cloneResponse.request().url());
            LogManager.e(TAG, "code : " + cloneResponse.code());
            LogManager.e(TAG, "protocol : " + cloneResponse.protocol());

            if (!TextUtils.isEmpty(cloneResponse.message())) {
                LogManager.e(TAG, "message : " + cloneResponse.message());
            }

            ResponseBody body = cloneResponse.body();
            if (body != null) {
                String resp = body.string();
                LogManager.e(TAG, "responseBody's content : " + resp);

                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    LogManager.e(TAG, "responseBody's contentType : " + mediaType.toString());

                    resultResponse = cloneResponse
                            .newBuilder()
                            .body(ResponseBody.create(mediaType, resp))
                            .build();
                } else {
                    LogManager.e(TAG, "responseBody's contentType == null");
                }
            } else {
                LogManager.e(TAG, "responseBody == null");
            }

            LogManager.e(TAG, "======== response end ========");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setCurrRespErrBean(resultResponse);
        return resultResponse;
    }

    private void setCurrRespErrBean(Response response) {
        try {
            if (response.code() == 401) {
                currRespErrBean = new RespErrBean();
                currRespErrBean.setTimestamp(FormatTimeUtil.formatDate2DateStr(new Date(), "yyyy/MM/dd HH:mm:ss"));
                currRespErrBean.setError("401");
                currRespErrBean.setMessage("Token无效");
            } else if (response.code() != 200) {
                try {
                    String resp = response.body().string();
                    currRespErrBean = new Gson().fromJson(resp, RespErrBean.class);
                } catch (Exception e) {
                    currRespErrBean = null;
                    e.printStackTrace();
                }
            } else {
                currRespErrBean = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RespErrBean getCurrRespErrBean() {
        return currRespErrBean;
    }
}
