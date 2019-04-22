package alonsod.mov.urjc.thermos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import static alonsod.mov.urjc.thermos.Messages.TypeMessage.REP_MACHINE_TEMP;
import static alonsod.mov.urjc.thermos.Messages.getTypeMessage;
import static alonsod.mov.urjc.thermos.Messages.readFrom;

public class MainActivity extends AppCompatActivity {

    public class ThermosUtils {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setImage();
        //getTemp();
    }

    public void startGraphActivity(View v) {
        Intent graph = new Intent(MainActivity.this, GraphActivity.class);
        startActivity(graph);
    }

    public void startHelpActivity(View v) {
        Intent help = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(help);
    }

    private void getTemp() {
        Log.d("MainActivity", "Dentro de getTemp");
        Thread c = new Thread(){
            @Override
            public void run(){
                try {
                    String HOST = "10.0.0.8";
                    Socket s = new Socket(HOST, 5000);
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    String machine = "alpha01";
                    Messages.RequestClient rc = new Messages.RequestClient(machine);
/*
                    Log.d("MainActivity","Tipo mensaje: "+rc.typeMessage());
*/
                    rc.writeTo(dos);
                    Messages replyToServer = Messages.readFrom(dis);
                    /*if (serverReply.typeMessage().equals(REP_MACHINE_TEMP)) {*/
                    if (replyToServer != null) {
                        Log.d("MainActivity", "Respuesta: " + dis.readUTF());
                        replyToServer.writeTo(dos);
                    }

                    /*}*/
                    //Log.d("MainActivity", "Tipo de mensaje ERROR");
//			Messages reponse = Messages.readFrom(dis);
//			if(reponse != null) {
//				reponse.writeTo(dos); // send reply to client
//			}
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
