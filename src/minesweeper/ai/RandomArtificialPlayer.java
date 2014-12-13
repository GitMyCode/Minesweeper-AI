package minesweeper.ai;

import minesweeper.*;

import java.util.*;

/**
 * Projet de joueur artificiel de Minesweeper avec différents algorithmes
 * Dans le cadre du 3e TP en Intelligence Artificielle (INF4230)
 * <p/>
 * Automne 2014
 * Par l'équipe:
 * Martin Bouchard
 * Frédéric Vachon
 * Louis-Bertrand Varin
 * Geneviève Lalonde
 * Nilovna Bascunan-Vasquez
 */
public class RandomArtificialPlayer implements ArtificialPlayer, Benchmarkable {

    protected Grid gameGrid;

    protected int nbUncertainMoves = 0;
    protected int nbTotalMoves = 0;
    protected int nbProbabilityFails = 0;
    protected int nbProbabilitySuccess = 0;

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {

        gameGrid = grid;
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
        Move move = new Move(index, coup);
        moves.add(move);
        addUncertainMoveToStats(move);
        return moves;

    }

    protected void addUncertainMoveToStats(Move move) {
        Set<Move> moves = new LinkedHashSet<Move>();
        moves.add(move);

        if (!gameGrid.checkMove(moves).isEmpty()) {
            nbProbabilityFails++;
        } else {
            nbProbabilitySuccess++;
        }

        nbUncertainMoves++;
        nbTotalMoves++;
    }

    @Override
    public String getName() {
        return "Random Artificial Player";
    }

    @Override
    public int getNbProbabilitySuccess() {
        return nbProbabilitySuccess;
    }

    @Override
    public int getNbProbabilityFails() {
        return nbProbabilityFails;
    }

    @Override
    public double getTrivialMoveRate() {
        return 0;
    }

    @Override
    public double getCSPMoveRate() {
        return 0;
    }

    @Override
    public double getUncertainMoveRate() {
        return 0;
    }
}
