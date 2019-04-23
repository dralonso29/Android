package alonsod.mov.urjc.thermos;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class FilesMachines {

    private static boolean mExternalStorageAvaliable;
    private static boolean mExternalStorageWriteable;

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
}
