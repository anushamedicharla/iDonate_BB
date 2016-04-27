package com.example.loginregister;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    com.example.loginregister.database_helper myDb;
    Button blogin;
    Button bRegister;
    Button bHospitalNearMe;
    EditText etName, etUsername, etBloodGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new com.example.loginregister.database_helper(this);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etName = (EditText) findViewById(R.id.etName);
        blogin = (Button) findViewById(R.id.blogin);
        bRegister = (Button) findViewById(R.id.bRegister);
        bHospitalNearMe = (Button) findViewById(R.id.bHospitalsNearMe);
        blogin.setOnClickListener(this);
        bRegister.setOnClickListener(this);
        bHospitalNearMe.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.blogin:
                Intent intent = new Intent(this, com.example.loginregister.Login.class);
                startActivity(intent);
                break;
            case R.id.bRegister:
                Intent intent1 = new Intent(this, com.example.loginregister.Register.class);
                startActivity(intent1);
                break;
            case R.id.bHospitalsNearMe:
                Intent intent2 = new Intent(this, com.example.loginregister.MapsActivity.class);
                startActivity(intent2);
                break;


        }
    }


}
