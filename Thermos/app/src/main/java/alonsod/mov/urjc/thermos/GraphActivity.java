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
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

public class GraphActivity extends AppCompatActivity {
    private static final String MACHINES[] = {"alpha", "beta", "delta", "epsilon", "gamma", "zeta"};
    public static final int NMACHINE = 2; // Max identifier for machine name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setImageInfo();
    }

    private void setImageInfo() {
        String img_info_graph = "ic_infograph01";
        int id = getResources().getIdentifier(img_info_graph, "drawable", getPackageName());
        ImageView imgv = findViewById(R.id.info_graph);
        imgv.setImageResource(id);
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
                    getTemp(nameItem);
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }

    private void getTemp(final String machine) {
        Log.d("GraphActivity", "Dentro de getTemp");
        Thread c = new Thread(){
            @Override
            public void run(){
                try {
                    String HOST = "10.0.0.8";
                    int PORT = 5000;
                    Socket s = new Socket(HOST, PORT);
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    Messages.RequestClient rc = new Messages.RequestClient(machine);
                    rc.writeTo(dos);
                    Messages replyToServer = Messages.readFrom(dis);
                    if (replyToServer != null) {
                        Log.d("GraphActivity", "Respuesta: " + dis.readUTF());
                        replyToServer.writeTo(dos);
                    }
                }catch (ConnectException e) {
                    System.out.println("Connection refused "+ e);
                }catch (UnknownHostException e) {
                    System.out.println("Cannot connect to host "+ e);
                }catch (IOException e) {
                    System.out.println("IOExcepton "+ e);
                }
            }
        };
        c.start();
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
