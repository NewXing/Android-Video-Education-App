package education.juxin.com.educationpro.ui.activity.home;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.bean.PreOrderBean;
import education.juxin.com.educationpro.bean.RespErrBean;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.http.LoggerInterceptor;
import education.juxin.com.educationpro.ui.activity.mine.PersonDataActivity;
import education.juxin.com.educationpro.util.FormatNumberUtil;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.bean.ProOrderParam;
import education.juxin.com.educationpro.view.RoundImageView;
import education.juxin.com.educationpro.view.ToastManager;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * 订单页面
 * The type Order activity.
 */
public class OrderActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private String[] permissionArr = {Manifest.permission.CAMERA};
    public static final int SCAN_RESULT_CODE = 0x11;
    public static final int SCAN_REQUEST_CODE = 0x12;

    private RoundImageView courseIconImg;
    private TextView courseTitleTv;
    private TextView currPriceTv;
    private TextView courseTotalNumTv;
    private TextView courseEndTimeTv;
    private EditText inviteCodeEdit;
    private TextView currPriceBottomTv;

    private String mCourseId;
    private String mIsCharges;
    private String courseCoverImgUrl;
    private String courseTitleStr;
    private String currPriceStr;
    private String courseTotalNumStr;
    private String courseEndTimeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_activty);
        initToolbar(true, R.string.order_info);

        mCourseId = getIntent().getStringExtra("course_id");
        currPriceStr = getIntent().getStringExtra("curr_price");

        mIsCharges = getIntent().getStringExtra("course_if_charges");
        courseCoverImgUrl = getIntent().getStringExtra("course_cover_img_url");
        courseTitleStr = getIntent().getStringExtra("course_title");
        courseTotalNumStr = getIntent().getStringExtra("course_total_num");
        courseEndTimeStr = getIntent().getStringExtra("course_end_time");

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateData();
    }

    private void initUI() {
        courseIconImg = findViewById(R.id.course_picture_img);
        courseTitleTv = findViewById(R.id.course_title_tv);
        currPriceTv = findViewById(R.id.current_price_tv);
        courseTotalNumTv = findViewById(R.id.course_total_num_tv);
        courseEndTimeTv = findViewById(R.id.end_time_tv);
        inviteCodeEdit = findViewById(R.id.invite_code_edit);
        currPriceBottomTv = findViewById(R.id.tv_money);

        findViewById(R.id.btn_order).setOnClickListener(this);
        findViewById(R.id.scan_btn).setOnClickListener(this);
    }

    private void updateData() {
        ImageUtils.GlideUtil(this, courseCoverImgUrl, courseIconImg);
        courseTitleTv.setText(courseTitleStr);
        currPriceTv.setText(String.format(getString(R.string.money_only_with_number), FormatNumberUtil.doubleFormat(currPriceStr)));
        courseTotalNumTv.setText(String.format(getString(R.string.with_num_course_time), courseTotalNumStr));
        courseEndTimeTv.setText(courseEndTimeStr);
        currPriceBottomTv.setText(String.format(getString(R.string.money_only_with_number), FormatNumberUtil.doubleFormat(currPriceStr)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_order:
                reqProOrder();
                break;

            case R.id.scan_btn:
                if (EasyPermissions.hasPermissions(this, permissionArr)) {
                    gotoScan();
                } else {
                    EasyPermissions.requestPermissions(this, getResources().getString(R.string.get_photo_permission_tip), 0x11, permissionArr);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        gotoScan();
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

    private void gotoScan() {
        startActivityForResult(new Intent(this, ScanActivity.class), SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == SCAN_RESULT_CODE && requestCode == SCAN_REQUEST_CODE) {
            if (data != null) {
                inviteCodeEdit.setText(data.getStringExtra("scan_invite_code"));
            }
        }
    }

    private void reqProOrder() {
        ProOrderParam param = new ProOrderParam();
        param.setCourseId(mCourseId);
        param.setPassiveInvitationCode(inviteCodeEdit.getText().toString().trim());
        param.setTotalAmount(currPriceStr);
        param.setOrderType("1");
        param.setLessonId("");
        param.setGiftId("");
        param.setGiftNum("");

        String jsonStr = new Gson().toJson(param);

        OkHttpUtils.postString()
                .url(HttpConstant.CREATE_PRE_ORDER)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonStr)
                .build()
                .execute(new HttpCallBack<PreOrderBean>(PreOrderBean.class, true, this) {
                    @Override
                    public void onResponse(PreOrderBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        Intent intent = new Intent(OrderActivity.this, PayModeActivity.class);
                        intent.putExtra("total_amount", currPriceStr);
                        intent.putExtra("order_id", response.getData().getOrderId());
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);

                        try {
                            for (Interceptor interceptor : OkHttpUtils.getInstance().getOkHttpClient().interceptors()) {
                                if (interceptor instanceof LoggerInterceptor) {
                                    RespErrBean errBean = ((LoggerInterceptor) interceptor).getCurrRespErrBean();
                                    if (errBean != null && errBean.getMessage() != null && "请先去完善个人资料信息".equals(errBean.getMessage().trim())) {
                                        startActivity(new Intent(OrderActivity.this, PersonDataActivity.class));
                                    }
                                    break;
                                }
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

}
