import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

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
class client extends JPanel{
    static DataOutputStream write;
    static DataInputStream read;
    static JTextArea mesajarea;
    client() throws Exception {
        setLayout(new BorderLayout());
        mesajarea = new JTextArea(8,40);
        mesajarea.setEditable(false);
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
        JTextField mesajgonder= new JTextField("mesaj yazin");
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
        mesajgonder.addActionListener(actionEvent -> {
            try {
                write.writeUTF(new sifreleme().encrypt("Kullanici" + " : " + mesajgonder.getText()+"\n"));
                mesajarea.append("Kullanici" + " : " + mesajgonder.getText()+"\n");
                mesajgonder.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //butonlar
        JButton addButton = new JButton("Gonder");
        JButton removeButton = new JButton("temizle");
        JButton sesgonder = new JButton("Bas-Konus");
        addButton.addActionListener(e -> {
            try {
                write.writeUTF(new sifreleme().encrypt("Kullanici" + " : " + mesajgonder.getText()+"\n"));
                mesajarea.append("Kullanici" + " : " + mesajgonder.getText() + "\n");
                mesajgonder.setText("");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        removeButton.addActionListener(e -> mesajarea.removeAll());
        add(new JScrollPane(mesajarea), BorderLayout.NORTH);
        add(addButton, BorderLayout.EAST);
        add(removeButton, BorderLayout.WEST);
        add(mesajgonder, BorderLayout.CENTER);
    }
    public static void main(String args[]) throws Exception {
        try {
            Socket soket = new Socket(JOptionPane.showInputDialog("Baglanilacak sunucu"), 42222);
            read = new DataInputStream(soket.getInputStream());
            write = new DataOutputStream(soket.getOutputStream());
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        JFrame frame= new JFrame("Sifreli chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new client());
        frame.setSize(690, 250);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
