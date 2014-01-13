package com.proinlab.kut;

import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.proinlab.kut.functions.A;
import com.proinlab.kut.functions.DataBaseHelper;
import com.proinlab.kut.functions.ListViewCustomAdapter;
import com.proinlab.kut.functions.MyListDB;
import com.proinlab.kut.functions.PREF;
import com.proinlab.kut.functions.StructureDB;
import com.proinlab.networkmanager.ParsingTool;

@SuppressLint("DefaultLocale")
public class Search extends Activity {

	private ListView SearchList;
	private ArrayList<String[]> SearchData = new ArrayList<String[]>();
	private ListViewCustomAdapter adapter;
	private DBEditFn DBEDIT = new DBEditFn();

	private DataBaseHelper LecDB;
	private MyListDB myListDB, FavoriteDB;
	private StructureDB structDB;

	private String SelectedData = "시간표 1";

	private ParsingTool parsingtool = new ParsingTool();

	private Spinner typespin, searchspin1, searchspin2, campusspin;
	private ArrayAdapter<String> adapter_typespin, adapter_searchspin1,
			adapter_searchspin2, adapter_campusspin;

	private LinearLayout SearchTypeLayout1, SearchTypeLayout2;
	private EditText SearchEditText;
	private Button SearchBtn1, SearchBtn2;
	private int _CurType = 0;

	private String _Col, _Dept, _Data, _Campus = "안암";
	private String[] _All_Col, _All_Dept;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable_search);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		SelectedData = pref.getString(PREF.MYLEC_NAME, "시간표 1");

		InitializeFn();
	}

	@SuppressLint("DefaultLocale")
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@SuppressLint("DefaultLocale")
		@Override
		public void onClick(View v) {
			if (v.getId() == SearchBtn1.getId()) {
				int dbindex = DataBaseHelper.DB_INDEX_3ROW_02_CATE2;
				String dept = _Dept;
				if (dept.contains("none")) {
					dept = _Col;
					dbindex = DataBaseHelper.DB_INDEX_3ROW_01_CATE1;
				}

				dept = parsingtool.REMOVE_UNNECESSORY(dept);

				ArrayList<String[]> tmparr = new ArrayList<String[]>();
				if (_CurType == A.MAJOR_SEARCH) {
					tmparr = DBEDIT.LECTURE_FIND_DATA_BY_VALUE(LecDB,
							DataBaseHelper.DB_TABLE_LECTURE, dept, dbindex);
					_Col = parsingtool.REMOVE_UNNECESSORY(_Col);
					
					SearchData = new ArrayList<String[]>();
					int size = 0;
					if (tmparr != null)
						size = tmparr.size();
					for (int i = 0; i < size; i++) {
						if (tmparr.get(i)[DataBaseHelper.DB_INDEX_3ROW_01_CATE1]
								.contains(_Col)) {
							SearchData.add(tmparr.get(i));
						}
					}
				} else if (_CurType == A.ETC_SEOUL_SEARCH) {
					tmparr = DBEDIT.LECTURE_FIND_DATA_BY_VALUE(LecDB,
							DataBaseHelper.DB_TABLE_CULTURE_LECTURE, dept,
							dbindex);

					SearchData = new ArrayList<String[]>();
					int size = 0;
					if (tmparr != null)
						size = tmparr.size();
					for (int i = 0; i < size; i++) {
						if (tmparr.get(i)[DataBaseHelper.DB_INDEX_3ROW_04_CAMPUS]
								.contains("안암")) {
							SearchData.add(tmparr.get(i));
						}
					}
				} else if (_CurType == A.ETC_SEJONG_SEARCH) {
					tmparr = DBEDIT.LECTURE_FIND_DATA_BY_VALUE(LecDB,
							DataBaseHelper.DB_TABLE_CULTURE_LECTURE, dept,
							dbindex);

					SearchData = new ArrayList<String[]>();
					int size = 0;
					if (tmparr != null)
						size = tmparr.size();
					for (int i = 0; i < size; i++) {
						if (tmparr.get(i)[DataBaseHelper.DB_INDEX_3ROW_04_CAMPUS]
								.contains("세종")) {
							SearchData.add(tmparr.get(i));
						}
					}
				}

				adapter = new ListViewCustomAdapter(Search.this,
						R.layout.timetable_search_listview, SearchData);

				SearchList.setAdapter(adapter);
				SearchList.setOnItemClickListener(mOnItemClickListener);
			} else if (v.getId() == SearchBtn2.getId()) {

				SearchData = new ArrayList<String[]>();
				ArrayList<String[]> tmparr = new ArrayList<String[]>();
				int dbindex = -1;
				_Data = SearchEditText.getText().toString();

				switch (_CurType) {
				case A.LECNAME_SEARCH:
					dbindex = DataBaseHelper.DB_INDEX_3ROW_07_LEC_NAME;
					break;
				case A.PROFESSOR_SEARCH:
					dbindex = DataBaseHelper.DB_INDEX_3ROW_08_PROFESSOR;
					break;
				case A.LECID_SEARCH:
					if (_Data == null)
						break;
					_Data = _Data.toUpperCase();
					dbindex = DataBaseHelper.DB_INDEX_3ROW_05_LEC_ID;
					break;
				}
				tmparr = DBEDIT
						.LECTURE_FIND_DATA_BY_VALUE_CONTAIN(LecDB,
								DataBaseHelper.DB_TABLE_CULTURE_LECTURE, _Data,
								dbindex);
				int size = 0;
				if (tmparr != null)
					size = tmparr.size();
				for (int i = 0; i < size; i++) {
					if (tmparr.get(i)[DataBaseHelper.DB_INDEX_3ROW_04_CAMPUS]
							.contains(_Campus)) {
						SearchData.add(tmparr.get(i));
					}
				}

				tmparr = DBEDIT.LECTURE_FIND_DATA_BY_VALUE_CONTAIN(LecDB,
						DataBaseHelper.DB_TABLE_LECTURE, _Data, dbindex);
				size = 0;
				if (tmparr != null)
					size = tmparr.size();
				for (int i = 0; i < size; i++) {
					if (tmparr.get(i)[DataBaseHelper.DB_INDEX_3ROW_04_CAMPUS]
							.contains(_Campus)) {
						SearchData.add(tmparr.get(i));
					}
				}
				adapter = new ListViewCustomAdapter(Search.this,
						R.layout.timetable_search_listview, SearchData);
				SearchList.setAdapter(adapter);
				SearchList.setOnItemClickListener(mOnItemClickListener);

			}
		}
	};

	private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (arg0.getId() == typespin.getId()) {
				switch (arg2) {
				// 전공과목찾기
				case A.MAJOR_SEARCH:
					_CurType = A.MAJOR_SEARCH;
					SearchTypeLayout1.setVisibility(View.VISIBLE);
					SearchTypeLayout2.setVisibility(View.GONE);
					_All_Col = DBEDIT.COLNAME_FIND_ALL(LecDB);
					adapter_searchspin1 = new ArrayAdapter<String>(Search.this,
							android.R.layout.simple_spinner_item, _All_Col);
					adapter_searchspin1
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					searchspin1.setAdapter(adapter_searchspin1);
					searchspin1
							.setOnItemSelectedListener(mOnItemSelectedListener);
					break;
				// 안암교양과목찾기
				case A.ETC_SEOUL_SEARCH:
					SearchTypeLayout1.setVisibility(View.VISIBLE);
					SearchTypeLayout2.setVisibility(View.GONE);
					_All_Col = DBEDIT.CULTURE_NAME_FIND_ALL(LecDB);
					_CurType = A.ETC_SEOUL_SEARCH;
					adapter_searchspin1 = new ArrayAdapter<String>(Search.this,
							android.R.layout.simple_spinner_item, _All_Col);
					adapter_searchspin1
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					searchspin1.setAdapter(adapter_searchspin1);
					searchspin1
							.setOnItemSelectedListener(mOnItemSelectedListener);

					break;
				// 세종교양과목찾기
				case A.ETC_SEJONG_SEARCH:
					_CurType = A.ETC_SEJONG_SEARCH;
					SearchTypeLayout1.setVisibility(View.VISIBLE);
					SearchTypeLayout2.setVisibility(View.GONE);
					_All_Col = DBEDIT.CULTURE_NAME_FIND_ALL(LecDB);
					adapter_searchspin1 = new ArrayAdapter<String>(Search.this,
							android.R.layout.simple_spinner_item, _All_Col);
					adapter_searchspin1
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					searchspin1.setAdapter(adapter_searchspin1);
					searchspin1
							.setOnItemSelectedListener(mOnItemSelectedListener);
					break;
				case A.LECNAME_SEARCH:
					_CurType = A.LECNAME_SEARCH;
					SearchTypeLayout1.setVisibility(View.GONE);
					SearchTypeLayout2.setVisibility(View.VISIBLE);
					break;
				case A.PROFESSOR_SEARCH:
					_CurType = A.PROFESSOR_SEARCH;
					SearchTypeLayout1.setVisibility(View.GONE);
					SearchTypeLayout2.setVisibility(View.VISIBLE);
					break;
				case A.LECID_SEARCH:
					_CurType = A.LECID_SEARCH;
					SearchTypeLayout1.setVisibility(View.GONE);
					SearchTypeLayout2.setVisibility(View.VISIBLE);
					break;
				}
			} else if (arg0.getId() == searchspin1.getId()) {
				switch (_CurType) {
				case A.MAJOR_SEARCH:
					_Col = _All_Col[arg2];
					_Dept = "none";
					_All_Dept = DBEDIT.PARAM_FIND_DEPT_BY_COL(LecDB,
							_All_Col[arg2]);
					adapter_searchspin2 = new ArrayAdapter<String>(Search.this,
							android.R.layout.simple_spinner_item, _All_Dept);
					adapter_searchspin2
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					searchspin2.setAdapter(adapter_searchspin2);
					searchspin2
							.setOnItemSelectedListener(mOnItemSelectedListener);
					break;
				case A.ETC_SEOUL_SEARCH:
					_Col = _All_Col[arg2];
					_Dept = "none";
					_All_Dept = DBEDIT.CULTURE_PARAM_FIND_DETAIL_BY_CULTURE(
							LecDB, _All_Col[arg2]);
					adapter_searchspin2 = new ArrayAdapter<String>(Search.this,
							android.R.layout.simple_spinner_item, _All_Dept);
					adapter_searchspin2
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					searchspin2.setAdapter(adapter_searchspin2);
					searchspin2
							.setOnItemSelectedListener(mOnItemSelectedListener);
					break;
				case A.ETC_SEJONG_SEARCH:
					_Col = _All_Col[arg2];
					_Dept = "none";
					_All_Dept = DBEDIT.CULTURE_PARAM_FIND_DETAIL_BY_CULTURE(
							LecDB, _All_Col[arg2]);
					adapter_searchspin2 = new ArrayAdapter<String>(Search.this,
							android.R.layout.simple_spinner_item, _All_Dept);
					adapter_searchspin2
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					searchspin2.setAdapter(adapter_searchspin2);
					searchspin2
							.setOnItemSelectedListener(mOnItemSelectedListener);
					break;
				}

			} else if (arg0.getId() == searchspin2.getId()) {
				if (_All_Dept != null)
					_Dept = _All_Dept[arg2];
				else
					_Dept = "none";
			} else if (arg0.getId() == campusspin.getId()) {
				if (arg2 == 0)
					_Campus = "안암";
				else
					_Campus = "세종";
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	// 리스트뷰 리스너
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
		if (id == 0) {
			String[] strarr = { "강의상세보기", "관심과목에 담기", "내 시간표에 담기" };
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setItems(strarr, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								Intent intent = new Intent(Search.this,
										WebViewCon.class);
								intent.putExtra(
										"url",
										selectData[DataBaseHelper.DB_INDEX_3ROW_12_LINK]);
								startActivity(intent);
							} else if (which == 1) {
								ArrayList<String[]> list = DBEDIT
										.LECTURE_FIND_ALL(FavoriteDB, "MY_DATA");
								if (list != null)
									for (int i = 0; i < list.size(); i++) {
										if (list.get(i)[DataBaseHelper.DB_INDEX_3ROW_05_LEC_ID]
												.equals(selectData[DataBaseHelper.DB_INDEX_3ROW_05_LEC_ID])
												&& list.get(i)[DataBaseHelper.DB_INDEX_3ROW_06_GROUP]
														.equals(selectData[DataBaseHelper.DB_INDEX_3ROW_06_GROUP])) {
											Toast.makeText(Search.this,
													"이미 추가되어 있습니다.",
													Toast.LENGTH_SHORT).show();
											return;
										}
									}
								DBEDIT.LECTURE_INSERT(FavoriteDB, "MY_DATA",
										selectData);
								Toast.makeText(Search.this, "관심과목목록에 저장되었습니다.",
										Toast.LENGTH_SHORT).show();
							} else if (which == 2) {
								showDialog(1);
							}
						}
					});
			AlertDialog alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		} else if (id == 1) {
			ArrayList<String[]> tmpArr = DBEDIT.STRUCTURE_FIND_ALL(structDB);
			String[] strarr = new String[tmpArr.size()];
			for (int i = 0; i < tmpArr.size(); i++) {
				strarr[i] = tmpArr.get(i)[0];
			}
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setItems(strarr, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (selectData[DataBaseHelper.DB_INDEX_3ROW_11_SCHEDULE]
									.equals("none")) {
								Toast.makeText(Search.this, "시간 정보가 없습니다.",
										Toast.LENGTH_SHORT).show();
								return;
							}

							ArrayList<String[]> tmpArr = DBEDIT
									.STRUCTURE_FIND_ALL(structDB);
							myListDB = new MyListDB(Search.this, tmpArr
									.get(which)[0]);
							ArrayList<String[]> CateDataL = DBEDIT
									.LECTURE_FIND_ALL(myListDB, "MY_DATA");
							if (CateDataL == null) {
								DBEDIT.LECTURE_INSERT(myListDB, "MY_DATA",
										selectData);
								Toast.makeText(Search.this, "시간표 목록에 저장되었습니다.",
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
											if (savedDataTimeL.get(k).equals(
													selectTimeL.get(j)))
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
										Toast.makeText(Search.this,
												"이미 저장된 과목입니다.",
												Toast.LENGTH_SHORT).show();
									else {
										DBEDIT.LECTURE_INSERT(myListDB,
												"MY_DATA", selectData);
										Toast.makeText(Search.this,
												"시간표 목록에 저장되었습니다.",
												Toast.LENGTH_SHORT).show();
									}
								} else {
									Toast.makeText(Search.this,
											"해당 강의의 시간에 다른 강의가 있습니다.",
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					});
			AlertDialog alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		}

		return null;
	}

	private void InitializeFn() {
		LecDB = new DataBaseHelper(this);
		myListDB = new MyListDB(this, SelectedData);
		FavoriteDB = new MyListDB(this, "FAVORITE/data");
		structDB = new StructureDB(this);

		SearchList = (ListView) findViewById(R.id.timetable_search_listview);
		typespin = (Spinner) findViewById(R.id.timetable_search_spinner_selecttype);

		SearchTypeLayout1 = (LinearLayout) findViewById(R.id.timetable_search_layout_catesearch);
		searchspin1 = (Spinner) findViewById(R.id.timetable_search_spinner_col);
		searchspin2 = (Spinner) findViewById(R.id.timetable_search_spinner_dept);
		SearchBtn1 = (Button) findViewById(R.id.timetable_search_button_search);
		SearchTypeLayout1.setVisibility(View.GONE);

		SearchTypeLayout2 = (LinearLayout) findViewById(R.id.timetable_search_layout_etcsearch);
		SearchEditText = (EditText) findViewById(R.id.timetable_search_edittext_searchdata);
		SearchBtn2 = (Button) findViewById(R.id.timetable_search_button_etcsearch);
		campusspin = (Spinner) findViewById(R.id.timetable_search_spinner_campus);
		SearchTypeLayout2.setVisibility(View.GONE);

		if (DBEDIT.COLNAME_FIND_ALL(LecDB) == null) {
			Toast.makeText(this, "강의 목록이 없습니다.\n강의목록을 불러와주세요.",
					Toast.LENGTH_LONG).show();
			return;
		}
		_All_Col = DBEDIT.COLNAME_FIND_ALL(LecDB);
		_Col = DBEDIT.COLNAME_FIND_ALL(LecDB)[0];
		if (DBEDIT.PARAM_FIND_DEPT_BY_COL(LecDB, _Col) == null) {
			Toast.makeText(this, "강의 목록이 없습니다.\n강의목록을 불러와주세요.",
					Toast.LENGTH_LONG).show();
			return;
		}
		_Dept = DBEDIT.PARAM_FIND_DEPT_BY_COL(LecDB, _Col)[0];
		_Data = "";

		// 검색방법 스피너
		String[] _all_type = { "전공과목 찾기", "교양과목 찾기(안암)", "교양과목 찾기(세종)",
				"강의명으로 찾기", "교수명으로 찾기", "학수번호로 찾기" };
		adapter_typespin = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, _all_type);
		adapter_typespin
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typespin.setAdapter(adapter_typespin);
		typespin.setOnItemSelectedListener(mOnItemSelectedListener);

		String[] str = { "안암", "세종" };
		adapter_campusspin = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, str);
		adapter_campusspin
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		campusspin.setAdapter(adapter_campusspin);
		campusspin.setOnItemSelectedListener(mOnItemSelectedListener);

		// 학과별 검색 대학명 스피너
		adapter_searchspin1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, _All_Col);
		adapter_searchspin1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		searchspin1.setAdapter(adapter_searchspin1);
		searchspin1.setOnItemSelectedListener(mOnItemSelectedListener);

		SearchBtn1.setOnClickListener(mOnClickListener);
		SearchBtn2.setOnClickListener(mOnClickListener);

		SearchData = new ArrayList<String[]>();
		adapter = new ListViewCustomAdapter(Search.this,
				R.layout.timetable_search_listview, SearchData);
		SearchList.setAdapter(adapter);
		SearchList.setOnItemClickListener(mOnItemClickListener);
	}

}