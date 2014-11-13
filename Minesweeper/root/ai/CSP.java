package root.ai;

import root.*;
import root.ENUM.CASEGRILLE;
import root.ENUM.COUP;
import root.ai.utilCSP.TimeOver;

import static root.ENUM.CASEGRILLE.*;
import static root.ENUM.COUP.*;

import java.util.*;
import static root.Direction.*;

/**
 * Created by MB on 10/31/2014.
 */
public class CSP implements ArtificialPlayer{


    /*Timer*/
    private long timer;
    private long remain;
    private boolean END = false;
    private final int LIMITE = 10;


    private Grid gameGrid;
    private Set<Move> sureMoves;

    //Set<Integer> undiscoveredFrontier;
    private Map<Integer,Integer> possibleMine;
    private List<Integer> nbMatchByFrontier;
    private List<List<Integer>> allFrontiere;
    private List<Set<Integer>> allUndiscovFrontier;
    private List<Map<Integer,Integer>> allHitFlag;
    private Integer  nbPossibilite =0;


    @Override
    public Set<Move> getNextMoves(Grid g, int thinkLimit) {

        /*TODO
        * 1) Splitter les frontieres en plusieurs frontiere indépendante
        * 2) Ordonner les variables pour qu'elle soient voisines (Heuristique de CSP)
        * 3) FAIRE LE MENAGE DANS CE BORDEL
        * */

        gameGrid = g;
        CASEGRILLE[] copyGrid = g.getCpyPlayerView();
        startTimer(thinkLimit);


        nbPossibilite =0;
        sureMoves = new HashSet<Move>();
        possibleMine = new HashMap<Integer, Integer>();
        //undiscoveredFrontier = new HashSet<Integer>();
        allUndiscovFrontier = new ArrayList<Set<Integer>>();
        allFrontiere = new ArrayList<List<Integer>>();
        allHitFlag = new ArrayList<Map<Integer, Integer>>();
        nbMatchByFrontier = new ArrayList<Integer>();


        try {
            getSureCoup(g);
        } catch (TimeOver ignored){

        }


        if (sureMoves.isEmpty()){
            /*try {
                gameGrid.saveToFile("CSP");
            }catch (Exception e){
                System.out.println(e);
            }
            int t=0;*/
            for (int frontierIndex = 0; frontierIndex < nbMatchByFrontier.size(); frontierIndex++){
                List<Integer> frontier = allFrontiere.get(frontierIndex);
                int nbPossibilityHere = nbMatchByFrontier.get(frontierIndex);
                Map<Integer,Integer> flagHits = allHitFlag.get(frontierIndex);
                for(Integer b : frontier){
                    for (Integer sur : gameGrid.getSurroundingIndex(b)){
                        if (copyGrid[sur] == UNDISCOVERED && !flagHits.containsKey(sur)){
                            sureMoves.add(new Move(sur, COUP.SHOW));
                            if(!gameGrid.checkMove(sureMoves)){
                                int adas=0;
                            }
                        } else if ( copyGrid[sur] == UNDISCOVERED && flagHits.get(sur) >=nbPossibilityHere){
                            if(flagHits.get(sur) > nbPossibilityHere){
                                System.out.println("wird");
                            }
                            sureMoves.add(new Move(sur, COUP.FLAG));
                            if (!gameGrid.checkMove(sureMoves)){
                                int adas=0;
                            }
                        }

                    }
                }

            }
        }


        if (sureMoves.isEmpty()){
            //int bestChance =Integer.MAX_VALUE;

        }
        if (!gameGrid.checkMove(sureMoves)){
            System.out.println(" Problem and is timeout:"+(timeUp())+"   grid is valid?:" +gameGrid.checkIfPresentGridValid());
        }
        if (timeUp()){
            System.out.println("Time UP!");
        }

        /*
        if(sureMoves.isEmpty() && bestChance != Integer.MAX_VALUE){

            System.out.println("best chance: "+ bestChance);
            sureMoves.add(new Move(bestChance,SHOW));
            return sureMoves;
        }*/

        if (sureMoves.isEmpty()){
            List<Integer> legalMoves = new ArrayList<Integer>();
            for(int i=0; i< g.length; i++){
                if(copyGrid[i] == UNDISCOVERED){
                    legalMoves.add(i);
                }
            }
            /*if(legalMoves.size() > undiscoveredFrontier.size()){
                legalMoves.removeAll(undiscoveredFrontier);
            }*/
            Random ran = new Random();
            int index = legalMoves.get(ran.nextInt(legalMoves.size()));
            sureMoves.add(new Move(index,COUP.SHOW));
        }

        return sureMoves;
    }



    @Override
    public String getName() {
        return "CSP-Martin";
    }

    void getSureCoup(Grid g) throws TimeOver{

        CASEGRILLE[] grid = g.getCpyPlayerView();

        allFrontiere = findFrontier(grid);

        //int stop = 0;

        if(!sureMoves.isEmpty()){
            return;
        }

        for (List<Integer> oneFrontiere : allFrontiere){

            Set<Integer> undiscovFrontier = new HashSet<Integer>();

            for (Integer i : oneFrontiere){
                List<Integer> unknownNeighbors = new ArrayList<Integer>();
                List<Integer> voisins = gameGrid.getSurroundingIndex(i);
                int nbFlaged =0;
                for (Integer v : voisins){
                    if (grid[v] == UNDISCOVERED){
                        unknownNeighbors.add(v);
                        undiscovFrontier.add(v);
                    } else if(grid[v] == FLAGED){
                        nbFlaged++;
                    }
                }
                if (nbFlaged == grid[i].indexValue){
                    //bordure.remove((Object) i);
                    if (unknownNeighbors.size()!=0){
                        for (Integer v2 : unknownNeighbors){
                            sureMoves.add(new Move(v2,COUP.SHOW));
                        }
                    }
                } else if((nbFlaged-grid[i].indexValue) == unknownNeighbors.size()){
                    //bordure.remove((Object) i);
                    if (unknownNeighbors.size()!=0){
                        for (Integer v2 : unknownNeighbors){
                            sureMoves.add(new Move(v2,COUP.FLAG));
                        }

                    }
                }
            }

            nbPossibilite=0;
            Map<Integer,Integer> mapHitFlags = new HashMap<Integer, Integer>();
            if (sureMoves.isEmpty()){
                recurseCSP(grid, oneFrontiere, undiscovFrontier,mapHitFlags, 0);
            } else if (!gameGrid.checkMove(sureMoves)){
                    System.out.println("ne devrait pas");
            }

            allUndiscovFrontier.add(undiscovFrontier);
            allHitFlag.add(mapHitFlags);
            nbMatchByFrontier.add(nbPossibilite);
        }
    }

    boolean recurseCSP(CASEGRILLE[] grid, List<Integer> bordure, Set<Integer> undiscoveredFrontier, Map<Integer, Integer> mapFlagHit, int index) throws TimeOver{


        if (timeUp()){
            throw new TimeOver();
        }

        if (!allFlagsOkay(grid, bordure, index)){
            return false;
        }


        if (index >= bordure.size()){
            //int stop=0;

            for(Integer i : undiscoveredFrontier){
                if(grid[i] == FLAGED){
                    int lastTimeFlaged = (mapFlagHit.containsKey(i))? mapFlagHit.get(i)+1 : 1;
                    mapFlagHit.put(i, lastTimeFlaged);
                }
            }
            nbPossibilite++;
            return true;
        }

        int variableToSatisfy = bordure.get(index);

        List<Integer> surrounding = gameGrid.getSurroundingIndex(variableToSatisfy);
        List<Integer> undiscovered = new ArrayList<Integer>();
        int nbFlagToPlace=0;
        for (Integer i : surrounding){
            if (grid[i]==FLAGED){
                nbFlagToPlace--;
            }
            if (grid[i] == UNDISCOVERED){
                undiscovered.add(i);
            }
        }

        if (undiscovered.size() == 8) {
            System.out.println("dfg");
        }

        nbFlagToPlace+= grid[variableToSatisfy].indexValue;
        if (nbFlagToPlace <0){return false;}
        if (nbFlagToPlace==0){
            CASEGRILLE[] cpyG = grid.clone();
            return recurseCSP(cpyG,bordure,undiscoveredFrontier,mapFlagHit,index+1);
        }


        int[] combinaison = new int[nbFlagToPlace];
        ArrayList<int[]> listC = new ArrayList<int[]>();
        combinaisonFlag(0,nbFlagToPlace,undiscovered.size(),combinaison,listC);

        for (int[] list : listC){
            CASEGRILLE[] gCpy = grid.clone();
            for (int i=0; i< nbFlagToPlace; i++){
                int indexToFlag = undiscovered.get(list[i]);
                gCpy[indexToFlag] = FLAGED;
            }

            recurseCSP(gCpy,bordure,undiscoveredFrontier,mapFlagHit,index+1);
        }

        return false;
    }



    boolean allFlagsOkay(CASEGRILLE[] grid, List<Integer> bordure, int nbDone){
        for (int i=0; i< nbDone;i++){
            int index = bordure.get(i);
            int value = grid[index].indexValue;
            List<Integer> voisins = gameGrid.getSurroundingIndex(index);
            int nbFlag=0;
            for (Integer v : voisins){
                if(grid[v] ==FLAGED ){
                    nbFlag++;
                }
            }
            if (nbFlag != value){
                return false;
            }
        }
        return true;
    }


    void combinaisonFlag(int index, int nbFlag, int nbCase, int[] combinaison, ArrayList<int[]> listeC){
        if (nbFlag ==0){return;}
        if (index >= nbFlag){

            int[] newCombinaison = combinaison.clone();
            listeC.add(newCombinaison);
            return ;
        }
        int start =0;
        if (index >0) start = combinaison[index-1]+1;
        for (int i=start; i<nbCase;i++){
            combinaison[index]=i;
            combinaisonFlag(index+1, nbFlag, nbCase, combinaison, listeC);
        }
    }

    void startTimer(int delai){
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


    long timeRemaining(){
        long passed = (System.currentTimeMillis() - timer);
        return remain - passed;
    }

    /***
     * Indique si le temps est écoulé
     * @return true si temps écoulé
     */
    boolean timeUp(){
        if (END){
            return true;
        }

        if (timeRemaining() < LIMITE){
            END = true;
            return true;
        }

        return false;
    }


    public List<Integer> getIndexNotInFrontier (int index, Set<Integer> frontiere){
        List<Integer> nextToFrontieres = new ArrayList<Integer>();
        List<Integer> voisins = gameGrid.getSurroundingIndex(index);
        for (Integer v : voisins){
            if (!frontiere.contains(v)){
                nextToFrontieres.add(v);
            }
        }
        return nextToFrontieres;
    }


    List<List<Integer>> findFrontier(CASEGRILLE[] grid){
        List<List<Integer>> allFrontiers = new LinkedList<List<Integer>>();
        Set<Integer> inFrontiereSoFar = new HashSet<Integer>();
        for (int i =0; i < grid.length; i++){
            if (CASEGRILLE.isIndicatorCase(grid[i])){
                if (isIndexSatisfied(grid, i)){
                    for(Integer c: getUndiscoveredneighbours(grid,i)){
                        sureMoves.add(new Move(c,SHOW));
                    }

                } else if(nbFlagToPlace(grid,i) == getUndiscoveredneighbours(grid,i).size()) {
                    for (Integer v : getUndiscoveredneighbours(grid,i)) {
                        sureMoves.add(new Move(v,FLAG));
                    }
                } else if( !inFrontiereSoFar.contains(i)){
                    Set<Integer> frontHash = new HashSet<Integer>();
                    List<Integer> front = new ArrayList<Integer>();
                    front.add(i);
                    frontHash.add(i);


                    putInFrontier(i,front,frontHash,inFrontiereSoFar,grid);
                    inFrontiereSoFar.addAll(frontHash);
                    if (front.size() >= 2){
                        allFrontiers.add(front);
                    }
                }
            }


        }
        return allFrontiers;
    }

    void putInFrontier(int nextIndex, List<Integer> front, Set<Integer> frontiereHash, Set<Integer> allFront, CASEGRILLE[] grid){

        Set<Direction> thisDirection = getPossibleDirection(grid, nextIndex, frontiereHash);
        if (thisDirection == null || thisDirection.isEmpty())
            return;

        Direction nextDirection;
        for (Direction d : thisDirection){
            nextDirection =d;
            int next = nextIndex+gameGrid.step(nextDirection);
            if (!frontiereHash.contains(next) && !allFront.contains(next) && !isIndexSatisfied(grid, next)){
                frontiereHash.add(next);front.add(next);
                putInFrontier(next,front,frontiereHash,allFront,grid);
            }
        }
    }



    Set<Direction> getPossibleDirection(CASEGRILLE[] grid, int index, Set<Integer> frontiere){
        Set<Direction> direction = new LinkedHashSet<Direction>();

        int nbDirCardinal =0;
        for (Direction D : direction8){
            int next = index+gameGrid.step(D);
            if (gameGrid.isStepThisDirInGrid(D,index) && !frontiere.contains(next) && CASEGRILLE.isIndicatorCase(grid[next])
                    ){
                direction.add(D);
                if (D.getCompDir().size() ==1){
                    nbDirCardinal++;
                }

            }
        }
        return direction;

    }


    Set<Integer> getUndiscoveredneighbours(CASEGRILLE[] grid, int index){
        Set<Integer> undiscovered = new HashSet<Integer>();
        for (Integer i: gameGrid.getSurroundingIndex(index)){
            if (grid[i] == UNDISCOVERED){
                undiscovered.add(i);
            }
        }
        return undiscovered;
    }


    int nbFlagToPlace(CASEGRILLE[] grid, int index){
        int nbFlagRemaining = grid[index].indexValue;
        for (Integer v : gameGrid.getSurroundingIndex(index)){
            if (grid[v] == FLAGED){
                nbFlagRemaining--;
            }
        }
        return nbFlagRemaining;
    }


   boolean isIndexSatisfied(CASEGRILLE[] grid, int index){
       int indice = grid[index].indexValue;
       int nbFlagPosed =0;
       for (Integer v: gameGrid.getSurroundingIndex(index)){
            if (grid[v] == FLAGED){
                nbFlagPosed++;
            }
       }
       return indice == nbFlagPosed;
   }
    
}
