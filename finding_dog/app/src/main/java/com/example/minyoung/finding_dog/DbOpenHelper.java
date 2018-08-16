package com.example.minyoung.finding_dog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;

public class DbOpenHelper {

    private static final String DATABASE_NAME = "SQLite_0.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBases.CreateDB._CREATE0);
        }

        public void onCreate(SQLiteDatabase db, String tableName){
            db.execSQL(DataBases.CreateDB.getCreate0(tableName));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException{
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();

        return this;
    }

    public void create(String tableName){
        mDBHelper.onCreate(mDB, tableName);
    }

    public void close(){
        mDB.close();
    }

    public long insertColumn(String tableName, int side, long time , String type, String msg, Bitmap img){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.SIDE, side);
        values.put(DataBases.CreateDB.TIME, time);
        values.put(DataBases.CreateDB.TYPE, type);
        values.put(DataBases.CreateDB.MSG, msg);

        if(type.contains("picture")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] insert_img = byteArrayOutputStream.toByteArray();
            values.put(DataBases.CreateDB.IMG, insert_img);
        }
        else{
            values.put(DataBases.CreateDB.IMG, "null");
        }

        return mDB.insert(tableName, null, values);
    }

    public Cursor selectColumns(String tableName){
        return mDB.query(tableName, null, null, null, null, null, null);
    }

}
