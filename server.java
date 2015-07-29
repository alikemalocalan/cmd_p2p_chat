import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.Scanner;

class sifreleme {
    public static String secret = "theBestSecretKey";
    private static final String ALGO = "AES";

    String encrypt(String Data) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret.getBytes(), ALGO));
        return new BASE64Encoder().encode(c.doFinal(Data.getBytes()));
    }
    String decrypt(String encryptedData) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secret.getBytes(), ALGO));
        return new String(c.doFinal(new BASE64Decoder().decodeBuffer(encryptedData)));
    }
}
class mesaj_oku extends Thread {
    public void run(){
        while(true){
            try {
                System.out.println(new sifreleme().decrypt(new server().read.readUTF()));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("kapatmak için bekleniyor");
                new Scanner(System.in).next();
            }
        }
    }
}
class mesaj_gonder extends Thread {
    public void run() {
        while(true){//Mesaj karsiya ulasti
            try {
                new server().write.writeUTF(new sifreleme().encrypt("SERVER MESAJ" + " : " + new Scanner(System.in).next() + "\n"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
class server {
    public static DataInputStream read;
    public static DataOutputStream write;
    public static Socket client;

    public static void main(String[] args) throws Exception {
        try {
            client=new ServerSocket(42222).accept();
            System.out.println(client.getLocalAddress());
            read = new DataInputStream(client.getInputStream());
            write= new DataOutputStream(client.getOutputStream());
        }

         catch (Exception e) {
            e.printStackTrace();
        }

        new mesaj_oku().start();
        new mesaj_gonder().start();

}
}
