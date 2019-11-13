import java.io. *;
import java.net. *;
import java.util. *;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom. *;

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

class ClientServerHandler extends Thread {

    private Socket connectionSocket;
    private Socket dataSocket;

    private boolean connectionIsLive;
    protected boolean initialConnection;

    private DataInputStream dataFromClient;
    private DataOutputStream outToClient;
    private BufferedReader inFromClient;
    private UserElement user;

    private int port;

    protected static Vector<UserElement> userList = new Vector<UserElement>();
    protected static Vector<FileElement> fileList = new Vector<FileElement>();

    public ClientServerHandler(Socket socket) {

        connectionSocket = socket;

        try {
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            this.initialConnection = true;
            this.connectionIsLive = true;
            System.out.println("Connection Established");
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public void run() {
        String fromClient;
        String clientCommand;


        while (connectionIsLive) {

            try{
                if (initialConnection) {
                try {
                    String userInfo = this.inFromClient.readLine();
                    try{
                        getInitialRequest(userInfo);
                    }catch(Exception initReq){
                        System.out.println(initReq);
                    }      
                } catch (IOException ioEx) {
                    System.out.println("Error in connection");
                }
                } else {
                    // waitForRequest();
                    fromClient = this.inFromClient.readLine();
                    // processRequest(fromClient);
                    StringTokenizer tokens = new StringTokenizer(fromClient);
                    try{
                        searchKeyword(tokens.nextToken());
                    }catch(Exception sKey){
                        System.out.println(sKey);
                    }
                        
                }
            } catch(IOException ioTotalEx){
                System.out.println("Error in total connection");
            }
            
        }
    }

    private void searchKeyword(String keyword)throws Exception {
        synchronized(fileList) {
            synchronized(userList) {
                String output = "";

                for (int i = 0; i < ClientServerHandler.fileList.size(); i ++) {
                    FileElement fileEntry = (FileElement)ClientServerHandler.fileList.get(i);
                    String description = fileEntry.getDescription();
                    if (description.contains(keyword)) {
                        UserElement user = fileEntry.getUser();
                        output += user.getSpeed() + " " + user.getHostName() + " " + fileEntry.getFileName() + "\t";
                    }
                }
                System.out.println("Sending back: " + "output will go here");
                this.outToClient.writeBytes(output + "\n");
            }
        }
    }

    private void getInitialRequest(String userInfo) {
        System.out.println(userInfo);

        StringTokenizer parseUserInfo = new StringTokenizer(userInfo);

        String userName = parseUserInfo.nextToken();
        String hostName = parseUserInfo.nextToken();
        String speed = parseUserInfo.nextToken();
        String port = parseUserInfo.nextToken();

        user = new UserElement(userName, speed, hostName);

        try{
            retrieveFiles(String.valueOf(port));
        }catch(Exception rFE){
            System.out.println(rFE);
        }
        
    }

    private File getFile()throws Exception {
        System.out.println("A client is sending a xml file...");
        FileOutputStream fos = new FileOutputStream("temp.xml");
        System.out.println("File Stream Created");
        byte[] fileData = new byte[1024];
        int bytes = 0;
        while ((bytes = this.dataFromClient.read(fileData)) != -1) {
            System.out.println("Bytes received: " + bytes);
            fos.write(fileData, 0, bytes);
            System.out.println("end");
        }
        System.out.println("File received!");
        fos.close();
        File file = new File("temp.xml");
        return file;
    }

    private ArrayList<FileElement> parseData(File file, UserElement user)throws Exception {
        ArrayList<FileElement> dataList = new ArrayList<FileElement>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize(); // Make sure there are no funky things going on in the parser
        NodeList nList = doc.getElementsByTagName("file");
        for (int i = 0; i < nList.getLength(); i ++) {
            Node node = nList.item(i);
            System.out.println("\nCurrent Element :" + node.getNodeName());
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eTemp = (Element)node;
                System.out.println(eTemp.getElementsByTagName("name").item(0).getTextContent());
                System.out.println(eTemp.getElementsByTagName("description").item(0).getTextContent());
                dataList.add(new FileElement(user, eTemp.getElementsByTagName("name").item(0).getTextContent(), eTemp.getElementsByTagName("description").item(0).getTextContent()));
            }
        }
        file.delete();
        return dataList;
    }

    private void addUser(UserElement newUser) {
        synchronized(userList) {
            System.out.println("Adding user to the list");
            userList.addElement(newUser);
            System.out.println("added user to the list");
        }
    }

    private void addContent(ArrayList<FileElement> newData) {
        synchronized(fileList) {
            System.out.println("Adding elements to the filelist");
            fileList.addAll(newData);
        }
    }

    private void retrieveFiles(String port)throws Exception {

        ServerSocket dataServerSocket = new ServerSocket(Integer.parseInt(port));
        Socket dataSocket = dataServerSocket.accept();


        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        Element hierarchy = doc.createElement("files");
        Element file;
        Element attrType;
        Element attrType1;
        Element attrType2;
        doc.appendChild(hierarchy);

        DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
        boolean notEnd = true;
        String modifiedSentence = "";

        while (notEnd) {
            modifiedSentence = inData.readUTF();
            if (modifiedSentence.equals("EOF")) {
                notEnd = false;
            } else {

                file = doc.createElement("file");
                hierarchy.appendChild(file);

                attrType = doc.createElement("hostname");
                attrType.appendChild(doc.createTextNode(user.getUserName()));
                file.appendChild(attrType);

                attrType1 = doc.createElement("speed");
                attrType1.appendChild(doc.createTextNode(user.getHostName()));
                file.appendChild(attrType1);

                attrType2 = doc.createElement("filename");
                attrType2.appendChild(doc.createTextNode(modifiedSentence));
                file.appendChild(attrType2);


                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("fileList/" + user.getUserName() + ".xml"));
                transformer.transform(source, result);
            }
        }
        notEnd = true;

    }

}
