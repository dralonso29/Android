package alonsod.mov.urjc.thermos;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {
    private static final String MACHINES[] = {"alpha", "beta", "delta", "epsilon", "gamma", "zeta", "local"};
    public static final int NMACHINE = 2; // Max identifier for machine name
    private Menu gMenu;

    private class saveMachine {
        String machine;
        saveMachine(String machine) {
           this.machine = machine;
        }
        private String getMachine(){
            return machine;
        }

        private void setMachine(String m){
            machine = m;
        }
    }
    saveMachine sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setVisibleImageInfo(true);
        //showGraph();
        sm = new saveMachine(null);
    }

    private void showGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);
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

    private void setTitleGraphActivity(String machine){
        String title = "MAQUINA: "+machine;
        TextView textv = findViewById(R.id.title_graph);
        textv.setText(title);
    }

    private void setVisibleUpdate(boolean visibility, Menu menu) {
        MenuItem mi = menu.findItem(R.id.menu_actualizar);
        mi.setVisible(visibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.conf, menu);
        gMenu = menu;
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
            case R.id.menu_actualizar:
                if (!isMachineName(machine)){
                    Log.d("GraphActivity", "Actualizamos: machine = "+sm.getMachine());
                    getTemp(sm.getMachine());
                    showGraph();
                    return true;
                }
                return super.onOptionsItemSelected(item);
            default:
                if (!isMachineName(machine)){
                    Log.d("GraphActivity", "YEEES: item:"+machine);
                    // llamar a la funcion que pida la temperatura de la maquina
                    //getTemp(m.getActualMachine());
                    sm.setMachine(machine);
                    Log.d("GraphActivity", "La maquina se llama:  "+sm.getMachine());
                    setVisibleImageInfo(false);
                    setTitleGraphActivity(machine);
                    setVisibleUpdate(true, gMenu);
                    showGraph();
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }


    private void getTemp(final String ip) {
        Log.d("GraphActivity", "Dentro de getTemp");
        Thread c = new Thread(){
            @Override
            public void run(){
                try {
                    int PORT = 5000;
                    Socket s = new Socket(ip, PORT);
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    Messages.RequestClient rc = new Messages.RequestClient();
                    rc.writeTo(dos);
                    Messages replyFromServer = Messages.readFrom(dis);
                    if (replyFromServer != null) {
                        String answer = dis.readUTF();
                        Log.d("GraphActivity", "Respuesta: " + answer);
                        FilesMachines.checkExternalStorage();
                        if (FilesMachines.isStorageWriteable()){
                            File file = FilesMachines.getFile(ip, GraphActivity.this); // add new Temperature to file
                            FilesMachines.writeOn(answer, file, true);
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
        for (String machine: MACHINES){
            if (machine.equals(nameItem)) {
                return true;
            }
        }
        return false;
    }
}
