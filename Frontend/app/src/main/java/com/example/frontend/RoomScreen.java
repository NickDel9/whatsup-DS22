package com.example.frontend;

import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

    public static RoomBinding binding;
    private String username;
    private String room;
    private ArrayAdapter<String> adapter;

    public static ChatArrayAdapter chatArrayAdapter;
    public static boolean side = false;
    private ArrayList<String> arrayList;

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

        binding.messagesView.setAdapter(adapter);

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

        binding.messagesView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        binding.messagesView.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                binding.messagesView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

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
