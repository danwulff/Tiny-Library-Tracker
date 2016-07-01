package com.epicodus.tinylibrarytracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {
    @Bind(R.id.emailDisplay) TextView mEmailDisplay;
    @Bind(R.id.passwordDisplay) TextView mPasswordDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mEmailDisplay.setText("Email provided: " + intent.getStringExtra("email"));
        mPasswordDisplay.setText("Password provided: " + intent.getStringExtra("password"));
    }
}
