package com.proinlab.kut;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.proinlab.kut.functions.PREF;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class AutoDownload extends BroadcastReceiver {

	private Context mContext;

	private SharedPreferences pref;

	public void onReceive(Context context, Intent intent) {

		mContext = context;

		pref = mContext.getSharedPreferences("pref", Activity.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(PREF.IS_DOWNLOADING, true);
		editor.commit();

		if (Main.alert != null)
			if (!Main.alert.isShowing())
				Main.alert.show();

		Intent service = new Intent(mContext, DownloadService.class);
		mContext.startService(service);

	}

}
