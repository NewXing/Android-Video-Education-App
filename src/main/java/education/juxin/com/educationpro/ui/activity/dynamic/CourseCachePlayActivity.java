package education.juxin.com.educationpro.ui.activity.dynamic;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.party3.ijkplayer.player.PlayerView;
import education.juxin.com.educationpro.party3.ijkplayer.utils.PlayStateParams;
import education.juxin.com.educationpro.util.ImageUtils;

public class CourseCachePlayActivity extends BaseActivity implements View.OnClickListener {

    private PowerManager.WakeLock wakeLock;

    private PlayerView mIjkPlayer;

    private ImageView coverImg;
    private ImageView playEndImg;

    private String mVideoPath = "";
    private String mCoverUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_cache_play);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "VideoPlayLock");
        }

        String locationVideoPath = getIntent().getStringExtra("location_video_path");
        if (locationVideoPath != null && !locationVideoPath.isEmpty()) {
            mVideoPath = locationVideoPath;
        }
        String coverUrl = getIntent().getStringExtra("course_cover_url");
        if (coverUrl != null && !coverUrl.isEmpty()) {
            mCoverUrl = coverUrl;
        }

        initUI();
        initVideo();
    }

    private void initUI() {
        findViewById(R.id.all_screen_back_btn).setOnClickListener(this);
    }

    private void initVideo() {
        mIjkPlayer = new PlayerView(this, null);
        mIjkPlayer.setScaleType(PlayStateParams.FIT_PARENT);
        mIjkPlayer.hideHideTopBar(true);
        mIjkPlayer.setForbidDoubleUp(true);
        mIjkPlayer.setPlaySource("标清", mVideoPath);
        mIjkPlayer.startPlay();

        coverImg = findViewById(R.id.cover_view);
        ImageUtils.GlideUtil(this, mCoverUrl, coverImg);
        playEndImg = findViewById(R.id.play_end_img);
        coverImg.setVisibility(View.GONE);
        playEndImg.setVisibility(View.GONE);
        playEndImg.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mIjkPlayer != null) {
            mIjkPlayer.onPause();
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mIjkPlayer != null) {
            mIjkPlayer.onResume();
        }

        if (wakeLock != null && mIjkPlayer != null && mIjkPlayer.isPlaying()) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mIjkPlayer != null) {
            mIjkPlayer.onDestroy();
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_screen_back_btn:
                finish();
                break;

            case R.id.play_end_img:
                mIjkPlayer.setPlaySource("标清", mVideoPath);
                mIjkPlayer.startPlay();

                coverImg.setVisibility(View.GONE);
                playEndImg.setVisibility(View.GONE);
                break;
        }
    }

}
