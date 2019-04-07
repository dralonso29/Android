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
        String aux_user = "None";
        int[] aux_arr = new int[MAXLEVELS];

        initArrayInt(aux_arr);
        ids = setIdsScores();
        if (mExternalStorageAvaliable) {
            StoreUsers st_users = new StoreUsers(aux_user, aux_arr); //aux_user and aux_arr will not be used
            File route = getExternalFilesDir("XorApp");
            File scores = new File(route, "scores.txt");
            st_users.readFile(scores);

            for (int i = 0; i < MAXLEVELS; i++){
                str = st_users.setBest(i);
                Log.d("ScoresActivity", "Nivel "+i+": "+ str);
                putTextBest(ids[i], str);
            }
        }
    }

    private void initArrayInt(int[] aux) {
        for (int i = 0; i < aux.length; i++){
            aux[i] = 0;
        }
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
