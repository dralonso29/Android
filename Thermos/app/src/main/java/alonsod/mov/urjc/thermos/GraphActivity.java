package alonsod.mov.urjc.thermos;

import android.content.Context;
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
import android.widget.Switch;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
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
    private static final int MAX_SPLIT = 4;
    private static final int MAX_IP_NUM = 255;
    private static final int MIN_IP_NUM = 0;
    private static final int MAX_PORT = 65535;
    private static final int MIN_PORT = 1025;
    private static final int MAX_PORT_LEN = 5;
    private final int INIT_ALARM = 100;
    private static final float TITLE_SIZE = 60;
    private static final int TIMEOUT = 3000;
    public static final int ORANGE_COLOR = Color.rgb(255, 153, 102);
    private Menu gMenu;

    private class saveMachine {
        String machine;
        int port;
        int alarm;
        ArrayList<Integer> tempAL;
        boolean tryConnection;
        boolean showNotifications;

        saveMachine(){
            this.machine = null;
            this.tryConnection = true;
            this.showNotifications = false;
        }

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
        private void setAttemptConnect(boolean b) {
            tryConnection = b;
        }
        private boolean getAttemptConnect(){return tryConnection;}
        private void setShowNotifications(boolean b){
            showNotifications = b;
        }
        private boolean getShowNotifications(){return showNotifications;}

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
        sm.setPort(SERVER_PORT);
        if (savedInstanceState != null){
            sm.setMachine(savedInstanceState.getString("machine"));
            sm.setAlarm(savedInstanceState.getInt("alarm"));
            sm.setShowNotifications(savedInstanceState.getBoolean("notify"));
            showGraph(sm.getMachine());
        }
        Log.d("GraphActivity", "Inicialmente machine:"+sm.getMachine());
        Log.d("GraphActivity", "Inicialmente port:"+sm.getPort());
        Log.d("GraphActivity", "Inicialmente alarma:"+sm.getAlarm());

        setAlarmTitle();
        saveAlarm(INIT_ALARM);
        savePort(SERVER_PORT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("GraphActivity", "onStop()");
        saveMachine(sm.getMachine());
        saveAlarm(sm.getAlarm());
        savePort(sm.getPort());
        Log.d("GraphActivity", "La alarma vale en onStop(): "+sm.getAlarm() );
        if (sm.getShowNotifications()){
            Log.d("GraphActivity", "Lanzamos el servicio...");
            Intent intent = new Intent(this, ThermosService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GraphActivity", "La alarma vale en onResume(): "+sm.getAlarm());
        if (sm.getShowNotifications()){
            Log.d("GraphActivity", "Paramos el servicio...");
            stopService(new Intent(this, ThermosService.class));
        }
    }


    public void setNotifications(View view) {
        if (sm.getShowNotifications()){
            sm.setShowNotifications(false);
            return;
        }
        sm.setShowNotifications(true);
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("machine", sm.getMachine());
        state.putInt("alarm", sm.getAlarm());
        state.putBoolean("notify", sm.getShowNotifications());
    }


    private void showGraph(String machine) {
        TextView title = findViewById(R.id.title_graph);
        TextView legend = findViewById(R.id.legend);
        GraphView graph = findViewById(R.id.graph);
        FilesMachines.checkExternalStorage();
        String filename = machine+".txt";

        if (FilesMachines.isStorageAvaliable() && FilesMachines.fileExists(filename, this)){
            setVisibilityImageInfo(View.GONE);
            File file = FilesMachines.getFile(machine, this);
            graph.removeAllSeries(); // reset graph
            sm.tempAL = FilesMachines.readFrom(file);
            title.setVisibility(View.INVISIBLE);

            //float title_size = 60;
            setTitleGraph(graph, TITLE_SIZE);
            setAxis(graph);
            setColorPoints(graph, sm.tempAL, sm.getAlarm());
            graph.getViewport().setScalable(true);
            graph.getViewport().setScalableY(true);
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScrollableY(true);
            legend.setVisibility(View.VISIBLE);

            DataPoint[] dp = FilesMachines.getDataPoint();
            PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(dp);
            showToastPoint(series);
            return;
        }
        graph.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        title.setText("No hay datos. Pulsa el boton de actualizar");
    }

    private void setAxis(GraphView graph) {
        float distanceY = 1.3f;

        graph.getGridLabelRenderer().setHorizontalAxisTitle("Número de medida");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Temperatura (℃)");
        graph.getGridLabelRenderer().setLabelsSpace(10);
        graph.setScaleY(distanceY);
    }

    private void setTitleGraph(GraphView graph, float size) {
        graph.setTitle("Maquina "+sm.getMachine());
        graph.setTitleTextSize(size);

    }

    public void setColorPoints(GraphView graph, ArrayList<Integer> tempAL, int alarm){
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

    public void setAlarmTitle() {
        String title = "No hay alarma";
        if (sm.getAlarm() < INIT_ALARM){
            title = "Alarma establecida en "+sm.getAlarm()+" grados";;
        }
        TextView titleAlarm = findViewById(R.id.alarm_title_graph);
        titleAlarm.setText(title);
    }

    public void setAlarm(View view) {
        EditText etAlarm = findViewById(R.id.alarm_edittext);
        String alarmS = etAlarm.getText()+"";
        int time = Toast.LENGTH_SHORT;
        Toast msg;
        if (!isInteger(alarmS)){
            String mymsg = "Introduce un numero entero, por favor";
            msg = Toast.makeText(GraphActivity.this, mymsg, time);
            msg.show();
            return;
        }
        int alarm = Integer.parseInt(alarmS);

        Log.d("GraphActivity", "Alarma vale: "+alarm);
        if (alarm >= ZERO_DEG && alarm < INIT_ALARM) {
            sm.setAlarm(alarm);
            setAlarmTitle();
            saveAlarm(sm.getAlarm());
            if (sm.getMachine() != null) {
                showGraph(sm.getMachine());
            }
            return;
        }
        String mymsg = "Alarma invalida, prueba otra vez";
        msg = Toast.makeText(GraphActivity.this, mymsg, time);
        msg.show();
    }
    private void saveAlarm(int alarm) {
        String FILENAME = "save_Alarm";

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(alarm);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMachine(String machine) {
        String FILENAME = "save_Machine";
        machine += "";
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(machine.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void savePort(int port) {
        String FILENAME = "save_Port";
        String sPort = port+"";

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(sPort.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
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
            if (!isIPOk(machine)){
                String mymsg = "¡IP invalida!";
                msg = Toast.makeText(GraphActivity.this, mymsg, time);
                msg.show();
                return;
            }
            if (!isPortOk(portstr)) {
                String mymsg = "¡Puerto invalido!";
                msg = Toast.makeText(GraphActivity.this, mymsg, time);
                msg.show();
                return;
            }
            port = Integer.parseInt(portstr);
            sm.setPort(port);
            sm.setMachine(machine);
            setVisibleUpdate(true, gMenu);
            //getTemp(sm.getMachine(machine), sm.getPort());
            showGraph(machine);
            return;
        }
        String mymsg = "Revisa los datos...";
        msg = Toast.makeText(GraphActivity.this, mymsg, time);
        msg.show();
    }

    private boolean isIPOk(String ip) {
        Log.d("GraphActivity", "La ip a trocear es: "+ip);
        boolean okIP = false;
        String[] splited = ip.split("\\.", MAX_SPLIT);
        Log.d("GraphActivity", "Despues de trocear la longitud es: "+splited.length);
        if (splited.length == MAX_SPLIT){
            for (int i = 0; i <= MAX_SPLIT - 1; i++){
                Log.d("GraphActivity", "splited["+i+"] = "+splited[i]);
                Log.d("GraphActivity", "isInteger: "+isInteger(splited[i]));
                Log.d("GraphActivity", "isLoweOrEqualThan: "+isBetweenTwo(splited[i], MAX_IP_NUM, MIN_IP_NUM));
                if (isInteger(splited[i]) && isBetweenTwo(splited[i], MAX_IP_NUM, MIN_IP_NUM)) {
                    okIP = true;
                    continue;
                }
                return false;
            }
        }
        Log.d("GraphActivity", "Al final okIP --> "+okIP);
        return okIP;
    }

    private boolean isBetweenTwo(String s, int max, int min) {
        int num = Integer.parseInt(s);
        return num <= max && num >= min;
    }

    private boolean isPortOk(String port) {
        if (port.length() > MAX_PORT_LEN){
            return false;
        }
        return isInteger(port) && isBetweenTwo(port, MAX_PORT, MIN_PORT);
    }

    private boolean isInteger(String s) {
        try{
            Integer.parseInt(s);
        }catch (NumberFormatException e){
            return false;
        }
        return true;
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
                    Socket s = new Socket();
                    s.connect(new InetSocketAddress(ip, PORT), TIMEOUT);
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
                    sm.setAttemptConnect(true);
                }catch (ConnectException e) {
                    System.out.println("Connection refused "+ e);
                    Log.d("GraphActivity", "Connection refused: cannot connect: ");
                    sm.setAttemptConnect(false);
                }catch (UnknownHostException e) {
                    System.out.println("Cannot connect to host "+ e);
                }catch (IOException e) {
                    System.out.println("IOExcepton "+ e);
                    Log.d("GraphActivity", "Connection refused: IOException");
                    sm.setAttemptConnect(false);
                }
            }
        };
        c.start();
        try {
            c.join(); //we must wait thread because of reading from file later
            if(!sm.getAttemptConnect()){
                notifyErrorConnect();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void notifyErrorConnect() {
        int time = Toast.LENGTH_SHORT;
        Toast msg;
        String mymsg = "Error: Connection refused";
        msg = Toast.makeText(GraphActivity.this, mymsg, time);
        msg.show();
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
