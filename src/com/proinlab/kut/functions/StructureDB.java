/*****************************************************************************
 * @author PROIN LAB [ DB ±¸Á¶ ]
 *         -------------------------------------------------------------------
 *         TABLE : FILE_LIST, CATEGORY_LIST
 *         -------------------------------------------------------------------
 *         FILE_LIST ROW : FILENAME LATESTTIME FIRSTTIME CATEGORY FILEDIR
 *         FILETYPE
 *         -------------------------------------------------------------------
 *         CATEGORY_LIST ROW : CATEGORY
 *****************************************************************************/

package com.proinlab.kut.functions;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class StructureDB extends SQLiteOpenHelper {

	public static final String DATABASE_DIR = Environment
			.getExternalStorageDirectory().toString() + "/.KUT/";

	public static final String DB_TABLE_MYLEC = "MYLEC";
	
	public static final String DB_ROW_NAME = "name";
	public static final String DB_ROW_DIR = "dir";
	
	public static final String[] DB_ROW_ENTIRE = { "name", "dir"};
	
	public StructureDB(Context context) {
		super(context, DATABASE_DIR + "structure.db", null, 1);
		File FILE = new File(DATABASE_DIR);
		if (!FILE.exists())
			while (FILE.mkdirs())
				;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DB_TABLE_MYLEC
				+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + DB_ROW_NAME
				+ " TEXT, " + DB_ROW_DIR + " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_MYLEC);
		onCreate(db);
	}

}
