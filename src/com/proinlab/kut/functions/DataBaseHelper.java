/*****************************************************************************
 * @author PROIN LAB [ DB ���� ]
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

	public static final String DB_3ROW_01_CATE1 = "a1"; // �з�1
	public static final String DB_3ROW_02_CATE2 = "a2"; // �з�2
	public static final String DB_3ROW_03_CATE3 = "a3"; // �̼�����
	public static final String DB_3ROW_04_CAMPUS = "a4"; // ķ�۽�
	public static final String DB_3ROW_05_LEC_ID = "a5"; // �м���ȣ
	public static final String DB_3ROW_06_GROUP = "a6"; // �й�
	public static final String DB_3ROW_07_LEC_NAME = "a7"; // �������
	public static final String DB_3ROW_08_PROFESSOR = "a8"; // ��米��
	public static final String DB_3ROW_09_CREDIT = "a9"; // ����, �ð�
	public static final String DB_3ROW_11_SCHEDULE = "a10"; // ���ǽð�, ���ǽ�
	public static final String DB_3ROW_12_LINK = "a11"; // ��ũ
	public static final String DB_3ROW_13_REMARKS1 = "a12"; // �����
	public static final String DB_3ROW_14_REMARKS2 = "a13"; // �ο�����
	public static final String DB_3ROW_15_REMARKS3 = "a14"; // ���
	public static final String DB_3ROW_16_REMARKS4 = "a15"; // ��ȯ�л�

	public static final int DB_INDEX_3ROW_01_CATE1 = 0; // �з�1
	public static final int DB_INDEX_3ROW_02_CATE2 = 1; // �з�2
	public static final int DB_INDEX_3ROW_03_CATE3 = 2; // �̼�����
	public static final int DB_INDEX_3ROW_04_CAMPUS = 3; // ķ�۽�
	public static final int DB_INDEX_3ROW_05_LEC_ID = 4; // �м���ȣ
	public static final int DB_INDEX_3ROW_06_GROUP = 5; // �й�
	public static final int DB_INDEX_3ROW_07_LEC_NAME = 6; // �������
	public static final int DB_INDEX_3ROW_08_PROFESSOR = 7; // ��米��
	public static final int DB_INDEX_3ROW_09_CREDIT = 8; // ����,��
	public static final int DB_INDEX_3ROW_11_SCHEDULE = 9; // ���ǽð�, ���ǽ�
	public static final int DB_INDEX_3ROW_12_LINK = 10; // ��ũ
	public static final int DB_INDEX_3ROW_13_REMARKS1 = 11; // �����
	public static final int DB_INDEX_3ROW_14_REMARKS2 = 12; // �ο�����
	public static final int DB_INDEX_3ROW_15_REMARKS3 = 13; // ���
	public static final int DB_INDEX_3ROW_16_REMARKS4 = 14; // ��ȯ�л�

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
