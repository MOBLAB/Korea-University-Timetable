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
import android.widget.Toast;

import com.proinlab.networkmanager.ParsingTool;
import com.proinlab.kut.functions.DataBaseHelper;
import com.proinlab.kut.functions.MyListDB;
import com.proinlab.kut.functions.ListViewCustomAdapter;
import com.proinlab.kut.functions.PREF;

public class FavoriteLecList extends Activity {

	private ListView SearchList;
	private ArrayList<String[]> SearchData = new ArrayList<String[]>();
	private ListViewCustomAdapter adapter;
	private DBEditFn DBEDIT = new DBEditFn();
	private MyListDB FavoriteListDB, myListDB;
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
			String[] strarr = { "강의상세보기", "내 시간표에 담기", "삭제" };
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setItems(strarr, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								Intent intent = new Intent(FavoriteLecList.this,
										WebViewCon.class);
								intent.putExtra(
										"url",
										selectData[DataBaseHelper.DB_INDEX_3ROW_12_LINK]);
								startActivity(intent);
							} else if (which == 1) {
								if(selectData[DataBaseHelper.DB_INDEX_3ROW_11_SCHEDULE].equals("none")) {
									Toast.makeText(FavoriteLecList.this, "시간 정보가 없습니다.",
											Toast.LENGTH_SHORT).show();
									return;
								}
								
								ParsingTool parsingtool = new ParsingTool();
								ArrayList<String[]> CateDataL = DBEDIT
										.LECTURE_FIND_ALL(
												myListDB,
												"MY_DATA");
								if (CateDataL == null) {
									DBEDIT.LECTURE_INSERT(myListDB,
											"MY_DATA",
											selectData);
									Toast.makeText(FavoriteLecList.this,
											"시간표 목록에 저장되었습니다.",
											Toast.LENGTH_SHORT).show();
								} else {
									boolean b = true;
									for (int i = 0; i < CateDataL.size(); i++) {
										ArrayList<String> savedDataTimeL = parsingtool
												.GET_PART_DETAIL_TIME(CateDataL
														.get(i));
										ArrayList<String> selectTimeL = parsingtool
												.GET_PART_DETAIL_TIME(selectData);

										for (int j = 0; j < selectTimeL.size(); j++) {
											for (int k = 0; k < savedDataTimeL
													.size(); k++) {
												if (savedDataTimeL.get(k)
														.equals(selectTimeL
																.get(j)))
													b = false;
											}
										}
									}
									if (b) {
										if (DBEDIT
												.LECTURE_FIND_DATA_BY_VALUE(
														myListDB,
														"MY_DATA",
														selectData[DataBaseHelper.DB_INDEX_3ROW_05_LEC_ID],
														DataBaseHelper.DB_INDEX_3ROW_05_LEC_ID)
												.size() != 0)
											Toast.makeText(FavoriteLecList.this,
													"이미 저장된 과목입니다.",
													Toast.LENGTH_SHORT).show();
										else {
											DBEDIT.LECTURE_INSERT(
													myListDB,
													"MY_DATA",
													selectData);
											Toast.makeText(FavoriteLecList.this,
													"시간표 목록에 저장되었습니다.",
													Toast.LENGTH_SHORT).show();
										}
									} else {
										Toast.makeText(FavoriteLecList.this,
												"해당 강의의 시간에 다른 강의가 있습니다.",
												Toast.LENGTH_SHORT).show();
									}
								}
							} else if (which == 2) {
								DBEDIT.DELETE_LECTURE(
										FavoriteListDB,
										"MY_DATA",
										selectData);
								ListUpdate("MY_DATA");
							}
						}
					});
			AlertDialog alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;		}
		return null;
	}

	private void InitailizeFn() {
		FavoriteListDB = new MyListDB(this, "FAVORITE/data");
		myListDB = new MyListDB(this, SelectedData);
		
		SearchList = (ListView) findViewById(R.id.my_lec_listv);
		All_credits = (TextView) findViewById(R.id.my_lec_credits);
		All_credits.setVisibility(View.GONE);
		ListUpdate("MY_DATA");

	}

	private void ListUpdate(String Table) {
		SearchData = DBEDIT.LECTURE_FIND_ALL(FavoriteListDB, Table);
		if (SearchData == null)
			SearchData = new ArrayList<String[]>();
		adapter = new ListViewCustomAdapter(FavoriteLecList.this,
				R.layout.timetable_search_listview, SearchData);
		SearchList.setAdapter(adapter);
		SearchList.setOnItemClickListener(mOnItemClickListener);
	}
}