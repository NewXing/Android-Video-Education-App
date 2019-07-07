package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class AddPersonalBean extends BaseBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AddPersonalBean{" +
                "data=" + data +
                '}';
    }

    public class DataBean {

        private String phone;
        private String headImgUrl;
        private String qiniuDomain;
        private String nickname;
        private String sex;
        private String email;
        private String balance;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        public String getQiniuDomain() {
            return qiniuDomain;
        }

        public void setQiniuDomain(String qiniuDomain) {
            this.qiniuDomain = qiniuDomain;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "phone='" + phone + '\'' +
                    ", headImgUrl='" + headImgUrl + '\'' +
                    ", qiniuDomain='" + qiniuDomain + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", sex='" + sex + '\'' +
                    ", email='" + email + '\'' +
                    ", balance='" + balance + '\'' +
                    '}';
        }
    }
}
