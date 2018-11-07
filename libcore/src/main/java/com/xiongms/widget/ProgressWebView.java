package com.xiongms.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xiongms.libcore.R;
import com.xiongms.libcore.utils.ViewUtil;

import java.util.Iterator;
import java.util.Map;

/**
 * 上方带有加载进度条的webview
 * Created by xiongms on 2016/3/3.
 */
public class ProgressWebView extends WebView {
    private Context mContext;
    private int screenWidth;
    private LayoutParams params;
    private View progressView;
    private OnTitleListener onTitleListener;
    private boolean isShowProgressView = true;

    private android.webkit.WebChromeClient webChromeClient;
    private android.webkit.WebViewClient webViewClient;

    private Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                int i = msg.arg1;
                double proWidth = screenWidth * i / 100.00;
                params.width = (int) proWidth;
                progressView.setLayoutParams(params);
                if (i == 100) {
                    progressView.setVisibility(View.GONE);
                } else if (isShowProgressView) {
                    progressView.setVisibility(View.VISIBLE);
                }
            }
        }
    };


    public ProgressWebView(Context context) {
        this(context, null);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setGeolocationEnabled(true);
        this.getSettings().setDefaultTextEncodingName("utf-8");

        String userAgent = this.getSettings().getUserAgentString();
        this.getSettings().setUserAgentString(userAgent);

        this.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        this.getSettings().setDatabaseEnabled(true);
        //设置定位的数据库路径
        String dir = getContext().getDir("database", Context.MODE_PRIVATE).getPath();
        this.getSettings().setGeolocationDatabasePath(dir);

        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setAppCacheEnabled(true);
        String cachePath = getContext().getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        this.getSettings().setAppCachePath(cachePath);
        this.getSettings().setAppCacheMaxSize(1024 * 1024 * 10);//设置缓冲大小为10M

        // 设置可以支持缩放
        this.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        this.getSettings().setBuiltInZoomControls(true);
        this.getSettings().setDisplayZoomControls(false);
        //扩大比例的缩放
        this.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        this.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        this.getSettings().setLoadWithOverviewMode(true);

        // android 5.0以上默认不支持Mixed Content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        this.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                getContext().startActivity(intent);
            }
        });


        this.mContext = context;
        progressView = new View(context);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewUtil.dip2px(context, 2), -1, 0);
        progressView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        progressView.setLayoutParams(params);
        WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        addView(progressView);
        super.setWebChromeClient(new CWebChromeClient());
        super.setWebViewClient(new CWebViewClient());
    }

    public void setProgressBarColor(int color) {
        progressView.setBackgroundColor(color);
    }

    public void setIsShowProgressBar(boolean isShow) {
        isShowProgressView = isShow;
        if (isShow) {
            progressView.setVisibility(VISIBLE);
        } else {
            progressView.setVisibility(GONE);
        }
    }

    @Override
    public void setWebChromeClient(android.webkit.WebChromeClient client) {
        this.webChromeClient = client;
    }

    @Override
    public void setWebViewClient(android.webkit.WebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }

    public class CWebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (webChromeClient != null)
                webChromeClient.onProgressChanged(view, newProgress);

            if (newProgress > 90) {
                String title = ProgressWebView.this.getTitle();
                if (onTitleListener != null) {
                    onTitleListener.onTitleChanged(title);
                }
            }

            Message message = Message.obtain();
            message.arg1 = newProgress;
            message.what = 0;
            progressHandler.sendMessage(message);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (webChromeClient != null)
                webChromeClient.onReceivedTitle(view, title);

            if (onTitleListener != null) {
                onTitleListener.onTitleChanged(title);
            }
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, true);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    public class CWebViewClient extends android.webkit.WebViewClient {

        @Override
        public void onLoadResource(WebView view, String url) {
            if (webViewClient != null) {
                webViewClient.onLoadResource(view, url);
            } else {
                super.onLoadResource(view, url);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (webViewClient != null && Build.VERSION.SDK_INT >= 24) {
                return webViewClient.shouldOverrideUrlLoading(view, request);
            } else {
                return super.shouldOverrideUrlLoading(view, request);
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (webViewClient != null && Build.VERSION.SDK_INT >= 23) {
                webViewClient.onReceivedHttpError(view, request, errorResponse);
            } else {
                super.onReceivedHttpError(view, request, errorResponse);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if (!(url.startsWith("http://") || url.startsWith("http://") || url.startsWith("file://"))) {
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(url));
                    getContext().startActivity(intent);
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (webViewClient != null) {
                return webViewClient.shouldOverrideUrlLoading(view, url);
            }
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (webViewClient != null) {
                webViewClient.onPageStarted(view, url, favicon);
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (webViewClient != null) {
                webViewClient.onPageFinished(view, url);
            } else {
                super.onPageFinished(view, url);
            }
        }

        @Override
        public void onReceivedSslError(WebView view,
                                       SslErrorHandler handler, SslError error) {
            // TODO Auto-generated method stub
            handler.cancel();// Android默认的处理方式
//            handler.proceed();// 接受所有网站的证书
//            handleMessage(Message msg);// 进行其他处理
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            if (errorCode == -10) {
//                view.loadUrl("file:///android_asset/html/urlerror.html?failUrl=" + URLEncoder.encode(failingUrl));
//            } else if (errorCode == 404) {
//                view.loadUrl("file:///android_asset/html/404.html?failUrl=" + URLEncoder.encode(failingUrl));
//            } else {
//                view.loadUrl("file:///android_asset/html/nowifi.html?failUrl=" + URLEncoder.encode(failingUrl));
//            }

            if (webViewClient != null) {
                webViewClient.onReceivedError(view, errorCode, description, failingUrl);
            } else {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressView.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressView.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }


    /**
     * 设置浏览器cookie
     *
     * @param domain  域名
     * @param cookies cookie键值对
     */
    public void setCookie(String domain, Map<String, String> cookies) {

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);


        Iterator<Map.Entry<String, String>> it = cookies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getValue() != null && entry.getValue().length() > 0) {
                String strCookie = entry.getKey() + "=" + entry.getValue() + ";";
                strCookie = strCookie + "domain=" + domain + ";";
                strCookie = strCookie + "path=/";
                cookieManager.setCookie(domain, strCookie);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else {
            CookieSyncManager.createInstance(getContext());
            CookieSyncManager.getInstance().sync();
        }
    }


    /**
     * 设置浏览器cookie
     *
     * @param domain  域名
     * @param cookies cookie字符串
     */
    public void setCookie(String domain, String cookies) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        cookieManager.setCookie(domain, cookies);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else {
            CookieSyncManager.createInstance(getContext());
            CookieSyncManager.getInstance().sync();
        }
    }

    /**
     * 删除cookie
     */
    public void removeCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else {
            CookieSyncManager.createInstance(getContext());
            CookieSyncManager.getInstance().sync();
        }
    }

    public void setOnTitleListener(OnTitleListener onTitleListener) {
        this.onTitleListener = onTitleListener;
    }

    public interface OnTitleListener {
        void onTitleChanged(String title);
    }

}