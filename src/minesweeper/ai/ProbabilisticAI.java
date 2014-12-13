package minesweeper.ai;

import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.Grid;
import minesweeper.Move;
import minesweeper.ai.dataRepresentation.FringeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class ProbabilisticAI extends SafeOrRandomAI {

    @Override
    protected void addMovesToPlay(Grid grid, Case[] gridCopy) {
        addMovesWithProbabilities();
        if (this.movesToPlay.isEmpty()) {
            addRandomMove(grid, gridCopy);
            Move move = null;
            for (Move m: movesToPlay) {
                move = m;
            }
            addUncertainMoveToStats(move);
        }
    }

    protected Move getSafestMove(PriorityQueue<FringeNode> allProbabilities) {
        ArrayList<FringeNode> safestMoves = getSafestMoves(allProbabilities);
        return getRandomMove(safestMoves);
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
    private ArrayList<FringeNode> getSafestMoves(PriorityQueue<FringeNode> allProbabilities) {
        ArrayList<FringeNode> safestMoves = new ArrayList<FringeNode>();
        float safestProbability = allProbabilities.peek().probabilityMine;

        while (!allProbabilities.isEmpty()
                && safestProbability == allProbabilities.peek().probabilityMine) {
            FringeNode safestMove = allProbabilities.poll();
            safestMoves.add(safestMove);
        }

        return safestMoves;
    }

    private Move getRandomMove(ArrayList<FringeNode> movesList) {
        Random random = new Random();
        int randomIndex = random.nextInt(movesList.size());
        FringeNode randomNode = movesList.get(randomIndex);
        Move move = new Move(randomNode.indexInGrid, Coup.SHOW);
        addUncertainMoveToStats(move);

        return move;
    }

    @Override
    public String getName() {
        return "ProbabilisticAI";
    }
}
