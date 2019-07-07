package education.juxin.com.educationpro.base;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.dialog.SharedContentDialog;
import education.juxin.com.educationpro.http.NetworkBroadcastReceiver;
import education.juxin.com.educationpro.http.NetworkHelper;
import education.juxin.com.educationpro.ui.activity.home.ScanActivity;
import education.juxin.com.educationpro.ui.activity.home.StartActivity;
import education.juxin.com.educationpro.ui.activity.home.WelcomeActivity;
import education.juxin.com.educationpro.util.ActivityCollector;
import education.juxin.com.educationpro.util.LogManager;

/**
 * activity的基类 里面对相应的方法进行封装， 在实现activity时  继承此类 可以使用里面的方法
 * <p>
 * The type Base activity.
 * create time 2018 -3-20
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements NetworkBroadcastReceiver.NetChangeEvent {

    public static NetworkBroadcastReceiver.NetChangeEvent netChangeEvent;

    protected interface IRefreshTag {
        int IS_REFRESH_DATA = 1;
        int IS_LOAD_MORE_DATA = 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        netChangeEvent = this;

        ActivityCollector.addActivity(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        MobclickAgent.onPageStart(getClass().getSimpleName()); // 友盟统计：手动统计页面
        MobclickAgent.onResume(this); // 友盟统计：统计时长

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkBroadcastReceiver networkReceiver = new NetworkBroadcastReceiver();
        registerReceiver(networkReceiver, filter);

        if (!(this instanceof StartActivity || this instanceof WelcomeActivity || this instanceof ScanActivity)) {
            getDataFromClipboard();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ActivityCollector.removeActivity(this);
    }

    // ========================================= 设置标题栏 ========================================= //

    public interface ToolBarBtnClickListener {
        void onToolBarBtnClick();
    }

    /**
     * 通用标题栏：标题、返回键
     *
     * @param hasBackArrows true-显示返回键，false-隐藏返回键
     * @param titleString   标题文字
     */
    public Toolbar initToolbar(boolean hasBackArrows, String titleString, final ToolBarBtnClickListener listener) {
        Toolbar toolbar = findViewById(R.id.id_toolbar);
        TextView textView = findViewById(R.id.toolbar_title);

        if (toolbar == null || textView == null) {
            LogManager.e("Toolbar 布局文件加载错误！");
            return null;
        }

        textView.setText(titleString);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true); // 设置返回键可用
            if (hasBackArrows) {
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener == null) {
                            finish(); // 默认返回上一个Activity
                        } else {
                            listener.onToolBarBtnClick();
                        }
                    }
                });
            } else {
                toolbar.setNavigationIcon(null);
            }
        }
        return toolbar;
    }

    public Toolbar initToolbar(boolean hasBackArrows, int titleStrId) {
        return initToolbar(hasBackArrows, getResources().getString(titleStrId), null);
    }

    public Toolbar initToolbar(boolean hasBackArrows, int titleStrId, ToolBarBtnClickListener listener) {
        return initToolbar(hasBackArrows, getResources().getString(titleStrId), listener);
    }

    // ======================================= 网络状态监听器 ======================================= //

    /**
     * 子类中实现网络状态变化后的具体操作，此处只打印出网络状态变化的日志
     *
     * @param netMobile 网络状态变化后的类型
     */
    @Override
    public void onNetChange(int netMobile) {
        String currNetState;
        switch (netMobile) {
            case NetworkHelper.NETWORK_NONE:
                currNetState = "NETWORK_NONE";
                break;

            case NetworkHelper.NETWORK_WIFI:
                currNetState = "NETWORK_WIFI";
                break;

            case NetworkHelper.NETWORK_MOBILE:
                currNetState = "NETWORK_MOBILE";
                break;

            default:
                currNetState = "NETWORK_NONE";
                break;
        }
        LogManager.e("activity net state change, now is " + currNetState);
    }

    public void getDataFromClipboard() {
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (manager != null && manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
            try {
                String clipString = manager.getPrimaryClip().getItemAt(0).getText().toString();
                String[] decodeArr = clipString.split("<￥>");
                if (decodeArr.length >= 2 && !decodeArr[1].isEmpty()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String[] resultArr = decodeArr[1].split("&&");
                            if (resultArr.length > 0 && resultArr.length <= 1) {
                                new SharedContentDialog(BaseActivity.this, resultArr[0], "");
                            } else if (resultArr.length > 1 && resultArr.length <= 2) {
                                new SharedContentDialog(BaseActivity.this, resultArr[0], resultArr[1]);
                            }

                            manager.setPrimaryClip(ClipData.newPlainText(null, ""));
                        }
                    }, 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
