package education.juxin.com.educationpro.ui.activity.mine;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.adapter.fragment.DepositTabAdapter;
import education.juxin.com.educationpro.adapter.listview.PriceGridAdapter;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.bean.ExtractionPriceBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.ui.fragment.AliPayFragment;
import education.juxin.com.educationpro.ui.fragment.WeChatFragment;
import education.juxin.com.educationpro.view.ToastManager;

/**
 * 申请提现页面
 * The type Deposit activity.
 */
public class DepositActivity extends BaseActivity implements View.OnClickListener {

    private AliPayFragment aliPayFragment;
    private WeChatFragment wxChatFragment;
    private DepositTabAdapter tabAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        initToolbar(true, R.string.apply_cash);
        initUI();
    }

    private void initUI() {
        // TabLayout & Fragments
        TabLayout tabLayout = findViewById(R.id.tab_title);
        viewPager = findViewById(R.id.content_viewpager);

        aliPayFragment = AliPayFragment.newInstance("apFragment");
        wxChatFragment = WeChatFragment.newInstance("wcFragment");

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(aliPayFragment);
        fragmentList.add(wxChatFragment);

        tabAdapter = new DepositTabAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(tabAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        tabLayout.setupWithViewPager(viewPager);

        findViewById(R.id.sure_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_btn:
                reqWithdrawCash();
                break;

            default:
                break;
        }
    }

    private void reqWithdrawCash() {
        int type = 1;
        String price = "";
        String extractionMoneyId = "";
        String aliPayOrWeChatId = "";

        PriceGridAdapter payAdapter;
        ExtractionPriceBean.ExtractionPriceData selectedData;

        if (viewPager.getCurrentItem() == 0) {
            payAdapter = aliPayFragment.getPriceGridAdapter();
            if (payAdapter == null) {
                return;
            }
            selectedData = payAdapter.getCurSelectedItem();
            if (selectedData == null) {
                ToastManager.showShortToast("请选择金额");
                return;
            }
            type = 1;
            price = selectedData.getPrice();
            extractionMoneyId = selectedData.getId();
            aliPayOrWeChatId = aliPayFragment.getAliPayIdStr();
            if (aliPayOrWeChatId == null || aliPayOrWeChatId.trim().isEmpty()) {
                ToastManager.showShortToast("请先绑定支付宝");
                return;
            }
        } else if (viewPager.getCurrentItem() == 1) {
            payAdapter = wxChatFragment.getPriceGridAdapter();
            if (payAdapter == null) {
                return;
            }
            selectedData = payAdapter.getCurSelectedItem();
            if (selectedData == null) {
                ToastManager.showShortToast("请选择金额");
                return;
            }
            type = 2;
            price = selectedData.getPrice();
            extractionMoneyId = selectedData.getId();
            aliPayOrWeChatId = wxChatFragment.getWxPayIdStr();
            if (aliPayOrWeChatId == null || aliPayOrWeChatId.trim().isEmpty()) {
                ToastManager.showShortToast("请先绑定微信");
                return;
            }
        }

        OkHttpUtils.post()
                .url(HttpConstant.START_WITHDRAW_CASH)
                .addParams("type", String.valueOf(type))
                .addParams("price", price)
                .addParams("extractionMoneyId", extractionMoneyId)
                .addParams("aliPayOrWechatId", aliPayOrWeChatId)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        ToastManager.showShortToast("提现成功");
                        finish();
                    }
                });
    }
}
