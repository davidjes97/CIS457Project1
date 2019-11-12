import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler{

    private int controlPort;

    private ServerSocket welcomeSocket;
    private ServerSocket dataServerSocket;
    private Socket dataSocket;
    private Socket connectionSocket;

    private DataInputStream dataIn;
    private DataOutputStream outToServer;

    private int numOfOps;
    private int globalPort;
    private int startingPort = 12000;
    final File folder = new File("file_folder/");

    public ClientHandler(String serverName, int controlPort, String userName, String hostName, String speed) throws Exception {
        globalPort = startingPort;
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

        // initializeConnection(sentence);
        

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

    //Gets new port
    private int setNewPort(){
        return globalPort += 2;
    }

    //Retrieves a file from a user
    private void retrieveFile(String fileName) throws Exception{
        FileOutputStream retrievedFile = new FileOutputStream(folder + fileName);
        byte[] fileData = new byte[1024];
        int bytes = 0;

        while((bytes = dataIn.read(fileData)) != -1){
            retrievedFile.write(fileData, 0 ,bytes);
        }
        retrievedFile.close();
    }

    private void closeAllConnections() throws Exception{
        this.dataIn.close();
        this.welcomeSocket.close();
        this.dataSocket.close();
        this.numOfOps++;
    }

    private void sendList(String userName, String hostName, String speed) throws Exception {

                setNewPort();

                //TODO: Get port from server
                outToServer.writeBytes(userName + " " + hostName + " " + speed + " " + globalPort + " " + '\n');
                //Setting stuff up
                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), globalPort);
                DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
                File[] temp = folder.listFiles();
                if(temp == null){
                dataOutToClient.writeUTF("EOF");
                }
                for (int i=0; i < temp.length; ++i) {
                        dataOutToClient.writeUTF (temp[i].getName() + "" + '\n');  
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

