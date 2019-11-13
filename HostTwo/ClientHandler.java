import java.io. *;
import java.net. *;
import java.util. *;

public class ClientHandler {

    private int controlPort;

    private ServerSocket welcomeSocket;
    private Socket dataSocket;
    private Socket connectionSocket;
    private Socket fileWelcomeSocket;

    private DataInputStream dataIn;
    private DataOutputStream outToServer;

    private int numOfOps;
    private int globalPort;
    private int startingPort = 12000;
    private int initialDataPort = 2328;
    private String fileServerName;
    final File folder = new File("file_folder/");

    public ClientHandler(String serverName, int controlPort, String userName, String hostName, String speed)throws Exception {
        globalPort = initialDataPort;
        this.controlPort = controlPort;
        this.connectionSocket = new Socket(serverName, controlPort);
        this.outToServer = new DataOutputStream(connectionSocket.getOutputStream());
        sendList(userName, hostName, speed);
    }

    public String runCommand(String sentence) throws Exception {
        String message = "";
        StringTokenizer token = new StringTokenizer(sentence);
        String command = token.nextToken();
        command = command.toLowerCase();

        if (command.equals("connect:")){
            connect(command);
            message = "Connected to " + token;
        }
        else if (command.equals("retr:")) {
            String fileName = token.nextToken();
            retrieveFile(fileName);
            message = "Succesfully downloaded \"" + fileName + "\".";
        } else if (command.equals("quit")) {
            message = "Disconnected from server.";
            closeAllConnections();
        }
        closeAllConnections();

        return message;
    }

    // Gets new port
    private int setNewPort() {
        return globalPort += 2;
    }

    // private void retrieveFile(String fileName) throws Exception {
    //     FileOutputStream retrievedFile = new FileOutputStream("./file_folder/" + fileName);
    //     byte[] fileData = new byte[1024];
    //     int bytes = 0;
    //     while ((bytes = dataIn.read(fileData)) != -1) {
    //       retrievedFile.write(fileData, 0, bytes);
    //     }
    //     retrievedFile.close();
    // }

    // Retrieves a file from a user
    private void retrieveFile(String fileName)throws Exception {
        int port = setNewPort();
        System.out.println("Woah someone is trying to get" + fileName);
        outToServer.writeBytes(port + " " + "retr:" + " " + fileName + " " + '\n');

        ServerSocket fileWelcomeSocket = new ServerSocket(port);
        Socket fileDataSocket = fileWelcomeSocket.accept();
        DataInputStream inData = new DataInputStream(new BufferedInputStream(fileDataSocket.getInputStream()));
        int read = 0;
        byte[] buffer = new byte[4096];

        FileOutputStream fos = new FileOutputStream(fileName);
        long fileSize = inData.readLong();
        long remaining = fileSize;

        System.out.println("Downloading File.....");

        while ((read = inData.read(buffer, 0, Math.min(buffer.length, (int)remaining))) > 0) {
            remaining -= (long)read;
            fos.write(buffer, 0, read);
        }
        System.out.println("\nFile Successfully downloaded.");
        fileWelcomeSocket.close();
        inData.close();
        fileDataSocket.close();
    }

    private void connect(String sentence) throws Exception {

        int connectionPort = setNewPort();
    
        this.welcomeSocket = new ServerSocket(connectionPort);

        outToServer.writeBytes(connectionPort + " " + sentence);
    
        this.dataSocket = this.welcomeSocket.accept();
    
        this.dataIn = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
    
    }

    // private void connect(String sentence) {
    //     StringTokenizer tokens = new StringTokenizer(sentence);

    //     fileServerName = tokens.nextToken();
    //     fileServerName = tokens.nextToken();
    //     initialDataPort = Integer.parseInt(tokens.nextToken());
        
    //     try{
    //         fileWelcomeSocket = new Socket(fileServerName, initialDataPort);
    //     }catch(Exception welcomeException){
    //         System.out.println(welcomeException);
    //     }
    // }

    // private void initializeConnection(String sentence) throws Exception{


    //     this.welcomeSocket = new ServerSocket(getNewPort());

    //     outToServer.writeBytes(this.getNewPort() + " " + sentence + " " + '\n');

    //     this.dataSocket = this.welcomeSocket.accept();
    //     this.dataIn = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
    // }

    private void closeAllConnections()throws Exception {
        this.dataIn.close();
        this.welcomeSocket.close();
        this.dataSocket.close();
        this.numOfOps ++;
    }

    private void sendList(String userName, String hostName, String speed)throws Exception {

        setNewPort();

        // TODO: Get port from server
        outToServer.writeBytes(userName + " " + hostName + " " + speed + " " + globalPort + " " + '\n');
        // Setting stuff up
        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), globalPort);
        DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
        File[] temp = folder.listFiles();
        if (temp == null) {
            dataOutToClient.writeUTF("EOF");
        }
        System.out.println("Here4");
        for (int i = 0; i < temp.length; ++i) {
            dataOutToClient.writeUTF(temp[i].getName() + "" + '\n');
        }
        dataOutToClient.writeUTF("EOF");
        dataOutToClient.close();
        dataSocket.close();
        System.out.println("Data Socket closed");
    }
}
