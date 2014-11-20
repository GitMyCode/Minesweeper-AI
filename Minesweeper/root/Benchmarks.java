package root;

import java.util.Set;
import root.ai.CSP;
import root.ai.RandomAi;

public final class Benchmarks {

    public final static int NB_PARTIES = 100;
    public final static int TAILLE_GRILLE = 50;
    public final static ArtificialPlayer[] JOUEURS = {new RandomAi(), new CSP()};

    private Benchmarks() {

    }

    public static void main(final String[] args) {

        for (ArtificialPlayer ai: JOUEURS) {
            int nbVictoires = 0;
            for(int i=0; i<NB_PARTIES; ++i) {
                Grid grille = new Grid(TAILLE_GRILLE, TAILLE_GRILLE, 400);
                while(!grille.gameIsFinished()) {
                    Set<Move> moves = ai.getNextMoves(grille, Integer.MAX_VALUE);
                    for(Move m : moves){
                        grille.play(m.index, m.coup);
                    }
                }
                if (grille.gameWon) {
                    nbVictoires++;
                }
            }
            System.out.println("--- " + ai.getName() + " ---");
            System.out.println("Nombre de victoires : " + nbVictoires);
        }

    }

}
