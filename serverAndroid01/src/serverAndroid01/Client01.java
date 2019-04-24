package serverAndroid01;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import serverAndroid01.Messages.RequestClient;

public class Client01 {

	public static void main(String[] args) {
		try {
			Socket s = new Socket("10.1.135.183", 5000);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			RequestClient rc = new RequestClient();
			rc.writeTo(dos);
			System.out.println("Tipo: "+ dis.readInt()+" Respuesta: "+dis.readUTF());
//			Messages reponse = Messages.readFrom(dis);
//			if(reponse != null) {
//				reponse.writeTo(dos); // send reply to client
//			}
			s.close();
		}catch (ConnectException e) {
			System.out.println("Connection refused "+ e);
		}catch (UnknownHostException e) {
			System.out.println("Cannot connect to host "+ e);
		}catch (IOException e) {
			System.out.println("IOExcepton "+ e);
		}
	}

}
