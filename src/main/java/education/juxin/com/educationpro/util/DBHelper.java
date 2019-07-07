package education.juxin.com.educationpro.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 管理SQLite数据库的工具类
 */
public class DBHelper extends SQLiteOpenHelper {

    public static class DBHelperStat {
        // 数据库版本号，初始值为1
        public static int DB_VERSION = 1;
        // 数据库名，必须
        public static final String DB_NAME = "qiantu_db";
        // 数据表的表名，必须
        public static final String BRAND_TABLE_NAME = "video_cache_info_tab";
        // 创建数据表的SQL语句，必须
        public static final String CREATE_BRAND_TABLE = "create table " + BRAND_TABLE_NAME + "(" +
                "cache_id integer primary key autoincrement, " +
                "cache_file_name text, " +
                "title text, " +
                "course_detail text, " +
                "count_lesson text, " +
                "course_cover_img text, " +
                "course_end_date text, " +
                "current_lesson_num text, " +
                "main_teacher_name text)";
    }

    private static DBHelper instanceDBHelper;
    private SQLiteDatabase db;
    private Context context;

    public static synchronized DBHelper getDBHelperInstance(Context context) {
        if (instanceDBHelper == null) {
            instanceDBHelper = new DBHelper(context);
        }
        return instanceDBHelper;
    }

    private DBHelper(Context context) {
        super(context, DBHelperStat.DB_NAME, null, DBHelperStat.DB_VERSION);
        this.context = context;
    }

    /**
     * 创建数据表
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBHelperStat.CREATE_BRAND_TABLE);
    }

    /**
     * 数据库版本自动更新
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * 执行自定义的SQL语句
     */
    public void execSQL(String SQLString) {
        db.execSQL(SQLString);
    }

    /**
     * 打开数据库
     */
    public void openDB() {
        db = this.getWritableDatabase();
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        if (db.isOpen()) {
            db.close();
        }
    }

    /**
     * 往指定表中插入一行数据
     */
    public long insert(String tableName, ContentValues values) {
        long rowId = db.insert(tableName, null, values);
        if (values != null) {
            values.clear();
        }
        return rowId;
    }

    /**
     * 删除指定的数据库
     */
    public boolean deleteDB(String DBName) {
        return context.deleteDatabase(DBName);
    }

    /**
     * 清空指定的数据表
     */
    public void deleteTable(String tableName) {
        db.delete(tableName, null, null);
    }

    /**
     * 删除数据表中指定的数据
     *
     * @param tableName   指定删除哪张表中的数据
     * @param whereSQLStr 第二、第三个参数用于去约束删除某一行或某几行的数据，不指定则默认删除所有行
     * @param rowIds      第二、第三个参数用于去约束删除某一行或某几行的数据，不指定则默认删除所有行
     */
    public void deleteData(String tableName, String whereSQLStr, String[] rowIds) {
        db.delete(tableName, whereSQLStr, rowIds);
    }

    /**
     * 更新数据表指定中的数据
     */
    public void update(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        if (values != null) {
            db.update(tableName, values, whereClause, whereArgs);
            values.clear();
        } else {
            LogManager.e("values is null！");
        }
    }

    /**
     * 查询数据表中附合条件的数据
     *
     * @param tableName     对应SQL语句：from table_name 指定查询的表名
     * @param columns       对应SQL语句：select column1, column2 指定查询的列名，如果不指定则默认查询所有列
     * @param selection     对应SQL语句：where column = value 指定where的约束条件
     * @param selectionArgs 为 where 中的占位符提供具体的值，第三、第四个参数用于约束查询某一行或某几行的数据，不指定则默认查询所有行
     */
    public Cursor queryData(String tableName, String[] columns, String selection, String[] selectionArgs) {
        return db.query(tableName, columns, selection, selectionArgs, null, null, null);
    }

    /**
     * 查询整个数据表中的数据
     */
    public Cursor queryTableData(String tableName) {
        return db.query(tableName, null, null, null, null, null, null);
    }

    // ===================== 二进制大文件的数据库存储，不建议将此类文件存储在数据库中 ===================== //

    /**
     * 存储图片
     */
    public long insertBitmap(String tableName, Bitmap bitmap) {
        if (bitmap != null) {
            ContentValues values = new ContentValues();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            values.put("bitmap_img", os.toByteArray());
            return insert(tableName, values);
        } else {
            LogManager.e("insertBitmap,bitmap is null!");
            return -1;
        }
    }

    /**
     * 按条件获取图片，返回Bitmap类型的图片对象
     */
    public Bitmap queryBitmap(String tableName, String[] columns, String selection, String[] selectionArgs) {
        byte[] in;
        Cursor cursor = queryData(tableName, columns, selection, selectionArgs);
        if (null != cursor && cursor.moveToFirst()) {
            do {
                in = cursor.getBlob(cursor.getColumnIndex("bitmap_img"));
            } while (cursor.moveToNext());
            cursor.close();
            return BitmapFactory.decodeByteArray(in, 0, in.length);
        } else {
            LogManager.e("queryBitmap,cursor is null!");
            return null;
        }
    }

    /**
     * 按条件获取图片，返回Drawable类型的图片对象
     */
    public Drawable queryDrawable(String tableName, String[] columns, String selection, String[] selectionArgs) {
        ByteArrayInputStream is = null;
        Cursor cursor = queryData(tableName, columns, selection, selectionArgs);
        if (null != cursor && cursor.moveToFirst()) {
            do {
                byte[] in = cursor.getBlob(cursor.getColumnIndex("bitmap_img"));
                is = new ByteArrayInputStream(in);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            LogManager.e("queryDrawable,cursor is null!");
        }
        return Drawable.createFromStream(is, "img");
    }

    /**
     * 更新图片
     */
    public void updateBitmap(String tableName, Bitmap bitmap, String whereClause, String[] whereArgs) {
        if (bitmap != null) {
            ContentValues values = new ContentValues();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            values.put("bitmap_img", os.toByteArray());
            update(tableName, values, whereClause, whereArgs);
        } else {
            LogManager.e("upBitmap,bitmap is null！");
        }
    }

}
