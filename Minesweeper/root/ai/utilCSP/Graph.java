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

    public int nbFrontiere =0;
    //public List<List<Node>> allFrontiere;
    /*Premiere version*/
    /*public HashMap<Integer,FringeNode> mapFringeNode;
    public List<List<HintNode>> allHintNode;
    public List<List<FringeNode>> allFringeNodes;*/
    /*Pour la deuxieme version*/
    public HashMap<Integer,HintNode> mapHintNode;
    public List<List<HintNode>> allHintNode;
    public List<List<FringeNode>> allFringeNodes;
    public Set<Integer> deactivatedNode;
    Set<Node> allNode;
    Grid gameGrid;
    CASEGRILLE[] caseGrille;

    public Graph(Grid gameGrid){
        this.gameGrid = gameGrid;
        CASEGRILLE[] c = gameGrid.getCpyPlayerView();
        caseGrille  =c;
    //    allFrontiere = new ArrayList<List<Node>>();
       /* mapFringeNode = new HashMap<Integer, FringeNode>();
        allHintNode = new ArrayList<List<HintNode>>();
        allFringeNodes = new ArrayList<List<FringeNode>>();
*/




      //  allFrontiere = findFrontier(c);

        /*Deuxieme version*/
        deactivatedNode = new HashSet<Integer>();
        mapHintNode = new HashMap<Integer, HintNode>();
        allHintNode = new ArrayList<List<HintNode>>();
        allFringeNodes = new ArrayList<List<FringeNode>>();

        lookForInvalidFringeNode(c);
        findFrontier2(c);

        nbFrontiere = allHintNode.size(); //allHintNode.size();
        int stop=0;

    }



    void lookForInvalidFringeNode(CASEGRILLE[] grid){
        for(Integer i =0 ; i< grid.length; i++){
            if(CASEGRILLE.isIndicatorCase(grid[i])){
                if(getUndiscoveredneighbours(grid,i).size() == 8){
                    deactivatedNode.add(i);
                }
            }
        }
    }

    void findFrontier2(CASEGRILLE[] grid){

        Set<Node> inFrontiereSoFar = new HashSet<Node>();
        for (int i =0; i < grid.length; i++){
            if (!inFrontiereSoFar.contains(i) && isAFringeNode(grid,i)){

                FringeNode fringeNode = new FringeNode(i);

                fringeNode.hintNodes = getHintNeirbour(grid,fringeNode);


                Set<Node> frontHash = new HashSet<Node>();
                List<FringeNode> front = new ArrayList<FringeNode>();
                front.add(fringeNode);
                frontHash.add(fringeNode);


                putInFrontier2(i, front, frontHash, inFrontiereSoFar, grid);
                inFrontiereSoFar.addAll(frontHash);
                if (front.size() >= 2){
                    //test(front);
                    allFringeNodes.add(front);
                    int stop2=0;
                    //allFrontiers.add(front);
                }
            }
        }

        for(List<FringeNode> l : allFringeNodes){
            Set<HintNode> fringe = new LinkedHashSet<HintNode>();
            for(FringeNode fn : l){
                fringe.addAll(fn.hintNodes);
            }
            allHintNode.add(new ArrayList<HintNode>(fringe));

        }


    }

    void putInFrontier2(int nextIndex, List<FringeNode> hintNodeList, Set<Node> hintNodeSet, Set<Node> inBorderSoFar, CASEGRILLE[] grid){

        /*Va chercher les prochains direction disponible (qui menent a un noeud non visite)*/
        Set<Direction> thisDirection = getPossibleDirection(grid, nextIndex, hintNodeSet);
        if (thisDirection == null || thisDirection.isEmpty())
            return;

        for (Direction nextDir : thisDirection){
            int next = nextIndex+gameGrid.step(nextDir);
            FringeNode nextNode = new FringeNode(next);


            if (!hintNodeSet.contains(nextNode) && !inBorderSoFar.contains(nextNode) && !isIndexSatisfied(grid, next)){

                nextNode.hintNodes = getHintNeirbour(grid,nextNode);

                //nextNode.nbFlagToPlace = getNbFlagToPlace(grid,nextIndex);


                hintNodeSet.add(nextNode);hintNodeList.add(nextNode);
                putInFrontier2(next, hintNodeList, hintNodeSet, inBorderSoFar, grid);
            }
        }
    }




    Set<Direction> getPossibleDirection(CASEGRILLE[] grid, int index, Set<Node> frontiere){
        Set<Direction> directions = new LinkedHashSet<Direction>();

        for (Direction D : direction8){
            int next = index+gameGrid.step(D);


            if (gameGrid.isStepThisDirInGrid(D,index) &&
                    !frontiere.contains(next) &&
                    isAFringeNode(grid,next))
            {
                Collection<Integer> indiceNeirboursCurrentNode = getIndiceNeirbours(grid,index);
                Collection<Integer> indiceNeirboursNextNode = getIndiceNeirbours(grid,next);


                if(!Collections.disjoint(indiceNeirboursCurrentNode,indiceNeirboursNextNode)){
                    directions.add(D);
                }

            }
        }
        return directions;

    }
    Set<Integer> getIndiceNeirbours(CASEGRILLE[] grid, int index){
        Set<Integer> indices = new LinkedHashSet<Integer>();
        for(Integer i : gameGrid.getSurroundingIndex(index)){
            if(CASEGRILLE.isIndicatorCase(grid[i]) && !deactivatedNode.contains(i)){
                indices.add(i);
            }
        }
        return indices;
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


    int getNbFlagToPlace(CASEGRILLE[] grid, int index){
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
  /*  public List<FringeNode> getFringeNeirbour(CASEGRILLE[] g, HintNode hintNode){

        List<FringeNode> fringeSet = new ArrayList<FringeNode>();
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

    }*/

    public Set<HintNode> getHintNeirbour(CASEGRILLE[] g, FringeNode fringeNode){

        Set<HintNode> hintNodes = new LinkedHashSet<HintNode>();
        for(Integer indexHint : gameGrid.getSurroundingIndex(fringeNode.indexInGrid)){

            if(CASEGRILLE.isIndicatorCase(g[indexHint])){
                HintNode hn =null;
                if(mapHintNode.containsKey(indexHint)){
                    hn = mapHintNode.get(indexHint);
                }else {
                    hn = new HintNode(indexHint,g[indexHint].indexValue);
                    mapHintNode.put(indexHint,hn);
                }
                hintNodes.add(hn);
                hn.connectedFringe.add(fringeNode);
            }

        }
        return hintNodes;

    }


    public boolean isAFringeNode(CASEGRILLE[] grid, int index){

        if(grid[index] == UNDISCOVERED){
            for(Integer surround : gameGrid.getSurroundingIndex(index)){
                if(CASEGRILLE.isIndicatorCase(grid[surround])){
                    return true;
                }
            }
        }
        return false;
    }




    /*
    * Class interne
    * Node
    *   |- HindNode
    *   |- FringeNode
    *
    * */

    public class Node{

        public int indexInGrid;

        Node(int indexInGrid){
            this.indexInGrid = indexInGrid;
        }

        @Override
        public boolean equals(Object obj) {

            /*
            * Tres wierd et certainement pas comforme aux bonnes pratiques.
            * Permet de comparer un Node avec un Integer
            * */
            if(obj.getClass() == Integer.class){
                if(indexInGrid == (Integer) obj){
                    return true;
                }else {
                    return false;
                }
            }


            Node other = (Node) obj;

            if(indexInGrid == other.indexInGrid){
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return indexInGrid;
        }
    }

    public class HintNode extends Node{

        public Set<FringeNode> connectedFringe;
        public int value;
        public int nbFlagToPlace;
        public int nbUndiscoveredNeighbors;


        public HintNode(int index){
            super(index);
        }
        public HintNode(int index,int value){
            super(index);
            this.value = getNbFlagToPlace(caseGrille,index);
            connectedFringe = new LinkedHashSet<FringeNode>();
        }

        public void makeConnectedFringe(Set<Integer> undiscov){
            for(Integer v : undiscov){
                this.connectedFringe.add(new FringeNode(v));
            }

        }

        /*
        * TODO je ne suis pas sur que ce soit safe
        * Il va regarder autour de lui les case qui on un flag
        * et celle qui sont encore non- decouverte
        * Il va ensuite updater ces varialbes
        * */
        public void updateSurroundingAwareness(){
            int nbFlagToPlace = value;
            int nbHide=0;
            for(FringeNode fn : connectedFringe){
                if(fn.state == FLAGED){
                    nbFlagToPlace--;
                }else if(fn.state == UNDISCOVERED) {
                    nbHide++;
                }
            }
            nbUndiscoveredNeighbors = nbHide;
            this.nbFlagToPlace = nbFlagToPlace;
        }


        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }


    public class FringeNode extends Node{
        public float probabilityMine = 0.5f;
        public int nbFlagHits =0;
        public Set<HintNode> hintNodes;

        public CASEGRILLE state = UNDISCOVERED;


        public FringeNode(int index){
            super(index);
           hintNodes = new LinkedHashSet<HintNode>();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }



}
