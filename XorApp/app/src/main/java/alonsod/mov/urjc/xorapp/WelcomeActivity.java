package alonsod.mov.urjc.xorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void startlevel(View v){
        Intent play = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(play);
    }
}
