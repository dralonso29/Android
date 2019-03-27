package alonsod.mov.urjc.xorapp;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Level1 extends Level {
    final static int level = 1;
    final static int visiblebuttons = 4;

    public Level1(int id, ImageView img, ToggleButton[] togg, TextView textheader) {
        super(id, img, level, visiblebuttons, togg, textheader);
    }

    public boolean SalidaBuena(boolean entradas[]) {
        Log.d("SalidaBuena","nivel1");
        return (entradas[0] && entradas[1]) || (entradas[2] || entradas[3]);
    }
    public boolean SalidaMala(boolean entradas[]) {
        return !((entradas[0] && entradas[1]) && (entradas[2] || entradas[3]));
    }
}
