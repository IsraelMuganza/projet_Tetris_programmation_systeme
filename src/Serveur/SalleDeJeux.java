package Serveur;

import Jeu.queue.Queue;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;


public class SalleDeJeux {
    private String NomDuJeux;
    private String TypeDeJeux;

    private int JoueursDansLaSalle;
    private int JoueursMax;

    protected ArrayList<Utilisateur> Joueurs = new ArrayList<Utilisateur>();
    protected ArrayList<PrintWriter> joueurOutputs = new ArrayList<PrintWriter>();

    private ArrayList<Queue<Character>> FileDesFormes = new ArrayList<Queue<Character>>();

    public SalleDeJeux(String gameName, String gameType){
        this.NomDuJeux = gameName;
        this.TypeDeJeux = gameType;

        this.JoueursMax = (gameType.equals("Online Battle")) ? 3 : 1;
        this.JoueursDansLaSalle = 0;
    }

    /**
     * enter a player
     * @param utilisateur player
     * @param output output
     */
    public synchronized void enterPlayer(Utilisateur utilisateur, PrintWriter output){
        for (Utilisateur player : Joueurs){
            if (player == utilisateur){
                output.println("**permission to play\n" + NomDuJeux);
                output.flush();

                for (Utilisateur joueur: Joueurs){
                    if (joueur != utilisateur){
                        output.println("**onlineGame\n" + NomDuJeux + "\ngetOpponent\n" + joueur.getNickName() + " " + joueur.getUserIconPic());
                    }
                }
                return;
            }
        }

        output.println("**permission to play\n" + NomDuJeux);
        output.flush();

        for (Utilisateur player: Joueurs){
            output.println("**onlineGame\n" + NomDuJeux + "\ngetOpponent\n" + player.getNickName() + " " + player.getUserIconPic());
        }

        Joueurs.add(utilisateur);
        joueurOutputs.add(output);
        FileDesFormes.add(new Queue<Character>());
        JoueursDansLaSalle++;
    }


    public synchronized void handleCommand(Utilisateur utilisateur, String command, String data){
        if (command.equals("requestTetromino")){
            Character tetrominoChar = new Character[]{'I', 'S', 'O', 'L', 'Z', 'T', 'J'}[new Random().nextInt(7)];
            char mychar;
            for (int i = 0; i < Joueurs.size(); i++){
                FileDesFormes.get(i).enqueue(tetrominoChar);

                System.out.println(" -- fait attendre " + Joueurs.get(i).nickName() + " un " + tetrominoChar);

                if (Joueurs.get(i) == utilisateur){
                    mychar = FileDesFormes.get(i).dequeue();
                    joueurOutputs.get(i).println("**onlineGame\n" + NomDuJeux + "\nenqueueTetromino\n" + mychar);
                    System.out.println(" -- a envoye " + utilisateur.nickName() + " u  " + mychar);
                    joueurOutputs.get(i).flush();
                }
            }

        } else {
            for (int i = 0; i < Joueurs.size(); i++){
                if (Joueurs.get(i) != utilisateur){
                    joueurOutputs.get(i).println("**onlineGame\n" + NomDuJeux + "\n" + command + "\n" + data);
                    joueurOutputs.get(i).flush();
                }
            }
        }
    }

    public void removeUser(Utilisateur utilisateur){
        for (int i = 0; i < Joueurs.size(); i++){
            if (Joueurs.get(i) == utilisateur){
                Joueurs.remove(i);
                joueurOutputs.remove(i);
                JoueursDansLaSalle--;
            }
        }
    }

    public String getGameName(){
        return NomDuJeux;
    }

    public String getGameType(){
        return TypeDeJeux;
    }

    public int getPlayersInRoom(){
        return JoueursDansLaSalle;
    }

    public int getMaxPlayers(){
        return JoueursMax;
    }
}
