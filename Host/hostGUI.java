import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util. *;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.table.*;
/*****************************************************************
* Host GUI supports the front end for three user functions:
* 1. User can connect to centralized server
* 2. Search for files based on a keyword search
* 3. Perform an FTP command to either retrieve a file or disconnect
* 
* @author Ryan De Jong
* @author Jesse David
* @author Josh Gorodinsky
* @author Isaac Dessert
*
* @version Fall 2019
******************************************************************/

public class hostGUI extends JFrame implements ActionListener {
    /**Displays the functions a user can perform through a graphical user interface*/

    /**Main panel for the GUI */
    private JPanel Panel;

    /**FOR THE CONNECTION SECTION*/

    /**Title labels */
    private JLabel connectTitle;
    private JLabel searchTitle;
    private JLabel FTPTitle;

    /**Label for the Hostname Textbox */
    private JLabel IPNameLBL;
    /**Label the port number textbox*/
    private JLabel portNumberLBL;
    /**Label for userName textbox*/
    private JLabel userNameLBL;
    /**Label for hostName textbox*/
    private JLabel hostNameLBL;
    /**Label for speed droplist*/
    private JLabel speedLBL;
    /**Used to connect to centralized server*/
    private JButton connectBut;
    /**Allows the user to enter desired IP address*/
    private JTextField hostIPTXT;
    /**text field for user to enter desired port number*/
    private JTextField portTXT;
    /**Allows the user to enter desired hostname and IP address*/
    private JTextField hostNameTXT;
    /**text field for user to enter their userName*/
    private JTextField userNameTXT;
    /**Creates the JCombobox of speed choices */
    JComboBox<String> speedList;

    /**FOR THE SEARCH SECTION*/

    /**Label for the keyword searchbox */
    private JLabel keywordLBL;
    /**Textfield for the user to enter desired keyword search*/
    private JTextField keywordTXT;
    /**Button that will initiate the search on the centralized server*/
    private JButton searchBut;
    /**Table that will display name of file, name of host, and description of the file */
    JTable searchTable;
    /**Adds a scrollbar to the text area displaying results from keyword search*/
    JScrollPane scroll;

    /**FOR THE FTP SECTION*/

    /**Label for the keyword searchbox */
    private JLabel enterCommandLBL;
    /**Textfield for the user to enter desired keyword search*/
    private JTextField commandTXT;
    /**Button that will initiate the search on the centralized server*/
    private JButton goBut;
    /**Text Area that will display messages from server, commands entered, etc */
    JTextArea commandArea;
    /**Adds a scrollbar to the commandArea*/
    JScrollPane commandScroll;

    /**Used to get user input*/
    private String hostIP;
    private int port;
    private String userName;
    private String hostName;
    private String speed;
    protected ClientHandler client;


    /**********************************************
    * Runs the GUI
    * @param args Standard declaration for main
    **********************************************/
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    hostGUI hgui = new hostGUI();
                    ftpserver server = new ftpserver();
                    Thread thread = new Thread(server);
                    thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**Initializes each variable*/
    public hostGUI() {
        setTitle("GV-NAPSTER Host");
        getContentPane();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /**ADDING IN BUTTON BINDING 
		 * Helped by Uluk Biy on Stack Overflow
		 * Link: https://stackoverflow.com/questions/23040531/how-to-disable-button-when-textfield-is-empty
		 */
        // BooleanBinding bb = new BooleanBinding() {
        // {
        // super.bind(keywordTXT.textProperty());
        // }

        // @Override
        // protected boolean computeValue(){
        // return (keywordTXT.getText().isEmpty());
        // }
        // };


        /**FOR THE CONNECTION SECTION */

        /** creates a string of speed options for the combobox */
        String[] speedListArray = new String[]{"Ethernet", "T1"};
        speedList = new JComboBox(speedListArray);

        Panel = (JPanel)getContentPane();
        Panel.setLayout(null);

        /**Initializes connect button*/
        connectBut = new JButton("Connect");
        connectBut.addActionListener(this);

        /**Initializes labels and textboxes*/
        hostIPTXT = new JTextField("", 10);
        portTXT = new JTextField("", 10);
        hostNameTXT = new JTextField("", 10);
        userNameTXT = new JTextField("", 10);
        connectTitle = new JLabel("<html> <font color='blue'>CONNECTION</font></html>");
        IPNameLBL = new JLabel("Server Hostname:");
        portNumberLBL = new JLabel("Port:");
        userNameLBL = new JLabel("Username:");
        hostNameLBL = new JLabel("Hostname:");
        speedLBL = new JLabel("Speed:");

        /**Organizes components on the panel*/
        connectTitle.setBounds(5, 5, 100, 26);
        /**First Line*/
        IPNameLBL.setBounds(10, 25, 150, 26);
        hostIPTXT.setBounds(120, 25, 200, 26);
        portNumberLBL.setBounds(370, 25, 70, 26);
        portTXT.setBounds(400, 25, 80, 26);
        connectBut.setBounds(520, 25, 180, 26);

        /** Second Line */
        userNameLBL.setBounds(10, 60, 160, 26);
        userNameTXT.setBounds(80, 60, 160, 26);
        hostNameLBL.setBounds(250, 60, 150, 26);
        hostNameTXT.setBounds(318, 60, 200, 26);
        speedLBL.setBounds(527, 60, 100, 26);
        speedList.setBounds(568, 60, 130, 26);


        /** adds to the panel */
        Panel.add(connectTitle);
        Panel.add(IPNameLBL);
        Panel.add(hostIPTXT);
        Panel.add(portNumberLBL);
        Panel.add(portTXT);
        Panel.add(connectBut);
        Panel.add(userNameLBL);
        Panel.add(userNameTXT);
        Panel.add(hostNameLBL);
        Panel.add(hostNameTXT);
        Panel.add(speedLBL);
        Panel.add(speedList);

		/**Array to display files from keyword search. */
		/**Create Table to show list of retirved files with hostname and speed */
		searchTable = new JTable(new DefaultTableModel(new Object[]{"FileName", "Hostname", "Speed"}, 0));
		scroll = new JScrollPane(searchTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		/**Initialize Variables */
		keywordTXT = new JTextField("",10);
		searchTitle = new JLabel("<html> <font color='blue'>SEARCH</font></html>");
		keywordLBL = new JLabel("Keyword:");
		searchBut = new JButton("Search");
		searchBut.addActionListener(this);
		//Adding boolean binding to keyword search button
		// searchBut.disablePropert().bind(bb);

        /**Create Table to show list of retirved files with hostname and speed */
        searchTable = new JTable(fileListArray, colNames);
        scroll = new JScrollPane(searchTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        /**Initialize Variables */
        keywordTXT = new JTextField("", 10);
        searchTitle = new JLabel("<html> <font color='blue'>SEARCH</font></html>");
        keywordLBL = new JLabel("Keyword:");
        searchBut = new JButton("Search");
        searchBut.addActionListener(this);
        // Adding boolean binding to keyword search button
        // searchBut.disablePropert().bind(bb);

        // VBox vBox = new VBox();
        // vBox.getChildren().addAll(keyWordTXT, searchBut);

        /**Set Location on Panel */
        searchTitle.setBounds(5, 127, 100, 26);
        keywordLBL.setBounds(10, 150, 160, 26);
        keywordTXT.setBounds(80, 150, 250, 26);
        searchBut.setBounds(390, 150, 150, 26);
        scroll.setBounds(10, 190, 700, 200);

        /**add to Panel */
        Panel.add(searchTitle);
        Panel.add(keywordTXT);
        Panel.add(keywordLBL);
        Panel.add(searchBut);
        Panel.add(scroll);

        /**FOR THE FTP SECTION */

        /**Initialize variables */
        commandTXT = new JTextField("", 10);
        FTPTitle = new JLabel("<html> <font color='blue'>FTP</font></html>");
        enterCommandLBL = new JLabel("Enter Command:");
        goBut = new JButton("Go");
        goBut.addActionListener(this);

        /**create text area for user to see server esponse and entered commands */
        commandArea = new JTextArea(600, 200);
        commandArea.setEditable(false); // user can't edit this text area
        commandScroll = new JScrollPane(commandArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        /**Set locations on panel */
        FTPTitle.setBounds(5, 397, 160, 26);
        enterCommandLBL.setBounds(10, 420, 160, 26);
        commandTXT.setBounds(120, 420, 500, 26);
        goBut.setBounds(650, 420, 58, 26);
        commandScroll.setBounds(10, 460, 700, 290);

        /**add to panel */
        Panel.add(FTPTitle);
        Panel.add(enterCommandLBL);
        Panel.add(commandTXT);
        Panel.add(goBut);
        Panel.add(commandScroll);

        /** sets size, location and visiblity of the box */
        setSize(720, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /*************************************************
	 * initializes the buttons functionality
	 ************************************************/
    @Override public void actionPerformed(ActionEvent e) {

        if (e.getSource() == connectBut) {

			try
			{
				client = new ClientHandler(hostIP, port, userName, hostName, speed);
				commandArea.append("connected to " + hostIP + " " + port + ".\n");
				connectBut.setEnabled(false);
			}
			catch(Exception E){
				System.out.println(E);
				commandArea.append("Problem Connecting or sending files to:" + hostIP + " " + port + ".\n");
			}
		}
		else if(e.getSource() == searchBut){
			if(client == null) {
				commandArea.append("You need to connect to a server first before you search a keyword.\n");
			}
			else if(keywordTXT.getText().isEmpty()){
				commandArea.append("Please Enter a Keyword.\n");
			}
			else{
			try {
				DefaultTableModel model = (DefaultTableModel) searchTable.getModel();
				model.setRowCount(0);			
				String keyword = keywordTXT.getText();
				client.sendKeyword(keyword);
				String temporary;
			do{
			temporary = client.retrieveFileNames();
			if(!temporary.equals("EOF")){
				StringTokenizer token = new StringTokenizer(temporary);

                model.addRow(new Object[]{token.nextToken(), token.nextToken(), token.nextToken()});
			}
			}while(!temporary.equals("EOF"));
			client.cleanUp();
			if(model.getRowCount()==0){commandArea.append("No files with that keyword.\n");}
			}
			catch (Exception E){
				System.out.println(E);	
			}
			}
		}
		else if(e.getSource() == goBut){
			/** used to get the command the user has entered*/
			// hostIP = hostIPTXT.getText();
			// port = Integer.parseInt(portTXT.getText());
			// userName = userNameTXT.getText();
			// hostName = hostNameTXT.getText();
			// speed = (String) speedList.getSelectedItem();
			//ClientHandler remoteClient = new ClientHandler(hostIP, port, userName, hostName, speed);
			StringTokenizer tokenizer = new StringTokenizer(commandTXT.getText());
			String command = tokenizer.nextToken();

            try {
                client = new ClientHandler(hostIP, port, userName, hostName, speed);
                commandArea.append("connected to " + hostIP + " " + port + ".\n");
            } catch (Exception E) {
                System.out.println(E);
                commandArea.append("Problem Connecting or sending files to:" + hostIP + " " + port + ".\n");
            }
        } else if (e.getSource() == searchBut) {
            String keyword = keywordTXT.getText();

        } else if (e.getSource() == goBut) {
            /** used to get the command the user has entered*/
            // hostIP = hostIPTXT.getText();
            // port = Integer.parseInt(portTXT.getText());
            // userName = userNameTXT.getText();
            // hostName = hostNameTXT.getText();
            // speed = (String) speedList.getSelectedItem();
            // ClientHandler remoteClient = new ClientHandler(hostIP, port, userName, hostName, speed);
            StringTokenizer tokenizer = new StringTokenizer(commandTXT.getText());
            String command = tokenizer.nextToken();

            //String response = client.runCommand(commandTXT.getText());
			//commandArea.append(response);
			System.out.println(commandTXT.getText());
			System.out.println(command);
            if (command.equals("quit")) {
				try{
					System.out.println("You made it to quit in the gui");
					client.runCommand(command);
					dispose();
				}catch(Exception qE){
					commandArea.append("Exception found: " + qE);
				}
				
                // call the quit function
            } else if (command.equals("retr:")) {
				String filename = tokenizer.nextToken();
				try{
					System.out.println("You made it to retr in the gui");
					client.runCommand(commandTXT.getText());
				}catch(Exception rE){
					commandArea.append("Exception found: " + rE);
				}
				
                // call the retr function, look at remote host connected with that file
                // probably have to iterate through array till specified filename is found
            } else if (command.equals("connect:")) {
                try{
                    client.runCommand(command);
                }catch(Exception cE){
                    commandArea.append("Exception found: " + cE);
                }
                
                String IPaddress = tokenizer.nextToken(); // IP address to connect with
                int remotePort = Integer.parseInt(tokenizer.nextToken()); // portnumber user has entered to connect with
            } else {
                commandArea.append("Invalid command! \nValid commands are: 'connect IP Port' || 'retr filename.txt' || 'quit'\n");
            }
        }
    }
}
