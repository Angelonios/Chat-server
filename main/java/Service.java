import java.io.*;
import java.net.Socket;

public class Service implements Runnable {


    private Server server;

    private Socket socket;

    private DataInputStream input;

    public Service(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        new Thread(this).start();
    }

    public void run() {
        try {
            input = new DataInputStream(socket.getInputStream());
            while (true) {
                String message = input.readUTF();
//                System.out.println(message);
//                server.response(socket, message);
//                if (message.equals("QUIT") || message == null) {
//
//                }
                server.sendToAll(message);
            }
        } catch(EOFException endOfFileError){

        } catch(IOException IOError) {
            IOError.printStackTrace();
        } finally{
                server.removeConnection(socket);
        }
    }
}

