package client;

import Jeu.Jeux.JeuTetris;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientTetris {
    //
    private LoginFenetre login;
    protected JeuTetris jeu;

    protected Socket socket;
    protected ClientBufferedReader input;
    protected PrintWriter output;

    protected boolean outputOpen;

    protected String pseudo;
    protected String CheminIcon;


    public static void main(String[] args) throws IOException{
        ClientTetris client = new ClientTetris();
        client.start();
    }


    public void start() throws IOException{
        login = new LoginFenetre(this);     // ajuster et afficher l'interface
        login.setVisible(true);
    }

    public void fermer() {
        try {  //fermeture de tous les sockets apres avoir quitte la boucle principale
            this.output.println("**close");       // Envoi du message de fermeture au serveur
            this.output.flush();

            try {
                Thread.sleep(1000);     // En attente d'une reponse speciale de la part du serveur
            } catch (InterruptedException e) {
            }

            this.outputOpen = false;       // Arret de reception des message du serveur

            input.close();         // Fermeture des sockets
            output.close();
            socket.close();

            System.exit(0);
        } catch (Exception e) {
            System.out.println("Echec de fermeture du socket");
        }
    }
}