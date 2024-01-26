package com.example.spshare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SavedNotesOpenHelper extends SQLiteOpenHelper{
    // Define a log tag
    private static final String TAG = com.example.spshare.SavedNotesOpenHelper.class.getSimpleName();

    private static final String KEY_ID = "_id";
    private static final String KEY_COURSENAME = "courseName";
    private static final String KEY_MODULENAME = "moduleName";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TITLE = "title";

    // DB version 1
    private static final int DATABASE_VERSION = 1;
    private static final String WORD_LIST_TABLE = "note_entries";
    private static final String DATABASE_NAME = "savedNotes";

    private static final String WORD_LIST_TABLE_CREATE =
                "CREATE TABLE " + WORD_LIST_TABLE + " (" +
                        KEY_ID + " INTEGER PRIMARY KEY, " +
                        KEY_COURSENAME + " TEXT, " +
                        KEY_MODULENAME + " TEXT, " +
                        KEY_CONTENT + " TEXT, " +
                        KEY_USERNAME + " TEXT, " +
                        KEY_TITLE + " TEXT " +
                        " );";

        // variables for references to readable/writeable dbs
        private SQLiteDatabase mWritableDB;
        private SQLiteDatabase mReadableDB;

        Context context;
        private RequestQueue queue;

    public SavedNotesOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

        @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_LIST_TABLE_CREATE);
    }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Log.w(com.example.spshare.NotesOpenHelper.class.getName(),
            //        "Upgrading database from version " + oldVersion + " to "
            //                + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + WORD_LIST_TABLE);
            onCreate(db);
        }

        public int getSize() {
            String query = "SELECT COUNT(*) as 'size' FROM " + WORD_LIST_TABLE;
            Cursor cursor = null;
            int size = 0;
            try {
                // obtain readable db and store in mReadableDB
                if (mReadableDB == null) {
                    mReadableDB = getReadableDatabase();
                }

                // execute sql query and set cursor to first item in results list
                cursor = mReadableDB.rawQuery(query, null);
                cursor.moveToFirst();
                size = cursor.getInt(cursor.getColumnIndex("size"));
            } catch (Exception e) {
                //Log.e(TAG, "EXCEPTION! " + e.getMessage());
            } finally {
                cursor.close();
                return size;
            }
        }

        public void sendAllNotes () {
            // sql query for retrieving word from position
            String query = "SELECT * FROM " + WORD_LIST_TABLE;

            // cursor to hold result from db
            Cursor cursor = null;
            try {
                // obtain readable db and store in mReadableDB
                if (mReadableDB == null) {
                    mReadableDB = getReadableDatabase();
                }

                // execute sql query and set cursor to first item in results list
                cursor = mReadableDB.rawQuery(query, null);
                cursor.moveToFirst();

                // get modules from comma-separated values
                while (!cursor.isLast()) {
                    new ASync().execute(
                            cursor.getString(cursor.getColumnIndex(KEY_COURSENAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_MODULENAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_CONTENT)),
                            cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_TITLE))
                    );
                    //Log.d(TAG, "uploaded KEY:" + cursor.getInt(cursor.getColumnIndex(KEY_ID)) + ". DELETING...");
                    delete(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    cursor.moveToNext();
                }

            } catch (Exception e) {
                //Log.e(TAG, "EXCEPTION! " + e.getMessage());
            } finally {
                cursor.close();
            }
        }



    public long insert (String c, String m, String cont, String name, String title) {
        long newID = 0;

        // container for word
        ContentValues values = new ContentValues();
        values.put(KEY_COURSENAME, c);
        values.put(KEY_MODULENAME, m);
        values.put(KEY_CONTENT, cont);
        values.put(KEY_USERNAME, name);
        values.put(KEY_TITLE, title);

        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newID = mWritableDB.insert(WORD_LIST_TABLE, null, values);
            //Log.d(TAG, "Created saveNotes of " + c + m + cont + name + title + " ID == " + newID);
        } catch (Exception e) {
            //Log.d(TAG, "INSERT EXCEPTION! " + e.getMessage());
        }

        return newID;
    }

    // Delete words from the db
    public int delete (int id) {
        int deleted = 0;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }

            // call delete on WORD_LIST_TABLE, select item by KEY_ID
            // ** '?' used to make building query more secure (similar to PreparedStatement)
            deleted = mWritableDB.delete(WORD_LIST_TABLE,
                    KEY_ID + " = ? ", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            //Log.d (TAG, "DELETE EXCEPTION! " + e.getMessage());
        }
        return deleted;
    }

    private class ASync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            final String n = strings[3];
            final String con = strings[2];
            final String t = strings[4];
            String url = "https://spshare-assignment.firebaseio.com/notes/" + strings[0] + "/" + strings[1] + ".json";

            // Request a string response from url.

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Log.d(TAG, "Response: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e(TAG, error.getMessage());
                }
            }){

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("title", t);
                        object.put("username", n);
                        object.put("content", con);

                        //Log.d(TAG, object.toString());

                        // send the bytes
                        return object.toString().getBytes();

                    } catch (Exception e) {
                        //Log.e(TAG, e.getMessage());
                    }
                    return super.getBody();
                }

                // set content type to json
                public String getBodyContentType () {
                    return "application/json";
                }
            };

            queue = Volley.newRequestQueue(context);
            queue.add(req);
            return null;
        }
    }
}
