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

import org.w3c.dom.Text;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static alonsod.mov.urjc.xorapp.LevelFactory.MAXLEVELS;
import static alonsod.mov.urjc.xorapp.LevelFactory.MAXTOGGLES;

public class MainActivity extends AppCompatActivity {

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
        int MAXFAILURES;

        PrepareLevel() {
            lay = findViewById(R.id.linearToggle);
            NLEVEL = 0;
            MAXFAILURES = 2;
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
                toggle.setId(i);
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

        public int getNLEVEL(){
            return NLEVEL;
        }

        public String getUsrname(){
            return usrname;
        }

        public TextView getTextViewFails() {
            return findViewById(R.id.fails);
        }

        public void setFailsText() {
            TextView txt = getTextViewFails();
            txt.setText("Fallos permitidos: "+MAXFAILURES);
        }
    }

    public class TimeControler{
        Date initial;
        Date current;
        int[] times;
        private static final int INIT_TIME = 999999999; // 11 dias y medio en ms
        TimeControler(){
            times = new int[MAXLEVELS];
        }

        private void initTimes(){
            for (int i = 0; i<MAXLEVELS;i++){
                times[i] = INIT_TIME;
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
        Button next;

        NextButt(PrepareLevel prep, Level level, LevelFactory lf, ImageView imgv, TextView textv) {
            p = prep;
            mylevel = level;
            mylf = lf;
            this.imgv = imgv;
            this.textv = textv;
            next = findViewById(R.id.nextbut);
        }

        @Override
        public void onClick(View v) {
            int time = Toast.LENGTH_SHORT;


            Toast msg;
            String mymsg;
            if(mylevel.SalidaBuena(p.getButtonsStatus()) && !mylevel.SalidaMala(p.getButtonsStatus())){
                tc.setCurrent(Calendar.getInstance().getTime());
                Log.d("MainActivity: Date: currentTime", tc.current.toString());
                Log.d("MainActivity: Date: diff", "Diff Nivel "+p.NLEVEL+": "+tc.getDiffTime()+"");
                tc.times[p.NLEVEL] = (int) tc.getDiffTime();
                Log.d("MainActivity: Tiempo del nivel[i]", "Tiempo del nivel["+p.NLEVEL+"]: "+tc.times[p.NLEVEL]);
                writeFileScores();
                p.NLEVEL++;
                if (p.NLEVEL < MAXLEVELS) {
                    prep.MAXFAILURES = 2;
                    prep.setFailsText();
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
            prep.resetButtons();
            prep.MAXFAILURES--;
            prep.setFailsText();
            if (prep.MAXFAILURES == 0){
                mymsg = "HAS PERDIDO";
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
            prep.MAXFAILURES = savedInstanceState.getInt("fails");
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
        if (prep.MAXFAILURES == 0){
            nextbut.setVisibility(View.GONE);
        }
        prep.setFailsText();
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

    private void writeFileScores() {
        checkExternalStorage();
        if (mExternalStorageWriteable) {
            StoreUsers st_users = new StoreUsers(prep.usrname, tc.times);

            File route = getExternalFilesDir("XorApp");
            Log.d("MainActivity ", "LA RUTA: " + route);
            Log.d("MainActivity", "Storage Avaliable: " + mExternalStorageAvaliable);
            Log.d("MainActivity", "Storage Writeable: " + mExternalStorageWriteable);
            File scores = new File(route, "scores.txt");
            st_users.readFile(scores);

            if (st_users.hsmp.isEmpty()) { // if file empty,(and hash map too) we write directly
                Log.d("ActivityMain", "El hash map esta vacio");
                String str = st_users.makeString(tc.times);
                st_users.writeOn(str, scores, false);
                return;
            }
            Log.d("ActivityMain", "El hash map NO esta vacio: " + st_users.getValue());

            if (!st_users.userFound()) { //first time user plays but file not empty
                String str = st_users.makeString(tc.times);
                st_users.writeOn(str, scores, true);
                return;
            }
            st_users.modifyHashMap();
            Log.d("ActivityMain", "La key de pepe es (actualizada): " + st_users.getValue());
            st_users.writing(scores);
        }
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
        state.putInt("fails", prep.MAXFAILURES);
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
                    prep.MAXFAILURES = 2;
                    prep.setFailsText();
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
