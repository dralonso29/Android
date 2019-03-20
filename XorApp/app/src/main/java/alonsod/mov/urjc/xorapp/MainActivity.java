package alonsod.mov.urjc.xorapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

        PrepareLevel() {
            lay = findViewById(R.id.linearToggle);
            NLEVEL = 0;
            TOGGNAME = 'A';
            arraytog = new ToggleButton[MAXTOGGLES];
            entradas = new boolean[MAXTOGGLES];
            imagesid = new int[MAXLEVELS];
            menuids = new int[MAXLEVELS];
        }

        private ToggleButton[] createButtons(int ntogg) {
            for (int i=0;i<ntogg;i++) {
                ToggleButton toggle = new ToggleButton(MainActivity.this);
                toggle.setTextOff(Character.toString((char) (TOGGNAME+i)) + " = 0");
                toggle.setTextOn(Character.toString((char) (TOGGNAME+i)) + " = 1");
                toggle.setChecked(false);
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
                int id = getResources().getIdentifier(img_level, "drawable", getPackageName());
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
                int id = getResources().getIdentifier(menu_level, "id", getPackageName());
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
    }
    PrepareLevel prep;
    LevelFactory lf;

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
                p.NLEVEL++;
                if (p.NLEVEL < MAXLEVELS) {
                    mymsg = "Enhorabuena, " + mylevel.getLevelName() + " completado!";
                    msg = Toast.makeText(MainActivity.this, mymsg, time);
                    msg.show();
                    p.resetButtons();
                    mylevel = mylf.produce(p.NLEVEL); //update level
                    mylevel.loadLevel();
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
        prep = new PrepareLevel();
        prep.createButtons(MAXTOGGLES);
        prep.setIdsMenu();
        ImageView imgv = prep.getImgViewLevel();
        TextView textv = prep.getTextViewHeader();
        lf = new LevelFactory(prep.arraytog,
                prep.getImagesIds(), imgv, textv);

        if (savedInstanceState != null){
            prep.NLEVEL = savedInstanceState.getInt("nlevel");
            prep.entradas = savedInstanceState.getBooleanArray("stateButtons");
            prep.setStatusButtons(prep.entradas);
        }

        Level level = lf.produce(prep.NLEVEL);
        level.loadLevel();

        Button nextbut = (Button) findViewById(R.id.nextbut);
        nextbut.setOnClickListener(new NextButt(prep, level, lf, imgv, textv));
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBooleanArray("stateButtons", prep.getButtonsStatus());
        state.putInt("nlevel", prep.NLEVEL);
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
                    next.setOnClickListener(new NextButt(prep, level, lf, prep.getImgViewLevel(), prep.getTextViewHeader()));
                    next.setVisibility(View.VISIBLE);
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }
}
