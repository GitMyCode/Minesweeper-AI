package minesweeper.ai;

import minesweeper.ArtificialPlayer;
import static minesweeper.ENUM.CASEGRILLE.*;
import static minesweeper.ENUM.COUP.*;

import minesweeper.ENUM.CASEGRILLE;
import minesweeper.ENUM.COUP;
import minesweeper.Grid;
import minesweeper.Move;

import java.util.*;

/**
* Projet de joueur artificiel de Minesweeper avec différents algorithmes
* Dans le cadre du 3e TP en Intelligence Artificielle (INF4230)
*
* Automne 2014
* Par l'équipe:
*   Martin Bouchard
*   Frédéric Vachon
*   Louis-Bertrand Varin
*   Geneviève Lalonde
*   Nilovna Bascunan-Vasquez
*/
public class RandomAi implements ArtificialPlayer {
    public RandomAi() {
    }

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {

        CASEGRILLE[] myView = grid.getCpyPlayerView();
        Random ran = new Random();

        List<Integer> legalMoves = new ArrayList<Integer>();
        for(int i=0; i< grid.length; i++){
            if (myView[i] == UNDISCOVERED){
                legalMoves.add(i);
            }
        }


        int index = legalMoves.get(ran.nextInt(legalMoves.size()));

        Set<COUP> coupSet = grid.getLegalCaseCoup(index);
        int ranCoup = ran.nextInt(coupSet.size());
        int i = 0;
        COUP coup = INVALID;
        for (COUP c : coupSet){
            if (i==ranCoup){
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
    public String getName() {
        return "Random";
    }
}
