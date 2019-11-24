package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import file.FileSend;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class ChatDialog extends JDialog implements ActionListener,WindowListener{

	JFrame parentFrame;
	JTextPane txtDisplayChat;
	JButton btnSend, btnSendFile, btnLike, btnSad, btnScared, btnSmileBig, btnSmileCry, btnSmile, btnCrying, btnHeartEye;
	JTextField txtMsg;
	DataOutputStream writer;
	
	private Socket socket;

	public static String SEND_DIRECT_MESSAGE = "<SEND_DIRECT_MESSAGE>";
	public static String SEND_DIRECT_FILE = "<SEND_DIRECT_FILE>";
	public static String ACTIVE_DIALOG = "<ACTIVE_DIALOG>";
	public static String STOP_CHAT_DIALOG = "<STOP_CHAT_DIALOG>";

	public ChatDialog(JFrame frame,Socket socket,String title){
		super(frame,title,Dialog.ModalityType.MODELESS);
		parentFrame = frame;
		this.socket = socket;
		try{
			writer = new DataOutputStream(this.socket.getOutputStream());	
		}catch(IOException exp){
			System.out.println(exp.getMessage());
		}

		setSize(400,400);
		setLocationRelativeTo(parentFrame);

		int bottomHeight = 35;
		int bottomWidth = 35;
		Dimension d = new Dimension(bottomWidth,bottomHeight);

		JPanel p = new JPanel(new BorderLayout());
		txtDisplayChat = new JTextPane();
		txtDisplayChat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtDisplayChat.setEditable(false);
		txtDisplayChat.setContentType( "text/html" );
		txtDisplayChat.setMargin(new Insets(6, 6, 6, 6));
		txtDisplayChat.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		appendToPane(txtDisplayChat, "<div class='clear' style='background-color:white'></div>"); //set default background
		updateChat_notify("Connected...");

		JScrollPane sp=new JScrollPane(txtDisplayChat);

		JPanel pnSouth = new JPanel(new BorderLayout());
		JPanel bottomPane = new JPanel(new GridBagLayout());
		
		btnSend = new JButton("");
		btnSend.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSend.setContentAreaFilled(false);
		btnSend.setIcon(new ImageIcon(TcpClient.class.getResource("/image/send.png")));
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text=txtMsg.getText();
				if(text.trim().length()==0){
					return;
				}

				try {
					writer.writeUTF(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + text);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send(txtMsg.getText());
				txtMsg.setText("");
			}
		});
		btnSend.setPreferredSize(d);

		btnSendFile = new JButton("");
		btnSendFile.setPreferredSize(d);
		btnSendFile.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSendFile.setContentAreaFilled(false);
		btnSendFile.setIcon(new ImageIcon(TcpClient.class.getResource("/image/attachment.png")));
		btnSendFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showDialog(ChatDialog.this, "Select");
				File file = fileChooser.getSelectedFile();
				String filePath = "", fileName = "";
				long fileSize = file.length();
				if (file != null) {
					filePath = file.getPath();
					fileName = file.getName();
				}
				try {
					writer.writeUTF(SEND_DIRECT_FILE + " " + getTitle() + " " + fileName + " " + fileSize);
					writer.flush();
					FileSend fileSend = new FileSend(filePath, "", writer);
					fileSend.send();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				if (fileName.length() > 0) 
					updateSendFile_notify(fileName + " was send.");
				txtMsg.setText("");
			}
		});
		
		btnLike = new JButton("");
		btnLike.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnLike.setContentAreaFilled(false);
		btnLike.setIcon(new ImageIcon(TcpClient.class.getResource("/image/like.png")));
		btnLike.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/like.png") +"'></img>";
				try {
					writer.writeUTF(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
				txtMsg.setText("");
			}
		});
		btnLike.setPreferredSize(d);
		
		btnCrying = new JButton("");
		btnCrying.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnCrying.setContentAreaFilled(false);
		btnCrying.setIcon(new ImageIcon(TcpClient.class.getResource("/image/crying.png")));
		btnCrying.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/crying.png") +"'></img>";
				try {
					writer.writeUTF(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
				txtMsg.setText("");
			}
		});
		btnCrying.setPreferredSize(d);
		
		btnHeartEye = new JButton("");
		btnHeartEye.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnHeartEye.setContentAreaFilled(false);
		btnHeartEye.setIcon(new ImageIcon(TcpClient.class.getResource("/image/heart_eye.png")));
		btnHeartEye.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/heart_eye.png") +"'></img>";
				try {
					writer.writeUTF(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
				txtMsg.setText("");
			}
		});
		btnHeartEye.setPreferredSize(d);
		
		btnSad = new JButton("");
		btnSad.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSad.setContentAreaFilled(false);
		btnSad.setIcon(new ImageIcon(TcpClient.class.getResource("/image/sad.png")));
		btnSad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/sad.png") +"'></img>";
				try {
					writer.writeUTF(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
				txtMsg.setText("");
			}
		});
		btnSad.setPreferredSize(d);
		
		btnScared = new JButton("");
		btnScared.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnScared.setContentAreaFilled(false);
		btnScared.setIcon(new ImageIcon(TcpClient.class.getResource("/image/scared.png")));
		btnScared.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/scared.png") +"'></img>";
				try {
					writer.writeUTF(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
				txtMsg.setText("");
			}
		});
		btnScared.setPreferredSize(d);
		
		btnSmileBig = new JButton("");
		btnSmileBig.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSmileBig.setContentAreaFilled(false);
		btnSmileBig.setIcon(new ImageIcon(TcpClient.class.getResource("/image/smile_big.png")));
		btnSmileBig.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/smile_big.png") +"'></img>";
				try {
					writer.writeUTF(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
				txtMsg.setText("");
			}
		});
		btnSmileBig.setPreferredSize(d);
		
		btnSmileCry = new JButton("");
		btnSmileCry.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSmileCry.setContentAreaFilled(false);
		btnSmileCry.setIcon(new ImageIcon(TcpClient.class.getResource("/image/smile_cry.png")));
		btnSmileCry.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/smile_cry.png") +"'></img>";
				try {
					writer.writeUTF(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
				txtMsg.setText("");
			}
		});
		btnSmileCry.setPreferredSize(d);
		
		btnSmile = new JButton("");
		btnSmile.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSmile.setContentAreaFilled(false);
		btnSmile.setIcon(new ImageIcon(TcpClient.class.getResource("/image/smile.png")));
		btnSmile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "<img src='" + TcpClient.class.getResource("/image/smile.png") +"'></img>";
				try {
					writer.writeUTF(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + msg);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send_Symbol(msg);
				txtMsg.setText("");
			}
		});
		btnSmileCry.setPreferredSize(d);

		txtMsg = new JTextField(100);
		txtMsg.setPreferredSize(new Dimension(0, 32));
		txtMsg.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		txtMsg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text=txtMsg.getText();
				if(text.trim().length()==0){
					return;
				}

				try {
					writer.writeUTF(SEND_DIRECT_MESSAGE + " " + getTitle() + " " + text);
					writer.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateChat_send(txtMsg.getText());
				txtMsg.setText("");
			}
		});
		addWindowListener(this);

		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridx=0;	gbc.gridy=0;	gbc.gridheight=1;	gbc.gridwidth=1;	gbc.anchor=GridBagConstraints.CENTER;
		gbc.insets = new Insets(2, 2, 0, 2);
		gbc.weightx=1.0;	gbc.weighty=1.0; 	
		gbc.fill=GridBagConstraints.BOTH;
		bottomPane.add(txtMsg,gbc);

		gbc.weightx=0;	gbc.weighty=0; 	
		gbc.gridx=1;	
		gbc.fill=GridBagConstraints.VERTICAL;
		bottomPane.add(btnSend,gbc);

		gbc.weightx=0;	gbc.weighty=0; 	
		gbc.gridx=2;	
		gbc.fill=GridBagConstraints.VERTICAL;
		bottomPane.add(btnLike,gbc);
		
		gbc.weightx=0;	gbc.weighty=0; 	
		gbc.gridx=3;	
		gbc.fill=GridBagConstraints.VERTICAL;
		bottomPane.add(btnSendFile,gbc);
		
		JPanel pnIcon = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnIcon.add(btnCrying);
		pnIcon.add(btnHeartEye);
		pnIcon.add(btnSad);
		pnIcon.add(btnScared);
		pnIcon.add(btnSmileBig);
		pnIcon.add(btnSmileCry);
		pnIcon.add(btnSmile);

		pnSouth.add(bottomPane, BorderLayout.CENTER);
		pnSouth.add(pnIcon, BorderLayout.SOUTH);
		p.add(sp, BorderLayout.CENTER);
		p.add(pnSouth, BorderLayout.SOUTH);
		add(p);

		setVisible(true);
	}

	public void updateChat_receive(String msg) {
		appendToPane(txtDisplayChat, "<table class='left' style='color: white; clear:both; width: 100%;'>"
				+ "<tr align='left'>"
				+ "<td style='width: 40%; background-color: #f1f0f0; '><font color = 'black'>" + msg + "</font></td>"
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
	
	public void updateSendFile_notify(String msg) {
		appendToPane(txtDisplayChat, "<table class='bang' style='color: white; width: 100%;'>"
				+ "<tr align='right'>"
				+ "<td style='width: 59%;'></td>"
				+ "<td style='width: 40%; background-color: #b45107;'>" + msg + "</td>"
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


	public void activeDialog(boolean b){
		if(b == true) {
			btnSend.setEnabled(true);
		}
		else {
			btnSend.setEnabled(false);
		}
	}


	public void windowClosed(WindowEvent a10){}
	public void windowActivated(WindowEvent a11){}
	public void windowDeactivated(WindowEvent a12){}
	public void windowIconified(WindowEvent a15){}
	public void windowDeiconified(WindowEvent a16){}
	public void windowOpened(WindowEvent a17){}

	public void windowClosing(WindowEvent a13){
		System.out.println("dialog closing");
		try {
			writer.writeUTF(STOP_CHAT_DIALOG + " " + getTitle());
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setVisible(false);
		dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		return;
	}

}
