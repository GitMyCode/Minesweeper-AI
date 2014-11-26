package minesweeper;

import java.util.ArrayList;
import java.util.Set;

import minesweeper.ai.CSPGraph;
import minesweeper.ai.ProbabilisticAI;
import minesweeper.ai.ProbabilisticAIwithRandomSelection;
import minesweeper.ai.RandomArtificialPlayer;

public final class Benchmarks {

    public final static int NB_PARTIES = 10;
    public final static int TAILLE_GRILLE = 50;
    public final static ArtificialPlayer[] JOUEURS = {
            new RandomArtificialPlayer(),
            new CSPGraph(),
            new ProbabilisticAI(),
            new ProbabilisticAIwithRandomSelection()
    };

    private static long startTime = 0;
    private static long endTime = 0;
    private static int nbVictoires;
    private static ArrayList<Long> timeToSolutionList;

    private Benchmarks() {

    }

    public static void main(final String[] args) {

        for (ArtificialPlayer ai: JOUEURS) {
            nbVictoires = 0;
            timeToSolutionList = new ArrayList<Long>();

            for(int i=0; i<NB_PARTIES; ++i) {
                startTime = System.currentTimeMillis();
                Grid grille = new Grid(TAILLE_GRILLE, TAILLE_GRILLE, 250);

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

    public static double getVictoryRate() {
        return ((double) nbVictoires / NB_PARTIES) * 100;
    }
    public static double getLossRate() {
        return ((double) (NB_PARTIES - nbVictoires) / NB_PARTIES) * 100;
    }
}
