package education.juxin.com.educationpro.base;


/**
 * 实体类的基类 json数据最外层的数据封装
 * <p>
 * The type Base bean.
 * create time 2018 -3 -20
 */
public class BaseBean {

    private int code; // 返回错误码,正确返回0,错误返回大于0的值
    private String msg; // 返回错误的具体详情信息
    private String total; // 总条数

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        if (msg == null || msg.isEmpty()) {
            return "未知错误";
        }
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
