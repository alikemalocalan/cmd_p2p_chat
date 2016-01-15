import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
class sifreleme {
    public static String secret = "theBestSecretKey"; //16 haneli sifre secmelisiniz
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
class client extends JPanel{
    public static Socket soket;
    public static Socket soketses;
    public static TargetDataLine microfon=null;
    public static AudioFormat af = new AudioFormat(8000,8,1,true,false);
    DataOutputStream write = new DataOutputStream(soket.getOutputStream());
    DataInputStream read = new DataInputStream(soket.getInputStream());
    static JTextArea mesajarea;
    JTextField mesajgonder;

    client() throws Exception {
        setLayout(new BorderLayout());
        JButton addButton = new JButton("Gonder");
        JButton removeButton = new JButton("temizle");
        JButton sesgonder = new JButton("Bas-Konus");
        mesajarea = new JTextArea(8,40);
        mesajarea.setEditable(false);
        mesajgonder= new JTextField("mesaj yazin");
        mesajgonder.addMouseListener(new MouseInputListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                mesajgonder.setText("");
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }
        });
        mesajgonder.addActionListener(actionEvent -> mesajgonder());

        addButton.addActionListener(e -> mesajgonder());
        removeButton.addActionListener(e -> mesajarea.removeAll());
        sesgonder.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ALT) {
                    microfonuac();
                }
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ALT) {
                    microfonuac();
                    System.out.println("Alt tusuna basildi");
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ALT) {
                    microfonuac();
                }
            }
        });
        sesgonder.addMouseListener(new MouseInputListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                microfonuac();
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                mikrofonukapa();
            }
        });
        add(new JScrollPane(mesajarea), BorderLayout.NORTH);
        add(addButton, BorderLayout.AFTER_LINE_ENDS);
        add(sesgonder, BorderLayout.WEST);
        add(mesajgonder, BorderLayout.CENTER);
        add(removeButton,BorderLayout.EAST);
        Thread worker = new Thread() {
            public void run() {
                try {
                    while(true){
                        mesajarea.append(new sifreleme().decrypt(read.readUTF()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.start();
    }
    private void mesajgonder() {
        try {
            write.writeUTF(new sifreleme().encrypt("SERVER MESAJİ" + " : " + mesajgonder.getText()+"\n"));
            mesajarea.append("SERVER MESAJİ" + " : " + mesajgonder.getText() + "\n");
            mesajgonder.setText("");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    private void mikrofonukapa() {
        try {
            microfon.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void microfonuac() {
        try {
            microfon.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]) throws Exception {
        String ip =JOptionPane.showInputDialog("Baglanilacak sunucu");
        try {
            soket = new Socket(ip,43333);
        }
        catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("baglanti koptu");
        }
        soketses= new Socket(ip,42222);
        JFrame frame= new JFrame("Sifreli chat-Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new client());
        frame.setSize(690, 250);
        frame.setVisible(true);
        frame.setResizable(false);

        microfon = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class,af));
        microfon.open(af);
        DataOutputStream dos = new DataOutputStream(soketses.getOutputStream());
        int bytesRead=0;
        byte[] soundData= new byte[1];
        new Thread(new SoundReceiver()).start();
        while(bytesRead!= -1){
            bytesRead = microfon.read(soundData,0,soundData.length);
            if(bytesRead>= 0){
                dos.write(soundData,0,bytesRead);
            }
        }
    }
}
class SoundReceiver implements Runnable{
    SourceDataLine inSpeaker= null;
    public SoundReceiver() throws IOException, LineUnavailableException {
        inSpeaker = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class,client.af));
        inSpeaker.open(client.af);
    }
    public void run(){
        int bytesRead= 0;
        byte[] insound=new byte[1];
        inSpeaker.start();
        while(bytesRead!= -1){
            try {
                bytesRead = new DataInputStream(client.soketses.getInputStream()).read(insound, 0, insound.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bytesRead>= 0){
                inSpeaker.write(insound,0,bytesRead);
            }
        }
    }
}
