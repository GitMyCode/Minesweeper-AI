package minesweeper.ai.strategyCSP;

import minesweeper.ai.dataRepresentation.FringeNode;
import minesweeper.ai.dataRepresentation.HintNode;

import java.util.ArrayList;
import java.util.List;

public class SimpleCSP extends AbstractCSP {


    protected boolean recurseCSP(List<HintNode> hintNodes, List<FringeNode> fringeNodes, int index, int indexFrontiere) {

        if (!allFlagsOkay(hintNodes, index)) {
            return false;
        }

        if (solutionFound(index, hintNodes)) {
            int indexDernier = graph.nbMinimalAssignementsPerFrontier.size() - 1;
            int nbFlags = computeFlagHits(fringeNodes, indexFrontiere);
            int minimum = Math.min(graph.nbMinimalAssignementsPerFrontier.get(indexDernier), nbFlags);
            graph.nbMinimalAssignementsPerFrontier.set(indexDernier, minimum);
            nbValidAssignations++;
            return true;
        }

        HintNode variableToSatisfy = hintNodes.get(index);
        variableToSatisfy.updateSurroundingAwareness();

        if (variableToSatisfy.isUnsatisfiable()) {
            return false;
        }
        if (variableToSatisfy.isSatisfied()) {
            return recurseCSP(hintNodes, fringeNodes, index + 1, indexFrontiere);
        }

        List<FringeNode> undiscoveredFringe = variableToSatisfy.getUndiscoveredFringe();
        ArrayList<int[]> allFlagCombinations = variableToSatisfy.getAllFlagCombinations();

        for (int[] combination : allFlagCombinations) {
            // Nécessaire pour la récursion
            int nbFlagToPlaceHere = variableToSatisfy.nbFlagToPlace;

            addFlagsToUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
            recurseCSP(hintNodes, fringeNodes, index + 1, indexFrontiere);
            removeFlagsFromUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
        }

        return false;
    }


    @Override
    public String strategyToString() {
        return "Simple CSP";
    }


}
