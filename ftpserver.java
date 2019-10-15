import java.io.*;
import java.net.*;
import java.util.*;
// .................
// .................

class ftpserver {
  public static void main(String argv[]) throws Exception {

    String fromClient;
    String clientCommand;
    byte[] data;
    int port;
    boolean connectionIsLive = true;
    final File folder = new File("file_folder/");

    ServerSocket welcomeSocket = new ServerSocket(12000);
    String frstln;

    Socket connectionSocket = welcomeSocket.accept();

    while (connectionIsLive) {

      DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
      BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

      fromClient = inFromClient.readLine();

      StringTokenizer tokens = new StringTokenizer(fromClient);
      frstln = tokens.nextToken();
      port = Integer.parseInt(frstln);
      clientCommand = tokens.nextToken();

      if (clientCommand.equals("list:")) {

        //Setting stuff up
        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
        DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());


        // ArrayList<String> fileNames = new ArrayList<String>(); 
        File[] temp = folder.listFiles();
        if(temp == null){
          dataOutToClient.writeUTF("Folder does not exist.\n");
        System.out.println("FIXME");
        }
        for (int i=0; i < temp.length; ++i) {
                dataOutToClient.writeUTF (temp[i].getName() + "" + '\n');  
        }
        dataOutToClient.writeUTF("EOF");
        dataOutToClient.close();
        dataSocket.close();
        System.out.println("Data Socket closed");
      }

      /*****************************************
       * Retrieve method:  
       * Takes in a file name and querries for it and sends it.
       */
      if (clientCommand.equals("retr:")) {

        long fileSize = 0;
        String[] words = fromClient.split(" ");
        File[] temp = folder.listFiles();        
        for (int i=0; i < temp.length; ++i) {
                if(temp[i].getName().equals(words[2])){
                fileSize = temp[i].length();

                }
            }
        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
        DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
        FileInputStream fis = new FileInputStream(folder + "/" + words[2]);
        byte[] buffer = new byte[4096];
        int read;
        dataOutToClient.writeLong (fileSize);  
        while((read = fis.read(buffer)) > 0) {
          dataOutToClient.write(buffer, 0, read);
        }

        dataOutToClient.close();
        dataSocket.close();
      }

      if (clientCommand.equals("stor:")) {
        // ..............................
        // ..............................
      }
      if (clientCommand.equals("quit")) {
        System.out.println(connectionSocket.getInetAddress() + " disconnected");
        connectionSocket.close();
        connectionIsLive = false;
      }
    }
  }
}
