package education.juxin.com.educationpro.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.LayoutBar;
import com.shizhefei.view.indicator.slidebar.ScrollBar;

import java.util.ArrayList;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.ui.activity.login.LoginActivity;
import education.juxin.com.educationpro.util.ImageUtils;

/**
 * 第一次打开APP的欢迎页
 */

public class WelcomeActivity extends BaseActivity {

    private Button entryBtn;

    private int[] mImgUrlList = new int[]{R.drawable.welcome_page_img_1, R.drawable.welcome_page_img_2, R.drawable.welcome_page_img_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initUI();
    }

    private void initUI() {
        // Entry Button
        entryBtn = findViewById(R.id.entry_btn);
        findViewById(R.id.entry_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                finish();
            }
        });

        // Banner
        ViewPager viewPager = findViewById(R.id.content_viewpager);
        FixedIndicatorView indicatorView = findViewById(R.id.indicator_view);
        indicatorView.setScrollBar(new LayoutBar(this, R.layout.view_indicator_checked, ScrollBar.Gravity.CENTENT));
        indicatorView.setVisibility(View.GONE);
        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(indicatorView, viewPager);
        indicatorViewPager.setAdapter(new IndicatorViewPager.IndicatorViewPagerAdapter() {
            @Override
            public int getCount() {
                return mImgUrlList.length;
            }

            @Override
            public View getViewForTab(int position, View convertView, ViewGroup container) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.view_indicator_uncheck, container, false);
                }
                return convertView;
            }

            @Override
            public View getViewForPage(int position, View convertView, ViewGroup container) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.item_welcome_viewpager, container, false);
                }
                ImageView imageView = convertView.findViewById(R.id.show_image);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                try {
                    Glide.with(WelcomeActivity.this).load(mImgUrlList[position]).into(imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return convertView;
            }
        });
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                if (currentItem == mImgUrlList.length - 1) {
                    entryBtn.setVisibility(View.VISIBLE);
                } else {
                    entryBtn.setVisibility(View.GONE);
                }
            }
        });
    }
}
