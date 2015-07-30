import javax.sound.sampled.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class SoundReceiver implements Runnable{
    Socket socket = null;
    DataInputStream soundIn =null;
    SourceDataLine inSpeaker= null;

    public SoundReceiver(Socket conn) throws IOException, LineUnavailableException {
        socket = conn;
        soundIn = new DataInputStream(socket.getInputStream());
        AudioFormat af = new AudioFormat(8000,8,1,true,false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class,af);
        inSpeaker = (SourceDataLine)AudioSystem.getLine(info);
        inSpeaker.open(af);
    }
    public void run(){
        int bytesRead= 0;
        byte[] insound=new byte[1];
        inSpeaker.start();
        while(bytesRead!= -1){
            try {
                bytesRead = soundIn.read(insound,0,insound.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bytesRead>= 0){
                inSpeaker.write(insound,0,bytesRead);
            }
        }
    }
}
public class voice {
    public static void main(String[] args) throws LineUnavailableException, IOException {
        AudioFormat af = new AudioFormat(8000,8,1,true,false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class,af);
        TargetDataLine microfon = (TargetDataLine) AudioSystem.getLine(info);
        microfon.open(af);
        Socket socket = new Socket("127.0.0.1",43333);
        microfon.start();
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        int bytesRead=0;
        byte[] soundData= new byte[1];
        new Thread(new SoundReceiver(socket)).start();
        while(bytesRead!= -1){
            bytesRead = microfon.read(soundData,0,soundData.length);
            if(bytesRead>= 0){
                dos.write(soundData,0,bytesRead);

            }
        }System.out.println("konusma bitii");
    }
}
