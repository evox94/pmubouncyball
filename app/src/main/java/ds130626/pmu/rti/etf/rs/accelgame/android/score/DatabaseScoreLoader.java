package ds130626.pmu.rti.etf.rs.accelgame.android.score;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by smiljan on 2/13/17.
 */

public class DatabaseScoreLoader implements ScoreLoader {
    MyDBHelper dbHelper;

    public DatabaseScoreLoader(Context context){
        dbHelper = new MyDBHelper(context, MyDBHelper.Contract.DATABASE_NAME, null, 1);
    }
    @Override
    public void put(long time, String name, String level) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_TIME, time/1000f);
        values.put(MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_LEVEL, level);
        values.put(MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_PLAYER, name);
        db.insert(MyDBHelper.Contract.TABLE_SCORE.TABLE_NAME, null,values);
    }

    @Override
    public Object getScores(String level) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT "+
                MyDBHelper.Contract.TABLE_SCORE._ID+","+
                MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_PLAYER+","+
                MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_LEVEL+","+
                "printf(\"%.2f\", "+ MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_TIME+") AS "+MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_TIME+
                " FROM "+MyDBHelper.Contract.TABLE_SCORE.TABLE_NAME+" WHERE "+MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_LEVEL+"='"+level+ "' ORDER BY CAST("+MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_TIME+" AS REAL) ASC", null);
    }

    @Override
    public void deleteForLevel(String level) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MyDBHelper.Contract.TABLE_SCORE.TABLE_NAME, MyDBHelper.Contract.TABLE_SCORE.COLUMN_NAME_LEVEL+"='"+level+"'",null);
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MyDBHelper.Contract.TABLE_SCORE.TABLE_NAME, null ,null);
    }
}
