package minesweeper.ai;

import minesweeper.*;

import static minesweeper.Case.*;
import static minesweeper.Coup.*;

import minesweeper.Case;

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

        Case[] myView = grid.getCpyPlayerView();
        Random ran = new Random();

        List<Integer> legalMoves = new ArrayList<Integer>();
        for(int i=0; i< grid.length; i++){
            if (myView[i] == UNDISCOVERED){
                legalMoves.add(i);
            }
        }


        int index = legalMoves.get(ran.nextInt(legalMoves.size()));

        Set<Coup> coupSet = grid.getLegalCaseCoup(index);
        int ranCoup = ran.nextInt(coupSet.size());
        int i = 0;
        Coup coup = INVALID;
        for (Coup c : coupSet){
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
