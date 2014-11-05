package root.ai;

import root.ArtificialPlayer;
import root.Dir;
import root.ENUM.CASE;
import root.ENUM.COUP;
import root.Grid;
import root.Move;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martin on 11/4/2014.
 */
public class nIA implements ArtificialPlayer{
    @Override
    public Set<Move> getAiPlay(Grid g, int thinkLimit) {

        CASE[] gc = g.getCpyPlayerView();

        Set<Move> test = new HashSet<Move>();
        g.getSurroundingIndex(40);

        int index = 45;
        for(Integer s : g.getSurroundingIndex(index)){
            test.add(new Move(s, COUP.FLAG));
        }



        return test;
    }

    @Override
    public String getAiName() {
        return "test";
    }
}
