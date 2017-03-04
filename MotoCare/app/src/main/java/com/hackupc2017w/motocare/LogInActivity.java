package com.hackupc2017w.motocare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogInActivity extends AppCompatActivity {

    private Button mLogIn;
    private Button mRegister;
    private EditText username;
    private Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        c = this;

        mLogIn = (Button) findViewById(R.id.log_in_buton);
        mRegister = (Button) findViewById(R.id.register_button);
        username = (EditText) findViewById(R.id.username);

        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Chekc if user exists in api database
                //if exists then log in OK and intent to main activity
                //bool b = api.existsUser(editextgettext)
                if(false){
                    new AlertDialog.Builder(c)
                            .setTitle("Log In Error")
                            .setMessage("Log In could not be performed")
                            .show();


                }
                else{
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

    }
}
