package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import file.FileReceive;
import server.ServerForm;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class TcpClient extends JFrame implements ActionListener, ListSelectionListener, MouseListener{

	private int side_panel_width = 150;
	private int bottom_panel_height = 45;
	private int server_port = 8081;
	private String server_ip = "";
	private String clientName = "";
	private JDialog loginDialog;

	private Thread receiver;
	private HashMap<String,ChatDialog> channels;	
	private Socket socket;	// server socket

	// controls
	private JButton btnSend, btnConnect, btnLike, btnSad, btnScared, btnSmileBig, btnSmileCry, btnSmile, btnCrying, btnHeartEye;
	private JTextField txtInput;
	private JTextPane txtDisplayChat;
	private JTextField txtName, txtIP, txtPort;

	private JList<String> lstOnline;
	private DefaultListModel<String> model;
	private boolean clickedAlready = false;
	private DataOutputStream writer;
	private DataInputStream reader;
	private String clientDir = "";

	static boolean threads_running_flag = false;

	public static String ADD_CLIENT = "<ADD_CLIENT>";
	public static String REMOVE_CLIENT = "<REMOVE_CLIENT>";
	public static String LIST_CLIENTS = "<LIST_CLIENTS>";
	public static String START_CHAT_DIALOG = "<START_CHAT_DIALOG>";
	public static String STOP_CHAT_DIALOG = "<STOP_CHAT_DIALOG>";
	public static String SEND_DIRECT_MESSAGE = "<SEND_DIRECT_MESSAGE>";
	public static String SEND_DIRECT_FILE = "<SEND_DIRECT_FILE>";
	public static String ACTIVE_DIALOG = "<ACTIVE_DIALOG>";
//	public static String DEACTIVE_DIALOG = "<DEACTIVE_DIALOG>";

	public TcpClient() { 
		setBackground(Color.white);
		setSize(600,550);
		setTitle("Group Messenger");
		setForeground(UIManager.getColor("RadioButtonMenuItem.foreground"));
		setResizable(false);
		getContentPane().setForeground(UIManager.getColor("RadioButtonMenuItem.acceleratorSelectionForeground"));
		channels = new HashMap<>();
		getContentPane().setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		model = new DefaultListModel<>();
		lstOnline = new JList<>(model);
		lstOnline.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstOnline.setFixedCellWidth(side_panel_width);
		lstOnline.setFixedCellHeight(30);
		lstOnline.addListSelectionListener(this);
		lstOnline.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		lstOnline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lstOnline.addMouseListener(this);

		btnSend = new JButton("");
		btnSend.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSend.setContentAreaFilled(false);
		btnSend.setIcon(new ImageIcon(TcpClient.class.getResource("/image/send.png")));
		btnSend.addActionListener(this);
		
		btnLike = new JButton("");
		btnLike.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnLike.setContentAreaFilled(false);
		btnLike.setIcon(new ImageIcon(TcpClient.class.getResource("/image/like.png")));
		btnLike.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/like.png") +"'></img>";
				
				try {
					writer.writeUTF(msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
			}
		});
		
		btnCrying = new JButton("");
		btnCrying.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnCrying.setContentAreaFilled(false);
		btnCrying.setIcon(new ImageIcon(TcpClient.class.getResource("/image/crying.png")));
		btnCrying.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/crying.png") +"'></img>";
				
				try {
					writer.writeUTF(msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
			}
		});
		
		btnHeartEye = new JButton("");
		btnHeartEye.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnHeartEye.setContentAreaFilled(false);
		btnHeartEye.setIcon(new ImageIcon(TcpClient.class.getResource("/image/heart_eye.png")));
		btnHeartEye.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/heart_eye.png") +"'></img>";
				
				try {
					writer.writeUTF(msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
			}
		});
		
		btnSad = new JButton("");
		btnSad.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSad.setContentAreaFilled(false);
		btnSad.setIcon(new ImageIcon(TcpClient.class.getResource("/image/sad.png")));
		btnSad.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/sad.png") +"'></img>";
				
				try {
					writer.writeUTF(msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
			}
		});
		
		btnScared = new JButton("");
		btnScared.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnScared.setContentAreaFilled(false);
		btnScared.setIcon(new ImageIcon(TcpClient.class.getResource("/image/scared.png")));
		btnScared.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/scared.png") +"'></img>";
				
				try {
					writer.writeUTF(msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
			}
		});
		
		btnSmileBig = new JButton("");
		btnSmileBig.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSmileBig.setContentAreaFilled(false);
		btnSmileBig.setIcon(new ImageIcon(TcpClient.class.getResource("/image/smile_big.png")));
		btnSmileBig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/smile_big.png") +"'></img>";
				
				try {
					writer.writeUTF(msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
			}
		});
		
		btnSmileCry = new JButton("");
		btnSmileCry.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSmileCry.setContentAreaFilled(false);
		btnSmileCry.setIcon(new ImageIcon(TcpClient.class.getResource("/image/smile_cry.png")));
		btnSmileCry.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/smile_cry.png") +"'></img>";
				
				try {
					writer.writeUTF(msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
			}
		});
		
		btnSmile = new JButton("");
		btnSmile.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSmile.setContentAreaFilled(false);
		btnSmile.setIcon(new ImageIcon(TcpClient.class.getResource("/image/smile.png")));
		btnSmile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/smile.png") +"'></img>";
				
				try {
					writer.writeUTF(msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
			}
		});

		createOneTimeDialog();
		createUsersPanel();
		createMainPanel();

		txtInput.requestFocus();
		setVisible(true);		
	}

	public void createOneTimeDialog(){
		loginDialog = new JDialog(this,"Login !!", Dialog.ModalityType.APPLICATION_MODAL);
		loginDialog.setSize(400,250);
		loginDialog.setResizable(false);
		loginDialog.setLocationRelativeTo(null);
		loginDialog.getContentPane().setLayout(null);
		loginDialog.setBackground(Color.ORANGE);
		loginDialog.getContentPane().setBackground(new Color(146, 186, 8));
		loginDialog.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				System.out.println("close window");
				dispose();
				System.exit(1);
			}
		});

		JLabel lblName = new JLabel("<html><font color='blue'>NAME: </font></html>");
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblName.setBounds(30, 80, 100, 50);
		loginDialog.getContentPane().add(lblName);

		txtName = new JTextField();
		txtName.setColumns(20);
		txtName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtName.setBounds(80, 90, 280, 32);
		txtName.addActionListener(this);
		loginDialog.getContentPane().add(txtName);

		JLabel lblIP = new JLabel("<html><font color='blue'>IP: </font></html>");
		lblIP.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblIP.setBounds(30, 20, 20, 50);
		loginDialog.getContentPane().add(lblIP);

		txtIP = new JTextField();
		txtIP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtIP.setColumns(10);
		txtIP.setText("localhost");
		txtIP.setBounds(80, 30, 150, 32);
		txtIP.addActionListener(this);
		loginDialog.getContentPane().add(txtIP);

		JLabel lblPort = new JLabel("<html><font color='blue'>PORT: </font></html>");
		lblPort.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblPort.setBounds(250, 20, 50, 50);
		loginDialog.getContentPane().add(lblPort);

		txtPort = new JTextField();
		txtPort.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtPort.setColumns(10);
		txtPort.setText("8081");
		txtPort.setBounds(300, 30, 60, 32);
		txtPort.addActionListener(this);
		loginDialog.getContentPane().add(txtPort);

		btnConnect = new JButton("CONNECT");
		btnConnect.setBackground(UIManager.getColor("RadioButtonMenuItem.selectionBackground"));
		btnConnect.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btnConnect.setBounds(230, 140, 130, 35);
		btnConnect.setIcon(new javax.swing.ImageIcon(ServerForm.class.getResource("/image/connect.png")));
		btnConnect.addActionListener(this);
		loginDialog.getContentPane().add(btnConnect);

		loginDialog.setVisible(true);
		txtName.requestFocus();
	}

	public void createUsersPanel(){

		JLabel lblImage = new JLabel();
		lblImage.setIcon(new ImageIcon(TcpClient.class.getResource("/image/user.png")));
		lblImage.setBounds(475, 20, 50, 50);
		getContentPane().add(lblImage);
		
		JTextField txtName = new JTextField();
		txtName.setText(clientName);
		txtName.setHorizontalAlignment(JTextField.CENTER);
		txtName.setForeground(Color.RED);
		txtName.setEditable(false);
		txtName.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtName.setBounds(475, 75, 50, 30);
		getContentPane().add(txtName);
		
		
		JLabel lblOnline = new JLabel("<html><Font color='green'>Online</Font></html>");
		lblOnline.setFont(new Font("Segoe UI", Font.BOLD, 14));
		JScrollPane sc = new JScrollPane(lstOnline);

		lblOnline.setBounds(475, 100, 100, 50);
		getContentPane().add(lblOnline);
		
		sc.setBounds(420, 150, 150, 300);
		getContentPane().add(sc);
	}

	public void createMainPanel(){

		txtInput = new JTextField("");
		txtInput.setPreferredSize(new Dimension(100,bottom_panel_height));
		txtInput.setFont(new Font("Arial",Font.PLAIN,20));
		txtInput.setBounds(20, 410, 270, 40);
		txtInput.addActionListener(this);
		getContentPane().add(txtInput);
		
		btnSend.setBounds(300, 410, 50, 40);
		getContentPane().add(btnSend);
		
		btnLike.setBounds(350, 410, 50, 40);
		getContentPane().add(btnLike);
		
		btnCrying.setBounds(20, 460, 32, 32);
		getContentPane().add(btnCrying);
		
		btnHeartEye.setBounds(60, 460, 32, 32);
		getContentPane().add(btnHeartEye);
		
		btnSad.setBounds(100, 460, 32, 32);
		getContentPane().add(btnSad);
		
		btnScared.setBounds(140, 460, 32, 32);
		getContentPane().add(btnScared);
		
		btnSmileBig.setBounds(180, 460, 32, 32);
		getContentPane().add(btnSmileBig);
		
		btnSmileCry.setBounds(220, 460, 32, 32);
		getContentPane().add(btnSmileCry);
		
		btnSmile.setBounds(260, 460, 32, 32);
		getContentPane().add(btnSmile);

		txtDisplayChat = new JTextPane();
		txtDisplayChat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtDisplayChat.setEditable(false);
		txtDisplayChat.setContentType( "text/html" );
		txtDisplayChat.setMargin(new Insets(6, 6, 6, 6));
		txtDisplayChat.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		appendToPane(txtDisplayChat, "<div class='clear' style='background-color:white'></div>"); //set default background
		updateChat_notify("Welcome...");
		
		JScrollPane sp = new JScrollPane(txtDisplayChat);
		sp.setBounds(20, 20, 380, 380);
		getContentPane().add(sp);
	}
	
	public void updateChat_receive(String name, String msg) {
		appendToPane(txtDisplayChat, "<table class='left' style='color: white; clear:both; width: 100%;'>"
				+ "<tr align='left'>"
				+ "<td style='width: 5%; background-color: #49600b; '>" + name + "</td>"
				+ "<td style='width: 35%; background-color: #f1f0f0; '><font color = 'black'>" + msg + "</font></td>"
				+ "<td style='width: 59%; '></td>"
				+ "</tr>"
				+ "</table>");
	}

	public void updateChat_send(String msg) {				
		appendToPane(txtDisplayChat, "<table class='bang' style='color: white; clear:both; width: 100%;'>"
				+ "<tr align='right'>"
				+ "<td style='width: 59%; '></td>"
				+ "<td style='width: 40%; background-color: #0084ff;'>" + msg 
				+"</td> </tr>"
				+ "</table>");
	}
	
	public void updateChat_notify(String msg) {
		appendToPane(txtDisplayChat, "<table class='bang' style='color: white; width: 100%;'>"
				+ "<tr align='left'>"
				+ "<td style='width: 40%; background-color: #f1c40f;'>" + msg + "</td>"
				+ "<td style='width: 59%; '></td>"
				+ "</tr>"
				+ "</table>");
	}

	
	public void updateChat_send_Symbol(String msg) {
		appendToPane(txtDisplayChat, "<table style='width: 100%;'>"
				+ "<tr align='right'>"
				+ "<td style='width: 59%;'></td>"
				+ "<td style='width: 40%;'>" + msg 
				+"</td> </tr>"
				+ "</table>");
	}

	// send html to pane
	private void appendToPane(JTextPane tp, String msg){
		HTMLDocument doc = (HTMLDocument)tp.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
		try {
			editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
			tp.setCaretPosition(doc.getLength());

		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void test(Object in){
		System.out.println(in.toString());
	}

	public static void main(String args[]){
		TcpClient client = new TcpClient();
	}

	public void actionPerformed(ActionEvent action){

		// for oneTimeDialog
		if((action.getSource() == btnConnect || action.getSource() == txtName ||
				action.getSource() == txtIP) && (!clickedAlready)){
			test("Enter a name: "+ txtName.getText());	

			if((txtName.getText() != null) && (txtIP.getText() != null) && (txtPort.getText() != null) &&
					(txtName.getText().length() != 0) && (txtIP.getText().length() != 0) && (txtPort.getText().length() != 0)){

				server_ip = txtIP.getText();
				int testPort = 0;
				try{
					testPort = Integer.parseInt(txtPort.getText());
				}catch(Exception exp){
					JOptionPane.showMessageDialog(this,"Invalid Server port format","Error!",JOptionPane.ERROR_MESSAGE);
					return;
				}

				server_port = testPort;

				try {
					socket = null;

					if(server_ip.equals("localhost")) {
						InetAddress addr = InetAddress.getByName("localhost");
						socket = new Socket(addr,server_port);
					} else {
						socket = new Socket(server_ip,server_port);
					}

					test("Socket = " + socket);

					clientName = txtName.getText().trim().replace(" ","_");
					test("Enter a name: " + clientName);		

					writer = new DataOutputStream(socket.getOutputStream());
					writer.writeUTF(clientName); writer.flush();

					reader = new DataInputStream(socket.getInputStream());
					String fromServer = reader.readUTF();

					if(fromServer.equals("alias already taken... please enter another alias")) {
						JOptionPane.showMessageDialog(this,"Username already exists !!","Error!",JOptionPane.ERROR_MESSAGE);
						txtName.setText("");
						return;
					} else {
						setTitle("Group Messenger");
						
						String list_all_actives = reader.readUTF();
						if(list_all_actives.contains(LIST_CLIENTS)){
							test("List recieved " + list_all_actives);

							String[] aliases = list_all_actives.split(" ");
							for(int i = 1; i < aliases.length; i++){
								addAliasToList(aliases[i]);
							}
						}

						final Socket socket2 = socket;

						receiver = new Thread(new Runnable(){
							public void run(){

								try{
									boolean flag = true;
									String str = null;
									int index_add_client = -1;
									int index_remove_client = -1;
									DataInputStream in = new DataInputStream(socket2.getInputStream());

									while(flag) {	
										str = in.readUTF(); test("log: "+str);

										if(str.contains(START_CHAT_DIALOG + "")) {
											String[] splits=str.split(" ");											
											ChatDialog d=channels.get(splits[1]);	

											if(d!=null){		
												if(!d.isVisible()){
													channels.put(splits[1],new ChatDialog(TcpClient.this,socket,splits[3]+" to "+splits[1]));	
												}else{
													d.activeDialog(true);
													d.updateChat_notify(splits[1] + " connects");
												}

											} else{	
												channels.put(splits[1],new ChatDialog(TcpClient.this,socket,splits[3]+" to "+splits[1]));	
											}

											continue;
										} else if(str.contains(SEND_DIRECT_MESSAGE+"")) {
											String[] splits=str.split(" ");
											ChatDialog d=channels.get(splits[1]);
											if(d!=null){
												d.updateChat_receive(str.substring(str.indexOf(splits[4])));
											}

											continue;
										} 
										else if(str.contains(SEND_DIRECT_FILE)){
											String[] splits=str.split(" ");
											String fileName = splits[4];
											long fileSize = Long.parseLong(splits[5]);
											ChatDialog d = channels.get(splits[1]);
											if(d!=null){
												d.updateChat_notify(fileName + " has been sent to you.");
												JFileChooser fileChooser = new JFileChooser();
												fileChooser.setCurrentDirectory(new File(System
														.getProperty("user.home")));
												fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
												int result = fileChooser.showSaveDialog(d);
												if (result == JFileChooser.APPROVE_OPTION) {
													clientDir = fileChooser.getSelectedFile().getAbsolutePath().toString();
												}
												FileReceive fileReceive = new FileReceive(clientDir, fileName, fileSize, socket2);
												fileReceive.receive();
											}

											continue;
										} 
										else if(str.contains(ACTIVE_DIALOG + "")){
											String[] splits=str.split(" ");
											ChatDialog d=channels.get(splits[1]);	
											if(d!=null){	
												d.activeDialog(true);
												d.updateChat_notify(splits[1] + " connects");
											}

											continue;
										} else if(str.contains(STOP_CHAT_DIALOG+"")) {
											String[] splits = str.split(" ");
											ChatDialog d = channels.get(splits[1]);	
											if(d != null){	
												d.activeDialog(false);
												d.updateChat_notify(splits[1] + " disconnects");
											}

											continue;
										}

										index_add_client = str.indexOf(ADD_CLIENT+"");
										boolean old_client = false;

										if(index_add_client != -1){
											int index = str.indexOf(" has entered in the room");
											if(index == -1){	 

												index = index_add_client;	
												old_client = true;   
												addAliasToList(str.substring(0, index));	
												str = str.substring(0, index);

											}else{
												addAliasToList(str.substring(0, index));	
												str = str.substring(0, index_add_client);
											}	

										}	

										if((str == null) || (old_client == true)){
											continue;
										}

										index_remove_client = str.indexOf(REMOVE_CLIENT+"");
										if(index_remove_client != -1){
											int index = str.indexOf(" disconnects");
											if(index == -1){   
												index = index_remove_client;    
												old_client = true;    
												removeAliasFromList(str.substring(0,index));
												str = str.substring(0, index);	
											} else {
												removeAliasFromList(str.substring(0,index));
												str = str.substring(0, index_remove_client);	
											}
										}	

										if((str.trim().length() == 0) || (old_client == true)){
											continue;
										}

										System.out.println("$ "+str);
										if (str.contains("disconnects") || str.contains("has entered in the room")) {
											updateChat_notify(str);
										} else {
											StringTokenizer st = new StringTokenizer(str);
											String name = st.nextToken();
											name = name.substring(0, name.length() - 1);
											String msg = "";
											while(st.hasMoreTokens()) {
												msg += st.nextToken() + " ";
											}
											updateChat_receive(name, msg); // chat room
										}

										if(str.equals("END")){
											flag=false;
										}
									}

								}catch(Exception exp){
									exp.printStackTrace();
								}
							}
						});

						threads_running_flag = true;
						receiver.start();

						addAliasToList(clientName);

						loginDialog.setVisible(false);
					}

					clickedAlready = true;

				}catch(Exception exp){
					test(exp.getMessage());
				}

			} else {
				JOptionPane.showMessageDialog(this,"Enter valid Name/IPaddress:port","Error!",JOptionPane.ERROR_MESSAGE);
			}

		} else if(action.getSource() == btnSend || action.getSource() == txtInput){
			String text = txtInput.getText();
			if(text.trim().length() == 0){
				return;
			}

			try {
				writer.writeUTF(text);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			updateChat_send(txtInput.getText());
			txtInput.setText("");
		}
	}

	public void addAliasToList(String alias){
		if(!model.contains(alias) && (!alias.trim().equals(clientName))){
			model.addElement(alias);	
			channels.put(alias,null);
		}
	}

	public void removeAliasFromList(String alias){
		model.removeElement(alias);
		ChatDialog d = channels.get(alias);
		if(d != null) {
			if(d.isVisible()){
				d.activeDialog(false);
				d.updateChat_notify(alias + " disconnects permanently");
			}
		}
		channels.remove(alias);
	}

	public void valueChanged(ListSelectionEvent event){
		if(!event.getValueIsAdjusting()){
			JList source=(JList)event.getSource();
			String item = "";
			if(source.getSelectedValue() != null){
				item = source.getSelectedValue().toString().trim();
			}
		}
	}

	public void mouseClicked(MouseEvent e){
		if(e.getClickCount()==2){
			int index = lstOnline.locationToIndex(e.getPoint());
			String title = clientName + " to " + lstOnline.getSelectedValue();
			System.out.println("Double clicked on Item " + index + " " + title);

			ChatDialog d = channels.get(lstOnline.getSelectedValue());
			if(d == null){
				channels.put(lstOnline.getSelectedValue(),new ChatDialog(this,socket,title));
				try {
					writer.writeUTF(START_CHAT_DIALOG + " " + title);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				if(!d.isVisible()) {
					channels.put(lstOnline.getSelectedValue(),new ChatDialog(this,socket,title));
					try {
						writer.writeUTF(START_CHAT_DIALOG + " " + title);
						writer.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					d.setVisible(true);	
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
}
