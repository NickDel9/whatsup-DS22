import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Server extends Thread{

    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    // key = username | value = password
    private static HashMap<String, String> appnodeInfo = new HashMap<>();
    private static ArrayList<ArrayList<String>> brokers = new ArrayList<>();
    private static HashMap<String, ArrayList<String>> appnodes = new HashMap<>();
    private static HashMap<String, ArrayList<String>> rooms = new HashMap<>();
    private static HashMap<Socket , HashMap<ObjectOutputStream,ObjectInputStream>> clients = new HashMap<>();;
    private static HashMap<Socket,String> RoomPerSocket = new HashMap<>();
    Socket connectionSocket;

    public Server(Socket connectionSocket) {
        try {
            this.connectionSocket = connectionSocket;
            objectOutputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        // get registered users
        init();

        ServerSocket serverSocket = null;
        Socket connectionSocket = null;

        serverSocket = new ServerSocket(5000);

        while (true){
            connectionSocket = serverSocket.accept();

            Thread server = new Server(connectionSocket);
            server.start();
        }


    }

    private static void init() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/credentials.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");

                String userName = values[0];
                String password = values[1];

                //appnodeInfo.put(userName, password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){

        String task = null;
        try {

            while (true){


                task = objectInputStream.readUTF();
                System.out.println(task);


                 if (task.equals("Broker")){
                    System.out.println("Broker Connected");
                    String action = (String) objectInputStream.readObject();

                    if (action.equals("Init")){
                        System.out.println("Init");

                        String Ip = (String) objectInputStream.readObject();
                        String port = (String) objectInputStream.readObject();

                        ArrayList<String> info = new ArrayList<>();
                        info.add(Ip);
                        info.add(port);

                        brokers.add(info);
                    }

                }
                else if (task.equals("UserNode")){

                     HashMap temp = new HashMap<>();
                     temp.put(objectOutputStream ,objectInputStream );
                     clients.put(connectionSocket , temp);


                    System.out.println("UserNode Connected");

                    String name = (String)objectInputStream.readObject();
                    UUID id = (UUID)objectInputStream.readObject();
                    String ip = (String)objectInputStream.readObject();
                    String port = (String)objectInputStream.readObject();

                    ArrayList<String> infos = new ArrayList<>();
                    infos.add(ip);
                    infos.add(port);
                    appnodes.put(name, infos);

                    objectOutputStream.writeInt(brokers.size());
                    objectOutputStream.flush();

                    for (int i = 0; i < brokers.size(); i++){
                        objectOutputStream.writeUTF(brokers.get(i).get(0));
                        objectOutputStream.flush();

                        objectOutputStream.writeUTF(brokers.get(i).get(1));
                        objectOutputStream.flush();
                    }

                    appnodeInfo.put(name , id.toString());
                    System.out.println(appnodeInfo.get(name));

                }
                else if (task.equals("Inserts")){
                    System.out.println("Inserts");
                    String room = objectInputStream.readUTF();
                    String name = objectInputStream.readUTF();

                    RoomPerSocket.put(connectionSocket , room);

                    if (rooms.containsKey(room)){
                        objectOutputStream.writeUTF("Join Room");
                        objectOutputStream.flush();
                        rooms.get(room).add(name);

                        ArrayList<String> names = new ArrayList<>();
                        names.addAll(appnodeInfo.keySet());

                        System.out.println("User joined");

                        System.out.println(clients.size());

                        for (Socket s : clients.keySet()){

                            ObjectOutputStream out = (ObjectOutputStream) clients.get(s).keySet().toArray()[0];

                            out.writeUTF("User joined");
                            out.flush();

                            out.writeObject(names); //send to client the usernames
                            out.flush();

                        }

                    }
                    else{
                        objectOutputStream.writeUTF("Create Room");
                        objectOutputStream.flush();
                        ArrayList<String> ns= new ArrayList<>();
                        ns.add(name);
                        rooms.put(room,ns);

                    }
                }

                else if (task.equals("Send")){

                    String BrokerIp = objectInputStream.readUTF();
                    String BrokerPort = objectInputStream.readUTF();

                    String sender = objectInputStream.readUTF();
                    String room = objectInputStream.readUTF();

                    String type = objectInputStream.readUTF();

                    System.out.println("Send "+ sender);
                    System.out.println("Send "+ rooms.get(room).size());

                    System.out.println(clients.size());

                     for (Socket s : clients.keySet()) {
                        if (s != connectionSocket && RoomPerSocket.get(s).equals(room)){
                            ObjectOutputStream out = (ObjectOutputStream) clients.get(s).keySet().toArray()[0];

                            if (type.equals("Text")){
                                out.writeUTF("Receive");
                                out.flush();
                            }
                            else{
                                out.writeUTF("Receive File");
                                out.flush();
                            }

                            out.writeUTF(BrokerIp);
                            out.flush();

                            out.writeUTF(BrokerPort);
                            out.flush();

                            out.writeUTF(sender);
                            out.flush();

                        }


                     }

                }

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }







    }

}
