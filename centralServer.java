import java.io. *;
import java.net. *;
import java.util. *;
import javax.xml.parsers.*;

class centralServer {

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
            ClientServerHandler handler = new ClientServerHandler(connectionSocket);

            handler.start();
        }
    }
}

class ClientServerHandler extends Thread{

    private Socket connectionSocket;

    private boolean connectionIsLive;
    protected boolean welcomeFlag;
    
    private DataInputStream dataFromClient;
    private DataOutputStream outToClient;
    private BufferedReader inFromClient;

    protected static Vector<UserElement> userList = new Vector<UserElement>();
    protected static Vector<FileElement> fileList = new Vector<FileElement>();

    public ClientServerHandler(Socket socket) {

        connectionSocket = socket;

        try {
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            this.welcomeFlag = true;
            this.connectionIsLive = true;
            System.out.println("Connection Established");
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public void run() {
        String fromClient;
        String clientCommand;
        int port;

        try{
            while(connectionIsLive){
                if (welcomeFlag){
                    String userInfo = this.inFromClient.readLine();
                    getInitialRequest(userInfo);
                } else {
                    waitForRequest();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void waitForRequest() throws Exception{
       System.out.println("Waiting for request");
       String fromClient = this.inFromClient.readLine();
       System.out.println("Keyword received");
       processRequest(fromClient);
    }

    private void processRequest(String sentence) throws Exception{
        StringTokenizer tokens = new StringTokenizer(sentence);

        System.out.println("Keyword searched");
        searchKeyword(tokens.nextToken());
    }

    private void searchKeyword(String keyword) throws Exception{
        synchronized (fileList){
            synchronized (userList){
                //String output = "";  Use an object for the output?

                for (int i = 0; i < ClientServerHandler.fileList.size(); i++){
                    FileElement fileEntry = (FileElement) ClientServerHandler.fileList.get(i);
                    String description = fileEntry.getDescription();
                    if (description.contains(keyword)){
                        UserElement user = fileEntry.getUser();
                        //output += user.getSpeed() + " " +  use an object for the output?
                    }
                }
                System.out.println("Sending back: " + "output will go here");
               //this.outToClient.writeBytes(output + "\n");
            }
        }
    }

    private void getInitialRequest(String userInfo) throws Exception{
        System.out.println(userInfo);
        System.out.println("Users data received");
        StringTokenizer parseUserInfo = new StringTokenizer(userInfo);
        System.out.println("First Token");
        String userName = parseUserInfo.nextToken();
        System.out.println("Second Token");
        String speed = parseUserInfo.nextToken();
        System.out.println("Third Token");
        String hostName = parseUserInfo.nextToken();
        System.out.println("done with Token");
        UserElement user = new UserElement(userName, speed, hostName);
        System.out.println("Adding the user");
        addUser(user);

        this.dataFromClient = new DataInputStream(new BufferedInputStream(this.dataSocket.getInpuStream()));

        System.out.println("User added");

        File file = getFile();
        ArrayList<FileElement> files = parseData(file, user);
        addContent(files);
        this.welcomeMessage = false;
        System.out.println("Initial connection completed");
        this.dataFromClient.close();
        this.dataSocket.close();
    }

    private File getFile() throws Exception{
        System.out.println("A client is sending a xml file...");
        FileOutputStream fos = new FileOutputStream("temp.xml");
        System.out.println("File Stream Created");
        byte[] fileData = new byte [1024];
        int bytes = 0;
        while ((bytes = this.dataFromClient.read(fileData)) != -1){
            System.out.println("Bytes received: " + bytes);
            fos.write(fileData, 0, bytes);
            System.out.println("end");
        }
        System.out.println("File received!");
        fos.close();
        File file = new File("temp.xml");
        return file;
    }

    private ArrayList<FileElement> parseData(File file, UserElement user) throws Exception {
        ArrayList<FileElement> dataList = new ArrayList();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();// Make sure there are no funky things going on in the parser
        NodeList nList = doc.getElementsByTagName("file");
        for (int i = 0; i < nList.getLength(); i++) {
          Node node = nList.item(i);
          System.out.println("\nCurrent Element :" + node.getNodeName());
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element eTemp = (Element) node;
            System.out.println(eTemp.getElementsByTagName("name").item(0).getTextContent());
            System.out.println(eTemp.getElementsByTagName("description").item(0).getTextContent());
            dataList.add(new FileElement(user, eTemp.getElementsByTagName("name").item(0).getTextContent(), eTemp.getElementsByTagName("description").item(0).getTextContent()));
          }
        }
        file.delete();
        return dataList;
      }
    
      private void addUser(UserElement newUser) {
        synchronized (userList) {
            System.out.println("Adding user to the list");
          userList.addElement(newUser);
          System.out.println("added user to the list");
        }
      }
    
      private void addContent(ArrayList<FileElement> newData) {
        synchronized (fileList) {
          System.out.println("Adding elements to the filelist");
          fileList.addAll(newData);
        }
      }

}