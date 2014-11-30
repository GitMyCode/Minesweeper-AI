package minesweeper.ai.utilCSP;

import minesweeper.Coup;
import minesweeper.Grid;
import minesweeper.Move;
import minesweeper.ai.dataRepresentation.FringeNode;
import minesweeper.ai.dataRepresentation.Graph;
import minesweeper.ai.dataRepresentation.HintNode;
import minesweeper.ai.strategyCSP.FowardCheckCSP;
import minesweeper.ai.strategyCSP.SimpleCSP;
import minesweeper.ai.strategyCSP.StrategyCSP;

import java.io.File;
import java.util.*;

import static minesweeper.Case.*;

/**
 * Created by MB on 11/28/2014.
 */
public class CSP {

    /*
    * TODO
    *
    * POUR TES TESTS UNIQUEMENT.
    * CLASSE TEMPORAIRE, NE PAS Y FAIRE ATTENTION
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    * */

    public static Graph graph = null;
    public static int nbValidAssignations = 0;
    public static List<Integer> nbValidAssignationsPerFrontier;
    public static Set<Move> movesToPlay;
    public static Set<HintNode> variableRemaining;

    static void CSPonGraph(Graph g) {
        graph = g;
        nbValidAssignationsPerFrontier = new ArrayList<Integer>();
        movesToPlay = new HashSet<Move>();
        CSPonAllFrontiers();
        addSafeMovesAndFlags();
    }


    static private void CSPonAllFrontiers() {
        for (int i = 0; i < graph.allHintNode.size(); i++) {
            long time = System.currentTimeMillis();
            List<HintNode> hintBorder = graph.allHintNode.get(i);
            List<FringeNode> fringeNodes = graph.allFringeNodes.get(i);
            nbValidAssignations = 0;
            variableRemaining = new HashSet<HintNode>();
            variableRemaining.addAll(hintBorder);
            recusivCSP(hintBorder.get(0), hintBorder, fringeNodes);
            nbValidAssignationsPerFrontier.add(nbValidAssignations);
            System.out.println("Temps frontiere " + i + ": " + (System.currentTimeMillis() - time) + " ms");
        }
    }

    static private boolean iterativeCSP(HintNode startNode, List<FringeNode> fringeNodes) {


        Stack<HintNode> stack = new Stack<HintNode>();
        stack.add(startNode);

        while (!stack.isEmpty()) {
            HintNode currentNode = stack.peek();

            currentNode.updateSurroundingAwareness();

            List<FringeNode> undiscoveredFringe = currentNode.getUndiscoveredFringe();
            ArrayList<int[]> allFlagCombinations = currentNode.getAllFlagCombinations();

            for (int[] combination : allFlagCombinations) {

                int nbFlagToPlaceHere = currentNode.nbFlagToPlace;
                addFlagsToUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);


                removeFlagsFromUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
            }

            stack.pop();
            continue;


        }


        return false;
    }

    static public void recusivCSP(HintNode currentNode, List<HintNode> hintNodes, List<FringeNode> fringeNodes) {

        if (solutionFound(hintNodes)) {
            computeFlagHits(fringeNodes);
            nbValidAssignations++;
            return;
        }

        if (currentNode.isUnsatisfiable()) {
            variableRemaining.add(currentNode);
            return;
        }
        if (currentNode.isSatisfied()) {
            variableRemaining.remove(currentNode);
            HintNode chosenVariable = nextVariable(currentNode);
            chosenVariable.updateSurroundingAwareness();

            recusivCSP(chosenVariable, hintNodes, fringeNodes);
            return;
        }

        List<FringeNode> undiscoveredFringe = currentNode.getUndiscoveredFringe();
        ArrayList<int[]> allFlagCombinations = currentNode.getAllFlagCombinations();

        for (int[] combination : allFlagCombinations) {

            int nbFlagToPlaceHere = currentNode.nbFlagToPlace;
            addFlagsToUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
            variableRemaining.remove(currentNode);
            if (allFlagsOkay(currentNode)) {
                recusivCSP(nextVariable(currentNode), hintNodes, fringeNodes);
            }

            variableRemaining.add(currentNode);
            removeFlagsFromUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
        }
        variableRemaining.remove(currentNode);


    }


    static public boolean solutionFound(List<HintNode> hintNodes) {
        for (HintNode hn : hintNodes) {
            if (!hn.isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    static public void computeFlagHits(List<FringeNode> fringeNodes) {
        for (FringeNode fn : fringeNodes) {
            if (fn.state == FLAGED) {
                fn.nbFlagsHit++;
            }
        }
    }


    static public HintNode nextVariable(HintNode current) {
        HintNode nextV = null;
        int MIV = Integer.MIN_VALUE; // Most Influence Variable;
        for (HintNode hintNode : current.connectedHint) {
            if (!hintNode.isSatisfied() && hintNode.connectedHint.size() > MIV) {
                MIV = hintNode.connectedHint.size();
                nextV = hintNode;
            }
        }

        if (nextV == null && !variableRemaining.isEmpty()) {
            nextV = variableRemaining.iterator().next();
            nextV.updateSurroundingAwareness();
        }
        if (nextV == null) {
            System.out.println(" wtf");
        }

        return nextV;
    }

    static private boolean allFlagsOkay(HintNode hintNode) {

        for (HintNode hn : hintNode.connectedHint) {
            hn.updateSurroundingAwareness();
            if (hn.isUnsatisfiable()) {
                return false;
            }
        }
        return true;
    }

    static private void addFlagsToUndiscoveredFringe(List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {


        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);//On utilise les combinaisons comme des index
            fringeToFlag.state = FLAGED;
        }
        for (FringeNode fn : undiscoveredFringe) {
            if (fn.state != FLAGED) {
                fn.isDeactivated = true;
            }
        }

    }

    static private void removeFlagsFromUndiscoveredFringe(List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);
            fringeToFlag.state = UNDISCOVERED;
        }
        for (FringeNode fn : undiscoveredFringe) {
            if (fn.isDeactivated) {
                fn.isDeactivated = false;
            }
        }
    }

    static public void addSafeMovesAndFlags() {
        for (int frontierIndex = 0; frontierIndex < graph.nbFrontiere; frontierIndex++) {
            List<FringeNode> fringeNodes = graph.allFringeNodes.get(frontierIndex);
            int nbPossibilityHere = nbValidAssignationsPerFrontier.get(frontierIndex);

            for (FringeNode fn : fringeNodes) {
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


    public static void main(String[] args) {

    }


}
