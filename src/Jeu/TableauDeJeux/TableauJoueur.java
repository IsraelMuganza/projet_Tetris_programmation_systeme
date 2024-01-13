package Jeu.TableauDeJeux;

import Jeu.Forme;


public class TableauJoueur extends TableauJeu {
    public boolean hasUnhandledTetromino = false;

    int[][][] chart = new int[12][4][3];

    public void moveToBestPosition(Forme hold) {
        hasUnhandledTetromino = false;

        int xToMove, numToFlip;
        int[] currentBestPosition, holdBestPosition;

        // calculate best positions
        currentBestPosition = findCurrentTetrominoBestPosition(this.current);
        holdBestPosition = findCurrentTetrominoBestPosition(hold);

        if (holdBestPosition[3] < currentBestPosition[3]) {
            holdSwitch = true;
            xToMove = holdBestPosition[0] - this.piecePositionX;
            numToFlip = holdBestPosition[2] >= this.current.getPhase() ? holdBestPosition[2] - this.current.getPhase() : 4 + holdBestPosition[2] - this.current.getPhase();

        } else {
            xToMove = currentBestPosition[0] - this.piecePositionX;
            numToFlip = currentBestPosition[2] >= this.current.getPhase() ? currentBestPosition[2] - this.current.getPhase() : 4 + currentBestPosition[2] - this.current.getPhase();
        }

        // deploy the pieces
        new Thread(() -> {
            for (int i = 0; i < Math.abs(xToMove); i++) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
                this.move(xToMove / Math.abs(xToMove));
            }
        }, "AI-HorizonalMove Thread").start();

        new Thread(() -> {
            for (int i = 0; i < Math.abs(numToFlip); i++) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                }
                this.rotate();
            }
        }, "AI-Flip Thread").start();

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
        }
    }


    private int[] findCurrentTetrominoBestPosition(Forme forme) {
        boolean[][] mapCopy;

        for (int i = 1; i < 13; i++) {
            for (int phase = 0; phase < 4; phase++) {

                chart[i - 1][phase][0] = i;

                mapCopy = getMapWithTetromino(forme, phase, i);

                if (mapCopy == null) {
                    chart[i - 1][phase][1] = 99999;
                    chart[i - 1][phase][2] = 99999;
                } else {
                    chart[i - 1][phase][2] = getSpikiness(mapCopy);
                }
            }
        }

        int bestX = 99999, bestPosition = 99999, bestY = 99999, bestSpikiness = 99999;
        for (int i = 0; i < 12; i++) {
            for (int phase = 0; phase < 4; phase++) {
                if ((chart[i][phase][2] < bestSpikiness) || (chart[i][phase][2] == bestSpikiness && chart[i][phase][1] < bestY)) {
                    bestX = chart[i][phase][0];
                    bestY = chart[i][phase][1];
                    bestSpikiness = chart[i][phase][2];
                    bestPosition = phase;
                }
            }
        }
        return new int[]{bestX, bestY, bestPosition, bestSpikiness};
    }


    private boolean[][] getMapWithTetromino(Forme forme, int phase, int xCoordinate) {
        boolean[][] shape, mapCopy;
        int YCoordinate;
        boolean exceed = false;

        shape = forme.getShapes()[phase];
        mapCopy = new boolean[14][25];
        for (int a = 0; a < 14; a++) {
            for (int b = 0; b < 25; b++) {
                if (this.movable[a][b]) {
                    mapCopy[a][b] = false;
                } else {
                    mapCopy[a][b] = this.map[a][b];     // generate map array without the movable
                }
            }
        }

        YCoordinate = 1;
        outerLoop:
        for (int a = 21; a > 0; a--) {
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    if (shape[x][y] && mapCopy[xCoordinate + x][a + y - 1]) {       // find y coordinate
                        YCoordinate = a;
                        break outerLoop;
                    }
                }
            }
        }

        chart[xCoordinate - 1][phase][1] = YCoordinate;

        outerLoop:
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (shape[x][y]) {
                    if ((xCoordinate + x) > 12) {
                        exceed = true;
                        break outerLoop;
                    } else {
                        mapCopy[xCoordinate + x][YCoordinate + y] = true;      // add tetromino
                    }
                }
            }
        }

        if (exceed) {
            return null;    // if not possible to map, return null
        } else {
            return mapCopy;     // return new map
        }
    }


    public int getSpikiness(boolean[][] map) {
        return (int) Math.round(51.0066 * getAggHeight(map) - 76.0666 * Math.pow(getCompleteLines(map), 2) + 35.663 * getHoleNum(map) + 18.4483 * getBumpiness(map));
    }

    private int getAggHeight(boolean[][] map) {
        int height = 0;
        for (int i = 1; i < 13; i++) {
            height += getHeight(map, i);
        }
        return height;
    }


    private int getCompleteLines(boolean[][] map) {
        int rowDisappered = 0;
        boolean disappear;
        for (int i = 1; i < 21; i++) {
            disappear = true;
            for (int j = 1; j < 13; j++) {
                if (!map[j][i]) {
                    disappear = false;
                    break;
                }
            }

            if (disappear) {
                rowDisappered++;
            }
        }
        return rowDisappered;
    }


    public int getHoleNum(boolean[][] map) {
        int holeNum = 0;
        for (int i = 1; i < 21; i++) {
            for (int j = 1; j < 13; j++) {
                if ((!map[j][i]) && map[j - 1][i] && map[j + 1][i] && map[j][i + 1] && map[j][i - 1]) {
                    holeNum++;
                }
            }
        }
        return holeNum;
    }


    private int getBumpiness(boolean[][] map) {
        int height = 0;
        for (int i = 2; i < 13; i++) {
            height += Math.abs(getHeight(map, i) - getHeight(map, i - 1));
        }
        return height;
    }


    @Override
    public void newTetromino(Forme forme) {
        hasUnhandledTetromino = true;
        super.newTetromino(forme);
    }
}