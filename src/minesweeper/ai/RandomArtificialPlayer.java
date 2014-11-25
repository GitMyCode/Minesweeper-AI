package minesweeper.ai;

import minesweeper.Grid;
import minesweeper.Move;
import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.ArtificialPlayer;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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
public class RandomArtificialPlayer implements ArtificialPlayer {

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {

        Case[] myView = grid.getCpyPlayerView();
        Random ran = new Random();

        List<Integer> legalMoves = new ArrayList<Integer>();
        for (int i = 0; i < grid.length; i++) {
            if (myView[i] == Case.UNDISCOVERED) {
                legalMoves.add(i);
            }
        }


        int index = legalMoves.get(ran.nextInt(legalMoves.size()));

        Set<Coup> coupSet = grid.getLegalCaseCoup(index);
        int ranCoup = ran.nextInt(coupSet.size());
        int i = 0;
        Coup coup = Coup.INVALID;
        for (Coup c : coupSet) {
            if (i == ranCoup) {
                coup = c;
                break;
            }
            i++;
        }

        Set<Move> moves = new HashSet<Move>();
        moves.add(new Move(index, coup));
        return moves;

    }

    @Override
    public String getName() {
        return "Random Artificial Player";
    }

}
