package alonsod.mov.urjc.thermos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    public static TypeMessage getTypeMessage(int pos) {
        return valuesTypeMessage[pos];
    }
    public static Messages readFrom(DataInputStream dis) {
        try {
            switch (getTypeMessage(dis.readInt())) {
                case REQ_MACHINE_TEMP:
                    String machine = dis.readUTF();
                    return new ReplyServer(machine);
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
        String machine;
        private static final TypeMessage CLIENTREQUEST = TypeMessage.REQ_MACHINE_TEMP;
        RequestClient(String machine){
            this.machine = machine;
        }
        @Override
        public TypeMessage typeMessage() { return CLIENTREQUEST; }
        @Override
        public void writeTo(DataOutputStream dos) {
            int typeM = typeMessage().ordinal();
            try {
                dos.writeInt(typeM);
                dos.writeUTF(machine);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class ReplyServer extends Messages {
        // respuesta del server hacia el cliente
        String machine;
        private static final TypeMessage SERVERREPLY = TypeMessage.REP_MACHINE_TEMP;

        ReplyServer(String machine) {
            this.machine = machine;
        }
        @Override
        public TypeMessage typeMessage() { return SERVERREPLY;}

        public String getTemperature() {
            String temperature = "None";
            String cmd = "cat /sys/class/thermal/thermal_zone0/temp";
            String ssh_cmd = "ssh -i ~/.ssh/android_ed25519 -o StrictHostKeyChecking=no alonsod@"+machine+" "+cmd;
            Runtime rt = Runtime.getRuntime();
            try {
                Process p = rt.exec(ssh_cmd);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    temperature = line + "\n";
                }

                int exitVal;
                exitVal = p.waitFor();
                if (exitVal == 0) {
                    System.out.println("Success!");
                    System.out.println(temperature);
                }
                return temperature;

            } catch (IOException e) {
                e.printStackTrace();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            return temperature;
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
            try {
                dos.writeInt(typeM);
                dos.writeUTF(/*getTime()+" "+*/getTemperature());
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
