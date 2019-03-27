package alonsod.mov.urjc.xorapp;

import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import static alonsod.mov.urjc.xorapp.LevelFactory.MAXTOGGLES;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class Level {
    private int visiblebuttons;
    private int level;
    public int id;
    private ImageView img;
    private ToggleButton[] togg;
    private TextView textheader;

    public Level(int id, ImageView img, int level, int visiblebuttons, ToggleButton[] togg, TextView textheader) {
        this.id = id;
        this.img = img;
        this.level = level;
        this.visiblebuttons = visiblebuttons;
        this.togg = togg;
        this.textheader = textheader;
    }

    public String getLevelName() { return "nivel " + level;}
    private void setImage() {
        img.setImageResource(id);
    }
    public abstract boolean SalidaBuena(boolean entradas[]);
    public abstract boolean SalidaMala(boolean entradas[]);
    private void setTitle() {
        textheader.setText("Nivel " + level);
    }
    private void setVisibleAll(){
        for (int i=0;i<MAXTOGGLES;i++){
            togg[i].setVisibility(VISIBLE);
        }
    }
    private void setButtons() {
        if (visiblebuttons >= MAXTOGGLES) {
            //devolver error
            return;
        }else{
            for (int i = visiblebuttons; i < MAXTOGGLES;i++) {
                togg[i].setVisibility(GONE);
            }
            return;
        }
    }
    /*public void setVisibleMenuButton(int i){
        menuv.findItem(R.id.menu_level1).setVisible(true);
    }*/
    public void loadLevel() {
        setVisibleAll();
        setButtons();
        setImage();
        setTitle();
    }
}
