package root.ai;

import root.ArtificialPlayer;
import root.ENUM.CASEGRILLE;
import root.ENUM.COUP;
import root.Grid;
import root.Move;

import java.util.HashSet;
import java.util.Set;

public class nIA implements ArtificialPlayer{
    @Override
    public Set<Move> getNextPossibleMoves(Grid g, int thinkLimit) {
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
