package alonsod.mov.urjc.xorapp;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import static alonsod.mov.urjc.xorapp.LevelFactory.MAXTOGGLES;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Level0 extends Level {
    final static int level = 0;
    final static int visiblebuttons = 4;

    public Level0(int id, ImageView img, ToggleButton[] togg, TextView textheader) {
        super(id, img, level, visiblebuttons, togg, textheader);
    }

    public boolean SalidaBuena(boolean entradas[]) {
        Log.d("SalidaBuena","nivel0");
        return !(entradas[0] && entradas[1]);
    }
    public boolean SalidaMala(boolean entradas[]) {
        return !(entradas[2] || entradas[3]);
    }
}
