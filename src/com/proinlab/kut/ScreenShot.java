package com.proinlab.kut;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.proinlab.kut.functions.DataBaseHelper;
import com.proinlab.kut.functions.MyListDB;
import com.proinlab.kut.functions.PREF;
import com.proinlab.networkmanager.ParsingTool;

@SuppressLint("HandlerLeak")
public class ScreenShot extends Activity {

	private LinearLayout timeLayout, Layout1, Layout2, Layout3, Layout4,
			Layout5, entireL, Layout6;
	private TextView timeTextView;
	private int displayHeight;
	private String[] dateStr = { "월", "화", "수", "목", "금", "토" };
	private DBEditFn DBEDIT = new DBEditFn();
	private MyListDB myListDB;
	private ArrayList<String[]> myLecList;
	private ParsingTool parse = new ParsingTool();
	private int dateint;
	private int[] colorset = LecBGSet();
	private int[] lecColor;

	private int finaldate = 5;
	private boolean isViewSat = false;
	
	private String SelectedData = "시간표 1";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		isViewSat = pref.getBoolean(PREF.TIMETABLE_DAY, false);
		SelectedData = pref.getString(PREF.MYLEC_NAME, "시간표 1");
		
		if (isViewSat) {
			finaldate = 6;
		} else {
			finaldate = 5;
		}

		myListDB = new MyListDB(this, SelectedData);
		myLecList = DBEDIT.LECTURE_FIND_ALL(myListDB, "MY_DATA");
		if (myLecList != null) {
			lecColor = new int[myLecList.size()];
			for (int i = 0; i < myLecList.size(); i++) {
				lecColor[i] = colorset[i % 7];
			}
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(1);
			}
		}).start();

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				entireL.buildDrawingCache();
				Bitmap captureView = entireL.getDrawingCache();
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(Environment
							.getExternalStorageDirectory().toString()
							+ "/DCIM/Camera/capture.png");
					if (captureView != null)
						captureView.compress(Bitmap.CompressFormat.PNG, 100,
								fos);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(), "앨범에 저장되었습니다.",
						Toast.LENGTH_LONG).show();
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory())));
				finish();
			} else {
				byWeekInit();
				new Thread(new Runnable() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(0);
					}
				}).start();
			}

		}
	};

	private void byWeekInit() {
		Calendar mCal = Calendar.getInstance();
		dateint = mCal.get(Calendar.DAY_OF_WEEK) - 2;
		if (dateint < 0)
			dateint = 0;

		Initialize_Week();

		if (myLecList == null)
			return;

		SetTimeTable_Week();

	}

	private void SetTimeTable_Week() {
		for (int _date = 0; _date < finaldate; _date++) {
			LinearLayout linear = null;
			switch (_date) {
			case 0:
				linear = Layout1;
				break;
			case 1:
				linear = Layout2;
				break;
			case 2:
				linear = Layout3;
				break;
			case 3:
				linear = Layout4;
				break;
			case 4:
				linear = Layout5;
				break;
			case 5:
				linear = Layout6;
				break;
			}
			for (int i = 1; i < 14; i++) {
				String[] str = returnData(_date, i);

				if (str != null) {
					ArrayList<String> tmps = parse.GET_LOCATION(str);
					String location = "";

					if (returnDate < tmps.size())
						location = tmps.get(returnDate);
					Log.e("TAG", str[DataBaseHelper.DB_INDEX_3ROW_07_LEC_NAME]);
					Log.i("TAG", location);
					Log.i("TAG", Integer.toString(tmps.size()));
					Log.i("TAG", Integer.toString(returnDate));
					int _color = colorset[0];
					for (int e = 0; e < myLecList.size(); e++) {
						if (myLecList.get(e)[4].equals(str[4])) {
							_color = lecColor[e];
						}
					}
					int len = getLecLength(str, _date, i);
					if (len != 0) {
						addTextView(str,
								str[DataBaseHelper.DB_INDEX_3ROW_07_LEC_NAME],
								linear, _color, len, location, i);
						i = i + len - 1;
					}
				} else {
					addTextView(str, "", linear, 0, 1, "", 0);
				}
			}
		}
	}

	private String[] selectedData;
	private String selectedLoc;

	private void addTextView(final String[] data, String text, LinearLayout l,
			int color, int size, final String location, final int time) {
		text = parse.REMOVE_UNNECESSORY(text);
		if (text != null)
			Log.d("TAG", text);
		if (text.indexOf(":") != -1)
			text = text.substring(0, text.indexOf(":"));
		if (text.length() > 11) {
			text = text.substring(0, 10) + "...";
		}

		TextView tv = new TextView(this);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, displayHeight / 14 * size);
		tv.setLayoutParams(param);
		tv.setGravity(Gravity.CENTER);
		tv.setText(text);
		tv.setTextColor(Color.BLACK);
		if (data != null)
			tv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selectedData = data;
					selectedLoc = location;
					onCreateDialogF(time);
				}
			});
		if (color != 0)
			tv.setBackgroundResource(color);
		else
			tv.setBackgroundColor(Color.WHITE);
		l.addView(tv);
	}

	private int getLecLength(String[] _data, int _date, int _time) {
		ArrayList<String> parseddate = parse.GET_PART_TIME(_data);
		if (parseddate == null)
			return 0;
		for (int i = 0; i < parseddate.size(); i++) {
			if (parseddate.get(i).contains(
					dateStr[_date] + "(" + Integer.toString(_time))) {
				// if (parseddate.get(i).length() == 4)
				// return 1;
				if (parseddate.get(i).indexOf("-") == -1) {
					return 1;
				}
				int start = Integer.parseInt(parseddate.get(i).substring(2,
						parseddate.get(i).indexOf("-")));
				int end = Integer.parseInt(parseddate.get(i).substring(
						parseddate.get(i).indexOf("-") + 1,
						parseddate.get(i).indexOf(")")));

				return end - start + 1;
			}
		}
		return 0;
	}

	private int returnDate;

	private String[] returnData(int date, int time) {
		for (int i = 0; i < myLecList.size(); i++) {
			ArrayList<String> parseddate = parse.GET_PART_DETAIL_TIME(myLecList
					.get(i));
			ArrayList<String> parseddate2 = parse.GET_PART_TIME(myLecList
					.get(i));
			for (int k = 0; k < parseddate.size(); k++) {
				if (parseddate.get(k).equals(
						dateStr[date] + Integer.toString(time))) {
					String search = dateStr[date] + "("
							+ Integer.toString(time);
					for (int l = 0; l < parseddate2.size(); l++) {
						if (parseddate2.get(l).contains(search))
							returnDate = l;
					}
					return myLecList.get(i);
				}
			}
		}
		return null;
	}

	private void Initialize_Week() {
		if (isViewSat) {
			setContentView(R.layout.timetable_screenshot_six);
			Layout6 = (LinearLayout) findViewById(R.id.timetable_layout_sixth);
		} else {
			setContentView(R.layout.timetable_screenshot);
		}

		Display display = getWindowManager().getDefaultDisplay();
		displayHeight = display.getHeight();

		timeLayout = (LinearLayout) findViewById(R.id.timetable_layout_time);
		Layout1 = (LinearLayout) findViewById(R.id.timetable_layout_firstdate);
		Layout2 = (LinearLayout) findViewById(R.id.timetable_layout_seconddate);
		Layout3 = (LinearLayout) findViewById(R.id.timetable_layout_thirddate);
		Layout4 = (LinearLayout) findViewById(R.id.timetable_layout_fourth);
		Layout5 = (LinearLayout) findViewById(R.id.timetable_layout_fifth);
		timeTextView = (TextView) findViewById(R.id.timetable_textview_time);
		entireL = (LinearLayout) findViewById(R.id.timetable_layout_capture);

		timeTextView.setBackgroundColor(Color.rgb(200, 200, 200));

		LinearLayout l = (LinearLayout) findViewById(R.id.timetable_scr_datelayout);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, displayHeight / 14);
		l.setLayoutParams(params);

		for (int i = 0; i < 13; i++) {
			TextView tv = new TextView(this);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, displayHeight / 14);
			tv.setLayoutParams(param);
			tv.setGravity(Gravity.CENTER);
			tv.setText(Integer.toString(i + 1));
			tv.setTextColor(Color.BLACK);
			if (i % 2 == 0)
				tv.setBackgroundColor(Color.WHITE);
			else
				tv.setBackgroundColor(Color.rgb(220, 220, 220));
			timeLayout.addView(tv);
		}
	}

	private int[] LecBGSet() {
		int[] c = new int[7];
		c[0] = R.drawable.table2;
		c[1] = R.drawable.table3;
		c[2] = R.drawable.table4;
		c[3] = R.drawable.table5;
		c[4] = R.drawable.table6;
		c[5] = R.drawable.table7;
		c[6] = R.drawable.table1;
		return c;
	}

	private void onCreateDialogF(int time) {
		String[] strarr2 = { "강의상세보기", "삭제" };
		String setTitleMsg = "강의실 : " + selectedLoc + " / 시간 : "
				+ getRealTime(selectedData, time) + " ";

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setItems(strarr2, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					Intent intent = new Intent(ScreenShot.this,
							WebViewCon.class);
					intent.putExtra("url",
							selectedData[DataBaseHelper.DB_INDEX_3ROW_12_LINK]);
					startActivity(intent);
				} else if (which == 1) {
					DBEDIT.DELETE_LECTURE(myListDB, "MY_DATA", selectedData);
					myLecList = DBEDIT.LECTURE_FIND_ALL(myListDB, "MY_DATA");
					if (myLecList != null) {
						lecColor = new int[myLecList.size()];
						for (int i = 0; i < myLecList.size(); i++) {
							lecColor[i] = colorset[i % 7];
						}
					}
					byWeekInit();
				}
			}
		});
		AlertDialog alert = alt_bld.create();
		alert.setTitle(setTitleMsg);
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}

	private String getRealTime(String[] DATA, int t) {
		String Campus = DATA[DataBaseHelper.DB_INDEX_3ROW_04_CAMPUS];
		if (Campus.contains("안암"))
			switch (t) {
			case 1:
				return "9:00";
			case 2:
				return "10:30";
			case 3:
				return "12:00";
			case 4:
				return "1:00";
			case 5:
				return "2:00";
			case 6:
				return "3:30";
			case 7:
				return "5:00";
			case 8:
				return "6:00";
			case 9:
				return "7:00";
			case 10:
				return "8:00";
			case 11:
				return "9:00";
			case 12:
				return "10:00";
			case 13:
				return "11:00";

			}
		else
			switch (t) {
			case 1:
				return "9:00";
			case 2:
				return "10:00";
			case 3:
				return "11:00";
			case 4:
				return "12:00";
			case 5:
				return "1:00";
			case 6:
				return "2:00";
			case 7:
				return "3:00";
			case 8:
				return "4:00";
			case 9:
				return "5:00";
			case 10:
				return "6:00";
			case 11:
				return "7:00";
			case 12:
				return "8:00";
			case 13:
				return "9:00";
			}
		return "";
	}
}