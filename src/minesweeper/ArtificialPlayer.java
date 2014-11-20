package minesweeper;

import java.util.Set;

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
public interface ArtificialPlayer {
    /**
     * Obtenir les prochains coups joué par le AI
     *
     * @param g : La grille de jeu actuelle
     * @param thinkLimit : Temps limite de reflexion. Arrete la recursion
     * @return Set des prochains coups possibles
     */
    public Set<Move> getNextMoves(Grid g, int thinkLimit);

    /**
     * Name display of the AI
     * @return the name of the AI
     */
    public String getName();
}
