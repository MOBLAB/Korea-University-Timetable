package com.proinlab.kut;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.proinlab.kut.functions.PREF;

public class Setting extends Activity {

	private TextView TimeTableDay_TXT;
	private ImageButton TimeTableDay_IMGBTN;
	private TimePicker tPicker;
	private CheckBox autodown;

	private SharedPreferences pref;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
		boolean tbool = pref.getBoolean(PREF.TIMETABLE_DAY, false);

		TimeTableDay_TXT = (TextView) findViewById(R.id.setting_timetable_day_txt);
		TimeTableDay_IMGBTN = (ImageButton) findViewById(R.id.setting_timetable_day_btn);
		tPicker = (TimePicker) findViewById(R.id.setting_timepicker);
		autodown = (CheckBox) findViewById(R.id.setting_check_auto_down);

		if (tbool) {
			TimeTableDay_TXT.setText("월 ~ 토");
		} else {
			TimeTableDay_TXT.setText("월 ~ 금");
		}

		TimeTableDay_IMGBTN.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(0);
			}
		});

		tPicker.setIs24HourView(true);
		tPicker.setCurrentHour(pref.getInt(PREF.AUTO_SAVE_HOUR, 2));
		tPicker.setCurrentMinute(pref.getInt(PREF.AUTO_SAVE_MINUTE, 0));

		tPicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putInt(PREF.AUTO_SAVE_HOUR, hourOfDay);
				editor.putInt(PREF.AUTO_SAVE_MINUTE, minute);
				editor.putBoolean(PREF.DOWNLOAD_AUTO, false);
				editor.commit();
				
				autodown.setChecked(false);
			}
		});

		autodown.setChecked(pref.getBoolean(PREF.DOWNLOAD_AUTO, false));

		autodown.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);

				if (pref.getString(PREF.DATABASE_DATE, "none").equals("none")) {
					Toast.makeText(Setting.this, "수강편람을 받아온 적이 없습니다.",
							Toast.LENGTH_SHORT).show();
					autodown.setChecked(false);
					return;
				}

				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean(PREF.DOWNLOAD_AUTO, autodown.isChecked());
				editor.commit();
				
				setAutoDownload(isChecked);

			}
		});
	}

	private void setAutoDownload(boolean isChecked) {
		if (isChecked) {
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(this, AutoDownload.class);
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
					0);
			Calendar Cal = Calendar.getInstance();
			Cal.set(Calendar.HOUR_OF_DAY, pref.getInt(PREF.AUTO_SAVE_HOUR, 2));
			Cal.set(Calendar.MINUTE, pref.getInt(PREF.AUTO_SAVE_MINUTE, 0));
			Cal.set(Calendar.SECOND, 0);

			am.setRepeating(AlarmManager.RTC, Cal.getTimeInMillis(),
					AlarmManager.INTERVAL_DAY, sender);
		} else {
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(this, AutoDownload.class);
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
					0);
			am.cancel(sender);
		}
	}

	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			String[] arr = { "월 ~ 금", "월 ~ 토" };
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setItems(arr, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								SharedPreferences pref = getSharedPreferences(
										"pref", Activity.MODE_PRIVATE);
								SharedPreferences.Editor editor = pref.edit();
								editor.putBoolean(PREF.TIMETABLE_DAY, false);
								editor.commit();
								TimeTableDay_TXT.setText("월 ~ 금");
								break;
							case 1:
								pref = getSharedPreferences("pref",
										Activity.MODE_PRIVATE);
								editor = pref.edit();
								editor.putBoolean(PREF.TIMETABLE_DAY, true);
								editor.commit();
								TimeTableDay_TXT.setText("월 ~ 토");
								break;

							}
						}
					});
			AlertDialog alert = alt_bld.create();
			alert.setTitle("시간표 설정");
			alert.setCanceledOnTouchOutside(true);
			return alert;
		}
		return null;
	}

}