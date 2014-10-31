package root;

import root.ENUM.COUP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import static root.ENUM.CASE.*;
/**
 * Created by MB on 10/29/2014.
 */
public class GridView extends JPanel {


    int nbligne=0;
    int nbcol =0;


    Image[] cases;

    Grid grid;
    GridController controller;


    public GridView(int nbligne,int nbcol,int width, int height){
        this.nbcol = nbcol;
        this.nbligne = nbligne;
        this.cases = new Image[nbcol*nbligne];

        grid = new Grid(nbligne,nbcol,20);


        setLayout(new GridLayout(nbligne,nbcol));
        Dimension dim_grid = new Dimension(width ,height);
        setPreferredSize(dim_grid);
        setMaximumSize(dim_grid);
        setMinimumSize(dim_grid);


        Arrays.fill(grid.gridSpace, (byte) UNDISCOVERED.indexValue);
       /* for(int i=0; i< 13; i++){
            java.net.URL imageUrl = getClass().getResource("root.img/j"+i+".gif");
            cases[i] = new ImageIcon(imageUrl).getImage();
        }*/

        enableEvents(AWTEvent.MOUSE_EVENT_MASK);

    }
    public void setGrid(Grid g) {
        grid = g;
        repaint();
    }
    public void setController(GridController gc){
        controller = gc;
    }




    protected void paintComponent(Graphics g){
        super.paintComponent(g);


        for(int i=0; i< (nbcol*nbligne); i++){
            int x = (i/nbcol)  * GLOBAL.CELL_SIZE;
            int y = (i%nbcol)  * GLOBAL.CELL_SIZE;

            g.drawImage(grid.gridPlayerView[i].image,y,x,this);
            //g.drawImage(cases[grid.gridSpace[i]], y, x, this);
        }
    }


    @Override
    protected void processMouseEvent(MouseEvent e) {
        if(e.getID() == MouseEvent.MOUSE_PRESSED){
            if( grid!=null){
                int l = e.getY() / GLOBAL.CELL_SIZE;
                int c = e.getX() / GLOBAL.CELL_SIZE;

                if(l<nbligne && c< nbcol){
                    int index = l*nbcol+c;
                    if(e.getButton() == MouseEvent.BUTTON3){
                        if(grid.gridPlayerView[index]==FLAGED){
                            controller.movePlay(new Move(index,COUP.UNFLAG));
                        }else {
                            controller.movePlay(new Move(index,COUP.FLAG));
                        }
                    }else{
                        controller.movePlay(new Move(index,COUP.SHOW));
                    }

                    if(grid.gameFinish()){
                    if(grid.lost){
                        //outputObserver.message("Lost!");
                        grid.showAllCase();
                    }else if(grid.win){
                        //outputObserver.message("Win!");
                    }
                }


                }



            }
        }
    }


}
