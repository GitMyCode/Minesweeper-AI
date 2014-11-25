package minesweeper;

import java.util.HashMap;
import java.util.Map;

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
public enum Case {
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

    public static final int MAX_MINES = 8;

    Case(int i) {
        indexValue = i;
    }

    private static final Map<Integer, Case> INT_TO_TYPE_MAP = new HashMap<Integer, Case>();
    static {
        for (Case type : Case.values()) {
            INT_TO_TYPE_MAP.put(type.indexValue, type);
        }
    }
    public static boolean isIndicatorCase(Case c) {
        return (c.indexValue >= 1 && c.indexValue <= MAX_MINES);
    }
    public static Case caseFromInt(int i) {
        return INT_TO_TYPE_MAP.get(i);
    }


    public final int indexValue;
    // --Commented out by Inspection (2014-11-13 12:17):final String  pathfile= "../minesweeper.img/j";

// --Commented out by Inspection START (2014-11-13 12:17):
//    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
//        BufferedImage dbi = null;
//        if (sbi != null) {
//            dbi = new BufferedImage(dWidth, dHeight, imageType);
//            Graphics2D g = dbi.createGraphics();
//            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
//            g.drawRenderedImage(sbi, at);
//        }
//        return dbi;
//    }
// --Commented out by Inspection STOP (2014-11-13 12:17)
}
