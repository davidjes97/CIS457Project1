import java.io. *;
import java.net. *;
import java.util. *;

public class ClientHandler{

    private int controlPort;

    private ServerSocket welcomeSocket;
    private Socket dataSocket;
    private Socket connectionSocket;

    private DataInputStream dataIn;
    private DataOutputStream outToServer;

    private int numOfOps;
    private int globalPort;
    private int startingPort = 8000;
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
    // private void retrieveFile(String fileName) throws Exception{
    //     FileOutputStream retrievedFile = new FileOutputStream(fileName);
    //     byte[] fileData = new byte[1024];
    //     int bytes = 0;

    //     while((bytes = dataIn.read(fileData)) != -1){
    //         retrievedFile.write(fileData, 0 ,bytes);
    //     }
    //     retrievedFile.close();
    // }

    private void retrieveFile(String fileName) throws Exception {
        int filePort = getNewPort();
        outToServer.writeBytes(port + " " + fileName + " " + '\n');

        ServerSocket fileWelcomeSocket = new ServerSocket(filePort);
        Socket fileDataSocket = fileWelcomeSocket.accept();
        DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
        int read = 0;
        byte[] buffer = new byte[4096];
        
        FileOutputStream fos = new FileOutputStream(fileName);
        long fileSize = inData.readLong();
        long remaining = fileSize;

        System.out.println("Downloading File.....");

        while ((read = inData.read(buffer, 0, Math.min(buffer.length, (int)remaining)))> 0) {
            remaining -= (long)read;
            fos.write(buffer, 0, read);
        }
        System.out.println("\nFile Successfully downloaded.");
        fileWelcomeSocket.close();
        inData.close();
        dataSocket.close();
    }


    // private void initializeConnection(String sentence) throws Exception{


    //     this.welcomeSocket = new ServerSocket(getNewPort());

    //     outToServer.writeBytes(this.getNewPort() + " " + sentence + " " + '\n');

    //     this.dataSocket = this.welcomeSocket.accept();
    //     this.dataIn = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
    // }

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
                System.out.println("Here4");
                for (int i=0; i < temp.length; ++i) {
                        dataOutToClient.writeUTF (temp[i].getName() + "" + '\n');  
                }
                dataOutToClient.writeUTF("EOF");
                dataOutToClient.close();
                dataSocket.close();
                System.out.println("Data Socket closed");
    }
}

