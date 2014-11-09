package root;

import javax.swing.*;
import java.awt.*;

/**
 * Created by MB on 10/29/2014.
 */
public class GLOBAL {


    /*DEFAULT*/
    public static int DEFAULT_MAXTHINK = 2000;
    public static int DEFAULT_DELAY    = 100;
    public static int NBCOL = 50;
    public static int NBLIGNE = 40;
    public static final int CELL_SIZE = 16;
    public static final int NBMINES = 400;
    public static final String DEFAULT_AI = "root.ai.RandomAi";
    public static final String DEFAULT_DESIGN = "img";
    public static final boolean CONTINUE_AFTER = true;

    public static final int NB_TYPE_IMAGE = 15;

    public static void addItem(JPanel p, JComponent c, int x, int y, int width, int height, int align) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = x;
        gc.gridy = y;
        gc.gridwidth = width;
        gc.gridheight = height;
        gc.weightx = 100.0;
        gc.weighty = 100.0;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = align;
        gc.fill = GridBagConstraints.NONE;
        p.add(c, gc);
    }


}
