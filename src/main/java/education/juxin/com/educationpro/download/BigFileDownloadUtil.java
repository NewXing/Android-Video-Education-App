package education.juxin.com.educationpro.download;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载回调
 */
public class BigFileDownloadUtil implements Callback {

    private OkHttpClient mOkHttpClient;
    private BigFileDownloadListener downListener;
    private String downPath;
    private long lastProgress;

    public BigFileDownloadUtil(BigFileDownloadListener downListener) {
        this.downListener = downListener;
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public void downFile(String downUrl, String downPath) {
        this.downPath = downPath;
        try {
            downListener.downStart();

            String downDir = downPath.substring(0, downPath.lastIndexOf("/")).trim();
            File destDir = new File(downDir);
            if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdirs();
            }

            Request request = new Request.Builder().url(downUrl).build();
            mOkHttpClient.newCall(request).enqueue(this);
        } catch (Exception e) {
            downListener.downFailed(e.toString());
        }
    }

    private void updateProgress(long progress, long speed) {
        if (progress > lastProgress) {
            downListener.downProgress(progress, speed);
        }
        lastProgress = progress;
    }

    public void cancelDown() {
        Dispatcher dispatcher = mOkHttpClient.dispatcher();
        for (Call call : dispatcher.runningCalls()) {
            call.cancel();
        }
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        downListener.downFailed(e.toString());
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = response.body().byteStream();
            File file = new File(downPath);
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists() && file.isFile()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);

            byte[] buf = new byte[2048];
            int len;
            long sum = 0;
            long saveSum = 0;

            //outputStream.write("wxing".getBytes()); // 开头加入冗余字段
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
                sum += len;
                long progress = (int) (sum * 1.0f / response.body().contentLength() * 100);
                long speed = sum - saveSum;
                saveSum = sum;
                updateProgress(progress, speed);
            }
            outputStream.flush();
            downListener.downSuccess(downPath);
        } catch (Exception e) {
            downListener.downFailed(e.toString());
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        }
    }

}
