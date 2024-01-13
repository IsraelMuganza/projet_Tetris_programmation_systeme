package Jeu.Jeux;

import Jeu.PanneauTransparent;
import Jeu.TableauDeJeux.DoublePlayerTableauJeu;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;

import static java.awt.event.KeyEvent.*;


public class JeuEnLigne extends JeuxDeuxJoueurs {

    public JeuEnLigne(String pseudo, String cheminIcon, PrintWriter output){
        super(pseudo, cheminIcon);

        this.output = output;

        nomTableauJeu = new DoublePlayerTableauJeu();
        nomTableauJeu.setBounds(170, 155, 338, 546);
        getContentPane().add(nomTableauJeu);

        opponentTableauJeu = new DoublePlayerTableauJeu();
        opponentTableauJeu.setBounds(170 + 680, 155, 338, 546);
        getContentPane().add(opponentTableauJeu);

        addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e){

            }

            @Override
            public void keyPressed(KeyEvent e){
                if (!(ready)){
                    ready = true;
                    output.println("**game\n" + gameName + "\n" + "getOpponent\n" + pseudo + " " + cheminIcon);
                    output.flush();
                }

                if (playing){
                    switch (e.getKeyCode()){
                        case VK_UP:
                        case VK_W:
                            nomTableauJeu.rotate();
                            output.println("**game\n" + gameName + "\n" + "rotate\n ");
                            output.flush();
                            break;
                        case VK_DOWN:
                        case VK_S:
                            nomTableauJeu.moveDown();
                            output.println("**game\n" + gameName + "\n" + "moveDown\n ");
                            output.flush();
                            myScore++;
                            break;
                        case VK_LEFT:
                        case VK_A:
                            nomTableauJeu.move(-1);
                            output.println("**game\n" + gameName + "\n" + "move\n-1");
                            output.flush();
                            break;
                        case VK_RIGHT:
                        case VK_D:
                            nomTableauJeu.move(1);
                            output.println("**game\n" + gameName  + "\n" + "move\n1");
                            output.flush();
                            break;
                        case VK_N:
                        case VK_SHIFT:
                            nomTableauJeu.holdSwitch = true;
                            output.println("**game\n" + gameName + "\n" + "hold\n ");
                            output.flush();
                            break;
                        case VK_SPACE:
                        case VK_CONTROL:
                            myScore += nomTableauJeu.moveToBottomUp2();
                            output.println("**game\n" + gameName + "\n" + "drop\n ");
                            output.flush();
                            break;
                    }
                } else {
                    if (e.getKeyCode() == VK_P){
                        playing = !playing;
                        output.println("**game\n" + gameName + "\n" + "pause\n ");
                        output.flush();
                        if (playing){
                            disposePauseScreen();
                        } else {
                            setupPauseScreen();
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e){

            }
        });
    }

    public void getAdversaire(String pseudo, String iconPath){
        opponentIcon.setText(pseudo);
        opponentIcon.setIcon(new ImageIcon(iconPath));
    }

    public void handleAdversaire(String command, String data){
        switch (command){
            case "getOpponent":
                getAdversaire(data.substring(0, data.lastIndexOf(' ')), data.substring(data.lastIndexOf(' ') + 1));
                opponentReady = true;
                break;
            case "pause":
                playing = !playing;
                if (playing){
                    disposePauseScreen();
                } else {
                    setupPauseScreen();
                }
                break;
            case "move":
                opponentTableauJeu.move(Integer.valueOf(data));
                break;
            case "moveDown":
                opponentTableauJeu.moveDown();
                opponentScore++;
                break;
            case "rotate":
                opponentTableauJeu.rotate();
                break;
            case "hold":
                opponentTableauJeu.holdSwitch = true;
                break;
            case "drop":
                opponentTableauJeu.moveToBottomUp2();
                break;
            default:
                System.out.println("Invalid command received");
                break;
        }
    }

    /**
     * reglage de l'ecran du joueur en attente
     */
    @Override
    public void setupMyReadyScreen(){
        PanneauTransparent waiting = new PanneauTransparent();
        waiting.setBounds(0, 0, 680, 768);
        getLayeredPane().add(waiting, 30);
        waiting.addCenterMessage("Pret ?");
        waiting.addSmallMessage("Appuie un bouton pour continuer");

        while (!ready){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        getLayeredPane().remove(waiting);
        repaint();
        revalidate();
    }

    /**
     * reglage de l'ecran d'attente de l'adversaire
     * appreterLecranAdversaire
     */
    @Override
    void setupOpponentReadyScreen(){
        PanneauTransparent waiting = new PanneauTransparent();

        waiting.setBounds(680, 0, 680, 768);
        getLayeredPane().add(waiting, 30);
        waiting.addCenterMessage("Une seconde...");
        waiting.addSmallMessage("En attente d'un adversaire...");

        while (!opponentReady){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        getLayeredPane().remove(waiting);
        repaint();
        revalidate();

        waiting = new PanneauTransparent();

        waiting.setBounds(680, 0, 680, 768);
        getLayeredPane().add(waiting, 30);
        waiting.addCenterMessage("Allons-y !");
        waiting.addSmallMessage("L'adversaire est pret !");

        while (!ready){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        getLayeredPane().remove(waiting);
        repaint();
        revalidate();
    }

    /**
     * reglage de l'ecran game over
     */
    @Override
    public void setupMyGameOverScreen(){
        String message = opponentGameOver ? "Victoire!!!" : "Defaite!!!";

        PanneauTransparent waiting = new PanneauTransparent();
        waiting.setBounds(0, 0, 680, 768);
        waiting.gameOver(message, myScore, myLines, level, true,
                new String[][]{
                        {"", ""},
                        {"", ""},
                        {"", ""},
                        {"", ""},
                        {"", ""}});
        getLayeredPane().add(waiting, 30);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        waiting.requestFocus();
        waiting.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e){
            }

            @Override
            public void keyPressed(KeyEvent e){
            }

            @Override
            public void keyReleased(KeyEvent e){
                gameOver = true;
                dispose();
                output.println("**leaveRoom\n" + gameName);
                output.flush();
            }
        });

        while (true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * reglage de l'ecran gameOver de l'adversaire.
     */
    @Override
    public void setupOpponentGameOverScreen(){
        String message = gameOver ? "Victoire!!!" : "Defaite!!!";

        PanneauTransparent waiting = new PanneauTransparent();
        waiting.setBounds(680, 0, 680, 768);
        waiting.gameOver(message, opponentScore, opponentLines, level, true,
                new String[][]{
                        {"", ""},
                        {"", ""},
                        {"", ""},
                        {"", ""},
                        {"", ""}});
        getLayeredPane().add(waiting, 30);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        waiting.requestFocus();
        waiting.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e){
            }

            @Override
            public void keyPressed(KeyEvent e){
            }

            @Override
            public void keyReleased(KeyEvent e){
                gameOver = true;
                dispose();
                output.println("**leaveRoom\n" + gameName);
                output.flush();
            }
        });

        while (true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void setupRewardModeScreen(int monNiveauScore, int adversaireNiveauScore){
        iWonLevel = (monNiveauScore - adversaireNiveauScore) > 0;

        PanneauTransparent waiting = new PanneauTransparent();
        waiting.setBounds(iWonLevel ? 0 : 680, 0, 680, 768);
        getLayeredPane().add(waiting, 30);
        waiting.addCenterMessage("Recompense");
        waiting.addSmallMessage(iWonLevel ?
                "<html><p align=\"center\">Ton score en ce niveau est : " + monNiveauScore +
                        "<br>Le score de l'ordinateur en ce niveau: " + adversaireNiveauScore +
                        "<p></html>"
                : "Une seconde... L'adversaire joue encore...");

        repaint();

        while (rewardMode){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        getLayeredPane().remove(waiting);
        repaint();
        revalidate();
    }
}
