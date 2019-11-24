package server;

import java.net.*;
import java.io.*;
import java.util.*;

import file.FileReceive;
import file.FileSend;

public class TcpServer
{
	public static final int PORT=8081;
	static ServerSocket serverSocket;
	static HashMap<Socket,String> hashMap = new HashMap<>();
	static Socket socket;
	static String name = null;
	static int count = 0;
	private boolean isStop = false;

	public static String ADD_CLIENT = "<ADD_CLIENT>";
	public static String REMOVE_CLIENT = "<REMOVE_CLIENT>";
	public static String LIST_CLIENTS = "<LIST_CLIENTS>";
	public static String START_CHAT_DIALOG = "<START_CHAT_DIALOG>";
	public static String STOP_CHAT_DIALOG = "<STOP_CHAT_DIALOG>";
	public static String SEND_DIRECT_MESSAGE = "<SEND_DIRECT_MESSAGE>";
	public static String SEND_DIRECT_FILE = "<SEND_DIRECT_FILE>";
	public static String ACTIVE_DIALOG = "<ACTIVE_DIALOG>";
//	public static String DEACTIVE_DIALOG = "<DEACTIVE_DIALOG>";

	public TcpServer(int port) throws Exception {
		serverSocket=new ServerSocket(port);
		ServerForm.updateMessage("@server: <STARTED> " + serverSocket);

		(new Server_accepter()).start();
	}

	public void stopserver() throws IOException {
		isStop = true;
		if (socket != null) socket.close();
		serverSocket.close();
	}

	public class Server_accepter extends Thread {

		DataInputStream reader;
		DataOutputStream out;

		public void run(){

			while(true){

				try{
					if(!isStop) socket = serverSocket.accept();
					else return;
					ServerForm.updateMessage("Connection Accepted: "+socket);

					reader = new DataInputStream(socket.getInputStream());
					String name = reader.readUTF();

					out = new DataOutputStream(socket.getOutputStream());	
					
					if((name == null) || nameAlreadyPresent(name)){
						out.writeUTF("alias already taken... please enter another alias"); out.flush();
						System.out.println("alias " + name + " already taken... please enter any another alias");
						continue;
					}

					Socket socket1=socket;

					count=count+1;

					hashMap.put(socket1,name);

					ServerForm.updateMessage("@server: " + hashMap.get(socket1)+" has entered in the room");
					out.writeUTF(name+" you are now part of group"); out.flush();
					remotePrint(socket1, hashMap.get(socket1)+" has entered in the room" + ADD_CLIENT);

					Set<Socket> ss = hashMap.keySet();
					Iterator<Socket> it1 = ss.iterator();
					String list_all_active_users = LIST_CLIENTS;

					while(it1.hasNext()){
						String alias = hashMap.get(it1.next());
						list_all_active_users = list_all_active_users + " " + alias;
					}

					out.writeUTF(list_all_active_users); out.flush();
					ServerForm.updateMessage("List client: " + list_all_active_users);

				}catch(Exception exp){
					exp.printStackTrace();
				}

				final Socket socket2 = socket;

				Thread receiver = new Thread(new Runnable(){
					public void run(){

						try{
							String str = null;
							DataInputStream in = new DataInputStream(socket2.getInputStream());

							while(true){
								str = in.readUTF();

								if((str == null) || (str.equals("null")) || (str.equals("END"))){
									ServerForm.updateMessage("@server: " + hashMap.get(socket2) + " disconnects");
									remotePrint(socket2,hashMap.get(socket2)+" disconnects" + REMOVE_CLIENT);
									hashMap.remove(socket2);
									break;
								}			

								boolean flag_nextloop = false;

								if((str.contains(START_CHAT_DIALOG+"")) || (str.contains(SEND_DIRECT_MESSAGE+"")) 
										|| (str.contains(ACTIVE_DIALOG+"")) || (str.contains(STOP_CHAT_DIALOG+""))) {
									String[] splits=str.split(" ");

									ServerForm.updateMessage("@server: " + str);
									String to_client=splits[3];

									// find client to send message
									Set<Socket> sockets = hashMap.keySet();
									Iterator<Socket> iterator = sockets.iterator();

									while(iterator.hasNext()){
										Socket sc1 = iterator.next();
										String temp = hashMap.get(sc1);
										if(temp.equals(to_client)){
											DataOutputStream out1 = new DataOutputStream(sc1.getOutputStream());
											out1.writeUTF(str); out1.flush();
											flag_nextloop = true;
											break;
										}
									}

								} else if (str.contains(SEND_DIRECT_FILE)) {
									Socket socket3 = socket2;
									String[] splits = str.split(" ");
									ServerForm.updateMessage("@server: " + str);
									long fileSize = Long.parseLong(splits[5]);
									String fileName = splits[4];
									String to_client = splits[3];
									// receive file
									FileReceive fileReceive = new FileReceive(ServerForm.saveServerDirDefault, fileName, fileSize, socket3);
									fileReceive.receive();
									
									Set<Socket> sockets = hashMap.keySet();
									Iterator<Socket> iterator = sockets.iterator();
									// find client to send file
									while(iterator.hasNext()) {
										Socket sc1 = iterator.next();
										String temp = hashMap.get(sc1);
										if(temp.equals(to_client)) {
											DataOutputStream out2 = new DataOutputStream(sc1.getOutputStream());
											out2.writeUTF(str); out2.flush();
											// send file to client was found in list client
											FileSend fileSend = new FileSend(ServerForm.saveServerDirDefault, fileName, out2);
											fileSend.send();
											flag_nextloop = true;
											break;
										}
									}										
									File f = new File(ServerForm.saveServerDirDefault + File.separator + fileName);
									if (f.exists()) {
										f.delete();
									}
								}

								if(flag_nextloop == true){
									continue;
								}

								ServerForm.updateMessage("@server: " + "<CHAT_ROOM> " + hashMap.get(socket2) + ": " + str);
								remotePrint(socket2,"" + hashMap.get(socket2) + ": " + str); // send message in chat room
							}

						}catch (Exception e) {
							if(!isStop) ServerForm.updateMessage("@server: " + hashMap.get(socket2) + " disconnects");
							remotePrint(socket2,hashMap.get(socket2)+" disconnects" + REMOVE_CLIENT);
							hashMap.remove(socket2);
						}
					}						
				});
				receiver.start();
			}
		}
		
		// send mesage to socket != current
		public void remotePrint(Socket current, String text) {
			try {
				Set<Socket> sockets = hashMap.keySet();
				Iterator<Socket> iterator = sockets.iterator();
				while(iterator.hasNext()){
					Socket temp = iterator.next();
					if(temp != current){
						DataOutputStream out1 = new DataOutputStream(temp.getOutputStream());
						out1.writeUTF(text); out1.flush();
					}
				}
			}catch(Exception exp){
				exp.printStackTrace();
			}
		}
		
		// check clientName in list client
		public boolean nameAlreadyPresent(String name) {
			boolean present = false;
			Set<Socket> sockets = hashMap.keySet();
			Iterator<Socket> iterator = sockets.iterator();
			while(iterator.hasNext()){
				String alias = hashMap.get(iterator.next());
				if(alias.equals(name)){
					present = true; break;
				}
			}
			return present;
		}
	}
}
