import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Server {

    private ServerSocket serverSocket;

    private Map<Socket, DataOutputStream> outputStreams;

    private Map<Socket, Service> runningServices;



    public Server(int port) throws IOException {
        outputStreams = new HashMap<>();
        runningServices = new HashMap<>();
        System.out.println("Starting Server. Listening on port number: " + port);
        listen(port);
    }

    private void listen(int port) throws IOException {
        serverSocket = new ServerSocket(8888);
        while(true){
            createServiceForNewConnection(serverSocket.accept());
        }
    }

    private void createServiceForNewConnection(Socket socket) throws IOException {
        System.out.println("Client has connected to server! Client connection info: " + socket.toString());
        outputStreams.put(socket, new DataOutputStream(socket.getOutputStream()));
        runningServices.put(socket, new Service(this, socket));
    }

    public void sendToAll(String message) {
        System.out.println("Sending message to all clients...");
        List<DataOutputStream> list = outputStreams.keySet().stream().map(key -> outputStreams.get(key)).collect(Collectors.toList());
        synchronized (list){
            list.stream().forEach(output -> {
                try {
                    System.out.println("Sending message to client :" + output.toString());
                    output.writeUTF("Message for all: " + message);
                    output.flush();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            });
        }


//        outputStreams.values().stream().forEach(output -> {
//            try{
//                output.writeUTF("Message for all: " + message);
//            } catch (IOException error){
//                System.out.println(error);
//            }
//        });
    }

    public void response(Socket socket, String message) throws IOException {
//        System.out.println("Responding message: " + message + "; to client: " + socket.toString());
        outputStreams.get(socket).writeUTF("Server: " + message);
    }

    public synchronized void removeConnection(Socket socket) {
        System.out.println("Removing connection: " + socket.toString());
        outputStreams.remove(socket);
        runningServices.remove(socket);
        try{
            socket.close();
        } catch (IOException error){
            error.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(8888);
    }

}
