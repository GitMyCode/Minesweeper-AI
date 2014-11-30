/**
 * Created by martin on 18/11/14.
 */
package minesweeper.ai;

import minesweeper.*;
import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.ai.dataRepresentation.FringeNode;
import minesweeper.ai.dataRepresentation.Graph;
import minesweeper.ai.dataRepresentation.HintNode;
import minesweeper.exceptions.TimeOverException;

import static minesweeper.Case.*;

import java.util.*;

public class CSPGraph implements ArtificialPlayer, Benchmarkable {

    private final int LIMITE = 10;
    private long timer;
    private long remain;
    private boolean END = false;

    protected int nbValidAssignations = 0;
    protected List<Integer> nbValidAssignationsPerFrontier;
    protected Grid gameGrid;
    protected Graph graph;
    protected Set<Move> movesToPlay;

    protected int nbTrivialMoves;
    protected int nbCSPMoves;
    protected int nbUncertainMoves;
    protected int nbTotalMoves;

    @Override
    public Set<Move> getNextMoves (Grid grid, int delay) {
        this.gameGrid = grid;
        this.nbValidAssignations = 0;
        Case[] gridCopy = grid.getCpyPlayerView();
        startTimer(delay);
        this.movesToPlay = grid.getSafeMoves();

        if (!this.movesToPlay.isEmpty()) {
            addTrivialMoveToStats();
            return movesToPlay;
        }
        nbValidAssignationsPerFrontier = new ArrayList<Integer>();
        computeMoves(grid);
        addMovesToPlay(grid, gridCopy);


        return movesToPlay;
    }

    private void computeMoves (Grid g) {
        try {
            executeMoveComputation(g);
        } catch (TimeOverException e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeMoveComputation (Grid g) throws TimeOverException {
        long time = System.currentTimeMillis();
        graph = new Graph(g);
        System.out.println("Temps graph: " + (System.currentTimeMillis() - time) + " ms");
        time = System.currentTimeMillis();
        CSPonAllFrontiers();
        System.out.println("Temps CSP: " + (System.currentTimeMillis() - time) + " ms");
        long total = System.currentTimeMillis() - time;
        if (total >= 100) {
            int stop = 0;
        }
    }

    private void CSPonAllFrontiers () throws TimeOverException {
        for (int i = 0; i < graph.allHintNode.size(); i++) {
            long time = System.currentTimeMillis();
            List<HintNode> hintBorder = graph.allHintNode.get(i);
            List<FringeNode> fringeNodes = graph.allFringeNodes.get(i);
            nbValidAssignations = 0;
            recurseCSP(hintBorder, fringeNodes, 0);
            nbValidAssignationsPerFrontier.add(nbValidAssignations);
            System.out.println("Temps frontiere " + i + ": " + (System.currentTimeMillis() - time) + " ms");
        }
    }

    private boolean recurseCSP (List<HintNode> hintNodes, List<FringeNode> fringeNodes, int index) throws TimeOverException {
        if (isTimeUp()) {
            throw new TimeOverException();
        }
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

    private boolean isTimeUp () {
        END = (timeRemaining() < LIMITE) ? true : END;
        return END;
    }

    private boolean allFlagsOkay (List<HintNode> hintNodes, int nbDone) {
        for (int i = 0; i < nbDone; i++) {
            if (!hintNodes.get(i).isSatisfied()) {
                return false;
            }
        }
        /*for (int i = 0; i < nbDone; i++) {
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
        }*/
        return true;
    }

    private boolean neighbourhoodOkey (HintNode hintNode) {
        for (HintNode hn : hintNode.connectedHint) {
            hn.updateSurroundingAwareness();
            if (hn.isUnsatisfiable()) {
                return false;
            }
        }
        return true;
    }

    static public boolean solutionFound (List<HintNode> hintNodes) {
        for (HintNode hn : hintNodes) {
            if (!hn.isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    private boolean solutionFound (int index, List<HintNode> hintNodes) {
        return (index >= hintNodes.size());
    }

    private void computeFlagHits (List<FringeNode> fringeNodes) {
        for (FringeNode fn : fringeNodes) {
            if (fn.state == FLAGED) {
                fn.nbFlagsHit++;
            }
        }
    }

    private void addFlagsToUndiscoveredFringe (List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
        for (int i = 0; i < nbFlagToPlaceHere; i++) {
            FringeNode fringeToFlag = undiscoveredFringe.get(oneCombination[i]);//On utilise les combinaisons comme des index
            fringeToFlag.state = FLAGED;
        }
        deactivateFringe(undiscoveredFringe);

    }

    private void removeFlagsFromUndiscoveredFringe (List<FringeNode> undiscoveredFringe, int[] oneCombination, int nbFlagToPlaceHere) {
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
    private void deactivateFringe (List<FringeNode> fringe) {
        for (FringeNode fn : fringe) {
            if (fn.state != FLAGED) {
                fn.isDeactivated = true;
            }
        }
    }

    /*
    * Use for reactivating a fringe previously deactivated (When the CSP backtrack we need to free theses fringes)
    * */
    private void activateFringe (List<FringeNode> fringe) {
        for (FringeNode fn : fringe) {
            if (fn.isDeactivated) {
                fn.isDeactivated = false;
            }
        }
    }


    protected void addMovesToPlay (Grid grid, Case[] gridCopy) {
        if (this.movesToPlay.isEmpty()) {
            addSafeMovesAndFlags();
            if (this.movesToPlay.isEmpty()) {
                addRandomMove(grid, gridCopy);
                addUncertainMoveToStats();
            } else {
                Set<Move> errors = grid.checkMove(movesToPlay);
                if (!errors.isEmpty()) {
                    System.out.println("ERREUR");
                }


                addCSPMoveToStats();
            }
        }
    }

    protected void addSafeMovesAndFlags () {
        for (int frontierIndex = 0; frontierIndex < graph.nbFrontiere; frontierIndex++) {
            List<FringeNode> fringeNodes = graph.allFringeNodes.get(frontierIndex);
            int nbPossibilityHere = nbValidAssignationsPerFrontier.get(frontierIndex);

            for (FringeNode fn : fringeNodes) {
                if (fn.nbFlagsHit == 0) {
                    // 0% Mine
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.SHOW));
                } else if (fn.nbFlagsHit == nbPossibilityHere) {
                    // 100% Mine
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.FLAG));
                }
            }
        }
    }

    protected void addRandomMove (Grid grid, Case[] gridCopy) {
        List<Integer> legalMoves = new ArrayList<Integer>();
        for (int i = 0; i < grid.length; i++) {
            if (gridCopy[i] == UNDISCOVERED) {
                legalMoves.add(i);
            }
        }

        Random ran = new Random();
        int index = legalMoves.get(ran.nextInt(legalMoves.size()));
        this.movesToPlay.add(new Move(index, Coup.SHOW));
    }

    private void startTimer (int delai) {
        END = false;
        timer = System.currentTimeMillis();
        remain = delai;
    }

    private String showTimeRemaing () {
        return ("Time: " + timeRemaining() + " ms");
    }

    public long timeRemaining () {
        long elaspsed = (System.currentTimeMillis() - timer);
        return remain - elaspsed;
    }

    protected void addTrivialMoveToStats () {
        nbTrivialMoves++;
        nbTotalMoves++;
    }

    protected void addCSPMoveToStats () {
        nbCSPMoves++;
        nbTotalMoves++;
    }

    protected void addUncertainMoveToStats () {
        nbUncertainMoves++;
        nbTotalMoves++;
    }

    @Override
    public String getName () {
        return "CSP-Martin";
    }

    @Override
    public boolean isProbabilistic () {
        return false;
    }

    @Override
    public double getProbabilitySuccessRate () {
        return 0;
    }

    @Override
    public double getTrivialMoveRate () {
        return (double) nbTrivialMoves / nbTotalMoves;
    }

    @Override
    public double getCSPMoveRate () {
        return (double) nbCSPMoves / nbTotalMoves;
    }

    @Override
    public double getUncertainMoveRate () {
        return (double) nbUncertainMoves / nbTotalMoves;
    }
}
