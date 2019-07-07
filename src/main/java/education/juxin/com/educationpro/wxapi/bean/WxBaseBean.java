package education.juxin.com.educationpro.wxapi.bean;

public class WxBaseBean {

    private String errcode;
    private String errmsg;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    @Override
    public String toString() {
        return "WxBaseBean{" +
                "errcode='" + errcode + '\'' +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
