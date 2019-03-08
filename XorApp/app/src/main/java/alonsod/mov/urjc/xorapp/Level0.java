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
    int level;
    int visiblebuttons;
    public Level0(int nlevel) {
        level = nlevel;
        visiblebuttons = 4;
    }

    public String getLevelName() { return "nivel " + level;}

    public void setImage(int id, ImageView img) {
        img.setImageResource(id);
    }
    public void setTitle(TextView textheader) {
        textheader.setText("Nivel " + level);
    }
    public void setButtons(ToggleButton toggle[]) {
        if (visiblebuttons >= MAXTOGGLES) {
            //devolver error
            return;
        }else{
            for (int i = visiblebuttons; i < MAXTOGGLES;i++) {
                toggle[i].setVisibility(GONE);
            }
            return;
        }
    }
    public void setVisibleAll(ToggleButton toggle[]){
        for (int i=0;i<MAXTOGGLES;i++){
            toggle[i].setVisibility(VISIBLE);
        }
    }
    public boolean SalidaBuena(boolean entradas[]) {
        Log.d("SalidaBuena","nivel0");
        return !(entradas[0] && entradas[1]);
    }
    public boolean SalidaMala(boolean entradas[]) {
        return !(entradas[2] || entradas[3]);
    }
}
