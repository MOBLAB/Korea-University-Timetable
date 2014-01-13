package com.proinlab.kut;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewCon extends Activity {

	private WebView WebViewController;
	private ProgressBar mPBar;
	private String addr = null;
	private Button backbtn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		addr = getIntent().getStringExtra("url");
		
		backbtn = (Button) findViewById(R.id.webview_backbtn);
		backbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		WebViewController = (WebView) findViewById(R.id.webview_web);
		WebViewController.setWebViewClient(new MyWebClient());
		WebViewController.setHorizontalScrollBarEnabled(false);
		WebViewController.setVerticalScrollBarEnabled(false);

		WebSettings set = WebViewController.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(true);
		set.setDomStorageEnabled(true);
		set.setDefaultZoom(WebSettings.ZoomDensity.FAR);

		mPBar = (ProgressBar) findViewById(R.id.webview_progress);

		WebViewController.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress < 100) {
					mPBar.setVisibility(ProgressBar.VISIBLE);
				} else if (progress == 100) {
					mPBar.setVisibility(ProgressBar.GONE);
				}
				mPBar.setProgress(progress);
			}
		});
		
		WebViewController.loadUrl(addr);
	}

	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}