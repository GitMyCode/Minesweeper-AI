package minesweeper.ai.strategyCSP;

import minesweeper.Grid;
import minesweeper.ai.dataRepresentation.FringeNode;
import minesweeper.ai.dataRepresentation.Graph;
import minesweeper.ai.dataRepresentation.HintNode;

import java.util.ArrayList;
import java.util.List;

import static minesweeper.Case.*;

/**
 * Created by MB on 11/29/2014.
 */
public class FowardCheckCSP implements StrategyCSP {

    private final int LIMITE = 10;
    private long timer;
    private long remain;
    private boolean END = false;

    protected int nbValidAssignations = 0;
    protected Grid gameGrid;
    protected Graph graph;
    protected String cumulatedTimeStats = "";


    @Override
    public void executeCSPonGraph(Graph graph) {
        this.graph = graph;
        CSPonAllFrontiers();

    }

    @Override
    public String strategyToString() {
        return "Foward checking";
    }

    private void CSPonAllFrontiers() {
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

    private boolean recurseCSP(List<HintNode> hintNodes, List<FringeNode> fringeNodes, int index) {
        /*if (!allFlagsOkay(hintNodes, index)) {
            return false;
        }*/
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
            /*
            * TODO
            * check si invalide ses voisin
            * */
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


    private boolean allFlagsOkay(List<HintNode> hintNodes, int nbDone) {
        for (int i = 0; i < nbDone; i++) {
            if (!hintNodes.get(i).isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    private boolean neighbourhoodOkey(HintNode hintNode) {
        for (HintNode hn : hintNode.connectedHint) {
            if (hn.isUnsatisfiable()) {
                return false;
            }
        }
        return true;
    }

    static public boolean solutionFound(List<HintNode> hintNodes) {
        for (HintNode hn : hintNodes) {
            if (!hn.isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    private boolean solutionFound(int index, List<HintNode> hintNodes) {
        return (index >= hintNodes.size());
    }

    private void computeFlagHits(List<FringeNode> fringeNodes) {
        for (FringeNode fn : fringeNodes) {
            if (fn.state == FLAGED) {
                fn.nbFlagsHit++;
            }
        }
    }

    private void addFlagsToUndiscoveredFringe(List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);//On utilise les combinaisons comme des index
            fringeToFlag.state = FLAGED;
        }
        deactivateFringe(undiscoveredFringe);

    }

    private void removeFlagsFromUndiscoveredFringe(List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);
            fringeToFlag.state = UNDISCOVERED;
        }
        activateFringe(undiscoveredFringe);
    }

    /*
        * This method is use when the hint is satisfied and any more flag
        * put on his fringe would invalid him (Foward checking)
        * */
    private void deactivateFringe(List<FringeNode> fringe) {
        for (FringeNode fn : fringe) {
            if (fn.state != FLAGED) {
                fn.isDeactivated = true;
            }
        }
    }

    /*
    * Use for reactivating a fringe previously deactivated (When the CSP backtrack we need to free theses fringes)
    * */
    private void activateFringe(List<FringeNode> fringe) {
        for (FringeNode fn : fringe) {
            if (fn.isDeactivated) {
                fn.isDeactivated = false;
            }
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
