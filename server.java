package com.serverapps;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

class sifreleme {
    public static String secret = "TheBestSecretKey";
    private static final String ALGO = "AES";
    private static final byte[] keyValue = secret.getBytes();

    public static String encrypt(String Data) throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }
    public static String decrypt(String encryptedData) throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
    public static void main(String[] args) throws Exception {

    }
}
class mesaj_oku extends Thread {

    mesaj_oku() {

    }

    public void run() {
        server s = new server();
        sifreleme cryto = new sifreleme();

        try {

            while(true){//Mesaj karsiya ulasti
                String okunanMesaj = cryto.decrypt(s.read.readUTF()); // gelen mesaji okuduk.
                System.out.println(okunanMesaj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

class mesaj_gonder extends Thread {
    public static Scanner input = new Scanner(System.in);
    mesaj_gonder() {

    }

    public void run() {
        server s = new server();
        sifreleme cryto = new sifreleme();



        try {
            while(true){//Mesaj karsiya ulasti
                String mesaj = input.next();
                s.write.writeUTF(cryto.encrypt(s.admin+" : " + mesaj + "\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

public class server {
    public static Scanner input = new Scanner(System.in);

    public static String kullanici,admin;
    public static DataOutputStream write;
    public static DataInputStream read;



    public static void main(String[] args) throws IOException {
        try {
            System.out.println("Kullanici adiniz: ");
            admin= input.next();
            ServerSocket server = new ServerSocket(42222); // portu dinlenilmeye

            System.out.println("Client Bekleniyor");
            Socket client=server.accept(); // Su an bize client  bekliyoruz.

            System.out.println("Client baglandi "+client.getLocalAddress()); // Client baglandi


            read = new DataInputStream(client.getInputStream());
            write = new DataOutputStream(client.getOutputStream()); //Clienta mesaj yollamak icin
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        mesaj_oku oku= new mesaj_oku();
        oku.start();
        mesaj_gonder gonder=new mesaj_gonder();
        gonder.start();

    }

}
