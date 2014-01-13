package com.proinlab.kut;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.proinlab.kut.functions.DataBaseHelper;
import com.proinlab.kut.functions.MyListDB;
import com.proinlab.kut.functions.ListViewCustomAdapter;
import com.proinlab.kut.functions.PREF;

public class MyLecList extends Activity {

	private ListView SearchList;
	private ArrayList<String[]> SearchData = new ArrayList<String[]>();
	private ListViewCustomAdapter adapter;
	private DBEditFn DBEDIT = new DBEditFn();
	private MyListDB mHelper;
	private TextView All_credits;
	
	private String SelectedData = "시간표 1";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_lec_list);
	}

	public void onResume() {
		super.onResume();
		
		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		SelectedData = pref.getString(PREF.MYLEC_NAME, "시간표 1");
		
		InitailizeFn();
	}

	private int getAllCredits() {
		if (SearchData == null)
			return 0;
		int returnint = 0;
		for (int i = 0; i < SearchData.size(); i++) {
			String tmp = SearchData.get(i)[DataBaseHelper.DB_INDEX_3ROW_09_CREDIT];
			returnint = returnint + Integer.parseInt(tmp.substring(0, 1));
		}
		return returnint;
	}

	private String[] selectData;
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			selectData = SearchData.get(arg2);

			showDialog(0);

			for (int i = 0; i < selectData.length; i++) {
				if (selectData[i] == "")
					selectData[i] = "none";
			}
		}
	};

	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case 0:
			String[] strarr2 = { "강의상세보기", "삭제" };
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setItems(strarr2, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								Intent intent = new Intent(MyLecList.this,
										WebViewCon.class);
								intent.putExtra(
										"url",
										selectData[DataBaseHelper.DB_INDEX_3ROW_12_LINK]);
								startActivity(intent);
							} else if (which == 1) {
								DBEDIT.DELETE_LECTURE(mHelper, "MY_DATA",
										selectData);
								ListUpdate("MY_DATA");
							}
						}
					});
			AlertDialog alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		}
		return null;
	}

	private void InitailizeFn() {
		
		mHelper = new MyListDB(this, SelectedData);

		SearchList = (ListView) findViewById(R.id.my_lec_listv);
		All_credits = (TextView) findViewById(R.id.my_lec_credits);

		ListUpdate("MY_DATA");

	}

	private void ListUpdate(String Table) {
		SearchData = DBEDIT.LECTURE_FIND_ALL(mHelper, Table);
		if (SearchData == null)
			SearchData = new ArrayList<String[]>();
		adapter = new ListViewCustomAdapter(MyLecList.this,
				R.layout.timetable_search_listview, SearchData);
		SearchList.setAdapter(adapter);
		SearchList.setOnItemClickListener(mOnItemClickListener);
		All_credits.setText("신청학점 : " + getAllCredits() + " ");
	}
}