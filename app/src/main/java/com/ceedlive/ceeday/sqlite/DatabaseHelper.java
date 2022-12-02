package com.ceedlive.ceeday.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.ceedlive.ceeday.Constant;
import com.ceedlive.ceeday.data.DdayItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;

    private static DatabaseHelper mDatabaseHelper = null;

    private static final String CLT_DDAY = "CLT_DDAY";
    private static final String CLT_DDAY_ID = "_ID";
    private static final String CLT_DDAY_DATE = "DATE";
    private static final String CLT_DDAY_TITLE = "TITLE";
    private static final String CLT_DDAY_DESCRIPTION = "DESCRIPTION";
    private static final String CLT_DDAY_DIFF_DAYS = "DIFF_DAYS";
    private static final String CLT_DDAY_NOTIFICATION = "NOTIFICATION";

    private static final String TAG = "DatabaseHelper";

    // SQLiteOpenHelper
    // 주요 함수는 onCreate, onUpgrade, onOpen 이며 데이타베이스 생성과 관리, 존재여부에 대한 역할을 한다.

    // SQLiteDatabase
    // 실질적으로 CRUD 를 수행하는데 쓰인다.

    // 출처: https://mainia.tistory.com/670 [녹두장군 - 상상을 현실로]

    // Is it OK to have one instance of SQLiteOpenHelper shared by all Activities in an Android application?
    // https://stackoverflow.com/questions/8888530/is-it-ok-to-have-one-instance-of-sqliteopenhelper-shared-by-all-activities-in-an

    // super(context, name, factory, version) 함수의 version 값이 올라가면 onUpgrade 함수가 호출된다.
    private DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    public static DatabaseHelper getInstance(Context context) {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(context.getApplicationContext(),
                    Constant.SQLITE.DB_FILE_NAME,
                    null,
                    Constant.SQLITE.DB_VERSION);
        }
        return mDatabaseHelper;
    }

    /**
     * Database 가 존재하지 않을 때, 딱 한번 실행된다.
     * DB를 만드는 역할을 한다.
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // String 보다 StringBuffer가 Query 만들기 편하다.
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE IF NOT EXISTS CLT_DDAY ");
        sb.append(" ( ");
        sb.append(" _ID INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" DATE TEXT, ");
        sb.append(" TITLE TEXT, ");
        sb.append(" DESCRIPTION TEXT, ");
        sb.append(" DIFF_DAYS TEXT, ");
        sb.append(" NOTIFICATION INTEGER ");
        sb.append(" ) ");

        // SQLite Database로 쿼리 실행
        sqLiteDatabase.execSQL(sb.toString());

        // 출처: https://cocomo.tistory.com/409 [Cocomo Coding]
    }

    /**
     * Application의 버전이 올라가서 Table 구조가 변경되었을 때 실행된다.
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if (oldVersion == 1) {
            try {

                Toast.makeText(mContext, "버전이 올라갔습니다.", Toast.LENGTH_SHORT).show();

                sqLiteDatabase.beginTransaction();
                sqLiteDatabase.execSQL("ALTER TABLE CLT_DDAY ADD COLUMN NOTIFICATION INTEGER DEFAULT 0");
                sqLiteDatabase.setTransactionSuccessful();
            } catch (IllegalStateException e) {

            } finally {
                sqLiteDatabase.endTransaction();
            }
        }

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    /**
     *
     * @param ddayItem
     */
    public long addDday(DdayItem ddayItem) {

        // 방법1
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append(" INSERT INTO CLT_DDAY ");
//        stringBuffer.append(" ( TITLE, DESCRIPTION, DATE, DIFF_DAYS ) ");
//        stringBuffer.append(" VALUES ");
//        stringBuffer.append(" ( ?, ?, ?, ?) ");
//
//        sqLiteDatabase.execSQL(stringBuffer.toString(),
//                new Object[] {
//                        ddayItem.getTitle(),
//                        ddayItem.getDescription(),
//                        ddayItem.getDate(),
//                        ddayItem.getDiffDays()
//                });

        // 방법2
        long result = 0;

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( "TITLE", ddayItem.getTitle() ); // TITLE 필드명
        values.put( "DESCRIPTION", ddayItem.getDescription() ); // DESCRIPTION 필드명
        values.put( "DATE", ddayItem.getDate() ); // DATE 필드명
        values.put( "NOTIFICATION", ddayItem.getNotification() ); // NOTIFICATION 필드명
        values.put( "DIFF_DAYS", ddayItem.getDiffDays() ); // DIFF_DAYS 필드명

        try {
            // 새로운 Row 추가
            result = db.insert("CLT_DDAY", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != db) {
                db.close(); // 연결종료
            }
        }

        return result;
    }

    /**
     * How to retrieve the last autoincremented ID from a SQLite table?
     * How to get the "autoincrement" value from the last insert?
     * @return
     */
    public long getLastAutoIncrementedId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        long lastId = 0;

        // SELECT ROWID from MYTABLE order by ROWID DESC limit 1
        try {
            String sql = "SELECT ROWID FROM CLT_DDAY ORDER BY ROWID DESC LIMIT 1";
            cursor = db.rawQuery(sql, null);
            if ( null != cursor && cursor.moveToFirst() ) {
                lastId = cursor.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return lastId;
    }

    /**
     *
     * @return
     */
    public List<DdayItem> getDdayList() {

        // FIXME 다음 주석처리된 코드는 버그 발생 코드, 수정 필요
//        StringBuffer sb = new StringBuffer();
//        sb.append(" SELECT _ID, DATE, TITLE, DESCRIPTION, DIFF_DAYS FROM CLT_DDAY ");
//
//        // 읽기 전용 DB 객체를 만든다.
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery(sb.toString(), null);

//        List<DdayItem> ddayItemList = new ArrayList<>();
//        DdayItem ddayItem;
//        while ( cursor.moveToNext() ) {
//            ddayItem = new DdayItem();
//
//            ddayItem.set_id( cursor.getInt(0) );
//            ddayItem.setTitle( cursor.getString(1) );
//            ddayItem.setDescription( cursor.getString(2) );
//            ddayItem.setDate( cursor.getString(3) );
//            ddayItem.setDiffDays( cursor.getString(4) );
//
//            ddayItemList.add(ddayItem);
//        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        List<DdayItem> ddayItemList = new ArrayList<>();
        DdayItem ddayItem;

        try {
            cursor = db.query("CLT_DDAY", null, null, null, null, null, null);
            if (cursor != null) {
                String date, title, description;
                String diffDays;
                int id, notification;

                while ( cursor.moveToNext() ) {
                    date = cursor.getString(cursor.getColumnIndex("DATE"));
                    title = cursor.getString(cursor.getColumnIndex("TITLE"));
                    description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));

                    diffDays = cursor.getString(cursor.getColumnIndex("DIFF_DAYS"));
                    id = cursor.getInt(cursor.getColumnIndex("_ID"));
                    notification = cursor.getInt(cursor.getColumnIndex("NOTIFICATION"));

                    ddayItem = new DdayItem.Builder(date, title, description)
                            .rowId(id)
                            .diffDays(diffDays)
                            .notification(notification)
                            .build();

                    ddayItemList.add(ddayItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return ddayItemList;
    }

    /**
     * id에 해당하는 DdayItem 객체 얻어오기
     * @param _id
     * @return
     */
    public DdayItem getDday(int _id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        DdayItem ddayItem = null;

        try {
            cursor = db.query(CLT_DDAY,
                    new String[] { CLT_DDAY_ID, CLT_DDAY_TITLE, CLT_DDAY_DESCRIPTION, CLT_DDAY_DATE, CLT_DDAY_DIFF_DAYS, CLT_DDAY_NOTIFICATION },
                    CLT_DDAY_ID + " = ?",
                    new String[] { String.valueOf(_id) }, null, null, null, null);

            if (cursor.moveToFirst()) {
                String date = cursor.getString(cursor.getColumnIndex("DATE"));
                String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));

                String diffDays = cursor.getString(cursor.getColumnIndex("DIFF_DAYS"));
                int id = cursor.getInt(cursor.getColumnIndex("_ID"));
                int notification = cursor.getInt(cursor.getColumnIndex("NOTIFICATION"));

                ddayItem = new DdayItem.Builder(date, title, description)
                        .rowId(id)
                        .diffDays(diffDays)
                        .notification(notification)
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return ddayItem;
    }

    /**
     * Dday 정보 업데이트
     * @param ddayItem
     * @return
     */
    public int updateDday(DdayItem ddayItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;

        try {
            // SQLite in Android How to update a specific row
            // This is the cleanes solution to update a specific row.
            ContentValues contentValues = new ContentValues();
            contentValues.put( "DATE", ddayItem.getDate() );
            contentValues.put( "TITLE", ddayItem.getTitle() );
            contentValues.put( "DESCRIPTION", ddayItem.getDescription() );
            contentValues.put( "NOTIFICATION", ddayItem.getNotification() );
            contentValues.put( "DIFF_DAYS", ddayItem.getDiffDays() );

            result = db.update("CLT_DDAY",
                    contentValues,
                    "_ID = ?",
                    new String[] { String.valueOf(ddayItem.get_id()) });

//        StringBuffer sb = new StringBuffer();
//        sb.append(" UPDATE CLT_DDAY ");
//        sb.append(" SET ");
//        sb.append("   DATE = ? ");
//        sb.append(" , TITLE = ? ");
//        sb.append(" , DESCRIPTION = ? ");
//        sb.append(" WHERE _ID = ? ");
//
//        db.execSQL(sb.toString(),
//                new Object[] {
//                        ddayItem.getDate(),
//                        ddayItem.getTitle(),
//                        ddayItem.getDescription(),
//                        ddayItem.get_id()
//                });

//        db.execSQL("update mytable set name='Park' where id=5;");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        // updating row
        return result;
    }

    /**
     * Dday 정보 삭제하기
     * @param _id
     */
    public int deleteDday(int _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;

        try {
            result = db.delete(CLT_DDAY,
                    CLT_DDAY_ID + " = ?",
                    new String[] { String.valueOf(_id) });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return result;
    }

    /**
     * Dday 정보 카운트
     * @return
     */
    public int getDdayListCount() {
        String countQuery = "SELECT * FROM " + CLT_DDAY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
