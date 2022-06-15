import jdk.jshell.execution.Util;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;

public class UserNode extends Thread{

    // hash of brokers. We need them for the brokersInfo
    public static ArrayList<BigInteger> brokerHashes = new ArrayList<BigInteger>();

    public static String Chat;

    // HashMap brokersInfo contains broker's hash and its IP/Port
    public static HashMap<BigInteger, ArrayList<String>> brokersInfo = new HashMap<BigInteger, ArrayList<String>>();

    Socket connectToServer;
    String MainServerIP = "127.0.0.1";
    int MainServerPort = 5000;
    UUID uuid;
    public static String name;
    public static String chatroom;
    public static String port;
    ObjectInputStream in;
    ObjectOutputStream out;
    HashMap<String,String> brokers = new HashMap<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        port = args[0];

//        UserNode userNode = new UserNode();
//
//        userNode.init();

        UserNode userNode1serNode = new UserNode();
        userNode1serNode.start();

    }

    public void init() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(MainServerIP, MainServerPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        this.uuid = UUID.randomUUID();

        Scanner sc = new Scanner(System. in);
        System.out.println("Hello! Type your Username!");
        this.name = sc.next();

        if (!name.equals("")){
            out.writeUTF("UserNode");
            out.flush();
            out.writeObject(name);
            out.flush();
            out.writeObject(uuid);
            out.flush();
            out.writeObject(getIP());
            out.flush();
            out.writeObject(port);
            out.flush();

            int brokers = in.readInt();

            for (int i = 0; i < brokers; i++) {
                String brokerIP = in.readUTF();
                String brokerPort = in.readUTF();

                BigInteger hash = Info.Util.hash(brokerIP + brokerPort);

                brokerHashes.add(hash);

                ArrayList<String> connectInfo = new ArrayList<String>();
                connectInfo.add(0, brokerIP);
                connectInfo.add(1, brokerPort);

                brokersInfo.put(hash, connectInfo);
            }

            Collections.sort(brokerHashes);

        }

        System.out.println("Create or Join a room by typing the name.");
        this.chatroom = sc.next();

        if (!this.chatroom.equals("")){

            Thread th = new UserListeners(this.in , this.out);
            th.start();

            out.writeUTF("Inserts");
            out.flush();

            out.writeUTF(this.chatroom);
            out.flush();

            out.writeUTF(this.name);
            out.flush();



           // menu();
        }

    }

    public void run() {

        try {
            init();

            System.out.println("Welcome "+name+" ! Start typing text and press enter to send it! .\n" +
                            "If you want to send Video/Image press 1 & Enter and then the path.");

            Thread sendmsg = new SendMessage(this.in, this.out);
            sendmsg.start();



        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void menu() throws IOException, ClassNotFoundException {


    }


    public static List<String> hashTopic(String topic) {
        BigInteger no = Info.Util.hash(topic);

        if (no.compareTo(brokerHashes.get(0)) > 0 && no.compareTo(brokerHashes.get(1)) < 0) {
            return brokersInfo.get(brokerHashes.get(1));
        } else if (no.compareTo(brokerHashes.get(1)) > 0 && no.compareTo(brokerHashes.get(2)) < 0) {
            return brokersInfo.get(brokerHashes.get(2));
        } else {
            return brokersInfo.get(brokerHashes.get(0));
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

    public static File ReInitFile(ArrayList<MultimediaFile> videoFile , String newPath) throws IOException
    {
        File file = new File(newPath);
        int size = 0;
        for (MultimediaFile element : videoFile) {
            size += element.getMultimediaFileChunk().length;
        }
        byte[] arr = new byte[size];

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        int a = 0;
        for (MultimediaFile item : videoFile) {
            try{
                byte[] temp = item.getMultimediaFileChunk();
                for (byte b : temp) {
                    arr[a] = b;
                    a++;
                }

            } catch (Exception e) {
                System.out.println("err");
            }
        }
        fileOutputStream.write(arr);
        fileOutputStream.close();

        return file;

    }

}
