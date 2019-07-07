package education.juxin.com.educationpro.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 物理存储工具类，清除缓存工具类
 * StorageUtil
 */
public class StorageUtil {

    private static final int SIZE_TYPE_B = 1;   // 获取文件大小单位为B的double值
    private static final int SIZE_TYPE_KB = 2;  // 获取文件大小单位为KB的double值
    private static final int SIZE_TYPE_MB = 3;  // 获取文件大小单位为MB的double值
    private static final int SIZE_TYPE_GB = 4;  // 获取文件大小单位为GB的double值

    /**
     * 判断外部存储是否可用
     */
    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取存储可用空间（优先外部存储空间）
     *
     * @return int 存储空间大小（byte）
     */
    public static long getAvailableMemorySize() {
        String filePath;
        if (isExternalStorageAvailable()) {
            filePath = Environment.getExternalStorageDirectory().getPath();
        } else {
            filePath = Environment.getDataDirectory().getPath();
        }
        StatFs stat = new StatFs(filePath);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 根据文件绝对路径获取文件名
     */
    public static String getFileByName(String filePath) {
        if ("".equals(filePath.trim())) {
            return null;
        }
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 创建文件
     */
    public static File makeCacheDir(String fileName) {
        if (isExternalStorageAvailable()) {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
        return null;
    }

    public static File makeFile(String fileName) {
        if (isExternalStorageAvailable()) {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    return file;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean makeDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {
            return true;
        } else {
            return false;
        }
    }

    public static File getOutputFile() {
        File dirFile = StorageUtil.makeCacheDir("");
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        return new File(dirFile, fileName);
    }

    // ======================================== 清除存储相关 ======================================== //

    /*
     * 系统文件（系统的文件系统，不可修改）
     * 内置SD卡（即ROM，不可移除，RAM是手机的运行内存）
     * 外置SD卡（可以移除）
     *
     * 应用内数据的路径：
     * /data/data/app_package_name/cache - 应用内缓存，对应方法：getCacheDir()
     * /data/data/app_package_name/files - 应用内文件，对应方法：getFilesDir()
     * /data/data/app_package_name/shared_prefs - 应用内配置文件(.xml文件)
     * /data/data/app_package_name/databases - 应用内数据库(.db文件)
     *
     * 外部Cache路径：/mnt/sdcard/android/data/app_package_name/cache 一般存储缓存数据，对应方法：getExternalCacheDir()获取
     * 外部Files路径：/mnt/sdcard/android/data/app_package_name/files 存储长时间存在的数据，对应方法：getExternalFilesDir(String)获取
     *
     * SD卡根目录：Environment.getExternalStorageDirectory().getAbsolutePath();
     */

    /**
     * 清除本应用内部Files(/data/data/app_package_name/files)
     */
    public static void cleanAppFiles(Context context) {
        deleteFiles(context.getFilesDir());
    }

    /**
     * 清除本应用内部缓存(/data/data/app_package_name/cache)
     */
    public static void cleanAppInternalCache(Context context) {
        deleteFiles(context.getCacheDir());
    }

    /**
     * 清除本应用内部SP(/data/data/app_package_name/shared_prefs)
     */
    public static void cleanAppSharedPreference(Context context) {
        deleteFiles("/data/data/" + context.getPackageName() + "/shared_prefs");
    }

    /**
     * 清除本应用内部数据库(/data/data/app_package_name/databases)
     */
    public static void cleanAppDatabases(Context context) {
        deleteFiles("/data/data/" + context.getPackageName());
    }

    /**
     * 清除本应用外部缓存(/mnt/sdcard/android/data/app_package_name/cache)
     */
    public static void cleanAppExternalCache(Context context) {
        if (isExternalStorageAvailable()) {
            deleteFiles(context.getExternalCacheDir());
        }
    }

    /**
     * 清除本应用外部Files(/mnt/sdcard/android/data/app_package_name/files)
     */
    public static void cleanAppExternalFiles(Context context) {
        if (isExternalStorageAvailable()) {
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        }
    }

    /**
     * 清除指定文件
     */
    private static void deleteFiles(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            for (File aFile : file.listFiles()) {
                deleteFileOrDirectory(aFile);
            }
        }
    }

    private static void deleteFiles(String filePath) {
        deleteFiles(new File(filePath));
    }

    public static void deleteFiles(String filePath, ArrayList<String> fileName) {
        if (new File(filePath).exists()) {
            for (String aFileName : fileName) {
                File file = new File(filePath + "/" + aFileName);
                deleteFileOrDirectory(file);
            }
        }
    }

    public static void deleteFileOrDirectory(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isFile()) {
            delete(file);
        }

        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                delete(file);
            } else {
                for (File f : childFile) {
                    deleteFileOrDirectory(f);
                }
                delete(file);
            }
        }
    }

    /**
     * 文件删除不掉的话先GC一下再尝试删除（单线程下）
     */
    private static void delete(File file) {
        try {
            if (!file.delete()) {
                System.gc();
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================== 读写文件相关 ======================================== //

    /**
     * 读取txt文件的内容
     */
    public static String readTxtFile(String filePath) {
        StringBuilder contentText = new StringBuilder();
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                streamReader = new InputStreamReader(new FileInputStream(file), "utf-8");
                bufferedReader = new BufferedReader(streamReader);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    contentText.append(lineTxt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return contentText.toString();
    }

    /**
     * 把String保存到文件中
     */
    public static void saveText2File(String text, String dirPath, String fileName, boolean append) {
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(fileName)) {
            return;
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            saveText2File(text, file, append);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveText2File(String text, String dirPath, boolean append) {
        File file = new File(dirPath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            saveText2File(text, file, append);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveText2File(String text, File file, boolean append) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, append);
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void stringWriteToFile(String filePath, String result) {
        File file = new File(filePath);
        try {
            if (!file.isFile()) {
                file.createNewFile();
                DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
                out.writeBytes(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readStringFromFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        StringBuilder lastStr = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                lastStr.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return lastStr.toString();
    }

    // ===================================== 格式化存储数据相关 ===================================== //

    /**
     * 获取指定文件或文件夹的大小，返回的数值按指定单位换算
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrDirectorySize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getDirectorySizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormatFileSize(blockSize, sizeType);
    }

    /**
     * 获取指定文件夹的大小
     */
    private static long getDirectorySizes(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            File childFiles[] = file.listFiles();
            for (File aFile : childFiles) {
                if (aFile.isDirectory()) {
                    size = size + getDirectorySizes(aFile);
                } else {
                    size = size + getFileSize(aFile);
                }
            }
        }
        return size;
    }

    /**
     * 获取指定文件的大小
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis;
            fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        }
        return size;
    }

    /**
     * 格式化存储数据大小
     *
     * @param fileS    大小
     * @param sizeType 类型
     * @return 格式完成后大小
     */
    private static double FormatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZE_TYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;

            case SIZE_TYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;

            case SIZE_TYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;

            case SIZE_TYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;

            default:
                break;
        }
        return fileSizeLong;
    }

}
