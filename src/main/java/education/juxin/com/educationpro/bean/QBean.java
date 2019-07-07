package education.juxin.com.educationpro.bean;

public class QBean {
    /**
     * "hash" -> "Fn_1hZMjn7Glp2WDq-j0Q7w6bt-Y"
     * "key" -> "Fn_1hZMjn7Glp2WDq-j0Q7w6bt-Y"
     */
    private String hash;
    private String key;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "QBean{" +
                "hash='" + hash + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
