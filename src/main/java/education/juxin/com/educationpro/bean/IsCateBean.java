package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class IsCateBean extends BaseBean {

    private boolean data;

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "IsCateBean{" +
                "data=" + data +
                '}';
    }
}
