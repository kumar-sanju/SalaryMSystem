package com.sanju.salarymsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import com.sanju.salarymsystem.SalaryContract.FeedEntry;

/**
 * Created by jmprathab on 03/11/15.
 */
public class SalaryDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Salary.db";
    public static final int DATABASE_VERSION = 2;

    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
            FeedEntry._ID + INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
            FeedEntry.COLUMN_NAME_DATE + INT_TYPE + COMMA_SEP +
            FeedEntry.COLUMN_NAME_IN_TIME + INT_TYPE + COMMA_SEP +
            FeedEntry.COLUMN_NAME_OUT_TIME + INT_TYPE + COMMA_SEP +
            FeedEntry.COLUMN_NAME_PAY + INT_TYPE + COMMA_SEP +
            FeedEntry.COLUMN_NAME_OTPAY + INT_TYPE + ")";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public SalaryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    public long insertDetails(long date, long inTime, long outTime, int pay, int otPay) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedEntry.COLUMN_NAME_DATE, date);
        contentValues.put(FeedEntry.COLUMN_NAME_IN_TIME, inTime);
        contentValues.put(FeedEntry.COLUMN_NAME_OUT_TIME, outTime);
        contentValues.put(FeedEntry.COLUMN_NAME_PAY, pay);
        contentValues.put(FeedEntry.COLUMN_NAME_OTPAY, otPay);
        return database.insert(FeedEntry.TABLE_NAME, null, contentValues);
    }

    public int getNumberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, FeedEntry.TABLE_NAME);
        return numRows;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public ArrayList<Data> getAllData() {
        ArrayList<Data> arrayList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor result = database.rawQuery("select * from " + FeedEntry.TABLE_NAME + " ORDER BY " + FeedEntry.COLUMN_NAME_DATE, null);
        result.moveToFirst();

        while (!result.isAfterLast()) {
            long id = result.getLong(0);
            long day = result.getLong(1);
            long inTime = result.getLong(2);
            long outTime = result.getLong(3);
            int pay = result.getInt(4);
            int otPay = result.getInt(5);
            Data data = new Data(id, day, inTime, outTime, pay, otPay);
            arrayList.add(data);
            result.moveToNext();
        }
        return arrayList;
    }

    public ArrayList<Data> getDataBetweenDates(long start, long end) {
        ArrayList<Data> arrayList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "select * from " + FeedEntry.TABLE_NAME + " WHERE " + FeedEntry.COLUMN_NAME_DATE + " BETWEEN " + start + " and " + end + " ORDER BY " + FeedEntry.COLUMN_NAME_DATE;
        Cursor result = database.rawQuery(query, null);
        //Cursor result = database.rawQuery("select * from " + FeedEntry.TABLE_NAME + " ORDER BY " + FeedEntry.COLUMN_NAME_DATE, null);

        result.moveToFirst();

        while (!result.isAfterLast()) {
            long id = result.getLong(0);
            long day = result.getLong(1);
            long inTime = result.getLong(2);
            long outTime = result.getLong(3);
            int pay = result.getInt(4);
            int otPay = result.getInt(5);
            Data data = new Data(id, day, inTime, outTime, pay, otPay);
            arrayList.add(data);
            result.moveToNext();
        }
        return arrayList;
    }

    public int updateDetails(long id, long date, long inTime, long outTime, int pay, int otPay) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedEntry.COLUMN_NAME_DATE, date);
        contentValues.put(FeedEntry.COLUMN_NAME_IN_TIME, inTime);
        contentValues.put(FeedEntry.COLUMN_NAME_OUT_TIME, outTime);
        contentValues.put(FeedEntry.COLUMN_NAME_PAY, pay);
        contentValues.put(FeedEntry.COLUMN_NAME_OTPAY, otPay);
        return database.update(FeedEntry.TABLE_NAME, contentValues, "_id = ?", new String[]{Long.toString(id)});
    }

    public int deleteDetails(long id) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(FeedEntry.TABLE_NAME, "_id = ?", new String[]{Long.toString(id)});
    }

    public Data getData(long id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor result = database.rawQuery("select * from " + FeedEntry.TABLE_NAME + " WHERE _id= " + id, null);
        if (result.moveToFirst()) {
            long day = result.getLong(1);
            long inTime = result.getLong(2);
            long outTime = result.getLong(3);
            int pay = result.getInt(4);
            int otPay = result.getInt(5);
            Data data = new Data(id, day, inTime, outTime, pay, otPay);
            return data;
        }
        return null;
    }
}
