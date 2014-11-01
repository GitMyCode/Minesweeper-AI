package root.ENUM;

import root.GLOBAL;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MB on 10/30/2014.
 */
public enum CASE {

    EMPTY(0),
    ONE(1),
    TWO(2),
    TREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    MINE(9),
    UNDISCOVERED(10),
    FLAGED(11),
    ERROR_FLAG(12),
    DEFUSED(13),
    BLOW(14);



    CASE(int i){

        try{

            indexValue = i;
            java.net.URL imageUrl = getClass().getResource("../img3/"+i+".png");
            image =  new ImageIcon(imageUrl).getImage();
            BufferedImage bi = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(image,0,0, GLOBAL.CELL_SIZE,GLOBAL.CELL_SIZE,null);
            image =  new ImageIcon(bi).getImage();
           /*
            File f = new File("../img2/c"+i+".png");
            BufferedImage rawImage = ImageIO.read(f);


            BufferedImage bufferedImage = new BufferedImage(15,15,rawImage.getType());
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(rawImage,0,0,15,15,null);
            g.dispose();
            int type = rawImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : rawImage.getType();

            image = scale(rawImage,type,15,15,0.5,0.5);*/

        }catch (Exception e){

        }


    }

    private static final Map<Integer, CASE> intToTypeMap = new HashMap<Integer, CASE>();
    static {
        for (CASE type : CASE.values()) {
            intToTypeMap.put(type.indexValue, type);
        }
    }
    public static boolean isIndicatorCase(CASE c){
        return (c.indexValue >= 1 && c.indexValue <=8);
    }
    public static CASE caseFromInt(int i ){
        return intToTypeMap.get(i);
    }




    public Image image;
    public int indexValue;
    final String  pathfile= "../root.img/j";

    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if(sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    }




}
