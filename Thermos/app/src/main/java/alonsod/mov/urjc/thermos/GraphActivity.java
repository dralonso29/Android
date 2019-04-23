package alonsod.mov.urjc.thermos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class GraphActivity extends AppCompatActivity {
    private static final String MACHINES[] = {"alpha", "beta", "delta", "epsilon", "gamma", "zeta"};
    public static final int NMACHINE = 2; // Max identifier for machine name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setVisibleImageInfo(true);
    }

    private void setVisibleImageInfo(boolean visible) {
        String img_info_graph = "ic_infograph01";
        int id = getResources().getIdentifier(img_info_graph, "drawable", getPackageName());
        ImageView imgv = findViewById(R.id.info_graph);
        imgv.setImageResource(id);
        if (!visible){
            imgv.setVisibility(View.GONE);
        }
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
        String machine = (String) item.getTitle();
        switch (myItem) {
            case R.id.menu_help:
                Intent help = new Intent(GraphActivity.this, HelpActivity.class);
                startActivity(help);
                return true;
            default:
                Log.d("GraphActivity", "item:"+machine);
                if (isMachineName(machine)){
                    Log.d("GraphActivity", "YEEES: item:"+item.getTitle());
                    // llamar a la funcion que pida la temperatura de la maquina
                    getTemp(machine);
                    setVisibleImageInfo(false);
                    //showGraph(machine);
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
                        String request = dis.readUTF();
                        Log.d("GraphActivity", "Respuesta: " + request);
                        FilesMachines.checkExternalStorage();
                        if (FilesMachines.isStorageWriteable()){
                            File file = FilesMachines.getFile(machine, GraphActivity.this); // add new Temperature to file
                            FilesMachines.writeOn(request, file, true);
                        }
                        //replyToServer.writeTo(dos);
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
        try {
            c.join(); //we must wait thread because of readding from file later
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isMachineName(String nameItem) {
        for (int i = 1; i <= NMACHINE; i++){
            for (String vmachine: MACHINES){
                String machine = vmachine+"0"+i; // alpha01, beta01 ...
                //Log.d("GraphActivity", "isMachineName: machine: "+machine);
                if (machine.equals(nameItem)) {
                    return true;
                }
            }
        }
        return false;
    }
}
