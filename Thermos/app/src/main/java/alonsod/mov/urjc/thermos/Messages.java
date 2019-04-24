package alonsod.mov.urjc.thermos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Messages {
    public enum TypeMessage {
        REQ_MACHINE_TEMP, REP_MACHINE_TEMP, RECEIVED_REP, BAD_REQ
    }
    public abstract TypeMessage typeMessage();
    public abstract void writeTo(DataOutputStream dos);
    private final static TypeMessage[] valuesTypeMessage = TypeMessage.values();
    public static Messages readFrom(DataInputStream dis) {
        try {
            switch (valuesTypeMessage[dis.readInt()]) {
                case REQ_MACHINE_TEMP:
                    return new ReplyServer();
                case REP_MACHINE_TEMP:
                    return new ReplyReceived();
                case RECEIVED_REP:
                    return null;
                case BAD_REQ:
                    return null;
                default:
                    return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static class RequestClient extends Messages {
        // peticion del cliente hacia el server

        private static final TypeMessage CLIENTREQUEST = TypeMessage.REQ_MACHINE_TEMP;

        @Override
        public TypeMessage typeMessage() { return CLIENTREQUEST; }
        @Override
        public void writeTo(DataOutputStream dos) {
            int typeM = typeMessage().ordinal();
            try {
                dos.writeInt(typeM);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class ReplyServer extends Messages {
        // respuesta del server hacia el cliente

        private static final TypeMessage SERVERREPLY = TypeMessage.REP_MACHINE_TEMP;

        @Override
        public TypeMessage typeMessage() { return SERVERREPLY;}

        public String getTemperature() {
            String ruta = "/sys/class/thermal/thermal_zone0/temp";
            String temp = null;
            try {
                File f = new File(ruta);
                FileInputStream fs = new FileInputStream(f);
                BufferedReader br = new BufferedReader(new InputStreamReader(fs));
                String line;
                while ((line = br.readLine()) != null) {
                    temp = line;
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return temp;
        }

        /*public static String getTime() {
            String pattern = "yyyy/MM/dd HH:mm:ss";
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime now = LocalDateTime.now();
            return dtf.format(now) + " ";
        }*/

        @Override
        public void writeTo(DataOutputStream dos) {
            int typeM = typeMessage().ordinal();
            String reply;
            try {
                reply = /*getTime()+" "+*/getTemperature();
                System.out.println("ReplyServer: typeM:"+typeM);
                System.out.println("ReplyServer: "+reply);
                dos.writeInt(typeM);
                dos.writeUTF(reply);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static class ReplyReceived extends Messages {

        @Override
        public TypeMessage typeMessage() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void writeTo(DataOutputStream dos) {
            // TODO Auto-generated method stub

        }
        // repuesta del cliente hacia el server de que todo ok
    }

    public static class BadRequest extends Messages {

        @Override
        public TypeMessage typeMessage() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void writeTo(DataOutputStream dos) {
            // TODO Auto-generated method stub

        }
        // peticion del cliente hacia el server
    }

}
