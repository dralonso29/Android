package alonsod.mov.urjc.xorapp;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import static alonsod.mov.urjc.xorapp.LevelFactory.MAXTOGGLES;
import static android.view.View.VISIBLE;

public abstract class Level {

    public abstract String getLevelName();
    public abstract void setImage(int id, ImageView img);
    public abstract void setButtons(ToggleButton toggle[]);
    public abstract boolean SalidaBuena(boolean entradas[]);
    public abstract boolean SalidaMala(boolean entradas[]);
    public abstract void setTitle(TextView textheader);
    public void setVisibleAll(ToggleButton toggle[]){
        for (int i=0;i<MAXTOGGLES;i++){
            toggle[i].setVisibility(VISIBLE);
        }
    }
}
