package minesweeper.ai.utilCSP;

import minesweeper.Case;
import minesweeper.Direction;
import minesweeper.Grid;

import static minesweeper.Case.*;
import static minesweeper.Direction.*;

import java.util.*;

/**
 * Created by martin on 17/11/14.
 */
public class Graph {

    public int nbFrontiere =0;

    public HashMap<Integer,HintNode> mapHintNode;
    public HashMap<Integer,FringeNode> mapFringeNode;
    public List<List<HintNode>> allHintNode;
    public List<List<FringeNode>> allFringeNodes;
    public Set<Integer> deactivatedNode;
    Grid gameGrid;
    Case[] caseGrille;

    public Graph(Grid gameGrid){
        this.gameGrid = gameGrid;
        caseGrille = gameGrid.getCpyPlayerView();

        deactivatedNode = new HashSet<Integer>();
        mapHintNode = new HashMap<Integer, HintNode>();
        mapFringeNode = new HashMap<Integer, FringeNode>();

        allHintNode = new ArrayList<List<HintNode>>();
        allFringeNodes = new ArrayList<List<FringeNode>>();

        lookForInvalidFringeNode(caseGrille);
        findFrontier(caseGrille);

        nbFrontiere = allHintNode.size();
        int stop=0;

    }



    /*
    * Va chercher les case qui sont entouré de case non-decouvert
    * C'est juste pour eviter que les frontieres fassent des détours
    * */
    void lookForInvalidFringeNode(Case[] grid){
        for(Integer i =0 ; i< grid.length; i++){
            if(Case.isIndicatorCase(grid[i])){
                if(gameGrid.getUndiscoveredneighbours(i).size() == 8){
                    deactivatedNode.add(i);
                }
            }
        }
    }

    /*
    * Va chercher les frontiers qui sont independantes
    * */
    void findFrontier(Case[] grid){
        //Un set pour s'assurer qu'on ne prend pas deux fois le meme noeud;
        Set<Node> inFrontiereSoFar = new HashSet<Node>();
        for (int i =0; i < grid.length; i++){

            if (!inFrontiereSoFar.contains(i) && isAFringeNode(grid,i)){

                FringeNode fringeNode = new FringeNode(i);

                //Va chercher les indices qui influence ce Noeud
                fringeNode.hintNodes = getHintNeirbour(grid,fringeNode);

                /*un Set pour s'assurer un frontiere de noeud unique
                * Et une parce que c'est ce qu'on retourne. ( c'est plus facile d'iterer sur les liste puiqu'on a un index)
                */
                List<FringeNode> front = new ArrayList<FringeNode>();
                /*front.add(fringeNode);
                inFrontiereSoFar.add(fringeNode);*/

                //On ce lance dans la recursion pour accumuler les nodes suivant
                putInFrontier(fringeNode, front, inFrontiereSoFar, grid);

                //Ajoute la frontier accumuler durant la recursion aux Nodes déja visité
                //Je contraint les frontiers a etre au moins plus que 2 sinon on ne peut pas faire grand chose
                //avec ca. Mais c'est peut etre une mauvaise idée
                if (front.size() >= 2){
                    allFringeNodes.add(front); // Un nouvelle frontiere a été trouver. On l'ajoute
                }
            }
        }

        /*
        * Dans certain cas on obtiens des frontieres qui sont lié a 1 seul
         * indice et on ne peut rien faire avec ca donc il faut les supprimer.
         * Cette liste garde les references des liste a effacer
        * */
        List<List<FringeNode>> fringesToRemove = new ArrayList<List<FringeNode>>();

        /*
        * Une fois qu'on a tout les frontieres des case-non decouvertes
        * on veut aussi les indices qui cotoient ces frontieres dans des listes
        * */
        for(List<FringeNode> l : allFringeNodes){
            Set<HintNode> hintNodes = new LinkedHashSet<HintNode>();
            for(FringeNode fn : l){
                //la liste des indices qui influence ce noeud
                hintNodes.addAll(fn.hintNodes);
            }

            //Pour une frontiere acceptable il faut au moins 2 indices (sinon on peut pas vraiment faire de probabilité)
            if(hintNodes.size() >1){
                allHintNode.add(new ArrayList<HintNode>(hintNodes));
            }else{
                fringesToRemove.add(l);
            }
        }
        //Efface les frontieres qui n'on qu'un seul indice
        allFringeNodes.removeAll(fringesToRemove);



    }

    /*
    * Methode qui recurse sur les noeuds et accumuler les nouveau qu'il trouve
    *
    * */
    void putInFrontier(FringeNode startNode, List<FringeNode> hintNodeList, Set<Node> inBorderSoFar, Case[] grid){

        Queue queue = new LinkedList();
        queue.add(startNode);

        //inBorderSoFar.add(startNode);
        mapFringeNode.put(startNode.indexInGrid,startNode);
        hintNodeList.add(startNode);

        while(!queue.isEmpty()){
            FringeNode currentNode = (FringeNode) queue.remove();
            FringeNode nextNode =null;
             /*Va chercher les prochains direction disponible (qui menent a un noeud non visite)*/

            Set<Direction> thisDirection = getPossibleDirection(grid, currentNode.indexInGrid, inBorderSoFar);
            //Si aucun direction alors on backtrack

            if (!(thisDirection == null || thisDirection.isEmpty())){
                for(Direction nextDirection : thisDirection){
                    int next = currentNode.indexInGrid + gameGrid.step(nextDirection);




                    if(!mapFringeNode.containsKey(next)){
                        nextNode = new FringeNode(next);
                        nextNode.hintNodes = getHintNeirbour(grid,nextNode);

                        mapFringeNode.put(nextNode.indexInGrid,nextNode);
                        //inBorderSoFar.add(nextNode);
                        hintNodeList.add(nextNode);
                        queue.add(nextNode);

                        nextNode.fringeNeighbor.add(currentNode);
                        currentNode.fringeNeighbor.add(nextNode);

                    }else{
                        currentNode.fringeNeighbor.add(mapFringeNode.get(next));
                    }

                }
            }

        }

       /* LinkedHashSet<Node> test = new LinkedHashSet<Node>();

        FringeNode TrueStart = hintNodeList.get(hintNodeList.size()-1);

        Set<Node> set = new HashSet<Node>();
        Stack<FringeNode> stack = new Stack<FringeNode>();
        stack.add(TrueStart);
        set.add(TrueStart);
        test.add(TrueStart);

        while (!stack.isEmpty()){
            FringeNode current = stack.pop();
            if(current.fringeNeighbor.isEmpty() ){
                int dsfsdf=0;
            }else{
                for(FringeNode f : current.fringeNeighbor){
                    if(!set.contains(f)){
                        stack.add(f);
                        test.add(f);
                        set.add(f);
                    }
                }
            }
        }*/

        /*Va chercher les prochains direction disponible (qui menent a un noeud non visite)*/
        /*Set<Direction> thisDirection = getPossibleDirection(grid, startNode.indexInGrid, inBorderSoFar);
        //Si aucun direction alors on backtrack
        if (thisDirection == null || thisDirection.isEmpty()){
            return;
        }



        for (Direction nextDir : thisDirection){
            int next = startNode.indexInGrid+gameGrid.step(nextDir);
            FringeNode nextNode = new FringeNode(next);

            //Si ce n'est pas un noeud qu'on connait déja alors on l'ajoute!
            if (!inBorderSoFar.contains(nextNode)){

                // va chercher les indices qui influences ce noeud
                nextNode.hintNodes = getHintNeirbour(grid,nextNode);

                *//*
                * on place le noeud trouver dasns les listes
                * *//*
                inBorderSoFar.add(nextNode);
                hintNodeList.add(nextNode);
                putInFrontier(nextNode, hintNodeList, inBorderSoFar, grid);
            }
        }*/
    }




    /*
    * Va aller chercher les prochaines directions qui menent vers un noeud valide
    * */
    Set<Direction> getPossibleDirection(Case[] grid, int index, Set<Node> frontiere){
        Set<Direction> directions = new LinkedHashSet<Direction>();

        for (Direction D : HUIT_DIRECTIONS){
            int next = index+gameGrid.step(D);


            if (gameGrid.isStepThisDirInGrid(D, index) &&
                    //!frontiere.contains(next) &&
                    isAFringeNode(grid,next))
            {
                Collection<Integer> indiceNeirboursCurrentNode = getIndiceNeirbours(grid,index);
                Collection<Integer> indiceNeirboursNextNode = getIndiceNeirbours(grid,next);

                //Check si les deux noeud partage un indice (Et donc s'influence)
                if(!Collections.disjoint(indiceNeirboursCurrentNode,indiceNeirboursNextNode)){
                    directions.add(D);
                }

            }
        }
        return directions;

    }
    /*
    * Retourne les cases voisines qui sont des index
    * */
    Set<Integer> getIndiceNeirbours(Case[] grid, int index){
        Set<Integer> indices = new LinkedHashSet<Integer>();
        for(Integer i : gameGrid.getSurroundingIndex(index)){
            if(Case.isIndicatorCase(grid[i]) && !deactivatedNode.contains(i)){
                indices.add(i);
            }
        }
        return indices;
    }

    public Set<HintNode> getHintNeirbour(Case[] g, FringeNode fringeNode){

        Set<HintNode> hintNodes = new LinkedHashSet<HintNode>();
        for(Integer indexHint : gameGrid.getSurroundingIndex(fringeNode.indexInGrid)){

            if(Case.isIndicatorCase(g[indexHint])){
                HintNode hn =null;
                if(mapHintNode.containsKey(indexHint)){
                    hn = mapHintNode.get(indexHint);
                }else {
                    hn = new HintNode(indexHint, this.gameGrid.countUnplacedFlags(indexHint));
                    mapHintNode.put(indexHint,hn);
                }
                hintNodes.add(hn);
                hn.connectedFringe.add(fringeNode);
            }

        }
        return hintNodes;

    }


    public boolean isAFringeNode(Case[] grid, int index){

        if(grid[index] == UNDISCOVERED){
            for(Integer surround : gameGrid.getSurroundingIndex(index)){
                if(Case.isIndicatorCase(grid[surround])){
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

        public HintNode(int index, int value){
            super(index);
            this.value = value;
            connectedFringe = new LinkedHashSet<FringeNode>();
        }

        public List<FringeNode> getUndiscoveredFringe() {
            List<Graph.FringeNode> undiscoveredFringe = new ArrayList<Graph.FringeNode>();
            for (Graph.FringeNode fn : connectedFringe) {
                if (fn.state == UNDISCOVERED) {
                    undiscoveredFringe.add(fn);
                }
            }
            return undiscoveredFringe;
        }

        public ArrayList<int[]> getAllFlagCombinations() {
            int[] combination = new int[this.nbFlagToPlace];
            ArrayList<int[]> listCombination = new ArrayList<int[]>();
            generateFlagCombinations(0, this.nbFlagToPlace, this.getUndiscoveredFringe().size(), combination, listCombination);

            return listCombination;
        }

        public void generateFlagCombinations(int index, int nbFlag, int nbCase, int[] combinaison, ArrayList<int[]> listeC) {
            if (nbFlag == 0) {
                return;
            }
            if (index >= nbFlag) {

                int[] newCombinaison = combinaison.clone();
                listeC.add(newCombinaison);
                return;
            }
            int start = 0;
            if (index > 0) start = combinaison[index - 1] + 1;
            for (int i = start; i < nbCase; i++) {
                combinaison[index] = i;
                generateFlagCombinations(index + 1, nbFlag, nbCase, combinaison, listeC);
            }
        }

        public void makeConnectedFringe(Set<Integer> undiscov){
            for(Integer v : undiscov){
                this.connectedFringe.add(new FringeNode(v));
            }
        }

        public boolean isOverAssigned() { return this.nbFlagToPlace < 0; }
        public boolean isSatisfied() { return this.nbFlagToPlace == 0; }

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

    public class FringeNode extends Node implements Comparable {
        public float probabilityMine = 0.5f;
        public int nbFlagsHit = 0;
        public Set<HintNode> hintNodes;
        public Set<FringeNode> fringeNeighbor;
        public Case state = UNDISCOVERED;


        public FringeNode(int index) {
            super(index);
            hintNodes = new LinkedHashSet<HintNode>();
            fringeNeighbor = new LinkedHashSet<FringeNode>();
        }

        public void computeMineProbability(int totalAssignations) {
            this.probabilityMine = (float)this.nbFlagsHit / totalAssignations;
        }

        public boolean isObviousMine() { return this.probabilityMine == 1; }
        public boolean isSafe() { return this.probabilityMine == 0; }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public int compareTo(Object o) {
            FringeNode other = (FringeNode) o;
            if (this.probabilityMine < other.probabilityMine) return -1;
            else if (this.probabilityMine > other.probabilityMine) return 1;
            return 0;
        }

        public String toString() {
            return "Probability of Mine : " + (this.probabilityMine*100) + "%";
        }
    }
}
