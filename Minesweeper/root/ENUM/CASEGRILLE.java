package root.ENUM;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MB on 10/30/2014.
 */
public enum CASEGRILLE {

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



    CASEGRILLE(int i){
        indexValue = i;
    }

    private static final Map<Integer, CASEGRILLE> intToTypeMap = new HashMap<Integer, CASEGRILLE>();
    static {
        for (CASEGRILLE type : CASEGRILLE.values()) {
            intToTypeMap.put(type.indexValue, type);
        }
    }
    public static boolean isIndicatorCase(CASEGRILLE c){
        return (c.indexValue >= 1 && c.indexValue <=8);
    }
    public static CASEGRILLE caseFromInt(int i ){
        return intToTypeMap.get(i);
    }


    public int indexValue;
    final String  pathfile= "../root.img/j";

    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    }
}
