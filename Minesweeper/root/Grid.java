package root;

import root.ENUM.CASE;
import root.ENUM.COUP;

import java.util.*;


/**
 * Created by MB on 10/29/2014.
 */

import static root.ENUM.CASE.*;


public class Grid {

    public int nbcol;
    public int nbligne;
    public int length;
    private final int NBMINES;

    protected int nbMinesRemaining;
    protected int nbFlagRemaining;
    protected boolean lost= false;
    protected boolean win = false;


    byte[] gridSpace;
    protected CASE[] underneathValues;
    CASE[] gridPlayerView;

    Random ran = new Random();

    public Grid(int nbligne, int nbcol,int nbMines) {
        this.nbcol = nbcol;
        this.nbligne = nbligne;
        this.length = nbcol*nbligne;
        this.NBMINES = nbMines;
        this.nbFlagRemaining = nbMines;
        this.nbMinesRemaining = nbMines;



        gridSpace = new byte[nbcol*nbligne];
        underneathValues = new CASE[nbcol*nbligne];
        gridPlayerView = new CASE[nbcol*nbligne];


        Arrays.fill(underneathValues, EMPTY);
        Arrays.fill(gridPlayerView, UNDISCOVERED);
        Arrays.fill(gridSpace, (byte) UNDISCOVERED.indexValue);


        for(int i=0; i<nbMines;i++){
            int putMineThere = ran.nextInt(underneathValues.length);
            if(underneathValues[putMineThere] == MINE){
                i--;
            }else {
                underneathValues[putMineThere] = MINE;
            }
        }
        calculate();
    }

    public int getNbFlagRemaining(){
        return nbFlagRemaining;
    }

    public Set<COUP> getLegalCaseCoup (int index){
        CASE c = gridPlayerView[index];
        switch (c){
            case UNDISCOVERED:
                if(nbFlagRemaining==0){
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
        nbFlagRemaining = NBMINES;

        for(int i =0; i< length; i++){
            gridPlayerView[i] = UNDISCOVERED;
        }
    }

    protected boolean gameFinish(){
        if(lost)
            return true;
        if(win)
            return true;

        if(nbMinesRemaining==0 && nbFlagRemaining==0){
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
            nbFlagRemaining--;
            gridPlayerView[index] = FLAGED;
            if(underneathValues[index] == MINE){
                nbMinesRemaining--;
            }
        }
    }

    private void playUNFlag(int index){

        if(gridPlayerView[index] == FLAGED){
            nbFlagRemaining++;
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
                    int indexVoisin = index + stepDir(D);
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
                    if(!((index + nbcol * (2 - 1)) < length)){
                        return false;
                    }
                    break;
                case TOP:
                    if(index < 0 || index >= length)
                        return false;
                    if(!((index + step(Dir.TOP) * (2 - 1)) >= 0)){
                        return false;
                    }
                    break;
                case LEFT:
                    if(index < 0 || index >= length)
                        return false;
                    if(!(((index%nbcol + 1) >= 2))) { return false; }
                    break;
                case RIGHT:
                    if(index < 0 || index >= length)
                        return false;
                    if(!((nbcol - (index%nbcol)) >= 2)){ return false; }
                    break;
            }
        }
        return true;
    }



    /*
    * WARNING: YOU must check if the next position is in the grid before this methode
    *           Use if(isStepInThisDirInGrid(RIGHT,currentPosition)){}
    * Return the distance to add to get to the next case in this direction
    *          nextplace = current position  + stepFor direction
    * exemple: index =       40              +  stepDir(RIGHT) = 41;
    * */
    public int stepDir(Dir D){
        int step=0;
        for(Dir d : D.getCompDir()){
            step += step(d);
        }
        return step;
    }



    private int step(Dir D){
        switch (D){
            case DOWN:  return nbcol;
            case TOP :  return -nbcol;
            case LEFT:  return -1;
            case RIGHT: return  1;
        }
        return 0;
    }




    private void calculate(){

        for(int i=0; i< underneathValues.length; i++){
            int value =0;

            if(underneathValues[i] != MINE){
                for(Dir D : Dir.values()){
                    int index = i+stepDir(D);
                    if(isStepThisDirInGrid(D, i) && underneathValues[index] == MINE){
                        value++;
                    }
                }
                underneathValues[i]= CASE.caseFromInt(value);
            }
        }
    }



}
