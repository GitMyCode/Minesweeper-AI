package minesweeper.ui;

import minesweeper.utils.ClassFinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

/**
 * Projet de joueur artificiel de Minesweeper avec différents algorithmes
 * Dans le cadre du 3e TP en Intelligence Artificielle (INF4230)
 * <p/>
 * Automne 2014
 * Par l'équipe:
 * Martin Bouchard
 * Frédéric Vachon
 * Louis-Bertrand Varin
 * Geneviève Lalonde
 * Nilovna Bascunan-Vasquez
 */
public class WindowMinesweeper extends JFrame {

    private int nbMines = GLOBAL.NBMINES;
    private int nbLines = GLOBAL.NBLIGNE;
    private int nbColumns = GLOBAL.NBCOL;
    private String selectedAi = GLOBAL.DEFAULT_AI;
    private String selectedDesign = GLOBAL.DEFAULT_DESIGN;
    private int timeDelay = GLOBAL.DEFAULT_DELAY;
    private int limiteReflexion = GLOBAL.DEFAULT_MAXTHINK;
    private int caseSize = GLOBAL.CELL_SIZE;

    private File savedGridToPlay = null;

    private JLabel nbLinesLabel;
    private JLabel nbColumnsLabel;
    private JLabel percentMinesLabel;
    private JLabel aiLabel;
    private JLabel delaiLabel;
    private JLabel designLabel;
    private JLabel sizeCasesLabel;
    private JLabel limiteReflexionLabel;
    private JLabel nbMinesLabel;
    private JSpinner nbColumnsSpinner;
    private JSpinner nbLinesSpinner;
    private JSpinner percentMinesSpinner;
    private JSpinner sizeCasesSpinner;
    private JSpinner limiteReflexionSpinner;
    private JSpinner delaiSpinner;
    private JComboBox aiComboBox;
    private JComboBox designComboBox;
    private JButton newGameBtn;
    private JButton importGameBtn;
    private JPanel rootPanel;
    private JPanel grillePanel;
    private JPanel joueurArtificielPanel;
    private JPanel designPanel;
    private JFileChooser importGameFileChooser;

    private List<Class<?>> aiList;
    private List<String> designsList;

    public static void main(String[] args) {
        // Création de la fenêtre principale
        WindowMinesweeper f = new WindowMinesweeper();
    }

    public WindowMinesweeper() {
        $$$setupUI$$$();
        setContentPane(rootPanel);
        setTitle("Démineur");

        // UI Elements action listener
        importGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importGameFileAction();
            }
        });

        newGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGrilleSettings();
                createBoard();
            }
        });

        percentMinesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateMines();
            }
        });

        nbLinesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateMines();
            }
        });

        nbColumnsSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateMines();
            }
        });

        nbMinesLabel.setText("Nombre de mines: " + nbMines);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Create Custom UI Elements
    private void createUIComponents() {
        nbLinesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        nbLinesSpinner.setValue(new Integer(nbLines));
        nbLinesSpinner.setMinimumSize(new Dimension(60, 20));
        JSpinner.NumberEditor nbLinesEditor = new JSpinner.NumberEditor(nbLinesSpinner, "#");
        nbLinesSpinner.setEditor(nbLinesEditor);

        nbColumnsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        nbColumnsSpinner.setValue(new Integer(nbColumns));
        nbColumnsSpinner.setMinimumSize(new Dimension(60, 20));
        JSpinner.NumberEditor nbColEditor = new JSpinner.NumberEditor(nbColumnsSpinner, "#");
        nbColumnsSpinner.setEditor(nbColEditor);

        percentMinesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 35, 1));
        percentMinesSpinner.setValue(new Integer(15));
        percentMinesSpinner.setMinimumSize(new Dimension(60, 20));
        percentMinesSpinner.setToolTipText("Maximum de 35%");
        JSpinner.NumberEditor percentMinesEditor = new JSpinner.NumberEditor(percentMinesSpinner, "#");
        percentMinesSpinner.setEditor(percentMinesEditor);

        aiComboBox = new JComboBox<String>();
        aiList = ClassFinder.find("minesweeper.ai");
        for (Class<?> c : aiList) {
            String name = c.getSimpleName();
            aiComboBox.addItem(name);
        }

        designComboBox = new JComboBox<String>();
        designsList = ClassFinder.findFolder("minesweeper.ui.design");
        for (String d : designsList) {
            designComboBox.addItem(d);
        }

        delaiSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        delaiSpinner.setValue(new Integer(timeDelay));
        delaiSpinner.setMinimumSize(new Dimension(60, 20));
        JSpinner.NumberEditor delaiEditor = new JSpinner.NumberEditor(delaiSpinner, "#");
        delaiSpinner.setEditor(delaiEditor);

        limiteReflexionSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        limiteReflexionSpinner.setValue(new Integer(limiteReflexion));
        limiteReflexionSpinner.setMinimumSize(new Dimension(60, 20));
        JSpinner.NumberEditor limiteReflexionEditor = new JSpinner.NumberEditor(limiteReflexionSpinner, "#");
        limiteReflexionSpinner.setEditor(limiteReflexionEditor);

        sizeCasesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        sizeCasesSpinner.setValue(new Integer(GLOBAL.CELL_SIZE));
        sizeCasesSpinner.setMinimumSize(new Dimension(60, 20));
        JSpinner.NumberEditor sizeCasesEditor = new JSpinner.NumberEditor(sizeCasesSpinner, "#");
        sizeCasesSpinner.setEditor(sizeCasesEditor);
    }

    void createBoard() {
        System.gc();
        new Thread(
                (new Runnable() {
                    @Override
                    public void run() {
                        BoardGameView bv = new BoardGameView.GameBuilder().
                                row(nbLines).
                                col(nbColumns).
                                mines(nbMines).
                                delay(timeDelay).
                                think(limiteReflexion).
                                aiName("minesweeper.ai." + selectedAi).
                                caseSize(caseSize).
                                design(selectedDesign).
                                build();
                        bv.setVisible(true);
                        bv.setLocationRelativeTo(null);
                    }
                })
        ).start();
        System.gc();
    }

    void loadGridToBoard() {
        System.out.println("Charger une grille");
        System.gc();
        Thread t = new Thread(
                (new Runnable() {
                    @Override
                    public void run() {
                        BoardGameView bv = new BoardGameView.GameBuilder().
                                loadGrid(savedGridToPlay).
                                aiName("minesweeper.ai." + selectedAi).
                                caseSize(caseSize).
                                delay(timeDelay).
                                think(limiteReflexion).
                                design(selectedDesign).
                                build();
                        bv.setVisible(true);
                        bv.setLocationRelativeTo(null);
                        bv.message("Chargement du fichier: " + savedGridToPlay.getName());
                    }
                })
        );
        t.start();

        System.gc();
    }

    private void importGameFileAction() {
        importGameFileChooser = new JFileChooser(".");
        if (importGameFileChooser.showDialog(new JFrame(), "Sélectionner partie à importer") == JFileChooser.APPROVE_OPTION) {
            if (importGameFileChooser.getSelectedFile() != null) {
                readFile(importGameFileChooser.getSelectedFile());
                updateGrilleSettings();
                loadGridToBoard();
            }
        }

    }

    void readFile(File f) {
        savedGridToPlay = f;
    }

    void updateGrilleSettings() {
        updateMines();
        // Grille settings
        nbLines = (Integer) nbLinesSpinner.getValue();
        nbColumns = (Integer) nbColumnsSpinner.getValue();
        // Joueur artificiel settings
        selectedAi = (String) aiComboBox.getSelectedItem();
        timeDelay = (Integer) delaiSpinner.getValue();
        limiteReflexion = (Integer) limiteReflexionSpinner.getValue();
        // Design settings
        selectedDesign = (String) designComboBox.getSelectedItem();
        caseSize = (Integer) sizeCasesSpinner.getValue();
    }

    void updateMines() {
        Integer nbLinesText = (Integer) nbLinesSpinner.getValue();
        Integer nbColumnsText = (Integer) nbColumnsSpinner.getValue();
        float minePercentage = (Integer) percentMinesSpinner.getValue();

        nbMines = (int) ((nbLinesText * nbColumnsText) * (minePercentage / 100));
        nbMinesLabel.setText("Nombre de mines: " + nbMines);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 2, new Insets(15, 15, 15, 15), -1, -1));
        rootPanel.setInheritsPopupMenu(false);
        grillePanel = new JPanel();
        grillePanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(10, 20, 10, 20), -1, -1));
        rootPanel.add(grillePanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        grillePanel.setBorder(BorderFactory.createTitledBorder("Grille"));
        nbColumnsLabel = new JLabel();
        nbColumnsLabel.setText("Nombre de colonnes");
        grillePanel.add(nbColumnsLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(133, 16), null, 0, false));
        percentMinesLabel = new JLabel();
        percentMinesLabel.setText("Pourcentage de mines (Max: 35%)");
        grillePanel.add(percentMinesLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nbLinesLabel = new JLabel();
        nbLinesLabel.setAlignmentY(0.5f);
        nbLinesLabel.setText("Nombre de lignes");
        grillePanel.add(nbLinesLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(113, 22), null, 0, false));
        percentMinesSpinner.setDoubleBuffered(false);
        grillePanel.add(percentMinesSpinner, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 16), null, 0, false));
        grillePanel.add(nbLinesSpinner, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 16), null, 0, false));
        grillePanel.add(nbColumnsSpinner, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 16), null, 0, false));
        nbMinesLabel = new JLabel();
        nbMinesLabel.setFont(new Font(nbMinesLabel.getFont().getName(), Font.PLAIN, 12));
        nbMinesLabel.setHorizontalAlignment(4);
        nbMinesLabel.setText("Nombre de mines: ");
        grillePanel.add(nbMinesLabel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
        designPanel = new JPanel();
        designPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(10, 20, 10, 20), -1, -1));
        rootPanel.add(designPanel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        designPanel.setBorder(BorderFactory.createTitledBorder("Design"));
        designLabel = new JLabel();
        designLabel.setText("Design");
        designPanel.add(designLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        designPanel.add(designComboBox, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sizeCasesLabel = new JLabel();
        sizeCasesLabel.setText("Taille des cases");
        designPanel.add(sizeCasesLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        designPanel.add(sizeCasesSpinner, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 16), null, 0, false));
        joueurArtificielPanel = new JPanel();
        joueurArtificielPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(10, 20, 10, 20), -1, -1));
        rootPanel.add(joueurArtificielPanel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        joueurArtificielPanel.setBorder(BorderFactory.createTitledBorder("Joueur artificiel"));
        joueurArtificielPanel.add(aiComboBox, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aiLabel = new JLabel();
        aiLabel.setText("AI");
        joueurArtificielPanel.add(aiLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        delaiLabel = new JLabel();
        delaiLabel.setText("Délai (ms)");
        joueurArtificielPanel.add(delaiLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        limiteReflexionLabel = new JLabel();
        limiteReflexionLabel.setText("Limite de réflexion (ms)");
        joueurArtificielPanel.add(limiteReflexionLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        joueurArtificielPanel.add(delaiSpinner, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 16), null, 0, false));
        joueurArtificielPanel.add(limiteReflexionSpinner, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 16), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        rootPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(-1, 15), null, 0, false));
        newGameBtn = new JButton();
        newGameBtn.setText("Créer une nouvelle partie");
        rootPanel.add(newGameBtn, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        importGameBtn = new JButton();
        importGameBtn.setText("Importer une partie...");
        rootPanel.add(importGameBtn, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        rootPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(-1, 10), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        rootPanel.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(-1, 10), null, 0, false));
        nbColumnsLabel.setLabelFor(nbColumnsSpinner);
        percentMinesLabel.setLabelFor(percentMinesSpinner);
        nbLinesLabel.setLabelFor(nbLinesSpinner);
        designLabel.setLabelFor(designComboBox);
        sizeCasesLabel.setLabelFor(sizeCasesSpinner);
        aiLabel.setLabelFor(aiComboBox);
        delaiLabel.setLabelFor(delaiSpinner);
        limiteReflexionLabel.setLabelFor(limiteReflexionSpinner);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
