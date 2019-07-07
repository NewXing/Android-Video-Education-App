package education.juxin.com.educationpro.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CusWebView extends WebView {

    public CusWebView(Context context) {
        this(context, null);
    }

    public CusWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CusWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebView();
    }

    private void initWebView() {
        WebSettings settings = getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);

        setWebViewClient(new CusWebViewClient(this));
    }

    private class CusWebViewClient extends WebViewClient {

        private WebView webView;

        CusWebViewClient(WebView webView) {
            this.webView = webView;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            imgSizeReset();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        /**
         * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
         * 原理就是在网页上加一段脚本
         */
        private void imgSizeReset() {
            webView.loadUrl("javascript:(function(){var obj = document.getElementsByTagName('img');" +
                    "for(var i = 0; i < obj.length; ++i){var img = obj[i];img.style.maxWidth = '100%'; img.style.height = 'auto';}})()"
            );
        }
    }

    /**
     * 加载 HTML 字符串
     */
    public void loadHtmlString(String htmlString) {
        loadData(htmlString, "text/html; charset=UTF-8", null);
    }
}
