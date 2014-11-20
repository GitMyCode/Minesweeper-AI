package minesweeper.ai;

import minesweeper.ArtificialPlayer;
import minesweeper.Grid;
import minesweeper.Move;

import java.util.Set;

/**
 * Created by fred on 20/11/14.
 */
public class ProbabilisticAI implements ArtificialPlayer {

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {
        return null;
    }

    @Override
    public String getName() {
        return "Probabilistic AI";
    }
}
