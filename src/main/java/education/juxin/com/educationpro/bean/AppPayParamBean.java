package education.juxin.com.educationpro.bean;

import java.util.Map;

import education.juxin.com.educationpro.base.BaseBean;

public class AppPayParamBean extends BaseBean {

    private AppPayParamData data;

    public AppPayParamData getData() {
        return data;
    }

    public void setData(AppPayParamData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AppPayParamBean{" +
                "data=" + data +
                '}';
    }

    public class AppPayParamData {

        private WechatPayResult wechatPayResult;
        private Map<String, String> aliPayResult;
        private String code;

        public Map<String, String> getAliPayResult() {
            return aliPayResult;
        }

        public void setAliPayResult(Map<String, String> aliPayResult) {
            this.aliPayResult = aliPayResult;
        }

        public WechatPayResult getWechatPayResult() {
            return wechatPayResult;
        }

        public void setWechatPayResult(WechatPayResult wechatPayResult) {
            this.wechatPayResult = wechatPayResult;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "AppPayParamData{" +
                    "wechatPayResult=" + wechatPayResult +
                    ", aliPayResult=" + aliPayResult +
                    ", code='" + code + '\'' +
                    '}';
        }

        public class WechatPayResult {

            private String appId;
            private String partnerId;
            private String prepayId;
            private String nonceStr;
            private String packageValue;
            private String sign;
            private String timeStamp;

            public String getAppId() {
                return appId;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public String getPartnerId() {
                return partnerId;
            }

            public void setPartnerId(String partnerId) {
                this.partnerId = partnerId;
            }

            public String getPrepayId() {
                return prepayId;
            }

            public void setPrepayId(String prepayId) {
                this.prepayId = prepayId;
            }

            public String getNonceStr() {
                return nonceStr;
            }

            public void setNonceStr(String nonceStr) {
                this.nonceStr = nonceStr;
            }

            public String getPackageValue() {
                return packageValue;
            }

            public void setPackageValue(String packageValue) {
                this.packageValue = packageValue;
            }

            public String getSign() {
                return sign;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public String getTimeStamp() {
                return timeStamp;
            }

            public void setTimeStamp(String timeStamp) {
                this.timeStamp = timeStamp;
            }

            @Override
            public String toString() {
                return "WechatPayResult{" +
                        "appid='" + appId + '\'' +
                        ", partnerId='" + partnerId + '\'' +
                        ", prepayId='" + prepayId + '\'' +
                        ", nonceStr='" + nonceStr + '\'' +
                        ", packageValue='" + packageValue + '\'' +
                        ", sign='" + sign + '\'' +
                        ", timestamp='" + timeStamp + '\'' +
                        '}';
            }
        }
    }
}
