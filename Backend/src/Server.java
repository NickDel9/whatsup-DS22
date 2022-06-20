import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Server extends Thread{

    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    private static ArrayList<ArrayList<String>> brokers = new ArrayList<>();
    private static HashMap<String, ArrayList<String>> appnodes = new HashMap<>();
    private static HashMap<String, ArrayList<String>> rooms = new HashMap<>();
    private static HashMap<Socket , HashMap<ObjectOutputStream,ObjectInputStream>> clients = new HashMap<>();;
    private static HashMap<Socket,String> RoomPerSocket = new HashMap<>();
    private static HashMap<String,ArrayList<String>> RoomPerUser = new HashMap<>();
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

        ServerSocket serverSocket = null;
        Socket connectionSocket = null;

        serverSocket = new ServerSocket(5000);

        while (true){
            connectionSocket = serverSocket.accept();

            Thread server = new Server(connectionSocket);
            server.start();
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

                    // get streams of current socket and save them to hashmap
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


                }
                else if (task.equals("Inserts")){
                    System.out.println("Inserts");
                    String room = objectInputStream.readUTF();
                    String name = objectInputStream.readUTF();

                    RoomPerSocket.put(connectionSocket , room);

                    if (!RoomPerUser.containsKey(room)){
                        ArrayList<String> users = new ArrayList<>();
                        users.add(name);
                        RoomPerUser.put(room , users);
                    }
                    else{
                        RoomPerUser.get(room).add(name);
                    }


                    if (rooms.containsKey(room)){
                        objectOutputStream.writeUTF("Join Room");
                        objectOutputStream.flush();
                        rooms.get(room).add(name);

                        System.out.println("User joined");

                        System.out.println(clients.size());

                        for (Socket s : clients.keySet()){
                            if (RoomPerSocket.get(s).equals(room)){
                                ObjectOutputStream out = (ObjectOutputStream) clients.get(s).keySet().toArray()[0];

                                out.writeUTF("User joined");
                                out.flush();

                                out.writeUTF(name); //send to client the usernames
                                out.flush();
                            }



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
                else if(task.equals("Online Users")){

                    String room = objectInputStream.readUTF();

                     objectOutputStream.writeUTF("Get UserList");
                     objectOutputStream.flush();

                     objectOutputStream.writeInt(RoomPerUser.get(room).size());
                     objectOutputStream.flush();

                     for (String s : RoomPerUser.get(room)){
                         objectOutputStream.writeUTF(s);
                         objectOutputStream.flush();
                     }


                 }

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }







    }

}
