package education.juxin.com.educationpro.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 创建者：王兴
 * 创建时间：2016/4/11 9:41
 * 类说明：SPHelper - 管理SharedPreferences的工具类
 */
public class SPHelper {

    private static final String TAG = "SPHelper";
    /**
     * 保存在手机里面的默认文件名
     */
    private static String FILE_NAME = "wx_sp_date";
    /**
     * 文件的存储模式
     */
    private static int spMode = Context.MODE_PRIVATE;

    /**
     * 保存数据，类型限于 String、boolean、int、float、long
     * 需要知道待保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context Activity 就行
     * @param key     键
     * @param object  要存储的值
     */
    public static void setSimpleKeyValue(Context context, String key, Object object) {
        // 获取 SharedPreferences 单例
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, spMode);
        SharedPreferences.Editor editor = sp.edit();

        // 具体写入操作
        switch (object.getClass().getSimpleName()) {
            case "String":
                editor.putString(key, (String) object);
                break;
            case "Boolean":
                editor.putBoolean(key, (Boolean) object);
                break;
            case "Integer":
                editor.putInt(key, (Integer) object);
                break;
            case "Float":
                editor.putFloat(key, (Float) object);
                break;
            case "Long":
                editor.putLong(key, (Long) object);
                break;
        }
        editor.apply();
    }

    /**
     * 获取保存数据的方法
     * 根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context       Activity 就行
     * @param key           待获取值对应的键
     * @param defaultObject 缺省值
     */
    public static Object getSimpleParam(Context context, String key, Object defaultObject) {
        // 获取 SharedPreferences 单例
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, spMode);

        // 具体读取操作
        switch (defaultObject.getClass().getSimpleName()) {
            case "String":
                return sp.getString(key, (String) defaultObject);
            case "Boolean":
                return sp.getBoolean(key, (Boolean) defaultObject);
            case "Integer":
                return sp.getInt(key, (Integer) defaultObject);
            case "Float":
                return sp.getFloat(key, (Float) defaultObject);
            case "Long":
                return sp.getLong(key, (Long) defaultObject);
            default:
                return null;
        }
    }

    /**
     * 清空 SharedPreferences XML 内容
     *
     * @param context Activity 就行
     */
    public static void clearSharedPreference(Context context) {
        // 获取 SharedPreferences 单例
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, spMode);
        SharedPreferences.Editor editor = sp.edit();

        // 具体写入操作
        editor.clear();
        editor.apply();
    }

    /**
     * 根据指定的 key 删除对应键值对
     *
     * @param context Activity 就行
     * @param key     待获取值对应的键
     */
    public static void removeByKey(Context context, String key) {
        // 获取 SharedPreferences 单例
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, spMode);
        SharedPreferences.Editor editor = sp.edit();

        // 具体写入操作
        if (sp.contains(key)) {
            editor.remove(key);
            editor.apply();
        } else {
            LogManager.e(TAG, "指定的 key 不存在！");
        }
    }

    /**
     * 保存任意 object 对象到 SharedPreferences
     * 使用 SharedPreferences 存储一个自定义对象，原理就是 Base64 转码为字符串存储。
     *
     * @param key    键
     * @param object 待保存的对象
     */
    public void setObject(Context context, String key, Object object) {
        // 获取 SharedPreferences 单例
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, spMode);

        ObjectOutputStream objOutputStream = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            objOutputStream = new ObjectOutputStream(outputStream);
            objOutputStream.writeObject(object);
            String objectVal = new String(Base64.encode(outputStream.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, objectVal);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从SharedPreferences获取任意object对象
     *
     * @param key    键
     * @param aClass Class 对象
     */
    public <T> T getObject(Context context, String key, Class<T> aClass) {
        // 获取 SharedPreferences 单例
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, spMode);

        if (preferences.contains(key)) {
            String objectVal = preferences.getString(key, null);
            assert objectVal != null;
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
            ObjectInputStream objInputStream = null;
            try {
                objInputStream = new ObjectInputStream(inputStream);
                return (T) objInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    if (objInputStream != null) {
                        objInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
