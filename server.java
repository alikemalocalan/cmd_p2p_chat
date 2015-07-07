package com.serverapps;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class server {
    public static Scanner input = new Scanner(System.in);

    public static String kullanici,admin;
    public static DataOutputStream write;
    public void baglan() throws IOException
    {
        System.out.println("Kullanici adiniz: ");
        admin= input.next();
        ServerSocket server = new ServerSocket(42222); //2347 portu dinlenilmeye
        System.out.println("Client Bekleniyor");
        Socket client=server.accept(); // Su an bize client  bekliyoruz.

        System.out.println("Client baglandi "+client.getLocalAddress()); // Client baglandi



        write = new DataOutputStream(client.getOutputStream()); //Clienta mesaj yollamak icin
        //Eger ki mesaj alsaydik DataInputStream  kullanacaktik.

        write.writeUTF(admin+ mesaj_gonder()); //Mesaj karsiya ulasti


    }
    public static void main(String[] args) {


        try {
            server s= new server();
            s.baglan();
        } catch (IOException ex) {

        }
    }
    public  String mesaj_gonder() throws IOException {
        String mesaj = input.next();
        try {
            write.writeUTF(admin +" : "+ mesaj+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  mesaj_gonder(); //Mesaj karsiya ulasti
    }

}

