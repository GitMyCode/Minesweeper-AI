package root;

import root.ENUM.COUP;

import javax.swing.*;
import java.util.Set;

/**
 * Created by MB on 10/29/2014.
 */
public class GridControllerImpl implements GridController {

    public Grid gridBoard;
    public GridView gridView;
    public JLabel flagRemain;

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

        System.out.println("dsfsdf");
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
