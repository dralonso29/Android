package alonsod.mov.urjc.xorapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        /*TextView helptext = findViewById(R.id.help_id);
        helptext.setVisibility(View.VISIBLE);*/
        ImageView imghelp = findViewById(R.id.img_help);
        String img_help = "ic_help";
        int id = getResources().getIdentifier(img_help, "drawable", getPackageName());
        imghelp.setImageResource(id);
    }
}
