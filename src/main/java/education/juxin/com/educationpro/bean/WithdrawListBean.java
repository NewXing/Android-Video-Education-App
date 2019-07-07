package education.juxin.com.educationpro.bean;

import java.util.List;

import education.juxin.com.educationpro.base.BaseBean;

public class WithdrawListBean extends BaseBean {

    private List<WithdrawData> data;

    public List<WithdrawData> getData() {
        return data;
    }

    public void setData(List<WithdrawData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "WithdrawListBean{" +
                "data=" + data +
                '}';
    }

    public class WithdrawData {

        private String createtime;
        private String id;
        private String price;
        private String type;

        public String getCreateTime() {
            return createtime;
        }

        public void setCreateTime(String createTime) {
            this.createtime = createTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "WithdrawData{" +
                    "createtime='" + createtime + '\'' +
                    ", id='" + id + '\'' +
                    ", price='" + price + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
