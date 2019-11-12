import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class ftpServerHandler implements Runnable{

    private Socket dataSocket;
    private Socket controlSocket;

    private DataOutputStream outToClient;
    private BufferedReader inFromClient;

    private DataOutputStream dataOutToClient;

    private boolean hostIsRunning;

    public ftpServerHandler(Socket connection) throws Exception{
        this.controlSocket = connection;
        this.outToClient = new DataOutputStream(this.controlSocket.getOutputStream());
        this.inFromClient = new BufferedReader(new InputStreamReader(this.controlSocket.getInputStream()));
    }

    public void run() {
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
            runCommand(fromClient);
        }
    }

    private void runCommand(String sentence) throws Exception {
        StringTokenizer tokens = new StringTokenizer(sentence);
        int port = Integer.parseInt(tokens.nextToken());
        String command = tokens.nextToken();

        this.dataSocket = new Socket(this.controlSocket.getInetAddress(), port);

        this.dataOutToClient = new DataOutputStream(this.dataSocket.getOutputStream());

        if(command.equals("retr:")){
            retrieve(tokens.nextToken());
        } else if (command.equals("quit:")){
            endConnection();
        }
    }

    private void retrieve(String fileName) throws Exception{
        FileInputStream fileToClient = new FileInputStream("./file_folder/" + fileName);
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