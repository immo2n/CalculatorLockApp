package com.ucllc.smcalculatorlock.Custom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.ucllc.smcalculatorlock.DataClasses.LockedFile;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "CalculatorLock.db";
    private static final int DB_VERSION = 3;
    private final String COL_ID = "id";

    //App State table
    private final String APP_STATE_TABLE_NAME = "app_state";
    private final String APP_STATE_KEY = "key_name";
    private final String APP_STATE_VALUE = "key_value";

    //Calculator history table
    private final String CALC_HISTORY_TABLE = "calculator_history";
    private final String CALC_HISTORY_ENTRY = "entry";

    //App Lock list table
    private final String APP_LOCK_LIST_TABLE = "app_lock_list";
    private final String APP_LOCK_LIST_PACKAGE = "package_name";

    //Locked files table
    private final String LOCKED_FILES_TABLE = "locked_files";
    private final String LOCKED_FILES_SOURCE_PATH = "file_source_path";
    private final String LOCKED_FILES_FILE_NAME = "file_name";
    private final String LOCKED_FILES_FILE_DATE_TIME = "file_date_time";
    private final String LOCKED_FILES_HASH = "file_hash";


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
        query = "CREATE TABLE " + APP_LOCK_LIST_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + APP_LOCK_LIST_PACKAGE + " TEXT)";
        db.execSQL(query);
        query = "CREATE TABLE " + LOCKED_FILES_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LOCKED_FILES_SOURCE_PATH + " TEXT,"
                + LOCKED_FILES_FILE_NAME + " TEXT,"
                + LOCKED_FILES_FILE_DATE_TIME + " TEXT,"
                + LOCKED_FILES_HASH + " TEXT)";
        db.execSQL(query);
    }

    public void addLockedFile(@NonNull String sourcePath, @NonNull String fileName, @NonNull String dateTime, @NonNull String hash){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(LOCKED_FILES_SOURCE_PATH, sourcePath);
            values.put(LOCKED_FILES_FILE_NAME, fileName);
            values.put(LOCKED_FILES_FILE_DATE_TIME, dateTime);
            values.put(LOCKED_FILES_HASH, hash);
            db.insert(LOCKED_FILES_TABLE, null, values);
        }
        catch (Exception e){
            Global.logError(e);
        }
    }

    public void removeLockedFile(@NonNull String sourcePath, @NonNull String fileName){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + LOCKED_FILES_TABLE + " WHERE " + LOCKED_FILES_SOURCE_PATH + " = ? AND " + LOCKED_FILES_FILE_NAME + " = ?", new String[]{sourcePath, fileName});
        }
        catch (Exception e){
            Global.logError(e);
        }
    }

    public List<LockedFile> getLockedFiles(){
        try {
            List<LockedFile> list = null;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + LOCKED_FILES_TABLE, null);
            if (cursor.moveToFirst()) {
                list = new ArrayList<>();
                do {
                    list.add(new LockedFile(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3)
                    ));
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

    public void addLockedApp(@NonNull String packageName){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(APP_LOCK_LIST_PACKAGE, packageName);
            db.insert(APP_LOCK_LIST_TABLE, null, values);
        }
        catch (Exception e){
            Global.logError(e);
        }
    }

    public void removeLockedApp(@NonNull String packageName){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + APP_LOCK_LIST_TABLE + " WHERE " + APP_LOCK_LIST_PACKAGE + " = ?", new String[]{packageName});
        }
        catch (Exception e){
            Global.logError(e);
        }
    }

    public boolean isAppLocked(@NonNull String packageName){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + APP_LOCK_LIST_PACKAGE + " FROM " + APP_LOCK_LIST_TABLE + " WHERE " + APP_LOCK_LIST_PACKAGE + " = ?", new String[]{packageName});
            boolean result = cursor.moveToFirst();
            cursor.close();
            return result;
        }
        catch (Exception e){
            Global.logError(e);
            return false;
        }
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

    public void setAppState(@NonNull List<String[]> stateList){
        try {
            for(String[] pair:stateList){
                setAppState(pair[0], pair[1]);
            }
        }
        catch (Exception e){
            Global.logError(e);
        }
    }

    public void deleteStateValue(@NonNull String stateKey){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + APP_STATE_TABLE_NAME + " WHERE " + APP_STATE_KEY + " = ?", new String[]{stateKey});
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

    //Create a method to delete all history
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
        db.execSQL("DROP TABLE IF EXISTS " + APP_LOCK_LIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOCKED_FILES_TABLE);
        onCreate(db);
    }
}