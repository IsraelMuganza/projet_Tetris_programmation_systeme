package Jeu.Jeux;

import Jeu.Forme;
import Jeu.TableauDeJeux.TableauDeuxJoueurs;
import Jeu.TableauDeJeux.DoublePlayerTableauJeu;
import Jeu.TableauDeJeux.TableauJeu;
import Jeu.queue.Queue;

import javax.swing.*;
import java.awt.*;


abstract class JeuxDeuxJoueurs extends JeuTetris {
    protected TableauJeu opponentTableauJeu;

    protected Forme opponentCurrentForme, opponentHoldForme = null;
    protected Queue<Forme> opponentTetrominoQueue = new Queue<Forme>();

    protected boolean opponentReady, opponentGameOver, rewardMode;

    protected int opponentScore = 0, opponentLines = 0, tetrominoEnqueued = 0;

    protected JLabel opponentHoldLabel, opponentScoreLabel, opponentLineLabel, opponentIcon;
    protected JLabel[] opponentNextLabels = new JLabel[3];

    protected boolean iWonLevel;

    public JeuxDeuxJoueurs(String nickName, String iconPath){
        super();

        setSize(1360, 768);
        setLocationRelativeTo(null);

        JLabel myIcon = new JLabel(nickName);
        myIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
        myIcon.setBounds(180, 80, 300, 64);
        myIcon.setIcon(new ImageIcon(iconPath));
        myIcon.setHorizontalAlignment(SwingConstants.LEFT);
        getContentPane().add(myIcon);

        opponentIcon = new JLabel();
        opponentIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
        opponentIcon.setBounds(1360 - 180 - 300, 80, 300, 64);
        opponentIcon.setHorizontalAlignment(SwingConstants.RIGHT);
        getContentPane().add(opponentIcon);

        iconLabel.setBounds(490, 39, 400, 50);
        levelLabel.setBounds(620, 100, 120, 36);

        /*JLabel lblHold = new JLabel("Hold");
        lblHold.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblHold.setBounds(680, 220, 170, 20);
        lblHold.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(lblHold);

        opponentHoldLabel = new JLabel("");
        ImageIcon img1 = new ImageIcon("resources/hold.png");
        opponentHoldLabel.setIcon(img1);
        opponentHoldLabel.setBounds(53 + 680, 250, 64, 64);
        getContentPane().add(opponentHoldLabel);*/

        JLabel lblScore = new JLabel("Score");
        lblScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblScore.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblScore.setBounds(680, 440, 170, 22);
        getContentPane().add(lblScore);

        opponentScoreLabel = new JLabel("0");
        opponentScoreLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        opponentScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        opponentScoreLabel.setBounds(680, 470, 170, 22);
        getContentPane().add(opponentScoreLabel);

        JLabel lblLines = new JLabel("Lignes");
        lblLines.setHorizontalAlignment(SwingConstants.CENTER);
        lblLines.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblLines.setBounds(680, 525, 170, 22);
        getContentPane().add(lblLines);

        opponentLineLabel = new JLabel("0");
        opponentLineLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        opponentLineLabel.setHorizontalAlignment(SwingConstants.CENTER);
        opponentLineLabel.setBounds(680, 555, 170, 22);
        getContentPane().add(opponentLineLabel);

        JLabel lblNext = new JLabel("Suivant");
        lblNext.setHorizontalAlignment(SwingConstants.CENTER);
        lblNext.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblNext.setBounds(509 + 680, 208, 170, 22);
        getContentPane().add(lblNext);

        opponentNextLabels[0] = new JLabel("");
        opponentNextLabels[0].setBounds(562 + 680, 241, 64, 64);
        getContentPane().add(opponentNextLabels[0]);

        opponentNextLabels[1] = new JLabel("");
        opponentNextLabels[1].setBounds(562 + 680, 316, 64, 64);
        getContentPane().add(opponentNextLabels[1]);

        opponentNextLabels[2] = new JLabel("");
        opponentNextLabels[2].setBounds(562 + 680, 391, 64, 64);
        getContentPane().add(opponentNextLabels[2]);
    }

    public void run(){
        setVisible(true);

        opponentReady = false;
        ready = false;
        requestFocus();

        new Thread(this :: setupMyReadyScreen).start();

        new Thread(this :: setupOpponentReadyScreen).start();

        while (!(ready && opponentReady)){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++){
            requestTetromino();
        }

        setupCountDownScreen();
        repaint();

        playing = true;
        gameOver = false;
        opponentGameOver = false;

        new Thread(this :: upDateLevel).start();

        new Thread(this :: myGameGo).start();

        new Thread(this :: opponentGameGo).start();

        new Thread(this :: checkMyHold).start();

        new Thread(this :: checkOpponentHold).start();
    }

    protected void myGameGo() {
        int linesDisappeared;

        myCurrentForme = myTetrominoQueue.dequeue();
        nomTableauJeu.newTetromino(myCurrentForme);

        Object[] tetrominos = myTetrominoQueue.peek(3);
        for (int i = 0; i < 3; i++) {
            myNextLabels[i].setIcon(((Forme) tetrominos[i]).getImg());
        }

        gameLoop:
        while (!gameOver) {
            while (playing) {
                try {
                    if (nomTableauJeu.isAtBottom()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (nomTableauJeu.isAtBottom()) {


                            nomTableauJeu.solidifyTetromino();

                            if (nomTableauJeu.gameOver()) {
                                gameOver = true;
                                break gameLoop;
                            }

                            tetrominoNum++;
                            myScore += 10 * level;

                            linesDisappeared = nomTableauJeu.checkDisapperance();
                            myLines += linesDisappeared;
                            myScore += Math.pow(linesDisappeared, 2) * 100;
                            try {
                                ((DoublePlayerTableauJeu) opponentTableauJeu).addLinesOnTop(linesDisappeared - 1);
                            } catch (ClassCastException e) {
                                ((TableauDeuxJoueurs) opponentTableauJeu).addLinesOnTop(linesDisappeared - 1);
                            }
                            myLineLabel.setText("" + myLines);
                            myScoreLabel.setText("" + myScore);

                            myCurrentForme = myTetrominoQueue.dequeue();
                            nomTableauJeu.newTetromino(myCurrentForme);

                            requestTetromino();

                            tetrominos = myTetrominoQueue.peek(3);
                            for (int i = 0; i < 3; i++) {
                                myNextLabels[i].setIcon(((Forme) tetrominos[i]).getImg());
                            }

                            if (linesDisappeared > 0) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            repaint();
                        }
                    }

                    nomTableauJeu.moveDown();

                    Thread.sleep(level < 7 ? (500 - level * 40) : (340 / (level - 5)) + 150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while (!playing) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        setupMyGameOverScreen();
    }

    protected void opponentGameGo(){
        int linesDisappeared;

        opponentCurrentForme = opponentTetrominoQueue.dequeue();
        opponentTableauJeu.newTetromino(opponentCurrentForme);

        Object[] tetrominos = opponentTetrominoQueue.peek(3);
        for (int i = 0; i < 3; i++){
            opponentNextLabels[i].setIcon(((Forme)tetrominos[i]).getImg());
        }

        gameLoop:
        while (!opponentGameOver){
            while (playing){
                try {
                    if (opponentTableauJeu.isAtBottom()){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }

                        if (opponentTableauJeu.isAtBottom()) {

                            opponentTableauJeu.solidifyTetromino();

                            if (opponentTableauJeu.gameOver()) {
                                opponentGameOver = true;
                                break gameLoop;
                            }

                            tetrominoNum++;
                            opponentScore += 10 * level;

                            linesDisappeared = opponentTableauJeu.checkDisapperance();
                            opponentLines += linesDisappeared;
                            opponentScore += Math.pow(linesDisappeared, 2) * 100;
                            ((DoublePlayerTableauJeu) nomTableauJeu).addLinesOnTop(linesDisappeared - 1);

                            opponentLineLabel.setText("" + opponentLines);
                            opponentScoreLabel.setText("" + opponentScore);

                            opponentCurrentForme = opponentTetrominoQueue.dequeue();
                            opponentTableauJeu.newTetromino(opponentCurrentForme);

                            requestTetromino();

                            tetrominos = opponentTetrominoQueue.peek(3);
                            for (int i = 0; i < 3; i++) {
                                opponentNextLabels[i].setIcon(((Forme) tetrominos[i]).getImg());
                            }

                            if (linesDisappeared > 0) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            repaint();
                        }
                    }

                    opponentTableauJeu.moveDown();

                    Thread.sleep(level < 7 ? (500 - level * 40) : (340 / (level - 5)) + 150);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            while (!playing){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

        setupOpponentGameOverScreen();
    }

    public void enqueueTetromino(Forme forme){
        myTetrominoQueue.enqueue(forme);
        opponentTetrominoQueue.enqueue(forme);
        tetrominoEnqueued++;
    }

    private void upDateLevel(){
        int prevLevel = level, myPrevScore = 0, opponentPrevScore = 0, myLevelScore, opponentLevelScore;
        while (!(gameOver && opponentGameOver)){
            level = tetrominoEnqueued / 8 + 1;
            levelLabel.setText("Niveau " + level);

            if (!(this instanceof JeuEnLigne)){
                if (level != prevLevel && !gameOver && !opponentGameOver){
                    playing = false;
                    rewardMode = true;

                    myLevelScore = myScore - myPrevScore;
                    opponentLevelScore = opponentScore - opponentPrevScore;

                    setupRewardModeScreen(myLevelScore, opponentLevelScore);

                    while (rewardMode){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }

                    prevLevel = level;
                    myPrevScore = myScore;
                    opponentPrevScore = opponentScore;

                    repaint();
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void checkOpponentHold(){
        while (!opponentGameOver){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            if (opponentTableauJeu.getHoldSwitch()){
                if (opponentHoldForme == null){
                    opponentHoldForme = opponentTetrominoQueue.dequeue();
                    opponentTetrominoQueue.enqueue(new Forme());

                    requestTetromino();

                    Object[] tetrominos = opponentTetrominoQueue.peek(3);
                    for (int i = 0; i < 3; i++){
                        opponentNextLabels[i].setIcon(((Forme)tetrominos[i]).getImg());
                    }
                }

                Forme buffer = opponentHoldForme;
                opponentHoldForme = opponentCurrentForme;
                opponentCurrentForme = buffer;

                opponentTableauJeu.current = opponentCurrentForme;
                opponentTableauJeu.replace(opponentCurrentForme);
                opponentHoldLabel.setIcon(opponentHoldForme.getImg());
                repaint();
            }
        }
    }

    /**
     * ecran semi transparant demandant a l'utilisateur s'il est pret de lancer le jeu
     */
    abstract void setupOpponentReadyScreen();

    /**
     * ecran semi transparent donnant les information au joueur a la fin du jeu
     */
    abstract void setupOpponentGameOverScreen();

    /**
     * ecran de recompense
     */
    abstract void setupRewardModeScreen(int myLevselScore, int opponentLevelScore);
}