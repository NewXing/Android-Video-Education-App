package education.juxin.com.educationpro.ui.activity.mine;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import education.juxin.com.educationpro.ProApplication;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.base.BaseBean;
import education.juxin.com.educationpro.bean.AddPersonalBean;
import education.juxin.com.educationpro.bean.QBean;
import education.juxin.com.educationpro.bean.QiniuToken;
import education.juxin.com.educationpro.dialog.ComOneBtnDialog;
import education.juxin.com.educationpro.http.HttpCallBack;
import education.juxin.com.educationpro.http.HttpConstant;
import education.juxin.com.educationpro.util.ImageUtils;
import education.juxin.com.educationpro.util.StorageUtil;
import education.juxin.com.educationpro.util.StringUtils;
import education.juxin.com.educationpro.view.CircleImageView;
import education.juxin.com.educationpro.dialog.HubDialog;
import education.juxin.com.educationpro.view.ToastManager;
import me.iwf.photopicker.PhotoPicker;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 个人资料页面
 * The type Person data activity.
 */
public class PersonDataActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private String[] permissionArr = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private static final int PORTRAIT_CROP_CODE = 6;

    private File outputFile;
    private String qiniuDomainStr = "";
    private String oldHeadUrlStr = "";

    private CircleImageView headImageView;
    private EditText nicknameEdit;
    private EditText emailEdit;
    private TextView sexTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_data);
        initToolbar(true, R.string.person_info);
        reqUserData();
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initUI() {
        findViewById(R.id.rl_sex).setOnClickListener(this);
        findViewById(R.id.save_btn).setOnClickListener(this);

        headImageView = findViewById(R.id.cim_img_view);
        headImageView.setOnClickListener(this);

        nicknameEdit = findViewById(R.id.et_view);
        emailEdit = findViewById(R.id.et_email);
        sexTv = findViewById(R.id.tv_sex);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_sex:
                ComOneBtnDialog.sexTypeStr = sexTv.getText().toString().trim();
                ComOneBtnDialog sexDialog = new ComOneBtnDialog(this, ComOneBtnDialog.DIALOG_SEX_SELECT);
                sexDialog.setDialogSureSelectListener(new ComOneBtnDialog.IDialogSureSelectListener() {
                    @Override
                    public void onDialogSureSelect(int selected) {
                        switch (selected) {
                            case R.id.man_radio_btn:
                                sexTv.setText("男");
                                break;

                            case R.id.woman_radio_btn:
                                sexTv.setText("女");
                                break;

                            default:
                                break;
                        }
                    }
                });
                sexDialog.show();
                break;

            case R.id.save_btn:
                reqModifyUserData();
                break;

            case R.id.cim_img_view:
                checkPer();
                break;

            default:
                break;
        }
    }

    private void checkPer() {
        if (EasyPermissions.hasPermissions(this, permissionArr)) {
            gotoPhoto();
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.get_photo_permission_tip), 0x11, permissionArr);
        }
    }

    private void gotoPhoto() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                List<String> items = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                if (items != null && items.size() > 0 && items.get(0) != null) {
                    outputFile = StorageUtil.getOutputFile();
                    ImageUtils.startCrop(this, new File(items.get(0)), outputFile, PORTRAIT_CROP_CODE);
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == PORTRAIT_CROP_CODE) {
            compressByLuban(outputFile.getAbsolutePath());
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // 请求权限已允许
        gotoPhoto();
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

    private void updateData(AddPersonalBean.DataBean data) {
        String headerImgUrlStr;
        if (data.getHeadImgUrl() != null && !data.getHeadImgUrl().trim().isEmpty()) {
            headerImgUrlStr = data.getHeadImgUrl();
        } else {
            headerImgUrlStr = "";
        }

        String headerUrlStr = qiniuDomainStr + headerImgUrlStr;
        String nicknameStr = data.getNickname();
        String emailStr = data.getEmail();
        String sexStr = data.getSex();

        ImageUtils.GlideUtil(this, headerUrlStr, headImageView);
        nicknameEdit.setText(nicknameStr == null ? "" : nicknameStr);
        emailEdit.setText(emailStr == null ? "" : emailStr);
        sexTv.setText(sexStr == null ? "" : (sexStr.equals("m") ? "男" : "女"));
    }

    // 图片压缩
    private void compressByLuban(final String path) {
        final File file = new File(path);
        Luban.get(this)
                .load(file)
                .putGear(Luban.THIRD_GEAR)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        qiniuUpload(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastManager.showShortToast("图片压缩失败");
                    }
                }).launch();
    }

    // 七牛图片上传接口
    private void qiniuUpload(final File file) {
        OkHttpUtils.get()
                .url(HttpConstant.GET_QINIU_TOKEN)
                .build()
                .execute(new HttpCallBack<QiniuToken>(QiniuToken.class, true, this) {
                    @Override
                    public void onResponse(final QiniuToken response, int id) {
                        if (response.getUptoken() == null || response.getUptoken().isEmpty()) {
                            ToastManager.showShortToast("获取图片上传Token失败");
                            return;
                        }

                        HubDialog.getInstance().show(PersonDataActivity.this);

                        ProApplication.getUploadManager().put(file, null, response.getUptoken(), new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject res) {
                                HubDialog.getInstance().dismiss();

                                if (info.isOK()) {
                                    ToastManager.showShortToast("头像上传成功");
                                    QBean bean = new Gson().fromJson(res.toString(), QBean.class);
                                    String path = bean.getKey();
                                    headImageView.setTag(R.id.imageId, path);

                                    try {
                                        Glide.with(PersonDataActivity.this).load(outputFile).into(headImageView);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //ImageUtils.GlideUtil(PersonDataActivity.this, outputFile, headImageView);
                                    //reqUserImageData();
                                } else {
                                    ToastManager.showShortToast("头像上传失败");
                                }
                            }
                        }, null);
                    }
                });
    }

    // 获取用户资料信息
    private void reqUserData() {
        OkHttpUtils.get()
                .url(HttpConstant.GET_USER_PERSONAL_DATA)
                .build()
                .execute(new HttpCallBack<AddPersonalBean>(AddPersonalBean.class, true, this) {
                    @Override
                    public void onResponse(AddPersonalBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        if (response.getData().getQiniuDomain() != null) {
                            qiniuDomainStr = response.getData().getQiniuDomain();
                        } else {
                            qiniuDomainStr = "";
                        }
                        oldHeadUrlStr = response.getData().getHeadImgUrl();
                        updateData(response.getData());
                    }
                });
    }

    private void reqUserImageData() {
        OkHttpUtils.post()
                .url(HttpConstant.UPDATE_USER_PERSONAL_DATA)
                .addParams("headImgUrl", (String) headImageView.getTag(R.id.imageId))
                .addParams("nickname", nicknameEdit.getText().toString())
                .addParams("sex", "男".equals(sexTv.getText().toString()) ? "m" : "g")
                .addParams("email", emailEdit.getText().toString())
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        OkHttpUtils.get()
                                .url(HttpConstant.GET_USER_PERSONAL_DATA)
                                .build()
                                .execute(new HttpCallBack<AddPersonalBean>(AddPersonalBean.class, true, PersonDataActivity.this) {
                                    @Override
                                    public void onResponse(AddPersonalBean response, int id) {
                                        if (response.getCode() != 0) {
                                            ToastManager.showShortToast(response.getMsg());
                                            return;
                                        }

                                        try {
                                            String headerUrlStr = response.getData().getQiniuDomain() + response.getData().getHeadImgUrl();
                                            ImageUtils.GlideUtil(PersonDataActivity.this, headerUrlStr, headImageView);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        //ImageUtils.GlideUtil(PersonDataActivity.this, (String) headImageView.getTag(R.id.imageId), headImageView);
                    }
                });
    }

    // 修改个人资料接口
    private void reqModifyUserData() {
        String headImgUrl = (String) headImageView.getTag(R.id.imageId);
        final String headUrl = headImgUrl != null ? headImgUrl : oldHeadUrlStr;

        if (StringUtils.isEmpty(nicknameEdit.getText().toString())) {
            ToastManager.showShortToast("您输入的昵称不能为空！");
            return;
        }
        if (StringUtils.containsEmoji(nicknameEdit.getText().toString())) {
            ToastManager.showShortToast("昵称不能包含特殊字符！");
            return;
        }
        if (StringUtils.isEmpty(sexTv.getText().toString())) {
            ToastManager.showShortToast("您还未选择性别！");
            return;
        }
        if (StringUtils.isEmpty(emailEdit.getText().toString())) {
            ToastManager.showShortToast("您输入的邮箱不能为空！");
            return;
        }
        if (!StringUtils.checkoutEmailAddress(emailEdit.getText().toString())) {
            ToastManager.showShortToast("您输入的邮箱格式不正确！");
            return;
        }

        OkHttpUtils.post()
                .url(HttpConstant.UPDATE_USER_PERSONAL_DATA)
                .addParams("headImgUrl", headUrl != null ? headUrl : "")
                .addParams("nickname", nicknameEdit.getText().toString())
                .addParams("sex", "男".equals(sexTv.getText().toString()) ? "m" : "g")
                .addParams("email", emailEdit.getText().toString())
                .build()
                .execute(new HttpCallBack<BaseBean>(BaseBean.class, true, this) {
                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response.getCode() != 0) {
                            ToastManager.showShortToast(response.getMsg());
                            return;
                        }

                        ToastManager.showShortToast("修改成功！");
                        finish();
                    }
                });
    }
}
