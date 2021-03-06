/*****************************************************************************
 * @author PROIN LAB [ DB 구조 ]
 *         -------------------------------------------------------------------
 *         TABLE : FILE_LIST, CATEGORY_LIST
 *         -------------------------------------------------------------------
 *         FILE_LIST ROW : FILENAME LATESTTIME FIRSTTIME CATEGORY FILEDIR
 *         FILETYPE
 *         -------------------------------------------------------------------
 *         CATEGORY_LIST ROW : CATEGORY
 *****************************************************************************/

package com.proinlab.kut.functions;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DataBaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_DIR = Environment
			.getExternalStorageDirectory().toString() + "/.KUT/database.db";

	public static final String DB_TABLE_PARAM_INFO = "PARAM_INFO";
	public static final String DB_TABLE_COLNAME = "TABLE_COLNAME";
	public static final String DB_TABLE_LECTURE = "TABLE_LECTURE";
	public static final String DB_TABLE_CULTURE_PARAM_INFO = "CUL_PARAM_INFO";
	public static final String DB_TABLE_CULTURE_NAME = "TABLE_CUL_NAME";
	public static final String DB_TABLE_CULTURE_LECTURE = "TABLE_CUL_LECTURE";
	
	public static final String DB_ROW_COL_ID = "colid";
	public static final String DB_ROW_COL_NAME = "colname";
	public static final String DB_ROW_DEPT_ID = "deptid";
	public static final String DB_ROW_DEPT_NAME = "deptname";

	public static final String DB_3ROW_01_CATE1 = "a1"; // 분류1
	public static final String DB_3ROW_02_CATE2 = "a2"; // 분류2
	public static final String DB_3ROW_03_CATE3 = "a3"; // 이수구분
	public static final String DB_3ROW_04_CAMPUS = "a4"; // 캠퍼스
	public static final String DB_3ROW_05_LEC_ID = "a5"; // 학수번호
	public static final String DB_3ROW_06_GROUP = "a6"; // 분반
	public static final String DB_3ROW_07_LEC_NAME = "a7"; // 교과목명
	public static final String DB_3ROW_08_PROFESSOR = "a8"; // 담당교수
	public static final String DB_3ROW_09_CREDIT = "a9"; // 학점, 시간
	public static final String DB_3ROW_11_SCHEDULE = "a10"; // 강의시간, 강의실
	public static final String DB_3ROW_12_LINK = "a11"; // 링크
	public static final String DB_3ROW_13_REMARKS1 = "a12"; // 상대평가
	public static final String DB_3ROW_14_REMARKS2 = "a13"; // 인원제한
	public static final String DB_3ROW_15_REMARKS3 = "a14"; // 대기
	public static final String DB_3ROW_16_REMARKS4 = "a15"; // 교환학생

	public static final int DB_INDEX_3ROW_01_CATE1 = 0; // 분류1
	public static final int DB_INDEX_3ROW_02_CATE2 = 1; // 분류2
	public static final int DB_INDEX_3ROW_03_CATE3 = 2; // 이수구분
	public static final int DB_INDEX_3ROW_04_CAMPUS = 3; // 캠퍼스
	public static final int DB_INDEX_3ROW_05_LEC_ID = 4; // 학수번호
	public static final int DB_INDEX_3ROW_06_GROUP = 5; // 분반
	public static final int DB_INDEX_3ROW_07_LEC_NAME = 6; // 교과목명
	public static final int DB_INDEX_3ROW_08_PROFESSOR = 7; // 담당교수
	public static final int DB_INDEX_3ROW_09_CREDIT = 8; // 학점,시
	public static final int DB_INDEX_3ROW_11_SCHEDULE = 9; // 강의시간, 강의실
	public static final int DB_INDEX_3ROW_12_LINK = 10; // 링크
	public static final int DB_INDEX_3ROW_13_REMARKS1 = 11; // 상대평가
	public static final int DB_INDEX_3ROW_14_REMARKS2 = 12; // 인원제한
	public static final int DB_INDEX_3ROW_15_REMARKS3 = 13; // 대기
	public static final int DB_INDEX_3ROW_16_REMARKS4 = 14; // 교환학생

	public static final String[] DB_3ROW_ENTIRE = { "a1", "a2", "a3", "a4",
			"a5", "a6", "a7", "a8", "a9", "a10", "a11", "a12", "a13", "a14",
			"a15" };

	public DataBaseHelper(Context context) {
		super(context, DATABASE_DIR, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DB_TABLE_PARAM_INFO
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + DB_ROW_COL_ID
				+ " TEXT, " + DB_ROW_COL_NAME + " TEXT, " + DB_ROW_DEPT_ID
				+ " TEXT, " + DB_ROW_DEPT_NAME + " TEXT);");
		db.execSQL("CREATE TABLE " + DB_TABLE_COLNAME
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DB_ROW_COL_NAME + " TEXT);");
		db.execSQL("CREATE TABLE " + DB_TABLE_LECTURE
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DB_3ROW_01_CATE1 + " TEXT, " + DB_3ROW_02_CATE2 + " TEXT, "
				+ DB_3ROW_03_CATE3 + " TEXT, " + DB_3ROW_04_CAMPUS + " TEXT, "
				+ DB_3ROW_05_LEC_ID + " TEXT, " + DB_3ROW_06_GROUP + " TEXT, "
				+ DB_3ROW_07_LEC_NAME + " TEXT, " + DB_3ROW_08_PROFESSOR
				+ " TEXT, " + DB_3ROW_09_CREDIT + " TEXT, "
				+ DB_3ROW_11_SCHEDULE + " TEXT, " + DB_3ROW_12_LINK + " TEXT, "
				+ DB_3ROW_13_REMARKS1 + " TEXT, " + DB_3ROW_14_REMARKS2
				+ " TEXT, " + DB_3ROW_15_REMARKS3 + " TEXT, "
				+ DB_3ROW_16_REMARKS4 + " TEXT);");
		db.execSQL("CREATE TABLE " + DB_TABLE_CULTURE_PARAM_INFO
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + DB_ROW_COL_ID
				+ " TEXT, " + DB_ROW_COL_NAME + " TEXT, " + DB_ROW_DEPT_ID
				+ " TEXT, " + DB_ROW_DEPT_NAME + " TEXT);");
		db.execSQL("CREATE TABLE " + DB_TABLE_CULTURE_NAME
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DB_ROW_COL_NAME + " TEXT);");
		db.execSQL("CREATE TABLE " + DB_TABLE_CULTURE_LECTURE
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DB_3ROW_01_CATE1 + " TEXT, " + DB_3ROW_02_CATE2 + " TEXT, "
				+ DB_3ROW_03_CATE3 + " TEXT, " + DB_3ROW_04_CAMPUS + " TEXT, "
				+ DB_3ROW_05_LEC_ID + " TEXT, " + DB_3ROW_06_GROUP + " TEXT, "
				+ DB_3ROW_07_LEC_NAME + " TEXT, " + DB_3ROW_08_PROFESSOR
				+ " TEXT, " + DB_3ROW_09_CREDIT + " TEXT, "
				+ DB_3ROW_11_SCHEDULE + " TEXT, " + DB_3ROW_12_LINK + " TEXT, "
				+ DB_3ROW_13_REMARKS1 + " TEXT, " + DB_3ROW_14_REMARKS2
				+ " TEXT, " + DB_3ROW_15_REMARKS3 + " TEXT, "
				+ DB_3ROW_16_REMARKS4 + " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_PARAM_INFO);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_COLNAME);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_LECTURE);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CULTURE_PARAM_INFO);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CULTURE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_CULTURE_LECTURE);
		onCreate(db);
	}

}
