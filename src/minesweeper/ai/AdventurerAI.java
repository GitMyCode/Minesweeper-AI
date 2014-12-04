package minesweeper.ai;

import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.Grid;
import minesweeper.Move;
import minesweeper.ai.dataRepresentation.FringeNode;

import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;

public class AdventurerAI extends ProbabilisticAI {

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {
        return super.getNextMoves(grid, delay);
    }

    protected void addMovesWithProbabilities() {
        PriorityQueue<FringeNode> allProbabilities = new PriorityQueue<FringeNode>();

        int nbMinimumMinesInFrontieres = 0;
        int nbCasesSurFrontiere = 0;

        for (int frontierIndex = 0; frontierIndex < graph.nbFrontiere; frontierIndex++) {

            List<FringeNode> fringeNodes = graph.allFringeNodes.get(frontierIndex);
            int totalValidAssignations = graph.nbValidAssignationsPerFrontier.get(frontierIndex);

            nbCasesSurFrontiere += fringeNodes.size();
            nbMinimumMinesInFrontieres += graph.nbMinimalAssignementsPerFrontier.get(frontierIndex);

            for (FringeNode fn : fringeNodes) {
                fn.computeMineProbability(totalValidAssignations);
                if (fn.isObviousMine()) {
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.FLAG));
                    addCSPMoveToStats();
                } else if (fn.isSafe()) {
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.SHOW));
                    addCSPMoveToStats();
                }
                allProbabilities.offer(fn);

            }
        }

        double probabiliteExterieur = 100.0;

        if (nbCasesSurFrontiere > 0 && graph.gameGrid.getNbUndiscoveredCases() - nbCasesSurFrontiere > 0) {
            probabiliteExterieur = ((double)graph.gameGrid.getNbFlagsRemaining() - (double)nbMinimumMinesInFrontieres) / 
                                    (graph.gameGrid.getNbUndiscoveredCases() - nbCasesSurFrontiere);
        }

        if (probabiliteExterieur <= 0.0) {
            ArrayList<Integer> undiscoveredIndexes = graph.gameGrid.getUndiscoveredCases();
            for (int i : undiscoveredIndexes) {
                movesToPlay.add(new Move(i, Coup.SHOW));
            }
        }


        if (movesToPlay.isEmpty() && !allProbabilities.isEmpty()) {
            Move safestMove = getSafestMove(allProbabilities, probabiliteExterieur);
            this.movesToPlay.add(safestMove);
        }
    }

    protected Move getSafestMove(PriorityQueue<FringeNode> allProbabilities, double probabiliteExterieur) {
        Move reponse;
        FringeNode safestMove = allProbabilities.poll();
        ArrayList<Integer> undiscoveredIndex = graph.gameGrid.getUndiscoveredCases();
        if (!undiscoveredIndex.isEmpty()
                && probabiliteExterieur < safestMove.probabilityMine) {

            reponse = new Move(undiscoveredIndex.get(0), Coup.SHOW);
        } else {
            reponse = new Move(safestMove.indexInGrid, Coup.SHOW);
        }
        addUncertainMoveToStats(reponse);
        return reponse;
    }

    @Override
    public String getName() { return "Adventurer AI"; }
}
