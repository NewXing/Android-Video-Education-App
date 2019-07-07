package education.juxin.com.educationpro.util;

import java.text.NumberFormat;

/**
 * 创建者：王兴
 * 创建时间：2016/4/11 9:23
 * 类说明：FormatNumberUtil - 数字格式处理的工具类
 */
public class FormatNumberUtil {

    /**
     * 将double类型数据格式化成指定格式的字符串
     *
     * @param digit        小数点保留位数
     * @param number       要格式化的数字
     * @param groupingUsed 是否添加默认分隔符，false-取消，true-添加
     */
    public static String doubleFormat(int digit, double number, boolean groupingUsed) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setGroupingUsed(groupingUsed);
        nf.setMinimumFractionDigits(digit);
        nf.setMaximumFractionDigits(digit);
        return nf.format(number);
    }

    public static String doubleFormat(int digit, String numberStr, boolean groupingUsed) {
        double number;

        try {
            number = Double.valueOf(numberStr);
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            number = 0;
        }

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setGroupingUsed(groupingUsed);
        nf.setMinimumFractionDigits(digit);
        nf.setMaximumFractionDigits(digit);

        return nf.format(number);
    }

    /**
     * 当浮点型数据位数超过一定位数(设置10位左右)后，数据变成科学计数法显示，
     * 用此方法可以使其正常显示，并限制小数点后位数
     */
    public static String doubleFormat(double number) {
        if (number != 0.00) {
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
            return df.format(number);
        } else {
            return "0.00";
        }
    }

    public static String doubleFormat(String numberStr, String format) {
        double number;

        try {
            number = Double.valueOf(numberStr);
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            number = 0;
        }

        if (number != 0.00) {
            java.text.DecimalFormat df = new java.text.DecimalFormat(format); // 0.00
            return df.format(number);
        } else {
            return "0";
        }
    }

    public static String doubleFormat(String numberStr) {
        double number;

        try {
            number = Double.valueOf(numberStr);
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            number = 0;
        }

        if (number != 0.00) {
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
            return df.format(number);
        } else {
            return "0.00";
        }
    }
}
