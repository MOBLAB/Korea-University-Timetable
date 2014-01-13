package com.proinlab.kut;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DevInfo extends Activity {

	private Button emailbtn, webbtn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devinfo);
		
		emailbtn= (Button) findViewById(R.id.devinfo_emailbtn);
		emailbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("mailto:proin0312@gmail.com");
				Intent it = new Intent(Intent.ACTION_SENDTO, uri);
				startActivity(it);
			}
		});
		
		webbtn= (Button) findViewById(R.id.devinfo_webbtn);
		webbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("http://proinlab.com");
				Intent it  = new Intent(Intent.ACTION_VIEW,uri);
				startActivity(it);
			}
		});
	}


}