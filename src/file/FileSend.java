package file;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileSend {
	
	private String fileName; 
	private DataOutputStream dos;
	private String saveDirDefault;

	public FileSend(String saveDirDefault, String fileName, DataOutputStream dos) {
		if (saveDirDefault.length() > 0) this.saveDirDefault = saveDirDefault;
		else this.saveDirDefault = "";
		this.fileName = fileName;
		this.dos = dos;
	}

	public void send() {
		File file;
		if (fileName.length() > 0)
			file = new File(saveDirDefault + File.separator + fileName);
		else 
			file = new File(saveDirDefault);
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] buff = new byte[10*1024];
		int data;
		try {
			while((data = bis.read(buff)) != -1) {
				dos.write(buff, 0, data);
			}
			dos.flush();
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}