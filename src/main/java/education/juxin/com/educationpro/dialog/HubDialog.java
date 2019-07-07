package education.juxin.com.educationpro.dialog;

import android.content.Context;
import android.widget.ImageView;

import com.kaopiz.kprogresshud.KProgressHUD;

public class HubDialog {

    private volatile static HubDialog instance;
    private boolean isShown;
    private KProgressHUD hud;

    private HubDialog() {
    }

    public static HubDialog getInstance() {
        if (instance == null) {
            synchronized (HubDialog.class) {
                if (instance == null) {
                    instance = new HubDialog();
                }
            }
        }
        return instance;
    }

    public void show(Context ctx) {
        show(ctx, "让我静静的刷一会");
    }

    public void show(Context ctx, String msg) {
        if (!isShown) {
            hud = KProgressHUD.create(ctx)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(msg)
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            isShown = true;
        }
    }

    public void show(Context ctx, int res, String msg) {
        if (!isShown) {
            ImageView imageView = new ImageView(ctx);
            imageView.setImageResource(res);

            hud = KProgressHUD.create(ctx)
                    .setCustomView(imageView)
                    .setLabel(msg)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            isShown = true;
        }
    }

    public void dismiss() {
        try {
            if (hud != null) {
                hud.dismiss();
                isShown = false;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
