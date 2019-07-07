package education.juxin.com.educationpro.view;

import android.widget.Toast;

import education.juxin.com.educationpro.ProApplication;

/**
 * 创建者：王兴
 * 创建时间：2016/4/11 9:53
 * 类说明：ToastManager - 简化Toast的工具类
 * 备注：用在界面中Toast使用频率较低的情况下。
 */
public class ToastManager {
    /**
     * 显示长时间的Toast
     */
    public static void showShortToast(String msgStr) {
        Toast.makeText(ProApplication.mApplicationContext, msgStr, Toast.LENGTH_SHORT).show();
    }

    public static void showShortToast(int msgResId) {
        Toast.makeText(ProApplication.mApplicationContext, msgResId, Toast.LENGTH_SHORT).show();
    }
}
