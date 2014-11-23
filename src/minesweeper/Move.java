package minesweeper;

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
public class Move {

    private static final int HASH = 31;
    public final Coup coup;
    public final int index;

    public Move(int index, Coup coup) {
        this.coup = coup;
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Move move = (Move) o;

        if (this.index != move.index) {
            return false;
        }

        if (this.coup != move.coup) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (this.coup != null) {
            result = this.coup.hashCode();
        }
        return (HASH * result) + index;
    }

}
