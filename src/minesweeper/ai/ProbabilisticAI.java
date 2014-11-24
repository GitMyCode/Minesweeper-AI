package minesweeper.ai;

import minesweeper.Grid;
import minesweeper.Move;

import java.util.Set;

public class ProbabilisticAI extends CSPGraph{

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {
        return super.getNextMoves(grid, delay);
    }

    @Override
    public String getName() { return "Probabilistic AI"; }
}
