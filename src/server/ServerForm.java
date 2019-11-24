package server;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.TextArea;
import java.awt.Font;

import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import client.TcpClient;

public class ServerForm {

	public static int port = 8081;
	public static String saveServerDirDefault = "";
	private JFrame frmServerMangement;
	private JTextField txtIP, txtPort, txtFolder;
	private JLabel lblStatus;
	private static TextArea txtMessage;
	private JButton btnStop, btnStart, btnFolder;
	TcpServer server;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerForm window = new ServerForm();
					window.frmServerMangement.setVisible(true); // show window
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ServerForm() {
		initialize();
		btnStart.setVisible(false);
		btnStop.setVisible(false);
		addEvents();
	}

	public static void updateMessage(String msg) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		String time = simpleDateFormat.format(new Date());
		txtMessage.append("[" + time + "] " + msg + "\n");
	}

	private void initialize() {
		// frmServerMangement
		frmServerMangement = new JFrame();
		frmServerMangement.setForeground(UIManager.getColor("RadioButtonMenuItem.foreground"));
		frmServerMangement.getContentPane().setForeground(UIManager.getColor("RadioButtonMenuItem.acceleratorSelectionForeground"));
		frmServerMangement.setTitle("Server");
		frmServerMangement.setResizable(false);
		frmServerMangement.setSize(950, 500);
		frmServerMangement.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServerMangement.getContentPane().setLayout(null);
		frmServerMangement.setLocationRelativeTo(null); // center creen
		frmServerMangement.setBackground(Color.ORANGE);
		frmServerMangement.getContentPane().setBackground(new Color(255, 255, 255));

		// title
		JLabel lblTitle = new JLabel();
		lblTitle.setText("<html><font color='blue'>Server Mangement</font></html>");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
		lblTitle.setBounds(375, 13, 268, 76);
		frmServerMangement.getContentPane().add(lblTitle);
		// ip
		JLabel lblIP = new JLabel("IP ADDRESS");
		lblIP.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblIP.setBounds(26, 120, 89, 16);
		frmServerMangement.getContentPane().add(lblIP);

		txtIP = new JTextField();
		txtIP.setFont(new Font("Segoe UI", Font.BOLD, 13));
		txtIP.setEditable(false);
		txtIP.setBounds(126, 114, 120, 28);
		txtIP.setColumns(10);
		frmServerMangement.getContentPane().add(txtIP);
		try {
			txtIP.setText(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// port
		JLabel lblPort = new JLabel("PORT");
		lblPort.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblPort.setBounds(26, 160, 48, 16);
		frmServerMangement.getContentPane().add(lblPort);

		txtPort = new JTextField();
		txtPort.setFont(new Font("Segoe UI", Font.BOLD, 13));
		txtPort.setColumns(10);
		txtPort.setBounds(126, 160, 120, 28);
		txtPort.requestFocus();
		frmServerMangement.getContentPane().add(txtPort);
		txtPort.setText("8081");

		// status
		JLabel lblStat = new JLabel("STATUS");
		lblStat.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblStat.setBounds(26, 200, 69, 16);
		frmServerMangement.getContentPane().add(lblStat);

		lblStatus = new JLabel();
		lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblStatus.setBounds(126, 195, 120, 28);
		lblStatus.setText("<html><font color='red'>OFF</font></html>");
		frmServerMangement.getContentPane().add(lblStatus);

		//
		txtMessage = new TextArea();
		txtMessage.setBackground(Color.BLACK);
		txtMessage.setForeground(Color.GREEN);
		txtMessage.setFont(new Font("Consolas", Font.PLAIN, 14));
		txtMessage.setEditable(false);
		txtMessage.setBounds(260, 115, 678, 348);
		frmServerMangement.getContentPane().add(txtMessage);

		// btn start
		btnStart = new JButton("START");
		btnStart.setBackground(UIManager.getColor("RadioButtonMenuItem.selectionBackground"));
		btnStart.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btnStart.setBounds(26, 283, 120, 43);
		frmServerMangement.getContentPane().add(btnStart);
		btnStart.setIcon(new ImageIcon(ServerForm.class.getResource("/image/start.png")));

		// btn stop
		btnStop = new JButton("STOP");
		btnStop.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btnStop.setBounds(26, 350, 120, 43);
		frmServerMangement.getContentPane().add(btnStop);
		btnStop.setIcon(new ImageIcon(ServerForm.class.getResource("/image/stop.png")));
		
		// btn Folder
		btnFolder = new JButton();
		btnFolder.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnFolder.setContentAreaFilled(false);
		btnFolder.setIcon(new ImageIcon(TcpClient.class.getResource("/image/folder.png")));
		btnFolder.setBounds(215, 230, 32, 32);
		frmServerMangement.getContentPane().add(btnFolder);
		
		// txt Folder
		txtFolder = new JTextField(50);
		txtFolder.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtFolder.setEditable(false);
		txtFolder.setBounds(26, 230, 185, 30);
		frmServerMangement.getContentPane().add(txtFolder);
	}

	private void addEvents() {
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					server.stopserver();
					ServerForm.updateMessage("@server: <STOPPED>");
				} catch (Exception e) {
					ServerForm.updateMessage("@server: <STOPPED>");
					e.printStackTrace();
				}
				lblStatus.setText("<html><font color='red'>OFF</font></html>");
			}
		});

		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					txtMessage.setText("");
					ServerForm.port = Integer.parseInt(txtPort.getText());
					server = new TcpServer(ServerForm.port);
					lblStatus.setText("<html><font color='green'>RUNNING...</font></html>");
				} catch (Exception e) {
					ServerForm.updateMessage("@server: Error " + e.getMessage());
				}
			}
		});
		
		btnFolder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System
						.getProperty("user.home")));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showSaveDialog(frmServerMangement);
				if (result == JFileChooser.APPROVE_OPTION) {
					saveServerDirDefault = fileChooser.getSelectedFile().getAbsolutePath().toString();
					txtFolder.setText(saveServerDirDefault);
				}
				btnStart.setVisible(true);
				btnStop.setVisible(true);
			}
		});
	}
}
