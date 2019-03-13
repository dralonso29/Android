package alonsod.mov.urjc.xorapp;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LevelFactory {
    static final int MAXLEVELS = 3;
    static final int MAXTOGGLES = 4;
    private static ToggleButton[] togg;
    private Context cont;
    private static int[] id;
    private ImageView img;
    private TextView textheader;

    LevelFactory(ToggleButton[] arraytog, int[] imagesid, ImageView imgv, TextView textv, Context maincontext){
        togg = arraytog;
        id = imagesid;
        img = imgv;
        cont = maincontext;
        textheader = textv;
    }

    public Level produce(int nlevel) {
        Level mylevel;
        switch (nlevel){
            case 0:
                Log.d("SalidaBuena","LF: nivel0");
                mylevel = new Level0(nlevel);
                break;
            case 1:
                Log.d("SalidaBuena","LF: nivel1");
                mylevel = new Level1(nlevel);
                break;
            case 2:
                Log.d("SalidaBuena","LF: nivel2");
                mylevel = new Level2(nlevel);
                break;
            default:
                mylevel = new Level0(nlevel);
        }
        mylevel.setVisibleAll(togg);
        mylevel.setButtons(togg);
        mylevel.setImage(id[nlevel], img);
        mylevel.setTitle(textheader);
        return mylevel;
    }
}
