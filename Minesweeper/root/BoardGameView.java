package root;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by MB on 10/29/2014.
 */
public class BoardGameView extends JFrame implements ActionListener{


    private JPanel gridPanel;
    private Rule les_y;
    private Rule les_x;
    private Box cadre;
    private JPanel containter;
    private JLabel flagRemaining;

    GridView gv;

    public Grid grid;

    int nbligne=0;
    int nbcol=0;



    public BoardGameView (int nbligne, int nbcol, int nbMine){
        setSize(WIDTH + 300, HEIGHT + 100);


        this.nbcol = nbcol;
        this.nbligne = nbligne;
        cadre = new Box(BoxLayout.Y_AXIS);
        cadre.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        cadre.add(Box.createVerticalGlue());
        cadre.add(Box.createHorizontalGlue());


        createGridView(nbligne, nbcol);
        flagRemaining = new JLabel(""+nbMine);
        add(flagRemaining,BorderLayout.SOUTH);

        grid = new Grid(nbligne,nbcol,nbMine);
        gv.setGrid(grid);
        GridController gc = new GridControllerImpl(grid,gv,flagRemaining);
        gv.setController(gc);

        pack();

    }


    public void createGridView(int row,int col){


        int width = (col*15) ; //pour expert : 480
        int height = (row * 15); //pour expert :280

        les_y = new Rule(1,col);
        Dimension dim_y = new Dimension(width+20,7);
        les_y.setPreferredSize(dim_y);
        les_y.setMinimumSize(dim_y);
        les_y.setMaximumSize(dim_y);
        les_y.setLayout(new GridLayout(1,col));

        les_x = new Rule(0,row);
        Dimension dim_x = new Dimension(10,height);
        les_x.setPreferredSize(dim_x);
        les_x.setMinimumSize(dim_x);
        les_x.setMaximumSize(dim_x);
        // les_x.setBackground(Color.CYAN);
        les_x.setLayout(new GridLayout(row,1));



        containter = new JPanel(new GridBagLayout());
        Dimension dim_container = new Dimension(width+40,height+30);
        containter.setMaximumSize(dim_container);
        containter.setMinimumSize(dim_container);
        containter.setPreferredSize(dim_container);
        //containter.setBackground(Color.red);


        GLOBAL.addItem(containter, les_y, 1, 0, 0, 7, GridBagConstraints.NORTHWEST);

        GLOBAL.addItem(containter, les_x, 0, 1, 0, 7, GridBagConstraints.WEST);


        gv = new GridView(row,col,width,height);
        gv.repaint();
        gv.setBackground(Color.cyan);



        GLOBAL.addItem(containter, gv, 1, 2, 0, 0, GridBagConstraints.CENTER);


        Dimension dim_cadre = new Dimension(width+30,height+30);
        cadre.setPreferredSize(dim_cadre);
        cadre.setMaximumSize(dim_cadre);
        cadre.setMaximumSize(dim_cadre);


        cadre.add(containter);
        cadre.add(Box.createVerticalGlue());
        cadre.add(Box.createHorizontalGlue());

        add(cadre);
    }


    @Override
    public void actionPerformed (ActionEvent actionEvent) {
        System.out.println("dsfsdf");
    }



}
