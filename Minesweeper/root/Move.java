package root;

import root.ENUM.COUP;

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

    public final COUP coup;
    public final int index;

    public Move(int index, COUP coup){
        this.coup = coup;
        this.index = index;
    }


    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (index != move.index) return false;
        if (coup != move.coup) return false;

        return true;
    }


    @Override
    public int hashCode () {
        int result = coup != null ? coup.hashCode() : 0;
        result = 31 * result + index;
        return result;
    }
}
