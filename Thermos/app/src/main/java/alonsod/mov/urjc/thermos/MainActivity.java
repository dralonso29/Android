package alonsod.mov.urjc.thermos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setImage();
    }

    public void startGraphActivity(View v) {
        Intent graph = new Intent(MainActivity.this, GraphActivity.class);
        startActivity(graph);
    }

    public void startHelpActivity(View v) {
        Intent help = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(help);
    }

    private void setImage() {
        String img_level = "ic_icon01";
        int id = getResources().getIdentifier(img_level, "drawable", getPackageName());
        ImageView imgv = findViewById(R.id.img_app);
        imgv.setImageResource(id);
    }

    public void start() {

    }

    public void help() {
        Intent help = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(help);
    }
}
