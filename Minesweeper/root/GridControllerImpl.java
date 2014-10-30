package root;

import root.ENUM.COUP;

import javax.swing.*;

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


    public void setGridModel(Grid g){
        gridBoard = g;
    }
    public void setGridView(GridView gv){
        gridView = gv;
    }

}
