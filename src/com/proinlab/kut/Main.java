package com.proinlab.kut;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.proinlab.kut.functions.MyListDB;
import com.proinlab.kut.functions.PREF;
import com.proinlab.kut.functions.StructureDB;

@SuppressLint("HandlerLeak")
public class Main extends TabActivity {

	private LinearLayout contents_layout, menu_layout, title_layout;
	private Animation aniShow, aniHide;
	private Button menu_btn;
	private boolean isMenu = false;
	private boolean isList = false;
	private int displayWidth, displayHeight;

	private LinearLayout.LayoutParams llp;
	private RelativeLayout.LayoutParams rlp;

	private ImageButton SettingBtn, SubMenuBtn;
	private TextView TitleText;

	private ImageButton MENU_TABLE, MENU_MYLIST, MENU_FAVORITE, MENU_SEARCH,
			MENU_LOAD, MENU_DEV, MENU_SHARE, MENU_SETTING;

	private ListView TimeTableNameLIST;

	private int CONTENT_NOW = 0;
	private static final int CONTENT_TIMETABLE = 0;
	private static final int CONTENT_MYLIST = 1;
	private static final int CONTENT_FAVORITE = 2;
	private static final int CONTENT_SEARCH = 3;
	private static final int CONTENT_LOAD = 4;
	private static final int CONTENT_DEV = 5;
	private static final int CONTENT_SETTING = 6;

	private TabHost mTabHost = null;

	private String SelectedData = "시간표 1";

	private String sharedData = "NO";

	private int processing_count = 0;

	public void onPause() {
		super.onPause();
		alert = null;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (alert.isShowing()) {
				alert.dismiss();
			}
		}
	};

	private SharedPreferences pref;

	public void onResume() {
		super.onResume();

		setDialog();

		pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
		if (pref.getBoolean(PREF.IS_DOWNLOADING, false)) {
			alert.show();

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

		NotificationManager notify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notify.cancel(1);

		if (sharedData == null)
			return;

		Intent intent = getIntent();
		if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			sharedData = intent.getData().getPath();
			showDialog(3);
		}
	}

	public static AlertDialog alert = null;

	private void setDialog() {
		LinearLayout linear = (LinearLayout) View.inflate(this,
				R.layout.loading_dialog, null);
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.re_main);

		SharedPreferences pref = getSharedPreferences("pref",
				Activity.MODE_PRIVATE);
		SelectedData = pref.getString(PREF.MYLEC_NAME, "시간표 1");
		processing_count = pref.getInt(PREF.PROCESSING_COUNT, 0);

		if (processing_count == 0) {
			DeleteDir(Environment.getExternalStorageDirectory().toString()
					+ "/.KUT/");
		}
		Log.i("TAG", Integer.toString(processing_count));

		processing_count++;
		if (processing_count == 0)
			processing_count--;
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(PREF.PROCESSING_COUNT, processing_count);
		editor.commit();

		findViewByIdList();
		setMainDisplay();
		setTab();

		MENU_TABLE.setOnClickListener(mOnclick);
		MENU_MYLIST.setOnClickListener(mOnclick);
		MENU_FAVORITE.setOnClickListener(mOnclick);
		MENU_SEARCH.setOnClickListener(mOnclick);
		MENU_LOAD.setOnClickListener(mOnclick);
		MENU_DEV.setOnClickListener(mOnclick);
		MENU_SHARE.setOnClickListener(mOnclick);
		MENU_SETTING.setOnClickListener(mOnclick);

		setListMenu();
		setSubMenuBtn();
		setDataBase();

		if (pref.getString(PREF.DATABASE_DATE, "none").equals("none")) {
			CONTENT_NOW = CONTENT_LOAD;
			mTabHost.setCurrentTab(CONTENT_NOW);
			TitleText.setBackgroundDrawable(null);
			TitleText.setText("수강편람 불러오기");
		}

	}

	private StructureDB structDB;
	private DBEditFn DBEDIT = new DBEditFn();
	private ArrayList<String> DBARRAY = new ArrayList<String>();

	private void setDataBase() {
		structDB = new StructureDB(this);

		ArrayList<String[]> tmpArr = DBEDIT.STRUCTURE_FIND_ALL(structDB);

		if (tmpArr == null) {
			DBEDIT.STRUCTURE_INSERT(structDB, "시간표 1", "시간표 1");
			SelectedData = "시간표 1";
			SharedPreferences pref = getSharedPreferences("pref",
					Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString(PREF.MYLEC_NAME, SelectedData);
			editor.commit();
			TitleText.setText(SelectedData);
		} else {
			SelectedData = tmpArr.get(0)[0];
			SharedPreferences pref = getSharedPreferences("pref",
					Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString(PREF.MYLEC_NAME, SelectedData);
			editor.commit();
			TitleText.setText(SelectedData);
			mTabHost.setCurrentTab(4);
			mTabHost.setCurrentTab(CONTENT_NOW);
		}

		tmpArr = DBEDIT.STRUCTURE_FIND_ALL(structDB);
		if (tmpArr != null)
			for (int i = 0; i < tmpArr.size(); i++) {
				DBARRAY.add(tmpArr.get(i)[0]);
			}
		showList();

	}

	private void setListMenu() {
		TitleText.setText(SelectedData);
		TitleText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isList) {
					isList = false;
					TimeTableNameLIST.setVisibility(View.GONE);
					if (CONTENT_NOW == CONTENT_TIMETABLE)
						SettingBtn.setBackgroundResource(R.drawable.settingbtn);
					else if (CONTENT_NOW == CONTENT_MYLIST)
						SettingBtn.setBackgroundResource(R.drawable.subbtn);
					SubMenuBtn.setBackgroundResource(R.drawable.sharebtn);
				} else {
					showList();
					isList = true;
					TimeTableNameLIST.setVisibility(View.VISIBLE);
					SettingBtn.setBackgroundResource(R.drawable.cancle_btn);
					SubMenuBtn.setBackgroundResource(R.drawable.addprojectbtn);
				}
			}
		});
	}

	private int DELETE_LIST_ARG = -1;

	private void showList() {
		ArrayAdapter<String> ARRAYADAPTER_LIST = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, DBARRAY);
		TimeTableNameLIST.setAdapter(ARRAYADAPTER_LIST);
		TimeTableNameLIST.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SelectedData = DBARRAY.get(arg2);
				SharedPreferences pref = getSharedPreferences("pref",
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putString(PREF.MYLEC_NAME, SelectedData);
				editor.commit();

				isList = false;
				TimeTableNameLIST.setVisibility(View.GONE);
				if (CONTENT_NOW == CONTENT_TIMETABLE)
					SettingBtn.setBackgroundResource(R.drawable.settingbtn);
				else if (CONTENT_NOW == CONTENT_MYLIST)
					SettingBtn.setBackgroundResource(R.drawable.subbtn);
				SubMenuBtn.setBackgroundResource(R.drawable.sharebtn);

				TitleText.setText(SelectedData);
				mTabHost.setCurrentTab(5);
				mTabHost.setCurrentTab(CONTENT_NOW);
			}
		});
		TimeTableNameLIST
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						DELETE_LIST_ARG = arg2;
						showDialog(2);
						return false;
					}
				});
	}

	private void findViewByIdList() {
		menu_layout = (LinearLayout) findViewById(R.id.re_main_menu_layout);
		title_layout = (LinearLayout) findViewById(R.id.re_main_title_layout);
		contents_layout = (LinearLayout) findViewById(R.id.re_main_contents_layout);
		
		menu_btn = (Button) findViewById(R.id.re_main_title_menubtn);
		TitleText = (TextView) findViewById(R.id.re_main_title_text);
		SettingBtn = (ImageButton) findViewById(R.id.re_main_title_setting);
		SubMenuBtn = (ImageButton) findViewById(R.id.re_main_title_submenu2);
		SubMenuBtn.setVisibility(View.GONE);

		MENU_TABLE = (ImageButton) findViewById(R.id.re_main_menu_timetable);
		MENU_MYLIST = (ImageButton) findViewById(R.id.re_main_menu_mylist);
		MENU_FAVORITE = (ImageButton) findViewById(R.id.re_main_menu_favoritelist);
		MENU_SEARCH = (ImageButton) findViewById(R.id.re_main_menu_search);
		MENU_LOAD = (ImageButton) findViewById(R.id.re_main_menu_loaddata);
		MENU_DEV = (ImageButton) findViewById(R.id.re_main_menu_developer);
		MENU_SHARE = (ImageButton) findViewById(R.id.re_main_menu_share);
		MENU_SETTING = (ImageButton) findViewById(R.id.re_main_menu_setting);

		TimeTableNameLIST = (ListView) findViewById(R.id.re_main_timetablenamelist);
		TimeTableNameLIST.setVisibility(View.GONE);		
	}

	private void setSubMenuBtn() {
		SettingBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (CONTENT_NOW) {
				case CONTENT_TIMETABLE:
					if (isList) {
						isList = false;
						TimeTableNameLIST.setVisibility(View.GONE);
						SettingBtn.setBackgroundResource(R.drawable.settingbtn);
					} else {
						showDialog(0);
					}
					break;
				case CONTENT_MYLIST:
					if (isList) {
						isList = false;
						TimeTableNameLIST.setVisibility(View.GONE);
						SettingBtn.setBackgroundResource(R.drawable.subbtn);
						SubMenuBtn.setBackgroundResource(R.drawable.sharebtn);
					} else {
						showList();
						isList = true;
						TimeTableNameLIST.setVisibility(View.VISIBLE);
						SettingBtn.setBackgroundResource(R.drawable.cancle_btn);
						SubMenuBtn
								.setBackgroundResource(R.drawable.addprojectbtn);
					}
					break;
				case CONTENT_FAVORITE:
					break;
				case CONTENT_SEARCH:
					break;
				case CONTENT_LOAD:
					break;
				case CONTENT_DEV:
					break;
				}
			}
		});

		SubMenuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (CONTENT_NOW) {
				case CONTENT_TIMETABLE:
					break;
				case CONTENT_MYLIST:
					if (isList) {
						showDialog(1);
					} else {
						String orgdir = MyListDB.DATABASE_DIR + SelectedData
								+ ".db";
						String cpydir = MyListDB.DATABASE_DIR + "Share/"
								+ SelectedData + ".mkut";

						File FILE = new File(orgdir);
						if (!FILE.exists()) {
							Toast.makeText(Main.this, "추가 된 강의가 없습니다.",
									Toast.LENGTH_SHORT).show();
							break;
						}

						FILE = new File(MyListDB.DATABASE_DIR + "Share/");
						if (!FILE.exists())
							while (FILE.mkdirs())
								;

						File orgFile = new File(orgdir);
						copyFile(orgFile, cpydir);

						Intent it = new Intent(Intent.ACTION_SEND);
						it.putExtra(Intent.EXTRA_STREAM,
								Uri.parse("file://" + cpydir));
						it.setType("application/dropbox");
						startActivity(it);

					}
					break;
				case CONTENT_FAVORITE:
					break;
				case CONTENT_SEARCH:
					break;
				case CONTENT_LOAD:
					break;
				case CONTENT_DEV:
					break;
				}
			}
		});
	}

	private void setTab() {

		rlp = new RelativeLayout.LayoutParams(displayWidth,
				LayoutParams.MATCH_PARENT);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setLayoutParams(rlp);

		TabSpec Spec1 = mTabHost.newTabSpec("");
		Spec1.setIndicator("시간표");
		Spec1.setContent(new Intent(this, TimeTable.class));
		mTabHost.addTab(Spec1);

		TabSpec Spec2 = mTabHost.newTabSpec("");
		Spec2.setIndicator("내강의");
		Spec2.setContent(new Intent(this, MyLecList.class));
		mTabHost.addTab(Spec2);

		TabSpec Spec6 = mTabHost.newTabSpec("");
		Spec6.setIndicator("관심과목");
		Spec6.setContent(new Intent(this, FavoriteLecList.class));
		mTabHost.addTab(Spec6);

		TabSpec Spec3 = mTabHost.newTabSpec("");
		Spec3.setIndicator("검색");
		Spec3.setContent(new Intent(this, Search.class));
		mTabHost.addTab(Spec3);

		TabSpec Spec4 = mTabHost.newTabSpec("");
		Spec4.setIndicator("수강편람");
		Spec4.setContent(new Intent(this, GetLectureList.class));
		mTabHost.addTab(Spec4);

		TabSpec Spec5 = mTabHost.newTabSpec("");
		Spec5.setIndicator("개발자 정보");
		Spec5.setContent(new Intent(this, DevInfo.class));
		mTabHost.addTab(Spec5);

		TabSpec Spec7 = mTabHost.newTabSpec("");
		Spec7.setIndicator("설정");
		Spec7.setContent(new Intent(this, Setting.class));
		mTabHost.addTab(Spec7);

		mTabHost.setCurrentTab(CONTENT_NOW);

	}

	private View.OnClickListener mOnclick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			isMenu = false;
			rlp = new RelativeLayout.LayoutParams(displayWidth, displayHeight);
			rlp.setMargins(0, 0, 0, 0);
			contents_layout.setLayoutParams(rlp);
			contents_layout.startAnimation(aniHide);
			TitleText.setOnClickListener(null);
			SettingBtn.setVisibility(View.GONE);
			SubMenuBtn.setVisibility(View.GONE);
			SettingBtn.setBackgroundResource(R.drawable.settingbtn);

			isList = false;
			TimeTableNameLIST.setVisibility(View.GONE);

			switch (v.getId()) {
			case R.id.re_main_menu_timetable:
				TitleText.setBackgroundResource(R.drawable.title_text_bg);
				CONTENT_NOW = CONTENT_TIMETABLE;
				setListMenu();
				SettingBtn.setVisibility(View.VISIBLE);
				mTabHost.setCurrentTab(CONTENT_NOW);
				break;
			case R.id.re_main_menu_mylist:
				TitleText.setBackgroundResource(R.drawable.title_text_bg);
				CONTENT_NOW = CONTENT_MYLIST;
				setListMenu();
				SettingBtn.setVisibility(View.VISIBLE);
				SubMenuBtn.setVisibility(View.VISIBLE);
				showList();
				isList = true;
				TimeTableNameLIST.setVisibility(View.VISIBLE);
				SettingBtn.setBackgroundResource(R.drawable.cancle_btn);
				SubMenuBtn.setBackgroundResource(R.drawable.addprojectbtn);
				mTabHost.setCurrentTab(CONTENT_NOW);
				break;
			case R.id.re_main_menu_favoritelist:
				TitleText.setBackgroundDrawable(null);
				TitleText.setText("관심강의 목록");
				CONTENT_NOW = CONTENT_FAVORITE;
				mTabHost.setCurrentTab(CONTENT_NOW);
				break;
			case R.id.re_main_menu_search:
				TitleText.setBackgroundDrawable(null);
				TitleText.setText("강의 검색");
				CONTENT_NOW = CONTENT_SEARCH;
				mTabHost.setCurrentTab(CONTENT_NOW);
				break;
			case R.id.re_main_menu_loaddata:
				TitleText.setBackgroundDrawable(null);
				TitleText.setText("수강편람 불러오기");
				CONTENT_NOW = CONTENT_LOAD;
				mTabHost.setCurrentTab(CONTENT_NOW);
				break;
			case R.id.re_main_menu_developer:
				TitleText.setBackgroundDrawable(null);
				TitleText.setText("개발자 정보");
				CONTENT_NOW = CONTENT_DEV;
				mTabHost.setCurrentTab(CONTENT_NOW);
				break;
			case R.id.re_main_menu_share:
				showDialog(5);
				break;
			case R.id.re_main_menu_setting:
				TitleText.setBackgroundDrawable(null);
				TitleText.setText("설정");
				CONTENT_NOW = CONTENT_SETTING;
				mTabHost.setCurrentTab(CONTENT_NOW);
				break;
			}

		}
	};

	private void setMainDisplay() {
		Display display = getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		if (displayWidth > displayHeight) {
			displayHeight = display.getWidth();
			displayWidth = display.getHeight();
		}

		contents_layout.setVisibility(View.VISIBLE);

		aniShow = AnimationUtils.loadAnimation(this, R.anim.left_in);
		aniHide = AnimationUtils.loadAnimation(this, R.anim.left_out);
		aniShow.setAnimationListener(mAniListener);

		llp = new LinearLayout.LayoutParams(displayWidth,
				displayWidth * 15 / 100);
		title_layout.setLayoutParams(llp);
		llp = new LinearLayout.LayoutParams(displayWidth * 15 / 100,
				displayWidth * 15 / 100);
		menu_btn.setLayoutParams(llp);
		rlp = new RelativeLayout.LayoutParams(displayWidth * 85 / 100,
				displayHeight);
		menu_layout.setLayoutParams(rlp);

		menu_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isMenu) {
					isMenu = false;
					rlp = new RelativeLayout.LayoutParams(displayWidth,
							displayHeight);
					rlp.setMargins(0, 0, 0, 0);
					contents_layout.setLayoutParams(rlp);
					contents_layout.startAnimation(aniHide);
				} else {
					isMenu = true;
					contents_layout.startAnimation(aniShow);
				}
			}
		});

		contents_layout.setOnTouchListener(menuOnTouchListener);

	}

	private AnimationListener mAniListener = new AnimationListener() {

		@Override
		public void onAnimationEnd(Animation animation) {
			rlp = new RelativeLayout.LayoutParams(displayWidth, displayHeight);
			rlp.setMargins(displayWidth * 85 / 100, 0, 0, 0);
			contents_layout.setLayoutParams(rlp);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationStart(Animation animation) {

		}
	};

	int PreTouchPosX = 0;
	int PreTouchPosY = 0;
	private View.OnTouchListener menuOnTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (PreTouchPosX == 0)
				PreTouchPosX = (int) event.getX();
			if (PreTouchPosY == 0)
				PreTouchPosY = (int) event.getY();
			if (event.getAction() == MotionEvent.ACTION_UP) {
				int nTouchPosX = (int) event.getX();
				int nTouchPosY = (int) event.getY();
				if (displayHeight / 10 < Math.abs(nTouchPosY - PreTouchPosY)) {
					PreTouchPosX = 0;
					PreTouchPosY = 0;
					return false;
				}

				if (isMenu) {
					if (PreTouchPosX > nTouchPosX) {
						if (PreTouchPosX - nTouchPosX > displayWidth * 5 / 100) {
							isMenu = false;
							rlp = new RelativeLayout.LayoutParams(displayWidth,
									displayHeight);
							rlp.setMargins(0, 0, 0, 0);
							contents_layout.setLayoutParams(rlp);
							contents_layout.startAnimation(aniHide);
						}
					}
				} else if (PreTouchPosX < nTouchPosX) {
					if (nTouchPosX - PreTouchPosX > displayWidth * 2 / 10) {
						isMenu = true;
						contents_layout.startAnimation(aniShow);
					}
				}
				PreTouchPosX = 0;
				PreTouchPosY = 0;
			}
			return true;
		}
	};

	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			String[] arr = { "월 ~ 금", "월 ~ 토", "공유", "사진으로 저장" };
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
								mTabHost.setCurrentTab(2);
								mTabHost.setCurrentTab(CONTENT_NOW);
								break;
							case 1:
								pref = getSharedPreferences("pref",
										Activity.MODE_PRIVATE);
								editor = pref.edit();
								editor.putBoolean(PREF.TIMETABLE_DAY, true);
								editor.commit();
								mTabHost.setCurrentTab(2);
								mTabHost.setCurrentTab(CONTENT_NOW);
								break;
							case 2:
								String orgdir = MyListDB.DATABASE_DIR
										+ SelectedData + ".db";
								String cpydir = MyListDB.DATABASE_DIR
										+ "Share/" + SelectedData + ".mkut";

								File FILE = new File(orgdir);
								if (!FILE.exists()) {
									Toast.makeText(Main.this, "추가 된 강의가 없습니다.",
											Toast.LENGTH_SHORT).show();
									break;
								}

								FILE = new File(MyListDB.DATABASE_DIR
										+ "Share/");
								if (!FILE.exists())
									while (FILE.mkdirs())
										;

								File orgFile = new File(orgdir);
								copyFile(orgFile, cpydir);

								Intent it = new Intent(Intent.ACTION_SEND);
								it.putExtra(Intent.EXTRA_STREAM,
										Uri.parse("file://" + cpydir));
								it.setType("application/dropbox");
								startActivity(it);
								break;
							case 3:
								Intent photointent = new Intent(Main.this,
										ScreenShot.class);
								startActivity(photointent);
								break;
							}
						}
					});
			AlertDialog alert = alt_bld.create();
			alert.setTitle("시간표 설정");
			alert.setCanceledOnTouchOutside(true);
			return alert;
		} else if (id == 1) {
			final LinearLayout linear = (LinearLayout) View.inflate(this,
					R.layout.add_list_dialog, null);
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setTitle("목록 추가")
					.setView(linear)
					.setPositiveButton("확인",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText NAME = (EditText) linear
											.findViewById(R.id.add_list_dialog_edittext);

									String name = NAME.getText().toString();

									ArrayList<String[]> tmpArr = DBEDIT
											.STRUCTURE_FIND_ALL(structDB);

									boolean isExists = false;
									for (int i = 0; i < tmpArr.size(); i++)
										if (name.equals(tmpArr.get(i)[0]))
											isExists = true;

									if (isExists) {
										Toast.makeText(Main.this,
												"동일한 제목이 이미 존재함니다.",
												Toast.LENGTH_SHORT).show();
									} else {
										DBEDIT.STRUCTURE_INSERT(structDB, name,
												name);
										DBARRAY = new ArrayList<String>();
										tmpArr = DBEDIT
												.STRUCTURE_FIND_ALL(structDB);
										if (tmpArr != null)
											for (int i = 0; i < tmpArr.size(); i++) {
												DBARRAY.add(tmpArr.get(i)[0]);
											}
										showList();

										SelectedData = name;
										SharedPreferences pref = getSharedPreferences(
												"pref", Activity.MODE_PRIVATE);
										SharedPreferences.Editor editor = pref
												.edit();
										editor.putString(PREF.MYLEC_NAME,
												SelectedData);
										editor.commit();

										TitleText.setText(SelectedData);
									}

									NAME.setText("");
								}
							})
					.setNegativeButton("취소",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText NAME = (EditText) linear
											.findViewById(R.id.add_list_dialog_edittext);
									NAME.setText("");
								}
							});
			AlertDialog alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		} else if (id == 2) {
			String[] arr = { "공유", "삭제" };
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setItems(arr, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 1:
								if (DBARRAY.size() == 1) {
									Toast.makeText(Main.this, "삭제 할 수 없습니다.",
											Toast.LENGTH_SHORT).show();
									break;
								}

								String DeleteName = DBARRAY
										.get(DELETE_LIST_ARG);
								DeleteFile(MyListDB.DATABASE_DIR + DeleteName
										+ ".db");
								DBEDIT.STRUCTURE_DELETE(structDB, DeleteName);

								DBARRAY = new ArrayList<String>();
								ArrayList<String[]> tmpArr = DBEDIT
										.STRUCTURE_FIND_ALL(structDB);
								if (tmpArr != null)
									for (int i = 0; i < tmpArr.size(); i++) {
										DBARRAY.add(tmpArr.get(i)[0]);
									}
								showList();
								if (DeleteName.equals(SelectedData)) {
									SelectedData = DBARRAY.get(0);
									SharedPreferences pref = getSharedPreferences(
											"pref", Activity.MODE_PRIVATE);
									SharedPreferences.Editor editor = pref
											.edit();
									editor.putString(PREF.MYLEC_NAME,
											SelectedData);
									editor.commit();

									TitleText.setText(SelectedData);
								}

								DELETE_LIST_ARG = -1;
								break;
							case 0:
								String orgdir = MyListDB.DATABASE_DIR
										+ DBARRAY.get(DELETE_LIST_ARG) + ".db";
								String cpydir = MyListDB.DATABASE_DIR
										+ "Share/"
										+ DBARRAY.get(DELETE_LIST_ARG)
										+ ".mkut";

								File FILE = new File(orgdir);
								if (!FILE.exists()) {
									Toast.makeText(Main.this, "추가 된 강의가 없습니다.",
											Toast.LENGTH_SHORT).show();
								} else {
									FILE = new File(MyListDB.DATABASE_DIR
											+ "Share/");
									if (!FILE.exists())
										while (FILE.mkdirs())
											;

									File orgFile = new File(orgdir);
									copyFile(orgFile, cpydir);

									Intent it = new Intent(Intent.ACTION_SEND);
									it.putExtra(Intent.EXTRA_STREAM,
											Uri.parse("file://" + cpydir));
									it.setType("application/dropbox");
									startActivity(it);
								}
								break;
							}
						}
					});
			AlertDialog alert = alt_bld.create();
			alert.setTitle("목록 삭제");
			alert.setCanceledOnTouchOutside(true);
			return alert;
		} else if (id == 3) {
			final LinearLayout linear = (LinearLayout) View.inflate(this,
					R.layout.add_list_dialog, null);
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setTitle("가져오기")
					.setView(linear)
					.setPositiveButton("확인",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText NAME = (EditText) linear
											.findViewById(R.id.add_list_dialog_edittext);

									String name = NAME.getText().toString();

									ArrayList<String[]> tmpArr = DBEDIT
											.STRUCTURE_FIND_ALL(structDB);

									boolean isExists = false;
									for (int i = 0; i < tmpArr.size(); i++)
										if (name.equals(tmpArr.get(i)[0]))
											isExists = true;

									if (isExists) {
										Toast.makeText(Main.this,
												"동일한 제목이 이미 존재합니다.",
												Toast.LENGTH_SHORT).show();
										NAME.setText("");
										showDialog(4);
									} else {
										DBEDIT.STRUCTURE_INSERT(structDB, name,
												name);
										DBARRAY = new ArrayList<String>();
										tmpArr = DBEDIT
												.STRUCTURE_FIND_ALL(structDB);
										if (tmpArr != null)
											for (int i = 0; i < tmpArr.size(); i++) {
												DBARRAY.add(tmpArr.get(i)[0]);
											}
										showList();

										SelectedData = name;
										SharedPreferences pref = getSharedPreferences(
												"pref", Activity.MODE_PRIVATE);
										SharedPreferences.Editor editor = pref
												.edit();
										editor.putString(PREF.MYLEC_NAME,
												SelectedData);
										editor.commit();

										TitleText.setText(SelectedData);

										String orgdir = sharedData;
										String cpydir = MyListDB.DATABASE_DIR
												+ SelectedData + ".db";

										File orgFile = new File(orgdir);
										copyFile(orgFile, cpydir);

										mTabHost.setCurrentTab(5);
										mTabHost.setCurrentTab(CONTENT_NOW);
										NAME.setText("");
										sharedData = null;
									}
								}
							})
					.setNegativeButton("취소",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText NAME = (EditText) linear
											.findViewById(R.id.add_list_dialog_edittext);
									NAME.setText("");
									sharedData = null;
								}
							});
			AlertDialog alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		} else if (id == 4) {
			final LinearLayout linear = (LinearLayout) View.inflate(this,
					R.layout.add_list_dialog, null);
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setTitle("가져오기")
					.setView(linear)
					.setPositiveButton("확인",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText NAME = (EditText) linear
											.findViewById(R.id.add_list_dialog_edittext);

									String name = NAME.getText().toString();

									ArrayList<String[]> tmpArr = DBEDIT
											.STRUCTURE_FIND_ALL(structDB);

									boolean isExists = false;
									for (int i = 0; i < tmpArr.size(); i++)
										if (name.equals(tmpArr.get(i)[0]))
											isExists = true;

									if (isExists) {
										Toast.makeText(Main.this,
												"동일한 제목이 이미 존재합니다.",
												Toast.LENGTH_SHORT).show();
										NAME.setText("");
										showDialog(4);
									} else {
										DBEDIT.STRUCTURE_INSERT(structDB, name,
												name);
										DBARRAY = new ArrayList<String>();
										tmpArr = DBEDIT
												.STRUCTURE_FIND_ALL(structDB);
										if (tmpArr != null)
											for (int i = 0; i < tmpArr.size(); i++) {
												DBARRAY.add(tmpArr.get(i)[0]);
											}
										showList();

										SelectedData = name;
										SharedPreferences pref = getSharedPreferences(
												"pref", Activity.MODE_PRIVATE);
										SharedPreferences.Editor editor = pref
												.edit();
										editor.putString(PREF.MYLEC_NAME,
												SelectedData);
										editor.commit();

										TitleText.setText(SelectedData);

										String orgdir = sharedData;
										String cpydir = MyListDB.DATABASE_DIR
												+ SelectedData + ".db";

										File orgFile = new File(orgdir);
										copyFile(orgFile, cpydir);

										mTabHost.setCurrentTab(5);
										mTabHost.setCurrentTab(CONTENT_NOW);
										NAME.setText("");
										sharedData = null;
									}
								}
							})
					.setNegativeButton("취소",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText NAME = (EditText) linear
											.findViewById(R.id.add_list_dialog_edittext);
									NAME.setText("");
									sharedData = null;
								}
							});
			AlertDialog alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		} else if (id == 5) {
			ArrayList<String[]> tmpArr = DBEDIT.STRUCTURE_FIND_ALL(structDB);
			String[] strarr = new String[tmpArr.size()];
			for (int i = 0; i < tmpArr.size(); i++) {
				strarr[i] = tmpArr.get(i)[0];
			}
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this)
					.setItems(strarr, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ArrayList<String[]> tmpArr = DBEDIT
									.STRUCTURE_FIND_ALL(structDB);
							String orgdir = MyListDB.DATABASE_DIR
									+ tmpArr.get(which)[0] + ".db";
							String cpydir = MyListDB.DATABASE_DIR + "Share/"
									+ tmpArr.get(which)[0] + ".mkut";

							File FILE = new File(orgdir);
							if (!FILE.exists()) {
								Toast.makeText(Main.this, "추가 된 강의가 없습니다.",
										Toast.LENGTH_SHORT).show();
								return;
							}

							FILE = new File(MyListDB.DATABASE_DIR + "Share/");
							if (!FILE.exists())
								while (FILE.mkdirs())
									;

							File orgFile = new File(orgdir);
							copyFile(orgFile, cpydir);

							Intent it = new Intent(Intent.ACTION_SEND);
							it.putExtra(Intent.EXTRA_STREAM,
									Uri.parse("file://" + cpydir));
							it.setType("application/dropbox");
							startActivity(it);

						}
					});
			alt_bld.setTitle("공유할 시간표를 선택해주세요");
			AlertDialog alert = alt_bld.create();
			alert.setCanceledOnTouchOutside(true);
			return alert;
		}
		return null;
	}

	private static void DeleteDir(String path) {
		File file = new File(path);
		if (!file.exists())
			return;

		File[] childFileList = file.listFiles();
		for (File childFile : childFileList) {
			if (childFile.isDirectory()) {
				DeleteDir(childFile.getAbsolutePath());
			} else {
				childFile.delete();
			}
		}
		file.delete();
	}

	private static void DeleteFile(String path) {
		File file = new File(path);
		if (!file.exists())
			return;
		file.delete();
	}

	private boolean copyFile(File file, String save_file) {
		boolean result;
		if (file != null && file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream newfos = new FileOutputStream(save_file);
				int readcount = 0;
				byte[] buffer = new byte[1024];
				while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
					newfos.write(buffer, 0, readcount);
				}
				newfos.close();
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			result = true;
		} else {
			result = false;
		}
		return result;
	}

}