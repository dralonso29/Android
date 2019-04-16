package serverAndroid01;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class Server02 {
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		//String host = "127.0.0.1";
		String ADDRESS = InetAddress.getLocalHost().getHostName();/*InetAddress.getByName(host);*/
		System.out.println(ADDRESS);
		final int PORT = 5000;
		ServerSocket serverS = new ServerSocket();
		SocketAddress endpoint = new InetSocketAddress(ADDRESS, PORT);
		serverS.bind(endpoint);
		Socket s;
		try {
			while(true) {
				System.out.println("Waiting for connection...");
				s = serverS.accept();
				System.out.println("A client is connected: "+s.getInetAddress().getHostName());
				//showMessages(s);
				
				/*lo suyo seria que el cliente le escribiese el pc del que quiere saber 
				 * la temperatura*/
				getTemperature();
			}	
		}finally {
			serverS.close();
		}
	}

	private static void getTemperature() {
		//String command = "ls *.cap";
		/*Para no verificar la SHA y que se quede pillado: ssh -o "StrictHostKeyChecking no" alonsod@delta04.aulas.gsyc.urjc.es
*/

		String command = "ssh -i ~/.ssh/android_ed25519 alonsod@delta01 cat /sys/class/thermal/thermal_zone*/temp";
		Runtime rt = Runtime.getRuntime();
		StringBuilder output = new StringBuilder();
		try {
			System.out.println("Dentro del try catch");
			Process p = rt.exec(command);
			System.out.println("Ejecutamos el comando: "+ command);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			System.out.println("Despues del BufferedReader");
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("LEEMOS: "+line+"\n");
				output.append(line + "\n");
			}
			
			int exitVal;
			exitVal = p.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println(output);
				System.exit(0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void showMessages(Socket s) throws IOException, ClassNotFoundException {
		DataInputStream in;
		String messageClient;
		try {
			in = new DataInputStream(s.getInputStream());
            PrintWriter salida = new PrintWriter(s.getOutputStream(), true);
            salida.println("Escribe lo que quieras");
            messageClient = (String) in.readUTF();
            System.out.println(messageClient);
            in.close();
		} finally {
            s.close();
        }
	}	
}
