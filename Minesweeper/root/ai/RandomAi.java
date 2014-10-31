package root.ai;


import root.ArtificialPlayer;
import root.ENUM.CASE;
import root.ENUM.COUP;
import root.Grid;
import root.Move;

import java.util.*;

/**
 * Created by MB on 10/30/2014.
 */
public class RandomAi implements ArtificialPlayer {


    public RandomAi(){

    }


    @Override
    public Set<Move> getAiPlay (Grid g) {

        CASE[] myView = g.getCpyPlayerView();
        Random ran = new Random();

        List<Integer> legalMoves = new ArrayList<Integer>();
        for(int i=0; i< g.length; i++){
            if(myView[i] ==CASE.UNDISCOVERED){
                legalMoves.add(i);
            }
        }


        COUP c = COUP.values()[ran.nextInt(2)];
        int index = legalMoves.get(ran.nextInt(legalMoves.size()));

        Set<Move> moves = new HashSet<Move>();
        moves.add(new Move(index,c));
        return moves;
    }

    @Override
    public String getAiType () {
        return null;
    }
}
