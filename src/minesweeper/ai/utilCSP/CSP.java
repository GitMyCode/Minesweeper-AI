package minesweeper.ai.utilCSP;

import minesweeper.Coup;
import minesweeper.Move;
import org.w3c.dom.html.HTMLIsIndexElement;

import java.util.*;

import static minesweeper.Case.*;

/**
 * Created by MB on 11/28/2014.
 */
public class CSP {

    public static Graph graph = null;
    public static int nbValidAssignations =0;
    public static List<Integer> nbValidAssignationsPerFrontier;
    public static Set<Move> movesToPlay;
    public static Set<Graph.HintNode> variableRemaining;
    static void CSPonGraph(Graph g){
        graph= g;
        nbValidAssignationsPerFrontier = new ArrayList<Integer>();
        movesToPlay = new HashSet<Move>();
        CSPonAllFrontiers();
        addSafeMovesAndFlags();
    }


     static private void CSPonAllFrontiers() {
        for (int i = 0; i < graph.allHintNode.size(); i++) {
            long time = System.currentTimeMillis();
            List<Graph.HintNode> hintBorder = graph.allHintNode.get(i);
            List<Graph.FringeNode> fringeNodes = graph.allFringeNodes.get(i);
            nbValidAssignations = 0;
            variableRemaining = new HashSet<Graph.HintNode>();
            variableRemaining.addAll(hintBorder);
            recusivCSP(hintBorder.get(0), hintBorder, fringeNodes);
            nbValidAssignationsPerFrontier.add(nbValidAssignations);
            System.out.println("Temps frontiere " + i + ": " + (System.currentTimeMillis() - time) + " ms");
        }
    }

    static private boolean iterativeCSP(Graph.HintNode startNode, List<Graph.FringeNode> fringeNodes) {


        Stack<Graph.HintNode> stack = new Stack<Graph.HintNode>();
        stack.add(startNode);

        while (!stack.isEmpty()){
            Graph.HintNode currentNode = stack.peek();

            currentNode.updateSurroundingAwareness();

            List<Graph.FringeNode> undiscoveredFringe = currentNode.getUndiscoveredFringe();
            ArrayList<int[]> allFlagCombinations = currentNode.getAllFlagCombinations();

            for(int[] combination : allFlagCombinations){

                int nbFlagToPlaceHere = currentNode.nbFlagToPlace;
                addFlagsToUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);


                removeFlagsFromUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
            }

            stack.pop();
            continue;


        }


        return false;
    }

    static public void recusivCSP(Graph.HintNode currentNode,List<Graph.HintNode> hintNodes, List<Graph.FringeNode> fringeNodes){

        if(solutionFound(hintNodes)){
            computeFlagHits(fringeNodes);
            nbValidAssignations++;
            return;
        }

        if(currentNode.isUnsatisfiable()){
            variableRemaining.add(currentNode);
            return;
        }
        if(currentNode.isSatisfied()){
            variableRemaining.remove(currentNode);
            Graph.HintNode chosenVariable = nextVariable(currentNode);
            chosenVariable.updateSurroundingAwareness();

            recusivCSP(chosenVariable, hintNodes, fringeNodes);
            return;
        }

        List<Graph.FringeNode> undiscoveredFringe = currentNode.getUndiscoveredFringe();
        ArrayList<int[]> allFlagCombinations = currentNode.getAllFlagCombinations();

        for(int[] combination : allFlagCombinations){

            int nbFlagToPlaceHere = currentNode.nbFlagToPlace;
            addFlagsToUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
            variableRemaining.remove(currentNode);
            if(allFlagsOkay(currentNode)){
                recusivCSP(nextVariable(currentNode),hintNodes,fringeNodes);
            }

            variableRemaining.add(currentNode);
            removeFlagsFromUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
        }
        variableRemaining.remove(currentNode);



    }


    static public boolean solutionFound(List<Graph.HintNode> hintNodes){
        for(Graph.HintNode hn : hintNodes){
            if(!hn.isSatisfied()){
                return false;
            }
        }
        return true;
    }
    static public void computeFlagHits(List<Graph.FringeNode> fringeNodes) {
        for (Graph.FringeNode fn : fringeNodes) {
            if (fn.state == FLAGED) { fn.nbFlagsHit++; }
        }
    }



    static public Graph.HintNode nextVariable(Graph.HintNode current){
        Graph.HintNode nextV =null;
        int MIV = Integer.MIN_VALUE; // Most Influence Variable;
        for (Graph.HintNode hintNode : current.connectedHint){
            if(!hintNode.isSatisfied() && hintNode.connectedHint.size() > MIV){
                MIV = hintNode.connectedHint.size();
                nextV = hintNode;
            }
        }

        if(nextV == null && !variableRemaining.isEmpty()){
            nextV = variableRemaining.iterator().next();
            nextV.updateSurroundingAwareness();
        }
        if(nextV ==null){
            System.out.println(" wtf");
        }

        return nextV;
    }

    static private boolean allFlagsOkay(Graph.HintNode hintNode){

        for(Graph.HintNode hn : hintNode.connectedHint){
            hn.updateSurroundingAwareness();
            if(hn.isUnsatisfiable()){
                return false;
            }
        }
        return true;
    }

     static private void addFlagsToUndiscoveredFringe(List<Graph.FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {


        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            Graph.FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);//On utilise les combinaisons comme des index
            fringeToFlag.state = FLAGED;
        }
        for(Graph.FringeNode fn : undiscoveredFringe ){
            if(fn.state != FLAGED){
                fn.isDeactivated = true;
            }
        }

    }

    static private void removeFlagsFromUndiscoveredFringe(List<Graph.FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            Graph.FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);
            fringeToFlag.state = UNDISCOVERED;
        }
        for(Graph.FringeNode fn : undiscoveredFringe ){
            if(fn.isDeactivated){
                fn.isDeactivated = false;
            }
        }
    }

    static public void addSafeMovesAndFlags() {
        for (int frontierIndex = 0; frontierIndex < graph.nbFrontiere; frontierIndex++) {
            List<Graph.FringeNode> fringeNodes = graph.allFringeNodes.get(frontierIndex);
            int nbPossibilityHere = nbValidAssignationsPerFrontier.get(frontierIndex);

            for (Graph.FringeNode fn : fringeNodes) {
                if (fn.nbFlagsHit == 0) {
                    // 0% Mine
                    movesToPlay.add(new Move(fn.indexInGrid, Coup.SHOW));
                } else if (fn.nbFlagsHit == nbPossibilityHere) {
                    // 100% Mine
                    movesToPlay.add(new Move(fn.indexInGrid, Coup.FLAG));
                }
            }
        }
    }




}
