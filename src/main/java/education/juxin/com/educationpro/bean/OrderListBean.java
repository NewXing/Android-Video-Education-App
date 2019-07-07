package education.juxin.com.educationpro.bean;

import java.util.List;

import education.juxin.com.educationpro.base.BaseBean;

public class OrderListBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OrderListBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String orderId;
        private String orderno;
        private String courseId;
        private String courseName;
        private String paytime;
        private String totalAmount;
        private String coverImgUrl;
        private String code;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getOrderno() {
            return orderno;
        }

        public void setOrderno(String orderno) {
            this.orderno = orderno;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getPaytime() {
            return paytime;
        }

        public void setPaytime(String paytime) {
            this.paytime = paytime;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getCoverImgUrl() {
            return coverImgUrl;
        }

        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "orderId='" + orderId + '\'' +
                    ", orderno='" + orderno + '\'' +
                    ", courseId='" + courseId + '\'' +
                    ", courseName='" + courseName + '\'' +
                    ", paytime='" + paytime + '\'' +
                    ", totalAmount='" + totalAmount + '\'' +
                    ", coverImgUrl='" + coverImgUrl + '\'' +
                    ", code='" + code + '\'' +
                    '}';
        }
    }
}
