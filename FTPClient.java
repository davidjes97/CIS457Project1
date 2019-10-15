import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;
class FTPClient { 

        public static void main(String argv[]) throws Exception { 
                String sentence; 
                String modifiedSentence; 
                boolean isOpen = true;
                int number=1;
                int port = 2328;
                boolean notEnd = true;
	            String statusCode;
	            boolean clientgo = true;
	    
	
	        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
                sentence = inFromUser.readLine();
                StringTokenizer tokens = new StringTokenizer(sentence);


	        if(sentence.startsWith("connect")){
	                String serverName = tokens.nextToken(); // pass the connect command
	                serverName = tokens.nextToken();
	                int port1 = Integer.parseInt(tokens.nextToken());
                        System.out.println("You are connected to " + serverName);
        
	                Socket ControlSocket= new Socket(serverName, port1);
        
	                while(isOpen && clientgo)
                        {      
	      
                                DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream()); 
	                        DataInputStream inFromServer = new DataInputStream(new BufferedInputStream (ControlSocket.getInputStream()));
          
    	                        sentence = inFromUser.readLine();
	   
                                if(sentence.equals("list:"))
                                {
					System.out.println("\nThe files in this server are: ");            
	                                
					port = port + 2;
	                                outToServer.writeBytes (port + " " + sentence + " " + '\n');
	    
                                        ServerSocket welcomeData = new ServerSocket(port);
	                                Socket dataSocket = welcomeData.accept(); 

 	                                DataInputStream inData = new DataInputStream(new BufferedInputStream (dataSocket.getInputStream()));
                                        while(notEnd) 
                                        {
                                                modifiedSentence = inData.readUTF();
                                                //    ........................................
	                                        //    ........................................
                                        }
	

	                                welcomeData.close();
	                                dataSocket.close();
	                                System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");
                                }
                                else if(sentence.startsWith("retr: "))
                                {
                                        // ....................................................
                                }

				else if(sentence.startsWith("stor: ")){
					// need to get a file (fileToSend)
					File fileToSend = new File("/Users/josh.gorodinsky/hello.txt");
					
					// Create input stream
					FileInputStream fis = new FileInputStream(fileToSend);
					BufferedInputStream bis = new BufferedInputStream(fis);
				

					// Create dataSocket connection.
					port = port1;
					ServerSocket welcomeData = new ServerSocket(port);
					Socket dataSocket = welcomeData.accept();
					DataOutputStream outData = new DataOutputStream(
							new BufferedOutputStream(dataSocket.getOutputStream()));

					//Notify client that the file is going to be sent over to the server
					System.out.println("Uploading file to server..." + fileToSend.getAbsolutePath());


					// creates an array of bytes to send to server 
					long fileLength = fileToSend.length();
					byte[] bytesToSend = new byte[(int) fileLength];

					// read bytesToSend into the bis
					bis.read(bytesToSend, 0, (int) fileLength);


					// Writes byte array
					outData.write(bytesToSend);


				}
                                ControlSocket.close();
                        }
                }  
        }
}
