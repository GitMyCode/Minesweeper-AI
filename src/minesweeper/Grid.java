package minesweeper;

import minesweeper.Coup;
import minesweeper.Case;

import java.io.File;
import java.io.FileWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.Scanner;
import java.util.Random;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Arrays;

public class Grid {

    public int nbCols;
    public int nbLignes;
    public int length;
    public int nbMines;
    public int nbMinesRemaining;
    public int nbFlagsRemaining;
    public boolean gameLost = false;
    public boolean gameWon = false;
    public Case[] underneathValues;
    public Case[] gridPlayerView;

    public Grid(File f) {
        try {
            Scanner sc = new Scanner(f);
            this.nbLignes = sc.nextInt();
            this.nbCols = sc.nextInt();
            this.nbMines = sc.nextInt();
            this.nbFlagsRemaining = sc.nextInt();
            this.nbMinesRemaining = sc.nextInt();
            this.length = nbCols * nbLignes;
            this.nbFlagsRemaining = nbMines;
            this.nbMinesRemaining = nbMines;

            this.gridPlayerView = new Case[length];
            this.underneathValues = new Case[length];

            for (int i = 0; i < length; i++) {
                this.underneathValues[i] = Case.caseFromInt(sc.nextInt());
            }

            String s = sc.next();
            for (int i = 0; i < length; i++) {
                this.gridPlayerView[i] = Case.caseFromInt(sc.nextInt());
            }

        } catch (Exception e) {
            System.out.println("Erreur remake grid:" + e);
        }
    }

    public Grid(int nbLignes, int nbCols, int nbMines) {
        this.nbCols = nbCols;
        this.nbLignes = nbLignes;
        this.length = nbCols * nbLignes;
        this.nbMines = nbMines;
        this.nbFlagsRemaining = nbMines;
        this.nbMinesRemaining = nbMines;
        this.gridPlayerView = new Case[nbCols * nbLignes];

        Arrays.fill(gridPlayerView, Case.UNDISCOVERED);

        underneathValues = createRandomGrid(nbLignes, nbCols, nbMines);

    }

    private Case[] createRandomGrid(int nbLignes, int nbColonnes, int nbMines) {
        Case[] grid = new Case[nbColonnes * nbLignes];
        Arrays.fill(grid, Case.EMPTY);
        placeMinesRandomly(grid, nbMines);
        generateValues(grid);
        return grid;
    }

    private void placeMinesRandomly(Case[] grid, int nbMines) {

        Random rand = new Random();

        for (int i = 0; i < nbMines; i++) {
            int nextMine = rand.nextInt(grid.length);
            if (grid[nextMine] == Case.MINE) {
                i--;
            } else {
                grid[nextMine] = Case.MINE;
            }
        }
    }

    private void generateValues(Case[] grid) {

        for (int i = 0; i < grid.length; i++) {
            int value = 0;

            if (grid[i] != Case.MINE) {
                for (Direction d : Direction.values()) {
                    int index = i + step(d);
                    if (this.isInGrid(index) && grid[index] == Case.MINE) {
                        value++;
                    }
                }
                grid[i] = Case.caseFromInt(value);
            }
        }
    }

    public boolean isValid() {
        for (int i = 0; i < this.length; i++) {
            if (this.underneathValues[i] != Case.MINE && this.gridPlayerView[i] == Case.FLAGED) {
                return false;
            }
        }
        return true;
    }

    /*
    * DES if parce que je veux m<assurer que la list retourner suivre cet ordre
    * */
    public List<Integer> getSurroundingIndex(int index) {
        List<Integer> list = new ArrayList<Integer>();


        if (isInGrid(index + step(Direction.RIGHT))) {
            list.add(index + step(Direction.RIGHT));
        }
        if (isInGrid(index + step(Direction.DOWN))) {
            list.add(index + step(Direction.DOWN));
        }

        if (isInGrid(index + step(Direction.TOP))) {
            list.add(index + step(Direction.TOP));
        }

        if (isInGrid(index + step(Direction.LEFT))) {
            list.add(index + step(Direction.LEFT));
        }

        if (isInGrid(index + step(Direction.TOPLEFT))) {
            list.add(index + step(Direction.TOPLEFT));
        }
        if (isInGrid(index + step(Direction.TOPRIGHT))) {
            list.add(index + step(Direction.TOPRIGHT));
        }
        if (isInGrid(index + step(Direction.DOWNLEFT))) {
            list.add(index + step(Direction.DOWNLEFT));
        }
        if (isInGrid(index + step(Direction.DOWNRIGHT))) {
            list.add(index + step(Direction.DOWNRIGHT));
        }

        return list;
    }

    public int getNbFlagsRemaining() {
        return nbFlagsRemaining;
    }

    public Set<Coup> getLegalCaseCoup(int index) {
        Case c = gridPlayerView[index];
        switch (c) {
            case UNDISCOVERED:
                if (nbFlagsRemaining == 0) {
                    return EnumSet.of(Coup.SHOW);
                } else {
                    return EnumSet.of(Coup.SHOW, Coup.FLAG);
                }

            case FLAGED:
                return EnumSet.of(Coup.UNFLAG);
            default:
                return EnumSet.of(Coup.INVALID);
        }
    }

    public Set<Integer> getUndiscoveredNeigbour(Case[] grid, int index) {
        Set<Integer> set = new HashSet<Integer>();

        for (Direction d: Direction.direction8) {
            if (isInGrid(index + step(d))) {
                int voisin = index + step(d);
                if (grid[voisin] == Case.UNDISCOVERED) {
                    set.add(voisin);
                }
            }
        }
        return set;
    }

    // #TODO devrait pas exister selon moi, à l'appelant de faire une copie.
    public Case[] getCpyPlayerView() {
        Case[] cpy;
        cpy = gridPlayerView.clone();
        return cpy;
    }

    public void showAllCases() {

        for (int i = 0; i < length; i++) {
            if (gridPlayerView[i] == Case.FLAGED && underneathValues[i] == Case.MINE) {
                gridPlayerView[i] = Case.DEFUSED;
            } else if (gridPlayerView[i] == Case.FLAGED && underneathValues[i] != Case.MINE) {
                gridPlayerView[i] = Case.ERROR_FLAG;
            } else if (gridPlayerView[i] != Case.BLOW) {
                gridPlayerView[i] = underneathValues[i];
            }
        }

    }

    public void play(int index, Coup coup) {
        switch (coup) {
            case FLAG:
                playFlag(index);
                break;
            case UNFLAG:
                playUNFlag(index);
                break;
            case SHOW:
                playUndiscoveredCase(index);
                break;
            default:
                break;

        }
    }

    public void resetGrid() {

        this.gameLost = false;
        this.gameWon = false;
        this.nbFlagsRemaining = nbMines;
        this.nbMinesRemaining = nbMines;

        for (int i = 0; i < length; i++) {
            this.gridPlayerView[i] = Case.UNDISCOVERED;
        }

        underneathValues = createRandomGrid(nbLignes, nbCols, nbMines);

    }

    public boolean gameIsFinished() {

        boolean reponse = true;

        if (this.gameLost) {
            reponse = true;
        } else if (this.gameWon) {
            reponse = true;
        } else if (nbMinesRemaining == 0 && nbFlagsRemaining == 0) {
            this.gameWon = true;
            reponse = true;
        } else {

            for (Case c: gridPlayerView) {
                if (c == Case.UNDISCOVERED) {
                    reponse = false;
                    break;
                }
            }

        }

        return reponse;

    }

    private void playFlag(int index) {
        Case theCase = gridPlayerView[index];
        if (theCase == Case.UNDISCOVERED) {
            nbFlagsRemaining--;
            gridPlayerView[index] = Case.FLAGED;
            if (underneathValues[index] == Case.MINE) {
                nbMinesRemaining--;
            }
        }
    }

    private void playUNFlag(int index) {

        if (gridPlayerView[index] == Case.FLAGED) {
            nbFlagsRemaining++;
            gridPlayerView[index] = Case.UNDISCOVERED;
        }

    }

    private void playUndiscoveredCase(int index) {

        if (gridPlayerView[index] == Case.UNDISCOVERED) {

            gridPlayerView[index] = underneathValues[index];
            if (underneathValues[index] == Case.MINE) {
                gridPlayerView[index] = Case.BLOW;
                this.gameLost = true;
            }


            if (underneathValues[index] == Case.EMPTY) {
                for (Direction d : Direction.values()) {
                    int indexVoisin = index + step(d);
                    if (isInGrid(index + step(d)) && gridPlayerView[indexVoisin].equals(Case.UNDISCOVERED)) {
                        playUndiscoveredCase(indexVoisin);
                    }
                }
            }

        }
    }

    public boolean isInGrid(int index) {
        if (index < 0 || index >= length) {
            return false;
        }
        return true;
    }

    /*
    * WARNING: YOU must check if the next position is in the grid before this methode
    *           Use if(isStepInThisDirInGrid(RIGHT,currentPosition)){}
    * Return the distance to add to get to the next case in this direction
    *          nextplace = current position  + step for direction(le OFFSET)
    * exemple: index =       40              +  step(RIGHT) = 41;
    * */
    public int step(Direction d) {
        int step = 0;
        for (Direction dir : d.getCompDir()) {
            step += stepUtility(dir);
        }
        return step;
    }

    private int stepUtility(Direction dir) {
        switch (dir) {
            case DOWN:  return nbCols;
            case TOP :  return -nbCols;
            case LEFT:  return -1;
            case RIGHT: return  1;
            default: break;
        }
        return 0;
    }

    public String getATimeStampedGridName() {
        Format formatter = new SimpleDateFormat("MM-dd_hh-mm-ss");
        return  "grid-" + (formatter.format(new Date()));
    }

    public void saveToFile(String fileName) throws Exception {

            FileWriter fw = new FileWriter(fileName);

            fw.write(nbLignes + " " + nbCols + " " + nbMines + " " + nbFlagsRemaining + " " + nbMinesRemaining + "\n");
            int i = 1;
            String gridAllValue = "";
            for (Case c : underneathValues) {
                gridAllValue += c.indexValue + " ";
                if (i % nbCols == 0) {
                    gridAllValue += "\n";
                }
                i++;
            }
            fw.write(gridAllValue);

            fw.write("-\n");
            i = 1;
            String stringGridPlayerView = "";
            for (Case c : gridPlayerView) {
                stringGridPlayerView += c.indexValue + " ";
                if (i % nbCols == 0) {
                    stringGridPlayerView += "\n";
                }
                i++;
            }
            fw.write(stringGridPlayerView);
            fw.close();
    }

    public int countUnplacedFlags(int index) {
        int reponse = this.gridPlayerView[index].indexValue;
        for (Integer v : this.getSurroundingIndex(index)) {
            if (this.gridPlayerView[v] == Case.FLAGED) {
                reponse--;
            }
        }
        return reponse;
    }

    public Set<Integer> getUndiscoveredneighbours(int index) {
        Set<Integer> reponse = new LinkedHashSet<Integer>();
        for (Integer i : this.getSurroundingIndex(index)) {
            if (this.gridPlayerView[i] == Case.UNDISCOVERED) {
                reponse.add(i);
            }
        }
        return reponse;
    }

    public Set<Move> checkForSafeMoves() {

        HashSet<Move> reponse = new HashSet<Move>();

        for (int index = 0; index < this.gridPlayerView.length; index++) {

            if (Case.isIndicatorCase(this.gridPlayerView[index])) {
                if (this.isIndexSatisfied(index)) {
                    for (Integer c : this.getUndiscoveredneighbours(index)) {
                        reponse.add(new Move(c, Coup.SHOW));
                    }

                } else if (this.countUnplacedFlags(index) == this.getUndiscoveredneighbours(index).size()) {
                    for (Integer v : this.getUndiscoveredneighbours(index)) {
                        reponse.add(new Move(v, Coup.FLAG));
                    }

                }


            }
        }

        return reponse;


    }

    boolean isIndexSatisfied(int index) {
        int indice = this.gridPlayerView[index].indexValue;
        int nbFlagPosed = 0;
        for (Integer v : this.getSurroundingIndex(index)) {
            if (this.gridPlayerView[v] == Case.FLAGED) {
                nbFlagPosed++;
            }
        }
        return indice == nbFlagPosed;
    }

}
