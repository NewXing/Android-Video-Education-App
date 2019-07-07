package education.juxin.com.educationpro.bean;

import java.util.ArrayList;

import education.juxin.com.educationpro.base.BaseBean;

public class ExtractionPriceBean extends BaseBean {

    private ArrayList<ExtractionPriceData> data;

    public ArrayList<ExtractionPriceData> getData() {
        return data;
    }

    public void setData(ArrayList<ExtractionPriceData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ExtractionPriceBean{" +
                "data=" + data +
                '}';
    }

    public class ExtractionPriceData {

        private String price;
        private String id;
        private String page;
        private String rows;
        private String createtime;

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

        @Override
        public String toString() {
            return "ExtractionPriceData{" +
                    "price='" + price + '\'' +
                    ", id='" + id + '\'' +
                    ", page='" + page + '\'' +
                    ", rows='" + rows + '\'' +
                    ", createtime='" + createtime + '\'' +
                    '}';
        }
    }
}
