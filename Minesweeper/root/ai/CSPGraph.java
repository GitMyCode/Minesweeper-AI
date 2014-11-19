package root.ai;

import root.*;
import root.ENUM.CASEGRILLE;
import root.ENUM.COUP;
import root.ai.utilCSP.Graph;
import root.ai.utilCSP.TimeOver;

import static root.ENUM.CASEGRILLE.*;

import java.util.*;

/**
 * Created by martin on 18/11/14.
 */
public class CSPGraph implements ArtificialPlayer {


    /*Timer*/
    private long timer;
    private long remain;
    private boolean END = false;
    private final int LIMITE = 10;


    private Grid gameGrid;
    private Set<Move> movesToPlay;

    //Set<Integer> undiscoveredFrontier;
    private Map<Integer,Integer> possibleMine;
    private List<Integer> nbMatchByFrontier;
    private List<List<Integer>> allFrontiere;
    private List<Set<Integer>> allUndiscovFrontier;
    private List<Map<Integer,Integer>> allHitFlag;
    private Integer  nbPossibilite =0;


    Graph graph;
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
        movesToPlay = new HashSet<Move>();
        possibleMine = new HashMap<Integer, Integer>();
        //undiscoveredFrontier = new HashSet<Integer>();
        allUndiscovFrontier = new ArrayList<Set<Integer>>();
        allFrontiere = new ArrayList<List<Integer>>();
        allHitFlag = new ArrayList<Map<Integer, Integer>>();
        nbMatchByFrontier = new ArrayList<Integer>();





        try {
            calculateMoves(g);
        } catch (TimeOver ignored){

        }


        if (movesToPlay.isEmpty()){
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
                            movesToPlay.add(new Move(sur, COUP.SHOW));
                            if(!gameGrid.checkMove(movesToPlay)){
                                int adas=0;
                            }
                        } else if ( copyGrid[sur] == UNDISCOVERED && flagHits.get(sur) >=nbPossibilityHere){
                            if(flagHits.get(sur) > nbPossibilityHere){
                                System.out.println("weird");
                            }
                            movesToPlay.add(new Move(sur, COUP.FLAG));
                            if (!gameGrid.checkMove(movesToPlay)){
                                int adas=0;
                            }
                        }

                    }
                }

            }
        }


        if (movesToPlay.isEmpty()){
            //int bestChance =Integer.MAX_VALUE;

        }
        if (!gameGrid.checkMove(movesToPlay)){
            System.out.println(" Problem and is timeout:"+(timeUp())+"   grid is valid?:" +gameGrid.checkIfPresentGridValid());
        }
        if (timeUp()){
            System.out.println("Time UP!");
        }

        /*
        if(movesToPlay.isEmpty() && bestChance != Integer.MAX_VALUE){

            System.out.println("best chance: "+ bestChance);
            movesToPlay.add(new Move(bestChance,SHOW));
            return movesToPlay;
        }*/

        if (movesToPlay.isEmpty()){
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
            movesToPlay.add(new Move(index,COUP.SHOW));
        }

        return movesToPlay;
    }



    @Override
    public String getName() {
        return "CSP-Martin";
    }

    void calculateMoves(Grid g) throws TimeOver{

        CASEGRILLE[] grid = g.getCpyPlayerView();



        graph = new Graph(g);

       // allFrontiere =

        //int stop = 0;

        if(!movesToPlay.isEmpty()){
            return;
        }
        int i=0;
        for(List<Graph.HintNode> hintBorder : graph.allHintNode){
            List<Graph.FringeNode> fringeBorder = graph.allFringeBorder.get(i);
            i++;
            for(Graph.HintNode hintNode : hintBorder){

                if(hintNode.connectedFringe.size() == hintNode.nbFlagToPlace){
                    for(Graph.FringeNode fringeNode : hintNode.connectedFringe){
                        movesToPlay.add(new Move(fringeNode.indexInGrid,COUP.FLAG));
                    }
                }else if(hintNode.nbFlagToPlace ==0 ){
                    for(Graph.FringeNode fringeNode : hintNode.connectedFringe){
                        movesToPlay.add(new Move(fringeNode.indexInGrid,COUP.SHOW));
                    }
                }

            }



/*
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
                            movesToPlay.add(new Move(v2, COUP.SHOW));
                        }
                    }
                } else if((nbFlaged-grid[i].indexValue) == unknownNeighbors.size()){
                    //bordure.remove((Object) i);
                    if (unknownNeighbors.size()!=0){
                        for (Integer v2 : unknownNeighbors){
                            movesToPlay.add(new Move(v2, COUP.FLAG));
                        }

                    }
                }
            }*/

            nbPossibilite=0;
            Map<Integer,Integer> mapHitFlags = new HashMap<Integer, Integer>();
            if (movesToPlay.isEmpty()){
                recurseCSP(grid, hintBorder,fringeBorder, mapHitFlags, 0);
            } else if (!gameGrid.checkMove(movesToPlay)){
                    System.out.println("ne devrait pas");
            }

            allUndiscovFrontier.add(undiscovFrontier);
            allHitFlag.add(mapHitFlags);
            nbMatchByFrontier.add(nbPossibilite);
        }

    }

    boolean recurseCSP(CASEGRILLE[] grid, List<Graph.HintNode> hintNodes, List<Graph.FringeNode> fringeNodes,
                       Map<Integer, Integer> mapFlagHit, int index) throws TimeOver{


        if (timeUp()){
            throw new TimeOver();
        }

        Graph.HintNode variableToSatisfy = hintNodes.get(index);
        variableToSatisfy.updateSurroundingAwareness();

        if (!allFlagsOkay(grid, hintNodes, index)){
            return false;
        }


        if (index >= hintNodes.size()){
            //int stop=0;

            for(Graph.FringeNode fn : fringeNodes){
                if(fn.state == FLAGED){
                    fn.nbFlagHits++;
                    //int lastTimeFlaged = (mapFlagHit.containsKey(fn))? mapFlagHit.get(fn)+1 : 1;
                   // mapFlagHit.put(fn, lastTimeFlaged);
                }
            }
            nbPossibilite++;
            return true;
        }


        List<Graph.FringeNode> neighborsFringe = variableToSatisfy.connectedFringe;

        List<Graph.FringeNode> undiscoveredFringe = new ArrayList<Graph.FringeNode>();
        for(Graph.FringeNode fn : neighborsFringe){
            if(fn.state == UNDISCOVERED){
                undiscoveredFringe.add(fn);
            }
        }

       /* List<Integer> surrounding = gameGrid.getSurroundingIndex(variableToSatisfy);
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

        nbFlagToPlace+= grid[variableToSatisfy].indexValue;*/

        if (variableToSatisfy.nbFlagToPlace <0){return false;}
        if (variableToSatisfy.nbFlagToPlace==0){
            //CASEGRILLE[] cpyG = grid.clone();
            return recurseCSP(grid,hintNodes,fringeNodes,mapFlagHit,index+1);
        }

        int[] combinaison = new int[variableToSatisfy.nbFlagToPlace];
        ArrayList<int[]> listC = new ArrayList<int[]>();
        combinaisonFlag(0,variableToSatisfy.nbFlagToPlace,variableToSatisfy.nbUndiscoveredNeighbors
                ,combinaison,listC);




        for (int[] list : listC){
            //CASEGRILLE[] gCpy = grid.clone();
            for (int i=0; i< variableToSatisfy.nbFlagToPlace; i++){
                Graph.FringeNode fringeToFlag = undiscoveredFringe.get(list[i]);
                fringeToFlag.state = FLAGED;
            }

            if(!recurseCSP(grid,hintNodes,fringeNodes,mapFlagHit,index+1)){
                for (int i=0; i< variableToSatisfy.nbFlagToPlace; i++){
                    Graph.FringeNode fringeToFlag = undiscoveredFringe.get(list[i]);
                    fringeToFlag.state = UNDISCOVERED;
                }
            }
        }

        return false;
    }



    boolean allFlagsOkay(CASEGRILLE[] grid, List<Graph.HintNode> hintNodes, int nbDone){
        for (int i=0; i< nbDone;i++){
            Graph.HintNode hintNode = hintNodes.get(i);
            int value = hintNode.value;

            List<Graph.FringeNode> neighborsFringe = hintNode.connectedFringe;

            int nbFlag=0;
            for(Graph.FringeNode fn : neighborsFringe){
                if(fn.state == FLAGED){
                    nbFlag++;
                }
            }
            if(nbFlag != value){
                return false;
            }
           /*
            List<Integer> voisins = gameGrid.getSurroundingIndex(hintNode);
            for (Integer v : voisins){
                if(grid[v] ==FLAGED ){
                    nbFlag++;
                }
            }
            if (nbFlag != value){
                return false;
            }*/
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

/*
    List<List<Integer>> findFrontier(CASEGRILLE[] grid){
        List<List<Integer>> allFrontiers = new LinkedList<List<Integer>>();
        Set<Integer> inFrontiereSoFar = new HashSet<Integer>();
        for (int i =0; i < grid.length; i++){
            if (CASEGRILLE.isIndicatorCase(grid[i])){
                if (isIndexSatisfied(grid, i)){
                    for(Integer c: getUndiscoveredneighbours(grid,i)){
                        movesToPlay.add(new Move(c, SHOW));
                    }

                } else if(nbFlagToPlace(grid,i) == getUndiscoveredneighbours(grid,i).size()) {
                    for (Integer v : getUndiscoveredneighbours(grid,i)) {
                        movesToPlay.add(new Move(v, FLAG));
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
   }*/

}
