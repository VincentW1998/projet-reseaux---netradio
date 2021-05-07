import java.net.*;
import java.io.*;
import java.util.*;

public class Service_Diffuser implements Runnable{
    Socket client;
    Service_multidiff sm;
    BufferedReader br;
    PrintWriter pw;
    static LinkedList<Message> msgs = new LinkedList<Message> ();
    static LinkedList<String> listFiles = new LinkedList<String>();

    public Service_Diffuser(Socket c, Service_multidiff servM){
        try{
            client = c;
            sm = servM;
            br = new BufferedReader(new InputStreamReader(c.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(c.getOutputStream()));
        }
        catch(Exception e){
            e.printStackTrace();
        }    
    }

    public void mess(String [] request){
        if(request.length != 3 || request[1].length() != 8 || request[2].length() != 140){
            System.out.println("error in request");
            return;
        }
        synchronized(msgs){
            if(msgs.size()>=99){
                sm.remove(msgs.getFirst());
                msgs.removeFirst();
            }
            Message msg = new Message(request[1], request[2]);     
            msgs.add(msg);
            sm.add(msg);
        }
        pw.print("ACKM\r\n");
        pw.flush();
    }

    public String addZero(int n) {
        String num_mess = String.valueOf(n);
        if (n < 10) {
            num_mess = "000" + num_mess;
        }
        else if (n < 100) {
            num_mess = "00" + num_mess;
        }
        else {
            num_mess = "0" + num_mess;
        }
        return num_mess;
    }

    public void last(String request []){
        try {
            int nbMess = Integer.parseInt(request[1]);
            if(request.length != 2 || nbMess > 9999 || nbMess < 0){
                System.out.println("error in last message composition");
                return;
            }
            synchronized(msgs){
                System.out.println("--- " + msgs.size() + " messages ---");
                if(nbMess > msgs.size())
                    nbMess = msgs.size();
                for(int i=0; i<nbMess; i++){
                    pw.print("OLDM " + addZero(i+1)+" "+msgs.get(i).toString()+"\r\n");
                    pw.flush();
                }
            }
            pw.print("ENDM\r\n");
        }
        catch(Exception e){
            System.out.println("error in last message composition");
            e.printStackTrace();
            return;
        }
    }

    // Stock the path file into list and print this list
    public void stockFile() {
        try {
            String fileName = br.readLine();
            System.out.println("Received this file : " + fileName);
            fileName = Diffuser.fill_hashtag_or_zero(fileName, 25, "#");
            System.out.println("Name :" + fileName);
            listFiles.add(fileName);
        }
        catch(Exception e) {
            System.out.println("Error readline");
        }
    }

    public void listFiles() {
        synchronized(listFiles){
            System.out.println("REQUEST : LISTFILES\r\n");
            try {
                pw.write("NBFI "+String.format("%02d", listFiles.size())+"\n");
                for(String fileName : listFiles) {
                    pw.write(fileName);
                }
                pw.flush();
                System.out.println("done");
            }
            catch (Exception e) {
                System.out.println("Error send listFiles !");
            }
        }
    }





    
    public void diff(){
        try{
            String message = br.readLine();
            System.out.println(message);
            String str [] = message.split(" ",3);
            switch(str[0]){
                case "MESS":
                    mess(str);
                    return;

                case "LAST":
                    last(str);
                    return;
                case "FILEREG" :
                    stockFile();
                    break;
                case "LISTFILES":
                    listFiles();
                    break;
                default:
                    return;
            }
        }
        catch(Exception e){
            System.out.println("error in diff");
            e.printStackTrace();
        }
    }

    public void run(){
        try{
            diff();
            pw.close();
            br.close();
            client.close();
        }
        catch(Exception e){
            System.out.println("error in run : Service_Diffuser.java");
            e.printStackTrace();
        }
    }
}
