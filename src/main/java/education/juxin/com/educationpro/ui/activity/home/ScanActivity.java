package education.juxin.com.educationpro.ui.activity.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.base.BaseActivity;
import education.juxin.com.educationpro.view.ToastManager;
import me.iwf.photopicker.PhotoPicker;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 扫一扫页面
 * The type Scan activity.
 */
public class ScanActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private String[] permissionArr = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initToolbar(true, R.string.scan);
        initUI();
    }

    private void initUI() {
        ZXingView zXingView = findViewById(R.id.zxing_view);
        zXingView.setDelegate(new QRCodeView.Delegate() {
            @Override
            public void onScanQRCodeSuccess(String result) {
                ToastManager.showShortToast("扫描完成");

                Intent intent = new Intent();
                intent.putExtra("scan_invite_code", result);
                setResult(OrderActivity.SCAN_RESULT_CODE, intent);

                finish();
            }

            @Override
            public void onScanQRCodeOpenCameraError() {
                ToastManager.showShortToast("打开相机出错，请检查是否开启权限！");
            }
        });
        zXingView.startSpot();

        findViewById(R.id.open_img_form_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPer();
            }
        });
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
                    openImgFormAlbum(items.get(0));
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void openImgFormAlbum(String picturePath) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return QRCodeDecoder.syncDecodeQRCode(picturePath);
            }

            @Override
            protected void onPostExecute(String result) {
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(ScanActivity.this, "图片中未发现二维码", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("scan_invite_code", result);
                    setResult(OrderActivity.SCAN_RESULT_CODE, intent);

                    finish();
                }
            }
        }.execute();
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
}
