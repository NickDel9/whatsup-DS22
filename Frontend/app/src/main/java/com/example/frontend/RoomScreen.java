package com.example.frontend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.frontend.File.MultimediaFile;
import com.example.frontend.databinding.RoomBinding;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoomScreen extends AppCompatActivity {

    private Camera mCamera;

    public static RoomBinding binding;
    private String username;
    private String room;
    private ArrayAdapter<String> adapter;

    public static Context context;

    public static ChatArrayAdapter chatArrayAdapter;
    public static boolean side = false;
    private ArrayList<String> arrayList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);

        binding = RoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(),  R.layout.right);
        binding.messagesView.setAdapter(chatArrayAdapter);

        username = getIntent().getExtras().get("username").toString();
        room = getIntent().getExtras().get("room").toString();

        binding.sendButton.setOnClickListener(view -> {
            if (binding.messageInput.getText().length() > 0) {
                try {
                    displayOwnMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.plusButton.setOnClickListener(view -> {
            if (binding.more.getVisibility() == View.INVISIBLE)
                binding.more.setVisibility(View.VISIBLE);
            else
                binding.more.setVisibility(View.INVISIBLE);
        });




        binding.messagesView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        binding.messagesView.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                binding.messagesView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });


        binding.messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("asaa  ",chatArrayAdapter.getItem(i).message);
                Log.e("file ?  ",chatArrayAdapter.getItem(i).type);

                if (chatArrayAdapter.getItem(i).type.equals("mp4")){
                    Intent intent = new Intent(RoomScreen.this , FileView.class);
                    intent.putExtra("URI" , chatArrayAdapter.getItem(i).message);
                    intent.putExtra("Type" , "video");
                    RoomScreen.this.startActivity(intent);
                }
                else if (chatArrayAdapter.getItem(i).type.equals("png") || chatArrayAdapter.getItem(i).type.equals("jpg")){
                    Intent intent = new Intent(RoomScreen.this , FileView.class);
                    intent.putExtra("URI" , chatArrayAdapter.getItem(i).message);
                    intent.putExtra("Type" , "picture");
                    RoomScreen.this.startActivity(intent);
                }
            }
        });

        binding.messageInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });

        binding.cameraButton.setOnClickListener(view -> {

            getCameraPermission();

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(intent, 1);
        });

        binding.fileButton.setOnClickListener(view -> {

        });



    }

    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    100);
        }
    }

    public boolean displayOwnMessage() throws IOException {

        Chat chat = new Chat();
        chat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String [])null);

        return true;
    }

    public static boolean displayRemoteMessage(String data , String type){

        if (Objects.equals(type, "text"))
            chatArrayAdapter.add(new Message(!side, data, "string"));
        else{
            int index = data.lastIndexOf('.');
            String extension = data.substring(index + 1);
            initFile(data , extension);
        }

        return true;
    }

    protected static void initFile(String path, String extension)
    {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String URL = path;
        Log.e("DEBUG", extension +" "+URL);

        chatArrayAdapter.add(new Message(!side, URL , extension));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<byte[]> Chunks(String path) throws IOException {

        int numOfChunks;
        byte[] multimediaFileChunk = null;

        byte[] arrayBytes = Files.readAllBytes(Paths.get(path));
        ArrayList<byte[]> multimediaFiles = new ArrayList<byte[]>();

        int totalBytes = arrayBytes.length;

        int kiloByte = (int) Math.pow(2, 10);
        int chunkSize = 512 * kiloByte;

        if (totalBytes % chunkSize == 0) {
            numOfChunks = totalBytes / chunkSize;
        } else {
            numOfChunks = totalBytes / chunkSize + 1;
        }

        for (int i = 0; i < numOfChunks; i++) {
            if (i == numOfChunks - 1) {
                multimediaFileChunk = new byte[totalBytes - (i * chunkSize)];
                for (int j = 0; j < multimediaFileChunk.length; j++) {
                    multimediaFileChunk[j] = arrayBytes[(i * chunkSize) + j];
                }
            } else {
                multimediaFileChunk = new byte[chunkSize];
                for (int j = 0; j < chunkSize; j++) {
                    multimediaFileChunk[j] = arrayBytes[(i * chunkSize) + j];
                }
            }

            byte[] multimediaFile = multimediaFileChunk;


            multimediaFiles.add(multimediaFile);
        }
        return multimediaFiles;

    }






    private class Chat extends AsyncTask<String,String,String>{


        @Override
        protected String doInBackground(String... strings) {

            List<String> rensposibleBroker = MainActivity.hashName(MainActivity.name);

            try {

                // connect to broker
                Socket socket = new Socket(rensposibleBroker.get(0), Integer.parseInt(rensposibleBroker.get(1)));
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                objectOutputStream.writeUTF("Sender");
                objectOutputStream.flush();


                objectOutputStream.writeUTF(binding.messageInput.getText().toString());
                objectOutputStream.flush();

                socket.close();
                objectInputStream.close();
                objectOutputStream.close();


                MainActivity.out.writeUTF("Send");
                MainActivity.out.flush();

                MainActivity.out.writeUTF(rensposibleBroker.get(0));
                MainActivity.out.flush();

                MainActivity.out.writeUTF(rensposibleBroker.get(1));
                MainActivity.out.flush();

                MainActivity.out.writeUTF(MainActivity.name);
                MainActivity.out.flush();

                MainActivity.out.writeUTF(MainActivity.chatroom);
                MainActivity.out.flush();

                MainActivity.out.writeUTF("Text");
                MainActivity.out.flush();




            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            chatArrayAdapter.add(new Message(side, binding.messageInput.getText().toString(),"string"));
            binding.messageInput.setText("");
        }
    }
}
