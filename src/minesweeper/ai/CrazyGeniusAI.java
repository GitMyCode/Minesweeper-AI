package minesweeper.ai;

import minesweeper.Coup;
import minesweeper.Grid;
import minesweeper.Move;
import minesweeper.ai.dataRepresentation.FringeNode;

import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;

public class CrazyGeniusAI extends ProbabilisticAI {

    @Override
    public Set<Move> getNextMoves(Grid grid, int delay) {
        return super.getNextMoves(grid, delay);
    }

    private double probabiliteCombinaison(List<FringeNode> fringeNodes, int indexCombinaison, double priorMineProbability) {

        double reponse = 1.0;
        for (FringeNode fn : fringeNodes) {
            if (fn.combinationsUsed.contains(indexCombinaison)) {
                reponse *= priorMineProbability;
            } else {
                reponse *= 1.0 - priorMineProbability;
            }
        }

        return reponse;

    }
    protected void addMovesWithProbabilities() {
        PriorityQueue<FringeNode> allProbabilities = new PriorityQueue<FringeNode>();

        int nbMinimumMinesInFrontieres = 0;
        int nbCasesSurFrontiere = 0;

        for (int frontierIndex = 0; frontierIndex < graph.nbFrontiere; frontierIndex++) {

            List<FringeNode> fringeNodes = graph.allFringeNodes.get(frontierIndex);
            int totalValidAssignations = graph.nbValidAssignationsPerFrontier.get(frontierIndex);
            double priorMineProbability = this.gameGrid.priorMineProbability();
            //System.out.println("priorMineProbability = " + priorMineProbability);
            double [] probabiliteCombinaisons = new double [graph.nbValidAssignationsPerFrontier.get(frontierIndex)];

            for (int i = 0; i < graph.nbValidAssignationsPerFrontier.get(frontierIndex); ++i) {
                double proba = probabiliteCombinaison(fringeNodes, i, gameGrid.priorMineProbability());
                System.out.println("probaFrontiere = " + proba);
                probabiliteCombinaisons[i] = proba;
            }

            double alpha = 0.0;
            for (double valeurCombinaison : probabiliteCombinaisons) {
                alpha += valeurCombinaison;
            }

            for (FringeNode fn : fringeNodes) {

                double probaMine = 0.0;
                for (int i = 0; i < graph.nbValidAssignationsPerFrontier.get(frontierIndex); ++i) {
                    if (fn.combinationsUsed.contains(i)) {
                        probaMine += probabiliteCombinaisons[i];
                    }
                }

                //System.out.println("probaMine = " + probaMine);
                probaMine = probaMine / alpha;
                System.out.println("probaMine = " + (float) probaMine);
                fn.probabilityMine = (float) probaMine;

                if (fn.isObviousMine()) {
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.FLAG));
                    addCSPMoveToStats();
                } else if (fn.isSafe()) {
                    this.movesToPlay.add(new Move(fn.indexInGrid, Coup.SHOW));
                    addCSPMoveToStats();
                }
                allProbabilities.offer(fn);

            }

            double probabiliteExterieur = 100.0;

            if (nbCasesSurFrontiere > 0 && graph.gameGrid.getNbUndiscoveredCases() - nbCasesSurFrontiere > 0) {
                probabiliteExterieur = ((double) graph.gameGrid.getNbFlagsRemaining() - (double) nbMinimumMinesInFrontieres)
                        / (graph.gameGrid.getNbUndiscoveredCases() - nbCasesSurFrontiere);
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

        double probabiliteExterieur = 100.0;

        if (nbCasesSurFrontiere > 0 && graph.gameGrid.getNbUndiscoveredCases() - nbCasesSurFrontiere > 0) {
            probabiliteExterieur = ((double) graph.gameGrid.getNbFlagsRemaining() - (double) nbMinimumMinesInFrontieres)
                    / (graph.gameGrid.getNbUndiscoveredCases() - nbCasesSurFrontiere);
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
    public String getName() {
        return "Crazy Genius AI";
    }

}
