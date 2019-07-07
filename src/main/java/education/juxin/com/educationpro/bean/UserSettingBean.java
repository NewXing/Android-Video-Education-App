package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class UserSettingBean extends BaseBean {

    private UserSettingData data;

    public UserSettingData getData() {
        return data;
    }

    public void setData(UserSettingData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserSettingBean{" +
                "data=" + data +
                '}';
    }

    public class UserSettingData {

        private String id;
        private String whetherOpen4g;
        private String whetherOpenMessagePush;
        private String whetherOpenVideoPlay;
        private String whetherOpenWifi;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getWhetherOpen4g() {
            return whetherOpen4g;
        }

        public void setWhetherOpen4g(String whetherOpen4g) {
            this.whetherOpen4g = whetherOpen4g;
        }

        public boolean isWhetherOpen4g() {
            return Integer.valueOf(whetherOpen4g) == 1;
        }

        public String getWhetherOpenMessagePush() {
            return whetherOpenMessagePush;
        }

        public void setWhetherOpenMessagePush(String whetherOpenMessagePush) {
            this.whetherOpenMessagePush = whetherOpenMessagePush;
        }

        public boolean isWhetherOpenMessagePush() {
            return Integer.valueOf(whetherOpenMessagePush) == 1;
        }

        public String getWhetherOpenVideoPlay() {
            return whetherOpenVideoPlay;
        }

        public void setWhetherOpenVideoPlay(String whetherOpenVideoPlay) {
            this.whetherOpenVideoPlay = whetherOpenVideoPlay;
        }

        public boolean isWhetherOpenVideoPlay() {
            return Integer.valueOf(whetherOpenVideoPlay) == 1;
        }

        public String getWhetherOpenWifi() {
            return whetherOpenWifi;
        }

        public void setWhetherOpenWifi(String whetherOpenWifi) {
            this.whetherOpenWifi = whetherOpenWifi;
        }

        public boolean isWhetherOpenWifi() {
            return Integer.valueOf(whetherOpenWifi) == 1;
        }

        @Override
        public String toString() {
            return "UserSettingData{" +
                    "id='" + id + '\'' +
                    ", whetherOpen4g='" + whetherOpen4g + '\'' +
                    ", whetherOpenMessagePush='" + whetherOpenMessagePush + '\'' +
                    ", whetherOpenVideoPlay='" + whetherOpenVideoPlay + '\'' +
                    ", whetherOpenWifi='" + whetherOpenWifi + '\'' +
                    '}';
        }
    }
}
