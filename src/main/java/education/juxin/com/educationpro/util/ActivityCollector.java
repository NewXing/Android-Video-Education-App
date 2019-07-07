package education.juxin.com.educationpro.util;

import android.app.Activity;

import java.util.Stack;

/**
 * 创建者：王兴
 * 创建时间：2015/9/6 8:48
 * 类描述：ActivityCollector - 基于基类BaseActivity的Activity栈管理器
 * 备注：
 */
public class ActivityCollector {

    private static Stack<Activity> activityStack = new Stack<>();

    public static void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityStack.remove(activity);
    }

    public static void finishAll() {
        while (!activityStack.isEmpty()) {
            Activity activity = activityStack.pop();
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void finishOther() {
        while (activityStack.size() > 1) {
            Activity activity = activityStack.pop();
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void finishNumber(int number) {
        for (int i = 0; i < number; i++) {
            if (activityStack.size() > 1) {
                Activity activity = activityStack.pop();
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
    }

    public static Activity peek() {
        return activityStack.peek(); // java.util.Stack.peek() 查看栈顶对象而不移除它
    }

    /**
     * 获取当前Activity（栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
        }
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public static void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束activity到指定位置
     *
     * @param clazz 指定的activity
     */
    public static void finishActivityTo(Class<?> clazz) {
        while (!activityStack.empty()) {
            Activity activity = activityStack.peek();
            if (activity.getClass().equals(clazz)) {
                break;
            } else {
                activity.finish();
                activityStack.pop();
            }
        }
    }

}
