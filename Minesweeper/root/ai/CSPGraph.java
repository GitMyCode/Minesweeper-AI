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

            System.out.println("essai avec les resultats csp");
            for(int frontierIndex =0; frontierIndex < graph.nbFrontiere; frontierIndex++){
                List<Graph.FringeNode> fringeNodes = graph.allFringeBorder.get(frontierIndex);

                int nbPossibilityHere = nbMatchByFrontier.get(frontierIndex);
                for(Graph.FringeNode fn : fringeNodes){

                    if(fn.nbFlagHits ==0){
                        movesToPlay.add(new Move(fn.indexInGrid,COUP.SHOW));
                    }else if(fn.nbFlagHits == nbPossibilityHere){
                        movesToPlay.add(new Move(fn.indexInGrid,COUP.FLAG));
                    }
                }

            }


            /*try {
                gameGrid.saveToFile("CSP");
            }catch (Exception e){
                System.out.println(e);
            }
            int t=0;*/

            /*for (int frontierIndex = 0; frontierIndex < nbMatchByFrontier.size(); frontierIndex++){
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

            }*/
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

        int i=0;
        for(List<Graph.HintNode> hintBorder : graph.allHintNode) {
            List<Graph.FringeNode> fringeBorder = graph.allFringeBorder.get(i);
            i++;
            for (Graph.HintNode hintNode : hintBorder) {

                if (hintNode.nbUndiscoveredNeighbors == hintNode.nbFlagToPlace) {
                    for (Graph.FringeNode fringeNode : hintNode.connectedFringe) {
                        movesToPlay.add(new Move(fringeNode.indexInGrid, COUP.FLAG));
                    }
                } else if (hintNode.nbFlagToPlace == 0) {
                    for (Graph.FringeNode fringeNode : hintNode.connectedFringe) {
                        movesToPlay.add(new Move(fringeNode.indexInGrid, COUP.SHOW));
                    }
                }

            }
        }



        if(!movesToPlay.isEmpty()){
            return;
        }
        System.out.println("va pour le csp");
        i=0;
        for(List<Graph.HintNode> hintBorder : graph.allHintNode){
            List<Graph.FringeNode> fringeBorder = graph.allFringeBorder.get(i);
            i++;

            nbPossibilite=0;
            Map<Integer,Integer> mapHitFlags = new HashMap<Integer, Integer>();
            if (movesToPlay.isEmpty()){
                recurseCSP(grid, hintBorder,fringeBorder, mapHitFlags, 0);
            } else if (!gameGrid.checkMove(movesToPlay)){
                    System.out.println("ne devrait pas");
            }

            allHitFlag.add(mapHitFlags);
            nbMatchByFrontier.add(nbPossibilite);
        }

    }

    boolean recurseCSP(CASEGRILLE[] grid, List<Graph.HintNode> hintNodes, List<Graph.FringeNode> fringeNodes,
                       Map<Integer, Integer> mapFlagHit, int index) throws TimeOver{


        if (timeUp()){
            throw new TimeOver();
        }


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

        Graph.HintNode variableToSatisfy = hintNodes.get(index);
        variableToSatisfy.updateSurroundingAwareness();

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

        if (variableToSatisfy.nbFlagToPlace <0){
            System.out.println("nb flag a placer negatif!");
            return false;}
        if (variableToSatisfy.nbFlagToPlace==0){
            //CASEGRILLE[] cpyG = grid.clone();
            return recurseCSP(grid,hintNodes,fringeNodes,mapFlagHit,index+1);
        }

        int[] combinaison = new int[variableToSatisfy.nbFlagToPlace];
        ArrayList<int[]> listC = new ArrayList<int[]>();
        combinaisonFlag(0,variableToSatisfy.nbFlagToPlace,variableToSatisfy.nbUndiscoveredNeighbors
                ,combinaison,listC);




        for (int[] list : listC){

            //Garder en  memoire le nb de flag a placer ici parce que variableToSatisfy va changer au moment de recurser
            int nbFlagToPlaceHere = variableToSatisfy.nbFlagToPlace;


            for (int i=0; i< nbFlagToPlaceHere; i++){
                Graph.FringeNode fringeToFlag = undiscoveredFringe.get(list[i]);
                fringeToFlag.state = FLAGED;
            }

            if(!recurseCSP(grid,hintNodes,fringeNodes,mapFlagHit,index+1)){
                for (int i=0; i< nbFlagToPlaceHere; i++){
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


}
