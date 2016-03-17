package com.example.loginregister;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity implements View.OnClickListener {
    database_helper myDb;
    Button bRegister;
    Button bviewAll;
    EditText etName,etBloodGroup, etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myDb = new database_helper(this);
        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etBloodGroup = (EditText) findViewById(R.id.etBloodGroup);
        bRegister = (Button) findViewById(R.id.bRegister);
        bviewAll = (Button) findViewById(R.id.bviewAll);
        bRegister.setOnClickListener(this);
        bviewAll.setOnClickListener(this);

    }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bRegister:
                    boolean isInserted = false;
                    String name = etName.getText().toString();
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    String bloodgroup = etBloodGroup.getText().toString();
                    if(name.equals("") || username.equals("") || password.equals("") || bloodgroup.equals(""))
                    {
                        Toast.makeText(Register.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        isInserted = myDb.insertData(name, username, password, bloodgroup);
                        if(isInserted == true) {
                            etUsername.setText("");
                            etPassword.setText("");
                            etBloodGroup.setText("");
                            etName.setText("");
                            Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Registration failed ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id. bviewAll:
                    Cursor res = myDb.getAllData();
                    if (res.getCount() == 0) {
                        Toast.makeText(Register.this, "count 0", Toast.LENGTH_SHORT).show();
                        showMessage("error", "nothing found");
                        return;

                    }
                    StringBuffer buffer = new StringBuffer();
                    while (res.moveToNext()) {
                        buffer.append("Id: " + res.getString(0) + "\n");
                        buffer.append("name: " + res.getString(1) + "\n\n");
                        buffer.append("username: " + res.getString(2) + "\n\n");
                    }
                    showMessage("Data", buffer.toString());

            }
        }

            public void showMessage(String title, String message)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.show();
            }
}
