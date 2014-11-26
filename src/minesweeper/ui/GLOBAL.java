package minesweeper.ui;

import javax.swing.*;
import java.awt.*;

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
class GLOBAL {
    /*DEFAULT*/
    public static final int DEFAULT_MAXTHINK = 2000;
    public static final int DEFAULT_DELAY    = 100;
    public static final int NBCOL = 50;
    public static final int NBLIGNE = 40;
    public static final int CELL_SIZE = 16;
    public static final int NBMINES = 400;
    public static final String DEFAULT_AI = "minesweeper.ai.RandomAi";
    public static final String DEFAULT_DESIGN = "img";
    public static final boolean CONTINUE_AFTER = true;

    public static final int NB_TYPE_IMAGE = 15;

    public static void addItem(JPanel p, Component c, int x, int y, int width, int height, int align) {
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
