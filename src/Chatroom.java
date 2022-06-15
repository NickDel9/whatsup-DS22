import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Chatroom extends Thread{

    ObjectInputStream in;
    ObjectOutputStream out;

    public Chatroom(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
    }


}
