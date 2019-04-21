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
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server02 {
	//private static final long TIMEWAIT = 10000; // 10 seconds

	public static void main(String[] args) throws Exception{
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
				processClientRequest(s);
			}	
		}finally {
			serverS.close();
		}
	}
	
	public static String createAnswer(String temperatura) {
		String pattern = "yyyy/MM/dd HH:mm:ss";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
		LocalDateTime now = LocalDateTime.now();
		if(temperatura.length() < 1) {
			return dtf.format(now) + " None";
		}
		return dtf.format(now) + " " + temperatura;
	}

	private static void processClientRequest(Socket s) throws Exception {
		String request = clientRequest(s);
		System.out.println("La consulta del usuario es: "+request+"&&&");
		if (request != null) {
			String temperature = getTemperature(request);
			String answer = createAnswer(temperature);
			sendAnswer(s, answer);
//			if(!answerWasReceived(s)) { //  if Client no receives temperature
//				sendAnswer(s, answer); // (Re)send answer
//			}
		}
	}

	private static boolean answerWasReceived(Socket s) throws IOException, InterruptedException {
		//Thread.sleep(TIMEWAIT);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		return in.readLine() == "ok";
	}

	private static String clientRequest(Socket s) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		return in.readLine();
	}

	private static String getTemperature(String host) throws Exception {
		String command = "ssh -i ~/.ssh/android_ed25519 -o StrictHostKeyChecking=no alonsod@"+host+" cat /sys/class/thermal/thermal_zone0/temp";
		Runtime rt = Runtime.getRuntime();
		String output = "";
		
		try {
//			System.out.println("Dentro del try catch");
			Process p = rt.exec(command);
//			System.out.println("Ejecutamos el comando: "+ command);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
//			System.out.println("Despues del BufferedReader");
			String line;
			while ((line = reader.readLine()) != null) {
//				System.out.println("LEEMOS: "+line+"\n");
				output = output + line + "\n";
			}
			
			int exitVal;
			exitVal = p.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println(output);
			}
			//System.out.println("output: "+output+", length: "+ output.length());
			return output;
//			if (exitVal == 255){
//				/*throw new Exception("An error ocurred with SSH: Error code: "+exitVal+
//						" ,Command: "+command);*/
//				return
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}
	
	

	private static void sendAnswer(Socket s, String answer) throws IOException, ClassNotFoundException {
		PrintWriter salida = new PrintWriter(s.getOutputStream(), true);
        salida.println(answer);
	}	
}
