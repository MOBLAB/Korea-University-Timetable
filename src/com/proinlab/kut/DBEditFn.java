/************************************************************************
 * @author PROIN LAB [ DB ���� �Լ� ]
 *         --------------------------------------------------------------
 *         DB_FN_INSERT / DB_FN_FIND / DB_FN_DELETE / DB_FN_CHANGEDATA /
 *         DB_FN_FIND_FILENAME_BY_CATEGORY
 *         --------------------------------------------------------------
 *         DB_FN_FIND_CATEGORY_ALL / DB_FN_FIND_CATEGORY_EXIST /
 *         DB_FN_INSERT_CATEGORY
 ************************************************************************/

package com.proinlab.kut;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.proinlab.kut.functions.DataBaseHelper;
import com.proinlab.kut.functions.StructureDB;

public class DBEditFn {

	public boolean STRUCTURE_DELETE(SQLiteOpenHelper mHelper, String data) {
		SQLiteDatabase db;
		db = mHelper.getWritableDatabase();
		db.delete(StructureDB.DB_TABLE_MYLEC, StructureDB.DB_ROW_NAME + " = '"
				+ data + "'", null);
		mHelper.close();
		return true;
	}

	public boolean STRUCTURE_INSERT(SQLiteOpenHelper mHelper, String name,
			String dir) {
		SQLiteDatabase db;
		ContentValues row;
		db = mHelper.getWritableDatabase();
		row = new ContentValues();
		row.put(StructureDB.DB_ROW_NAME, name);
		row.put(StructureDB.DB_ROW_DIR, dir);
		db.insert(StructureDB.DB_TABLE_MYLEC, null, row);
		mHelper.close();
		return true;
	}

	public ArrayList<String[]> STRUCTURE_FIND_ALL(SQLiteOpenHelper mHelper) {
		SQLiteDatabase db;

		ArrayList<String[]> result = null;
		String[] columns = StructureDB.DB_ROW_ENTIRE;

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(StructureDB.DB_TABLE_MYLEC, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0) {
			result = null;
		} else {
			result = new ArrayList<String[]>();
			while (cursor.moveToNext()) {
				String[] resultstr = new String[2];
				for (int i = 0; i < 2; i++)
					resultstr[i] = cursor.getString(i);
				result.add(resultstr);
			}
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * ���� ���̺� ������ �����Ѵ�
	 * 
	 * @param mHelper
	 * @param Table
	 * @param values
	 * @return
	 */

	public boolean LECTURE_INSERT(SQLiteOpenHelper mHelper, String Table,
			String[] values) {
		SQLiteDatabase db;
		ContentValues row;
		db = mHelper.getWritableDatabase();
		row = new ContentValues();
		for (int i = 0; i < DataBaseHelper.DB_3ROW_ENTIRE.length; i++) {
			row.put(DataBaseHelper.DB_3ROW_ENTIRE[i], values[i]);
		}

		db.insert(Table, null, row);
		mHelper.close();
		return true;
	}

	/**
	 * �������̺��� ��� �����͸� �˻����ش�
	 * 
	 * @param mHelper
	 * @param Table
	 * @return
	 */
	public ArrayList<String[]> LECTURE_FIND_ALL(SQLiteOpenHelper mHelper,
			String Table) {
		SQLiteDatabase db;

		ArrayList<String[]> result = null;
		String[] columns = DataBaseHelper.DB_3ROW_ENTIRE;

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(Table, columns, null, null, null, null, null);

		if (cursor.getCount() == 0) {
			result = null;
		} else {
			result = new ArrayList<String[]>();
			while (cursor.moveToNext()) {
				String[] resultstr = new String[15];
				for (int i = 0; i < 15; i++)
					resultstr[i] = cursor.getString(i);
				result.add(resultstr);
			}
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * ���� ���̺��� �ش��ϴ� ROW�� ��ġ�ϴ� �����Ͱ� �ִ� ROW�� ��� �ҷ��´�
	 * 
	 * @param mHelper
	 * @param Table
	 * @param value
	 * @param type
	 * @return ArrayList<String[]>
	 */

	public ArrayList<String[]> LECTURE_FIND_DATA_BY_VALUE(
			SQLiteOpenHelper mHelper, String Table, String value, int type) {
		SQLiteDatabase db;

		ArrayList<String[]> result = null;
		String[] columns = DataBaseHelper.DB_3ROW_ENTIRE;

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(Table, columns, null, null, null, null, null);

		if (cursor.getCount() == 0) {
			result = null;
		} else {
			result = new ArrayList<String[]>();
			while (cursor.moveToNext()) {
				if (value.equals(cursor.getString(type))) {
					String[] resultstr = new String[15];
					for (int i = 0; i < 15; i++)
						resultstr[i] = cursor.getString(i);
					result.add(resultstr);
				}
			}
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * ���� ���̺��� �κ���ġ�ϴ� �����͸� �˻����ش�
	 * 
	 * @param mHelper
	 * @param Table
	 * @param value
	 * @param type
	 * @return
	 */
	public ArrayList<String[]> LECTURE_FIND_DATA_BY_VALUE_CONTAIN(
			SQLiteOpenHelper mHelper, String Table, String value, int type) {
		SQLiteDatabase db;

		ArrayList<String[]> result = null;
		String[] columns = DataBaseHelper.DB_3ROW_ENTIRE;

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(Table, columns, null, null, null, null, null);

		if (cursor.getCount() == 0) {
			result = null;
		} else {
			result = new ArrayList<String[]>();
			while (cursor.moveToNext()) {
				if (cursor.getString(type).contains(value)) {
					String[] resultstr = new String[15];
					for (int i = 0; i < 15; i++)
						resultstr[i] = cursor.getString(i);
					result.add(resultstr);
				}
			}
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * ���� ��� ���̺� ���������� �����Ѵ�
	 * 
	 * @param mHelper
	 * @param name
	 * @return
	 */

	public boolean CULTURE_NAME_INSERT(SQLiteOpenHelper mHelper, String name) {
		SQLiteDatabase db;
		ContentValues row;
		db = mHelper.getWritableDatabase();
		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_COL_NAME, name);
		db.insert(DataBaseHelper.DB_TABLE_CULTURE_NAME, null, row);
		mHelper.close();
		return true;
	}

	/**
	 * ���� ��� ���̺� �ִ� ��� ���������� �����´�
	 * 
	 * @param mHelper
	 * @return
	 */

	public String[] CULTURE_NAME_FIND_ALL(SQLiteOpenHelper mHelper) {
		SQLiteDatabase db;

		ArrayList<String> returnStr;
		String[] result = null;
		String[] columns = { DataBaseHelper.DB_ROW_COL_NAME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_CULTURE_NAME, columns, null,
				null, null, null, null);

		if (cursor.getCount() == 0)
			result = null;
		else {
			returnStr = new ArrayList<String>();
			while (cursor.moveToNext()) {
				returnStr.add(cursor.getString(0));
			}
			result = new String[returnStr.size()];
			for (int i = 0; i < returnStr.size(); i++)
				result[i] = returnStr.get(i);
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * ���и����̺� ���и��� �����Ѵ�
	 * 
	 * @param mHelper
	 * @param col_name
	 * @return
	 */

	public boolean COLNAME_INSERT(SQLiteOpenHelper mHelper, String col_name) {
		SQLiteDatabase db;
		ContentValues row;
		db = mHelper.getWritableDatabase();
		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_COL_NAME, col_name);
		db.insert(DataBaseHelper.DB_TABLE_COLNAME, null, row);
		mHelper.close();
		return true;
	}

	/**
	 * ���и� ���̺� �ִ� ��� ���и��� �����´�
	 * 
	 * @param mHelper
	 * @return
	 */

	public String[] COLNAME_FIND_ALL(SQLiteOpenHelper mHelper) {
		SQLiteDatabase db;

		ArrayList<String> returnStr;
		String[] result = null;
		String[] columns = { DataBaseHelper.DB_ROW_COL_NAME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_COLNAME, columns, null, null,
				null, null, null);

		if (cursor.getCount() == 0)
			result = null;
		else {
			returnStr = new ArrayList<String>();
			while (cursor.moveToNext()) {
				returnStr.add(cursor.getString(0));
			}
			result = new String[returnStr.size()];
			for (int i = 0; i < returnStr.size(); i++)
				result[i] = returnStr.get(i);
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * ������ �Ķ���͸� �����Ѵ�
	 * 
	 * @param mHelper
	 * @param culture_id
	 * @param culture_name
	 * @param detail_id
	 * @param detail_name
	 * @return
	 */
	public boolean CULTURE_PARAM_INSERT(SQLiteOpenHelper mHelper,
			String culture_id, String culture_name, String detail_id,
			String detail_name) {

		SQLiteDatabase db;
		ContentValues row;

		db = mHelper.getWritableDatabase();
		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_COL_ID, culture_id);
		row.put(DataBaseHelper.DB_ROW_COL_NAME, culture_name);
		row.put(DataBaseHelper.DB_ROW_DEPT_ID, detail_id);
		row.put(DataBaseHelper.DB_ROW_DEPT_NAME, detail_name);
		db.insert(DataBaseHelper.DB_TABLE_CULTURE_PARAM_INFO, null, row);
		mHelper.close();
		return true;
	}

	/**
	 * ���� �Ķ���� ���̺��� ������ �� ����� �����´�
	 * 
	 * @param mHelper
	 * @param cul_name
	 * @return
	 */

	public String[] CULTURE_PARAM_FIND_DETAIL_BY_CULTURE(
			SQLiteOpenHelper mHelper, String cul_name) {
		SQLiteDatabase db;

		ArrayList<String> returnStr;
		String[] result = null;
		String[] columns = { DataBaseHelper.DB_ROW_COL_ID,
				DataBaseHelper.DB_ROW_COL_NAME, DataBaseHelper.DB_ROW_DEPT_ID,
				DataBaseHelper.DB_ROW_DEPT_NAME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_CULTURE_PARAM_INFO, columns,
				null, null, null, null, null);

		if (cursor.getCount() == 0)
			result = null;
		else {
			returnStr = new ArrayList<String>();
			while (cursor.moveToNext()) {
				if (cul_name.equals(cursor.getString(1))) {
					returnStr.add(cursor.getString(3));
				}
			}
			result = new String[returnStr.size()];
			for (int i = 0; i < returnStr.size(); i++)
				result[i] = returnStr.get(i);
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	public String[] CULTURE_PARAM_FIND_DATA_BY_DETAIL(SQLiteOpenHelper mHelper,
			String dept) {
		SQLiteDatabase db;
		if (dept == null)
			return null;
		String[] result = null;
		String[] columns = { DataBaseHelper.DB_ROW_COL_ID,
				DataBaseHelper.DB_ROW_COL_NAME, DataBaseHelper.DB_ROW_DEPT_ID,
				DataBaseHelper.DB_ROW_DEPT_NAME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_CULTURE_PARAM_INFO, columns,
				null, null, null, null, null);

		if (cursor.getCount() == 0)
			result = null;
		else {
			result = new String[4];
			while (cursor.moveToNext()) {
				if (dept.equals(cursor.getString(3))) {
					for (int i = 0; i < 4; i++)
						result[i] = cursor.getString(i);
				}
			}
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * �Ķ�������̺� �����͸� �����Ѵ�
	 * 
	 * @param mHelper
	 * @param col_id
	 *            ���� ID
	 * @param col_name
	 *            ���и�
	 * @param dept_id
	 *            �а� ID
	 * @param dept_name
	 *            �а���
	 * @return
	 */

	public boolean PARAM_INSERT(SQLiteOpenHelper mHelper, String col_id,
			String col_name, String dept_id, String dept_name) {

		SQLiteDatabase db;
		ContentValues row;

		db = mHelper.getWritableDatabase();
		row = new ContentValues();
		row.put(DataBaseHelper.DB_ROW_COL_ID, col_id);
		row.put(DataBaseHelper.DB_ROW_COL_NAME, col_name);
		row.put(DataBaseHelper.DB_ROW_DEPT_ID, dept_id);
		row.put(DataBaseHelper.DB_ROW_DEPT_NAME, dept_name);
		db.insert(DataBaseHelper.DB_TABLE_PARAM_INFO, null, row);
		mHelper.close();
		return true;
	}

	/**
	 * �Ķ�������̺��� ���и� �ش��ϴ� ��� �а��� ã�´�
	 * 
	 * @param mHelper
	 * @param col_name
	 * @return String[]
	 */

	public String[] PARAM_FIND_DEPT_BY_COL(SQLiteOpenHelper mHelper,
			String col_name) {
		SQLiteDatabase db;

		ArrayList<String> returnStr;
		String[] result = null;
		String[] columns = { DataBaseHelper.DB_ROW_COL_ID,
				DataBaseHelper.DB_ROW_COL_NAME, DataBaseHelper.DB_ROW_DEPT_ID,
				DataBaseHelper.DB_ROW_DEPT_NAME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_PARAM_INFO, columns, null,
				null, null, null, null);

		if (cursor.getCount() == 0)
			result = null;
		else {
			returnStr = new ArrayList<String>();
			while (cursor.moveToNext()) {
				if (col_name.equals(cursor.getString(1))) {
					returnStr.add(cursor.getString(3));
				}
			}
			result = new String[returnStr.size()];
			for (int i = 0; i < returnStr.size(); i++)
				result[i] = returnStr.get(i);
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * �Ķ�������̺��� �а��� �ش��ϴ� Parameter ������ �����´�
	 * 
	 * @param mHelper
	 * @param dept
	 *            �а���
	 * @return String[] col_id, col_name, dept_id, dept_name
	 */

	public String[] PARAM_FIND_DATA_BY_DEPT(SQLiteOpenHelper mHelper,
			String dept) {
		SQLiteDatabase db;

		String[] result = null;
		String[] columns = { DataBaseHelper.DB_ROW_COL_ID,
				DataBaseHelper.DB_ROW_COL_NAME, DataBaseHelper.DB_ROW_DEPT_ID,
				DataBaseHelper.DB_ROW_DEPT_NAME };

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.query(DataBaseHelper.DB_TABLE_PARAM_INFO, columns, null,
				null, null, null, null);

		if (cursor.getCount() == 0)
			result = null;
		else {
			result = new String[4];
			while (cursor.moveToNext()) {
				if (dept.equals(cursor.getString(3))) {
					for (int i = 0; i < 4; i++)
						result[i] = cursor.getString(i);
				}
			}
		}
		cursor.close();
		mHelper.close();
		return result;
	}

	/**
	 * ���̺��� �����Ѵ�
	 * 
	 * @param mHelper
	 * @param TABLENAME
	 * @return
	 */

	public boolean DELETE_TABLE(SQLiteOpenHelper mHelper, String TABLENAME) {
		SQLiteDatabase db;
		db = mHelper.getWritableDatabase();
		db.delete(TABLENAME, null, null);
		mHelper.close();
		return true;
	}

	/**
	 * ���� ���̺��� �ش��ϴ� �����͸� �����Ѵ�
	 * 
	 * @param mHelper
	 * @param TABLENAME
	 * @param data
	 * @return
	 */
	public boolean DELETE_LECTURE(SQLiteOpenHelper mHelper, String TABLENAME,
			String[] data) {
		SQLiteDatabase db;
		db = mHelper.getWritableDatabase();
		db.delete(TABLENAME, DataBaseHelper.DB_3ROW_11_SCHEDULE + " = '"
				+ data[DataBaseHelper.DB_INDEX_3ROW_11_SCHEDULE] + "'", null);
		mHelper.close();
		return true;
	}
}
