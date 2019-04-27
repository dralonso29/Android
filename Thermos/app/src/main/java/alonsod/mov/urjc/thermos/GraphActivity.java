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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {
    private static final String MACHINES[] = {"alpha", "beta", "delta", "epsilon", "gamma", "zeta", "local"};
    public static final int SERVER_PORT = 25029;
    private static final int ZERO_DEG = 0;
    private final int INIT_ALARM = 500;
    public static final int ORANGE_COLOR = Color.rgb(255, 153, 102);
    private Menu gMenu;

    private class saveMachine {
        String machine;
        int port;
        int alarm;
        ArrayList<Integer> tempAL;

        private String getMachine(){
            return machine;
        }
        private void setMachine(String m){
            machine = m;
        }
        private int getPort(){
            return port;
        }
        private void setPort(int p){
            port = p;
        }
        private int getAlarm(){
            return alarm;
        }
        private void setAlarm(int a){
            alarm = a;
        }
    }
    saveMachine sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setVisibilityImageInfo(View.VISIBLE);
        //showGraph();
        sm = new saveMachine();
        sm.setAlarm(INIT_ALARM);
    }

    private void showGraph(String machine) {
        TextView title = findViewById(R.id.title_graph);
        FilesMachines.checkExternalStorage();
        String filename = machine+".txt";

        if (FilesMachines.isStorageAvaliable() && FilesMachines.fileExists(filename, this)){
            File file = FilesMachines.getFile(machine, this);
            sm.tempAL = FilesMachines.readFrom(file);

            setColorPoints(sm.tempAL, sm.getAlarm());

            //No se por que no funciona lo de pulsar en cada boton, si supuestamente le paso el PointsGraphSeries

            /*DataPoint[] dp = FilesMachines.getDataPoint();
            PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(dp);
            showToastPoint(series);*/
            return;
        }
        title.setText("OOOPS: No hay datos. Pulsa el boton de actualizar");
        /*GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);*/
    }

    public void setColorPoints(ArrayList<Integer> tempAL, int alarm){
        GraphView graph = findViewById(R.id.graph);
        PointsGraphSeries<DataPoint> series;
        graph.setVisibility(View.VISIBLE);

        for (int i = 0; i< tempAL.size(); i++) {
            series = getPointsGraphSeries(i, tempAL.get(i));
            if (tempAL.get(i) > alarm) { // danger
                series.setColor(Color.RED);
                graph.addSeries(series);
                continue;
            }else if (tempAL.get(i) == alarm){ // warning
                series.setColor(ORANGE_COLOR);
                graph.addSeries(series);
                continue;
            }
            series.setColor(Color.GREEN); // ok
            graph.addSeries(series);
        }
    }

    public PointsGraphSeries<DataPoint> getPointsGraphSeries(int x, int y) {
        return new PointsGraphSeries<DataPoint>(new DataPoint[] { new DataPoint(x, y) });
    }

    public void setAlarmTitle(String title) {
        TextView titleAlarm = findViewById(R.id.alarm_title_graph);
        titleAlarm.setText(title);
    }

    public void setAlarm(View view) {
        EditText etAlarm = findViewById(R.id.alarm_edittext);
        String alarmS = etAlarm.getText()+"";
        int time = Toast.LENGTH_SHORT;
        Toast msg;
        if (!isInteger(alarmS)){
            return;
        }
        int alarm = Integer.parseInt(alarmS);

        Log.d("GraphActivity", "Alarma vale: "+alarm);
        if (alarm >= ZERO_DEG && alarm < INIT_ALARM) {
            sm.setAlarm(alarm);
            String title = "Alarma establecida en "+sm.getAlarm()+" grados";
            setAlarmTitle(title);
            showGraph(sm.getMachine());
            return;
        }
        String mymsg = "Alarma invalida, prueba otra vez";
        msg = Toast.makeText(GraphActivity.this, mymsg, time);
        msg.show();
    }

    private boolean isInteger(String alarmS) {
        int time = Toast.LENGTH_SHORT;
        Toast msg;
        try{
            Integer.parseInt(alarmS);
            return true;
        }catch (NumberFormatException e){
            String mymsg = "Introduce un numero entero, por favor";
            msg = Toast.makeText(GraphActivity.this, mymsg, time);
            msg.show();
            return false;
        }
    }

    private void showToastPoint(PointsGraphSeries<DataPoint> series) {

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String msg = "La medida "+Math.round(dataPoint.getX())+" es de "+dataPoint.getY()+" grados";
                Toast.makeText(GraphActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVisibilityImageInfo(int v) {
        String img_info_graph = "ic_infograph01";
        int id = getResources().getIdentifier(img_info_graph, "drawable", getPackageName());
        ImageView imgv = findViewById(R.id.info_graph);
        imgv.setImageResource(id);
        imgv.setVisibility(v);
    }

    public void manualRequest(View view) {
        EditText ipedit = findViewById(R.id.ip_edittext);
        EditText portedit = findViewById(R.id.port_edittext);
        String machine = ipedit.getText()+"";
        String portstr = portedit.getText()+"";
        int time = Toast.LENGTH_SHORT;
        int port;
        Toast msg;

        if (machine.length() > 0 && portstr.length() > 0){
            port = Integer.parseInt(portstr);
            sm.setPort(port);
            setVisibilityImageInfo(View.GONE);
            sm.setMachine(machine);
            setVisibleUpdate(true, gMenu);
            //getTemp(sm.getMachine(machine), sm.getPort());
            setTitleGraphActivity(machine);
            showGraph(machine);
            return;
        }
        String mymsg = "Revisa los datos...";
        msg = Toast.makeText(GraphActivity.this, mymsg, time);
        msg.show();
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
                    Log.d("GraphActivity", "Boton actualizar: machine = "+sm.getMachine());
                    getTemp(sm.getMachine(), sm.getPort());
                    showGraph(sm.getMachine());
                    return true;
                }
                return super.onOptionsItemSelected(item);
            default:
                if (!isMachineName(machine)){
                    Log.d("GraphActivity", "YEEES: item:"+machine);
                    // llamar a la funcion que pida la temperatura de la maquina
                    //getTemp(m.getActualMachine());
                    sm.setMachine(machine);
                    sm.setPort(SERVER_PORT);
                    Log.d("GraphActivity", "La maquina se llama:  "+sm.getMachine());
                    setVisibilityImageInfo(View.GONE);
                    setTitleGraphActivity(machine);
                    setVisibleUpdate(true, gMenu);
                    showGraph(sm.getMachine());
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }
    }


    public void getTemp(final String ip, final int PORT) {
        Log.d("GraphActivity", "Dentro de getTemp");
        Thread c = new Thread(){
            @Override
            public void run(){
                try {
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
            c.join(); //we must wait thread because of reading from file later
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
