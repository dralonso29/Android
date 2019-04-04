package alonsod.mov.urjc.xorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {
    private String usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setImage();
        String usrname = getUsrName(); //mirar si usrname no es null
        //Log.d("WelcomeActivity", usrname);
        usr = usrname;
        putWelcomeMsg(usrname);
    }

    private void putWelcomeMsg(String usrname) {
        TextView txt = findViewById(R.id.usr_welcome);
        txt.setText("Loggeado como "+usrname);
    }

    private String getUsrName() {

        Intent intent = getIntent();
        Bundle info = intent.getExtras();
        if (info != null) {
            return info.getString("username");
        }
        return "None";
    }

    private void setImage() {
        String img_level = "ic_brain02";
        int id = getResources().getIdentifier(img_level, "drawable", getPackageName());
        ImageView imgv = findViewById(R.id.img_app);
        imgv.setImageResource(id);
    }

    public void startlevel(View v){
        Intent play = new Intent(WelcomeActivity.this, MainActivity.class);
        Bundle bdl = new Bundle();
        bdl.putString("username", usr);
        play.putExtras(bdl);
        startActivity(play);
    }

    public void showhelp(View v) {
        Intent help = new Intent(WelcomeActivity.this, Help.class);
        startActivity(help);
    }

    public void showscores(View v) {
        Intent scores = new Intent(WelcomeActivity.this, ScoresActivity.class);
        startActivity(scores);
    }
}
