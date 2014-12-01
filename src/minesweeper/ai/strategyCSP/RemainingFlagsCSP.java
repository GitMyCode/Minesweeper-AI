package minesweeper.ai.strategyCSP;

import minesweeper.Case;
import minesweeper.ai.dataRepresentation.FringeNode;
import minesweeper.ai.dataRepresentation.HintNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 30/11/14.
 */
public class RemainingFlagsCSP extends SimpleCSP {

    private int flagsRemaining = 0;

    @Override
    protected void CSPonAllFrontiers() {
        flagsRemaining = graph.gameGrid.getNbFlagsRemaining();

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

    @Override
    protected boolean recurseCSP(List<HintNode> hintNodes, List<FringeNode> fringeNodes, int index) {
        if (!allFlagsOkay(hintNodes, index)
                || countFlags(fringeNodes) > this.flagsRemaining) {
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

    protected int countFlags(List<FringeNode> fringeNodes) {
        int count = 0;
        for (FringeNode fn : fringeNodes) {
            if (fn.state == Case.FLAGED) {
                count++;
            }
        }

        return count;
    }
}
