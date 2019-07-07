package education.juxin.com.educationpro.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import java.util.ArrayList;

import education.juxin.com.educationpro.util.DBHelper;
import education.juxin.com.educationpro.util.LogManager;
import education.juxin.com.educationpro.util.StorageUtil;

import static education.juxin.com.educationpro.util.DBHelper.DBHelperStat.BRAND_TABLE_NAME;

public class VideoCacheManager {
    /**
     * 创建视频缓存文件的路径和名称
     */
    public static String checkCacheVideoPath(String fileName) {
        if (StorageUtil.isExternalStorageAvailable()) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/._qiantu_";
            StorageUtil.makeDir(path);
            return path + "/." + fileName;
        }
        return "";
    }

    /**
     * 把视频的附加信息写入数据库
     */
    public static void writeVideoInfo(Context context, VideoCacheInfoData data) {
        DBHelper dbHelper = DBHelper.getDBHelperInstance(context);
        dbHelper.openDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put("cache_file_name", data.getCacheFileName());
        contentValues.put("title", data.getTitle());
        contentValues.put("course_cover_img", data.getCourseCoverImg());
        contentValues.put("course_end_date", data.getCourseEndDate());
        contentValues.put("current_lesson_num", data.getCurrentLessonNum());
        contentValues.put("main_teacher_name", data.getMainTeacherName());
        dbHelper.insert(BRAND_TABLE_NAME, contentValues);
        dbHelper.closeDB();
    }

    /**
     * 把视频的附加信息从数据库中读出
     */
    public static ArrayList<VideoCacheInfoData> readAllVideoInfo(Context context) {
        DBHelper dbHelper = DBHelper.getDBHelperInstance(context);
        dbHelper.openDB();
        ArrayList<VideoCacheInfoData> arrayList = new ArrayList<>();
        VideoCacheInfoData data;
        Cursor cursor = dbHelper.queryTableData(BRAND_TABLE_NAME);
        while (cursor.moveToNext()) {
            data = new VideoCacheInfoData();
            data.setCacheFileName(cursor.getString(cursor.getColumnIndex("cache_file_name")));
            data.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            data.setCourseCoverImg(cursor.getString(cursor.getColumnIndex("course_cover_img")));
            data.setCourseEndDate(cursor.getString(cursor.getColumnIndex("course_end_date")));
            data.setCurrentLessonNum(cursor.getString(cursor.getColumnIndex("current_lesson_num")));
            data.setMainTeacherName(cursor.getString(cursor.getColumnIndex("main_teacher_name")));
            arrayList.add(data);
        }
        cursor.close();
        dbHelper.closeDB();

        LogManager.e("已存入的数据：" + arrayList);

        return arrayList;
    }

    public static VideoCacheInfoData readVideoInfoById(Context context, String videoId) {
        DBHelper dbHelper = DBHelper.getDBHelperInstance(context);
        dbHelper.openDB();
        Cursor cursor = dbHelper.queryData(BRAND_TABLE_NAME, null, "cache_file_name=?", new String[]{videoId});
        VideoCacheInfoData data = new VideoCacheInfoData();
        data.setCacheFileName(cursor.getString(cursor.getColumnIndex("cache_file_name")));
        data.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        data.setCourseCoverImg(cursor.getString(cursor.getColumnIndex("course_cover_img")));
        data.setCourseEndDate(cursor.getString(cursor.getColumnIndex("course_end_date")));
        data.setCurrentLessonNum(cursor.getString(cursor.getColumnIndex("current_lesson_num")));
        data.setMainTeacherName(cursor.getString(cursor.getColumnIndex("main_teacher_name")));
        cursor.close();
        dbHelper.closeDB();
        return data;
    }

    /**
     * 把视频的附加信息从数据库中删除
     */
    public static void deleteAllVideoInfo(Context context) {
        DBHelper dbHelper = DBHelper.getDBHelperInstance(context);
        dbHelper.openDB();
        dbHelper.deleteTable(BRAND_TABLE_NAME);
        dbHelper.closeDB();
    }

    public static void deleteVideoInfoById(Context context, String videoId) {
        DBHelper dbHelper = DBHelper.getDBHelperInstance(context);
        dbHelper.openDB();
        dbHelper.deleteData(BRAND_TABLE_NAME, "cache_file_name=?", new String[]{videoId});
        dbHelper.closeDB();
    }

    /**
     * 判断是否已经下载
     */
    public static boolean isExistsVideo(Context context, String videoId) {
        boolean isExistsFlag;

        DBHelper dbHelper = DBHelper.getDBHelperInstance(context);
        dbHelper.openDB();
        Cursor cursor = dbHelper.queryData(BRAND_TABLE_NAME, null, "cache_file_name=?", new String[]{videoId});
        isExistsFlag = cursor.moveToNext();
        cursor.close();
        dbHelper.closeDB();

        return isExistsFlag;
    }
}
