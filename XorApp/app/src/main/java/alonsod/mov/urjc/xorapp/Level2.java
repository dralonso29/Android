package alonsod.mov.urjc.xorapp;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import static alonsod.mov.urjc.xorapp.LevelFactory.MAXTOGGLES;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Level2 extends Level {
    final static int level = 2;
    final static int visiblebuttons = 3;

    public Level2(int id, ImageView img, ToggleButton[] togg, TextView textheader) {
        super(id, img, level, visiblebuttons, togg, textheader);
    }

    public boolean SalidaBuena(boolean entradas[]) {
        Log.d("SalidaBuena","Salida buena");
        Log.d("SalidaBuena","A --> " + entradas[0]);
        Log.d("SalidaBuena","B --> " + entradas[1]);
        Log.d("SalidaBuena","C --> " + entradas[2]);
        Log.d("SalidaBuena", ""+(((!entradas[0]) && entradas[1]) && ((!entradas[2]) || entradas[1]))+"\n");
        return ((!entradas[0]) && entradas[1]) && ((!entradas[2]) || entradas[1]);
    }
    public boolean SalidaMala(boolean entradas[]) {
        Log.d("SalidaMala","Salida mala");
        Log.d("SalidaMala","A --> " + entradas[0]);
        Log.d("SalidaMala","B --> " + entradas[1]);
        Log.d("SalidaMala","C --> " + entradas[2]);
        Log.d("SalidaMala", ""+!((!entradas[2]) || entradas[1])+"\n");
        return !((!entradas[2]) || entradas[1]);
    }
}
