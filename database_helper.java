
package com.example.loginregister;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.widget.Toast;

/**
 * Created by Bhavya on 2/20/2016.
 */
public class database_helper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "User1.db";
    public static final String TABLE_NAME = "user_table1";
    public static final String COL1 = "ID";
    public static final String COL2 = "NAME";
    public static final String COL3 = "username";
    public static final String COL4 = "password";
    public static final String COL5 = "bloodgroup";
   

    public database_helper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,username TEXT,password TEXT, bloodgroup TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, String username, String password, String bloodgroup){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL2, name);
		content.put(COL3, username);
		content.put(COL4, password);
		content.put(COL5, bloodgroup);
		long result =  db.insert(TABLE_NAME, null, content);
        if(result == -1) return false;
        else return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }
   
    public Context getApplicationContext() {
        Context context = getApplicationContext();
        return context;
    }
}

