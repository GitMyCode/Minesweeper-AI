package root;

import root.ENUM.COUP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static root.ENUM.CASE.*;
/**
 * Created by MB on 10/29/2014.
 */
public class GridView extends JPanel {


    int nbligne=0;
    int nbcol =0;
    int caseSize =16;

    Image[] cases;

    Grid grid;
    GridController controller;


    public GridView(BoardGameView.GameBuilder b){

    }

    public GridView(int nbligne,int nbcol,int width, int height,int caseSize){
        this.nbcol = nbcol;
        this.nbligne = nbligne;
        this.cases = new Image[GLOBAL.NB_TYPE_IMAGE];
        this.caseSize = caseSize;

        grid = new Grid(nbligne,nbcol,20);


        setLayout(new GridLayout(nbligne,nbcol));
        Dimension dim_grid = new Dimension(width ,height);
        setPreferredSize(dim_grid);
        setMaximumSize(dim_grid);
        setMinimumSize(dim_grid);


       /* for(int i=0; i< 13; i++){
            java.net.URL imageUrl = getClass().getResource("root.img/j"+i+".gif");
            cases[i] = new ImageIcon(imageUrl).getImage();
        }*/

        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        initCasesImages();

    }
    public void setGrid(Grid g) {
        grid = g;
        repaint();
    }
    public void setController(GridController gc){
        controller = gc;
    }


    private void initCasesImages(){
        for(int i=0; i< GLOBAL.NB_TYPE_IMAGE; i++){
            try{


                java.net.URL imageUrl = getClass().getResource("design/img3/"+i+".png");
                cases[i] =  new ImageIcon(imageUrl).getImage();
                BufferedImage bi = new BufferedImage(cases[i].getWidth(null),cases[i].getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bi.createGraphics();
                g.drawImage(cases[i],0,0, caseSize,caseSize,null);
                cases[i]=  new ImageIcon(bi).getImage();

            }catch (Exception e){

            }
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);


        for(int i=0; i< (nbcol*nbligne); i++){
            int x = (i/nbcol)  * caseSize;
            int y = (i%nbcol)  * caseSize;

            g.drawImage(cases[grid.gridPlayerView[i].indexValue],y,x,this);
        }
    }


    @Override
    protected void processMouseEvent(MouseEvent e) {
        if(e.getID() == MouseEvent.MOUSE_PRESSED){
            if( grid!=null){
                int l = e.getY() / caseSize;
                int c = e.getX() / caseSize;

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
