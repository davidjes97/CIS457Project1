import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient { 
    //  private static int port = 4203;
    // private static InetAddress = host;

    public static void main(String argv[]) throws Exception {
        int port = 4203;
        String sentence;
        String modifiedSentence;
        boolean isOpen = true;
        int number = 1;
        boolean notEnd = true;
        String statusCode;
        boolean clientgo = true;


        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);

        //When the command connect is entered it will attempt a connection to the server and then allow for more commands
        if (sentence.startsWith("connect")) {
            String serverName = tokens.nextToken(); // pass the connect command
            serverName = tokens.nextToken();
            port1 = Integer.parseInt(tokens.nextToken());
            System.out.println("You are connected to " + serverName);

            //Attempts to connect to the server
            try {
                Socket ControlSocket = new Socket(serverName, port1);
                clientgo = true;
            } catch (exception e) {//If the server does not exist an error is returned
                clientgo = false;
                System.out.println(e);
            }

            //Continous loop that stops only when a user requests to quit
            while (isOpen && clientgo) {

                DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());

                DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));

                sentence = inFromUser.readLine();

                /*
                When this command is sent to the server, the server returns a list of the
                files in the current directory on which it is executing. The client should get the
                list and display it on the screen.
                 */
                if (sentence.equals("list:")) {

                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();

                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                    while (notEnd) {
                        modifiedSentence = inData.readUTF();
               ........................................
	       ........................................
                    }


                    welcomeData.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                }
                /*
                RETR <filename>: This command allows a client to get a file specified by its
                filename from the server.
                 */
                else if (sentence.startsWith("retr: ")) {

                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');

                    ServerSocket welecomeData = new ServerSocket(port);
                    Socket dataSocket = welecomeData.accept();

                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                    while (notEnd) {
                        modifiedSentence = inData.readUTF();
               ........................................
	       ........................................
                    }


                    welcomeData.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");
                }
                /*
                STOR <filename>: This command allows a client to send a file specified by its
                filename to the server.
                 */
                else if (sentence.startsWith("stor: ")) {
                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');

                    ServerSocket welecomeData = new ServerSocket(port);
                    Socket dataSocket = welecomeData.accept();

                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                    while (notEnd) {
                        modifiedSentence = inData.readUTF();
               ........................................
	       ........................................
                    }


                    welcomeData.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");
                }
                 /*
                 QUIT: This command allows a client to terminate the control connection. On
                 receiving this command, the client should send it to the server and terminate
                 the connection. When the ftp_server receives the quit command it should
                 close its end of the connection.
                  */
                else if (sentence.equals("quit:")) {

                    port = port + 2;
                    outToServer.writeBytes(sentence);

                    ControlSocket.close();
                    isOpen = false

                } else {
                    System.out.println("Error, incorrect command");
                }
            }