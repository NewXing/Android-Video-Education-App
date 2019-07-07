package education.juxin.com.educationpro.bean;

import education.juxin.com.educationpro.base.BaseBean;

public class UserBalanceBean extends BaseBean {

    private UserBalanceData data;

    public UserBalanceData getData() {
        return data;
    }

    public void setData(UserBalanceData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserBalanceBean{" +
                "data=" + data +
                '}';
    }

    public class UserBalanceData {

        private String balance;

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        @Override
        public String toString() {
            return "UserBalanceData{" +
                    "balance='" + balance + '\'' +
                    '}';
        }
    }
}
