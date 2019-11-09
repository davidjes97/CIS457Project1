import java.io. *;
import java.net. *;
import java.util. *;

public class ftpserver implements Runnable {

    private static ServerSocket welcomeSocket;

    public ftpserver() throws Exception {
        try {
            welcomeSocket = new ServerSocket(12000);

        } catch (IOException ioEx) {
            System.out.println("\nUnable to set up port!");
            System.exit(1);
        }

        public void run(){
            while (true) {
                try{
                    Socket connectionSocket = welcomeSocket.accept();

                    System.out.println("\nNew client accepted.\n");
                    ClientHandler handler = new ClientHandler(connectionSocket);

                    handler.start();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }   
        }
    }     
}

class ClientHandler implements Runnable extends Thread {

    private Socket dataSocket;
    private Socket controlSocket;

    private DataOutputStream outToClient;
    private BufferedReader inFromClient;

    private DataOutputStream dataOutToClient;

    private boolean hostIsRunning;

    public ClientHandler(Socket connection) throws Exception{
        this.connectionSocket = connection;
        this.outToClient = new DatOutputStream(this.connectionSocket.getOutputStream());
        this.inFromClient = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream());
    }

    public String run() {
        try{
            this.hostIsRunning = true;
            waitForRequest();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void waitForRequest() throws Exception{
        while(this.hostIsRunning){
            String fromClient = null;
            fromClient = this.inFromClient.readLine();
            processRequest(fromClient);
        }
    }

    private void runCommand(String sentence) throws Exception {
        StringTokenizer tokens = new StringTokenizer(sentence);
        int port = Integer.parseInt(tokens.nextToken());
        String command = tokens.nextToken();

        this.dataSocket = new Socket(this.connectionSocket.getInetAddress(), port);

        this.dataOutToClient = new DataOutputStream(this.dataSocket.getOutputStream());

        if(command.equals("retr:")){
            retrieve(tokens.nextToken());
        } else if (command.equals("quit:")){
            endConnection();
        }
    }

    private void retrieve(String fileName) throws Exception{
        FileInputStream fileToClient = new FileInputStream("./ServerFiles/" + fileName);
        byte[] fileData = new byte[1024];
        int bytes = 0;
        while ((bytes = fileToClient.read(fileData, 0, fileData.length)) != -1) {
          this.dataOutToClient.write(fileData, 0, bytes);
        }
        fileToClient.close();
        endConnection();
    }

    private void endConnection() throws Exception {
        this.dataOutToClient.close();
        this.dataSocket.close();
        this.hostIsRunning = false;
    }
}