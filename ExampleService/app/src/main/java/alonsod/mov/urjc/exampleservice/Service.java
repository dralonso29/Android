package alonsod.mov.urjc.exampleservice;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Service extends IntentService {

    private static final int NOTIFICATION_ID = 7777;

    public Service() {
        super("HelloIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long endTime = System.currentTimeMillis() + 5*1000;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    String machine = getMachine();
                    int alarm = getAlarm();
                    showNotification(machine, alarm);
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        }
    }

    private int getAlarm() {
        String FILENAME = "save_Alarm";
        int alarm = 0;
        try {
            FileInputStream in = openFileInput(FILENAME);
            alarm = in.read();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return alarm;
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

    private void showNotification(String machine, int alarm) {
        String textTitle = "Atención que se prende la wea";
        String textContent = "Maquina: "+machine+", con alarma: "+alarm;

        NotificationManager nmng =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        nmng.notify(NOTIFICATION_ID, notification);
    }

}
