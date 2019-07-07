package education.juxin.com.educationpro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import cn.jpush.android.api.JPushInterface;

import com.qiniu.android.storage.UploadManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.util.HashMap;
import java.util.Map;

import education.juxin.com.educationpro.http.NetworkHelper;
import education.juxin.com.educationpro.util.LogManager;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.wxapi.WxApiUtils;
import education.juxin.com.educationpro.wxapi.bean.WxBeanAccessToken;

public class ProApplication extends MultiDexApplication {

    public static Context mApplicationContext;
    private static UploadManager uploadManager;
    private static WxBeanAccessToken wxBeanAccessToken;
    private static Map<String, String> jPushParams;

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.green_bg, R.color.bg_white);
                ClassicsHeader classicsHeader = new ClassicsHeader(context);
                classicsHeader.setEnableLastTime(false);
                return classicsHeader;
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                ClassicsFooter classicsFooter = new ClassicsFooter(context);
                classicsFooter.setDrawableSize(20).setBackgroundColor(mApplicationContext.getResources().getColor(R.color.bg_white));
                return classicsFooter;
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();

        uploadManager = new UploadManager();
        mApplicationContext = getApplicationContext();
        LogManager.isDebug = true;

        NetworkHelper.initOkHttp();
        initParty3();
    }

    /**
     * 初始化第三方相关
     */
    private void initParty3() {
        // 初始化Bugly
        CrashReport.initCrashReport(mApplicationContext, ProConstant.BUGLY_APP_ID, LogManager.isDebug);

        // 初始化极光
        jPushParams = new HashMap<>();
        JPushInterface.setDebugMode(LogManager.isDebug);
        JPushInterface.init(mApplicationContext);
        if ("1".equals(SPHelper.getSimpleParam(this, "sendMsg", "0"))) {
            JPushInterface.resumePush(mApplicationContext);
        } else {
            JPushInterface.stopPush(mApplicationContext);
        }

        // 初始化微信
        WxApiUtils.initWxApi(mApplicationContext);

        // 初始化友盟：统计+分享SDK
        UMConfigure.init(mApplicationContext, ProConstant.UMENG_APP_KEY, "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(LogManager.isDebug);

        MobclickAgent.setScenarioType(mApplicationContext, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSecret(mApplicationContext, ProConstant.UMENG_APP_SECRET);
        MobclickAgent.openActivityDurationTrack(false); // 禁止默认的页面统计功能，改为手动

        PlatformConfig.setWeixin(ProConstant.WX_APP_ID, ProConstant.WX_SECRET);
        PlatformConfig.setQQZone(ProConstant.QQ_APP_ID, ProConstant.QQ_SECRET);
        PlatformConfig.setSinaWeibo(ProConstant.SINA_APP_ID, ProConstant.SINA_SECRET, "http://sns.whalecloud.com");
    }

    public static UploadManager getUploadManager() {
        return uploadManager;
    }

    public static WxBeanAccessToken getWxBeanAccessToken() {
        return wxBeanAccessToken;
    }

    public static void setWxBeanAccessToken(WxBeanAccessToken wxBeanAccessToken) {
        ProApplication.wxBeanAccessToken = wxBeanAccessToken;
    }

    public static Map<String, String> getJPushParams() {
        return jPushParams;
    }

    public static void setJPushParams(Map<String, String> jPushParams) {
        ProApplication.jPushParams = jPushParams;
    }

}
