package education.juxin.com.educationpro.http;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import education.juxin.com.educationpro.bean.RespErrBean;
import education.juxin.com.educationpro.ui.activity.login.LoginActivity;
import education.juxin.com.educationpro.util.LogManager;
import education.juxin.com.educationpro.dialog.HubDialog;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求的封装类
 * The type Http call back.
 */
public abstract class HttpCallBack<T> extends Callback<T> {

    private Class clz;
    private boolean showDialog;
    private Context context;

    public HttpCallBack(Class clz, boolean showDialog, Context context) {
        this.clz = clz;
        this.showDialog = showDialog;
        this.context = context;
    }

    public HttpCallBack(Class clz) {
        this.clz = clz;
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String json = response.body().string();
        if (OkHttpUtils.getInstance().getOkHttpClient().interceptors().size() != 0) {
            LogManager.e("HttpCallBack", json);
        }
        return (T) new Gson().fromJson(json, clz);
    }

    @Override
    public void onResponse(T response, int id) {
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        e.printStackTrace();

        HubDialog.getInstance().dismiss();

        String errTipsStr = "网络请求错误！";
        try {
            for (Interceptor interceptor : OkHttpUtils.getInstance().getOkHttpClient().interceptors()) {
                if (interceptor instanceof LoggerInterceptor) {
                    RespErrBean errBean = ((LoggerInterceptor) interceptor).getCurrRespErrBean();
                    if (errBean != null && errBean.getMessage() != null) {
                        errTipsStr = errBean.getMessage();
                    }
                    if (errBean != null && errBean.getError() != null && "401".equals(errBean.getError().trim())) {
                        errTipsStr = "请重新登录！";
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                    break;
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        ToastManager.showShortToast(errTipsStr);
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        if (showDialog) {
            HubDialog.getInstance().show(context);
        }
    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
        if (showDialog) {
            HubDialog.getInstance().dismiss();
        }
    }

}
