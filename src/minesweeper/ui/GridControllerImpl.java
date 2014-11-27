package minesweeper.ui;

import minesweeper.Coup;
import minesweeper.Grid;
import minesweeper.Move;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
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

        gridBoard.play(index, Coup.SHOW);
        flagRemain.setText(String.valueOf(gridBoard.nbFlagsRemaining));
        updateView();

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
        updateView();
    }

    @Override
    public synchronized void movePlay (Move move) {
        gridBoard.play(move.index, move.coup);
        flagRemain.setText(String.valueOf(gridBoard.getNbFlagsRemaining()));
        updateView();
    }

    public synchronized void updateView(){

            Runnable codeToRun = new Runnable() {
                @Override
                public void run () {
                    gridView.repaint();
                }
            };

            if(SwingUtilities.isEventDispatchThread()){
                codeToRun.run();
            }else {
                try {
                    SwingUtilities.invokeAndWait(codeToRun);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
    }

    public void setGridModel(Grid g){
        gridBoard = g;
    }
    public void setGridView(GridView gv){
        gridView = gv;
    }

}
