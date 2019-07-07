package education.juxin.com.educationpro.party3.ijkplayer.utils;

public class VideoIjkBean {

    private String stream;  // 分辨率名称
    private String url;     // 分辨率对应视频地址
    private boolean select; // 当前选中项

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        VideoIjkBean that = (VideoIjkBean) o;

        return (stream != null ? stream.equals(that.stream) : that.stream == null)
                && (url != null ? url.equals(that.url) : that.url == null);
    }
}
