package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class LoginFenetre extends JFrame{
    private static JTextField champNomUtilisateur;
	private JTextField champIp, champPort;

    public LoginFenetre(ClientTetris client){
        // GUI Stuff
        setSize(650, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.BLACK);
        setTitle("Connexion au serveur");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        addWindowListener(new WindowListener() {
            public void windowClosed(WindowEvent e) {
            }

            public void windowOpened(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                client.fermer();
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
            }
        });

        ImageIcon img1 = new ImageIcon("resources/tetronimo.png");
        JLabel lblNewLabel = new JLabel("Connexion");
        //lblNewLabel.setIcon(img1);
        lblNewLabel.resize(81,81);
        lblNewLabel.setBounds(250, 22, 400, 50);
        lblNewLabel.setForeground(Color.white);
        getContentPane().add(lblNewLabel);

        JLabel lblNickname = new JLabel("Pseudo :");
        lblNickname.setBounds(80, 142, 109, 14);
        lblNickname.setForeground(Color.white);
        getContentPane().add(lblNickname);

        JLabel lblIp = new JLabel("Adresse IP:");
        lblIp.setBounds(80, 178, 109, 14);
        lblIp.setForeground(Color.white);
        getContentPane().add(lblIp);

        JLabel lblPort = new JLabel("Port :");
        lblPort.setBounds(335, 178, 41, 14);
        lblPort.setForeground(Color.white);
        getContentPane().add(lblPort);

        champNomUtilisateur = new JTextField();
        champNomUtilisateur.setBounds(180, 140, 200, 24);
        getContentPane().add(champNomUtilisateur);
        champNomUtilisateur.setColumns(10);

        champIp = new JTextField();
        champIp.setColumns(10);
        champIp.setBounds(180, 175, 120, 24);
        champIp.setText("127.0.0.1");
        getContentPane().add(champIp);

        champPort = new JTextField();
        champPort.setColumns(10);
        champPort.setBounds(385, 175, 40, 24);
        champPort.setText("1234");
        getContentPane().add(champPort);

        JLabel lblChooseAPic = new JLabel("Choisir une image :");
        lblChooseAPic.setBounds(78, 95, 109, 14);
        getContentPane().add(lblChooseAPic);

        ImageIcon[] imageIcons = new ImageIcon[5];
        Image[] scaledImgs = new Image[5];
        JButton[] btnImgs = new JButton[5];
        String[] iconSources = {"resources/emojiDevil.png", "resources/emojiFun.png", "resources/emojiHaha.png", "resources/emojiLaugh.png", "resources/emojiSmirk.png"};

        ActionListener imgButtonAC = e -> {
            for(Component comp : getContentPane().getComponents()){
                if(comp.getClass().toString().equals(JButton.class.toString())){
                    comp.setBackground(new JButton().getBackground());
                }
            }
            JButton bt = (JButton) e.getSource();
            bt.setBackground(Color.BLACK);
            client.CheminIcon = iconSources[(bt.getBounds().x - 197) / 42];
        };

        for (int i = 0; i < 5; i++){
            imageIcons[i] = new ImageIcon(iconSources[i]);
            scaledImgs[i] = imageIcons[i].getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btnImgs[i] = new JButton("");
            btnImgs[i].setIcon(new ImageIcon(scaledImgs[i]));
            btnImgs[i].setBounds(197 + 42 * i, 90, 32, 32);
            btnImgs[i].addActionListener(imgButtonAC);
            getContentPane().add(btnImgs[i]);
        }

        JLabel lblWarning = new JLabel();
        lblWarning.setBounds(0,205,500,14);
        lblWarning.setForeground(Color.RED);
        lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(lblWarning);

        JButton btnNewButton = new JButton("Connexion");
        btnNewButton.setBounds(200, 230, 100, 24);
        btnNewButton.addActionListener(e -> {
            if (champNomUtilisateur.getText().equals("")){
                lblWarning.setText("*S'il vous plait entrer votre pseudo");
            } else if (client.CheminIcon == null){
                lblWarning.setText("*Veillez choisir un image svp !");
            } else {
                try {
                    // En attente de connexion
                    System.out.println("En cours de connection..");

                    client.pseudo = champNomUtilisateur.getText();

                    client.socket = new Socket(champIp.getText(), Integer.valueOf(champPort.getText()));  // attempt socket connection (local address). This will wait until a connection is made

                    InputStreamReader stream1 = new InputStreamReader(client.socket.getInputStream()); // stream for network input
                    client.input = new ClientBufferedReader(stream1);
                    client.output = new PrintWriter(client.socket.getOutputStream()); // assign printwriter to network stream

                    client.output.println("**login\n" + champNomUtilisateur.getText() + "\n" + client.CheminIcon);
                    client.output.flush();

                    client.outputOpen = true;
                    while(client.outputOpen){
                        if (client.input.ready()){
                            if (client.input.readLine().equals("**successful")){
                                client.outputOpen = false;
                            }
                        }
                    }

                    System.out.println("Connexion etablie.");
                    dispose();

                    new AttenteFenetre(client).setVisible(true);
                } catch (IOException ex){
                    ex.printStackTrace();
                    // Erreur des connection probable
                    System.out.println("Echec de connexion au serveur");
                    System.exit(-1);
                }
            }
        });
        getContentPane().add(btnNewButton);
    }
}