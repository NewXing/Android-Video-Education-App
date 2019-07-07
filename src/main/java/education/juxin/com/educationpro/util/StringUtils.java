package education.juxin.com.educationpro.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * 验证手机号码
     *
     * @param phone 要验证的手机号
     * @return 验证结果
     */
    public static boolean isPhoneNumber(String phone) {
        return !isEmpty(phone) && phone.length() == 11 && (phone.startsWith("134")
                || phone.startsWith("135")
                || phone.startsWith("136")
                || phone.startsWith("137")
                || phone.startsWith("138")
                || phone.startsWith("139")
                || phone.startsWith("147")
                || phone.startsWith("150")
                || phone.startsWith("151")
                || phone.startsWith("152")
                || phone.startsWith("157")
                || phone.startsWith("158")
                || phone.startsWith("159")
                || phone.startsWith("178")
                || phone.startsWith("182")
                || phone.startsWith("183")
                || phone.startsWith("184")
                || phone.startsWith("187")
                || phone.startsWith("188")
                || phone.startsWith("130")
                || phone.startsWith("131")
                || phone.startsWith("132")
                || phone.startsWith("145")
                || phone.startsWith("155")
                || phone.startsWith("156")
                || phone.startsWith("171")
                || phone.startsWith("175")
                || phone.startsWith("176")
                || phone.startsWith("185")
                || phone.startsWith("186")
                || phone.startsWith("133")
                || phone.startsWith("149")
                || phone.startsWith("153")
                || phone.startsWith("173")
                || phone.startsWith("177")
                || phone.startsWith("180")
                || phone.startsWith("181")
                || phone.startsWith("189")
                || phone.startsWith("170"));
    }

    /**
     * 验证密码
     *
     * @param s 字符串
     * @return 是否6-16位字母和数字
     */
    public static boolean checkPassword(String s) {
        if (!TextUtils.isEmpty(s)) {
            String regex = "^([A-Z]|[a-z]|[0-9]|[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“'。，、？]){6,16}$";
            return s.matches(regex);
        }
        return false;
    }

    /**
     * 身份证号格式校验
     */
    public static boolean checkoutIdNumber(String idNumber) {
        String idNumberRegex = "^[a-zA-Z][\\\\w\\\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9]" +
                "[\\\\w\\\\.-]*[a-zA-Z0-9]\\\\.[a-zA-Z][a-zA-Z\\\\.]*[a-zA-Z]$";
        return !TextUtils.isEmpty(idNumber) && idNumber.matches(idNumberRegex);
    }

    /**
     * 邮箱格式校验
     */
    public static boolean checkoutEmailAddress(String emailAddress) {
        String emailAddressRegex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return !TextUtils.isEmpty(emailAddress) && emailAddress.matches(emailAddressRegex);
    }

    /**
     * 是否为纯数字字符串
     */
    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        }

        String str = obj.toString();
        if (str.trim().isEmpty()) {
            return false;
        }

        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(String value) {
        return value == null || TextUtils.isEmpty(value.trim());
    }

    /**
     * 判断(Unicode编码)字符串中是否包含汉字
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);

        return m.find();
    }

    public static String unicodeToChinese(String unicode) {
        StringBuilder out = new StringBuilder();
        if (!isEmpty(unicode)) {
            for (int i = 0; i < unicode.length(); i++) {
                out.append(unicode.charAt(i));
            }
        }
        return out.toString();
    }

    /**
     * 检测字符串中是否有emoji字符
     */
    public static boolean containsEmoji(String source) {
        if (source == null || source.isEmpty()) {
            return false;
        }

        for (int i = 0; i < source.length(); i++) {
            char codePoint = source.charAt(i);
            if (isEmojiCharacter(codePoint)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    /**
     * 字符串分段设置特效
     */
    public static SpannableString setColorSpan(String srcString, int colorId, int startIndex, int endIndex) {
        SpannableString ss = new SpannableString(srcString);
        ss.setSpan(new ForegroundColorSpan(colorId), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static SpannableString setColorSpan(String srcString, int colorId) {
        return setColorSpan(srcString, colorId, 0, srcString.length());
    }

    public static SpannableString setUnderlineSpan(String srcString, int startIndex, int endIndex) {
        SpannableString ss = new SpannableString(srcString);
        ss.setSpan(new UnderlineSpan(), startIndex, endIndex, 0);
        return ss;
    }

    public static SpannableString setUnderlineSpan(String srcString) {
        return setUnderlineSpan(srcString, 0, srcString.length());
    }

    public static SpannableString setFontSizeSp(Context context, String str, int start, int end, int spSize) {
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new AbsoluteSizeSpan(ScreenUtils.sp2px(context, spSize)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

}
