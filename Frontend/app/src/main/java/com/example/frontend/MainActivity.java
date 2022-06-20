package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.frontend.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private Socket socket;
    public static ObjectOutputStream out;
    public static ObjectInputStream in;

    // hash of brokers. We need them for the brokersInfo
    public static ArrayList<BigInteger> brokerHashes = new ArrayList<BigInteger>();

    // HashMap brokersInfo contains broker's hash and its IP/Port
    public static HashMap<BigInteger, ArrayList<String>> brokersInfo = new HashMap<BigInteger, ArrayList<String>>();

    String MainServerIP = "192.168.1.8";
    int MainServerPort = 5000;
    public static String name;
    public static String chatroom;
    public static String port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        port = "3013";

        binding.connect.setOnClickListener(view -> {

            name = binding.username.getText().toString();
            chatroom = binding.room.getText().toString();

            String[] params = new String[2];
            params[0] = name;
            params[1] = chatroom;

            Join join = new Join();
            join.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

        });
    }


    private class Join extends AsyncTask<String,String,String> {

        String name;
        String room;
        @Override
        protected String doInBackground(String... strings) {

            name = strings[0];
            room = strings[1];

            try {
                init();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void init() throws IOException {
            socket = new Socket(MainServerIP, MainServerPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeUTF("UserNode");
            out.flush();
            out.writeObject(name);
            out.flush();
            out.writeObject(UUID.randomUUID());
            out.flush();
            out.writeObject(getIP());
            out.flush();
            out.writeObject(port);
            out.flush();

            int brokers = in.readInt();
            for (int i = 0; i < brokers; i++) {
                String brokerIP = in.readUTF();
                String brokerPort = in.readUTF();

                BigInteger hash = Util.hash(brokerIP + brokerPort);

                brokerHashes.add(hash);

                ArrayList<String> connectInfo = new ArrayList<String>();
                connectInfo.add(0, brokerIP);
                connectInfo.add(1, brokerPort);

                brokersInfo.put(hash, connectInfo);
            }

            Collections.sort(brokerHashes);

            chatroom = room;

            if (!chatroom.equals("")){

                Thread th = new UserListeners(in , out);
                th.start();

                out.writeUTF("Inserts");
                out.flush();

                out.writeUTF(chatroom);
                out.flush();

                out.writeUTF(name);
                out.flush();

                //open room
                Intent myIntent = new Intent(MainActivity.this, RoomScreen.class);
                myIntent.putExtra("username", name);
                myIntent.putExtra("room", room);
                MainActivity.this.startActivity(myIntent);

            }
        }

    }

    public static String getIP()
    {
        String ip = "";
        try{
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static List<String> hashName(String topic) {
        BigInteger no = Util.hash(topic);

        if (no.compareTo(brokerHashes.get(0)) > 0 && no.compareTo(brokerHashes.get(1)) < 0) {
            return brokersInfo.get(brokerHashes.get(1));
        } else if (no.compareTo(brokerHashes.get(1)) > 0 && no.compareTo(brokerHashes.get(2)) < 0) {
            return brokersInfo.get(brokerHashes.get(2));
        } else {
            return brokersInfo.get(brokerHashes.get(0));
        }
    }
}