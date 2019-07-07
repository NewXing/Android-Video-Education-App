package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class AboutUsBean extends BaseBean {

    private AboutUsData data;

    public AboutUsData getData() {
        return data;
    }

    public void setData(AboutUsData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AboutUsBean{" +
                "data=" + data +
                '}';
    }

    public class AboutUsData {

        private String content;
        private String title;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "AboutUsBean{" +
                    "content='" + content + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }
}
