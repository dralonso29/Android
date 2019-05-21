package alonsod.mov.urjc.thermos;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ThermosService extends IntentService {

    private static final int NOTIFICATION_ID = 7777;
    private static final int WAITING_TIME = 10*1000;
    private static final int TIMEOUT = 4000;
    private boolean isServiceAlive;

    public void getTemp(final String ip, final int PORT) {
        Log.d("ThermosService", "Dentro de getTemp");
        Thread c = new Thread(){
            @Override
            public void run(){
                try {
                    while(isServiceAlive) {
                        Log.d("ThermosService", "getTemp: proceso de pedir temperatura");
                        long endTime = System.currentTimeMillis() + WAITING_TIME;
                        Socket s = new Socket();
                        s.connect(new InetSocketAddress(ip, PORT), TIMEOUT);
                        DataInputStream dis = new DataInputStream(s.getInputStream());
                        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                        //Log.d("ThermosService","Hay que segui con el servicio? --> "+checkAlarm(dis, dos, ip));
                        if (!checkAlarm(dis, dos, ip)) {
                            dis.close();
                            dos.close();
                            break;
                        }
                        dis.close();
                        dos.close();
                        sleep(endTime - System.currentTimeMillis());
                        Log.d("ThermosService", "Esperando...");
                    }
                    Log.d("ThermosService", "getTemp: ya no pedimos temperatura");
                }catch (ConnectException e) {
                    System.out.println("Connection refused "+ e);
                    Log.d("ThermosService", "Connection refused...");
                }catch (UnknownHostException e) {
                    System.out.println("Cannot connect to host "+ e);
                    Log.d("ThermosService", "Unknown host...");
                }catch (IOException e) {
                    System.out.println("IOExcepton "+ e);
                    Log.d("ThermosService", "IOException...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("ThermosService", "InterruptedException...");
                }
            }

            private boolean checkAlarm(DataInputStream dis, DataOutputStream dos, String machine) {
                Log.d("ThermosService", "Dentro de checkAlarm");
                boolean runService = true;
                Messages.RequestClient rc = new Messages.RequestClient();
                rc.writeTo(dos);
                Messages replyFromServer = Messages.readFrom(dis);
                int alarm = getAlarm();
                //String machine = "MaquinaPrueba";
                if (replyFromServer != null) {
                    String answer = null;
                    try {
                        answer = dis.readUTF();
                        Log.d("ThermosService", "Respuesta: " + answer);
                        int temp = Integer.parseInt(answer)/1000;
                        if (temp >= alarm) {
                            showNotification(machine, temp, alarm);
                            runService = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return runService;
            }
        };
        c.start();
        try {
            c.join(); //we must wait thread because of reading from file later
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        synchronized (this){
            isServiceAlive = false;
        }
        Log.d("ThermosService", "onDestroy: isServiceAlive: "+isServiceAlive);
    }

    public ThermosService() {
        super("HelloThermosService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String ip = getMachine();
        int port  = getPort();//25029;
        isServiceAlive = true;
        Log.d("ThermosService", "Hacemos getMachine: "+getMachine());
        Log.d("ThermosService", "Hacemos getPort: "+getPort());
        Log.d("ThermosService", "onHandleIntent: isServiceAlive: "+isServiceAlive);
        if ((ip != null) && !ip.equals("null")) {
            synchronized (this) {
                try {
                    getTemp(ip, port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        stopSelf();
    }

    private void showNotification(String machine, int temperatura, int alarm) {
        String textTitle = "¡Atención, hay una máquina calentándose!";
        String textContent = "Maquina: "+machine+" con temperatura de "+temperatura+" grados, ha superado la alarma de "+alarm+" grados";
        Intent intent = new Intent(this, GraphActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManager nmng = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .build();

        nmng.notify(NOTIFICATION_ID, notification);
    }

    private int getAlarm() {
        String FILENAME = "save_Alarm";
        int alarm = 0;
        try {
            FileInputStream in = openFileInput(FILENAME);
            alarm = in.read();
            Log.d("ThermosService", "getAlarm: alarma = "+alarm);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return alarm;
    }

    private int getPort() {
        String FILENAME = "save_Port";
        int port = 0;
        String sPort = null;
        try {
            FileInputStream in = openFileInput(FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            sPort = bufferedReader.readLine();
            port = Integer.parseInt(sPort);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }

    private String getMachine() {
        String FILENAME = "save_Machine";
        String machine = null;
        try {
            FileInputStream in = openFileInput(FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            machine = bufferedReader.readLine();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return machine;
    }
}
