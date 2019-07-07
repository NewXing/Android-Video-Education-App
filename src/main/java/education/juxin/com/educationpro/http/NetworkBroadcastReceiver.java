package education.juxin.com.educationpro.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseFragment;

/**
 * 网络状态监听
 * Created on 2018/4/16.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    public NetChangeEvent netChangeEventActivity = BaseActivity.netChangeEvent;
    public NetChangeEvent netChangeEventFragment = BaseFragment.netChangeEvent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            int netWorkState = NetworkHelper.getNetWorkState(context);
            if (netChangeEventActivity != null) {
                netChangeEventActivity.onNetChange(netWorkState);
            }
            if (netChangeEventFragment != null) {
                netChangeEventFragment.onNetChange(netWorkState);
            }
        }
    }

    public interface NetChangeEvent {
        void onNetChange(int netMobile);
    }
}
