package com.proinlab.kut;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import com.proinlab.kut.functions.DataBaseHelper;
import com.proinlab.kut.functions.MyListDB;
import com.proinlab.kut.functions.PREF;
import com.proinlab.networkmanager.HttpManager;
import com.proinlab.networkmanager.ParsingTool;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class DownloadService extends Service {

	private static final int THREAD_ID_GETCOLDATA = 0;
	private static final int THREAD_ID_GETDEPTDATA = 1;
	private static final int THREAD_ID_GETLECDATA = 2;
	private static final int THREAD_ID_GETCULTURE_CATEDATA = 3;
	private static final int THREAD_ID_GETCULTURE_DETAILDATA = 4;
	private static final int THREAD_ID_GETCULTURE_LECDATA = 5;

	private ParsingTool parsingtool = new ParsingTool();
	private HttpManager httpmanager = new HttpManager();
	public static DefaultHttpClient httpclient;
	private DataBaseHelper mHelper;
	private MyListDB FavoriteListDB;
	private DBEditFn DBEDIT = new DBEditFn();

	private String selectedTerm, InputYear;
	private ArrayList<String> ColIdList, colNameList;
	private ArrayList<String> DeptIdList, DeptNameList;
	private ArrayList<String> CultureIdList, cultureNameList;
	private ArrayList<String> CultureDetailIdList, cultureDetailNameList;

	private SharedPreferences pref;

	private String[] sejong_col = { "인문대학", "과학기술대학", "경상대학", "공공행정학부",
			"사회체육학부", "약학대학" };

	@Override
	public void onDestroy() {
		super.onDestroy();

		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(PREF.IS_DOWNLOADING, false);
		editor.commit();

	}

	@Override
	public void onCreate() {
		super.onCreate();

		mHelper = new DataBaseHelper(this);
		FavoriteListDB = new MyListDB(this, "FAVORITE/data");

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		httpclient = new DefaultHttpClient();

		Calendar mCalendar = Calendar.getInstance();
		pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
		selectedTerm = pref.getString(PREF.DATABASE_SEMISTER, "1R");
		InputYear = pref.getString(PREF.DATABASE_YEAR,
				Integer.toString(mCalendar.get(Calendar.YEAR)));

		Log.i("TAG", "start");
		getColDataThread();

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case THREAD_ID_GETCOLDATA:
				getDeptDataThread();
				break;
			case THREAD_ID_GETDEPTDATA:
				if (msg.arg1 == 100)
					getLecDataThread();
				break;
			case THREAD_ID_GETLECDATA:
				if (msg.arg1 == 100)
					getCultureCateDataThread();
				break;
			case THREAD_ID_GETCULTURE_CATEDATA:
				getCultureDetailDataThread();
				break;
			case THREAD_ID_GETCULTURE_DETAILDATA:
				if (msg.arg1 == 100)
					getCultureLecDataThread();
				break;
			case THREAD_ID_GETCULTURE_LECDATA:
				if (msg.arg1 == 100) {
					SharedPreferences.Editor editor = pref.edit();
					editor.putBoolean(PREF.IS_DOWNLOADING, false);
					editor.commit();
					Log.i("TAG", "end");
					stop();
				}
				break;
			}
		}
	};

	private void stop() {
		notification();
		this.stopSelf();
	}

	private void notification() {
		NotificationManager notify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification noti = new Notification(R.drawable.icon,
				"수강편람 다운로드가 끝났습니다.", System.currentTimeMillis());
		noti.defaults |= Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(DownloadService.this, Main.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setAction(Intent.ACTION_MAIN);
		PendingIntent content = PendingIntent.getActivity(DownloadService.this,
				0, intent, 0);
		noti.setLatestEventInfo(DownloadService.this, "수강편람 다운로드가 끝났습니다.",
				"수강편람 다운로드가 끝났습니다.", content);
		notify.notify(1, noti);
	}

	private void getColDataThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String htmldata = null;
				String addr = "http://sugang.korea.ac.kr:7080/lecture/LecMajorSub.jsp";
				htmldata = httpmanager.GET_DATA(httpclient, addr, HTTP.UTF_8);

				ColIdList = parsingtool.PARSE_SOURCELIST_BY_TAG(htmldata,
						"<option   value=\"", "\"");

				colNameList = parsingtool.PARSE_VALUELIST_BY_TAG(htmldata,
						"STYLE=\"color:black\"");

				if (!pref.getBoolean(PREF.IS_ANAM, false)) {
					for (int i = 0; i < colNameList.size(); i++) {
						boolean isTrue = true;

						for (int j = 0; j < sejong_col.length; j++) {
							if (colNameList.get(i).equals(sejong_col[j])) {
								isTrue = false;
							}
						}

						if (isTrue) {
							ColIdList.remove(i);
							colNameList.remove(i);
							i--;
						}
					}
				}

				if (!pref.getBoolean(PREF.IS_SEJONG, false)) {
					for (int i = 0; i < colNameList.size(); i++) {
						boolean isTrue = false;

						for (int j = 0; j < sejong_col.length; j++) {
							if (colNameList.get(i).equals(sejong_col[j])) {
								isTrue = true;
							}
						}

						if (isTrue) {
							ColIdList.remove(i);
							colNameList.remove(i);
							i--;
						}
					}
				}

				for (int i = 0; i < colNameList.size(); i++) {
					Log.i("TAG", colNameList.get(i));
				}

				mHandler.post(new Runnable() {
					public void run() {
						mHandler.sendEmptyMessage(THREAD_ID_GETCOLDATA);
					}
				});
			}
		}).start();
	}

	private void getDeptDataThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DBEDIT.DELETE_TABLE(FavoriteListDB, "MY_DATA");
				DBEDIT.DELETE_TABLE(mHelper, DataBaseHelper.DB_TABLE_PARAM_INFO);
				DBEDIT.DELETE_TABLE(mHelper, DataBaseHelper.DB_TABLE_COLNAME);
				DBEDIT.DELETE_TABLE(mHelper, DataBaseHelper.DB_TABLE_LECTURE);
				DBEDIT.DELETE_TABLE(mHelper,
						DataBaseHelper.DB_TABLE_CULTURE_PARAM_INFO);
				DBEDIT.DELETE_TABLE(mHelper,
						DataBaseHelper.DB_TABLE_CULTURE_NAME);
				DBEDIT.DELETE_TABLE(mHelper,
						DataBaseHelper.DB_TABLE_CULTURE_LECTURE);
				for (int i = 0; i < colNameList.size(); i++) {
					String htmldata = null;
					String addr = "http://sugang.korea.ac.kr:7080/lecture/LecDeptPopup.jsp?frm=frm_ms&colcd="
							+ ColIdList.get(i)
							+ "&dept=dept&year="
							+ InputYear
							+ "&term=" + selectedTerm;
					htmldata = httpmanager.GET_DATA(httpclient, addr,
							HTTP.UTF_8);
					DeptIdList = parsingtool.PARSE_SOURCELIST_BY_TAG(htmldata,
							"el.value =\"", "\"");
					DeptNameList = parsingtool.PARSE_SOURCELIST_BY_TAG(
							htmldata, "el.text = \"", "\"");

					DBEDIT.COLNAME_INSERT(mHelper, colNameList.get(i));
					for (int j = 0; j < DeptIdList.size(); j++) {
						DBEDIT.PARAM_INSERT(mHelper, ColIdList.get(i),
								colNameList.get(i), DeptIdList.get(j),
								DeptNameList.get(j));
					}

					final int process = i * 100 / colNameList.size();

					mHandler.post(new Runnable() {
						public void run() {
							Message msg = new Message();
							msg.what = THREAD_ID_GETDEPTDATA;
							msg.arg1 = process;
							mHandler.sendMessage(msg);
						}
					});
				}

				mHandler.post(new Runnable() {
					public void run() {
						Message msg = new Message();
						msg.what = THREAD_ID_GETDEPTDATA;
						msg.arg1 = 100;
						mHandler.sendMessage(msg);
					}
				});

			}
		}).start();
	}

	private void getLecDataThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				for (int i = 0; i < colNameList.size(); i++) {
					String htmldata = null;
					String[] DEPTSTRLIST = DBEDIT.PARAM_FIND_DEPT_BY_COL(
							mHelper, colNameList.get(i));
					final int process = i * 100 / colNameList.size();

					if (DEPTSTRLIST == null) {
						DEPTSTRLIST = new String[0];
					}

					for (int k = 0; k < DEPTSTRLIST.length; k++) {
						ArrayList<String> param = new ArrayList<String>();
						ArrayList<ArrayList<String>> postData = new ArrayList<ArrayList<String>>();

						String[] paramstr = { "yy", "tm", "col", "dept" };
						String[] valuestr = {
								InputYear,
								selectedTerm,
								DBEDIT.PARAM_FIND_DATA_BY_DEPT(mHelper,
										DEPTSTRLIST[k])[0],
								DBEDIT.PARAM_FIND_DATA_BY_DEPT(mHelper,
										DEPTSTRLIST[k])[2] };
						for (int j = 0; j < paramstr.length; j++) {
							param = new ArrayList<String>();
							param.add(paramstr[j]);
							param.add(valuestr[j]);
							postData.add(param);
						}

						String addr = "http://sugang.korea.ac.kr:7080/lecture/LecMajorSub.jsp";

						htmldata = httpmanager.POST_DATA(httpclient, addr,
								postData, HTTP.UTF_8);

						htmldata = parsingtool.get_start_location(htmldata,
								"<tr class=\"teble02\">");
						htmldata = htmldata.substring(1);
						htmldata = parsingtool.get_start_location(htmldata,
								"<tr");
						htmldata = htmldata.substring(1);

						String tmpdata = parsingtool.PARSE_SOURCE_BY_TAG(
								htmldata, "<tr>", "</tr>");
						while (tmpdata != null) {

							String getdata;

							String[] InsertRowData = new String[15];
							String[] tmpRow = new String[10];
							// 캠퍼스, 분반, 이수구분, 담당교수, 학점, 강의시간, 비고1~4

							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_01_CATE1] = DBEDIT
									.PARAM_FIND_DATA_BY_DEPT(mHelper,
											DEPTSTRLIST[k])[1];
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_02_CATE2] = DEPTSTRLIST[k];

							int j = 0;
							tmpdata = tmpdata.substring(1);
							tmpdata = parsingtool.get_start_location(tmpdata,
									"<td class=\"teble02_\">");

							// 강의명 파싱
							getdata = parsingtool.PARSE_SOURCE_BY_TAG(tmpdata,
									"<td>", "</td>");
							if (getdata != null) {
								if (getdata.equals(""))
									getdata = "none";
								tmpRow[j] = getdata;
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_07_LEC_NAME] = getdata;
							}
							// 강의계획서 링크
							getdata = parsingtool.PARSE_SOURCE_BY_TAG(tmpdata,
									"<a href=\"", "\"");
							if (getdata != null) {
								if (getdata.equals(""))
									getdata = "none";
								tmpRow[j] = getdata;
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_12_LINK] = getdata;
							}
							// 학수번호 파싱
							getdata = parsingtool.PARSE_VALUE_BY_TAG(tmpdata,
									"<a href=");
							if (getdata != null) {
								if (getdata.equals(""))
									getdata = "none";
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_05_LEC_ID] = getdata;
							}

							getdata = "start";
							while (getdata != null) {
								getdata = parsingtool.PARSE_SOURCE_BY_TAG(
										tmpdata, "<td class=\"teble02_\">",
										"</td>");
								tmpdata = tmpdata.substring(1);
								tmpdata = parsingtool.get_start_location(
										tmpdata, "<td class=\"teble02_\">");
								if (getdata != null) {
									if (getdata.equals(""))
										getdata = "none";
									tmpRow[j] = getdata;
									j++;
								}
							}
							// 캠퍼스, 분반, 이수구분, 담당교수, 학점, 강의시간, 비고1~4
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_04_CAMPUS] = tmpRow[0];
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_06_GROUP] = tmpRow[1];
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_03_CATE3] = tmpRow[2];
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_08_PROFESSOR] = tmpRow[3];
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_09_CREDIT] = tmpRow[4];
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_11_SCHEDULE] = tmpRow[5];
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_13_REMARKS1] = tmpRow[6];
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_14_REMARKS2] = tmpRow[7];
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_15_REMARKS3] = tmpRow[8];
							InsertRowData[DataBaseHelper.DB_INDEX_3ROW_16_REMARKS4] = tmpRow[9];

							for (int z = 0; z < InsertRowData.length; z++) {
								if (InsertRowData[z] == null) {
									InsertRowData = null;
									break;
								}
							}

							if (InsertRowData != null) {
								for (int l = 0; l < InsertRowData.length; l++) {
									InsertRowData[l] = parsingtool
											.REMOVE_UNNECESSORY(InsertRowData[l]);
								}
								DBEDIT.LECTURE_INSERT(mHelper,
										DataBaseHelper.DB_TABLE_LECTURE,
										InsertRowData);
							}
							htmldata = parsingtool.get_start_location(htmldata,
									"<tr");
							htmldata = htmldata.substring(1);
							htmldata = parsingtool.get_start_location(htmldata,
									"<tr");
							htmldata = htmldata.substring(1);
							tmpdata = parsingtool.PARSE_SOURCE_BY_TAG(htmldata,
									"<tr>", "</tr>");
						}

						final String curloc = DEPTSTRLIST[k];
						mHandler.post(new Runnable() {
							public void run() {
								Message msg = new Message();
								msg.what = THREAD_ID_GETLECDATA;
								msg.arg1 = process;
								msg.obj = curloc;
								mHandler.sendMessage(msg);
							}
						});
					}
				}

				mHandler.post(new Runnable() {
					public void run() {
						Message msg = new Message();
						msg.what = THREAD_ID_GETLECDATA;
						msg.arg1 = 100;
						mHandler.sendMessage(msg);
					}
				});

			}
		}).start();
	}

	private void getCultureCateDataThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String htmldata = null;
				String addr = "http://sugang.korea.ac.kr:7080/lecture/LecEtcSub.jsp";
				htmldata = httpmanager.GET_DATA(httpclient, addr, HTTP.UTF_8);
				htmldata = parsingtool.get_start_location(htmldata,
						"<select name=\"col\"");

				CultureIdList = parsingtool.PARSE_SOURCELIST_BY_TAG(htmldata,
						"<option value=\"", "\"");

				cultureNameList = parsingtool.PARSE_VALUELIST_BY_TAG(htmldata,
						"<option value=\"");

				mHandler.post(new Runnable() {
					public void run() {
						mHandler.sendEmptyMessage(THREAD_ID_GETCULTURE_CATEDATA);
					}
				});
			}
		}).start();
	}

	private void getCultureDetailDataThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < cultureNameList.size(); i++) {
					String htmldata = null;
					String addr = "http://sugang.korea.ac.kr:7080/lecture/LecDeptPopup.jsp?frm=frm_ets&colcd="
							+ CultureIdList.get(i) + "&dept=dept";

					htmldata = httpmanager.GET_DATA(httpclient, addr,
							HTTP.UTF_8);
					CultureDetailIdList = parsingtool.PARSE_SOURCELIST_BY_TAG(
							htmldata, "el.value =\"", "\"");
					cultureDetailNameList = parsingtool
							.PARSE_SOURCELIST_BY_TAG(htmldata, "el.text = \"",
									"\"");
					DBEDIT.CULTURE_NAME_INSERT(mHelper, cultureNameList.get(i));
					for (int j = 0; j < CultureDetailIdList.size(); j++) {
						DBEDIT.CULTURE_PARAM_INSERT(mHelper,
								CultureIdList.get(i), cultureNameList.get(i),
								CultureDetailIdList.get(j),
								cultureDetailNameList.get(j));
					}

					final int process = i * 100 / cultureNameList.size();

					mHandler.post(new Runnable() {
						public void run() {
							Message msg = new Message();
							msg.what = THREAD_ID_GETCULTURE_DETAILDATA;
							msg.arg1 = process;
							mHandler.sendMessage(msg);
						}
					});
				}

				mHandler.post(new Runnable() {
					public void run() {
						Message msg = new Message();
						msg.what = THREAD_ID_GETCULTURE_DETAILDATA;
						msg.arg1 = 100;
						mHandler.sendMessage(msg);
					}
				});

			}
		}).start();
	}

	private void getCultureLecDataThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 안암
				if (pref.getBoolean(PREF.IS_ANAM, false))
					for (int i = 0; i < cultureNameList.size(); i++) {
						String htmldata = null;
						String[] DETAILSTRLIST = DBEDIT
								.CULTURE_PARAM_FIND_DETAIL_BY_CULTURE(mHelper,
										cultureNameList.get(i));

						if (DETAILSTRLIST == null) {
							DETAILSTRLIST = new String[0];
						} else if (DETAILSTRLIST.length == 0) {
							DETAILSTRLIST = new String[1];
							DETAILSTRLIST[0] = "none";
						}

						for (int k = 0; k < DETAILSTRLIST.length; k++) {
							ArrayList<String> param = new ArrayList<String>();
							ArrayList<ArrayList<String>> postData = new ArrayList<ArrayList<String>>();
							int ps;
							if (i == 0)
								ps = k * 50 / DETAILSTRLIST.length;
							else
								ps = 50;
							final int process = ps;
							String value5;
							String[] strarr = DBEDIT
									.CULTURE_PARAM_FIND_DATA_BY_DETAIL(mHelper,
											DETAILSTRLIST[k]);
							if (strarr == null) {
								value5 = null;
							} else {
								value5 = strarr[2];
							}
							String[] paramstr = { "yy", "tm", "campus", "col",
									"dept" };
							String[] valuestr = { InputYear, selectedTerm, "1",
									CultureIdList.get(i), value5 };
							for (int j = 0; j < paramstr.length; j++) {
								param = new ArrayList<String>();
								param.add(paramstr[j]);
								param.add(valuestr[j]);
								postData.add(param);
							}

							String addr = "http://sugang.korea.ac.kr:7080/lecture/LecEtcSub.jsp";

							htmldata = httpmanager.POST_DATA(httpclient, addr,
									postData, HTTP.UTF_8);
							htmldata = parsingtool.get_start_location(htmldata,
									"<tr class=\"teble02\">");
							htmldata = htmldata.substring(1);
							htmldata = parsingtool.get_start_location(htmldata,
									"<tr");
							htmldata = htmldata.substring(1);

							String tmpdata = parsingtool.PARSE_SOURCE_BY_TAG(
									htmldata, "<tr>", "</tr>");

							while (tmpdata != null) {

								String getdata;

								String[] InsertRowData = new String[15];
								String[] tmpRow = new String[10];
								// 분반, 이수구분, 담당교수, 학점, 강의시간, 비고1~4

								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_01_CATE1] = cultureNameList
										.get(i);
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_03_CATE3] = "none";
								if (DETAILSTRLIST[k] == null)
									DETAILSTRLIST[k] = "none";
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_02_CATE2] = DETAILSTRLIST[k];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_04_CAMPUS] = "안암";
								int j = 0;
								tmpdata = tmpdata.substring(1);
								tmpdata = parsingtool.get_start_location(
										tmpdata, "<tr class=\"teble02\">");

								// 강의명 파싱
								getdata = parsingtool.PARSE_SOURCE_BY_TAG(
										tmpdata, "<td height=\"25\">", "</td>");
								if (getdata != null) {
									if (getdata.equals(""))
										getdata = "none";
									InsertRowData[DataBaseHelper.DB_INDEX_3ROW_07_LEC_NAME] = getdata;
								}
								// 강의계획서 링크
								getdata = parsingtool.PARSE_SOURCE_BY_TAG(
										tmpdata, "<a href=\"", "\"");
								if (getdata != null) {
									if (getdata.equals(""))
										getdata = "none";
									InsertRowData[DataBaseHelper.DB_INDEX_3ROW_12_LINK] = getdata;
								}
								// 학수번호 파싱
								getdata = parsingtool.PARSE_VALUE_BY_TAG(
										tmpdata, "<a href=");
								if (getdata != null) {
									if (getdata.equals(""))
										getdata = "none";
									InsertRowData[DataBaseHelper.DB_INDEX_3ROW_05_LEC_ID] = getdata;
								}

								getdata = "start";

								while (getdata != null) {
									if (j == 9)
										getdata = parsingtool
												.PARSE_SOURCE_BY_TAG(
														tmpdata,
														"<td class=\"teble02_\">",
														"</td>");
									else
										getdata = parsingtool
												.PARSE_SOURCE_BY_TAG(
														tmpdata,
														"<td class=\"teble02_\" height=\"25\">",
														"</td>");
									tmpdata = tmpdata.substring(1);
									tmpdata = parsingtool
											.get_start_location(tmpdata,
													"<td class=\"teble02_\" height=\"25\">");
									if (getdata != null) {
										if (getdata.equals(""))
											getdata = "none";
										tmpRow[j] = getdata;
										j++;
									}
								}
								// 캠퍼스, 분반, 이수구분, 담당교수, 학점, 강의시간, 비고1~4
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_06_GROUP] = tmpRow[2];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_08_PROFESSOR] = tmpRow[3];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_09_CREDIT] = tmpRow[4];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_11_SCHEDULE] = tmpRow[5];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_13_REMARKS1] = tmpRow[6];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_14_REMARKS2] = tmpRow[7];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_15_REMARKS3] = tmpRow[8];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_16_REMARKS4] = tmpRow[9];

								for (int z = 0; z < InsertRowData.length; z++) {
									if (InsertRowData[z] == null) {
										InsertRowData = null;
										break;
									}
								}

								if (InsertRowData != null) {
									for (int l = 0; l < InsertRowData.length; l++) {
										InsertRowData[l] = parsingtool
												.REMOVE_UNNECESSORY(InsertRowData[l]);
									}
									DBEDIT.LECTURE_INSERT(
											mHelper,
											DataBaseHelper.DB_TABLE_CULTURE_LECTURE,
											InsertRowData);
								}
								htmldata = parsingtool.get_start_location(
										htmldata, "<tr");
								htmldata = htmldata.substring(1);
								htmldata = parsingtool.get_start_location(
										htmldata, "<tr");
								htmldata = htmldata.substring(1);
								tmpdata = parsingtool.PARSE_SOURCE_BY_TAG(
										htmldata, "<tr>", "</tr>");
							}

							final String curloc = DETAILSTRLIST[k];
							mHandler.post(new Runnable() {
								public void run() {
									Message msg = new Message();
									msg.what = THREAD_ID_GETCULTURE_LECDATA;
									msg.arg1 = process;
									msg.obj = curloc;
									mHandler.sendMessage(msg);
								}
							});
						}
					}

				// 세종
				if (pref.getBoolean(PREF.IS_SEJONG, false))
					for (int i = 0; i < cultureNameList.size(); i++) {
						String htmldata = null;
						String[] DETAILSTRLIST = DBEDIT
								.CULTURE_PARAM_FIND_DETAIL_BY_CULTURE(mHelper,
										cultureNameList.get(i));

						if (DETAILSTRLIST == null) {
							DETAILSTRLIST = new String[0];
						} else if (DETAILSTRLIST.length == 0) {
							DETAILSTRLIST = new String[1];
							DETAILSTRLIST[0] = "none";
						}

						for (int k = 0; k < DETAILSTRLIST.length; k++) {
							ArrayList<String> param = new ArrayList<String>();
							ArrayList<ArrayList<String>> postData = new ArrayList<ArrayList<String>>();

							int ps;
							if (i == 0)
								ps = 50 + k * 50 / DETAILSTRLIST.length;
							else
								ps = 99;
							final int process = ps;

							String value5;
							String[] strarr = DBEDIT
									.CULTURE_PARAM_FIND_DATA_BY_DETAIL(mHelper,
											DETAILSTRLIST[k]);
							if (strarr == null) {
								value5 = null;
							} else {
								value5 = strarr[2];
							}
							String[] paramstr = { "yy", "tm", "campus", "col",
									"dept" };
							String[] valuestr = { InputYear, selectedTerm, "2",
									CultureIdList.get(i), value5 };
							for (int j = 0; j < paramstr.length; j++) {
								param = new ArrayList<String>();
								param.add(paramstr[j]);
								param.add(valuestr[j]);
								postData.add(param);
							}

							String addr = "http://sugang.korea.ac.kr:7080/lecture/LecEtcSub.jsp";

							htmldata = httpmanager.POST_DATA(httpclient, addr,
									postData, HTTP.UTF_8);
							htmldata = parsingtool.get_start_location(htmldata,
									"<tr class=\"teble02\">");
							htmldata = htmldata.substring(1);
							htmldata = parsingtool.get_start_location(htmldata,
									"<tr");
							htmldata = htmldata.substring(1);

							String tmpdata = parsingtool.PARSE_SOURCE_BY_TAG(
									htmldata, "<tr>", "</tr>");

							while (tmpdata != null) {

								String getdata;

								String[] InsertRowData = new String[15];
								String[] tmpRow = new String[10];
								// 분반, 이수구분, 담당교수, 학점, 강의시간, 비고1~4

								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_01_CATE1] = cultureNameList
										.get(i);
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_03_CATE3] = "none";
								if (DETAILSTRLIST[k] == null)
									DETAILSTRLIST[k] = "none";
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_02_CATE2] = DETAILSTRLIST[k];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_04_CAMPUS] = "세종";
								int j = 0;
								tmpdata = tmpdata.substring(1);
								tmpdata = parsingtool.get_start_location(
										tmpdata, "<tr class=\"teble02\">");

								// 강의명 파싱
								getdata = parsingtool.PARSE_SOURCE_BY_TAG(
										tmpdata, "<td height=\"25\">", "</td>");
								if (getdata != null) {
									if (getdata.equals(""))
										getdata = "none";
									InsertRowData[DataBaseHelper.DB_INDEX_3ROW_07_LEC_NAME] = getdata;
								}
								// 강의계획서 링크
								getdata = parsingtool.PARSE_SOURCE_BY_TAG(
										tmpdata, "<a href=\"", "\"");
								if (getdata != null) {
									if (getdata.equals(""))
										getdata = "none";
									InsertRowData[DataBaseHelper.DB_INDEX_3ROW_12_LINK] = getdata;
								}
								// 학수번호 파싱
								getdata = parsingtool.PARSE_VALUE_BY_TAG(
										tmpdata, "<a href=");
								if (getdata != null) {
									if (getdata.equals(""))
										getdata = "none";
									InsertRowData[DataBaseHelper.DB_INDEX_3ROW_05_LEC_ID] = getdata;
								}

								getdata = "start";

								while (getdata != null) {
									if (j == 9)
										getdata = parsingtool
												.PARSE_SOURCE_BY_TAG(
														tmpdata,
														"<td class=\"teble02_\">",
														"</td>");
									else
										getdata = parsingtool
												.PARSE_SOURCE_BY_TAG(
														tmpdata,
														"<td class=\"teble02_\" height=\"25\">",
														"</td>");
									tmpdata = tmpdata.substring(1);
									tmpdata = parsingtool
											.get_start_location(tmpdata,
													"<td class=\"teble02_\" height=\"25\">");
									if (getdata != null) {
										if (getdata.equals(""))
											getdata = "none";
										tmpRow[j] = getdata;
										j++;
									}
								}
								// 캠퍼스, 분반, 이수구분, 담당교수, 학점, 강의시간, 비고1~4
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_06_GROUP] = tmpRow[2];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_08_PROFESSOR] = tmpRow[3];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_09_CREDIT] = tmpRow[4];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_11_SCHEDULE] = tmpRow[5];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_13_REMARKS1] = tmpRow[6];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_14_REMARKS2] = tmpRow[7];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_15_REMARKS3] = tmpRow[8];
								InsertRowData[DataBaseHelper.DB_INDEX_3ROW_16_REMARKS4] = tmpRow[9];

								for (int z = 0; z < InsertRowData.length; z++) {
									if (InsertRowData[z] == null) {
										InsertRowData = null;
										break;
									}
								}

								if (InsertRowData != null) {
									for (int l = 0; l < InsertRowData.length; l++) {
										InsertRowData[l] = parsingtool
												.REMOVE_UNNECESSORY(InsertRowData[l]);
									}
									DBEDIT.LECTURE_INSERT(
											mHelper,
											DataBaseHelper.DB_TABLE_CULTURE_LECTURE,
											InsertRowData);
								}
								htmldata = parsingtool.get_start_location(
										htmldata, "<tr");
								htmldata = htmldata.substring(1);
								htmldata = parsingtool.get_start_location(
										htmldata, "<tr");
								htmldata = htmldata.substring(1);
								tmpdata = parsingtool.PARSE_SOURCE_BY_TAG(
										htmldata, "<tr>", "</tr>");
							}

							final String curloc = DETAILSTRLIST[k];
							mHandler.post(new Runnable() {
								public void run() {
									Message msg = new Message();
									msg.what = THREAD_ID_GETCULTURE_LECDATA;
									msg.arg1 = process;
									msg.obj = curloc;
									mHandler.sendMessage(msg);
								}
							});
						}
					}

				mHandler.post(new Runnable() {
					public void run() {
						Message msg = new Message();
						msg.what = THREAD_ID_GETCULTURE_LECDATA;
						msg.arg1 = 100;
						mHandler.sendMessage(msg);
					}
				});

			}
		}).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
