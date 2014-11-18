package root.ai.utilCSP;

import root.Direction;
import root.ENUM.CASEGRILLE;
import root.Grid;

import static root.ENUM.CASEGRILLE.*;
import static root.Direction.*;

import java.util.*;

/**
 * Created by martin on 17/11/14.
 */
public class Graph {

    Set<Node> allNode;
    Grid gameGrid;

    public List<List<Node>> allFrontiere;



    public HashMap<Integer,FringeNode> mapFringeNode;
    public List<List<HintNode>> allHintNode;
    public List<List<FringeNode>> allFringeBorder;

    public Graph(Grid gameGrid){
        this.gameGrid = gameGrid;
        CASEGRILLE[] c = gameGrid.getCpyPlayerView();

        mapFringeNode = new HashMap<Integer, FringeNode>();
        allHintNode = new ArrayList<List<HintNode>>();
        allFringeBorder = new ArrayList<List<FringeNode>>();


        allFrontiere = findFrontier(c);
        int stop=0;

    }



    List<List<Node>> findFrontier(CASEGRILLE[] grid){




        List<List<Node>> allFrontiers = new LinkedList<List<Node>>();
        Set<HintNode> inFrontiereSoFar = new HashSet<HintNode>();
        for (int i =0; i < grid.length; i++){
            if (CASEGRILLE.isIndicatorCase(grid[i])){

                HintNode hintNode = new HintNode(i,grid[i].indexValue);

                if(!inFrontiereSoFar.contains(hintNode)){



                    hintNode.connectedFringe = getFringeNeirbour(grid,hintNode);

                    if (isIndexSatisfied(grid, i)){
                        for(Integer c: getUndiscoveredneighbours(grid,i)){
                            //movesToPlay.add(new Move(c, SHOW));

                        }

                    } else if(nbFlagToPlace(grid,i) == getUndiscoveredneighbours(grid,i).size()) {
                        for (Integer v : getUndiscoveredneighbours(grid,i)) {
                            //movesToPlay.add(new Move(v, FLAG));
                        }





                        //Va chercher la prochaine frontieres
                    } else if( !inFrontiereSoFar.contains(hintNode)){
                        Set<HintNode> frontHash = new HashSet<HintNode>();
                        List<HintNode> front = new ArrayList<HintNode>();
                        front.add(hintNode);
                        frontHash.add(hintNode);


                        putInFrontier(i,front,frontHash,inFrontiereSoFar,grid);
                        inFrontiereSoFar.addAll(frontHash);
                        if (front.size() >= 2){
                            test(front);
                            allHintNode.add(front);
                            //allFrontiers.add(front);
                        }
                    }
                }
            }
        }

        for(List<HintNode> l : allHintNode){
            List<FringeNode> fringe = new ArrayList<FringeNode>();
            for(HintNode hn : l){
                fringe.addAll(hn.connectedFringe);
            }
            allFringeBorder.add(fringe);

        }



        return allFrontiers;
    }

    public Set<FringeNode> getFringeNeirbour(CASEGRILLE[] g, HintNode hintNode){

        Set<FringeNode> fringeSet = new LinkedHashSet<FringeNode>();
        for(Integer indexFringe : getUndiscoveredneighbours(g,hintNode.indexInGrid)){
            if(mapFringeNode.containsKey(indexFringe)){
                FringeNode fn = mapFringeNode.get(indexFringe);
                fn.hintNodes.add(hintNode);
                fringeSet.add(fn);

            }else {
                FringeNode fn = new FringeNode(indexFringe);
                fn.hintNodes.add(hintNode);
                fringeSet.add(fn);
                mapFringeNode.put(indexFringe,fn);
            }

        }
        return fringeSet;

    }

    public void test(List<? extends Node> listToAdd){
        allFrontiere.add(new ArrayList<Node>( listToAdd));
    }

    void putInFrontier(int nextIndex, List<HintNode> front, Set<HintNode> frontiereHash, Set<HintNode> allFront, CASEGRILLE[] grid){

        /*Va chercher les prochains direction disponible (qui menent a un noeud non visite)*/
        Set<Direction> thisDirection = getPossibleDirection(grid, nextIndex, frontiereHash);
        if (thisDirection == null || thisDirection.isEmpty())
            return;

        for (Direction nextDir : thisDirection){
            int next = nextIndex+gameGrid.step(nextDir);
            HintNode nextNode = new HintNode(next,grid[next].indexValue);


            if (!frontiereHash.contains(nextNode) && !allFront.contains(nextNode) && !isIndexSatisfied(grid, next)){
                frontiereHash.add(nextNode);front.add(nextNode);

                putInFrontier(next,front,frontiereHash,allFront,grid);
            }
        }
    }



    Set<Direction> getPossibleDirection(CASEGRILLE[] grid, int index, Set<HintNode> frontiere){
        Set<Direction> directions = new LinkedHashSet<Direction>();

        int nbDirCardinal =0;
        for (Direction D : direction8){
            int next = index+gameGrid.step(D);
            HintNode nextNode = new HintNode(next,grid[next].indexValue);

            if (gameGrid.isStepThisDirInGrid(D,index) && !frontiere.contains(nextNode) && CASEGRILLE.isIndicatorCase(grid[next])
                    ){
                directions.add(D);
                if (D.getCompDir().size() ==1){
                    nbDirCardinal++;
                }

            }
        }
        return directions;

    }

    Set<Integer> getUndiscoveredneighbours(CASEGRILLE[] grid, int index){
        Set<Integer> undiscovered = new LinkedHashSet<Integer>();
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



    public class Node{

        int indexInGrid;

        Node(int indexInGrid){
            this.indexInGrid = indexInGrid;
        }

        @Override
        public boolean equals(Object obj) {
            Node other = (Node) obj;

            if(indexInGrid == other.indexInGrid){
                return true;
            }
            return false;
        }
    }

    public class HintNode extends Node{

        Set<FringeNode> connectedFringe;
        public int value;

        public HintNode(int index,int value){
            super(index);
            this.value = value;
            connectedFringe = new LinkedHashSet<FringeNode>();
        }

        public void makeConnectedFringe(Set<Integer> undiscov){
            for(Integer v : undiscov){
                this.connectedFringe.add(new FringeNode(v));
            }

        }


        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }

    public class FringeNode extends Node{
        float probabilityMine;
        int nbFlagHits =0;
        List<HintNode> hintNodes;

        public CASEGRILLE state = UNDISCOVERED;


        public FringeNode(int index){
            super(index);
           hintNodes = new ArrayList<HintNode>();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }



}
