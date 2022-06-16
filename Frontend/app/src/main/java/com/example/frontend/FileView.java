package com.example.frontend;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend.databinding.RoomBinding;
import com.example.frontend.databinding.VideoViewBinding;

import java.util.ArrayList;

public class FileView extends AppCompatActivity {

    public static VideoViewBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);

        binding = VideoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        String Type = getIntent().getExtras().get("Type").toString();

        String URI = getIntent().getExtras().get("URI").toString();

        if (Type.equals("video")){
            MediaController mediaController = new MediaController(getApplicationContext());

            binding.video.setMediaController(mediaController);


            binding.video.setVideoURI(Uri.parse(URI));
            binding.video.start();
        }
        else{
            binding.img.setImageURI(Uri.parse(URI));
        }


        binding.back.setOnClickListener(view -> {

            finish();
        });
    }
}