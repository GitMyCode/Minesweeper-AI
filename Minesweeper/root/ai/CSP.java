package root.ai;

import root.ArtificialPlayer;
import root.ENUM.CASE;
import root.ENUM.COUP;
import root.Grid;
import root.Move;
import static root.ENUM.CASE.*;

import java.util.*;

/**
 * Created by MB on 10/31/2014.
 */
public class CSP implements ArtificialPlayer{


    Grid gameGrid;
    Set<Move> sureMoves;
    List<Integer> bordure;
    Set<Integer> undiscoveredFrontier;
    Map<Integer,Integer> possibleMine;
    Integer  nbPossibilite =0;
    @Override
    public Set<Move> getAiPlay (Grid g) {
        nbPossibilite =0;
        gameGrid = g;
        CASE[] copyGrid = g.getCpyPlayerView();
        sureMoves = new HashSet<Move>();
        possibleMine = new HashMap<Integer, Integer>();
        undiscoveredFrontier = new HashSet<Integer>();
        getSureCoup(g);

        for(Integer b : bordure){
            for(Integer sur : gameGrid.getSurroundingIndex(b)){
                if(copyGrid[sur] == UNDISCOVERED && !possibleMine.containsKey(sur)){
                    sureMoves.add(new Move(sur, COUP.SHOW));
                }else if (copyGrid[sur] == UNDISCOVERED && possibleMine.get(sur) ==nbPossibilite){
                    sureMoves.add(new Move(sur, COUP.FLAG));
                }
            }
        }

        if(sureMoves.isEmpty()){
            List<Integer> legalMoves = new ArrayList<Integer>();
            for(int i=0; i< g.length; i++){
                if(copyGrid[i] == UNDISCOVERED){
                    legalMoves.add(i);
                }
            }
            Random ran = new Random();
            int index = legalMoves.get(ran.nextInt(legalMoves.size()));
            sureMoves.add(new Move(index,COUP.SHOW));
        }

        return sureMoves;
    }

    @Override
    public String getAiName () {
        return "CSP-Martin";
    }

    public void getSureCoup(Grid g){

        CASE[] grid = g.getCpyPlayerView();

        bordure = new ArrayList<Integer>();


        for(int i=0; i< grid.length; i++){
            if(CASE.isIndicatorCase(grid[i])){
                bordure.add(i);

                List<Integer> voisins = gameGrid.getSurroundingIndex(i);
                for(Integer v : voisins){
                    if(grid[v] == UNDISCOVERED){
                        undiscoveredFrontier.add(v);
                    }
                }
            }
        }

        recurseCSP(grid,bordure,0);


    }

    public boolean recurseCSP(CASE[] grid,List<Integer> bordure,int index){


        if(!allFlagOkey(grid,bordure,index)){
            return false;
        }
        if(index >= bordure.size()){

            for(Integer i : undiscoveredFrontier){
                if(grid[i] == FLAGED){
                    int lastTimeFlaged = (possibleMine.containsKey(i))? possibleMine.get(i)+1 : 1;
                    possibleMine.put(i, lastTimeFlaged);
                }
            }
            nbPossibilite++;
            return true;
        }

        int variableToSatisfy = bordure.get(index);

        List<Integer> surrounding = gameGrid.getSurroundingIndex(variableToSatisfy);
        List<Integer> undiscovered = new ArrayList<Integer>();
        int nbFlagToPlace=0;
        for(Integer i : surrounding){
            if(grid[i]==FLAGED){
                nbFlagToPlace--;
            }
            if(grid[i] == UNDISCOVERED){
                undiscovered.add(i);
            }
        }
        nbFlagToPlace+= grid[variableToSatisfy].indexValue;
        if(nbFlagToPlace <0){return false;}
        if(nbFlagToPlace==0){return recurseCSP(grid,bordure,index+1);}


        int[] combinaison = new int[nbFlagToPlace];
        ArrayList<int[]> listC = new ArrayList<int[]>();
        combinaisonFlag(0,nbFlagToPlace,undiscovered.size(),combinaison,listC);


        for(int[] list : listC){
            CASE[] gCpy = grid.clone();
            for(int i=0; i< nbFlagToPlace; i++){
                int indexToFlag = undiscovered.get(list[i]);
                gCpy[indexToFlag] = FLAGED;
            }

            recurseCSP(gCpy,bordure,index+1);


        }



        return false;
    }


    public boolean allFlagOkey(CASE[] grid, List<Integer> bordure, int nbDone ){

        for(int i=0; i< nbDone;i++){
            int index = bordure.get(i);
            int value = grid[index].indexValue;
            List<Integer> voisins = gameGrid.getSurroundingIndex(index);
            int nbFlag=0;
            for(Integer v : voisins){
                if(grid[v] ==FLAGED )
                    nbFlag++;
            }
            if(nbFlag != value){
                return false;
            }
        }
        return true;
    }


    public void combinaisonFlag(int index,int nbFlag, int nbCase,int[] combinaison, ArrayList<int[]> listeC){
        if(nbFlag ==0){return;}
        if(index >= nbFlag){

            int[] newCombinaison = combinaison.clone();
            listeC.add(newCombinaison);
            return ;
        }
        int start =0;
        if(index >0) start = combinaison[index-1]+1;
        for(int i=start; i<nbCase;i++){
            combinaison[index]=i;
            combinaisonFlag(index+1, nbFlag, nbCase, combinaison, listeC);
        }
    }
}
