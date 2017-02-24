package ds130626.pmu.rti.etf.rs.accelgame.android.score;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by smiljan on 2/13/17.
 */

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+Contract.TABLE_SCORE.TABLE_NAME+"("
        + Contract.TABLE_SCORE._ID+" integer primary key,"
                + Contract.TABLE_SCORE.COLUMN_NAME_LEVEL+" text,"
                + Contract.TABLE_SCORE.COLUMN_NAME_PLAYER+" text,"
                + Contract.TABLE_SCORE.COLUMN_NAME_TIME+" real)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static class Contract{
        public static String DATABASE_NAME = "scores.db";
        public static class TABLE_SCORE implements BaseColumns {
            public static String TABLE_NAME = "score";
            public static String COLUMN_NAME_PLAYER = "player";
            public static String COLUMN_NAME_LEVEL = "level";
            public static String COLUMN_NAME_TIME = "time";
        }

    }
}
