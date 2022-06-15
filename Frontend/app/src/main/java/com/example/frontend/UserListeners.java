package com.example.frontend;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class UserListeners extends Thread{

    ObjectInputStream in;
    ObjectOutputStream out;

    public UserListeners(ObjectInputStream in, ObjectOutputStream out){
        this.in = in;
        this.out = out;


    }

    public void run(){

        String task = null;

        try {

            while (true){
                task = in.readUTF();


                if (task.equals("Create Room")){
                    System.out.println("create room "+ MainActivity.name);
                }
                else if (task.equals("Join Room")){
                    System.out.println("join room "+ MainActivity.name);
                }
                else if (task.equals("User joined")){
                    ArrayList<String> friends = (ArrayList<String>) in.readObject();

                    for(int i = 0; i < friends.size(); i++){
                        if (!friends.get(i).equals(MainActivity.name))
                            System.out.println(i+". "+friends.get(i));
                    }
                }
                else if (task.equals("Receive")){
                    String BrokerIp = in.readUTF();
                    String BrokerPort = in.readUTF();
                    String senderName = in.readUTF();

//                    System.out.println("receive "+senderName);

                    // connect to broker
                    Socket socket = new Socket(BrokerIp, Integer.parseInt(BrokerPort));
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                    objectOutputStream.writeUTF("Receiver");
                    objectOutputStream.flush();

                    String text = objectInputStream.readUTF();
                    System.out.println(senderName + " : " +text);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            RoomScreen.displayRemoteMessage(senderName + " : " +text , "text");
                        }
                    });


                    socket.close();
                    objectInputStream.close();
                    objectOutputStream.close();

                }
                else if (task.equals("Receive File")){
                    String BrokerIp = in.readUTF();
                    String BrokerPort = in.readUTF();
                    String senderName = in.readUTF();

//                    System.out.println("receive "+senderName);

                    // connect to broker
                    Socket socket = new Socket(BrokerIp, Integer.parseInt(BrokerPort));
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                    objectOutputStream.writeUTF("File Receiver");
                    objectOutputStream.flush();

                    //pull
                    ArrayList<MultimediaFile> file = new ArrayList<>();
                    int len = objectInputStream.readInt();

                    String extension = objectInputStream.readUTF();

                    for (int i = 0; i < len; i++){
                        MultimediaFile chunk = (MultimediaFile) objectInputStream.readObject();
                        file.add(chunk);
                    }
                    System.out.println(file.size());

                    int size = 0;
                    for (MultimediaFile element : file) {
                        size += element.getMultimediaFileChunk().length;
                    }
                    byte[] arr = new byte[size];

                    int a = 0;
                    for (MultimediaFile item : file) {
                        try{
                            byte[] temp = item.getMultimediaFileChunk();
                            for (byte b : temp) {
                                arr[a] = b;
                                a++;
                            }

                        } catch (Exception e) {
                            System.out.println("ERROR");
                        }
                    }

                    String path = writeFileData("element"+file.size() , arr);
                    //Desktop.getDesktop().open(UserNode.ReInitFile(file , "src/out/element"+file.size() +"."+ extension));

                    socket.close();
                    objectInputStream.close();
                    objectOutputStream.close();


                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            RoomScreen.displayRemoteMessage(path, "file");
                        }
                    });
                }
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private String writeFileData(String name , byte[] data) {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dir = new File(root.getAbsolutePath() + "/app_data");

        String path = dir.getAbsolutePath() + "/"+ name + ".mp4";

        if (!new File(path).exists()) {

            Log.e("NAME", name);
            File file = new File(dir.toString(), name + ".mp4");

            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(file);

                fileOutputStream.write(data);

                Log.e("PATH", String.valueOf(file));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return path;
    }


}

