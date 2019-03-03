package alonsod.mov.urjc.xorapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

    //LinearLayout lay;
    //int NTOGGLES = 4;
    //int NLEVEL;
    //int MAXLEVELS = 2;
    //ToggleButton arraytog[];
    //Level arraylevels[];


    public class PrepareLevel {
        int NLEVEL;
        LinearLayout lay;
        ToggleButton arraytog[];
        Level arraylevels[];
        char TOGGNAME;
        Boolean[] entradas;
        int[] imagesid;

        PrepareLevel() {
            lay = findViewById(R.id.linearToggle);
            NLEVEL = 0;
            TOGGNAME = 'A';
            arraytog = new ToggleButton[MAXTOGGLES];
            entradas = new Boolean[MAXTOGGLES];
            imagesid = new int[MAXLEVELS];
        }

        public ToggleButton[] createButtons(int ntogg) {
            for (int i=0;i<ntogg;i++) {
                ToggleButton toggle = new ToggleButton(MainActivity.this);
                toggle.setTextOff(Character.toString((char) (TOGGNAME+i)) + " = 0");
                toggle.setTextOn(Character.toString((char) (TOGGNAME+i)) + " = 1");
                toggle.setChecked(false);
                //toggle.setId(i);
                lay.addView(toggle);
                arraytog[i] = toggle;
            }
            return arraytog;
        }

        public Boolean[] getButtonsStatus() {
            for (int i=0; i<MAXTOGGLES;i++){
                entradas[i] = arraytog[i].isChecked();
            }
            return entradas;
        }

        public int[] getImagesIds() {
            for (int i=0;i<MAXLEVELS;i++) {
                String img_level = "ic_level" + i;
                int id = getResources().getIdentifier(img_level, "drawable", getPackageName());
                imagesid[i] = id;
            }
            return imagesid;
        }
        public void resetButtons() {
            for (int i = 0; i < MAXTOGGLES; i++) {
                arraytog[i].setChecked(false);
            }
        }
    }
    PrepareLevel prep;
    LevelFactory lf;

    /*public void info(View view) {
        TextView infotxt = (TextView) findViewById(R.id.infolevel0);
        if (infotxt.getVisibility() == View.VISIBLE) {
            infotxt.setVisibility(View.GONE);
        }else {
            infotxt.setVisibility(View.VISIBLE);
        }
    }

    public void resetButtons(ToggleButton but[]) {
        for (int i = 0; i < NTOGGLES; i++) {
            but[i].setChecked(false);
        }
    }
    */
    class NextButt implements View.OnClickListener {
        PrepareLevel p;
        Level mylevel;
        LevelFactory mylf;

        NextButt(PrepareLevel prep, Level level, LevelFactory lf) {
            p = prep;
            mylevel = level;
            mylf = lf;
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
                    return;
                }else{
                    mymsg = "HAS COMPLETADO TODOS LOS NIVELES!";
                    msg = Toast.makeText(MainActivity.this, mymsg, time);
                    msg.show();
                    next.setVisibility(View.GONE);
                    return;
                }
            }else{
                mymsg = "Lo siento, el resultado no es correcto";
                msg = Toast.makeText(MainActivity.this, mymsg, time);
                msg.show();
            }

        }
    }
    /*public void setLevel(int nlevel) {
        ImageView imgv = (ImageView) findViewById(R.id.img_level);
        String img_level = "ic_level" + nlevel;
        int id = getResources().getIdentifier(img_level, "drawable", getPackageName());
        imgv.setImageResource(id);

        TextView header = (TextView) findViewById(R.id.level_header);
        header.setText("Nivel " + nlevel);
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prep = new PrepareLevel();
        prep.createButtons(MAXTOGGLES);
        ImageView imgv = (ImageView) findViewById(R.id.img_level);
        TextView textv = findViewById(R.id.level_header);
        lf = new LevelFactory(prep.arraytog,
                prep.getImagesIds(), imgv, textv,MainActivity.this);

        /*char s;
        NLEVEL = 0;
        arraytog = new ToggleButton[NTOGGLES];*/
        /*arraylevels = new Level[MAXLEVELS];
        lay = findViewById(R.id.linearToggle);
        for (int i=0;i<NTOGGLES;i++) {
            s = (char) ('A' + i);
            createButtons(lay, i, s, arraytog);
        }
        createLevels(MAXLEVELS, arraylevels);
        setLevel(NLEVEL);*/
        Level level = lf.produce(prep.NLEVEL);

        Button nextbut = (Button) findViewById(R.id.nextbut);
        nextbut.setOnClickListener(new NextButt(prep, level, lf));
    }

    /*private void createLevels(int maxlevels, Level arraylevels[]) {
        for(int i = 0;i < maxlevels; i++){
            arraylevels[i] = LevelFactory.produce(i); {
            }
        }
    }*/

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

        switch (item.getItemId()) {
            case R.id.menu_level0:
                msg = Toast.makeText(MainActivity.this, "Estas en el nivel 0", time);
                msg.show();
                prep.NLEVEL = 0;
                prep.resetButtons();
                level = lf.produce(prep.NLEVEL);
                next.setOnClickListener(new NextButt(prep, level, lf));
                next.setVisibility(View.VISIBLE);
                return true;
            case R.id.menu_level1:
                msg = Toast.makeText(MainActivity.this, "Estas en el nivel 1", time);
                msg.show();
                prep.NLEVEL = 1;
                prep.resetButtons();
                level = lf.produce(prep.NLEVEL);
                next.setOnClickListener(new NextButt(prep, level, lf));
                next.setVisibility(View.VISIBLE);
                return true;
            case R.id.menu_level2:
                msg = Toast.makeText(MainActivity.this, "Estas en el nivel 2", time);
                msg.show();
                prep.NLEVEL = 2;
                prep.resetButtons();
                level = lf.produce(prep.NLEVEL);
                next.setOnClickListener(new NextButt(prep, level, lf));
                next.setVisibility(View.VISIBLE);
                return true;
            /*case R.id.help:
                View helpbutton = findViewById(R.id.help);
                info(helpbutton);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}