import java.io. *;
import java.net. *;
import java.util. *;

class centralServer {

    private static ServerSocket welcomeSocket;
    public static void main(String argv[])throws Exception {
        try {
            welcomeSocket = new ServerSocket(12000);

        } catch (IOException ioEx) {
            System.out.println("\nUnable to set up port!");
            System.exit(1);
        }

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();

            System.out.println("\nNew client accepted.\n");
            ClientHandler handler = new ClientHandler(connectionSocket);

            handler.start();
        }
    }
}

class ClientHandler extends Thread{

    private Socket connectionSocket;
    private DataOutputStream outToClient;
    private BufferedReader inFromClient;

    public ClientHandler(Socket socket) {

        connectionSocket = socket;

        try {
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            //TODO:  Add to client list here (userList)
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public void run() {
        String fromClient;
        String clientCommand;
        int port;
        boolean connectionIsLive = true;
        final File folder = new File("file_folder/");
        String frstln;

        while (connectionIsLive) {
            //Add in functions for using centralized server
        }
    }

}