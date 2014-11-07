package root;

import javax.swing.*;
import java.awt.*;

/**
 * Created by MB on 10/29/2014.
 */
public class GLOBAL {


    /*DEFAULT*/
    public static int DEFAULT_MAXTHINK = 200;
    public static int DEFAULT_DELAY    = 100;
    public static int NBCOL = 20;
    public static int NBLIGNE = 20;
    public static final int CELL_SIZE = 10;
    public static final int NBMINES = 80;
    public static final String DEFAULT_AI = "root.ai.RandomAi";

    public static final int NB_TYPE_IMAGE = 16;

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
