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
                    System.out.println("create room "+ UserNode.name);
                }
                else if (task.equals("Join Room")){
                    System.out.println("join room "+ UserNode.name);
                }
                else if (task.equals("User joined")){
                    ArrayList<String> friends = (ArrayList<String>) in.readObject();

                    for(int i = 0; i < friends.size(); i++){
                        if (!friends.get(i).equals(UserNode.name))
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

                    socket.close();
                    objectInputStream.close();
                    objectOutputStream.close();

                }
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
