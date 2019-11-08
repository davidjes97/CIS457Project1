import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
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
* @author Stefan Kiser
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
	/**Text Area that will display name of file, name of host, and description of the file */
	JTextArea searchArea;
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

    
    /**********************************************
    * Runs the GUI
    * @param args Standard declaration for main
    **********************************************/
    public static void main(String args[]) {
        hostGUI hgui = new hostGUI();
    }

	/**Initializes each variable*/
	public hostGUI() {
		setTitle("GV-NAPSTER Host");
		getContentPane();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/**FOR THE CONNECTION SECTION */

		/** creates a string of speed options for the combobox */
		String[] speedListArray = new String[] {"Ethernet", "T1"};
		speedList = new JComboBox<>(speedListArray);

		Panel = (JPanel) getContentPane();
		Panel.setLayout(null);

		/**Initializes connect button*/
		connectBut = new JButton("Connect");
		connectBut.addActionListener(this);

		/**Initializes labels and textboxes*/
		hostIPTXT = new JTextField("",10);
		portTXT = new JTextField("",10);
		hostNameTXT = new JTextField("",10);
		userNameTXT = new JTextField("",10);
		connectTitle = new JLabel("<html> <font color='blue'>CONNECTION</font></html>");
		IPNameLBL = new JLabel("Server Hostname:");
		portNumberLBL = new JLabel("Port:");
        	userNameLBL = new JLabel("Username:");
        	hostNameLBL = new JLabel("Hostname:");
        	speedLBL = new JLabel("Speed:");

		/**Organizes components on the panel*/
		connectTitle.setBounds(5,5, 100, 26);
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

		/**FOR THE KEYWORD SEARCH SECTION */

		/**Array to display files from keyword search. */
		String[] fileListArray = new String[] {"Speed\t||  HostName   \t||  filename", "Ethernet\t||  191.12.32.96\t||  example.txt"};

		/**Create text area to show list of retirved files with hostname and speed */
		searchArea = new JTextArea(700,150);
		for(int a = 0; a < fileListArray.length; a++)
		{
			searchArea.append(fileListArray[a] + "\n");
		}
		searchArea.setEditable(false); //user can't edit this text area 
		scroll = new JScrollPane(searchArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		/**Initialize Variables */
		keywordTXT = new JTextField("",10);
		searchTitle = new JLabel("<html> <font color='blue'>SEARCH</font></html>");
		keywordLBL = new JLabel("Server Hostname:");
		searchBut = new JButton("Search");
		searchBut.addActionListener(this);

		/**Set Location on Panel */
		searchTitle.setBounds(5, 127, 100, 26);
		keywordLBL.setBounds(10, 150, 160, 26);
		keywordTXT.setBounds(120, 150, 250, 26);
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
		commandTXT = new JTextField("",10);
		FTPTitle = new JLabel("<html> <font color='blue'>FTP</font></html>");
		enterCommandLBL = new JLabel("Enter Command:");
		goBut = new JButton("Go");
		goBut.addActionListener(this);

		/**create text area for user to see server esponse and entered commands */
		commandArea = new JTextArea(600,200);
		for(int a = 0; a < fileListArray.length; a++)
		{
			commandArea.append(fileListArray[a] + "\n");
		}
		commandArea.setEditable(false); //user can't edit this text area 
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
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == connectBut) {	
            
		}
		else if(e.getSource() == searchBut){

		}
		else if(e.getSource() == goBut){
			dispose();
			if(commandTXT.getText() == "quit")
			{
				//call the quit function
			}
			else if(commandTXT.getText() == "retr")			//will need to change this, only look st first word of commandTXT
			{
				//call the retr function, look at remote host connected with that file
				//probably have to iterate through array till specified filename is found
			}
		}
	}
}
