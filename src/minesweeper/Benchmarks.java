package minesweeper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Set;

import minesweeper.ai.*;

public final class Benchmarks {

    public final static int NB_PARTIES = 400;
    public final static int GRID_ROWS = 16;
    public final static int GRID_COLUMNS = 30;
    public final static int NB_MINES = 99;

    public final static ArtificialPlayer[] JOUEURS = {
            new SafeOrRandomAI(),
            new ProbabilisticAI(),
            new ProbabilisticAIwithRandomSelection(),
            new AdventurerAI()
    };

    private static long startTime = 0;
    private static long endTime = 0;
    private static int nbVictoires;
    private static ArrayList<Long> timeToSolutionList;
    private static ArrayList<Double> trivialMoveRateList;
    private static ArrayList<Double> safeMoveRateList;
    private static ArrayList<Double> uncertainMoveRateList;
    private static int nbProbabilitySuccess;
    private static int nbProbabilityFails;
    private static NumberFormat formatter = new DecimalFormat("#0.00");

    private Benchmarks() {

    }

    public static void main(final String[] args) {

        for (ArtificialPlayer ai: JOUEURS) {
            nbVictoires = 0;
            timeToSolutionList = new ArrayList<Long>();
            trivialMoveRateList = new ArrayList<Double>();
            safeMoveRateList = new ArrayList<Double>();
            uncertainMoveRateList = new ArrayList<Double>();
            nbProbabilitySuccess = 0;
            nbProbabilityFails = 0;


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

                trivialMoveRateList.add(((Benchmarkable)ai).getTrivialMoveRate());
                safeMoveRateList.add(((Benchmarkable)ai).getCSPMoveRate());
                uncertainMoveRateList.add(((Benchmarkable)ai).getUncertainMoveRate());
                nbProbabilitySuccess += ((Benchmarkable) ai).getNbProbabilitySuccess();
                nbProbabilityFails += ((Benchmarkable) ai).getNbProbabilityFails();
                //System.out.println("nbProbabilitySuccess : " + nbProbabilitySuccess);
                //System.out.println("nbProbabilityFails : " + nbProbabilityFails);
            }

            printResult(ai);
        }

    }

    public static double getMeanTimeToSolution() {
        double totalTime = 0;

        if (timeToSolutionList.isEmpty()) {
            return 0.0;
        }

        for (long time: timeToSolutionList) {
            totalTime += time;
        }

        return (double) totalTime / timeToSolutionList.size();
    }

    public static double getAverageProbabilitySuccessRate() {
        double total = (double) nbProbabilityFails / (nbProbabilitySuccess  + nbProbabilityFails);
        return (1 - total) * 100;
    }

    public static double getVictoryRate() { return ((double) nbVictoires / NB_PARTIES) * 100; }
    public static double getLossRate() {
        return ((double) (NB_PARTIES - nbVictoires) / NB_PARTIES) * 100;
    }

    public static double getAverageTrivialMoveRate() {
        double totalTime = 0;

        if (trivialMoveRateList.isEmpty()) {
            return 0.0;
        }

        for (double time: trivialMoveRateList) {
            totalTime += time;
        }

        return ((double) totalTime / trivialMoveRateList.size()) * 100;
    }

    public static double getAverageSafeMoveRate() {
        double totalTime = 0;

        if (safeMoveRateList.isEmpty()) {
            return 0.0;
        }

        for (double time: safeMoveRateList) {
            totalTime += time;
        }

        return ((double) totalTime / safeMoveRateList.size()) * 100;
    }

    public static double getAverageUncertainMoveRate() {
        double totalTime = 0;

        if (uncertainMoveRateList.isEmpty()) {
            return 0.0;
        }

        for (double time: uncertainMoveRateList) {
            totalTime += time;
        }

        return ((double) totalTime / uncertainMoveRateList.size()) * 100;
    }

    private static void printResult(ArtificialPlayer ai) {
        System.out.println("--- " + ai.getName() + " ---");
        System.out.println("Nombre de victoires : " + nbVictoires);
        System.out.println("% de victoires : " + formatter.format(getVictoryRate()) + "%");
        System.out.println("% de défaites : " + formatter.format(getLossRate()) + "%");
        System.out.println("Temps de résolution moyen : " + formatter.format(getMeanTimeToSolution()) + "ms");
        System.out.println("Taux de réussite moyen sous incertitude : " + formatter.format(getAverageProbabilitySuccessRate()) + "%");
        System.out.println("Taux moyen de coups triviaux : " + formatter.format(getAverageTrivialMoveRate()) + "%");
        System.out.println("Taux moyen de coups certains : " + formatter.format(getAverageSafeMoveRate()) + "%");
        System.out.println("Taux moyen de coups incertains : " + formatter.format(getAverageUncertainMoveRate()) + "%");
        System.out.println();
    }
}
