package minesweeper.ai;

import minesweeper.ArtificialPlayer;
import minesweeper.ENUM.CASEGRILLE;
import minesweeper.ENUM.COUP;
import minesweeper.Grid;
import minesweeper.Move;

import java.util.HashSet;
import java.util.Set;

public class nIA implements ArtificialPlayer{
    @Override
    public Set<Move> getNextMoves(Grid g, int thinkLimit) {
        Set<Move> test = new HashSet<Move>();
        g.getSurroundingIndex(40);

        int index = 45;
        for(Integer s : g.getSurroundingIndex(index)){
            test.add(new Move(s, COUP.FLAG));
        }

        return test;
    }

    @Override
    public String getName() {
        return "test";
    }
}