package minesweeper.ai;

import minesweeper.Coup;
import minesweeper.Move;
import minesweeper.ai.utilCSP.Graph;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

public class ProbabilisticAIwithRandomSelection extends ProbabilisticAI {

    @Override
    protected void selectSafestMove(PriorityQueue<Graph.FringeNode> allProbabilities) {
        ArrayList<Graph.FringeNode> safestMoves = getSafestMoves(allProbabilities);
        Move nextMove = getRandomMove(safestMoves);
        this.movesToPlay.add(nextMove);
    }

    private ArrayList<Graph.FringeNode> getSafestMoves(PriorityQueue<Graph.FringeNode> allProbabilities) {
        ArrayList<Graph.FringeNode> safestMoves = new ArrayList<Graph.FringeNode>();
        float safestProbability = allProbabilities.peek().probabilityMine;

        while (!allProbabilities.isEmpty()
                && safestProbability == allProbabilities.peek().probabilityMine) {
            Graph.FringeNode safestMove = allProbabilities.poll();
            safestMoves.add(safestMove);
        }

        return safestMoves;
    }

    private Move getRandomMove(ArrayList<Graph.FringeNode> movesList) {
        Random random = new Random();
        int randomIndex = random.nextInt(movesList.size());
        Graph.FringeNode randomNode = movesList.get(randomIndex);
        return new Move(randomNode.indexInGrid, Coup.SHOW);
    }
}
