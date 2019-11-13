import java.io. *;
import java.net. *;
import java.util. *;
import java.lang.*;

public class ftpserver implements Runnable {

    private ServerSocket welcomeSocket;

    public ftpserver()throws Exception {
            welcomeSocket = new ServerSocket(42001);
    }

    public void run(){
        while (true) {
            try{
                Socket connectionSocket = welcomeSocket.accept();

                System.out.println("\nNew client accepted.\n");
                ftpServerHandler handler = new ftpServerHandler(connectionSocket);

                
                Thread thread = new Thread(handler);
                thread.start();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }   
    }     
}

