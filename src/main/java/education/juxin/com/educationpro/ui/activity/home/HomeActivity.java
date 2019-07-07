package education.juxin.com.educationpro.ui.activity.home;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;

import education.juxin.com.educationpro.interfaces.IMessageCount;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.ui.fragment.fragmentNavigator.FragmentNavigator;
import education.juxin.com.educationpro.ui.fragment.fragmentNavigator.HomeNavigatorAdapter;
import education.juxin.com.educationpro.util.ActivityCollector;
import education.juxin.com.educationpro.view.RedPointView;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 首页 页面
 * <p>
 * The type Home activity.
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener, IMessageCount {

    public static IMessageCount messageCount;
    // 极光推送相关
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";

    private long exitTime = 0;

    private FragmentNavigator navigator;
    private RedPointView redPointView;
    private ArrayList<RadioButton> naviRadioBtnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        messageCount = this;
        initUI();

        //startService(new Intent(this, ClipboardService.class));
    }

    private void initUI() {
        RadioButton homeRadioBtn = findViewById(R.id.rb_recreation);
        RadioButton dynamicRadioBtn = findViewById(R.id.rb_dynamic);
        RadioButton courseRadioBtn = findViewById(R.id.rb_chat);
        RadioButton mimeRadioBtn = findViewById(R.id.rb_mine);

        homeRadioBtn.setChecked(true);

        homeRadioBtn.setOnClickListener(this);
        dynamicRadioBtn.setOnClickListener(this);
        courseRadioBtn.setOnClickListener(this);
        mimeRadioBtn.setOnClickListener(this);

        naviRadioBtnList = new ArrayList<>();
        naviRadioBtnList.add(homeRadioBtn);
        naviRadioBtnList.add(dynamicRadioBtn);
        naviRadioBtnList.add(courseRadioBtn);
        naviRadioBtnList.add(mimeRadioBtn);

        navigator = new FragmentNavigator(getSupportFragmentManager(), new HomeNavigatorAdapter(), R.id.container);
        navigator.setDefaultPosition(0);
        navigator.showFragment(0);

        ViewGroup bottomBar = findViewById(R.id.bottom);
        redPointView = new RedPointView(this);
        redPointView.setTargetView(bottomBar);
        redPointView.setClickable(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_recreation:
                navigator.showFragment(0);
                break;

            case R.id.rb_dynamic:
                navigator.showFragment(1);
                break;

            case R.id.rb_chat:
                navigator.showFragment(2);
                break;

            case R.id.rb_mine:
                navigator.showFragment(3);
                break;

            default:
                navigator.showFragment(0);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastManager.showShortToast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            ActivityCollector.finishAll();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public void onMessageCountChange(int messageCount) {
        if (redPointView != null) {
            if (10 > messageCount) {
                redPointView.setRedPointMargin(0, 1, 25, 0);
            } else {
                redPointView.setRedPointMargin(0, 1, 20, 0);
            }
            redPointView.setRedPointCount(messageCount);
        }
    }

}
