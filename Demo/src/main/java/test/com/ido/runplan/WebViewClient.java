package test.com.ido.runplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;

public class WebViewClient extends BridgeWebViewClient {
    private Context context;
    private PageLoadView mListener;
    public WebViewClient(BridgeWebView webView, PageLoadView listener) {
        super(webView);
        mListener=listener;
    }
    public WebViewClient(BridgeWebView webView, Context context, PageLoadView listener){
        super(webView);
        this.context = context;
        mListener=listener;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mListener.LoadPageFinish();
    }


}
