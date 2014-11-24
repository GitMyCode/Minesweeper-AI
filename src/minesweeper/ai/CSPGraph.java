/**
 * Created by martin on 18/11/14.
 */
package minesweeper.ai;

import minesweeper.*;
import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.ai.utilCSP.Graph;
import minesweeper.exceptions.TimeOverException;

import static minesweeper.Case.*;

import java.util.*;

public class CSPGraph implements ArtificialPlayer {

    private final int LIMITE = 10;
    private long timer;
    private long remain;
    private boolean END = false;
    private int nbValidAssignations = 0;
    private List<Integer> nbValidAssignationsPerFrontier;
    private Grid gameGrid;
    private Graph graph;
    private Set<Move> movesToPlay;

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {
        gameGrid = grid;
        nbValidAssignations = 0;
        Case[] gridCopy = grid.getCpyPlayerView();
        startTimer(delay);
        this.movesToPlay = grid.checkForSafeMoves();

        if(!this.movesToPlay.isEmpty()) { return movesToPlay; }
        nbValidAssignationsPerFrontier = new ArrayList<Integer>();
        computeMoves(grid);
        addMovesToPlay(grid, gridCopy);

        return movesToPlay;
    }

    public void computeMoves(Grid g) {
        try {
            executeMoveComputation(g);
        } catch (TimeOverException e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeMoveComputation(Grid g) throws TimeOverException {
        graph = new Graph(g);
        CSPonAllFrontiers();
    }

    private void CSPonAllFrontiers() throws TimeOverException {
        for (int i = 0; i < graph.allHintNode.size(); i++) {
            List<Graph.HintNode> hintBorder = graph.allHintNode.get(i);
            List<Graph.FringeNode> fringeNodes = graph.allFringeNodes.get(i);
            nbValidAssignations = 0;
            recurseCSP(hintBorder, fringeNodes, 0);
            nbValidAssignationsPerFrontier.add(nbValidAssignations);
        }
    }

    boolean recurseCSP(List<Graph.HintNode> hintNodes, List<Graph.FringeNode> fringeNodes, int index) throws TimeOverException {
        if (isTimeUp()) { throw new TimeOverException(); }
        if (!allFlagsOkay(hintNodes, index)) { return false; }

        if (solutionFound(index, hintNodes)) {
            computeFlagHits(fringeNodes);
            nbValidAssignations++;
            return true;
        }

        Graph.HintNode variableToSatisfy = hintNodes.get(index);
        variableToSatisfy.updateSurroundingAwareness();

        if (variableToSatisfy.isOverAssigned()) { return false; }
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

    public boolean isTimeUp() {
        END = (timeRemaining() < LIMITE) ? true: END;
        return END;
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
            if (fn.state == FLAGED) { fn.nbFlagHits++; }
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

    private void addMovesToPlay(Grid grid, Case[] gridCopy) {
        if (this.movesToPlay.isEmpty()) {
            addSafeMovesAndFlags();
            if (this.movesToPlay.isEmpty()) {
                addRandomMove(grid, gridCopy);
            }
        }
    }

    private void addSafeMovesAndFlags() {
        for (int frontierIndex = 0; frontierIndex < graph.nbFrontiere; frontierIndex++) {
            List<Graph.FringeNode> fringeNodes = graph.allFringeNodes.get(frontierIndex);
            int nbPossibilityHere = nbValidAssignationsPerFrontier.get(frontierIndex);

            for (Graph.FringeNode fn : fringeNodes) {
                if (fn.nbFlagHits == 0) {
                    // 0% Mine
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.SHOW));
                } else if (fn.nbFlagHits == nbPossibilityHere) {
                    // 100% Mine
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.FLAG));
                }
            }
        }
    }

    private void addRandomMove(Grid grid, Case[] gridCopy) {
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

    private void startTimer(int delai) {
        END = false;
        timer = System.currentTimeMillis();
        remain = delai;
    }

    private String showTimeRemaing() { return ("Time: " + timeRemaining() + " ms"); }

    public long timeRemaining() {
        long elaspsed = (System.currentTimeMillis() - timer);
        return remain - elaspsed;
    }

    @Override
    public String getName() { return "CSP-Martin"; }
}
