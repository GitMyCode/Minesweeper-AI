package minesweeper.ai.utilCSP.strategyCSP;

import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.Grid;
import minesweeper.Move;
import minesweeper.ai.utilCSP.Graph;
import minesweeper.exceptions.TimeOverException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static minesweeper.Case.*;

/**
 * Created by MB on 11/29/2014.
 */
public class SimpleCSP implements StrategyCSP{

    private final int LIMITE = 10;
    private long timer;
    private long remain;
    private boolean END = false;

    protected int nbValidAssignations = 0;
    protected Grid gameGrid;
    protected Graph graph;
    protected Set<Move> movesToPlay;



    @Override
    public void executeCSPonGraph (Graph graph) {
        this.graph = graph;
        CSPonAllFrontiers();
    }

    @Override
    public String strategyToString () {
        return "Simple CSP";
    }


    private void CSPonAllFrontiers() {
        for (int i = 0; i < graph.allHintNode.size(); i++) {
            long time = System.currentTimeMillis();
            List<Graph.HintNode> hintBorder = graph.allHintNode.get(i);
            List<Graph.FringeNode> fringeNodes = graph.allFringeNodes.get(i);
            nbValidAssignations = 0;
            recurseCSP(hintBorder, fringeNodes, 0);
            graph.nbValidAssignationsPerFrontier.add(nbValidAssignations);
            System.out.println("Temps frontiere " + i + ": " + (System.currentTimeMillis() - time) + " ms");
        }
    }

    private boolean recurseCSP(List<Graph.HintNode> hintNodes, List<Graph.FringeNode> fringeNodes, int index) {
        if (!allFlagsOkay(hintNodes, index)) { return false; }

        if (solutionFound(index, hintNodes)) {
            computeFlagHits(fringeNodes);
            nbValidAssignations++;
            return true;
        }

        Graph.HintNode variableToSatisfy = hintNodes.get(index);
        variableToSatisfy.updateSurroundingAwareness();

        if (variableToSatisfy.isUnsatisfiable()) { return false; }
        if (variableToSatisfy.isSatisfied()) {
            return recurseCSP(hintNodes, fringeNodes, index + 1);
        }

        List<Graph.FringeNode> undiscoveredFringe = variableToSatisfy.getUndiscoveredFringe();
        ArrayList<int[]> allFlagCombinations = variableToSatisfy.getAllFlagCombinations();

        for (int[] combination : allFlagCombinations) {
            // Nécessaire pour la récursion
            int nbFlagToPlaceHere = variableToSatisfy.nbFlagToPlace;

            addFlagsToUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
            recurseCSP(hintNodes, fringeNodes, index + 1);
            removeFlagsFromUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
        }

        return false;
    }


    private boolean allFlagsOkay(List<Graph.HintNode> hintNodes, int nbDone) {
        for (int i = 0; i < nbDone; i++) {
            Graph.HintNode hintNode = hintNodes.get(i);
            int value = hintNode.value;

            Set<Graph.FringeNode> neighborsFringe = hintNode.connectedFringe;

            int nbFlag = 0;
            for (Graph.FringeNode fn : neighborsFringe) {
                if (fn.state == FLAGED) { nbFlag++; }
            }

            if (nbFlag != value) {
                return false;
            }
        }
        return true;
    }

    private boolean solutionFound(int index, List<Graph.HintNode> hintNodes) {
        return (index >= hintNodes.size());
    }

    private void computeFlagHits(List<Graph.FringeNode> fringeNodes) {
        for (Graph.FringeNode fn : fringeNodes) {
            if (fn.state == FLAGED) { fn.nbFlagsHit++; }
        }
    }

    private void addFlagsToUndiscoveredFringe(List<Graph.FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            Graph.FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);//On utilise les combinaisons comme des index
            fringeToFlag.state = FLAGED;
        }
    }

    private void removeFlagsFromUndiscoveredFringe(List<Graph.FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            Graph.FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);
            fringeToFlag.state = UNDISCOVERED;
        }
    }


}
