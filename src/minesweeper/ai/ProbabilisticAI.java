package minesweeper.ai;

import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.Grid;
import minesweeper.Move;
import minesweeper.ai.dataRepresentation.FringeNode;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class ProbabilisticAI extends SafeOrRandomAI {

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {
        return super.getNextMoves(grid, delay);
    }

    @Override
    protected void addMovesToPlay(Grid grid, Case[] gridCopy) {
        addMovesWithProbabilities();
        if (this.movesToPlay.isEmpty()) { addRandomMove(grid, gridCopy); }
    }

    protected void addMovesWithProbabilities() {
        PriorityQueue<FringeNode> allProbabilities = new PriorityQueue<FringeNode>();

        for (int frontierIndex = 0; frontierIndex < graph.nbFrontiere; frontierIndex++) {
            List<FringeNode> fringeNodes = graph.allFringeNodes.get(frontierIndex);
            int totalValidAssignations = graph.nbValidAssignationsPerFrontier.get(frontierIndex);

            for (FringeNode fn : fringeNodes) {
                fn.computeMineProbability(totalValidAssignations);
                if (fn.isObviousMine()) {
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.FLAG));
                    addCSPMoveToStats();
                } else if (fn.isSafe()) {
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.SHOW));
                    addCSPMoveToStats();
                }
                allProbabilities.offer(fn);

            }
        }

        if (movesToPlay.isEmpty() && !allProbabilities.isEmpty()) {
            Move safestMove = getSafestMove(allProbabilities);
            this.movesToPlay.add(safestMove);
        }
    }

    protected Move getSafestMove(PriorityQueue<FringeNode> allProbabilities) {
        FringeNode safestMove = allProbabilities.poll();
        printProbabilities(allProbabilities);
        addUncertainMoveToStats();
        return new Move(safestMove.indexInGrid, Coup.SHOW);
    }

    private void printProbabilities(PriorityQueue<FringeNode> allProbabilities) {
        System.out.println("###########################################");
        while (!allProbabilities.isEmpty()) {
            System.out.println(allProbabilities.poll().toString());
        }
        System.out.println("###########################################");
    }

    @Override
    public String getName() { return "Probabilistic AI"; }
}
