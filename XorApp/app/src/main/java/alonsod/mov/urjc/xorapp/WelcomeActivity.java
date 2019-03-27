package alonsod.mov.urjc.xorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setImage();
    }

    private void setImage() {
        String img_level = "ic_brain02";
        int id = getResources().getIdentifier(img_level, "drawable", getPackageName());
        ImageView imgv = findViewById(R.id.img_app);
        imgv.setImageResource(id);
    }

    public void startlevel(View v){
        Intent play = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(play);
    }

    public void showhelp(View v) {
        Intent help = new Intent(WelcomeActivity.this, Help.class);
        startActivity(help);
    }
}
