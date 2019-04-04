package alonsod.mov.urjc.xorapp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static alonsod.mov.urjc.xorapp.LevelFactory.MAXLEVELS;
import static alonsod.mov.urjc.xorapp.LevelFactory.MAXTOGGLES;

public class MainActivity extends AppCompatActivity {

    private static final int MAXLINES = 1024;

    public class PrepareLevel {
        int NLEVEL;
        LinearLayout lay;
        ToggleButton arraytog[];
        char TOGGNAME;
        boolean[] entradas;
        int[] imagesid;
        int[] menuids;
        boolean[] passed;
        Resources rso;
        String usrname;

        PrepareLevel() {
            lay = findViewById(R.id.linearToggle);
            NLEVEL = 0;
            TOGGNAME = 'A';
            arraytog = new ToggleButton[MAXTOGGLES];
            entradas = new boolean[MAXTOGGLES];
            imagesid = new int[MAXLEVELS];
            menuids = new int[MAXLEVELS];
            passed = new boolean[MAXLEVELS];
            rso = getResources();
        }

        private ToggleButton[] createButtons(int ntogg) {
            for (int i=0;i<ntogg;i++) {
                ToggleButton toggle = new ToggleButton(MainActivity.this);
                toggle.setTextOff(Character.toString((char) (TOGGNAME+i)) + " = 0");
                toggle.setTextOn(Character.toString((char) (TOGGNAME+i)) + " = 1");
                toggle.setChecked(false);
                toggle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                lay.addView(toggle);
                arraytog[i] = toggle;
            }
            return arraytog;
        }

        private boolean[] getButtonsStatus() {
            for (int i=0; i<MAXTOGGLES;i++){
                entradas[i] = arraytog[i].isChecked();
            }
            return entradas;
        }

        private int[] getImagesIds() {
            for (int i=0;i<MAXLEVELS;i++) {
                String img_level = "ic_level" + i;
                int id = rso.getIdentifier(img_level, "drawable", getPackageName());
                imagesid[i] = id;
            }
            return imagesid;
        }
        private void resetButtons() {
            for (int i = 0; i < MAXTOGGLES; i++) {
                arraytog[i].setChecked(false);
            }
        }
        private void setStatusButtons(boolean[] state) {

            for (int i = 0; i < MAXTOGGLES; i++){
                arraytog[i].setChecked(state[i]);
            }
        }
        private int[] setIdsMenu() {

            for (int i=0;i<MAXLEVELS;i++) {
                String menu_level = "menu_level" + i;
                int id = rso.getIdentifier(menu_level, "id", getPackageName());
                menuids[i] = id;
            }
            return menuids;
        }
        private boolean setLevelMenu(int menuid) {
            for (int i = 0; i < MAXLEVELS; i++) {
                if (menuid == menuids[i]) {
                    prep.NLEVEL = i;
                    return true;
                }
            }
            return false;
        }
        private ImageView getImgViewLevel(){
            return findViewById(R.id.img_level);
        }

        public TextView getTextViewHeader() {
            return findViewById(R.id.level_header);
        }

        private void iniatlizeMenuVisibility(){
            for (int i = 0; i < MAXLEVELS; i++){
                passed[i] = false;
            }
        }
    }

    public class TimeControler{
        Date initial;
        Date current;
        int[] times;
        TimeControler(){
            times = new int[MAXLEVELS];
        }

        private void initTimes(){
            for (int i = 0; i<MAXLEVELS;i++){
                times[i] = 0;
            }
        }
        private void setInitial(Date d) {
            initial = d;
        }
        private void setCurrent(Date d) {
            current = d;
        }
        private Date getInitial() {
            return initial;
        }
        private Date getCurrent() {
            return current;
        }

        public long getDiffTime() {
            return current.getTime() - initial.getTime();
        }
    }
    PrepareLevel prep;
    LevelFactory lf;
    TimeControler tc;
    boolean mExternalStorageAvaliable = false;
    boolean mExternalStorageWriteable = false;

    class NextButt implements View.OnClickListener {
        PrepareLevel p;
        Level mylevel;
        LevelFactory mylf;
        ImageView imgv;
        TextView textv;

        NextButt(PrepareLevel prep, Level level, LevelFactory lf, ImageView imgv, TextView textv) {
            p = prep;
            mylevel = level;
            mylf = lf;
            this.imgv = imgv;
            this.textv = textv;
        }

        @Override
        public void onClick(View v) {
            int time = Toast.LENGTH_SHORT;

            Toast msg;
            Button next = findViewById(R.id.nextbut);
            String mymsg;
            if(mylevel.SalidaBuena(p.getButtonsStatus()) && !mylevel.SalidaMala(p.getButtonsStatus())){
                tc.setCurrent(Calendar.getInstance().getTime());
                Log.d("MainActivity: Date: currentTime", tc.current.toString());
                Log.d("MainActivity: Date: diff", tc.getDiffTime()+"");
                tc.times[p.NLEVEL] = (int) tc.getDiffTime();
                writeFileScores();
                p.NLEVEL++;
                if (p.NLEVEL < MAXLEVELS) {
                    p.passed[p.NLEVEL] = true;
                    mymsg = "Enhorabuena, " + mylevel.getLevelName() + " completado!";
                    msg = Toast.makeText(MainActivity.this, mymsg, time);
                    msg.show();
                    p.resetButtons();
                    mylevel = mylf.produce(p.NLEVEL); //update level
                    mylevel.loadLevel();
                    tc.setInitial(tc.current);
                    Log.d("MainActivity: Date: initialTime (Actualizado)", tc.initial.toString());
                    return;
                }
                mymsg = "HAS COMPLETADO TODOS LOS NIVELES!";
                msg = Toast.makeText(MainActivity.this, mymsg, time);
                msg.show();
                next.setVisibility(View.GONE);
                return;
            }
            mymsg = "Lo siento, el resultado no es correcto";
            msg = Toast.makeText(MainActivity.this, mymsg, time);
            msg.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tc = new TimeControler();
        prep = new PrepareLevel();
        prep.createButtons(MAXTOGGLES);
        prep.setIdsMenu();
        prep.iniatlizeMenuVisibility();
        tc.initTimes();
        ImageView imgv = prep.getImgViewLevel();
        TextView textv = prep.getTextViewHeader();
        lf = new LevelFactory(prep.arraytog,
                prep.getImagesIds(), imgv, textv);

        if (savedInstanceState != null){
            prep.NLEVEL = savedInstanceState.getInt("nlevel");
            prep.entradas = savedInstanceState.getBooleanArray("stateButtons");
            prep.passed = savedInstanceState.getBooleanArray("passed");
            prep.setStatusButtons(prep.entradas);
        }

        prep.usrname = getUsrName();
        Log.d("ActivityMain", prep.usrname);
        writeFileScores();

        Level level = lf.produce(prep.NLEVEL);
        level.loadLevel();
        tc.setInitial(Calendar.getInstance().getTime());
        Log.d("MainActivity: Date: initialTime", tc.initial.toString());
        Button nextbut = findViewById(R.id.nextbut);
        nextbut.setOnClickListener(new NextButt(prep, level, lf, imgv, textv));
    }

    private String getUsrName() {
        Intent intent = getIntent();
        Bundle info = intent.getExtras();
        if (info != null) {
            return info.getString("username");
        }
        return "None";
    }

    private void writing(String str, File scores, boolean append){
        Log.d("ActivityMain", "append :"+append);
        try {
            BufferedOutputStream output = new
                    BufferedOutputStream(new FileOutputStream(scores, append));
            DataOutputStream data = new DataOutputStream(output);
            data.writeBytes(str);
            data.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeFileScores() {
        checkExternalStorage();
        if(mExternalStorageWriteable) {
            File route = getExternalFilesDir("XorApp");
            Log.d("MainActivity ", "LA RUTA: "+route);
            Log.d("MainActivity","Storage Avaliable: "+mExternalStorageAvaliable);
            Log.d("MainActivity","Storage Writeable: "+mExternalStorageWriteable);
            File scores = new File(route, "scores.txt");
            List<String> arraylines = readFile(scores);
            boolean append = false;
            String[] lines = new String[arraylines.size()];
            arraylines.toArray(lines);
            String[] line;
            String usr;
            String t0, t1, t2;
            boolean found = false;

            if (arraylines.isEmpty()) { // si esta vacio el fichero, escribimos directamente
                String str = make_string();
                writing(str, scores, append);
                return;
            }

            for (int j = 0; j < lines.length; j++){
                Log.d("ActivityMain", "lines["+j+"] = "+ lines[j]);
                line = split_by(lines[j], "\n");
                if (line[0].length() < 1){
                    continue;
                }
                usr = split_by(line[0], " ")[0];
                t0 = split_by(line[0], " ")[1];
                t1 = split_by(line[0], " ")[2];
                t2 = split_by(line[0], " ")[3];
                Log.d("ActivityMain", "Nombre usr linea "+j+" fichero: "+ usr);
                if (usr.equals(prep.usrname)){
                    found = true;
                    Log.d("ActivityMain", "line["+j+"] == username--> "+ usr + " "+ prep.usrname);
                    lines[j] = create_String(Integer.parseInt(t0), Integer.parseInt(t1), Integer.parseInt(t2));
                    Log.d("ActivityMain", "El nuevo String es: "+lines[j]);

                }
            }
            if (!found){
                String str = make_string();
                writing(str, scores, true);
                return;
            }
            /*for (int i = 0; i< lines.length; i++){
                Log.d("ActivityMain", "lines["+i+"] = "+lines[i]+"@@");
            }*/
            boolean first = true;
            for (int i = 0; i< lines.length; i++){
                if (first){
                    writing(lines[i], scores, false);
                    first = false;
                    continue;
                }
                writing(lines[i], scores, true);
            }
        }
    }

    private String create_String(int t0, int t1, int t2) {
        if (t0 >= tc.times[0] || t0 == 0){
            t0 = tc.times[0];
        }
        if (t1 >= tc.times[1]|| t1 == 0){
            t1 = tc.times[1];
        }
        if (t2 >= tc.times[2]|| t2 == 0){
            t2 = tc.times[2];
        }
        return prep.usrname + " " + t0 + " " + t1 + " " + t2 +"\n";
    }

    private String[] split_by(String line, String s) {
        return line.split(s);
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

    private String make_string() {
        String str = prep.usrname;
        for (int i = 0; i < MAXLEVELS;i++) {
            str = str + " " + tc.times[i];
        }
        return str+"\n";
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

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBooleanArray("stateButtons", prep.getButtonsStatus());
        state.putInt("nlevel", prep.NLEVEL);
        state.putBooleanArray("passed", prep.passed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.conf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int time = Toast.LENGTH_SHORT;
        Toast msg;
        Level level;
        Button next = findViewById(R.id.nextbut);
        int myItem = item.getItemId();
        switch (myItem) {
            case R.id.help:
                Intent help = new Intent(MainActivity.this, Help.class);
                startActivity(help);
                return true;
            default:
                if (prep.setLevelMenu(myItem)){ //prep.NLEVEL updated on setLevelMenu
                    msg = Toast.makeText(MainActivity.this, "Estas en el nivel " + prep.NLEVEL, time);
                    msg.show();
                    prep.resetButtons();
                    level = lf.produce(prep.NLEVEL);
                    level.loadLevel();
                    tc.setInitial(Calendar.getInstance().getTime());
                    Log.d("MainActivity: Date: initialTime (Menu)", tc.initial.toString());
                    next.setOnClickListener(new NextButt(prep, level, lf, prep.getImgViewLevel(), prep.getTextViewHeader()));
                    next.setVisibility(View.VISIBLE);
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        for (int i = 1; i<MAXLEVELS;i++){
            if (prep.passed[i]){ //if we have passed level, can show level on menu
                menu.findItem(prep.menuids[i]).setVisible(true);
            }
        }
        return true;
    }

}
