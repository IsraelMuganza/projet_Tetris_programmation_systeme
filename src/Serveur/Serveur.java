package Serveur;



//importations pour la communication reseau

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Serveur {

    public static void main(String[] args) throws IOException{
        new Serveur().go(); //Demarrer le serveur
    }

    // Socket du serveur
    private ServerSocket serverSock;

    // Booleen pour accepter les connexions au serveur
    private boolean enMarche = true;

    // ArrayLists des objets user et thread user
    private ArrayList<Utilisateur> utilisateurs = new ArrayList<Utilisateur>();
    private ArrayList<Thread> threadsDesClients = new ArrayList<Thread>();
    private ArrayList<SalleDeJeux> salleDeJeux = new ArrayList<SalleDeJeux>();

    private ArrayList<String> messagePseudo = new ArrayList<String>();
    private ArrayList<String> messagesIcon = new ArrayList<String>();
    private ArrayList<String> messagesTime = new ArrayList<String>();
    private ArrayList<String> messages = new ArrayList<String>();

    // File and PrintWriter
    private File messageFile = new File("resources/messages.txt");
    private PrintWriter usersFile = new PrintWriter(new FileWriter(messageFile, true));

    // number of users
    private int userNum = 0;

    //numero de port du socket
    public static final String ADRESSE_IP = "127.0.0.1";
    public static final int NUMERO_PORT = 1234;

    JFrame fenetreServeur;
    JLabel titreLbl;
    JLabel utilisateurLbl;
    JButton boutonQuitter;


    public Serveur() throws IOException{

        // Variable des nombres total d'utilisateur
        try {
            // Assignation du port au serveur
            serverSock = new ServerSocket(NUMERO_PORT);

            // Lire les utilisteurs qui sont dans le fichier texte
            Scanner messagesReader = new Scanner(messageFile);
            while (messagesReader.hasNextLine()){
                messagePseudo.add(messagesReader.nextLine());
                messagesIcon.add(messagesReader.nextLine());
                messagesTime.add(messagesReader.nextLine());
                messages.add(messagesReader.nextLine());
            }
            messagesReader.close();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Fermeture");
                usersFile.close();

                try {
                    PrintWriter messageFilePw = new PrintWriter(messageFile);
                    for (int i = 0; i < messages.size(); i++){
                        if (messages.get(i) != null){
                            messageFilePw.println(messagePseudo.get(i));
                            messageFilePw.println(messagesIcon.get(i));
                            messageFilePw.println(messagesTime.get(i));
                            messageFilePw.println(messages.get(i));
                        }
                    }
                    messageFilePw.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }, "Shutdown-thread"));

            System.out.println("En attente d'un client..");

        } catch (IOException e){
            System.out.println("Echec d'initialisation");
            System.exit(-1);
        }

        fenetreServeur = new JFrame("Serveur multijoueur");
        fenetreServeur.setSize(600, 600);
        fenetreServeur.setResizable(false);
        fenetreServeur.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fenetreServeur.getContentPane().setLayout(null);
        fenetreServeur.getContentPane().setBackground(Color.BLACK);
        fenetreServeur.setAlwaysOnTop(true);

        titreLbl = new JLabel("Tetris serveur");
        titreLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 35));
        titreLbl.setBounds(150, 60, 300, 45);
        titreLbl.setForeground(Color.white);
        titreLbl.setHorizontalAlignment(SwingConstants.CENTER);
        fenetreServeur.getContentPane().add(titreLbl);

        utilisateurLbl = new JLabel("0 Utilisateur en ligne");
        utilisateurLbl.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
        utilisateurLbl.setBounds(150, 135, 300, 20);
        utilisateurLbl.setForeground(Color.white);
        utilisateurLbl.setHorizontalAlignment(SwingConstants.CENTER);
        fenetreServeur.getContentPane().add(utilisateurLbl);

        boutonQuitter = new JButton("Quitter");
        boutonQuitter.setBounds(250, 200, 120, 30);
        boutonQuitter.addActionListener(e -> {
            System.exit(0);
        });
        fenetreServeur.getContentPane().add(boutonQuitter);

        fenetreServeur.setVisible(true);
    }

    /**
     * Methode pour Demarrer le serveur
     */
    public void go() throws IOException{
        // Boucle qui accepte tous les utilisateurs
        Socket tempSocket;
        Utilisateur thisUtilisateur;
        try {
            while (enMarche){
                // Ajout de l'utilisateur a la liste
                tempSocket = serverSock.accept();
                thisUtilisateur = new Utilisateur(tempSocket);
                utilisateurs.add(thisUtilisateur);  // Attente de connexion

                //On cree un thread pour chaque utilisateur
                threadsDesClients.add(new Thread(new ConnexionHandler(thisUtilisateur)));
                threadsDesClients.get(threadsDesClients.size() - 1).start(); //start the new thread

                System.out.println("Nouveau client connecte");
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Connexion non accepte");
            System.exit(-1);
        }
    }

    //***** Classe interne - thread de la connexion du client
    class ConnexionHandler implements Runnable{
        private PrintWriter output; // Assignation de printwriter au flux du reseau
        private ServerBufferedReader input; // Flux de l'entree du reseau
        private Socket client;  // garder le socket du client
        private Utilisateur utilisateur;
        private String inputMessage;

        boolean waiting;

        /**
		 * param user objet contenant les infos du client
		 */
        ConnexionHandler(Utilisateur utilisateur){
            this.utilisateur = utilisateur;
            // affecte la variable du socket
            this.client = utilisateur.socket;

            try {
                //affect les input et output au client
                this.output = new PrintWriter(client.getOutputStream());
                InputStreamReader stream = new InputStreamReader(this.client.getInputStream());
                this.input = new ServerBufferedReader(stream);
            } catch (IOException e){
                e.printStackTrace();
            }
            enMarche = true;
        }

        /**
         * executer
		 * execute au demarrage du thread
		 */
        public synchronized void run(){
            waiting = true;
            while (waiting){
                // Verification pseudo
                try {
                    if (input.ready()){
                        inputMessage = input.readLine();
                        if (inputMessage.equals("**login")){
                            // Takes in the username
                            utilisateur.nickName(input.readLine());
                            utilisateur.setUserIconPic(input.readLine());

                            userNum++;
                            if (userNum == 1){
                                utilisateurLbl.setText("1 Utilisateur en ligne");
                            } else {
                                utilisateurLbl.setText(userNum + " Utilisateurs en ligne");
                            }

                            output.println("**successful");
                            waiting = false;
                        } else {
                            output.println("Invalid Command Received");
                            output.flush();
                        }
                    }
                    // Si il ya rien a envoyer au client
                } catch (IOException e){
                    System.out.println("Aucun nom ni mot de pas recu de la part de l'utilisateur");
                    e.printStackTrace();
                }
            }

            waiting = true;

            // Envoyer tous les messages au client
            for (int i = 0; i < messages.size(); i++){
                output.println("**message\n" +
                        messagePseudo.get(i) + "\n" +
                        messagesIcon.get(i) + "\n" +
                        messagesTime.get(i) + "\n" +
                        messages.get(i));
            }
            output.flush();

            new Thread(() -> {
                while (waiting){    // Boucle pour recevoir les messages du client a tou moment
                    for (int i = 0; i < salleDeJeux.size(); i++){
                        if (salleDeJeux.get(i) != null){
                            output.println("**gameRoom");      // Envoyer tous les infos du GameRoom au client
                            output.println(salleDeJeux.get(i).getGameName());
                            output.println(salleDeJeux.get(i).getGameType());
                            output.println(salleDeJeux.get(i).getPlayersInRoom());
                            output.println(salleDeJeux.get(i).getMaxPlayers());
                            output.flush();
                        }
                    }

                    for (int i = 0; i < utilisateur.getNewMessages().size(); i++){
                        output.println("**message\n" +
                                utilisateur.getNewMessagesNick().get(i) + "\n" +
                                utilisateur.getNewMessagesIcon().get(i) + "\n" +
                                utilisateur.getNewMessagesTime().get(i) + "\n" +
                                utilisateur.getNewMessages().get(i));
                    }
                    output.flush();

                    // Suppression des messages
                    utilisateur.getNewMessagesNick().clear();
                    utilisateur.getNewMessagesIcon().clear();
                    utilisateur.getNewMessagesTime().clear();
                    utilisateur.getNewMessages().clear();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e){
                    }
                }

            }).start();

            new Thread(() -> {
                while (waiting){
                    String userInput, userInput1, userInput2, userInput3, userInput4;
                    // Verfie si l'utilisateur est toujours connecte
                    try {
                        if (input.ready()){
                            // Verifie si l'utilisateur envoie un message
                            //
                            userInput = input.readLine();

                            // Par rapport a ce que l'utilisateur souhaite
                            switch (userInput){
                                case "**sendMessage":    // Envoi du message
                                    userInput1 = input.readLine();  // pseudo
                                    userInput2 = input.readLine();  // icone
                                    userInput3 = input.readLine();  // timeStamp
                                    userInput4 = input.readLine();  // message

                                    for (int i = 0; i < utilisateurs.size(); i++){
                                        if (utilisateurs.get(i) != null && utilisateurs.get(i) != utilisateur){
                                            // Recois et envoie les messages
                                            utilisateurs.get(i).getNewMessagesNick().add(userInput1);
                                            utilisateurs.get(i).getNewMessagesIcon().add(userInput2);
                                            utilisateurs.get(i).getNewMessagesTime().add(userInput3);
                                            utilisateurs.get(i).getNewMessages().add(userInput4);
                                        }
                                    }
                                    messagePseudo.add(userInput1);
                                    messagesIcon.add(userInput2);
                                    messagesTime.add(userInput3);
                                    messages.add(userInput4);
                                    break;

                                case "**createRoom":
                                    userInput1 = input.readLine();  // Nom du jeu
                                    userInput2 = input.readLine();  // Type de jeu

                                    salleDeJeux.add(new SalleDeJeux(userInput1, userInput2));
                                    break;

                                case "**enterRoomAndPlay":
                                    userInput1 = input.readLine();  // nom de la salle

                                    for (int i = 0; i < salleDeJeux.size(); i++){
                                        if (salleDeJeux.get(i).getGameName().equals(userInput1)){
                                            salleDeJeux.get(i).enterPlayer(utilisateur, output);
                                        }
                                    }
                                    break;

                                case "**game":
                                    userInput1 = input.readLine();  // nom de la salle
                                    userInput2 = input.readLine();  // commande
                                    userInput3 = input.readLine();  // donnee

                                    for (SalleDeJeux salleDeJeux : salleDeJeux){
                                        if (salleDeJeux.getGameName().equals(userInput1)){
                                            salleDeJeux.handleCommand(utilisateur, userInput2, userInput3);
                                        }
                                    }
                                    break;

                                case "**leaveRoom":
                                    userInput1 = input.readLine();  // nom de la salle
                                    for (SalleDeJeux salleDeJeux : salleDeJeux){
                                        if (salleDeJeux.getGameName().equals(userInput1)){
                                            salleDeJeux.removeUser(utilisateur);
                                        }
                                    }
                                    break;

                                case "**close":
                                    utilisateurs.remove(utilisateur);
                                    userNum--;
                                    if (userNum == 1){
                                        utilisateurLbl.setText("1 utilisateur en ligne");
                                    } else {
                                        utilisateurLbl.setText(userNum + " utilisateurs en ligne");
                                    }

                                    break;

                                default:
                                    output.println("Invalid Command Received");
                                    output.flush();
                                    break;
                            }
                        }
                    } catch (IOException e){
                        System.out.println("Echec de reception du message du client");
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e){
                    }
                }
            }).start();

        }
    }

    /**
     *Lecteur du serveur
     * Lire les utilisateurs
     *Peut afficher les messages de utilisateurs si necessaire.
     */
    class ServerBufferedReader extends BufferedReader{
        public ServerBufferedReader(Reader in){
            super(in);
        }

        @Override
        public String readLine() throws IOException{
            String line = super.readLine();
            System.out.println("Client: " + line);
            return line;
        }
    }
}