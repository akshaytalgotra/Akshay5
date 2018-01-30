package com.example.akshay.akshay5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by AKSHAY on 28-01-2018.
 */

public class ProfileActivity extends AppCompatActivity {

    Button LogOut;
    TextView EmailShow;
    String EmailHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //twitter username fetching

        String username = getIntent().getStringExtra("username");
        TextView uname = findViewById(R.id.textView2);
        uname.setText(username);


        LogOut = findViewById(R.id.button);
        EmailShow = findViewById(R.id.EmailShow);


        Intent intent = getIntent();
        EmailHolder = intent.getStringExtra(UserLoginActivity.UserEmail);
        EmailShow.setText(EmailHolder);


        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

                Intent intent = new Intent(ProfileActivity.this, UserLoginActivity.class);

                startActivity(intent);

                Toast.makeText(ProfileActivity.this, "Log Out Successfully", Toast.LENGTH_LONG).show();


            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        String name = sharedPreferences.getString("username", "");
        //String name = sharedPreferences.getString("pass","");
        EmailShow.setText(name);
    }
}