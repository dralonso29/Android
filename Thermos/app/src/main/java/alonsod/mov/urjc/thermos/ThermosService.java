package alonsod.mov.urjc.thermos;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ThermosService extends IntentService {

    private static final int NOTIFICATION_ID = 7777;
    private static final int WAITING_TIME = 5*1000;

    public void getTemp(final String ip, final int PORT) {
        Log.d("ThermosService", "Dentro de getTemp");
        Thread c = new Thread(){
            @Override
            public void run(){
                try {
                    long endTime = System.currentTimeMillis() + WAITING_TIME;
                    Socket s = new Socket(ip, PORT);
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    Log.d("ThermosService","Hay que segui con el servicio? --> "+checkAlarm(dis, dos));
                    /*while(checkAlarm(dis, dos)) {
                        wait(endTime - System.currentTimeMillis());
                    }*/
                }catch (ConnectException e) {
                    System.out.println("Connection refused "+ e);
                }catch (UnknownHostException e) {
                    System.out.println("Cannot connect to host "+ e);
                }catch (IOException e) {
                    System.out.println("IOExcepton "+ e);
                } /*catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }

            private boolean checkAlarm(DataInputStream dis, DataOutputStream dos) {
                Log.d("ThermosService", "Dentro de checkAlarm");
                boolean runService = true;
                Messages.RequestClient rc = new Messages.RequestClient();
                rc.writeTo(dos);
                Messages replyFromServer = Messages.readFrom(dis);
                int alarm = 30;
                String machine = "MaquinaPrueba";
                if (replyFromServer != null) {
                    String answer = null;
                    try {
                        answer = dis.readUTF();
                        Log.d("ThermosService", "Respuesta: " + answer);
                        if (Integer.parseInt(answer) >= alarm) {
                            showNotification(machine, alarm);
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

    public ThermosService() {
        super("HelloThermosService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String ip = "10.0.0.8";
        int port  = 25029;
        synchronized (this) {
            try {
                getTemp(ip, port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showNotification(String machine, int alarm) {
        String textTitle = "Atención que se prende la wea";
        String textContent = "Maquina: "+machine+", con alarma: "+alarm;

        NotificationManager nmng =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        nmng.notify(NOTIFICATION_ID, notification);
    }
}
