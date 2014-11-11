package root;

import root.ENUM.CASE;
import root.ENUM.COUP;

import java.io.File;
import java.io.FileWriter;
import java.util.*;


/**
 * Created by MB on 10/29/2014.
 */

import static root.ENUM.CASE.*;


public class Grid {

    public int nbCols;
    public int nbLignes;
    public int length;
    protected int nbMines;

    protected int nbMinesRemaining;
    protected int nbFlagsRemaining;
    protected boolean lost = false;
    protected boolean win = false;

    protected CASE[] underneathValues;
    CASE[] gridPlayerView;

    Random rand = new Random();

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

            this.gridPlayerView = new CASE[length];
            this.underneathValues = new CASE[length];

            for(int i = 0; i < length; i++){
                this.underneathValues[i] = CASE.caseFromInt(sc.nextInt());
            }

            String s = sc.next();
            for(int i = 0; i < length; i++){
                this.gridPlayerView[i] = CASE.caseFromInt(sc.nextInt());
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
        this.gridPlayerView = new CASE[nbCols * nbLignes];

        Arrays.fill(gridPlayerView, UNDISCOVERED);


        underneathValues = createRdmGrid(nbLignes, nbCols,nbMines);
        /*placeMinesRmd(underneathValues,nbMines);
        calculateCasesValues(underneathValues);*/
    }

    private CASE[] createRdmGrid(int nbligne,int nbcol, int nbMines){
        CASE[] grid = new CASE[nbcol*nbligne];
        Arrays.fill(grid,EMPTY);
        placeMinesRmd(grid, nbMines);
        calculateCasesValues(grid);
        return grid;
    }
    private void placeMinesRmd(CASE[] grid,int nbMines){
        for(int i=0; i<nbMines;i++){
            int putMineThere = rand.nextInt(grid.length);
            if(grid[putMineThere] == MINE){
                i--;
            }else {
                grid[putMineThere] = MINE;
            }
        }
    }

    /*
    * TODO
    * Pour debuggage seulement Ne doit pas etre utiliser comme strategie dans
    * Les AI
    * */
    public boolean checkMove(Set<Move> moves){
        for(Move m : moves){
            if(underneathValues[m.index] != CASE.MINE && m.coup == COUP.FLAG){
                return false;
            }else if(underneathValues[m.index] == CASE.MINE && m.coup == COUP.SHOW){
                return false;
            }
        }
        return true;
    }
    public boolean checkIfPresentGridValid(){
        for(int i =0;i<length;i++){
            if(underneathValues[i] != MINE && gridPlayerView[i] == FLAGED ){
                return false;
            }
        }
        return true;
    }


    /*
    * DES if parce que je veux m<assurer que la list retourner suivre cet ordre
    * */
    public List<Integer> getSurroundingIndex(int index){
        List<Integer> list = new ArrayList<Integer>();


        if(isStepThisDirInGrid(Dir.RIGHT,index)){
            list.add(index+step(Dir.RIGHT));
        }
        if(isStepThisDirInGrid(Dir.DOWN,index)){
            list.add(index+step(Dir.DOWN));
        }

        if(isStepThisDirInGrid(Dir.TOP,index)){
            list.add(index+step(Dir.TOP));
        }

        if(isStepThisDirInGrid(Dir.LEFT,index)){
            list.add(index+step(Dir.LEFT));
        }

        if(isStepThisDirInGrid(Dir.TOPLEFT,index)){
            list.add(index+step(Dir.TOPLEFT));
        }
        if(isStepThisDirInGrid(Dir.TOPRIGHT,index)){
            list.add(index+step(Dir.TOPRIGHT));
        }
        if(isStepThisDirInGrid(Dir.DOWNLEFT,index)){
            list.add(index+step(Dir.DOWNLEFT));
        }
        if(isStepThisDirInGrid(Dir.DOWNRIGHT,index)){
            list.add(index+step(Dir.DOWNRIGHT));
        }

        /*for(Dir D : Dir.direction8){
            if(isStepThisDirInGrid(D,index)){
                list.add((index+ step(D)));

            }
        }*/

        return list;
    }

    public int getNbFlagsRemaining(){
        return nbFlagsRemaining;
    }

    public Set<COUP> getLegalCaseCoup (int index){
        CASE c = gridPlayerView[index];
        switch (c){
            case UNDISCOVERED:
                if(nbFlagsRemaining ==0){
                    return EnumSet.of(COUP.SHOW);
                }else {
                    return EnumSet.of(COUP.SHOW,COUP.FLAG);
                }

            case FLAGED:
                return EnumSet.of(COUP.UNFLAG);
            default:
                return EnumSet.of(COUP.INVALID);
        }
    }


    public Set<Integer> getUndiscoveredNeigbour(CASE[] grid, int index){
        Set<Integer> set = new HashSet<Integer>();

        for(Dir D: Dir.direction8){
            if(isStepThisDirInGrid(D,index)){
                int voisin = index + step(D);
                if(grid[voisin] == UNDISCOVERED){
                    set.add(voisin);
                }
            }
        }
        return set;
    }


    public CASE[] getCpyPlayerView(){
        CASE[] cpy;
        cpy = gridPlayerView.clone();
        return cpy;
    }

    protected void showAllCase(){
        for(int i=0; i< length; i++) {
            if (gridPlayerView[i] == FLAGED && underneathValues[i] == MINE){
                gridPlayerView[i] = DEFUSED;
            }else if(gridPlayerView[i] == FLAGED && underneathValues[i] !=MINE){
                gridPlayerView[i] = ERROR_FLAG;
            }else if(gridPlayerView[i] == BLOW) {
            }else {
                gridPlayerView[i] = underneathValues[i];
            }
        }
    }
    protected void play (int index, COUP coup){
        switch (coup){
            case FLAG:
                playFlag(index);
                break;
            case UNFLAG:
                playUNFlag(index);
                break;
            case SHOW:
                playUndiscoveredCase(index);
                break;

        }
    }

    protected void resetGrid(){

        lost = false;
        win  = false;
        nbFlagsRemaining = nbMines;
        nbMinesRemaining= nbMines;

        for(int i =0; i< length; i++){
            gridPlayerView[i] = UNDISCOVERED;
        }
        underneathValues = createRdmGrid(nbLignes, nbCols, nbMines);


    }

    protected boolean gameFinish(){
        if(lost){
            return true;
        }
        if(win)
            return true;

        if(nbMinesRemaining==0 && nbFlagsRemaining ==0){
            win = true;

            return true;
        }

        for(CASE c: gridPlayerView){
            if(c == UNDISCOVERED){
                return false;
            }
        }
        return true;
    }




    private void playFlag(int index){
        CASE theCase = gridPlayerView[index];
        if(theCase == UNDISCOVERED){
            nbFlagsRemaining--;
            gridPlayerView[index] = FLAGED;
            if(underneathValues[index] == MINE){
                nbMinesRemaining--;
            }
        }
    }

    private void playUNFlag(int index){

        if(gridPlayerView[index] == FLAGED){
            nbFlagsRemaining++;
            gridPlayerView[index] = UNDISCOVERED;
        }

    }

    private void playUndiscoveredCase (int index){

        if(gridPlayerView[index] == UNDISCOVERED){

            gridPlayerView[index] = underneathValues[index];
            if(underneathValues[index]==MINE){
                gridPlayerView[index] = BLOW;
                lost = true;
            }


            if(underneathValues[index] == EMPTY){
                for(Dir D : Dir.values()){
                    int indexVoisin = index + step(D);
                    if(isStepThisDirInGrid(D, index) && gridPlayerView[indexVoisin].equals(UNDISCOVERED) ){
                        playUndiscoveredCase(indexVoisin);
                    }
                }
            }

        }


    }



    public boolean isStepThisDirInGrid (Dir D, int index){

        for(Dir d : D.getCompDir()){
            switch (d){
                case DOWN:
                    if(index < 0 || index >= length)
                        return false;
                    if(!((index + nbCols * (2 - 1)) < length)){
                        return false;
                    }
                    break;
                case TOP:
                    if(index < 0 || index >= length)
                        return false;
                    if(!((index + stepUtility(Dir.TOP) * (2 - 1)) >= 0)){
                        return false;
                    }
                    break;
                case LEFT:
                    if(index < 0 || index >= length)
                        return false;
                    if(!(((index% nbCols + 1) >= 2))) { return false; }
                    break;
                case RIGHT:
                    if(index < 0 || index >= length)
                        return false;
                    if(!((nbCols - (index% nbCols)) >= 2)){ return false; }
                    break;
            }
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
    public int step(Dir D){
        int step=0;
        for(Dir d : D.getCompDir()){
            step += stepUtility(d);
        }
        return step;
    }



    private int stepUtility(Dir D){
        switch (D){
            case DOWN:  return nbCols;
            case TOP :  return -nbCols;
            case LEFT:  return -1;
            case RIGHT: return  1;
        }
        return 0;
    }




    private void calculateCasesValues (CASE[] grid){

        for(int i=0; i< grid.length; i++){
            int value =0;

            if(grid[i] != MINE){
                for(Dir D : Dir.values()){
                    int index = i+ step(D);
                    if(isStepThisDirInGrid(D, i) && grid[index] == MINE){
                        value++;
                    }
                }
                grid[i]= CASE.caseFromInt(value);
            }
        }
    }

    public void saveToFile(String fileName) throws Exception{


            FileWriter fw = new FileWriter(fileName);

            fw.write(nbLignes +" "+ nbCols +" "+ nbMines +" "+ nbFlagsRemaining +" "+nbMinesRemaining+ "\n");
            int i=1;String gridAllValue ="";
            for(CASE c : underneathValues){
                gridAllValue+= c.indexValue+ " ";
                if(i % nbCols ==0){
                    gridAllValue+="\n";
                }
                i++;
            }
            fw.write(gridAllValue);

            fw.write("-\n");
            i=1;String stringGridPlayerView ="";
            for(CASE c : gridPlayerView){
                stringGridPlayerView+= c.indexValue+" ";
                if(i % nbCols ==0){
                    stringGridPlayerView+="\n";
                }
                i++;
            }
            fw.write(stringGridPlayerView);
            fw.close();
    }





}
