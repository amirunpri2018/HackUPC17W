package com.hackupc2017w.motocare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends AppCompatActivity {

    private Button mLogIn;
    private EditText username;
    private Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(App.APP_PREFS, MODE_PRIVATE);
        preferences.getInt("userId", -1);

       /* if (preferences.getInt("userId", -1) != -1) {
            startMainActivity();
        }*/
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);

        c = this;

        mLogIn = (Button) findViewById(R.id.log_in_buton);
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
                    startMainActivity();
                }
            }
        });

    }

    private void startMainActivity() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.register_button) public void registerNewUser() {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        //finish();
    }
}
