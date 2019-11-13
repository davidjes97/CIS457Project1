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

        if(command.equals("retr")){
            this.dataSocket = new Socket(this.controlSocket.getInetAddress(), port);

            this.dataOutToClient = new DataOutputStream(this.dataSocket.getOutputStream());
            retrieve(tokens.nextToken());
        } else if (command.equals("quit")){
            endConnection();
        }
    }


    private void retrieve(String filename) throws Exception {
        File folder = new File("file_folder/");
        long fileSize = 0;
        File[] temp = folder.listFiles();     
        for (int i=0; i < temp.length; ++i) {
                if(temp[i].getName().equals(filename)){
                fileSize = temp[i].length();

                }
            }
        FileInputStream fis = new FileInputStream(folder + "/" + filename);
        byte[] buffer = new byte[4096];
        int read;
        this.dataOutToClient.writeLong (fileSize); 
        while((read = fis.read(buffer)) > 0) {
          this.dataOutToClient.write(buffer, 0, read);
        }

        fis.close();
        dataOutToClient.close();
        dataSocket.close();
    }

    private void endConnection() throws Exception {
        System.out.println(controlSocket.getInetAddress() + " disconnected");
        controlSocket.close();
        this.hostIsRunning = false;
    }
}