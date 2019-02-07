package alonsod.mov.urjc.pruebadepuracion1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static int nbutton = 1;
    class MyButton implements View.OnClickListener {

        LinearLayout lay;
        MyButton(LinearLayout lay) {
            this.lay = lay;
        }

        @Override
        public void onClick(View arg0) {
            int time = Toast.LENGTH_SHORT;
            Button b = (Button)arg0;
            Toast msg = Toast.makeText(MainActivity.this, "Pulsado " + b.getText(), time);
            msg.show();
            createButton(lay);
        }
    }

    public void createButton(LinearLayout lay) {
        Button but = new Button(this);
        but.setOnClickListener(new MyButton(lay));
        but.setText("Boton " + nbutton);
        nbutton++;
        lay.addView(but);
        setContentView(lay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout lay = new LinearLayout(this);
        for (int i = 0; i < 2; i++){
            createButton(lay);
        }
    }
}
