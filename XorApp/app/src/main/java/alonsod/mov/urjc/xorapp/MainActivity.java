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

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity {

    LinearLayout lay;
    int NTOGGLES = 4;
    int NLEVEL;
    ToggleButton arraytog[];
    public void createButtons(LinearLayout lay, int i, char entry, ToggleButton array[]) {
        ToggleButton toggle = new ToggleButton(this);
        toggle.setTextOff(Character.toString(entry));
        toggle.setTextOn(Character.toString(entry));
        toggle.setChecked(false);
        toggle.setId(i);
        toggle.setText(Character.toString(entry));
        lay.addView(toggle);
        array[i] = toggle;
    }

    public boolean SalidaBuena(boolean A, boolean B) {
        return !(A && B);
    }

    public boolean SalidaMala(boolean C, boolean D) {
        return !(C || D);
    }

    public void info(View view) {
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

    class NextButt implements View.OnClickListener {
        ToggleButton arraytog[];
        NextButt(ToggleButton arr[]) {
            arraytog = arr;
        }

        @Override
        public void onClick(View v) {
            int time = Toast.LENGTH_SHORT;
            boolean A = arraytog[0].isChecked();
            boolean B = arraytog[1].isChecked();
            boolean C = arraytog[2].isChecked();
            boolean D = arraytog[3].isChecked();

            Toast msg;
            if(SalidaBuena(A , B) && !SalidaMala(C, D)){
                msg = Toast.makeText(MainActivity.this, "Enhorabuena, pasas al siguiente nivel!", time);
                resetButtons(arraytog);
                NLEVEL++;
                setLevel(NLEVEL);
            }else{
                msg = Toast.makeText(MainActivity.this, "Lo siento, el resultado no es correcto", time);
            }
            msg.show();
        }
    }

    public void setLevel(int nlevel) {
        ImageView imgv = (ImageView) findViewById(R.id.img_level);
        String img_level = "ic_level" + nlevel;
        int id = getResources().getIdentifier(img_level, "drawable", getPackageName());
        imgv.setImageResource(id);

        TextView header = (TextView) findViewById(R.id.level_header);
        header.setText("Level " + nlevel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        char s;
        NLEVEL = 0;
        arraytog = new ToggleButton[NTOGGLES];
        lay = findViewById(R.id.linearToggle);
        for (int i=0;i<NTOGGLES;i++) {
            s = (char) ('A' + i);
            createButtons(lay, i, s, arraytog);
        }
        setLevel(NLEVEL);
        Button nextbut = (Button) findViewById(R.id.nextbut);
        nextbut.setOnClickListener(new NextButt(arraytog));
    }
    @Override
    protected void onPause() {
        super.onPause();
        exit(0);
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
        switch (item.getItemId()) {
            case R.id.menu_level0:
                msg = Toast.makeText(MainActivity.this, "Estas en el nivel 0", time);
                msg.show();
                setLevel(0);
                return true;
            case R.id.menu_level1:
                msg = Toast.makeText(MainActivity.this, "Estas en el nivel 1", time);
                msg.show();
                setLevel(1);
                return true;
            case R.id.help:
                View helpbutton = findViewById(R.id.help);
                info(helpbutton);
                return true;
            case R.id.exit:
                onPause();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}