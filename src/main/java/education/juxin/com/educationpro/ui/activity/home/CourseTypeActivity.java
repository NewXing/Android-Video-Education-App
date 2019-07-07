package education.juxin.com.educationpro.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.adapter.fragment.CourseTypeTabAdapter;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.ui.fragment.ClassifyCourseFragment;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 课程类型
 * <p>
 * Created on 2018/3/19.
 */
public class CourseTypeActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<String> allTypeArr;
    private ArrayList<String> allTypeNameArr;
    private int currTypeId;

    private EditText etView;
    private HorizontalScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_type);

        allTypeArr = getIntent().getStringArrayListExtra("all_type_arr");
        allTypeNameArr = getIntent().getStringArrayListExtra("all_type_name_arr");
        currTypeId = getIntent().getIntExtra("curr_type_id", 0);

        if (allTypeArr != null && allTypeNameArr != null
                && allTypeArr.size() > 0 && allTypeNameArr.size() > 0
                && allTypeArr.size() == allTypeNameArr.size()) {
            initUI();
        }
    }

    private void initUI() {
        // Title
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.search_btn).setOnClickListener(this);

        etView = findViewById(R.id.search_edit);
        etView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    if (etView.getText().toString().isEmpty()) {
                        ToastManager.showShortToast("请输入搜索内容！");
                        return false;
                    }
                    String text = etView.getText().toString();
                    Intent intent = new Intent(CourseTypeActivity.this, CourseTypeResultActivity.class);
                    intent.putExtra("search_content", text);
                    startActivity(intent);
                }
                return false;
            }
        });

        // TabLayout & Fragments
        TabLayout tabLayout = findViewById(R.id.tab_title);
        ViewPager viewPager = findViewById(R.id.content_viewpager);

        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < allTypeArr.size(); ++i) {
            ClassifyCourseFragment fragment = ClassifyCourseFragment.newInstance("page_" + i, allTypeArr.get(i));
            fragmentList.add(fragment);
        }

        CourseTypeTabAdapter courseTypeTabAdapter = new CourseTypeTabAdapter(getSupportFragmentManager(), fragmentList);
        courseTypeTabAdapter.setTabTitle(allTypeNameArr);
        viewPager.setAdapter(courseTypeTabAdapter);
        viewPager.setCurrentItem(currTypeId);
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;

            case R.id.search_btn:
                if (etView.getText().toString().isEmpty()) {
                    ToastManager.showShortToast("请输入搜索内容！");
                    return;
                }
                String text = etView.getText().toString();
                Intent intent = new Intent(this, CourseTypeResultActivity.class);
                intent.putExtra("search_content", text);
                startActivity(intent);
                break;

            case R.id.search_edit:
                break;

            default:
                break;
        }
    }

}
