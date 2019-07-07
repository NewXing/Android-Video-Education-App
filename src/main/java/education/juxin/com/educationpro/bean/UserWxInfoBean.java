package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class UserWxInfoBean extends BaseBean {

    private UserWxInfoData data;

    public UserWxInfoData getData() {
        return data;
    }

    public void setData(UserWxInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserWxInfoBean{" +
                "data=" + data +
                '}';
    }

    public class UserWxInfoData {

        private String id;
        private String headimgurl;
        private String nickname;
        private String phone;
        private String realName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        @Override
        public String toString() {
            return "UserWxInfoData{" +
                    "id='" + id + '\'' +
                    ", headimgurl='" + headimgurl + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", phone='" + phone + '\'' +
                    ", realName='" + realName + '\'' +
                    '}';
        }
    }
}
