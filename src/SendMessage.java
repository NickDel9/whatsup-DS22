import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class SendMessage extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;

    public SendMessage(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {

            try {

                String ans = sc.nextLine();
                if (!ans.equals("")) {

                    List<String> rensposibleBroker = UserNode.hashTopic(UserNode.name);

                    out.writeUTF("Send");

                    out.flush();

                    out.writeUTF(rensposibleBroker.get(0));
                    out.flush();

                    out.writeUTF(rensposibleBroker.get(1));
                    out.flush();

                    out.writeUTF(UserNode.name);
                    out.flush();

                    out.writeUTF(UserNode.chatroom);
                    out.flush();

                    // connect to broker
                    Socket socket = new Socket(rensposibleBroker.get(0), Integer.parseInt(rensposibleBroker.get(1)));
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                    objectOutputStream.writeUTF("Sender");
                    objectOutputStream.flush();

                    objectOutputStream.writeUTF(ans);
                    objectOutputStream.flush();

                    socket.close();
                    objectInputStream.close();
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
