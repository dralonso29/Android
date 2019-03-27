package alonsod.mov.urjc.xorapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ImageView imghelp = findViewById(R.id.img_help);
        String img_help = "ic_help_logic";
        int id = getResources().getIdentifier(img_help, "drawable", getPackageName());
        imghelp.setImageResource(id);
    }
}
