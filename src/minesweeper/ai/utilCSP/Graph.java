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
                if(getUndiscoveredneighbours(grid,i).size() == 8){
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
                Set<Node> frontHash = new HashSet<Node>();
                List<FringeNode> front = new ArrayList<FringeNode>();
                front.add(fringeNode);
                frontHash.add(fringeNode);


                //On ce lance dans la recursion pour accumuler les nodes suivant
                putInFrontier(i, front, frontHash, inFrontiereSoFar, grid);

                //Ajoute la frontier accumuler durant la recursion aux Nodes déja visité
                inFrontiereSoFar.addAll(frontHash);
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
    void putInFrontier(int nextIndex, List<FringeNode> hintNodeList, Set<Node> hintNodeSet, Set<Node> inBorderSoFar, Case[] grid){

        /*Va chercher les prochains direction disponible (qui menent a un noeud non visite)*/
        Set<Direction> thisDirection = getPossibleDirection(grid, nextIndex, hintNodeSet);
        //Si aucun direction alors on backtrack
        if (thisDirection == null || thisDirection.isEmpty()){
            return;
        }


        for (Direction nextDir : thisDirection){
            int next = nextIndex+gameGrid.step(nextDir);
            FringeNode nextNode = new FringeNode(next);

            //Si ce n'est pas un noeud qu'on connait déja alors on l'ajoute!
            if (!hintNodeSet.contains(nextNode) && !inBorderSoFar.contains(nextNode)){

                // va chercher les indices qui influences ce noeud
                nextNode.hintNodes = getHintNeirbour(grid,nextNode);

                /*
                * on place le noeud trouver dasns les listes
                * */
                hintNodeSet.add(nextNode);
                hintNodeList.add(nextNode);
                putInFrontier(next, hintNodeList, hintNodeSet, inBorderSoFar, grid);
            }
        }
    }




    /*
    * Va aller chercher les prochaines directions qui menent vers un noeud valide
    * */
    Set<Direction> getPossibleDirection(Case[] grid, int index, Set<Node> frontiere){
        Set<Direction> directions = new LinkedHashSet<Direction>();

        for (Direction D : direction8){
            int next = index+gameGrid.step(D);


            if (gameGrid.isInGrid(index + gameGrid.step(D)) &&
                    !frontiere.contains(next) &&
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
    /*
    * Retourne les cases voisines qui sont non-decouvertes
    * */
    Set<Integer> getUndiscoveredneighbours(Case[] grid, int index){
        Set<Integer> undiscovered = new LinkedHashSet<Integer>();
        for (Integer i: gameGrid.getSurroundingIndex(index)){
            if (grid[i] == UNDISCOVERED){
                undiscovered.add(i);
            }
        }
        return undiscovered;
    }

    /*
    * Retourne le nombre de flag qui reste a poser pour satisfaire l'indice
    * */
    int getNbFlagToPlace(Case[] grid, int index){
        int nbFlagRemaining = grid[index].indexValue;
        for (Integer v : gameGrid.getSurroundingIndex(index)){
            if (grid[v] == FLAGED){
                nbFlagRemaining--;
            }
        }
        return nbFlagRemaining;
    }


    boolean isIndexSatisfied(Case[] grid, int index){
        int indice = grid[index].indexValue;
        int nbFlagPosed =0;
        for (Integer v: gameGrid.getSurroundingIndex(index)){
            if (grid[v] == FLAGED){
                nbFlagPosed++;
            }
        }
        return indice == nbFlagPosed;
    }

    public Set<HintNode> getHintNeirbour(Case[] g, FringeNode fringeNode){

        Set<HintNode> hintNodes = new LinkedHashSet<HintNode>();
        for(Integer indexHint : gameGrid.getSurroundingIndex(fringeNode.indexInGrid)){

            if(Case.isIndicatorCase(g[indexHint])){
                HintNode hn =null;
                if(mapHintNode.containsKey(indexHint)){
                    hn = mapHintNode.get(indexHint);
                }else {
                    hn = new HintNode(indexHint);
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

        public HintNode(int index){
            super(index);
            this.value = getNbFlagToPlace(caseGrille,index);
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
        public Case state = UNDISCOVERED;

        public FringeNode(int index){
            super(index);
            hintNodes = new LinkedHashSet<HintNode>();
        }

        public void computeMineProbability(int totalAssignations) {
            this.probabilityMine = (float)this.nbFlagsHit / totalAssignations;
        }

        public boolean isObviousMine() { return this.probabilityMine == 1; }

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
