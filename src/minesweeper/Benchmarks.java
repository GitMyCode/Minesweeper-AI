package minesweeper;

import java.util.ArrayList;
import java.util.Set;

import minesweeper.ai.*;

public final class Benchmarks {

    public final static int NB_PARTIES = 10;
    public final static int GRID_ROWS = 50;
    public final static int GRID_COLUMNS = 50;
    public final static int NB_MINES = 10;

    public final static ArtificialPlayer[] JOUEURS = {
            new RandomArtificialPlayer(),
            new SafeOrRandomAI(),
            new ProbabilisticAI(),
            new ProbabilisticAIwithRandomSelection(),
            new AdventurerAI()
    };

    private static long startTime = 0;
    private static long endTime = 0;
    private static int nbVictoires;
    private static ArrayList<Long> timeToSolutionList;
    private static ArrayList<Double> probabilitySuccessRateList;

    private Benchmarks() {

    }

    public static void main(final String[] args) {

        for (ArtificialPlayer ai: JOUEURS) {
            nbVictoires = 0;
            timeToSolutionList = new ArrayList<Long>();

            for(int i=0; i<NB_PARTIES; ++i) {
                startTime = System.currentTimeMillis();
                Grid grille = new Grid(GRID_ROWS, GRID_COLUMNS, NB_MINES);

                while(!grille.gameIsFinished()) {
                    Set<Move> moves = ai.getNextMoves(grille, Integer.MAX_VALUE);
                    for(Move m : moves){
                        grille.play(m.index, m.coup);
                    }
                }

                if (grille.gameWon) {
                    nbVictoires++;
                    endTime = System.currentTimeMillis();
                    timeToSolutionList.add(endTime - startTime);
                }

            }

            System.out.println("--- " + ai.getName() + " ---");
            System.out.println("Nombre de victoires : " + nbVictoires);
            System.out.println("% de victoires : " + getVictoryRate() + "%");
            System.out.println("% de défaites : " + getLossRate() + "%");
            System.out.println("Temps de résolution moyen : " + getMeanTimeToSolution() + "ms");
            System.out.println("Taux de réussite moyen sous incertitude : " + getMeanTimeToSolution() + "%");
        }

    }

    public static double getMeanTimeToSolution() {
        long totalTime = 0;

        if (timeToSolutionList.isEmpty()) {
            return 0.0;
        }

        for (long time: timeToSolutionList) {
            totalTime += time;
        }

        return (double) totalTime / timeToSolutionList.size();
    }

    public static double getAverageProbabilitySuccessRate() {
        double total = 0.0;

        for (Double d: probabilitySuccessRateList) {
            total += d;
        }

        return total / probabilitySuccessRateList.size();
    }

    public static double getVictoryRate() {
        return ((double) nbVictoires / NB_PARTIES) * 100;
    }
    public static double getLossRate() {
        return ((double) (NB_PARTIES - nbVictoires) / NB_PARTIES) * 100;
    }
}
