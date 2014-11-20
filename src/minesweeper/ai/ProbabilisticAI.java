package minesweeper.ai;

import minesweeper.ArtificialPlayer;
import minesweeper.Grid;
import minesweeper.Move;

import java.util.Set;

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
