package education.juxin.com.educationpro.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 创建者：王兴
 * 创建时间：2016/4/11 9:52
 * 类说明：ScreenUtils - 处理和屏幕尺寸相关的工具类
 */
public class ScreenUtils {

// ======================================== 获取屏幕相关尺寸 ========================================= //

    /**
     * 获得屏幕像素宽度
     */
    public static int getScreenWidth(Context context) {
        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm == null) {
                return 0;
            }
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.widthPixels;
        } else {
            return 0;
        }
    }

    /**
     * 获得屏幕像素高度
     */
    public static int getScreenHeight(Context context) {
        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            if (wm != null) {
                wm.getDefaultDisplay().getMetrics(outMetrics);
            }
            return outMetrics.heightPixels;
        } else {
            return 0;
        }
    }

    /**
     * 获取屏幕dip
     */

    public static float getDensity(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获得状态栏高度，通过Resource类方法获取status_bar_height的具体数值
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

//    /**
//     * 获得状态栏高度，通过反射获取R类中status_bar_height的Id再进行计算
//     */
//    public static int getStatusBarHeight(Context context) {
//        int statusHeight = -1;
//        try {
//            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
//            Object object = clazz.newInstance();
//            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
//            statusHeight = context.getResources().getDimensionPixelSize(height);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return statusHeight;
//    }

// ============================================ 单位转换 ============================================ //

    /**
     * 根据手机的分辨率从 dp 转 px，保证尺寸大小不变
     */
    public static int dp2px(Context context, float dpValue) {
        if (context != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } else {
            return 0;
        }

        // 系统提供的直接转换方法
//        return (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                dpValue,
//                context.getResources().getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 sp 转 px，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);

        // 系统提供的直接转换方法
//        return (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_SP,
//                spValue,
//                context.getResources().getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 px 转 dp，保证尺寸大小不变
     */
    public static float px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px 转 sp，保证文字大小不变
     */
    public static float px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / fontScale + 0.5f);
    }

}
