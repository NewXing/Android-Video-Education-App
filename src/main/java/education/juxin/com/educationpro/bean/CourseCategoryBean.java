package education.juxin.com.educationpro.bean;

import java.util.List;

import education.juxin.com.educationpro.base.BaseBean;

public class CourseCategoryBean extends BaseBean {

    private List<CourseCategoryData> data;

    public List<CourseCategoryData> getData() {
        return data;
    }

    public void setData(List<CourseCategoryData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CourseCategoryBean{" +
                "data=" + data +
                '}';
    }

    public class CourseCategoryData {

        private String id;
        private String name;
        private String icon;
        private String state;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return "CourseCategoryData{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", state='" + state + '\'' +
                    '}';
        }
    }
}
