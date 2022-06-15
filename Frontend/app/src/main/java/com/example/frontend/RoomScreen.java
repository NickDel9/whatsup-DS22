package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend.databinding.RoomBinding;

public class RoomScreen extends AppCompatActivity {

    RoomBinding binding;
    private String username;
    private String room;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        username = getIntent().getExtras().get("username").toString();
        room = getIntent().getExtras().get("room").toString();
    }
}
