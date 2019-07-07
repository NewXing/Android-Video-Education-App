package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class QnVideoUrlBean extends BaseBean {

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "QnVideoUrlBean{" +
                "data='" + data + '\'' +
                '}';
    }
}
