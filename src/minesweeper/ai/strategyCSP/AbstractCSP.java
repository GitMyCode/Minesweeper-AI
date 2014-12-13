package minesweeper.ai.strategyCSP;

import minesweeper.ai.dataRepresentation.FringeNode;
import minesweeper.ai.dataRepresentation.Graph;
import minesweeper.ai.dataRepresentation.HintNode;

import static minesweeper.Case.*;

import java.util.List;
import java.util.Set;

/**
 * Created by MB on 12/2/2014.
 */
public abstract class AbstractCSP implements StrategyCSP {

    protected int nbValidAssignations = 0;
    protected Graph graph;
    protected String cumulatedTimeStats = "";


    @Override
    public void executeCSPonGraph(Graph graph) {
        this.graph = graph;
        optionalActionBeforeCSPonFrontiers();
        cspOnAllFrontiers();

    }

    /*
    * Pour permettre au classe herité d'utiliser cette method pour
    * faire des operations customs avant de lancé la recursion CSP sur toute
    * les frontieres sans avoir a override cspOnAllFrontiers
    * */
    protected void optionalActionBeforeCSPonFrontiers() {

    }

    protected void cspOnAllFrontiers() {
        for (int i = 0; i < graph.allHintNode.size(); i++) {
            long time = System.currentTimeMillis();
            List<HintNode> hintBorder = graph.allHintNode.get(i);
            List<FringeNode> fringeNodes = graph.allFringeNodes.get(i);
            nbValidAssignations = 0;
            graph.nbMinimalAssignementsPerFrontier.add(Integer.MAX_VALUE);
            recurseCSP(hintBorder, fringeNodes, 0, i);
            graph.nbValidAssignationsPerFrontier.add(nbValidAssignations);
            addLineToExecutionLog("frontiere (" + i + ") :" + (System.currentTimeMillis() - time) + " ms");
        }
    }

    protected boolean recurseCSP(List<HintNode> hintNodes, List<FringeNode> fringeNodes, int index, int indexFrontiere) {

        return false;
    }

    protected boolean solutionFound(int index, List<HintNode> hintNodes) {
        return (index >= hintNodes.size());
    }

    protected int computeFlagHits(List<FringeNode> fringeNodes, int indexFrontiere) {
        int nbFlagsAssigned = 0;
        for (FringeNode fn : fringeNodes) {
            if (fn.state == FLAGED) {
                fn.combinationsUsed.add(nbValidAssignations);
                fn.nbFlagsHit++;
                nbFlagsAssigned += 1;
            }
        }
        return nbFlagsAssigned;
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

    protected void addFlagsToUndiscoveredFringe(List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]); //On utilise les combinaisons comme des index
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
    public String strategyToString() {
        return "Blue Print";
    }

    @Override
    public String getExecutionLog() {
        return cumulatedTimeStats;
    }

    @Override
    public void addLineToExecutionLog(String line) {
        cumulatedTimeStats += line + "\n";
    }
}
