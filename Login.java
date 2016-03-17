package com.example.loginregister;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button blogin;
    TextView Registerlink;
    EditText etUsername, etPassword;
    database_helper myDb = new database_helper(this);
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        blogin = (Button) findViewById(R.id.blogin);
        blogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blogin:
                String userName = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                SQLiteDatabase db = myDb.getReadableDatabase();
                boolean loginstatus = false;
                if(userName.equals("") || password.equals(""))//Checks if all the fields are filled
                {
                    etUsername.setText("");
                    etPassword.setText("");
                    Toast.makeText(Login.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    String[] columns = {myDb.COL1, myDb.COL2, myDb.COL3, myDb.COL4, myDb.COL5};
                    Cursor cursor = db.query(myDb.TABLE_NAME, columns, myDb.COL3 + "=?", new String[]{userName}, null, null, null);
                    if (cursor.getCount() < 1)//Username doesn't exist
                    {
                        cursor.close();
                    } else {

                        cursor.moveToFirst();
                        do {
                            if (userName.equals(cursor.getString(2)) && password.equals(cursor.getString(3))) {
                                loginstatus = true;
                            } else {
                                loginstatus = false;
                            }

                        } while (cursor.moveToNext());
                    }

                    if (loginstatus) {
                        etUsername.setText("");
                        etPassword.setText("");
                        Toast.makeText(getApplicationContext(), "Successfully Logged In ", Toast.LENGTH_SHORT).show();


                    } else {
                        etUsername.setText("");
                        etPassword.setText("");
                        Toast.makeText(getApplicationContext(), "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
                    }

                cursor.close();
                }
                break;


        }
    }

    //To help delete the database incase it is full
    public boolean deleteRow(long rowId)
    {
        SQLiteDatabase db = myDb.getWritableDatabase();
        String where = myDb.COL1 + " = " + rowId;
        return db.delete(myDb.TABLE_NAME, where, null) != 0;

    }

    public void deleteAll()
    {
        SQLiteDatabase db = myDb.getWritableDatabase();
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(myDb.COL1);
        if(c.moveToFirst()){
            do{
                deleteRow(c.getColumnIndex(myDb.COL1));
            }while(c.moveToNext());
        }
        c.close();
    }
    public Cursor getAllRows(){
        SQLiteDatabase db = myDb.getReadableDatabase();
        String[] columns = {myDb.COL1, myDb.COL2, myDb.COL3, myDb.COL4, myDb.COL5};
        Cursor c = db.query(myDb.TABLE_NAME, columns, null, null, null, null, null);
        if(c != null){
            c.moveToNext();
        }
        return c;
    }




}
