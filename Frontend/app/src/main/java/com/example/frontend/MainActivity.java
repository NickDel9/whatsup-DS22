package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.frontend.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding.connect.setOnClickListener(view -> {

            String username = binding.username.getText().toString();
            String room = binding.room.getText().toString();

            //open room
            Intent myIntent = new Intent(MainActivity.this, RoomScreen.class);
            myIntent.putExtra("username", username);
            myIntent.putExtra("room", room);
            MainActivity.this.startActivity(myIntent);


        });



    }
}