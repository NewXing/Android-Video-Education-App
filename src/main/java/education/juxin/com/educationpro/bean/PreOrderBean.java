package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class PreOrderBean extends BaseBean {

    private PreOrderData data;

    public PreOrderData getData() {
        return data;
    }

    public void setData(PreOrderData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PreOrderBean{" +
                "data=" + data +
                '}';
    }

    public class PreOrderData {

        private String orderno;
        private String orderId;
        private String code;
        private String courseId;
        private String courseName;
        private String coverImgUrl;
        private String paytime;
        private String totalAmount;

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

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
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

        public String getCoverImgUrl() {
            return coverImgUrl;
        }

        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
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

        @Override
        public String toString() {
            return "PreOrderData{" +
                    "orderno='" + orderno + '\'' +
                    ", orderId='" + orderId + '\'' +
                    ", code='" + code + '\'' +
                    ", courseId='" + courseId + '\'' +
                    ", courseName='" + courseName + '\'' +
                    ", coverImgUrl='" + coverImgUrl + '\'' +
                    ", paytime='" + paytime + '\'' +
                    ", totalAmount='" + totalAmount + '\'' +
                    '}';
        }
    }
}
