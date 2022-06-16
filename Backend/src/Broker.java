

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Broker {

    Socket connectionSocket = null;
    ServerSocket serverSocket = null;
    private static int port;

    protected static String brokerIP;
    protected static int brokerPort;

    public static String  MainServerIP;
    public static int MainServerPort;

    // contains hashtag key. Also for every key we have a list that contains the channelName that published the hashtag.
    public static HashMap<String, ArrayList<String>> registeredUsers;
    // contains channelName key. for every key we have a list that contains channelName's ip at .get(0) position and port at  .get(1) position.
    public static HashMap<String, ArrayList<String>> UsersInfo;

    String text = "";
    String extension = "";
    ArrayList<byte[]> file = new ArrayList<>();

    public Broker() {
        registeredUsers = new HashMap<String, ArrayList<String>>();
        UsersInfo = new HashMap<String, ArrayList<String>>();

        MainServerPort = 5000;

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        port = Integer.parseInt(args[0]);
        Broker broker = new Broker();
        broker.init();
        broker.updateMainServer();
        broker.start();
    }


    public void init() throws IOException {
        serverSocket = new ServerSocket(port);
        brokerPort = port;
        brokerIP = Broker.getIP();

        System.out.println("Broker's Ip : " + brokerIP);
    }

    public void updateMainServer() {
        try {
            Socket connectToMS = new Socket(UserNode.MainServerIP, MainServerPort);
            ObjectOutputStream out = new ObjectOutputStream(connectToMS.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(connectToMS.getInputStream());

            out.writeUTF("Broker");
            out.flush();

            out.writeObject("Init");
            out.flush();

            out.writeObject(brokerIP);
            out.flush();

            out.writeObject(String.valueOf(brokerPort));
            out.flush();

            connectToMS.close();
            out.close();
            in.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void start() throws IOException, ClassNotFoundException {
        while(true){

            connectionSocket = serverSocket.accept();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());

            String task = objectInputStream.readUTF();

            if (task.equals("Sender")){
                text = objectInputStream.readUTF();

            }

            if (task.equals("File Sender")){
                int len = objectInputStream.readInt();

                extension = objectInputStream.readUTF();

                for (int i = 0; i < len; i++){
                    byte[] chunk = (byte[] ) objectInputStream.readObject();
                    file.add(chunk);
                }

            }

            if (task.equals("Receiver")){

                objectOutputStream.writeUTF(text);
                objectOutputStream.flush();
                System.out.println("Send "+ text);
            }

            if (task.equals("File Receiver")){

                objectOutputStream.writeInt(file.size());
                objectOutputStream.flush();

                objectOutputStream.writeUTF(extension);
                objectOutputStream.flush();


                for (byte[] multimediaFile : file) {
                    objectOutputStream.writeObject(multimediaFile);
                    objectOutputStream.flush();
                }
            }



        }
    }

    private static String getIP()
    {
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }


    }
