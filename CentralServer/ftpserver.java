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
    System.out.println(connectionSocket.getInetAddress() + " connected");
    while (connectionIsLive) {

      DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
      BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

      fromClient = inFromClient.readLine();

      StringTokenizer tokens = new StringTokenizer(fromClient);
      frstln = tokens.nextToken();
      port = Integer.parseInt(frstln);
      clientCommand = tokens.nextToken();

      /*******************************************************
      *  _      _     _   
      * | |    (_)   | |  
      * | |     _ ___| |_ 
      * | |    | / __| __|
      * | |____| \__ \ |_ 
      * |______|_|___/\__|
      *  
      * Lists all file names in the file folder.
      ********************************************************/
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
      /*******************************************************
      *   ____        _ _   
      *  / __ \      (_) |  
      * | |  | |_   _ _| |_ 
      * | |  | | | | | | __|
      * | |__| | |_| | | |_ 
      *  \___\_\\__,_|_|\__|
      *                                                
      * Is notified by the clien that the connection is being closed
      ********************************************************/
      if (clientCommand.equals("quit")) {
        System.out.println(connectionSocket.getInetAddress() + " disconnected");
        connectionSocket.close();
        connectionIsLive = false;
      }
    }
  }
}
