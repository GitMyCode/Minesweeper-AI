package root;

import com.sun.java.util.jar.pack.*;
import root.ENUM.CASE;
import root.ENUM.COUP;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;

import static root.ENUM.CASE.*;
/**
 * Created by MB on 10/29/2014.
 */
public class GridView extends JPanel {


    int nbligne=0;
    int nbcol =0;
    int caseSize =GLOBAL.CELL_SIZE;

    String designFolder = GLOBAL.DEFAULT_DESIGN;
    Image[] cases;

    Grid grid;
    GridController controller;




    public GridView(int nbligne,int nbcol,int width, int height,int caseSize,String designFolder){
        this.nbcol = nbcol;
        this.nbligne = nbligne;
        this.caseSize = caseSize;
        this.cases = new Image[CASE.values().length];
        this.designFolder = designFolder;

        grid = new Grid(nbligne,nbcol,20);


        setLayout(new GridLayout(nbligne,nbcol));
        Dimension dim_grid = new Dimension(width ,height);
        setPreferredSize(dim_grid);
        setMaximumSize(dim_grid);
        setMinimumSize(dim_grid);


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


    /*
    * TODO
    * je penses que ce devrait etre gerer par une autre class mais bon
    * */
    private void initCasesImages(){
            try{

                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                final String scannedPath = "root/design/"+designFolder;
                Enumeration<URL> ressource = classLoader.getResources(scannedPath);
                final File folder  = new File(ressource.nextElement().getFile());
                File[] t = folder.listFiles();



                try {

                    Arrays.sort(t,new compImg());
                }catch (Exception e){
                    System.out.println(e);
                }
                int i=0;
                for (File cellImg : t){
                    if(i == CASE.values().length)
                        break;
                    

                    java.net.URL imageUrl =  cellImg.toURL();
                    cases[i] =  new ImageIcon(imageUrl).getImage();
                    BufferedImage im = ImageIO.read(cellImg);
                    BufferedImage b2 = getScaledInstance(im,caseSize,caseSize,true);
                    /*RescaleOp rescaleOp = new RescaleOp(0.88f, 20f, null);
                    rescaleOp.filter(b2, b2);*/
                    BufferedImage bi = new BufferedImage(cases[i].getWidth(null),cases[i].getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    Graphics g = bi.createGraphics();
                    g.drawImage(cases[i],0,0, caseSize,caseSize,null);
                    cases[i]=  new ImageIcon(b2).getImage();
                    i++;
                }

            }catch (Exception e){
                System.out.println(e);

            }
    }



    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        int casePlusSpace = caseSize ;
        for(int i=0; i< (nbcol*nbligne); i++){
            int x = (i/nbcol)  * casePlusSpace;
            int y = (i%nbcol)  * casePlusSpace;

            g.drawImage(cases[grid.gridPlayerView[i].indexValue],y,x,this);
        }

        g.dispose();
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

    public class compImg implements Comparator<File>{
        @Override
        public int compare (File o1, File o2) {
            int name1 = Integer.valueOf(o1.getName().split("\\.")[0]);
            int name2 = Integer.valueOf(o2.getName().split("\\.")[0]);
            if(name1 < name2){
            return -1;
            }
            if(name1 > name2){
                return 1;
            }

            return 0;
        }
    }

    /*
    * https://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
    * */
    public BufferedImage getScaledInstance(BufferedImage img,
                                           int targetWidth,
                                           int targetHeight,
                                           boolean higherQuality)
    {
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);


            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }


}
