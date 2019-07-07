package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class GiftDetailBean extends BaseBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GiftDetailBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String id;
        private String imgUrl;
        private String price;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id=" + id +
                    ", imgUrl='" + imgUrl + '\'' +
                    ", price=" + price +
                    '}';
        }
    }
}
