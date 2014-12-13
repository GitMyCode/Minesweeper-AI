package minesweeper.ai.strategyCSP;

import minesweeper.ai.dataRepresentation.FringeNode;
import minesweeper.ai.dataRepresentation.HintNode;

import java.util.ArrayList;
import java.util.List;

import static minesweeper.Case.*;

/**
 * Created by MB on 11/29/2014.
 */
public class ForwardCheckCSP extends AbstractCSP {


    @Override
    public String strategyToString() {
        return "Foward checking";
    }

    @Override
    protected boolean recurseCSP(List<HintNode> hintNodes, List<FringeNode> fringeNodes, int index) {
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
            List<FringeNode> fringe = variableToSatisfy.getUndiscoveredFringe();
            deactivateFringe(fringe);
            recurseCSP(hintNodes, fringeNodes, index + 1);
            activateFringe(fringe);
            return true;
        }

        List<FringeNode> undiscoveredFringe = variableToSatisfy.getUndiscoveredFringe();
        ArrayList<int[]> allFlagCombinations = variableToSatisfy.getAllFlagCombinations();

        for (int[] combination : allFlagCombinations) {
            // Nécessaire pour la récursion
            int nbFlagToPlaceHere = variableToSatisfy.nbFlagToPlace;

            addFlagsToUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
            variableToSatisfy.updateSurroundingAwareness();
            if (neighbourhoodOkey(variableToSatisfy)) {
                recurseCSP(hintNodes, fringeNodes, index + 1);
            }
            removeFlagsFromUndiscoveredFringe(undiscoveredFringe, combination, nbFlagToPlaceHere);
            variableToSatisfy.updateSurroundingAwareness();
        }

        return false;
    }

    private boolean neighbourhoodOkey(HintNode hintNode) {
        for (HintNode hn : hintNode.connectedHint) {
            if (hn.isUnsatisfiable()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void addFlagsToUndiscoveredFringe(List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]); //On utilise les combinaisons comme des index
            fringeToFlag.state = FLAGED;
        }
        deactivateFringe(undiscoveredFringe);
    }

    @Override
    protected void removeFlagsFromUndiscoveredFringe(List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);
            fringeToFlag.state = UNDISCOVERED;
        }
        activateFringe(undiscoveredFringe);
    }

    // No more flag can be added to this fringe
    private void deactivateFringe(List<FringeNode> fringe) {
        for (FringeNode fn : fringe) {
            if (fn.state != FLAGED) {
                fn.isDeactivated = true;
            }
        }
    }

    private void activateFringe(List<FringeNode> fringe) {
        for (FringeNode fn : fringe) {
            if (fn.isDeactivated) {
                fn.isDeactivated = false;
            }
        }
    }
}
