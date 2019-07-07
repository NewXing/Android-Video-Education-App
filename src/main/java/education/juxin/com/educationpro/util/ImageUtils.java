package education.juxin.com.educationpro.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import education.juxin.com.educationpro.R;
import education.juxin.com.educationpro.view.CircleImageView;

/**
 * 创建者：王兴
 * 创建时间：2016/4/11 9:12
 * 类说明：ImageUtils - 图片特殊效果处理的工具类
 */
public class ImageUtils {
    /**
     * 调用系统的裁剪图片功能
     *
     * @param activity    mActivity
     * @param inputFile   要裁剪图片
     * @param outputFile  裁剪完的图片
     * @param requestCode 请求code
     */
    public static void startCrop(Activity activity, File inputFile, File outputFile, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(getImageContentUri(activity, inputFile), "image/*");//自己使用Content Uri替换File Uri
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 380);
        intent.putExtra("outputY", 380);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));//定义输出的File Uri
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 使用Content Uri替换File Uri
     */
    private static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = null;
        Uri resultUri = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + "=? ",
                    new String[]{filePath}, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                resultUri = Uri.withAppendedPath(baseUri, "" + id);
            } else if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                resultUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return resultUri;
    }

    /**
     * 获取当前屏幕截图
     *
     * @param hasStatusBar 是否包含包含状态栏
     */
    public static Bitmap screenShot(Activity activity, boolean hasStatusBar) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        int width = ScreenUtils.getScreenWidth(activity);
        int height = ScreenUtils.getScreenHeight(activity);

        Bitmap bmp = view.getDrawingCache();
        Bitmap bp;

        if (hasStatusBar) {
            bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        } else {
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;

            bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        }

        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 图片模糊效果
     * 使用方法:
     * 1.获取要进行模糊处理的图片:
     * Bitmap blurImage = BitmapFactory.decodeResource(getResources(), R.drawable.img_bg);
     * 2.设置模糊半径，参数值越大越模糊，参数范围为 0-25，否则会报错:
     * "android.renderscript.RSIllegalArgumentException: Radius out of range (0 < r <= 25)."
     * Bitmap newImg = Blur.fastBlur(this, blurImage, 5);
     * 3.在 ImageView 中添加经过模糊处理后的Bitmap图片:
     * aboveOneImageView.setImageBitmap(newImg1);
     *
     * @param context    Context 对象
     * @param sentBitmap 进行模糊处理的 Bitmap 对象
     * @param radius     模糊半径
     * @return 返回经过模糊处理之后的 Bitmap 对象
     */
    @SuppressLint("NewApi")
    public static Bitmap blurByBitmap(Context context, Bitmap sentBitmap, int radius) {
        // 如果系统的Api在16以上，就可以直接使用系统提供的方法对图片进行模糊处理
        if (Build.VERSION.SDK_INT > 16) {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, sentBitmap,
                    Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }

        // 如果Api条件不满足16，则使用如下的Stack Blur算法算法
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rSum, gSum, bSum, x, y, i, p, yp, yi, yw;
        int vMin[] = new int[Math.max(w, h)];
        int divSum = (div + 1) >> 1;
        divSum *= divSum;
        int dv[] = new int[256 * divSum];
        for (i = 0; i < 256 * divSum; i++) {
            dv[i] = (i / divSum);
        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackPointer;
        int stackStart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routSum, goutSum, boutSum;
        int rinSum, ginSum, binSum;
        for (y = 0; y < h; y++) {
            rinSum = ginSum = binSum = routSum = goutSum = boutSum = rSum = gSum = bSum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rSum += sir[0] * rbs;
                gSum += sir[1] * rbs;
                bSum += sir[2] * rbs;
                if (i > 0) {
                    rinSum += sir[0];
                    ginSum += sir[1];
                    binSum += sir[2];
                } else {
                    routSum += sir[0];
                    goutSum += sir[1];
                    boutSum += sir[2];
                }
            }
            stackPointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rSum];
                g[yi] = dv[gSum];
                b[yi] = dv[bSum];
                rSum -= routSum;
                gSum -= goutSum;
                bSum -= boutSum;
                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];
                routSum -= sir[0];
                goutSum -= sir[1];
                boutSum -= sir[2];
                if (y == 0) {
                    vMin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vMin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinSum += sir[0];
                ginSum += sir[1];
                binSum += sir[2];
                rSum += rinSum;
                gSum += ginSum;
                bSum += binSum;
                stackPointer = (stackPointer + 1) % div;
                sir = stack[(stackPointer) % div];
                routSum += sir[0];
                goutSum += sir[1];
                boutSum += sir[2];
                rinSum -= sir[0];
                ginSum -= sir[1];
                binSum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinSum = ginSum = binSum = routSum = goutSum = boutSum = rSum = gSum = bSum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rSum += r[yi] * rbs;
                gSum += g[yi] * rbs;
                bSum += b[yi] * rbs;
                if (i > 0) {
                    rinSum += sir[0];
                    ginSum += sir[1];
                    binSum += sir[2];
                } else {
                    routSum += sir[0];
                    goutSum += sir[1];
                    boutSum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackPointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rSum] << 16) | (dv[gSum] << 8) | dv[bSum];
                rSum -= routSum;
                gSum -= goutSum;
                bSum -= boutSum;
                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];
                routSum -= sir[0];
                goutSum -= sir[1];
                boutSum -= sir[2];
                if (x == 0) {
                    vMin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vMin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinSum += sir[0];
                ginSum += sir[1];
                binSum += sir[2];
                rSum += rinSum;
                gSum += ginSum;
                bSum += binSum;
                stackPointer = (stackPointer + 1) % div;
                sir = stack[stackPointer];
                routSum += sir[0];
                goutSum += sir[1];
                boutSum += sir[2];
                rinSum -= sir[0];
                ginSum -= sir[1];
                binSum -= sir[2];
                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }

    /**
     * 判别是否包含Emoji表情
     */
    public static boolean containsEmoji(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    public static void GlideUtil(Context context, String imgUrl, ImageView imageView) {
        try {

            if (context == null || imageView == null) {
                return;
            }

            if (imageView instanceof CircleImageView) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }

            int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            imageView.measure(widthSpec, heightSpec);
            int measuredWidth = imageView.getMeasuredWidth() * 2;
            int measuredHeight = imageView.getMeasuredHeight() * 2;

            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.ic_launcher); // 缺省占位图
            options.error(R.drawable.load_error);
            options.override(measuredWidth, measuredHeight);

            if (imgUrl == null || imgUrl.trim().isEmpty()) {
                Glide.with(context)
                        .load(R.mipmap.ic_launcher_round)
                        .apply(options)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(buildGlideUrl(imgUrl))
                        .apply(options)
                        .into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GlideUrl buildGlideUrl(String url) {
        return new GlideUrl(url, new LazyHeaders.Builder().addHeader("Referer", "http://app.qiantu66.com").build());
    }

}
