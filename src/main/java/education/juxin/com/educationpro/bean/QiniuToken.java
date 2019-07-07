package education.juxin.com.educationpro.bean;

public class QiniuToken {

    private String uptoken;

    public String getUptoken() {
        return uptoken;
    }

    public void setUptoken(String uptoken) {
        this.uptoken = uptoken;
    }

    @Override
    public String toString() {
        return "QiniuToken{" +
                "uptoken='" + uptoken + '\'' +
                '}';
    }
}
