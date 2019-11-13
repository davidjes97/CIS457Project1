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
    private String filename;
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
                        sendFile(tokens.nextToken(), tokens.nextToken());                    }catch(Exception sKey){
                        System.out.println(sKey);
                    }
                        
                }
            } catch(IOException ioTotalEx){
                File temp = new File(filename);
                temp.delete();
                System.out.println("Error in total connection");
            }
            
        }
    }


    private void getInitialRequest(String userInfo)throws Exception {
        System.out.println(userInfo);

        StringTokenizer parseUserInfo = new StringTokenizer(userInfo);

        String userName = parseUserInfo.nextToken();
        String hostName = parseUserInfo.nextToken();
        String speed = parseUserInfo.nextToken();
        String port = parseUserInfo.nextToken();

        user = new UserElement(userName, speed, hostName);

        retrieveFiles(port);

        this.initialConnection = false;
        System.out.println("Initial connection completed with: " + userName);
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

        String f = "";
        while (new File("fileList/" + user.getUserName() + f + ".xml").exists()) 
            f = f + "z";
        

        filename = "fileList/" + user.getUserName() + f + ".xml";

        while (notEnd) {
            modifiedSentence = inData.readUTF();
            if (modifiedSentence.equals("EOF")) {
                notEnd = false;
            } else {

                file = doc.createElement("file");
                hierarchy.appendChild(file);

                attrType = doc.createElement("hostname");
                attrType.appendChild(doc.createTextNode(user.getHostName()));
                file.appendChild(attrType);

                attrType1 = doc.createElement("speed");
                attrType1.appendChild(doc.createTextNode(user.getSpeed()));
                file.appendChild(attrType1);

                attrType2 = doc.createElement("filename");
                attrType2.appendChild(doc.createTextNode(modifiedSentence));
                file.appendChild(attrType2);


                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(filename));
                transformer.transform(source, result);
            }
        }
        notEnd = true;

    }

    private void sendFile(String port, String keyword) {

        try {
            Socket dataSocket = new Socket(connectionSocket.getInetAddress(), Integer.parseInt(port));
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
            Node nNode;
            NodeList nList;
            String hostName;
            String speed;
            File inputFile;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            File folder = new File("fileList/");
            File[] temp = folder.listFiles();

            if (temp == null) {
                dataOutToClient.writeUTF("EOF");
            }
            for (int i = 0; i < temp.length; ++i) {
                inputFile = temp[i];
                doc = dBuilder.parse(inputFile);
                doc.getDocumentElement().normalize();
                nList = doc.getElementsByTagName("file");
                // dataOutToClient.writeUTF(temp[i].getName() + "" + '\n');
                for (int num = 0; num < nList.getLength(); num ++) {
                    nNode = nList.item(num);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element)nNode;
                        if (eElement.getElementsByTagName("filename").item(0).getTextContent().contains(keyword)) 
                            dataOutToClient.writeUTF(eElement.getElementsByTagName("filename").item(0).getTextContent() + " " + eElement.getElementsByTagName("hostname").item(0).getTextContent() + " " + eElement.getElementsByTagName("speed").item(0).getTextContent() + " " + '\n');
                        

                    }
                }
            }
            dataOutToClient.writeUTF("EOF");
            dataOutToClient.close();
            dataSocket.close();
            System.out.println("Data Socket closed");
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } catch (Exception E) {
            E.printStackTrace();
        }
    }
}
