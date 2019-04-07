package alonsod.mov.urjc.xorapp;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


import static alonsod.mov.urjc.xorapp.LevelFactory.MAXLEVELS;


public class StoreUsers {

    private static final int KEYVALUE = 2;

    HashMap<String, String> hsmp;
    private String str;
    private String username;
    private int[] actual_times;

    StoreUsers(String username, int[] actual_times) {
        hsmp = new HashMap<>();
        this.username = username;
        this.actual_times = actual_times;
    }

    /*public String getString() {
        return str;
    }*/


    public String[] parseLine(String line){
        String lineaux;
        String[] splited = new String[KEYVALUE];
        int i;
        String times = "";

        lineaux = line.split("\n")[0];
        splited[0] = lineaux.split(" ")[0]; //username
        for (i = 1; i <= MAXLEVELS ; i++){
            if (i == 1){
                times = times + lineaux.split(" ")[i];
                continue;
            }
            times = times +" "+ lineaux.split(" ")[i];
        }
        splited[1] = times; //Es posible que aqui haya que añadir un \n
        return splited;
    }

    public void putOnHashMap(String[] array){
        hsmp.put(array[0], array[1]);
    }

    public int[] getTimesString(String usr){
        String t = hsmp.get(usr);
        String[] aux = t.split(" ");
        int[] times = new int[MAXLEVELS];
        for (int i =0 ; i<MAXLEVELS; i++) {
            times[i] = Integer.parseInt(aux[i]);
        }
        return times;
    }

    public void readFile(File scores) {
        try {
            FileInputStream fs = new FileInputStream(scores);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            String line;
            String[] parsed;
            while ((line = br.readLine()) != null) {
                Log.d("StoreUsers", "Leemos -->" + line + "@@@@");
                parsed = parseLine(line);
                Log.d("StoreUsers", "parsed[0]: "+parsed[0]);
                Log.d("StoreUsers", "parsed[1]: "+parsed[1]+"@@@@");
                putOnHashMap(parsed);
                /*arraylines.add(line + "\n"); // este salto de linea es de vital importancia*/
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String make_string(int[] times) {
        String str = username;
        for (int i = 0; i < MAXLEVELS;i++) {
            str = str + " " + times[i];
        }
        return str;
    }

    public void writeOn(String str, File scores, boolean append) {
        try {
            BufferedOutputStream output = new
                    BufferedOutputStream(new FileOutputStream(scores, append));
            DataOutputStream data = new DataOutputStream(output);
            str = str+"\n";
            Log.d("StoreUsers", "Escribimos en el fichero: "+str+"$$$$$$");
            data.writeBytes(str);
            data.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String arrayIntToStr(int[] times){
        String str = "";
        for (int i = 0; i < MAXLEVELS; i++){
            if (i == 0){
                str = str + times[i];
                continue;
            }
            str = str + " " + times[i];
        }
        return str;
    }

    public void modify_HashMap() {
        String value;
        int[] times_file = getTimesString(username);
        for (int i = 0; i < MAXLEVELS; i++){
            Log.d("StoreUsers", "modify_HasMap: actual_times["+i+"]: "+actual_times[i]);
            Log.d("StoreUsers", "modify_HasMap: times_file["+i+"]: "+times_file[i]);
            if (actual_times[i] <= times_file[i]){
                times_file[i] = actual_times[i];
            }
        }
        value = arrayIntToStr(times_file);
        Log.d("StoreUsers", "Modificado: "+value+"@@@@"); //Los @ son para ver si mete los saltos de linea
        hsmp.put(username, value);
    }

    public boolean userFound() {
        if (hsmp.get(username) == null){
            return false;
        }
        return true;
    }

    public String getValue() {
        return hsmp.get(username);
    }

    public void writing(File scores) {
        boolean first = true;
        for (HashMap.Entry<String, String> hash: hsmp.entrySet()){
            String str;
            str = hash.getKey() + " " + hash.getValue();
            if (first) {
                writeOn(str, scores, false);
                first = false;
                continue;
            }
            writeOn(str, scores, true);
        }
    }
}
