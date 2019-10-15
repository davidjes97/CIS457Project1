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
                    notEnd= true;
                } 
                else if (sentence.startsWith("retr: "))
                { 
                    // ....................................................
                } 
                else if (sentence.startsWith("stor: "))
                { 
                    // ....................................................
                } 
                else if (sentence.startsWith("quit")) 
                {
                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');
                    isOpen = false;
                    System.out.println("Closing Connection, Goodbye!");
                    ControlSocket.close();
                } else {}
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
