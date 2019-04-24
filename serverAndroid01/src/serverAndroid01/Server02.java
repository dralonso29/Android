package serverAndroid01;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server02 {
	//private static final long TIMEWAIT = 10000; // 10 seconds
	
	public static void main(String[] args) throws Exception{
//		String host = "10.0.0.8";
//		InetAddress ADDRESS = InetAddress.getByName(host);
//		//InetAddress ADDRESS = InetAddress.getLocalHost();
//		System.out.println(ADDRESS);
		final int PORT = 5000;
		ServerSocket serverS = new ServerSocket(PORT);
//		SocketAddress endpoint = new InetSocketAddress(ADDRESS, PORT);
//		serverS.bind(endpoint);
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
	
//	public static String createAnswer(String temperatura) {
//		String pattern = "yyyy/MM/dd HH:mm:ss";
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
//		LocalDateTime now = LocalDateTime.now();
//		if(temperatura.length() < 1) {
//			return dtf.format(now) + " None";
//		}
//		return dtf.format(now) + " " + temperatura;
//	}

	private static void processClientRequest(Socket s) throws Exception {
		DataInputStream dis = new DataInputStream(s.getInputStream());
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		Messages request = Messages.readFrom(dis);
		if(request != null) {
			request.writeTo(dos); // send reply to client
		}
	}
}
//		String request = clientRequest(s);
//		System.out.println("La consulta del usuario es: "+request+"&&&");
//		if (request != null) {
//			String temperature = getTemperature(request);
//			String answer = createAnswer(temperature);
//			sendAnswer(s, answer);
////			if(!answerWasReceived(s)) { //  if Client no receives temperature
////				sendAnswer(s, answer); // (Re)send answer
////			}
//		}
//	}

//	private static boolean answerWasReceived(Socket s) throws IOException, InterruptedException {
//		//Thread.sleep(TIMEWAIT);
//		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
//		return in.readLine() == "ok";
//	}
//
//	private static String clientRequest(Socket s) throws IOException {
//		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
//		return in.readLine();
//	}
//
//	private static String getTemperature() throws Exception {
//		String ruta = "/sys/class/thermal/thermal_zone0/temp";
//		try {
//            FileInputStream fs = new FileInputStream(ruta);
//            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
//            String line;
//            String[] parsed;
//            while ((line = br.readLine()) != null) {
//                Log.d("StoreUsers", "Leemos -->" + line + "@@@@");
//                parsed = parseLine(line);
//                Log.d("StoreUsers", "parsed[0]: "+parsed[0]);
//                Log.d("StoreUsers", "parsed[1]: "+parsed[1]+"@@@@");
//                putOnHashMap(parsed);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//	}
//	
//	
//
//	private static void sendAnswer(Socket s, String answer) throws IOException, ClassNotFoundException {
//		PrintWriter salida = new PrintWriter(s.getOutputStream(), true);
//        salida.println(answer);
//	}	
//}
