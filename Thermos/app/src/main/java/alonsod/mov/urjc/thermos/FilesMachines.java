package alonsod.mov.urjc.thermos;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

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

public class FilesMachines {

    private static boolean mExternalStorageAvaliable;
    private static boolean mExternalStorageWriteable;
    public static LineGraphSeries<DataPoint> series;
    private static ArrayList<Integer> tempAL;
    private static final int NSTADOS = 3;

    public static void checkExternalStorage() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvaliable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvaliable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvaliable = mExternalStorageWriteable = false;
        }
    }

    public static boolean isStorageAvaliable(){return mExternalStorageAvaliable;}
    public static boolean isStorageWriteable(){return mExternalStorageWriteable;}

    public static void writeOn(String str, File scores, boolean append) {
        try {
            BufferedOutputStream output = new
                    BufferedOutputStream(new FileOutputStream(scores, append));
            DataOutputStream data = new DataOutputStream(output);
            str = str+"\n";
            Log.d("FilesMachines", "Escribimos en el fichero: "+str+"$$$$$$");
            data.writeBytes(str);
            data.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getFile(String machine, Context c) {
        File route = c.getExternalFilesDir("Thermal");
        return new File(route, machine+".txt");
    }

    public static boolean fileExists(String filename, Context c) {
        File route = c.getExternalFilesDir("Thermal");
        File f = new File(route, filename);
        return f.exists();
    }

    public static ArrayList<Integer> readFrom(File file) {
        tempAL = new ArrayList<>();
        try {
            FileInputStream fs = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            String line;
            while ((line = br.readLine()) != null) {
                tempAL.add(Integer.parseInt(line)/1000);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempAL;
    }

    public static DataPoint[] getDataPoint() {
        DataPoint[] dp = new DataPoint[tempAL.size()];

        for (int i = 0; i < tempAL.size(); i++){
            Log.d("FilesMachines", "tempAL["+i+"] = "+tempAL.get(i));
            dp[i] = new DataPoint(i, tempAL.get(i));
        }
        return dp;
    }
}
