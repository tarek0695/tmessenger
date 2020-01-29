package com.pixelhubllc.tmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    private String recvUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        recvUserId = getIntent().getExtras().get("visit_user_id").toString();

        Toast.makeText(this, recvUserId, Toast.LENGTH_SHORT).show();
    }
}
