import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

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
        while(true){//Mesaj karsiya ulasti
            try {
                System.out.println(new sifreleme().decrypt(client.read.readUTF()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
class mesaj_gonder extends Thread {
    public void run() {
        while(true){//Mesaj karsiya ulasti
            try {
                client.write.writeUTF(new sifreleme().encrypt("Kullanici" + " : " + new Scanner(System.in).next() + "\n"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
class client{
    public static DataOutputStream write;
    public static DataInputStream read;

    public static void main(String args[]) throws IOException {
        Socket soket = new Socket("127.0.0.1", 42222); //Servera baglandim.
        read = new DataInputStream(soket.getInputStream());
        write = new DataOutputStream(soket.getOutputStream()); //Clienta mesaj yollamak icin
        //Eger ki mesaj alsaydik DataInputStream  kullanacaktik.
        new mesaj_oku().start();
        new mesaj_gonder().start();
    }
}
