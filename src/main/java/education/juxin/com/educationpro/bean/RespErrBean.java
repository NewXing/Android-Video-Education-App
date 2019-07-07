package education.juxin.com.educationpro.bean;

public class RespErrBean {

    private String timestamp;
    private String status;
    private String error;
    private String exception;
    private String message;
    private String path;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "RespErrBean{" +
                "timestamp='" + timestamp + '\'' +
                ", status='" + status + '\'' +
                ", error='" + error + '\'' +
                ", exception='" + exception + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
