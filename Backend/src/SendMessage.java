



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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



                    List<String> rensposibleBroker = UserNode.hashName(UserNode.name);

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

                    if (ans.equals("1")){

                        System.out.print("Type your File Path and press Enter . . . -----> ");

                        String path = sc.next();

                        ArrayList<byte[]> chunks = new ArrayList<>(Chunks(path));

                        // connect to broker
                        Socket socket = new Socket(rensposibleBroker.get(0), Integer.parseInt(rensposibleBroker.get(1)));
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                        objectOutputStream.writeUTF("File Sender");
                        objectOutputStream.flush();

                        objectOutputStream.writeInt(chunks.size());
                        objectOutputStream.flush();

                        int index = path.lastIndexOf('.');
                        String extension = path.substring(index + 1);

                        objectOutputStream.writeUTF(extension);
                        objectOutputStream.flush();

                     //   System.out.println(extension);

                        //push
                        for (byte[] chunk : chunks){
                            objectOutputStream.writeObject(chunk);
                            objectOutputStream.flush();
                        }

                        socket.close();
                        objectInputStream.close();
                        objectOutputStream.close();

                        // tell to server to notify other peer for incoming file
                        out.writeUTF("File");
                        out.flush();


                    }
                    else{

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

                        // tell to server to notify other peer for incoming text message
                        out.writeUTF("Text");
                        out.flush();
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

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
                if (multimediaFileChunk.length >= 0)
                    System.arraycopy(arrayBytes, (i * chunkSize), multimediaFileChunk, 0, multimediaFileChunk.length);
            } else {
                multimediaFileChunk = new byte[chunkSize];
                if (chunkSize >= 0)
                    System.arraycopy(arrayBytes, (i * chunkSize), multimediaFileChunk, 0, chunkSize);
            }

            byte[] multimediaFile = multimediaFileChunk;


            multimediaFiles.add(multimediaFile);
        }
        return multimediaFiles;

    }


}
