/**
 * Created by martin on 18/11/14.
 */
package minesweeper.ai;

import minesweeper.*;
import minesweeper.ai.dataRepresentation.FringeNode;
import minesweeper.ai.dataRepresentation.Graph;
import minesweeper.ai.strategyCSP.ForwardCheckCSP;
import minesweeper.ai.strategyCSP.RemainingFlagsCSP;
import minesweeper.ai.strategyCSP.SimpleCSP;
import minesweeper.ai.strategyCSP.StrategyCSP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static minesweeper.Case.UNDISCOVERED;

public class SafeOrRandomAI implements ArtificialPlayer, Benchmarkable {

    private final int LIMITE = 10;
    private long timer;
    private long remain;
    private boolean END = false;

    protected Grid gameGrid;
    protected Graph graph;
    protected Set<Move> movesToPlay;

    protected int nbTrivialMoves;
    protected int nbCSPMoves;
    protected int nbUncertainMoves;
    protected int nbTotalMoves;

    StrategyCSP csp;

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {
        this.gameGrid = grid;
        Case[] gridCopy = grid.getCpyPlayerView();
        startTimer(delay);
        this.movesToPlay = grid.getSafeMoves();

        if (!this.movesToPlay.isEmpty()) {
            addTrivialMoveToStats();
            return movesToPlay;
        }

        csp = new SimpleCSP();

        graph = new Graph(grid);
        csp.executeCSPonGraph(graph);

        addMovesToPlay(grid, gridCopy);


        return movesToPlay;
    }

    private boolean isTimeUp() {
        END = (timeRemaining() < LIMITE) ? true : END;
        return END;
    }


    protected void addMovesToPlay(Grid grid, Case[] gridCopy) {
        if (this.movesToPlay.isEmpty()) {
            addSafeMovesAndFlags();
            if (this.movesToPlay.isEmpty()) {
                addRandomMove(grid, gridCopy);
                addUncertainMoveToStats();
            } else {
                Set<Move> errors = grid.checkMove(movesToPlay);
                if (!errors.isEmpty()) {
                    System.out.println("ERREUR");
                }


                addCSPMoveToStats();
            }
        }
    }

    protected void addSafeMovesAndFlags() {
        for (int frontierIndex = 0; frontierIndex < graph.nbFrontiere; frontierIndex++) {
            List<FringeNode> fringeNodes = graph.allFringeNodes.get(frontierIndex);
            int nbPossibilityHere = graph.nbValidAssignationsPerFrontier.get(frontierIndex);

            for (FringeNode fn : fringeNodes) {
                if (fn.nbFlagsHit == 0) {
                    // 0% Mine
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.SHOW));
                } else if (fn.nbFlagsHit == nbPossibilityHere) {
                    // 100% Mine
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.FLAG));
                }
            }
        }
    }

    protected void addRandomMove(Grid grid, Case[] gridCopy) {
        List<Integer> legalMoves = new ArrayList<Integer>();
        for (int i = 0; i < grid.length; i++) {
            if (gridCopy[i] == UNDISCOVERED) {
                legalMoves.add(i);
            }
        }

        Random ran = new Random();
        int index = legalMoves.get(ran.nextInt(legalMoves.size()));
        this.movesToPlay.add(new Move(index, Coup.SHOW));
    }

    private void startTimer(int delai) {
        END = false;
        timer = System.currentTimeMillis();
        remain = delai;
    }

    private String showTimeRemaing() {
        return ("Time: " + timeRemaining() + " ms");
    }

    public long timeRemaining() {
        long elaspsed = (System.currentTimeMillis() - timer);
        return remain - elaspsed;
    }

    protected void addTrivialMoveToStats() {
        nbTrivialMoves++;
        nbTotalMoves++;
    }

    protected void addCSPMoveToStats() {
        nbCSPMoves++;
        nbTotalMoves++;
    }

    protected void addUncertainMoveToStats() {
        nbUncertainMoves++;
        nbTotalMoves++;
    }

    @Override
    public String getName() {
        return "Safe or Random";
    }

    @Override
    public boolean isProbabilistic() {
        return false;
    }

    @Override
    public double getProbabilitySuccessRate() {
        return 0;
    }

    @Override
    public double getTrivialMoveRate() {
        return (double) nbTrivialMoves / nbTotalMoves;
    }

    @Override
    public double getCSPMoveRate() {
        return (double) nbCSPMoves / nbTotalMoves;
    }

    @Override
    public double getUncertainMoveRate() {
        return (double) nbUncertainMoves / nbTotalMoves;
    }
}
