package file;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileReceive {
	private String saveDirDefault;
	private String fileName;
	private long fileSize;
	private Socket socket;

	public FileReceive(String saveDirDefault, String fileName, long fileSize, Socket socket) {
		if (saveDirDefault.length() > 0) this.saveDirDefault = saveDirDefault;
		else this.saveDirDefault = "";
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.socket = socket;
	}

	public void receive() throws IOException{
		DataInputStream in1 =  new DataInputStream(socket.getInputStream());
		File f = new File(this.saveDirDefault + File.separator + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
		int byteRead, byteMustRead;
		long remain = fileSize;
		byte[] buff = new byte[10*1024];
		while(remain > 0) {
			byteMustRead = buff.length > remain ? (int) remain : buff.length;
			if (byteMustRead == 0) break;
			byteRead = in1.read(buff, 0, byteMustRead);
			bos.write(buff, 0, byteRead);
			remain -= byteRead;
		}
		try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}