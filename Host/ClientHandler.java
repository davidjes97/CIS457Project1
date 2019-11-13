import java.io. *;
import java.net. *;
import java.util. *;

public class ClientHandler {

    private int controlPort;

    private ServerSocket welcomeSocket;
    private ServerSocket dataServerSocket;
    private Socket dataSocket;
    private Socket connectionSocket;
    private Socket fileWelcomeSocket;

    private DataInputStream dataIn;
    private DataOutputStream outToServer;

    private int numOfOps;
    private int globalPort;
    private int startingPort = 12000;
    private int initialDataPort = 2327;
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
        System.out.println("In the run command with command: " + command);


        connect(sentence);

        if (command.equals("retr:")) {
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

    // Retrieves a file from a user
    private void retrieveFile(String fileName)throws Exception {
        System.out.println("Woah someone is trying to get" + fileName);

        int read = 0;
        byte[] buffer = new byte[4096];

        System.out.println("Attempting fos");
        FileOutputStream fos = new FileOutputStream("./file_folder/" + fileName);
        long fileSize = dataIn.readLong();
        long remaining = fileSize;

        System.out.println("Downloading File.....");

        while ((read = dataIn.read(buffer, 0, Math.min(buffer.length, (int)remaining))) > 0) {
            remaining -= (long)read;
            fos.write(buffer, 0, read);
        }
        System.out.println("\nFile Successfully downloaded.");
        fos.close();
    }

    private void connect(String sentence) throws Exception {

        System.out.println("Gonna try to connect to the other host?");
        System.out.println("This is my sentence in connect " + sentence);

        int connectionPort = setNewPort();
    
        this.welcomeSocket = new ServerSocket(connectionPort);

        outToServer.writeBytes(connectionPort + " " + sentence + "\n");
    
        this.dataSocket = this.welcomeSocket.accept();
    
        this.dataIn = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
    
    }

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

    public void sendKeyword(String keyword) throws Exception {
        setNewPort();
        outToServer.writeBytes(globalPort + " " + keyword + " " + '\n');
        dataServerSocket = new ServerSocket(globalPort);
        dataSocket = dataServerSocket.accept();
        dataIn = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
        
    }

    public String retrieveFileNames() throws Exception {

        String sentence;
        sentence = dataIn.readUTF();
        return sentence;

    }

    public void cleanUp() throws Exception {
        dataServerSocket.close();
        dataSocket.close();
        System.out.println("Data Socket closed");
    }
}
