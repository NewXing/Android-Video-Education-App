package education.juxin.com.educationpro.bean;

import java.util.List;

import education.juxin.com.educationpro.base.BaseBean;

public class GiftBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GiftBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String imgUrl;
        private String price;
        private String id;
        private String page;
        private String rows;
        private String createtime;
        private String content;

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getRows() {
            return rows;
        }

        public void setRows(String rows) {
            this.rows = rows;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "imgUrl='" + imgUrl + '\'' +
                    ", price='" + price + '\'' +
                    ", id=" + id +
                    ", page=" + page +
                    ", rows=" + rows +
                    ", createtime='" + createtime + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}
