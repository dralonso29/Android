package alonsod.mov.urjc.xorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class NameActivity extends AppCompatActivity {

    private static final int MINLEN = 0;
    private static final int MAXLEN = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        setImage();
    }

    private void setImage() {
        String img_level = "ic_brain02";
        int id = getResources().getIdentifier(img_level, "drawable", getPackageName());
        ImageView imgv = findViewById(R.id.img_app);
        imgv.setImageResource(id);
    }

    public void showmenu(View view) {
        EditText username = findViewById(R.id.username_button);
        String usr = username.getText()+"";
        if (isValidUsrName(usr)){
            //Log.d("NameActivity","Usuario: "+ usr);
            Intent showmenu = new Intent(NameActivity.this, WelcomeActivity.class);
            Bundle bdl = new Bundle();
            bdl.putString("username", usr);
            showmenu.putExtras(bdl);
            startActivity(showmenu);
        }
        return;
    }

    private boolean isValidUsrName(String usr) {
        int time = Toast.LENGTH_SHORT;
        Toast msg;
        if (usr.length() <= MINLEN){
            String mymsg = "Escribe un nombre de usuario";
            msg = Toast.makeText(NameActivity.this, mymsg, time);
            msg.show();
            return false;
        }else if (usr.length() >= MAXLEN){
            String mymsg = "Nombre de usuario demasiado largo. Maximo permitido: "+MAXLEN+" caracteres";
            msg = Toast.makeText(NameActivity.this, mymsg, time);
            msg.show();
            return false;
        }
        return true;
    }
}
