package education.juxin.com.educationpro.party3.ijkplayer.interfaces;

import android.widget.ImageView;

public interface OnShowThumbnailListener {

    /**
     * 回传封面的view，让用户自主设置
     */
    void onShowThumbnail(ImageView ivThumbnail);
}