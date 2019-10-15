import java.io. *;
import java.net. *;
import java.util. *;
import java.text. *;
import java.lang. *;
import javax.swing. *;
class FTPClient {

    public static void main(String argv[])throws Exception {
        String sentence;
        String modifiedSentence;
        boolean isOpen = true;
        int number = 1;
        int port = 2328;
        boolean notEnd = true;
        String statusCode;
        boolean clientgo = true;

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System. in));
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);


        if (sentence.startsWith("connect")) {
            String serverName = tokens.nextToken(); // pass the connect command
            serverName = tokens.nextToken();
            int port1 = Integer.parseInt(tokens.nextToken());
            System.out.println("You are connected to " + serverName);

            Socket ControlSocket = new Socket(serverName, port1);

            while (isOpen && clientgo) {
                System.out.println("\nWhat would you like to do next: \n list: || retr: file.txt || stor: file.txt || quit ");
                DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));

                sentence = inFromUser.readLine();
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
                if (sentence.equals("list:")) 
                {

                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();

                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    while (notEnd) {
                        modifiedSentence = inData.readUTF();
                        if (modifiedSentence.equals("EOF")) {
                            notEnd = false;
                        } else 
                            System.out.print(modifiedSentence);
                        
                    }
                    inData.close();
                    welcomeData.close();
                    dataSocket.close();
                    notEnd = true;
                } 
                /*******************************************************
                *   _____      _        _                
                *  |  __ \    | |      (_)               
                *  | |__) |___| |_ _ __ _  _____   _____ 
                *  |  _  // _ \ __| '__| |/ _ \ \ / / _ \
                *  | | \ \  __/ |_| |  | |  __/\ V /  __/
                *  |_|  \_\___|\__|_|  |_|\___| \_/ \___|
                *                             
                * Takes in a file name and querries for it and sends it.
                ********************************************************/
                else if (sentence.startsWith("retr: "))
                { 
                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                    int read = 0;
                    byte[] buffer = new byte[4096];
                    String str = sentence;
                    String[] words = str.split(" ");
                    FileOutputStream fos = new FileOutputStream(words[1]);
                    long fileSize = inData.readLong();
                    long remaining = fileSize;

                    System.out.println("Downloading File.......")

                    while ((read = inData.read(buffer, 0, Math.min(buffer.length, (int) remaining))) > 0){ 
                        remaining -= (long) read;
                        fos.write(buffer, 0, read);

                    }     
                    System.out.println("\nFile Successfully downloaded.")
                    welcomeData.close();
                    inData.close();
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
                else if (sentence.startsWith("stor: "))
                { 
                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');
                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

                    File folder = new File("./");
                    File[] temp = folder.listFiles();  
                    String str = sentence;
                    String[] words = str.split(" ");
                    FileInputStream fis = new FileInputStream(words[1]);
                    byte[] buffer = new byte[4096];
                    long fileSize = 0;
                    int read;        
                    for (int i=0; i < temp.length; ++i) {
                        if(temp[i].getName().equals(words[1])){
                        fileSize = temp[i].length();
                        }
                    }
                    dataOutToClient.writeLong(fileSize); 

                    while((read = fis.read(buffer)) > 0) {
                        dataOutToClient.write(buffer, 0, read);
                    }
                    welcomeData.close();
                    dataOutToClient.close();
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
                else if (sentence.startsWith("quit")) 
                {
                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');
                    isOpen = false;
                    System.out.println("Closing Connection, Goodbye!");
                    ControlSocket.close();
                } else {
                    System.out.println("Didn't quite recognize that command try one of thses!");
                }
            }
        }
    }
    public static String parse_string(String myString) {
        String str = myString.split(" ")[0];
        str = str.replace("\\", "");
        String[] arr = str.split("u");
        String text = "";
        for (int i = 1; i < arr.length; i ++) {
            int hexVal = Integer.parseInt(arr[i], 16);
            text += (char)hexVal;
        }
        return text;
    }
}
