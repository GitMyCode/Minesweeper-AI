package minesweeper.ai;

import minesweeper.Coup;
import minesweeper.Move;
import minesweeper.ai.dataRepresentation.FringeNode;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

public class ProbabilisticAIwithRandomSelection extends ProbabilisticAI {

    @Override
    protected Move getSafestMove(PriorityQueue<FringeNode> allProbabilities) {
        ArrayList<FringeNode> safestMoves = getSafestMoves(allProbabilities);
        addUncertainMoveToStats();
        return getRandomMove(safestMoves);
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
        return new Move(randomNode.indexInGrid, Coup.SHOW);
    }
}
