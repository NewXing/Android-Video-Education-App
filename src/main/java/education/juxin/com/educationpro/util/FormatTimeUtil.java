package education.juxin.com.educationpro.util;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * 创建者：王兴
 * 创建时间：2016/6/2 10:26
 * 类描述：FormatTimeUtil - 时间格式处理的工具类
 * 备注：
 */
public class FormatTimeUtil {
    /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr 字符串日期
     * @param format  如：yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateStr2TimeStamp(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(dateStr).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param format  如：yyyy-MM-dd HH:mm:ss
     */
    public static String formatTimeStamp2DateStr(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (!StringUtils.isNumeric(seconds)) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    public static String formatTimeStamp2DateStr(String seconds) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (!StringUtils.isNumeric(seconds)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * Data对象转换成日期格式字符串
     *
     * @param date   Date 日期
     * @param format String 格式
     * @return String 日期字符串
     */
    public static String formatDate2DateStr(Date date, String format) {
        String result = "";
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                result = sdf.format(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}
