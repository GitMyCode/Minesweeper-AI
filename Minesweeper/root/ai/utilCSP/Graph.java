package root.ai.utilCSP;

import root.Direction;
import root.ENUM.CASEGRILLE;
import root.Grid;
import root.Move;

import static root.ENUM.CASEGRILLE.*;
import static root.ENUM.COUP.*;
import static root.Direction.*;

import java.util.*;

/**
 * Created by martin on 17/11/14.
 */
public class Graph {

    Set<Node> allNode;
    Grid gameGrid;

    public     List<List<Integer>> allFrontiere;


    public Graph(Grid gameGrid){
        this.gameGrid = gameGrid;
        CASEGRILLE[] c = gameGrid.getCpyPlayerView();
        allFrontiere = findFrontier(c);

    }



    List<List<Integer>> findFrontier(CASEGRILLE[] grid){
        List<List<Integer>> allFrontiers = new LinkedList<List<Integer>>();
        Set<Integer> inFrontiereSoFar = new HashSet<Integer>();
        for (int i =0; i < grid.length; i++){
            if (CASEGRILLE.isIndicatorCase(grid[i])){
                if (isIndexSatisfied(grid, i)){
                    for(Integer c: getUndiscoveredneighbours(grid,i)){
                        //movesToPlay.add(new Move(c, SHOW));
                    }

                } else if(nbFlagToPlace(grid,i) == getUndiscoveredneighbours(grid,i).size()) {
                    for (Integer v : getUndiscoveredneighbours(grid,i)) {
                        //movesToPlay.add(new Move(v, FLAG));
                    }





                //Va chercher la prochaine frontieres
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

    public class IndexNode extends Node{

        List<FringeNode> connectedFringe;

        public IndexNode(int index){
            super(index);
        }


        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }

    public class FringeNode extends Node{
        float probability;
        int nbFlagHits =0;
        List<IndexNode> indexNodes;

        public FringeNode(int index){
            super(index);
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }



}
