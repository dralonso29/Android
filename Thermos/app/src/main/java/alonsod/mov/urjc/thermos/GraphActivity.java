package alonsod.mov.urjc.thermos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class GraphActivity extends AppCompatActivity {
    private static final String MACHINES[] = {"alpha", "beta", "delta", "epsilon", "gamma", "zeta"};
    public static final int NMACHINE = 2; // Max identifier for machine name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.conf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int myItem = item.getItemId();
        String nameItem = (String) item.getTitle();
        switch (myItem) {
            case R.id.menu_help:
                Intent help = new Intent(GraphActivity.this, HelpActivity.class);
                startActivity(help);
                return true;
            default:
                Log.d("GraphActivity", "item:"+nameItem);
                if (isMachineName(nameItem)){
                    Log.d("GraphActivity", "YEEES: item:"+item.getTitle());
                    // llamar a la funcion que pida la temperatura de la maquina
                    return true;
                }
                return super.onOptionsItemSelected(item);
                /*if (prep.setLevelMenu(myItem)){ //prep.NLEVEL updated on setLevelMenu
                    msg = Toast.makeText(MainActivity.this, "Estas en el nivel " + prep.NLEVEL, time);
                    msg.show();
                    prep.resetButtons();
                    level = lf.produce(prep.NLEVEL);
                    level.loadLevel();
                    tc.setInitial(Calendar.getInstance().getTime());
                    prep.MAXFAILURES = 2;
                    prep.setFailsText();
                    next.setOnClickListener(new NextButt(prep, level, lf, prep.getImgViewLevel(), prep.getTextViewHeader()));
                    next.setVisibility(View.VISIBLE);
                    return true;
                }
                return super.onOptionsItemSelected(item);*/
        }
    }

    private boolean isMachineName(String nameItem) {
        for (int i = 1; i <= NMACHINE; i++){
            for (String vmachine: MACHINES){
                String machine = vmachine+"0"+i; // alpha01, beta01 ...
                Log.d("GraphActivity", "isMachineName: machine: "+machine);
                if (machine.equals(nameItem)) {
                    return true;
                }
            }
        }
        return false;
    }
}
