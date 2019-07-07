package education.juxin.com.educationpro.party3.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import education.juxin.com.educationpro.ProApplication;
import education.juxin.com.educationpro.ui.activity.dynamic.CourseDetailActivity;
import education.juxin.com.educationpro.ui.activity.dynamic.SpaceMainActivity;
import education.juxin.com.educationpro.util.LogManager;
import education.juxin.com.educationpro.ui.activity.home.HomeActivity;

/**
 * 极光推送接收器
 * Created on 2018/3/21.
 */
public class CusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String action = intent.getAction();

        if (bundle == null || action == null) {
            LogManager.e("[MyReceiver] bundle or action is null!");
            return;
        }

        LogManager.e(printBundle(bundle));

        switch (action) {
            case JPushInterface.ACTION_REGISTRATION_ID:
                LogManager.e("[MyReceiver] 推送的RegistrationId: " + bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID));
                break;

            case JPushInterface.ACTION_MESSAGE_RECEIVED:
                LogManager.e("[MyReceiver] 推送的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                break;

            case JPushInterface.ACTION_NOTIFICATION_RECEIVED:
                LogManager.e("[MyReceiver] 推送的通知的ID: " + bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
                processMessage(bundle);
                break;

            case JPushInterface.ACTION_NOTIFICATION_OPENED:
                LogManager.e("[MyReceiver] 用户点击打开通知: " + bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
                gotoActivity(context);
                break;

            case JPushInterface.ACTION_RICHPUSH_CALLBACK:
                LogManager.e("[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                break;

            case JPushInterface.ACTION_CONNECTION_CHANGE:
                LogManager.e("[MyReceiver] connected state change to：" + intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false));
                break;

            default:
                break;
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LogManager.e("This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:").append(key).append(", value: [").append(myKey).append(" - ").append(json.optString(myKey)).append("]");
                    }
                } catch (JSONException e) {
                    LogManager.e("Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private void processMessage(Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        if (null == extras || extras.trim().length() == 0) {
            return;
        }
        try {
            JSONObject json = new JSONObject(extras);
            Iterator<String> it = json.keys();

            Map<String, String> map = new HashMap<>();
            while (it.hasNext()) {
                String key = it.next();
                map.put(key, json.optString(key));
            }
            ProApplication.setJPushParams(map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void gotoActivity(Context context) {
        if (ProApplication.getJPushParams() == null || ProApplication.getJPushParams().size() == 0) {
            Intent intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            return;
        }

        Intent intent;
        switch (ProApplication.getJPushParams().get("type")) {
            case "1": // 公众号
                String teacherIdStr = ProApplication.getJPushParams().get("teacherId");
                if (teacherIdStr == null || teacherIdStr.isEmpty() || "null".equalsIgnoreCase(teacherIdStr)) {
                    return;
                }

                intent = new Intent(context, SpaceMainActivity.class);
                intent.putExtra("teacherId", teacherIdStr);
                break;

            case "2": // 课程
                String courseId = ProApplication.getJPushParams().get("courseId");
                if (courseId == null || courseId.isEmpty() || "null".equalsIgnoreCase(courseId)) {
                    return;
                }

                intent = new Intent(context, CourseDetailActivity.class);
                intent.putExtra("id_course_detail", courseId);
                break;

            case "3": // 课节
                String courseId3 = ProApplication.getJPushParams().get("courseId");
                String lessonId3 = ProApplication.getJPushParams().get("lessionId");
                if (courseId3 == null || courseId3.isEmpty() || "null".equalsIgnoreCase(courseId3)
                        || lessonId3 == null || lessonId3.isEmpty() || "null".equalsIgnoreCase(lessonId3)) {
                    return;
                }

                intent = new Intent(context, CourseDetailActivity.class);
                intent.putExtra("id_course_detail", courseId3);
                intent.putExtra("lesson_id", lessonId3);
                break;

            default:
                intent = new Intent(context, HomeActivity.class);
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ProApplication.getJPushParams().clear();
    }

}
