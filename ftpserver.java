import java.io. *;
import java.net. *;
import java.util. *;

class ftpserver {

    private static ServerSocket welcomeSocket;
    public static void main(String argv[])throws Exception {
        try {
            welcomeSocket = new ServerSocket(12000);

        } catch (IOException ioEx) {
            System.out.println("\nUnable to set up port!");
            System.exit(1);
        }

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();

            System.out.println("\nNew client accepted.\n");
            ClientHandler handler = new ClientHandler(connectionSocket);

            handler.start();
        }
    }
}

class ClientHandler extends Thread {

    private Socket connectionSocket;
    private DataOutputStream outToClient;
    private BufferedReader inFromClient;

    public ClientHandler(Socket socket) {

        connectionSocket = socket;

        try {
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public void run() {
        String fromClient;
        String clientCommand;
        int port;
        boolean connectionIsLive = true;
        final File folder = new File("file_folder/");
        String frstln;

        while (connectionIsLive) {

          try {
              fromClient = inFromClient.readLine();

                StringTokenizer tokens = new StringTokenizer(fromClient);
                frstln = tokens.nextToken();
                port = Integer.parseInt(frstln);
                clientCommand = tokens.nextToken();
                System.out.println(connectionSocket.getInetAddress() + " connected");

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
                if (clientCommand.equals("list:")) { // Setting stuff up
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());


                    // ArrayList<String> fileNames = new ArrayList<String>();
                    File[] temp = folder.listFiles();
                    if (temp == null) {
                        dataOutToClient.writeUTF("Folder does not exist.\n");
                        System.out.println("FIXME");
                    }
                    for (int i = 0; i < temp.length; ++i) {
                        dataOutToClient.writeUTF(temp[i].getName() + "" + '\n');
                    }
                    dataOutToClient.writeUTF("EOF");
                    dataOutToClient.close();
                    dataSocket.close();
                    System.out.println("Data Socket closed");
                }

                /*******************************************************
        *  _____      _        _                
        *  |  __ \    | |      (_)               
        *  | |__) |___| |_ _ __ _  _____   _____ 
        *  |  _  // _ \ __| '__| |/ _ \ \ / / _ \
        *  | | \ \  __/ |_| |  | |  __/\ V /  __/
        *  |_|  \_\___|\__|_|  |_|\___| \_/ \___|
        *                             
        * Takes in a file name and querries for it and sends it.
        ********************************************************/
                if (clientCommand.equals("retr:")) {

                    long fileSize = 0;
                    String[] words = fromClient.split(" ");
                    File[] temp = folder.listFiles();
                    for (int i = 0; i < temp.length; ++i) {
                        if (temp[i].getName().equals(words[2])) {
                            fileSize = temp[i].length();

                        }
                    }
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
                    FileInputStream fis = new FileInputStream(folder + "/" + words[2]);
                    byte[] buffer = new byte[4096];
                    int read;
                    dataOutToClient.writeLong(fileSize);
                    while ((read = fis.read(buffer)) > 0) {
                        dataOutToClient.write(buffer, 0, read);
                    }
                    System.out.println("\nFile " + words[2] + " Sent.");
                    fis.close();
                    dataOutToClient.close();
                    dataSocket.close();
                }
                /*******************************************************
                *   _____ _                             
                *  / ____| |                            
                * | (___ | |_ ___  _ __ __ _  __ _  ___ 
                *  \___ \| __/ _ \| '__/ _` |/ _` |/ _ \
                *  ____) | || (_) | | | (_| | (_| |  __/
                * |_____/ \__\___/|_|  \__,_|\__, |\___|
                *                             __/ |     
                *                            |___/                        
                * Takes in a file delivery.
                ********************************************************/
                if (clientCommand.equals("stor:")) {
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                    int read = 0;
                    byte[] buffer = new byte[4096];
                    String str = fromClient;
                    String[] words = str.split(" ");
                    FileOutputStream fos = new FileOutputStream(folder + "/" + words[2]);
                    long fileSize = inData.readLong();
                    long remaining = fileSize;


                    while ((read = inData.read(buffer, 0, Math.min(buffer.length, (int)remaining))) > 0) {
                        remaining -= (long)read;
                        fos.write(buffer, 0, read);
                    }
                    System.out.println("\nFile " + words[2] + " Successfully downloaded.");
                    inData.close();
                    dataSocket.close();
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
            } catch (IOException ioEx) {
                System.out.println("Error in connection");
            }

        }
    }
}
