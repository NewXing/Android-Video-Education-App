package education.juxin.com.educationpro.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import education.juxin.com.educationpro.bean.GetMessageCountBean;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseFragment;
import education.juxin.com.educationpro.bean.AddPersonalBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.http.NetworkHelper;
import education.juxin.com.educationpro.ui.activity.home.HomeActivity;
import education.juxin.com.educationpro.ui.activity.mine.CollectActivity;
import education.juxin.com.educationpro.ui.activity.mine.ConcernActivity;
import education.juxin.com.educationpro.ui.activity.mine.BuyJiLuActivity;
import education.juxin.com.educationpro.ui.activity.mine.MessageNotifyActivity;
import education.juxin.com.educationpro.ui.activity.mine.MyRewardActivity;
import education.juxin.com.educationpro.ui.activity.mine.MyWalletActivity;
import education.juxin.com.educationpro.ui.activity.mine.PersonDataActivity;
import education.juxin.com.educationpro.ui.activity.mine.SettingActivity;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.util.SPHelper;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.CircleImageView;
import education.juxin.com.educationpro.view.RedPointView;
import education.juxin.com.educationpro.view.ToastManager;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * tab 我的碎片 页面
 * The type Mime fragment.
 */
public class MimeFragment extends BaseFragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private String[] permissionArr = {Manifest.permission.CALL_PHONE};

    private View mView;
    private TextView nickNameTv;
    private CircleImageView userHeaderImg;
    private RedPointView redPointView;
    private RelativeLayout headerLayout;
    private ScrollView rootScroll;
    private LinearLayout noNetLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab_mime, container, false);
        initUI();
        return mView;
    }

    private void initUI() {
        headerLayout = mView.findViewById(R.id.mime_fragment_layout);
        rootScroll = mView.findViewById(R.id.mime_fragment_scroll);
        noNetLayout = mView.findViewById(R.id.error_relative);
        noNetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqUserData();
                String tokenStr = (String) SPHelper.getSimpleParam(getContext(), "token", "");
                if (tokenStr != null && !tokenStr.isEmpty()) {
                    getMessageCountData();
                }
            }
        });

        userHeaderImg = mView.findViewById(R.id.user_head_circle_img);
        userHeaderImg.setOnClickListener(this);

        nickNameTv = mView.findViewById(R.id.user_name_tv);
        nickNameTv.setOnClickListener(this);

        mView.findViewById(R.id.buy_notes_tv).setOnClickListener(this);
        mView.findViewById(R.id.my_collect_tv).setOnClickListener(this);
        mView.findViewById(R.id.my_concern_tv).setOnClickListener(this);
        mView.findViewById(R.id.my_wallet_tv).setOnClickListener(this);
        mView.findViewById(R.id.my_reward_tv).setOnClickListener(this);
        mView.findViewById(R.id.setting_tv).setOnClickListener(this);
        mView.findViewById(R.id.phone_number_tv).setOnClickListener(this);

        ImageButton messageBtn = mView.findViewById(R.id.message_btn);
        messageBtn.setOnClickListener(this);
        redPointView = new RedPointView(getActivity());
        redPointView.setTargetView(messageBtn);
    }

    @Override
    public void onResume() {
        super.onResume();

        reqUserData();
        String tokenStr = (String) SPHelper.getSimpleParam(getContext(), "token", "");
        if (tokenStr != null && !tokenStr.isEmpty()) {
            getMessageCountData();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            reqUserData();
            String tokenStr = (String) SPHelper.getSimpleParam(getContext(), "token", "");
            if (tokenStr != null && !tokenStr.isEmpty()) {
                getMessageCountData();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_circle_img:
            case R.id.user_name_tv:
                startActivity(new Intent(getActivity(), PersonDataActivity.class));
                break;

            case R.id.message_btn:
                startActivity(new Intent(getActivity(), MessageNotifyActivity.class));
                break;

            case R.id.buy_notes_tv:
                startActivity(new Intent(getActivity(), BuyJiLuActivity.class));
                break;

            case R.id.my_collect_tv:
                startActivity(new Intent(getActivity(), CollectActivity.class));
                break;

            case R.id.my_concern_tv:
                startActivity(new Intent(getActivity(), ConcernActivity.class));
                break;

            case R.id.my_wallet_tv:
                startActivity(new Intent(getActivity(), MyWalletActivity.class));
                break;

            case R.id.my_reward_tv:
                startActivity(new Intent(getActivity(), MyRewardActivity.class));
                break;

            case R.id.setting_tv:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;

            case R.id.phone_number_tv:
                checkPer();
                break;
            default:
                break;
        }
    }

    private void reqUserData() {
        OkHttpUtils.get()
                .url(HttpConstant.GET_USER_PERSONAL_DATA)
                .build()
                .execute(new HttpCallBack<AddPersonalBean>(AddPersonalBean.class, true, getActivity()) {
                    @Override
                    public void onResponse(AddPersonalBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        String domainStr;
                        if (response.getData().getQiniuDomain() != null && !response.getData().getQiniuDomain().trim().isEmpty()) {
                            domainStr = response.getData().getQiniuDomain();
                        } else {
                            domainStr = "";
                        }

                        String headerImgUrlStr;
                        if (response.getData().getHeadImgUrl() != null && !response.getData().getHeadImgUrl().trim().isEmpty()) {
                            headerImgUrlStr = response.getData().getHeadImgUrl();
                        } else {
                            headerImgUrlStr = "";
                        }
                        updateData(domainStr + headerImgUrlStr, response.getData().getNickname());
                    }
                });
    }

    private void updateData(String headerUrlStr, String nickNameStr) {
        ImageUtils.GlideUtil(getActivity(), headerUrlStr, userHeaderImg);

        if (nickNameStr == null || nickNameStr.isEmpty()) {
            nickNameTv.setText("尚未设置昵称");
            nickNameTv.setTextColor(getResources().getColor(R.color.red));
        } else {
            nickNameTv.setText(nickNameStr);
            nickNameTv.setTextColor(getResources().getColor(R.color.black));
        }
    }

    //获取消息个数
    private void getMessageCountData() {
        OkHttpUtils.get()
                .url(HttpConstant.GET_MESSAGE_COUNT)
                .build()
                .execute(new HttpCallBack<GetMessageCountBean>(GetMessageCountBean.class, false, getActivity()) {
                    @Override
                    public void onResponse(GetMessageCountBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        int num = 0;
                        if (StringUtils.isNumeric(response.getData())) {
                            num = Integer.parseInt(response.getData());
                        }

                        redPointView.setRedPointCount(num);
                        if (HomeActivity.messageCount != null) {
                            HomeActivity.messageCount.onMessageCountChange(num);
                        }
                    }
                });
    }

    private void checkPer() {
        if (EasyPermissions.hasPermissions(getActivity(), permissionArr)) {
            call("024-66701493");
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.get_photo_permission_tip), 0x11, permissionArr);
        }
    }

    //调拨打电话功能
    private void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        call("024-66701493");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).setRationale(R.string.get_permission_tip).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);

        if (netMobile == NetworkHelper.NETWORK_NONE) {
            noNetLayout.setVisibility(View.VISIBLE);
            headerLayout.setVisibility(View.GONE);
            rootScroll.setVisibility(View.GONE);
        } else {
            noNetLayout.setVisibility(View.GONE);
            headerLayout.setVisibility(View.VISIBLE);
            rootScroll.setVisibility(View.VISIBLE);
        }
    }
}
