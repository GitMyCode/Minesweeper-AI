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
 *
 * Automne 2014
 * Par l'équipe:
 *   Martin Bouchard
 *   Frédéric Vachon
 *   Louis-Bertrand Varin
 *   Geneviève Lalonde
 *   Nilovna Bascunan-Vasquez
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

    public static void main (String[] args) {
        // Création de la fenêtre principale
        WindowMinesweeper f = new WindowMinesweeper();
    }

    public WindowMinesweeper() {
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
        for(Class<?> c : aiList){
            String name = c.getSimpleName();
            aiComboBox.addItem(name);
        }

        designComboBox = new JComboBox<String>();
        designsList = ClassFinder.findFolder("minesweeper.ui.design");
        for(String d : designsList){
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

    void createBoard(){
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

    void loadGridToBoard(){
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
                        bv.message("Chargement du fichier: "+savedGridToPlay.getName());
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

    void readFile(File f){
        savedGridToPlay = f;
    }

    void updateGrilleSettings(){
        updateMines();
        // Grille settings
        nbLines = (Integer)nbLinesSpinner.getValue();
        nbColumns = (Integer)nbColumnsSpinner.getValue();
        // Joueur artificiel settings
        selectedAi = (String)aiComboBox.getSelectedItem();
        timeDelay = (Integer)delaiSpinner.getValue();
        limiteReflexion = (Integer)limiteReflexionSpinner.getValue();
        // Design settings
        selectedDesign = (String)designComboBox.getSelectedItem();
        caseSize = (Integer)sizeCasesSpinner.getValue();
    }

    void updateMines(){
        Integer nbLinesText = (Integer)nbLinesSpinner.getValue();
        Integer nbColumnsText = (Integer)nbColumnsSpinner.getValue();
        float minePercentage = (Integer)percentMinesSpinner.getValue();

        nbMines = (int)((nbLinesText * nbColumnsText) * (minePercentage / 100));
        nbMinesLabel.setText("Nombre de mines: " + nbMines);
    }
}
