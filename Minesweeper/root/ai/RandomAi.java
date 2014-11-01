package root.ai;


import root.ArtificialPlayer;
import static root.ENUM.CASE.*;
import static root.ENUM.COUP.*;

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
    public Set<Move> getAiPlay (Grid g,int thinkLimit) {

        CASE[] myView = g.getCpyPlayerView();
        Random ran = new Random();

        List<Integer> legalMoves = new ArrayList<Integer>();
        for(int i=0; i< g.length; i++){
            if(myView[i] == UNDISCOVERED){

                legalMoves.add(i);
            }
        }



        int index = legalMoves.get(ran.nextInt(legalMoves.size()));

        Set<COUP> coupSet = g.getLegalCaseCoup(index);
        int ranCoup = ran.nextInt(coupSet.size());
        int i=0;
        COUP coup = INVALID;
        for(COUP c : coupSet){
            if(i==ranCoup){
                coup=c;
                break;
            }
            i++;
        }


        Set<Move> moves = new HashSet<Move>();
        moves.add(new Move(index,coup));
        return moves;
    }

    @Override
    public String getAiName () {
        return "Random";
    }
}
