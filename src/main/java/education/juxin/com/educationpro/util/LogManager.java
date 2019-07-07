package education.juxin.com.educationpro.util;

import android.util.Log;

/**
 * 创建者：王兴
 * 创建时间：2016/4/11 9:51
 * 类说明：LogManager - 管理和简化Log打印的工具类
 */
public class LogManager {

    // 默认Tag值
    private static final String TAG = "LogManager";

    // 是否需要打印，在application.onCreate方法中初始化
    public static boolean isDebug;

    /**
     * 四个默认 tag 的打印信息函数
     *
     * @param msg 打印信息
     */
    public static void v(String msg) {
        if (isDebug) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (isDebug) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }

    /**
     * 传入自定义 tag 的打印信息函数
     *
     * @param tag 自定义 tag
     * @param msg 打印信息
     */
    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

}
