package minesweeper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Set;

import minesweeper.ai.*;

public final class Benchmarks {

    public static final int NB_PARTIES = 400;
    public static final int[] GRIDS_COLUMNS = { 30 };
    public static final int[] GRIDS_ROWS    = { 16 };
    public static final double[] MINE_RATIOS  = { 0.15, 0.25, 0.30 };
    public static final ArtificialPlayer[] JOUEURS = {
            new RandomArtificialPlayer(),
            new SafeOrRandomAI(),
            new ProbabilisticAI(),
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

        for (int i = 0; i < MINE_RATIOS.length; i++) {
            System.out.println("###################");
            System.out.println("# RATIO MINE "+ formatter.format(MINE_RATIOS[i]) +" #");
            System.out.println("###################");
            System.out.println();
            for (int j = 0; j < GRIDS_COLUMNS.length; j++) {
                for (ArtificialPlayer ai : JOUEURS) {
                    benchGame(ai, GRIDS_ROWS[j], GRIDS_COLUMNS[j], MINE_RATIOS[i]);
                }
            }
        }

    }

    private static void benchGame(ArtificialPlayer ai, int grid_rows, int grid_columns, double mine_ratio) {
        int nb_mines = (int) ((grid_rows * grid_columns) * mine_ratio);
        nbVictoires = 0;
        timeToSolutionList = new ArrayList<Long>();
        trivialMoveRateList = new ArrayList<Double>();
        safeMoveRateList = new ArrayList<Double>();
        uncertainMoveRateList = new ArrayList<Double>();
        nbProbabilitySuccess = 0;
        nbProbabilityFails = 0;


        for (int i = 0; i < NB_PARTIES; ++i) {
            startTime = System.currentTimeMillis();
            Grid grille = new Grid(grid_rows, grid_columns, nb_mines);

            while (!grille.gameIsFinished()) {
                Set<Move> moves = ai.getNextMoves(grille, Integer.MAX_VALUE);
                for (Move m : moves) {
                    grille.play(m.index, m.coup);
                }
            }

            if (grille.gameWon) {
                nbVictoires++;
                endTime = System.currentTimeMillis();
                timeToSolutionList.add(endTime - startTime);
            }

            trivialMoveRateList.add(((Benchmarkable) ai).getTrivialMoveRate());
            safeMoveRateList.add(((Benchmarkable) ai).getCSPMoveRate());
            uncertainMoveRateList.add(((Benchmarkable) ai).getUncertainMoveRate());
            nbProbabilitySuccess += ((Benchmarkable) ai).getNbProbabilitySuccess();
            nbProbabilityFails += ((Benchmarkable) ai).getNbProbabilityFails();
        }

        printResult(ai);
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

    public static double getVictoryRate() {
        return ((double) nbVictoires / NB_PARTIES) * 100;
    }

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
