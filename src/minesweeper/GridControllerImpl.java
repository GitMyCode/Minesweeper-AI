package minesweeper;

import minesweeper.ENUM.COUP;

import javax.swing.*;
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
public class GridControllerImpl implements GridController {

    private Grid gridBoard;
    private GridView gridView;
    private final JLabel flagRemain;

    public GridControllerImpl(Grid gm, GridView gv,JLabel refFlagRemain){
        gridBoard = gm;
        gridView = gv;
        flagRemain = refFlagRemain;
    }

    @Override
    public void caseClicked (int ligne, int colonne) {
        int index = ligne* gridBoard.nbCols + colonne;

        gridBoard.play(index, COUP.SHOW);


        flagRemain.setText(String.valueOf(gridBoard.nbFlagsRemaining));
        gridView.repaint();

    }

    @Override
    public synchronized void caseClicked (int indexCase) {

        System.out.println("case cliquée");
    }

    @Override
    public synchronized void movesSetPlay (Set<Move> moves) {
        for(Move m : moves){
            gridBoard.play(m.index, m.coup);
        }
        flagRemain.setText(String.valueOf(gridBoard.getNbFlagsRemaining()));
        gridView.repaint();
    }

    @Override
    public synchronized void movePlay (Move move) {
        gridBoard.play(move.index, move.coup);
        flagRemain.setText(String.valueOf(gridBoard.getNbFlagsRemaining()));
        gridView.repaint();
    }

    public void setGridModel(Grid g){
        gridBoard = g;
    }
    public void setGridView(GridView gv){
        gridView = gv;
    }

}