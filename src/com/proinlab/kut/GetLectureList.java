package com.proinlab.kut;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.proinlab.kut.functions.PREF;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class GetLectureList extends Activity {

	private Spinner spinner;
	private EditText EditTextYear;
	private Button getInfoBtn;

	private TextView DBdate;

	private ArrayList<String> spinneritems = new ArrayList<String>();
	private ArrayList<String> itemid = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private String selectedTerm, InputYear;
	private LinearLayout linear;

	private CheckBox campus_anam, campus_sejong;

	private SharedPreferences pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		spinner = (Spinner) findViewById(R.id.main_spinner_term);
		EditTextYear = (EditText) findViewById(R.id.main_edittext_year);
		getInfoBtn = (Button) findViewById(R.id.main_button_getinfo);

		campus_anam = (CheckBox) findViewById(R.id.main_campuscheck_anam);
		campus_sejong = (CheckBox) findViewById(R.id.main_campuscheck_sejong);
	
		Calendar mCalendar = Calendar.getInstance();
		String yearstr = Integer.toString(mCalendar.get(Calendar.YEAR));
		EditTextYear.setText(yearstr);

		getInfoBtn.setOnClickListener(mOnclickListener);

		selectedTerm = "1R";
		spinneritems.add("1학기");
		spinneritems.add("여름학기");
		spinneritems.add("2학기");
		spinneritems.add("겨울학기");
		spinneritems.add("국제하계대학");
		itemid.add("1R");
		itemid.add("1S");
		itemid.add("2R");
		itemid.add("2W");
		itemid.add("SC");

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinneritems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(mOnItemSelectedListener);

		pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
		campus_anam.setChecked(pref.getBoolean(PREF.IS_ANAM, false));
		campus_sejong.setChecked(pref.getBoolean(PREF.IS_SEJONG, false));
		EditTextYear.setText(pref.getString(PREF.DATABASE_YEAR, yearstr));
		InputYear = pref.getString(PREF.DATABASE_YEAR, yearstr);
		selectedTerm = pref.getString(PREF.DATABASE_SEMISTER, selectedTerm);
		if (selectedTerm.equals("1R"))
			spinner.setSelection(0);
		else if (selectedTerm.equals("1S"))
			spinner.setSelection(1);
		else if (selectedTerm.equals("2R"))
			spinner.setSelection(2);
		else if (selectedTerm.equals("2W"))
			spinner.setSelection(3);
		else if (selectedTerm.equals("SC"))
			spinner.setSelection(4);
		DBdate = (TextView) findViewById(R.id.main_database_date);
		if (pref.getString(PREF.DATABASE_DATE, "none").equals("none")) {
			DBdate.setText("DB를 받아온 적이 없습니다");
		} else {
			DBdate.setText("현재 DB 날짜 : "
					+ pref.getString(PREF.DATABASE_DATE, "none"));
		}
	

		linear = (LinearLayout) View.inflate(this, R.layout.loading_dialog,
				null);
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setView(linear);
		alt_bld.setNegativeButton("나가기", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		alert = alt_bld.create();
		alert.setCancelable(false);

	}

	private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			selectedTerm = itemid.get(arg2);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			selectedTerm = itemid.get(0);
		}
	};

	private View.OnClickListener mOnclickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == getInfoBtn.getId()) {

				if (!campus_anam.isChecked() && !campus_sejong.isChecked()) {
					Toast.makeText(GetLectureList.this, "캠퍼스가 선택되지 않았습니다.",
							Toast.LENGTH_SHORT).show();
					return;
				}

				Calendar c = Calendar.getInstance();
				String db_date = Integer.toString(c.get(Calendar.YEAR)) + "/"
						+ Integer.toString(c.get(Calendar.MONTH) + 1) + "/"
						+ Integer.toString(c.get(Calendar.DAY_OF_MONTH));

				InputYear = EditTextYear.getText().toString();

				pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean(PREF.IS_ANAM, campus_anam.isChecked());
				editor.putBoolean(PREF.IS_SEJONG, campus_sejong.isChecked());
				editor.putString(PREF.DATABASE_YEAR, InputYear);
				editor.putString(PREF.DATABASE_SEMISTER, selectedTerm);
				editor.putString(PREF.DATABASE_DATE, db_date);
				editor.putBoolean(PREF.IS_DOWNLOADING, true);
				editor.commit();
				
				alert.show();
				Intent intent = new Intent(GetLectureList.this,
						DownloadService.class);
				startService(intent);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							if (!pref.getBoolean(PREF.IS_DOWNLOADING, false)) {
								mHandler.post(new Runnable() {
									public void run() {
										mHandler.sendEmptyMessage(0);
									}
								});
								break;
							}
						}
					}
				}).start();

			}
		}
	};

	private AlertDialog alert;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (alert.isShowing()) {
				alert.dismiss();
			}
		}
	};
}
