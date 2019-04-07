package alonsod.mov.urjc.xorapp;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static alonsod.mov.urjc.xorapp.LevelFactory.MAXLEVELS;

public class ScoresActivity extends AppCompatActivity {

    private boolean mExternalStorageAvaliable;
    private boolean mExternalStorageWriteable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        setImageScores();
        setTextScores();
    }

    private void setTextScores() {
        checkExternalStorage();
        Log.d("ActivityScores", mExternalStorageAvaliable+"");
        String str;
        int[] ids;

        ids = setIdsScores();
        if (mExternalStorageAvaliable) {
            File route = getExternalFilesDir("XorApp");
            File scores = new File(route, "scores.txt");
            List<String> arraylines = readFile(scores);
            String[] lines = new String[arraylines.size()];
            arraylines.toArray(lines);
            for (int i = 0; i < MAXLEVELS; i++){
                str = setBest(lines, i);
                putTextBest(ids[i], str);
            }
        }
    }

    private String setBest(String[] lines, int n) {
        String user;
        String time;
        String best = "";
        int time_best = 99999999;

        for (int i = 0; i < lines.length;i++){
            Log.d("ActivityScores", "line:"+lines[i]);
            user = lines[i].split(" ")[0];
            Log.d("ActivityScores", "user:"+user);
            time = lines[i].split(" ")[n+1];
            Log.d("ActivityScores", "time:"+time);
            if (Integer.parseInt(time.trim()) <= time_best && Integer.parseInt(time.trim()) > 0){
                time_best = Integer.parseInt(time.trim());
                best = user;
            }
        }
        Log.d("ActivityScores", "Best usr:"+best);
        Log.d("ActivityScores", "Best time:"+time_best);
        if (time_best == 99999999){
            return "Aún no hay registros";
        }
        return "Usuario: "+best+" Tiempo empleado: " + beautifyTime(time_best);
    }

    private String beautifyTime(int time_best) {
        time_best = time_best / 1000; //now time_best is on seconds
        long seconds = time_best % 60;
        long minutes = (time_best / 60) % 60;
        long hours = (time_best / (60*60)) % 24;
        return String.format("%d horas %02d minutos %02d segundos ", hours, minutes, seconds);
    }

    private int[] setIdsScores() {
        Resources rso = getResources();
        int[] ids = new int[MAXLEVELS];
        for (int i = 0; i<MAXLEVELS; i++) {
            String menu_level = "scores_best" + i;
            int id = rso.getIdentifier(menu_level, "id", getPackageName());
            ids[i] = id;
        }
        return ids;
    }

    private void putTextBest(int id, String str) {
        TextView txt = findViewById(id);
        txt.setText(str);
    }

    private void setImageScores() {
        String img_level = "ic_ts";
        int id = getResources().getIdentifier(img_level, "drawable", getPackageName());
        ImageView imgv = findViewById(R.id.tc);
        imgv.setImageResource(id);
    }

    private List<String> readFile(File scores) {
        List<String> arraylines = new ArrayList<>();
        try {
            FileInputStream fs = new FileInputStream(scores);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            String line;
            while((line = br.readLine()) != null){
                Log.d("ActivityMain", "Leemos -->"+line);
                arraylines.add(line+"\n"); // este salto de linea es de vital importancia
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arraylines;
    }

    public void checkExternalStorage() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvaliable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvaliable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvaliable = mExternalStorageWriteable = false;
        }
    }
}
