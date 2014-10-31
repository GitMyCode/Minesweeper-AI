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
        int index = ligne* gridBoard.nbcol + colonne;
        //gridBoard.gridSpace[index] = 1;

        gridBoard.set(index, COUP.SHOW);


        flagRemain.setText(String.valueOf(gridBoard.nbFlagRemaining));
        gridView.repaint();

    }

    @Override
    public void caseClicked (int indexCase) {

        System.out.println("dsfsdf");
    }

    @Override
    public void movesSetPlay (Set<Move> moves) {
        for(Move m : moves){
            gridBoard.set(m.index,m.coup);
        }
        flagRemain.setText(String.valueOf(gridBoard.getNbFlagRemaining()));
        gridView.repaint();
    }


    public void setGridModel(Grid g){
        gridBoard = g;
    }
    public void setGridView(GridView gv){
        gridView = gv;
    }

}
