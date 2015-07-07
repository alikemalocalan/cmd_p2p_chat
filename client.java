package com.company;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
    public void baglan() throws UnknownHostException, IOException, ClassNotFoundException
    {
        String ip="127.0.0.1"; // Hangi servera baglanmak istedigimizi yaziyoruz. Ben local deki baglanacagim
        // bundan dolayi kendi ip mi yazdim.

        Socket soket = new Socket(ip, 42222); //Servera baglandim. 2347 portundan

        DataInputStream read = new DataInputStream(soket.getInputStream()); // Bana mesaj gelecegi icin DataInputStream i kullandim.
        //Mesaj Gonderecek olsam DataOutputStreami kullanirdim.

        while(true){
            String okunanMesaj = read.readUTF(); // gelen mesaji okuduk.
            System.out.println(okunanMesaj);
        }
    }
    public static void main(String[] args) throws ClassNotFoundException {
        client cl = new client();
        try {
            cl.baglan();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
