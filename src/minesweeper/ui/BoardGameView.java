package minesweeper.ui;

import minesweeper.ArtificialPlayer;
import minesweeper.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Constructor;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class BoardGameView extends JFrame implements OutputObserver {
    /*Object for running game*/
    private GameRunner runner = null;
    private Runnable task = null;
    private Thread t = null;

    private boolean isInfiniteGame = GLOBAL.CONTINUE_AFTER;
    private int nbMines = 0;
    private int delayTime = 100;
    private int nbLost = 0;
    private int nbWins = 0;
    private int thinkLimit = 1000;
    private int caseSize = 16;
    private int nbCols = 0;
    private int nbLines = 0;
    private String designName = GLOBAL.DEFAULT_DESIGN;
    private final ArtificialPlayer ai;

    private GridView grilleGv;
    private GridController gridController;
    private final Grid grid;

    private Box grilleGameBox;
    private JButton startGameBtn;
    private JButton pauseGameBtn;
    private JButton resetGameBtn;
    private JButton nextStepGameBtn;
    private JButton saveGameBtn;
    private JPanel infoCurrentGamePanel;
    private JLabel aiNameLabel;
    private JLabel nbLignesLabel;
    private JLabel nbColsLabel;
    private JLabel nbMinesLabel;
    private JLabel nbMinesValueLabel;
    private JLabel nbLignesValueLabel;
    private JLabel nbColsValueLabel;
    private JLabel aiNameValueLabel;
    private JLabel nbFlagsLabel;
    private JLabel nbWinsLabel;
    private JLabel nbLossLabel;
    private JLabel nbLossValueLabel;
    private JLabel nbWinsValueLabel;
    private JLabel nbFlagsValueLabel;
    private JLabel continueGameLabel;
    private JPanel statistiquesPanel;
    private JPanel optionsGamePanel;
    private JPanel debugPanel;
    private JPanel rootPanel;
    private JPanel statsContentPanel;
    private JPanel optionsBtnPanel;
    private JPanel rightSidePanel;
    private JPanel leftSidePanel;
    private JRadioButton yesContinueGameRbtn;
    private JRadioButton noContinueGameRbtn;
    private JScrollPane debugScrollPane;
    private JTextArea debugTextArea;

    public static class GameBuilder {
        // Defaults if not set
        int nbLignes = GLOBAL.NBLIGNE;
        int nbCols = GLOBAL.NBCOL;
        int nbMines = GLOBAL.NBMINES;
        int delayTime = GLOBAL.DEFAULT_DELAY;
        int thinkLimit = GLOBAL.DEFAULT_MAXTHINK;
        int caseSize = GLOBAL.CELL_SIZE;

        Grid grid = null;
        ArtificialPlayer ai;
        String aiName;
        String designName = GLOBAL.DEFAULT_DESIGN;

        public GameBuilder () {

        }

        public BoardGameView build() {
            if (this.grid == null) {
                this.grid = new Grid(nbLignes, nbCols, nbMines);
            }
            return new BoardGameView(this);
        }

        public GameBuilder loadGrid(File f) {
            this.grid = new Grid();
            this.grid.loadFromFile(f);
            this.nbCols = grid.nbCols;
            this.nbLignes = grid.nbLignes;
            this.nbMines = grid.nbMines;
            return this;
        }

        public GameBuilder row(int nbLignes) {
            this.nbLignes = nbLignes;
            return this;
        }

        public GameBuilder design(String designName) {
            this.designName = designName;
            return this;
        }

        public GameBuilder col(int nbCols) {
            this.nbCols = nbCols;
            return this;
        }

        public GameBuilder mines(int nbMines) {
            this.nbMines = nbMines;
            return this;
        }

        public GameBuilder delay(int delayTime) {
            this.delayTime = delayTime;
            return this;
        }

        public GameBuilder think(int thinkLimit) {
            this.thinkLimit = thinkLimit;
            return this;
        }

        public GameBuilder caseSize(int caseSize) {
            this.caseSize = caseSize;
            return this;
        }

        public GameBuilder aiName(String aiName) {
            this.aiName = aiName;
            this.ai = getAI(aiName);
            return this;
        }

        // Prend le nom du ai et va chercher la class puis cree une instance
        private ArtificialPlayer getAI(String aiName) {
            ArtificialPlayer returnAi = null;
            try {
                Class c = Class.forName(aiName);
                Constructor<?> constructor = c.getConstructor();
                returnAi = (ArtificialPlayer) constructor.newInstance();
            } catch (Exception e) {
                System.out.println(e);
            }
            return returnAi;
        }
    }

    private BoardGameView (GameBuilder b) {
        this.grid = b.grid;
        this.caseSize = b.caseSize;
        this.nbCols = b.nbCols;
        this.nbMines = b.nbMines;
        this.nbLines = b.nbLignes;
        this.ai = b.ai;
        this.delayTime = b.delayTime;
        this.thinkLimit = b.thinkLimit;
        this.designName = b.designName;

        setContentPane(rootPanel);
        setTitle(this.ai.getName());

        // Valeurs par défaut pour certains éléments du UI
        int percentMines = (nbMines * 100) / (nbCols * nbLines);
        nbLignesValueLabel.setText(String.valueOf(nbLines));
        nbColsValueLabel.setText(String.valueOf(nbCols));
        nbMinesValueLabel.setText(String.valueOf(nbMines) + " (" + String.valueOf(percentMines) + " %)");
        aiNameValueLabel.setText(ai.getName());
        yesContinueGameRbtn.setSelected(isInfiniteGame);
        message("Initialiser l'IA: " + this.ai.getName());

        // Action listeners
        startGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        pauseGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (runner != null) {
                    runner.terminate();
                    runner = null;
                }
            }
        });

        resetGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        nextStepGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JButton) e.getSource()).setEnabled(false);
                if (runner == null) {
                    startGame();
                    runner.terminate();
                }
            }
        });

        saveGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGrid();
            }
        });

        yesContinueGameRbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isInfiniteGame = true;
            }
        });

        noContinueGameRbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isInfiniteGame = false;
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                closingCleanUp();
            }
        });

        pack();
        setVisible(true);
    }

    private void createUIComponents() {
        int width = (nbCols * (caseSize)); //pour expert : 480
        int height = (nbLines * (caseSize)); //pour expert :280

        nbFlagsValueLabel = new JLabel();

        // Contruire la grille du jeu
        grilleGameBox = new Box(BoxLayout.Y_AXIS);
        grilleGameBox.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        // Setter la GridView  (grill)
        grilleGv = new GridView(nbLines, nbCols, width, height, caseSize, designName);
        grilleGv.setBackground(new Color(0x33383D));
        grilleGv.setGrid(grid);
        gridController = new GridControllerImpl(grid, grilleGv, nbFlagsValueLabel);
        grilleGv.setController(gridController);
        grilleGv.repaint();

        leftSidePanel = new JPanel(new GridBagLayout());
        leftSidePanel.add(grilleGv);
    }

    private synchronized void startGame () {
        try {
            if (runner == null) {
                runner = new GameRunner(ai, grid, gridController, delayTime, thinkLimit);
                runner.setOutputObserver(this);
                task = new Runnable() {
                    @Override
                    public void run () {
                        try {
                            System.gc();
                            runner.run();
                            System.gc();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                };

                t = new Thread(task);
                t.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    void resetGame () {
        grid.resetGrid();

        if (t != null) {
            t.interrupt();
        }
        if (runner != null) runner.terminate();
        runner = null;
        grilleGv.repaint();
        nbFlagsValueLabel.setText(String.valueOf(nbMines));
        nextStepGameBtn.setEnabled(true);
        message("Grille réinitialisée.");
    }


    /* De connect5 auteur Éric beaudry */
    public synchronized void message (final String msg) {
        debugTextArea.append(msg + "\n");
        debugTextArea.setCaretPosition(debugTextArea.getDocument().getLength());
    }

    @Override
    public synchronized void callback () {
        if (isInfiniteGame && (runner != null && runner.isRunning())) {
            resetGame();
            startGame();
        } else {
            runner = null;
        }

        /*Remettre disponible le step button au cas ou il avait ete desactive (pour prevenir  le spam)*/
        nextStepGameBtn.setEnabled(true);
    }

    @Override
    public void updateLost() {
        nbLost++;
        nbLossValueLabel.setText(String.valueOf(nbLost));
    }

    @Override
    public void updateWins() {
        nbWins++;
        nbWinsValueLabel.setText(String.valueOf(nbWins));
    }

    private void closingCleanUp() {
        if (t != null) {
            t.interrupt();
        }

        if (task != null) {
            task = null;
        }

        if (runner != null) {
            runner.terminate();
            runner = null;
        }
    }

    void saveGrid () {
        try {
            Format formatter = new SimpleDateFormat("MM-dd_hh-mm-ss");
            String fileName = "grid-" + (formatter.format(new Date()));
            grid.saveToFile(fileName);
            message("Grille enregistree : " + fileName);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
