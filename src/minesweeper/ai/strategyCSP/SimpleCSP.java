package minesweeper.ai.strategyCSP;

import minesweeper.Grid;
import minesweeper.Move;
import minesweeper.ai.dataRepresentation.FringeNode;
import minesweeper.ai.dataRepresentation.Graph;
import minesweeper.ai.dataRepresentation.HintNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static minesweeper.Case.*;

/**
 * Created by MB on 11/29/2014.
 */
public class SimpleCSP implements StrategyCSP {

    protected int nbValidAssignations = 0;
    protected Graph graph;
    protected String cumulatedTimeStats = "";


    @Override
    public void executeCSPonGraph(Graph graph) {
        this.graph = graph;
        CSPonAllFrontiers();
    }

    @Override
    public String strategyToString() {
        return "Simple CSP";
    }

    protected void CSPonAllFrontiers() {
        for (int i = 0; i < graph.allHintNode.size(); i++) {
            long time = System.currentTimeMillis();
            List<HintNode> hintBorder = graph.allHintNode.get(i);
            List<FringeNode> fringeNodes = graph.allFringeNodes.get(i);
            nbValidAssignations = 0;
            recurseCSP(hintBorder, fringeNodes, 0);
            graph.nbValidAssignationsPerFrontier.add(nbValidAssignations);
            addLineToExecutionLog("frontiere (" + i + ") :" + (System.currentTimeMillis() - time) + " ms");
        }
    }

    protected boolean recurseCSP(List<HintNode> hintNodes, List<FringeNode> fringeNodes, int index) {
        if (!allFlagsOkay(hintNodes, index)) {
            return false;
        }

        if (solutionFound(index, hintNodes)) {
            computeFlagHits(fringeNodes);
            nbValidAssignations++;
            return true;
        }

        HintNode variableToSatisfy = hintNodes.get(index);
        variableToSatisfy.updateSurroundingAwareness();

        if (variableToSatisfy.isUnsatisfiable()) {
            return false;
        }
        if (variableToSatisfy.isSatisfied()) {
            return recurseCSP(hintNodes, fringeNodes, index + 1);
        }

        List<FringeNode> undiscoveredFringe = variableToSatisfy.getUndiscoveredFringe();
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


    protected boolean allFlagsOkay(List<HintNode> hintNodes, int nbDone) {
        for (int i = 0; i < nbDone; i++) {
            HintNode hintNode = hintNodes.get(i);
            int value = hintNode.value;

            Set<FringeNode> neighborsFringe = hintNode.connectedFringe;

            int nbFlag = 0;
            for (FringeNode fn : neighborsFringe) {
                if (fn.state == FLAGED) {
                    nbFlag++;
                }
            }

            if (nbFlag != value) {
                return false;
            }
        }
        return true;
    }

    protected boolean solutionFound(int index, List<HintNode> hintNodes) {
        return (index >= hintNodes.size());
    }

    protected void computeFlagHits(List<FringeNode> fringeNodes) {
        for (FringeNode fn : fringeNodes) {
            if (fn.state == FLAGED) {
                fn.nbFlagsHit++;
            }
        }
    }

    protected void addFlagsToUndiscoveredFringe(List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);//On utilise les combinaisons comme des index
            fringeToFlag.state = FLAGED;
        }
    }

    protected void removeFlagsFromUndiscoveredFringe(List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);
            fringeToFlag.state = UNDISCOVERED;
        }
    }

    @Override
    public void addLineToExecutionLog(String line) {
        cumulatedTimeStats += line + "\n";
    }

    @Override
    public String getExecutionLog() {
        return cumulatedTimeStats;
    }

}
