package com.example.myapplicationjava.task3;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_PASSWORD = "password";

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        db = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_LOGIN + " TEXT NOT NULL, " + COLUMN_PASSWORD + " TEXT NOT NULL )");
    }

    public void addUser(String login, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, login);
        values.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_NAME, null, values);
    }

    public boolean checkUserExists(String login) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN + " = ?", new String[]{login});
        return cursor.moveToFirst();
    }

    public boolean checkUserPassword(String login, String password) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{login, password});
        return cursor.moveToFirst();
    }

    public void close() {
        db.close();
    }
}