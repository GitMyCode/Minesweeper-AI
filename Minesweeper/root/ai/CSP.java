package root.ai;

import root.ArtificialPlayer;
import root.ENUM.CASE;
import root.ENUM.COUP;
import root.GameRunner;
import root.Grid;
import root.Move;
import root.ai.utilCSP.TimeOver;

import static root.ENUM.CASE.*;
import static root.ENUM.COUP.*;

import java.util.*;

/**
 * Created by MB on 10/31/2014.
 */
public class CSP implements ArtificialPlayer{


    /*Timer*/
    public long timer;
    public long remain;
    public boolean END = false;
    public final int LIMITE = 10;

    public GameRunner forTest;


    Grid gameGrid;
    Set<Move> sureMoves;

    Set<Integer> undiscoveredFrontier;
    Map<Integer,Integer> possibleMine;
    Integer  nbPossibilite =0;
    @Override
    public Set<Move> getAiPlay (Grid g,int thinkLimit) {
        gameGrid = g;
        CASE[] copyGrid = g.getCpyPlayerView();
        startTimer(thinkLimit);


        nbPossibilite =0;
        sureMoves = new HashSet<Move>();
        possibleMine = new HashMap<Integer, Integer>();
        undiscoveredFrontier = new HashSet<Integer>();
        List<Integer> bordure= new ArrayList<Integer>();


        try {
            getSureCoup(g,bordure);
        }catch (TimeOver e){

        }




        if(sureMoves.isEmpty()){
            int bestChance =Integer.MAX_VALUE;
        for(Integer b : bordure){
            for(Integer sur : gameGrid.getSurroundingIndex(b)){
                if(copyGrid[sur] == UNDISCOVERED && !possibleMine.containsKey(sur)){
                    sureMoves.add(new Move(sur, COUP.SHOW));
                   /* if(!forTest.checkMove(sureMoves)){
                        int adas=0;
                    }*/
                }else if ( copyGrid[sur] == UNDISCOVERED && possibleMine.get(sur) >=nbPossibilite){
                    if(possibleMine.get(sur) > nbPossibilite){
                        System.out.println("wird");
                    }
                    sureMoves.add(new Move(sur, COUP.FLAG));
                   /* if(!forTest.checkMove(sureMoves)){
                        int adas=0;
                    }*/
                }

                if(copyGrid[sur]== UNDISCOVERED && possibleMine.containsKey(sur) && possibleMine.get(sur)< bestChance){
                    bestChance = possibleMine.get(sur);
                }
            }
        }
        }
        if(!gameGrid.checkMove(sureMoves)){
            System.out.println(" Problem and is timeout:"+(timeUp()));
        }

/*
        if(sureMoves.isEmpty() && bestChance != Integer.MAX_VALUE){

            System.out.println("best chance: "+ bestChance);
            sureMoves.add(new Move(bestChance,SHOW));
            return sureMoves;
        }*/

        if(sureMoves.isEmpty()){
            List<Integer> legalMoves = new ArrayList<Integer>();
            for(int i=0; i< g.length; i++){
                if(copyGrid[i] == UNDISCOVERED){
                    legalMoves.add(i);
                }
            }
            if(legalMoves.size() > undiscoveredFrontier.size()){
                legalMoves.removeAll(undiscoveredFrontier);
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

    public void getSureCoup(Grid g,List<Integer> bordure) throws TimeOver{

        CASE[] grid = g.getCpyPlayerView();



        for(int i=0; i< grid.length; i++){
            if(CASE.isIndicatorCase(grid[i])){
                bordure.add(i);

                List<Integer> voisins = gameGrid.getSurroundingIndex(i);
                boolean isSurrounded= true;
                List<Integer> unknownNeighbors = new ArrayList<Integer>();
                int nbFlaged =0;
                for(Integer v : voisins){
                    if(grid[v] == UNDISCOVERED){
                        unknownNeighbors.add(v);
                    }else if(grid[v] == FLAGED){
                        nbFlaged++;
                    }
                    else {
                        isSurrounded = false;
                    }
                }
                if(nbFlaged == grid[i].indexValue){
                    //bordure.remove((Object) i);
                    if(unknownNeighbors.size()!=0){
                        for(Integer v2 : unknownNeighbors){
                            sureMoves.add(new Move(v2,COUP.SHOW));
                        }
                    }
                }else if((nbFlaged-grid[i].indexValue) == unknownNeighbors.size()){
                    //bordure.remove((Object) i);
                    if(unknownNeighbors.size()!=0){
                        for(Integer v2 : unknownNeighbors){
                            sureMoves.add(new Move(v2,COUP.FLAG));
                        }

                    }
                }

                else if(!isSurrounded){
                    undiscoveredFrontier.addAll(unknownNeighbors);
                }else{
                    bordure.remove((Object)i);
                }

            }
        }

        if(sureMoves.isEmpty()){
            recurseCSP(grid,bordure,0);
        }else{
            if(!gameGrid.checkMove(sureMoves)){
                System.out.println("ne devrait pas");
                int sdfsd=0;
            }
        }



    }

    public boolean recurseCSP(CASE[] grid,List<Integer> bordure,int index) throws TimeOver{


        if(timeUp()){
            throw new TimeOver("Time Over");
        }

        if(!allFlagOkey(grid,bordure,index)){
            return false;
        }


        if(index >= bordure.size()){
            int stop=0;

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

        if(undiscovered.size() == 8) {
            System.out.println("dfg");
        }

        nbFlagToPlace+= grid[variableToSatisfy].indexValue;
        if(nbFlagToPlace <0){return false;}
        if(nbFlagToPlace==0){
            CASE[] cpyG = grid.clone();
            return recurseCSP(cpyG,bordure,index+1);}
       /* if(undiscovered.size() == nbFlagToPlace){
            //System.out.println("nope");
            CASE[] cpyG = grid.clone();
            for(Integer i : undiscovered){
                cpyG[i]= FLAGED;
                sureMoves.add(new Move(i,COUP.FLAG));
            }
            return recurseCSP(cpyG,bordure,index+1);
        }*/

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
                if(grid[v] ==FLAGED ){
                    nbFlag++;
                }
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

    public void startTimer(int delai){
        END = false;
        timer = System.currentTimeMillis();
        remain = delai;
    }


   public String showTimeRemain(){
        return ("Time: " + (remain - (System.currentTimeMillis() - timer)) + " ms");
    }

    /**
     * @return Retourne le temps restant
     */
    public long timeRemaining(){
        long passed = (System.currentTimeMillis() - timer);
        return remain - passed;
    }

    /***
     * Indique si le temps est écoulé
     * @return true si temps écoulé
     */
    public boolean timeUp(){
        if(END){
            return true;
        }

        if(timeRemaining() < LIMITE){
            END = true;
            return true;
        }

        return false;
    }





    
}
