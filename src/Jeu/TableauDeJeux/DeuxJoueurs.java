package Jeu.TableauDeJeux;

import java.awt.*;


public interface DeuxJoueurs {
    void addLinesOnTop(int lineNum);

    void gravityDrop();


    Point deployRewardPiece();


    void drawPiece(Graphics g);
}
