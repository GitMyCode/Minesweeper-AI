package minesweeper.ui;

import minesweeper.Move;

import java.util.EventListener;
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
interface GridController extends EventListener {

    public void caseClicked(int ligne, int colonne);
    public void caseClicked(int indexCase);

    public void movesSetPlay(Set<Move> moves);
    public void movePlay(Move move);

}
