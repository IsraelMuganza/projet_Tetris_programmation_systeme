package Jeu.Jeux;

import Jeu.TableauDeJeux.TableauJeu;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;

import static java.awt.event.KeyEvent.*;

public class JeuEnsolo extends JeuMonoJoueur {
    public JeuEnsolo(PrintWriter output) {
        super();

        this.output = output;

        nomTableauJeu = new TableauJeu();
        nomTableauJeu.setBounds(170, 155, 338, 546);
        getContentPane().add(nomTableauJeu);

        addKeyListener(new KeyListener() {
                           @Override
                           public void keyTyped(KeyEvent e) {
                           }

                           @Override
                           public void keyPressed(KeyEvent e){
                               if (playing){
                                   switch (e.getKeyCode()){
                                       case VK_UP:
                                       case VK_W:
                                           nomTableauJeu.rotate();
                                           break;
                                       case VK_DOWN:
                                       case VK_S:
                                           nomTableauJeu.moveDown();
                                           myScore++;
                                           break;
                                       case VK_LEFT:
                                       case VK_A:
                                           nomTableauJeu.move(-1);
                                           break;
                                       case VK_RIGHT:
                                       case VK_D:
                                           nomTableauJeu.move(1);
                                           break;
                                       case VK_SHIFT:
                                           nomTableauJeu.holdSwitch = true;
                                           break;
                                       case VK_SPACE:
                                           myScore += nomTableauJeu.moveToBottomUp2();
                                           break;
                                   }
                               }
                           }

                           @Override
                           public void keyReleased(KeyEvent e) {
                           }
                       }
        );
    }

    public void run(){
        this.setVisible(true);
        new Thread(() -> {
            ready = false;
            this.setupMyReadyScreen();
            super.run();
        }).start();
    }
}