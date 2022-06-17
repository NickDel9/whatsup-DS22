package com.example.frontend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
                    displayOwnMessage("text");
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

            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File dir = new File(root.getAbsolutePath() + "/app_data");

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,dir);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(intent, 1);
        });

        binding.fileButton.setOnClickListener(view -> {

            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Video.Media.INTERNAL_CONTENT_URI);
            galleryIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

            galleryIntent.setType("video/*, image/*");
            String[] mimetypes = {"image/*", "video/*"};
            galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            startActivityForResult(galleryIntent, 10);

        });



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 || requestCode == 10) {

            if (resultCode == Activity.RESULT_OK)
            {
                assert data != null;

                Uri vid = data.getData();
                String videoPath = getRealPathFromURI(vid);

                String[] params = new String[1];
                params[0] = videoPath;

                Log.e("asdad" , params[0]);

                FileChat chat = new FileChat();
                chat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (params));


            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("VIDEO_UPLOAD_TAG", "Recording or picking video is cancelled");
            }
            else {
                Log.i("VIDEO_UPLOAD_TAG", "Recording or picking video has got some error");
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    100);
        }
    }

    public boolean displayOwnMessage(String type) throws IOException {

        if (type.equals("text")){
            Chat chat = new Chat();
            chat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String [])null);
        }
        else{
            String URL = type;
            int index = type.lastIndexOf('.');
            String extension = type.substring(index + 1);

            Log.e("DEBUG", extension +" "+URL);

            chatArrayAdapter.add(new Message(side, URL , extension));
        }


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

        String URL = path;
        Log.e("DEBUG", extension +" "+URL);

        chatArrayAdapter.add(new Message(!side, URL , extension));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<byte[]> Chunks(String path) throws IOException {

        int numOfChunks;
        byte[] multimediaFileChunk = null;

//        Uri.fromFile(new File(path));
////
//        byte[] arrayBytes = Files.readAllBytes(Paths.get(String.valueOf(Uri.fromFile(new File(path)))));

        InputStream iStream =  getContentResolver().openInputStream(Uri.fromFile(new File(path)));
        byte[] arrayBytes = getBytes(iStream);

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

    public byte[] getBytes(InputStream inputStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (IOException ioException) {
            return null;
        }
        return byteBuffer.toByteArray();
    }


    private class FileChat extends AsyncTask<String,String,String>{

        String path = "";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... strings) {
            List<String> rensposibleBroker = MainActivity.hashName(MainActivity.name);

            try {

                // connect to broker
                Socket socket = new Socket(rensposibleBroker.get(0), Integer.parseInt(rensposibleBroker.get(1)));
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                ArrayList<byte[]> chunks = new ArrayList<>(Chunks(strings[0]));

                objectOutputStream.writeUTF("File Sender");
                objectOutputStream.flush();

                objectOutputStream.writeInt(chunks.size());
                objectOutputStream.flush();

                path = strings[0];

                int index = strings[0].lastIndexOf('.');
                String extension = strings[0].substring(index + 1);

                objectOutputStream.writeUTF(extension);
                objectOutputStream.flush();

                System.out.println(extension);

                //push
                for (byte[] chunk : chunks){
                    objectOutputStream.writeObject(chunk);
                    objectOutputStream.flush();
                }

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

                MainActivity.out.writeUTF("File");
                MainActivity.out.flush();




            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                displayOwnMessage(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
