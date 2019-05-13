package alonsod.mov.urjc.exampleservice;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String machine = "delta01";
        saveMachineName(machine);
        int alarm = 50;
        saveAlarm(alarm);
        startService(new Intent(this, Service.class));
    }

    private void saveAlarm(int alarm) {
        String FILENAME = "save_Alarm";

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(alarm);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMachineName(String machine) {
        String FILENAME = "save_Machine";

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(machine.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
