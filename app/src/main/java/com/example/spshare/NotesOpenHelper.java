package com.example.spshare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NotesOpenHelper extends SQLiteOpenHelper {
    // Define a log tag
    private static final String TAG = NotesOpenHelper.class.getSimpleName();

    public static final String KEY_ID = "_id";
    public static final String KEY_COURSENAME = "courseName";
    public static final String KEY_MODULENAME = "moduleName";

    // DB version 1
    private static final int DATABASE_VERSION = 1;
    private static final String WORD_LIST_TABLE = "word_entries";
    private static final String DATABASE_NAME = "wordlist";

    private static final String WORD_LIST_TABLE_CREATE =
            "CREATE TABLE " + WORD_LIST_TABLE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " +
                    KEY_COURSENAME + " TEXT, " +
                    KEY_MODULENAME + " TEXT " +
                    " );";

    // variables for references to readable/writeable dbs
    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public NotesOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_LIST_TABLE_CREATE);
        fillDatabase(db);
    }

    private void fillDatabase(SQLiteDatabase db) {
        // create courses with modules
        HashMap<String, String> data = new HashMap<>();

        data.put("Infocomm-Security-Management", "CAOS,DBMS,WCD,MAPP");
        data.put("Games-Design-And-Development", "BDC,GP");
        data.put("Human-Resource-And-Psychology", "EI,NAT,BS");
        data.put("Business-Accountancy", "BA,TAX,BCL");

        // create cntainer for data
        ContentValues values = new ContentValues();

        // put column/values pairs into container and insert into table
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry<String, String> item = (Map.Entry) it.next();
            values.put(KEY_COURSENAME, item.getKey());
            values.put(KEY_MODULENAME, item.getValue());
            db.insert(WORD_LIST_TABLE, null, values);
            Log.e(TAG, values.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.w(NotesOpenHelper.class.getName(),
        //      "Upgrading database from version " + oldVersion + " to "
        //                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + WORD_LIST_TABLE);
        onCreate(db);
    }

    public List<String> queryModules (String selectedCourse) {
        // sql query for retrieving word from position
        String query = "SELECT * FROM " + WORD_LIST_TABLE +
                " WHERE " + KEY_COURSENAME + " = \"" + selectedCourse + "\"";

        // cursor to hold result from db
        Cursor cursor = null;
        List<String> modules = new ArrayList<>();
        try {
            // obtain readable db and store in mReadableDB
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }

            // execute sql query and set cursor to first item in results list
            cursor = mReadableDB.rawQuery(query, null);
            cursor.moveToFirst();

            // get modules from comma-separated values
            modules = Arrays.asList(cursor.getString(cursor.getColumnIndex(KEY_MODULENAME)).split(","));

        } catch (Exception e) {
            //Log.e(TAG, "EXCEPTION! " + e.getMessage());
        } finally {
            cursor.close();
            return modules;
        }
    }
}
