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
class Rule extends JPanel {
    private JLabel[] indicateurs;
        public Rule(int xOry, int length) {
            super();
            indicateurs = null;
            if (xOry == 1) {
                JLabel placeholder = new JLabel();
                placeholder.setText("y-x");
                placeholder.setForeground(Color.BLACK);
                placeholder.setFont(new Font("Arial", Font.BOLD, 9));
                add(placeholder);
            }
            indicateurs = new JLabel[length];
            for (int i = 0; i < length; i++) {
                JLabel num = new JLabel();
                num.setText(Integer.toString(i));
                num.setForeground(Color.BLUE);
                num.setFont(new Font("Serif", Font.PLAIN, 10));
                indicateurs[i] = num;
                add(num);
            }
        }

}
