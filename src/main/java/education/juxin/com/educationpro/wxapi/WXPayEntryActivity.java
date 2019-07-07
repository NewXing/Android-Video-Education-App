package education.juxin.com.educationpro.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import education.juxin.com.educationpro.ui.activity.home.OrderActivity;
import education.juxin.com.educationpro.ui.activity.home.PayModeActivity;
import education.juxin.com.educationpro.util.ActivityCollector;
import education.juxin.com.educationpro.util.LogManager;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.view.ToastManager;

public class WXPayEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WxApiUtils.getApi();
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        LogManager.e("Pay_onResp_errCode=" + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            ActivityCollector.finishActivity(PayModeActivity.class);
            ActivityCollector.finishActivity(OrderActivity.class);

            switch (resp.errCode) {
                case 0:
                    ToastManager.showShortToast("支付成功");
                    SPHelper.setSimpleKeyValue(this, "pay_success_flag", "1");
                    break;

                case -2:
                    ToastManager.showShortToast("支付取消");
                    SPHelper.setSimpleKeyValue(this, "pay_success_flag", "0");
                    break;
            }
        }

        finish();
    }
}
