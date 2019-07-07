package education.juxin.com.educationpro.ui.activity.mine;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.List;

import androidkun.com.versionupdatelibrary.entity.VersionUpdateConfig;
import cn.jpush.android.api.JPushInterface;
import education.juxin.com.educationpro.ProApplication;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.bean.NewVersionBean;
import education.juxin.com.educationpro.bean.UserSettingBean;
import education.juxin.com.educationpro.dialog.ComTwnBtnDialog;
import education.juxin.com.educationpro.download.VideoCacheManager;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.interfaces.IRefreshUI;
import education.juxin.com.educationpro.ui.activity.login.LoginActivity;
import education.juxin.com.educationpro.util.ActivityCollector;
import education.juxin.com.educationpro.util.ApkInfoUtil;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.util.StorageUtil;
import education.juxin.com.educationpro.view.ToastManager;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 设置页面
 * The type Setting activity.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private String[] permissionArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public static IRefreshUI refreshUI;

    private String currVersionCode = ""; // 获取当前项目的版本号
    private String newVersionAppUrl = "";
    private String newVersionAppNum = "";

    private Button noWifiSwitch;
    private Button sendMsgSwitch;
    private Button autoPlaySwitch;
    private Button onlyWifiSwitch;
    private TextView versionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initToolbar(true, R.string.setting);
        initView();

        versionTv.setText("V" + ApkInfoUtil.getVersionName(this));
        reqUserSettingsData();
    }

    private void initView() {
        currVersionCode = ApkInfoUtil.getVersionName(SettingActivity.this);

        versionTv = findViewById(R.id.version_tv);

        noWifiSwitch = findViewById(R.id.no_wifi_tip_switch);
        sendMsgSwitch = findViewById(R.id.send_message_switch);
        autoPlaySwitch = findViewById(R.id.auto_play_free_video_switch);
        onlyWifiSwitch = findViewById(R.id.only_wifi_load_switch);
        noWifiSwitch.setOnClickListener(this);
        sendMsgSwitch.setOnClickListener(this);
        autoPlaySwitch.setOnClickListener(this);
        onlyWifiSwitch.setOnClickListener(this);

        findViewById(R.id.feedback_tv).setOnClickListener(this);
        findViewById(R.id.about_me_tv).setOnClickListener(this);
        findViewById(R.id.account_safe_tv).setOnClickListener(this);
        findViewById(R.id.del_cache_tv).setOnClickListener(this);
        findViewById(R.id.version_update_tv).setOnClickListener(this);
        findViewById(R.id.exit_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback_tv:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;

            case R.id.about_me_tv:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;

            case R.id.account_safe_tv:
                startActivity(new Intent(this, AccountActivity.class));
                break;

            case R.id.del_cache_tv:
                ComTwnBtnDialog clearDialog = new ComTwnBtnDialog(this, ComTwnBtnDialog.DIALOG_CLEAR_CACHE);
                clearDialog.setDialogBtnClickListener(new ComTwnBtnDialog.IDialogBtnClickListener() {
                    @Override
                    public void onDialogLeftBtnClick() {
                    }

                    @Override
                    public void onDialogRightBtnClick() {
                        StorageUtil.deleteFileOrDirectory(new File(Environment.getExternalStorageDirectory().getPath() + "/_qiantu_"));
                        VideoCacheManager.deleteAllVideoInfo(SettingActivity.this);
                        if (refreshUI != null) {
                            refreshUI.onRefreshUI();
                        }
                    }
                });
                clearDialog.show();
                break;

            case R.id.version_update_tv:
                ComTwnBtnDialog updateDialog = new ComTwnBtnDialog(this, ComTwnBtnDialog.DIALOG_VERSION_UPDATE);
                updateDialog.setDialogBtnClickListener(new ComTwnBtnDialog.IDialogBtnClickListener() {
                    @Override
                    public void onDialogLeftBtnClick() {
                        updateDialog.dismiss();
                    }

                    @Override
                    public void onDialogRightBtnClick() {
                        getUpdateVersion();
                    }
                });
                updateDialog.show();
                break;

            case R.id.exit_login:
                ComTwnBtnDialog exitLoginDialog = new ComTwnBtnDialog(this, ComTwnBtnDialog.DIALOG_EXIT_LOGIN);
                exitLoginDialog.setDialogBtnClickListener(new ComTwnBtnDialog.IDialogBtnClickListener() {
                    @Override
                    public void onDialogLeftBtnClick() {
                    }

                    @Override
                    public void onDialogRightBtnClick() {
                        reqLogoutData();
                    }
                });
                exitLoginDialog.show();
                break;

            case R.id.no_wifi_tip_switch:
                setButtonState(noWifiSwitch, "noWifi", !((boolean) noWifiSwitch.getTag()));
                updateStateBtn();
                break;

            case R.id.send_message_switch:
                setButtonState(sendMsgSwitch, "sendMsg", !((boolean) sendMsgSwitch.getTag()));
                if ("1".equals(SPHelper.getSimpleParam(this, "sendMsg", "0"))) {
                    JPushInterface.resumePush(ProApplication.mApplicationContext);
                } else {
                    JPushInterface.stopPush(ProApplication.mApplicationContext);
                }
                updateStateBtn();
                break;

            case R.id.auto_play_free_video_switch:
                setButtonState(autoPlaySwitch, "autoPlay", !((boolean) autoPlaySwitch.getTag()));
                updateStateBtn();
                break;

            case R.id.only_wifi_load_switch:
                setButtonState(onlyWifiSwitch, "onlyWifiLoad", !((boolean) onlyWifiSwitch.getTag()));
                updateStateBtn();
                break;

            default:
                break;
        }
    }

    private void checkPer() {
        if (EasyPermissions.hasPermissions(this, permissionArr)) {
            gotoDownload(newVersionAppUrl, newVersionAppNum);
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.get_photo_permission_tip), 0x11, permissionArr);
        }
    }

    private void gotoDownload(String downUrls, String versionNum) {
        String downPath = Environment.getExternalStorageDirectory().getPath() + "/new_package.apk";

        ToastManager.showShortToast("开始下载...");

        VersionUpdateConfig.getInstance()
                .setContext(SettingActivity.this)
                .setDownLoadURL(downUrls)
                .setNewVersion(versionNum)
                .setFileSavePath(downPath)
                .setNotificationIconRes(R.mipmap.ic_launcher)
                .setNotificationSmallIconRes(R.mipmap.ic_launcher)
                .setNotificationTitle("乾途版本升级")
                .startDownLoad();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        gotoDownload(newVersionAppUrl, newVersionAppNum);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // 请求权限被拒绝
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).setRationale(R.string.get_permission_tip).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //请求更新版本的apk
    private void getUpdateVersion() {
        OkHttpUtils.get()
                .url(HttpConstant.GET_NEW_VERSION)
                .addParams("version", currVersionCode)
                .build()
                .execute(new HttpCallBack<NewVersionBean>(NewVersionBean.class, false, SettingActivity.this) {
                    @Override
                    public void onResponse(NewVersionBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData() == null) {
                            ToastManager.showShortToast("已是最新版本！");
                        } else {
                            newVersionAppUrl = response.getData().getDowUrl();
                            newVersionAppNum = response.getData().getVersionNum();
                            checkPer();
                        }
                    }
                });
    }

    // 请求开关控制器状态接口
    private void reqUserSettingsData() {
        OkHttpUtils.get()
                .url(HttpConstant.USER_SETTINGS)
                .build()
                .execute(new HttpCallBack<UserSettingBean>(UserSettingBean.class, false, this) {
                    @Override
                    public void onResponse(UserSettingBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        setButtonState(noWifiSwitch, "noWifi", response.getData().isWhetherOpen4g());
                        setButtonState(sendMsgSwitch, "sendMsg", response.getData().isWhetherOpenMessagePush());
                        setButtonState(autoPlaySwitch, "autoPlay", response.getData().isWhetherOpenVideoPlay());
                        setButtonState(onlyWifiSwitch, "onlyWifiLoad", response.getData().isWhetherOpenWifi());

                        if ("1".equals(SPHelper.getSimpleParam(SettingActivity.this, "sendMsg", "0"))) {
                            JPushInterface.resumePush(ProApplication.mApplicationContext);
                        } else {
                            JPushInterface.stopPush(ProApplication.mApplicationContext);
                        }
                    }
                });
    }

    private void updateStateBtn() {
        String onlyWifiLoad = String.valueOf(SPHelper.getSimpleParam(this, "onlyWifiLoad", "0"));
        String noWifi = String.valueOf(SPHelper.getSimpleParam(this, "noWifi", "0"));
        String sendMsg = String.valueOf(SPHelper.getSimpleParam(this, "sendMsg", "0"));
        String autoPlay = String.valueOf(SPHelper.getSimpleParam(this, "autoPlay", "0"));

        OkHttpUtils.get()
                .url(HttpConstant.UPDATE_STATE_BTN)
                .addParams("whetherOpenWifi", onlyWifiLoad)
                .addParams("whetherOpen4g", noWifi)
                .addParams("whetherOpenMessagePush", sendMsg)
                .addParams("whetherOpenVideoPlay", autoPlay)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, false, getApplicationContext()) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }
                    }
                });
    }

    private void setButtonState(Button button, String state, boolean isChecked) {
        button.setTag(isChecked);

        SPHelper.setSimpleKeyValue(this, state, isChecked ? "1" : "0");

        if ((boolean) button.getTag()) {
            button.setBackgroundResource(R.drawable.switch_open);
        } else {
            button.setBackgroundResource(R.drawable.switch_close);
        }
    }

    // 请求退出登录接口
    private void reqLogoutData() {
        OkHttpUtils.post()
                .url(HttpConstant.USER_EXIT_LOGIN)
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        SPHelper.setSimpleKeyValue(SettingActivity.this, "token", "");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "phone", "");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "password", "");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "pay_success_flag", "0");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "pay_success_invite_code", "");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "openId", "");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "nickName", "");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "headImgUrl", "");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "unionId", "");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "onlyWifiLoad", "1");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "noWifi", "1");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "sendMsg", "1");
                        SPHelper.setSimpleKeyValue(SettingActivity.this, "autoPlay", "1");

                        MobclickAgent.onProfileSignOff(); // 友盟统计：用户退出统计

                        ActivityCollector.finishAll();
                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                    }
                });
    }
}
