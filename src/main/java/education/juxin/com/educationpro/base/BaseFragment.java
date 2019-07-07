package education.juxin.com.educationpro.base;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.http.NetworkBroadcastReceiver;
import education.juxin.com.educationpro.http.NetworkHelper;
import education.juxin.com.educationpro.util.LogManager;

/**
 * fragment 的基类 里面有公共方法
 * 实现方式：创建一个fragment 继承baseFragment 使用里面的公共方法
 * Created on 2018/3/23.
 */

public class BaseFragment extends Fragment implements NetworkBroadcastReceiver.NetChangeEvent {

    private OnFragmentTitleBack onFragmentTitleBack;
    public static NetworkBroadcastReceiver.NetChangeEvent netChangeEvent;

    protected interface IRefreshTag {
        int IS_REFRESH_DATA = 1;
        int IS_LOAD_MORE_DATA = 2;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        netChangeEvent = this;
    }

    @Override
    public void onResume() {
        super.onResume();

        MobclickAgent.onPageStart(getClass().getSimpleName());

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkBroadcastReceiver networkReceiver = new NetworkBroadcastReceiver();
        getActivity().registerReceiver(networkReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    // ========================================= 设置标题栏 ========================================= //

    /**
     * 通用标题栏：标题、返回键
     *
     * @param hasBackArrows true-显示返回键，false-隐藏返回键
     * @param titleString   标题文字
     */
    public Toolbar initToolbar(View rootView, boolean hasBackArrows, String titleString) {
        Toolbar toolbar = rootView.findViewById(R.id.id_toolbar);
        TextView textView = rootView.findViewById(R.id.toolbar_title);

        if (toolbar == null || textView == null) {
            LogManager.e("Toolbar 布局文件加载错误！");
            return null;
        }
        textView.setText(titleString);
        if (hasBackArrows) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFragmentTitleBack.onFragmentTitleButtonClick();
                }
            });
        } else {
            toolbar.setNavigationIcon(null);
        }
        return toolbar;
    }

    public Toolbar initToolbar(View rootView, boolean hasBackArrows, int titleStrId) {
        return initToolbar(rootView, hasBackArrows, getResources().getString(titleStrId));
    }

    public interface OnFragmentTitleBack {
        void onFragmentTitleButtonClick();
    }

    public void setOnFragmentTitleBack(OnFragmentTitleBack onFragmentTitleBack) {
        this.onFragmentTitleBack = onFragmentTitleBack;
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
        LogManager.e("fragment net state change, now is " + currNetState);
    }
}
