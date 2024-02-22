package com.ucllc.smcalculatorlock.Custom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "CalculatorLock.db";
    private static final int DB_VERSION = 1;
    private final String COL_ID = "id";

    //App State table
    private final String APP_STATE_TABLE_NAME = "app_state";
    private final String APP_STATE_KEY = "key_name";
    private final String APP_STATE_VALUE = "key_value";

    //Calculator history table
    private final String CALC_HISTORY_TABLE = "calculator_history";
    private final String CALC_HISTORY_ENTRY = "entry";

    public DBHandler(@NonNull Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + APP_STATE_TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + APP_STATE_KEY + " TEXT,"
                + APP_STATE_VALUE + " TEXT)";
        db.execSQL(query);
        query = "CREATE TABLE " + CALC_HISTORY_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CALC_HISTORY_ENTRY + " TEXT)";
        db.execSQL(query);
    }

    public void setAppState(@NonNull String stateKey, @NonNull String value){
        try {
            //DB
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            //VALUES
            values.put(APP_STATE_KEY, stateKey);
            values.put(APP_STATE_VALUE, value);
            //Pass
            if (getStateValue(stateKey) != null) {
                db.update(APP_STATE_TABLE_NAME, values, APP_STATE_KEY + " = ?", new String[]{stateKey});
            } else {
                db.insert(APP_STATE_TABLE_NAME, null, values);
            }
        }
        catch (Exception e){
            Global.logError(e);
        }
    }

    public String getStateValue(@NonNull String stateKey){
        try {
            String result = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + APP_STATE_VALUE + " FROM " + APP_STATE_TABLE_NAME + " WHERE " + APP_STATE_KEY + " = ? ORDER BY id DESC LIMIT 1 OFFSET 0", new String[]{stateKey});
            if (cursor.moveToFirst()) {
                do {
                    result = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return result;
        }
        catch (Exception e){
            return null;
        }
    }

    public void insertHistory(@NonNull String entry){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CALC_HISTORY_ENTRY, entry);
            db.insert(CALC_HISTORY_TABLE, null, values);
        }
        catch (Exception e){
            Global.logError(e);
        }
    }

    public List<String> getHistory(){
        try {
            List<String> list = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + CALC_HISTORY_ENTRY + " FROM " + CALC_HISTORY_TABLE + " ORDER BY "+ COL_ID +" DESC", null);
            if (cursor.moveToFirst()) {
                list = new ArrayList<>();
                do {
                    list.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return list;
        }
        catch (Exception e){
            Global.logError(e);
            return null;
        }
    }

    public void deleteHistory(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + CALC_HISTORY_TABLE);
        }
        catch (Exception e){
            Global.logError(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + APP_STATE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CALC_HISTORY_TABLE);
        onCreate(db);
    }
}