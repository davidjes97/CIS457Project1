import java.io. *;
import java.net. *;
import java.util. *;

// public class FTPClient {

//     private static ServerSocket welcomeSocket;

//     public static void main(String argv[])throws Exception {
//         try {
//             welcomeSocket = new ServerSocket(12000);

//         } catch (IOException ioEx) {
//             System.out.println("\nUnable to set up port!");
//             System.exit(1);
//         }

//         while (true) {
//             try{
//                 Socket connectionSocket = welcomeSocket.accept();

//                 System.out.println("\nNew client accepted.\n");
//                 ClientHandler handler = new ClientHandler(connectionSocket);

//                 handler.start();
//             }
//             catch(Exception e){
//                 e.printStackTrace();
//             }
//         }
//     }
// }

public class ClientHandler{

    private int controlPort;

    private ServerSocket welcomeSocket;
    private Socket dataSocket;
    private Socket controlSocket;

    private DataInputStream dataIn;
    private DataOutputStream outToServer;

    private int numOfOps;

    public ClientHandler(String serverName, int controlPort) throws Exception {
        this.controlPort = controlPort;
        this.controlSocket = new Socket(serverName, controlPort);
        this.outToServer = new DataOutputStream(controlSocket.getOutputStream());
    }

    public String runCommand(String sentence) throws Exception {
        String message = "";
        StringTokenizer token = new StringTokenizer(sentence);
        String command = token.nextToken();
        command = command.toLowerCase();

        initializeConnection(sentence);
        

        if(command.equals("retr:")){
            String fileName = token.nextToken();
            retrieveFile(fileName);
            message = "Succesfully downloaded \"" + fileName + "\".";
        } else if (command.equals("quit:")){
            message= "Disconnected from server.";
        }
        closeAllConnections();
        return message;
    }

    private int getNewPort(){
        return this.controlPort + 2 + ( 2 + this.numOfOps) % 8;
    }

    private void retrieveFile(String fileName) throws Exception{
        FileOutputStream retrievedFile = new FileOutputStream("./Download/" + fileName);
        byte[] fileData = new byte[1024];
        int bytes = 0;

        while((bytes = dataIn.read(fileData)) != -1){
            retrievedFile.write(fileData, 0 ,bytes);
        }
        retrievedFile.close();
    }

    private void welcomeMessage(int port, String sentence) throws Exception {
        outToServer.writeBytes(port + " " + sentence + " " + "\n");
    }

    private void initializeConnection(String sentence) throws Exception{

        int connectionPort = getNewPort();

        this.welcomeSocket = new ServerSocket(connectionPort);

        welcomeMessage(connectionPort, sentence);

        this.dataSocket = this.welcomeSocket.accept();
        this. dataIn = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
    }

    private void closeAllConnections() throws Exception{
        this.dataIn.close();
        this.welcomeSocket.close();
        this.dataSocket.close();
        this.numOfOps++;
    }
}

