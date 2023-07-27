package com.codeninjas.bowwow.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codeninjas.bowwow.models.RemindersModel;

import java.util.ArrayList;

public class SqlHelperClass {
    SqlOpenHelper myhelper;
    public SqlHelperClass(Context context)
    {
        myhelper = new SqlOpenHelper(context);
    }

    public long insertRemindersData(RemindersModel remindersModel)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SqlOpenHelper.title, remindersModel.getTitle());
        contentValues.put(SqlOpenHelper.message, remindersModel.getMessage());
        contentValues.put(SqlOpenHelper.date, remindersModel.getDate());
        contentValues.put(SqlOpenHelper.time, remindersModel.getTime());
        contentValues.put(SqlOpenHelper.ID, remindersModel.getId());
        long id = dbb.insert(SqlOpenHelper.TABLE_NAME, null , contentValues);

        return id;
    }

    public ArrayList<RemindersModel> getRemindersData()
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {SqlOpenHelper.ID,SqlOpenHelper.title,SqlOpenHelper.message, SqlOpenHelper.date, SqlOpenHelper.time};
        Cursor cursor =db.query(SqlOpenHelper.TABLE_NAME,columns,null,null,null,null,null);

        RemindersModel remindersModel;
        ArrayList<RemindersModel> addRemindersModelArrayList = new ArrayList<>();
        while (cursor.moveToNext())
        {
            String cid =cursor.getString(0); //cursor.getColumnIndex(SqlOpenHelper.UID)
            String title =cursor.getString(1);
            String message =cursor.getString(2);
            String date =cursor.getString(3);
            String time =cursor.getString(4);
            remindersModel = new RemindersModel(title, date, time, message, cid);
            addRemindersModelArrayList.add(remindersModel);
        }
        return addRemindersModelArrayList;
    }

    static class SqlOpenHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "bowwow";    // Database Name
        private static final String TABLE_NAME = "reminders";   // Table Name public
        private static final String title = "title";
        private static final String date = "date";
        private static final String ID = "id";
        private static final String time = "time";
        private static final String message = "message";
        private static final int DATABASE_Version = 1;    // Database Version

        private static final String CREATE_WRTITINGS_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID+", "+title+" VARCHAR(255) ,"+ message+" VARCHAR(225) ,"+ date+" VARCHAR(225) ," + time+ " VARCHAR(225));";

        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public SqlOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_WRTITINGS_TABLE);
            } catch (Exception e) {
//                Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
//                Message.message(context,"OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
//                Message.message(context,""+e);
            }
        }
    }

}
