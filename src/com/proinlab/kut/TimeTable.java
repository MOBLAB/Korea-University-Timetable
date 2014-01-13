package com.proinlab.kut;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.proinlab.kut.functions.DataBaseHelper;
import com.proinlab.kut.functions.MyListDB;
import com.proinlab.kut.functions.PREF;
import com.proinlab.networkmanager.ParsingTool;

public class TimeTable extends Activity {

	private LinearLayout timeLayout, Layout1, Layout2, Layout3, Layout4,
			Layout5, Layout6;
	private TextView timeTextView;
	private int displayHeight, displayWidth;
	private String[] dateStr = { "월", "화", "수", "목", "금", "토" };
	private DBEditFn DBEDIT = new DBEditFn();

	private MyListDB myListDB;

	private ArrayList<String[]> myLecList;
	private ParsingTool parse = new ParsingTool();
	private int dateint;
	private int[] colorset = LecBGSet();
	private int[] lecColor;
	private TextView AllCredit_tv, ScreenShotTV;

	private int finaldate = 5;
	private boolean isViewSat = false;

	private String SelectedData = "시간표 1";

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			showDialog(0);
			return true;
		}
		return false;
	}

	public void onResume() {
		super.onResume();

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
		byWeekInit();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void byWeekInit() {
		Calendar mCal = Calendar.getInstance();
		dateint = mCal.get(Calendar.DAY_OF_WEEK) - 2;
		if (dateint < 0)
			dateint = 0;

		Initialize_Week();

		if (myLecList == null)
			return;

		SetTimeTable_Week();
		AllCredit_tv.setText("신청학점 : " + Integer.toString(getAllCredits())
				+ " ");
	}

	private int getAllCredits() {
		if (myLecList == null)
			return 0;
		int returnint = 0;
		for (int i = 0; i < myLecList.size(); i++) {
			String tmp = myLecList.get(i)[MyListDB.DB_INDEX_3ROW_09_CREDIT];
			returnint = returnint + Integer.parseInt(tmp.substring(0, 1));
		}
		return returnint;
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
					int _color = colorset[0];
					for (int e = 0; e < myLecList.size(); e++) {
						if (myLecList.get(e)[4].equals(str[4])) {
							_color = lecColor[e];
						}
					}
					int len = getLecLength(str, _date, i);
					if (len != 0) {
						addTextView(str,
								str[MyListDB.DB_INDEX_3ROW_07_LEC_NAME],
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

		if (text.indexOf(":") != -1)
			text = text.substring(0, text.indexOf(":"));
		if (text.length() > 11) {
			text = text.substring(0, 10) + "...";
		}

		TextView tv = new TextView(this);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, displayHeight / 10 * size);
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
			setContentView(R.layout.timetable_allday_six);
			Layout6 = (LinearLayout) findViewById(R.id.timetable_layout_sixth);
		} else {
			setContentView(R.layout.timetable_allday);
		}

		Display display = getWindowManager().getDefaultDisplay();
		displayHeight = display.getHeight();
		displayWidth = display.getWidth();

		timeLayout = (LinearLayout) findViewById(R.id.timetable_layout_time);
		Layout1 = (LinearLayout) findViewById(R.id.timetable_layout_firstdate);
		Layout2 = (LinearLayout) findViewById(R.id.timetable_layout_seconddate);
		Layout3 = (LinearLayout) findViewById(R.id.timetable_layout_thirddate);
		Layout4 = (LinearLayout) findViewById(R.id.timetable_layout_fourth);
		Layout5 = (LinearLayout) findViewById(R.id.timetable_layout_fifth);
		AllCredit_tv = (TextView) findViewById(R.id.timetable_textview_allcredit);
		timeTextView = (TextView) findViewById(R.id.timetable_textview_time);
		ScreenShotTV = (TextView) findViewById(R.id.timetable_textview_screenshot);

		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				displayWidth, LayoutParams.FILL_PARENT);
		LinearLayout tmp = (LinearLayout) findViewById(R.id.timetable_layout_flip);
		tmp.setLayoutParams(llp);

		timeTextView.setBackgroundColor(Color.rgb(200, 200, 200));

		for (int i = 0; i < 13; i++) {
			TextView tv = new TextView(this);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, displayHeight / 10);
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

		ScreenShotTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimeTable.this, ScreenShot.class);
				startActivity(intent);
			}
		});

		ScreenShotTV.setVisibility(View.GONE);
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
					Intent intent = new Intent(TimeTable.this, WebViewCon.class);
					intent.putExtra("url",
							selectedData[MyListDB.DB_INDEX_3ROW_12_LINK]);
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
		String Campus = DATA[MyListDB.DB_INDEX_3ROW_04_CAMPUS];
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