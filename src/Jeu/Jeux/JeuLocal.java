package Jeu.Jeux;

import Jeu.PanneauTransparent;
import Jeu.TableauDeJeux.DoublePlayerTableauJeu;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;

import static java.awt.event.KeyEvent.*;


public class JeuLocal extends JeuxDeuxJoueurs {
    public JeuLocal(String pseudo, String cheminIcon, PrintWriter output){
        super(pseudo, cheminIcon);

        this.output = output;

        nomTableauJeu = new DoublePlayerTableauJeu();
        nomTableauJeu.setBounds(170, 155, 338, 546);
        getContentPane().add(nomTableauJeu);

        opponentTableauJeu = new DoublePlayerTableauJeu();
        opponentTableauJeu.setBounds(170 + 680, 155, 338, 546);
        getContentPane().add(opponentTableauJeu);

        opponentIcon.setText(pseudo);
        opponentIcon.setIcon(new ImageIcon(cheminIcon));

        this.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e){

            }

            @Override
            public void keyPressed(KeyEvent e){
                if (!(ready && opponentReady)){
                    switch (e.getKeyCode()){
                        case VK_Q:
                            ready = true;
                            break;
                        case VK_M:
                            opponentReady = true;
                            break;
                    }
                }

                if (playing){
                    switch (e.getKeyCode()){
                        case VK_UP:
                            opponentTableauJeu.rotate();
                            break;
                        case VK_W:
                            nomTableauJeu.rotate();
                            break;
                        case VK_DOWN:
                            opponentTableauJeu.moveDown();
                            opponentScore++;
                            break;
                        case VK_S:
                            nomTableauJeu.moveDown();
                            myScore++;
                            break;
                        case VK_LEFT:
                            opponentTableauJeu.move(-1);
                            break;
                        case VK_A:
                            nomTableauJeu.move(-1);
                            break;
                        case VK_RIGHT:
                            opponentTableauJeu.move(1);
                            break;
                        case VK_D:
                            nomTableauJeu.move(1);
                            break;
                        case VK_N:
                            opponentTableauJeu.holdSwitch = true;
                            break;
                        case VK_SHIFT:
                            nomTableauJeu.holdSwitch = true;
                            break;
                        case VK_SPACE:
                            opponentScore += opponentTableauJeu.moveToBottomUp2();
                            break;
                        case VK_CONTROL:
                            myScore += nomTableauJeu.moveToBottomUp2();
                            break;
                    }
                }

                if (rewardMode){
                    switch (e.getKeyCode()){
                        case VK_K:
                            if (!iWonLevel){
                                ((DoublePlayerTableauJeu) opponentTableauJeu).gravityDrop();
                                rewardMode = false;
                                playing = true;
                            }
                            break;
                        case VK_Z:
                            if (iWonLevel){
                                ((DoublePlayerTableauJeu) nomTableauJeu).gravityDrop();
                                rewardMode = false;
                                playing = true;
                            }
                            break;
                        case VK_L:
                            if (!iWonLevel){
                                new Thread(() -> {
                                    ((DoublePlayerTableauJeu) nomTableauJeu).deployRewardPiece();
                                    requestFocus();
                                    rewardMode = false;
                                    setupCountDownScreen();
                                    playing = true;
                                }).start();
                            }
                            break;
                        case VK_X:
                            if (iWonLevel){
                                new Thread(() -> {
                                    ((DoublePlayerTableauJeu) opponentTableauJeu).deployRewardPiece();
                                    requestFocus();
                                    rewardMode = false;
                                    setupCountDownScreen();
                                    playing = true;
                                }).start();
                            }
                            break;
                    }
                } else {
                    if (e.getKeyCode() == VK_P){
                        playing = !playing;
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

    @Override
    public void setupMyReadyScreen(){
        PanneauTransparent waiting = new PanneauTransparent();
        waiting.setBounds(0, 0, 680, 768);
        getLayeredPane().add(waiting, 30);
        waiting.addCenterMessage("Pret ?");
        waiting.addSmallMessage("Appuie sur Q pour continuer");

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

    @Override
    public void setupOpponentReadyScreen(){
        PanneauTransparent waiting = new PanneauTransparent();
        waiting.setBounds(680, 0, 680, 768);
        getLayeredPane().add(waiting, 30);
        waiting.addCenterMessage("Pret ?");
        waiting.addSmallMessage("Appuie sur M pour continuer");

        while (!opponentReady){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        getLayeredPane().remove(waiting);
        repaint();
        revalidate();
    }

    @Override
    public void setupMyGameOverScreen(){
        String message = opponentGameOver ? "Victoire!!" : "Defaite!!!";

        PanneauTransparent waiting = new PanneauTransparent();
        waiting.setBounds(0, 0, 680, 768);
        waiting.gameOver(message, myScore, myLines, level, opponentGameOver,
                new String[][]{
                        {"", ""},
                        {"", ""},
                        {"", ""},
                        {"", ""},
                        {"", ""}});
        getLayeredPane().add(waiting, 30);

        if (opponentGameOver){
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
        }

        while (true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setupOpponentGameOverScreen(){
        String message = gameOver ? "Victoire!!!" : "Defaite!!!";

        PanneauTransparent waiting = new PanneauTransparent();
        waiting.setBounds(680, 0, 680, 768);
        waiting.gameOver(message, opponentScore, opponentLines, level, gameOver,
                new String[][]{
                        {"aaa", "5000"},
                        {"bbb", "4000"},
                        {"ccc", "3000"},
                        {"ddd", "2000"},
                        {"eee", "1000"}});
        getLayeredPane().add(waiting, 30);

        if (gameOver){
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
        }

        while (true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setupRewardModeScreen(int myLevelScore, int opponentLevelScore){
        iWonLevel = (myLevelScore - opponentLevelScore) > 0;

        PanneauTransparent waiting = new PanneauTransparent();
        waiting.setBounds(iWonLevel ? 0 : 680, 0, 680, 768);
        getLayeredPane().add(waiting, 30);
        waiting.addCenterMessage("Reward");
        waiting.addSmallMessage("<html><p align=\"center\">Ton score en ce niveau " + myLevelScore +
                "<br>Le score de ton adversaire en ce niveau: " + opponentLevelScore +
                "<p></html>");

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