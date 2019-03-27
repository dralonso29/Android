package alonsod.mov.urjc.xorapp;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LevelFactory {
    static final int MAXLEVELS = 3;
    static final int MAXTOGGLES = 4;

    private static ToggleButton[] togg;
    private static int[] id;
    private ImageView img;
    private TextView textheader;

    LevelFactory(ToggleButton[] arraytog, int[] imagesid, ImageView imgv, TextView textv){
        togg = arraytog;
        id = imagesid;
        img = imgv;
        textheader = textv;
    }

    public Level produce(int nlevel) {
        Level mylevel;
        switch (nlevel){
            case 0:
                Log.d("Debug:LevelFactory","LF: nivel0");
                mylevel = new Level0(id[nlevel], img, togg, textheader);
                break;
            case 1:
                Log.d("Debug:LevelFactory","LF: nivel1");
                mylevel = new Level1(id[nlevel], img, togg, textheader);
                break;
            case 2:
                Log.d("Debug:LevelFactory","LF: nivel2");
                mylevel = new Level2(id[nlevel], img, togg, textheader);
                break;
            default:
                Log.d("Debug:LevelFactory","LF: nivel0 por defecto");
                mylevel = new Level0(id[nlevel], img, togg, textheader);
        }
        /*mylevel.setVisibleAll(togg);
        mylevel.setButtons(togg);
        mylevel.setImage(id[nlevel], img);
        mylevel.setTitle(textheader);*/
        return mylevel;
    }
}
