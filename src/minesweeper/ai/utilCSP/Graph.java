package minesweeper.ai.utilCSP;

import minesweeper.Case;
import minesweeper.Direction;
import minesweeper.Grid;
import minesweeper.Move;
import minesweeper.ui.BoardGameView;

import javax.swing.*;
import javax.xml.bind.annotation.XmlElementDecl;

import static minesweeper.Case.*;
import static minesweeper.Direction.*;

import java.io.File;
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
    public List<Integer> nbValidAssignationsPerFrontier;



    public Set<Integer> deactivatedNode;
    public Grid gameGrid;
    public Case[] caseGrille;

    public Graph(Grid gameGrid){
        this.gameGrid = gameGrid;
        caseGrille = gameGrid.getCpyPlayerView();

        deactivatedNode = new HashSet<Integer>();
        mapHintNode = new HashMap<Integer, HintNode>();
        mapFringeNode = new HashMap<Integer, FringeNode>();

        allHintNode = new ArrayList<List<HintNode>>();
        allFringeNodes = new ArrayList<List<FringeNode>>();
        nbValidAssignationsPerFrontier = new ArrayList<Integer>();

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
        Set<Integer> inFrontiereSoFar = new HashSet<Integer>();
        for (int i =0; i < grid.length; i++){

            if (!inFrontiereSoFar.contains(i) && isAFringeNode(grid,i)){


                FringeNode fringeNode = new FringeNode(i);

                if(!inFrontiereSoFar.contains(i) && inFrontiereSoFar.contains(fringeNode)){
                    inFrontiereSoFar.contains(i);
                    inFrontiereSoFar.contains(fringeNode);
                }
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
                for(HintNode hn : fn.hintNodes){
                    hn.connectedHint.addAll(fn.hintNodes);
                    hn.connectedHint.remove(hn);
                }
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
    void putInFrontier(FringeNode startNode, List<FringeNode> fringe, Set<Integer> inBorderSoFar, Case[] grid){

        Queue queue = new LinkedList();
        queue.add(startNode);

        mapFringeNode.put(startNode.indexInGrid, startNode);

        FringeNode lastNode = startNode;
        FringeNode mostFlagToPlace =null;


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
                        queue.add(nextNode);

                        if(nextDirection.getCompDir().size()>1){
                            nextNode.fringeNeighbor.addLast(currentNode);
                            currentNode.fringeNeighbor.addLast(nextNode);
                        }else{
                            nextNode.fringeNeighbor.addFirst(currentNode);
                            currentNode.fringeNeighbor.addFirst(nextNode);
                        }

                        lastNode = nextNode;
                    }else{
                        if(currentNode.fringeNeighbor.contains(mapFringeNode.get(next))){
                            if(nextDirection.getCompDir().size()>1){
                                currentNode.fringeNeighbor.addLast(mapFringeNode.get(next));
                            }else {
                                currentNode.fringeNeighbor.addFirst(mapFringeNode.get(next));
                            }
                        }


                    }

                }
            }

        }


        /*
        * TODO
        * Je ne penses pas que c'est bon. Le graph devrait juste lié tout les noeud ensemble et
        * Le CSP devrait lui même gerer comment il va circuler dans les NOdes
        * */


        FringeNode TrueStart = lastNode;




        Stack<FringeNode> stack = new Stack<FringeNode>();
        stack.add(TrueStart);
        inBorderSoFar.add(TrueStart.indexInGrid);
        fringe.add(TrueStart);

        while (!stack.isEmpty()){;
            FringeNode current = stack.peek();
            FringeNode nextNode = null;
            if(!current.fringeNeighbor.isEmpty() ){
                for(FringeNode f : current.fringeNeighbor){
                    if(!inBorderSoFar.contains(f.indexInGrid)){
                        nextNode = f;
                        stack.add(nextNode);
                        fringe.add(nextNode);
                        inBorderSoFar.add(nextNode.indexInGrid);
                        break;
                    }
                }
            }
            if(nextNode == null){
                stack.pop();
            }
        }

    }




    /*
    * Va aller chercher les prochaines directions qui menent vers un noeud valide
    * */
    Set<Direction> getPossibleDirection(Case[] grid, int index, Set<Integer> frontiere){
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

    public LinkedHashSet<HintNode> getHintNeirbour(Case[] g, FringeNode fringeNode){

        LinkedHashSet<HintNode> hintNodes = new LinkedHashSet<HintNode>();
        for(Integer indexHint : gameGrid.getSurroundingIndex(fringeNode.indexInGrid)){

            if(Case.isIndicatorCase(g[indexHint])){
                HintNode hn =null;
                if(mapHintNode.containsKey(indexHint)){
                    hn = mapHintNode.get(indexHint);
                }else {
                    hn = new HintNode(indexHint, this.gameGrid.countUnplacedFlags(indexHint), this.gameGrid.getUndiscoveredneighbours(indexHint).size());
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
        public Set<HintNode> connectedHint;
        public int value;
        public int nbFlagToPlace;
        public int nbUndiscoveredNeighbors;

        public HintNode(int index, int value,int nbUndiscoveredNeighbors){
            super(index);
            this.value = value;
            nbFlagToPlace = value;
            this.nbUndiscoveredNeighbors = nbUndiscoveredNeighbors;
            connectedFringe = new LinkedHashSet<FringeNode>();
            connectedHint = new LinkedHashSet<HintNode>();
        }

        public List<FringeNode> getUndiscoveredFringe() {
            List<Graph.FringeNode> undiscoveredFringe = new ArrayList<Graph.FringeNode>();
            for (Graph.FringeNode fn : connectedFringe) {
                if (fn.state == UNDISCOVERED && !fn.isDeactivated) {
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


        /*
        * This method is use when the hint is satisfied and any more flag
        * put on his fringe would invalid him (Foward checking)
        * */
        public void deactivateAccessibleFringe(){

        }

        public boolean isUnsatisfiable() {
            updateSurroundingAwareness();
            return ((this.nbFlagToPlace < 0 )||
                    (nbUndiscoveredNeighbors < nbFlagToPlace)); }
        public boolean isSatisfied() { return this.nbFlagToPlace == 0; }

        /*
        * TODO je ne suis pas sur que ce soit safe
        * Il va regarder autour de lui les case qui on un flag
        * et celle qui sont encore non- decouverte
        * Il va ensuite updater ces varialbes
        * */
        public void updateSurroundingAwareness(){
            int nbFlagToPlace = value;
            int nbPlaceForFlag=0;
            for(FringeNode fn : connectedFringe){
                if(fn.state == FLAGED){
                    nbFlagToPlace--;
                }else if(fn.state == UNDISCOVERED && !fn.isDeactivated) {
                    nbPlaceForFlag++;
                }
            }
            nbUndiscoveredNeighbors = nbPlaceForFlag;
            this.nbFlagToPlace = nbFlagToPlace;
        }
        public Set<FringeNode> getFlaggedFringe(){
            Set<FringeNode> flagged = new LinkedHashSet<FringeNode>();
            for(FringeNode fn : connectedFringe){
                if(fn.state == FLAGED){
                    flagged.add(fn);
                }
            }
            return flagged;
        }
        public Set<FringeNode> getDeactivatedFringe(){
            Set<FringeNode> deactivatedFringe = new LinkedHashSet<FringeNode>();
            for(FringeNode fn : connectedFringe){
                if(fn.isDeactivated){
                    deactivatedFringe.add(fn);
                }
            }
            return deactivatedFringe;
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
        public LinkedHashSet<HintNode> hintNodes;
        public LinkedList<FringeNode> fringeNeighbor;
        public Case state = UNDISCOVERED;
        public boolean isDeactivated = false;


        public FringeNode(int index) {
            super(index);
            hintNodes = new LinkedHashSet<HintNode>();
            fringeNeighbor = new LinkedList<FringeNode>();
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


    public static void main (String[] args) {
        final JFileChooser chooser = new JFileChooser(".");

        if (chooser.showDialog(new JFrame("Choisir un fichier"), "Ok") == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile() != null) {
                chooser.getSelectedFile();

                Grid grid = new Grid(chooser.getSelectedFile());
                Graph graph = new Graph(grid);
                CSP.CSPonGraph(graph);

                Set<Move> errors = grid.checkMove(CSP.movesToPlay);
                if(!errors.isEmpty()){
                    System.out.println("ERREUR");
                }

            }


        }
    }
}
