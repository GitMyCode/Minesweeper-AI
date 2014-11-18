package root;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class BoardGameView extends JFrame implements ActionListener, OutputObserver{
    private Rule yAxis;
    private Rule xAxis;
    private Box cadre;
    private JPanel containterField;

    /*Game statistic*/
    private JLabel flagRemaining;
    private JLabel winsTotal;
    private JLabel lostTotal;

    private JPanel menu;

    private JButton reset;
    private JButton start;
    private JButton step;
    private JButton stop;

    private JRadioButton rbInfinite;
    private JRadioButton rbStopAfterGame;
    private ButtonGroup buttonGroup;
    private JButton infinite;
    private JButton saveGridToFile;

    private GridView gv;
    private GridController gridController;
    private final Grid grid;
    private final ArtificialPlayer ai;

    private JTextArea messageTextArea ;

    /*Object for running game*/
    private GameRunner runner = null;
    private Runnable task = null;
    private Thread t = null;

    private boolean infiniteGame = GLOBAL.CONTINUE_AFTER;
    private final int nbMines = 0;
    private int delayTime = 100;
    private int nbLost = 0;
    private int nbWins = 0;
    private int thinkLimit = 1000;
    private int caseSize = 16;

    public static class GameBuilder {
        int nbLignes = GLOBAL.NBLIGNE;
        int nbCols = GLOBAL.NBCOL;
        int nbMines = GLOBAL.NBMINES;
        int delayTime = GLOBAL.DEFAULT_DELAY;
        int thinkLimit = GLOBAL.DEFAULT_MAXTHINK;
        int caseSize = GLOBAL.CELL_SIZE;
        Grid grid = null;
        ArtificialPlayer ai;
        String aiString;
        String designFolder = GLOBAL.DEFAULT_DESIGN;

        public GameBuilder(){ }

        public BoardGameView build() {
            grid = (grid == null)? new Grid(nbLignes, nbCols,nbMines): grid;
            return new BoardGameView(this);
        }

        public GameBuilder loadGrid(File f){
            this.grid = new Grid(f);
            this.nbCols = grid.nbCols;
            this.nbLignes = grid.nbLignes;
            this.nbMines = grid.nbMines;
            return this;
        }

        public GameBuilder row (int nbLignes) {
            this.nbLignes = nbLignes;
            return this;
        }

        public GameBuilder design (String s){
            this.designFolder = s;
            return this;
        }

        public GameBuilder col (int nbCols) {
            this.nbCols = nbCols;
            return this;
        }

        public GameBuilder mines (int nbMines) {
            this.nbMines = nbMines;
            return this;
        }

        public GameBuilder delay (int delayTime) {
            this.delayTime = delayTime;
            return this;
        }

        public GameBuilder think (int thinkLimit) {
            this.thinkLimit = thinkLimit;
            return this;
        }

        public GameBuilder caseSize (int caseSize) {
            this.caseSize = caseSize;
            return this;
        }
        public GameBuilder aiName(String aiName){
            this.aiString = aiName;
            this.ai = getAI(aiName);
            return this;
        }

// --Commented out by Inspection START (2014-11-13 12:16):
//        public GameBuilder grid (Grid grid) {
//            this.grid = grid;
//            return this;
//        }
// --Commented out by Inspection STOP (2014-11-13 12:16)

        /*Prend le nom du fichier et va chercher la class puis cree une instance*/
        private ArtificialPlayer getAI(String name) {
            ArtificialPlayer returnAi = null;
            try {
                Class c = Class.forName(name);
                Constructor<?> constructor = c.getConstructor();
                returnAi = (ArtificialPlayer) constructor.newInstance();

            } catch (Exception e) {
                System.out.println(e);
            }
            return returnAi;
        }

    }

    private BoardGameView(GameBuilder b){
        this.grid = b.grid;
        this.caseSize = b.caseSize;
        this.ai = b.ai;
        this.delayTime = b.delayTime;
        this.thinkLimit = b.thinkLimit;
        setTitle(ai.getName());

        constructUi(b.nbLignes, b.nbCols, b.caseSize, b.designFolder);
        pack();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing (WindowEvent windowEvent) {
            super.windowClosing(windowEvent);
            closingCleanUp();
            }
        });
        linkMVC();
    }

    @Override
    public void actionPerformed (ActionEvent actionEvent) {

        if(actionEvent.getActionCommand() == "Demarrer") {
            startGame();
        } else if(actionEvent.getActionCommand() == "Arreter") {
            if(runner!= null) {
                runner.terminate();
                runner =null;
            }
        } else if(actionEvent.getActionCommand() == "Reinitialiser") {
            resetGame();
        } else if(actionEvent.getActionCommand() == "Parties infinies") {
            infiniteGame = true;
            startGame();
        } else if(actionEvent.getActionCommand() == "Prochain pas") {
            ((JButton)actionEvent.getSource()).setEnabled(false);
            if(runner == null){
                startGame();
                runner.terminate();
            }
        }
    }

    private synchronized void startGame(){
        try {
            if(runner == null) {
                runner = new GameRunner(ai, grid, gridController, delayTime, thinkLimit);
                runner.setOutputObserver(this);
                task = new Runnable(){
                    @Override
                    public void run(){
                    try {
                        System.gc();
                        runner.run();
                        System.gc();
                    } catch(Exception e) {
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


    void resetGame() {
        grid.resetGrid();

        if(t != null) {
            t.interrupt();
        }
        if(runner != null)runner.terminate();
        runner = null;
        gv.repaint();
        flagRemaining.setText(String.valueOf(nbMines));
        step.setEnabled(true);
        message("Reinitialiser");
    }


    /* De connect5 auteur Éric beaudry */
    public synchronized void message(final String msg) {
        messageTextArea.append(msg + "\n");
        messageTextArea.setCaretPosition(messageTextArea.getDocument().getLength());
    }

    @Override
    public synchronized void callback () {
        if(infiniteGame && (runner != null && runner.isRunning())) {
            resetGame();
            startGame();
        } else {
            runner = null;
        }

        /*Remettre disponible le step button au cas ou il avait ete desactive (pour prevenir  le spam)*/
        step.setEnabled(true);

    }

    @Override
    public void updateLost () {
        nbLost++;
        lostTotal.setText(String.valueOf(nbLost));
    }

    @Override
    public void updateWins () {
        nbWins++;
        winsTotal.setText(String.valueOf(nbWins));
    }

    void updateGameLoopChoice(){
        if(rbInfinite.isSelected()) {
            infiniteGame = true;
        } else {
            infiniteGame = false;
        }
    }

    private void closingCleanUp(){
        System.out.println("Fermeture");
        if(t != null) {
            t.interrupt();
        }

        if(task != null) {
            task = null;
        }

        if(runner != null) {
            runner.terminate();
            runner = null;
        }
    }

    void saveGrid(){
        try {
            Format formatter = new SimpleDateFormat("MM-dd_hh-mm-ss");
            String fileName = "grid-" + (formatter.format(new Date()));
            grid.saveToFile(fileName);
            message("Grille enregistree : " + fileName);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void constructUi(int nbLignes, int nbCols, int caseSize, String designFolder){
        createGridView(nbLignes, nbCols, caseSize, designFolder);
        flagRemaining = new JLabel("" + nbMines);

        menu = new JPanel(new GridBagLayout());

        reset = new JButton("Reinitialiser");
        start = new JButton("Demarrer");
        infinite = new JButton("Parties infinies");
        step = new JButton("Prochain pas");
        stop = new JButton("Arreter");
        stop.addActionListener(this);
        reset.addActionListener(this);
        start.addActionListener(this);
        infinite.addActionListener(this);
        step.addActionListener(this);

        rbInfinite = new JRadioButton("Continuer apres la partie");
        rbStopAfterGame = new JRadioButton("Arreter apres la partie");
        buttonGroup = new ButtonGroup();
        buttonGroup.add(rbInfinite); buttonGroup.add(rbStopAfterGame);
        rbInfinite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateGameLoopChoice();
            }
        });

        rbStopAfterGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateGameLoopChoice();
            }
        });

        rbInfinite.setSelected(GLOBAL.CONTINUE_AFTER);

        saveGridToFile = new JButton("Enregistrer la grille");
        saveGridToFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e) {
                saveGrid();
            }
        });

        JPanel topMenu = new JPanel(new GridBagLayout());
        GLOBAL.addItem(topMenu, reset, 0, 0, 1, 1, GridBagConstraints.CENTER);
        GLOBAL.addItem(topMenu, start, 0, 1, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(topMenu, step, 1, 1, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(topMenu, stop, 2, 1, 1, 1, GridBagConstraints.WEST);

        GLOBAL.addItem(menu, topMenu, 0, 0, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(menu, rbInfinite, 0, 1, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(menu, rbStopAfterGame, 0, 2, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(menu, saveGridToFile, 0, 3, 1, 1, GridBagConstraints.WEST);

        add(menu, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        JPanel bottomPanelScore = new JPanel(new GridBagLayout());
        JScrollPane pane = new JScrollPane();
        messageTextArea = new JTextArea();
        messageTextArea.setColumns(30);
        messageTextArea.setEditable(false);
        messageTextArea.setRows(5);
        pane.setViewportView(messageTextArea);

        GLOBAL.addItem(bottomPanelScore, new JLabel("Nb drapeaux: "), 0, 0, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(bottomPanelScore, flagRemaining, 1, 0, 1, 1, GridBagConstraints.EAST);

        GLOBAL.addItem(bottomPanelScore, new JLabel("Nb defaites: "), 0, 1, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(bottomPanelScore, lostTotal = new JLabel("0"), 1, 1, 1, 1, GridBagConstraints.EAST);

        GLOBAL.addItem(bottomPanelScore, new JLabel("Nb victoires: "), 0, 2, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(bottomPanelScore, winsTotal = new JLabel("0"), 1, 2, 1, 1, GridBagConstraints.EAST);

        GLOBAL.addItem(bottomPanel, bottomPanelScore, 0, 0, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(bottomPanel, pane, 1, 0, 1, 1, GridBagConstraints.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        message("Initialiser l'IA: " + ai.getName());
    }

     void createGridView(int row, int col, int caseSize, String designFolder){
        cadre = new Box(BoxLayout.Y_AXIS);
        cadre.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        cadre.add(Box.createVerticalGlue());
        cadre.add(Box.createHorizontalGlue());

        int width = (col * (caseSize)) ; //pour expert : 480
        int height = (row * (caseSize)); //pour expert :280

        yAxis = new Rule(1, col);
        Dimension dim_y = new Dimension(width+20,7);
        yAxis.setPreferredSize(dim_y);
        yAxis.setMinimumSize(dim_y);
        yAxis.setMaximumSize(dim_y);
        yAxis.setLayout(new GridLayout(1, col));

        xAxis = new Rule(0,row);
        Dimension dim_x = new Dimension(10,height);
        xAxis.setPreferredSize(dim_x);
        xAxis.setMinimumSize(dim_x);
        xAxis.setMaximumSize(dim_x);
        xAxis.setLayout(new GridLayout(row, 1));



        containterField = new JPanel(new GridBagLayout());
        Dimension dim_container = new Dimension(width+40,height+30);
        containterField.setMaximumSize(dim_container);
        containterField.setMinimumSize(dim_container);
        containterField.setPreferredSize(dim_container);
        //containterField.setBackground(Color.red);


        GLOBAL.addItem(containterField, yAxis, 1, 0, 0, 7, GridBagConstraints.NORTHWEST);

        GLOBAL.addItem(containterField, xAxis, 0, 1, 0, 7, GridBagConstraints.WEST);


        gv = new GridView(row,col,width,height,caseSize,designFolder);
        gv.setBackground(new Color(0x33383D));

        GLOBAL.addItem(containterField, gv, 1, 2, 0, 0, GridBagConstraints.CENTER);


        Dimension dim_cadre = new Dimension(width+30,height+30);
        cadre.setPreferredSize(dim_cadre);
        cadre.setMaximumSize(dim_cadre);
        cadre.setMaximumSize(dim_cadre);


        cadre.add(containterField);
        cadre.add(Box.createVerticalGlue());
        cadre.add(Box.createHorizontalGlue());

        add(cadre,BorderLayout.CENTER);
    }
    private void linkMVC(){
        gv.setGrid(grid);
        gridController = new GridControllerImpl(grid,gv,flagRemaining);
        gv.setController(gridController);
        gv.repaint();
    }





}
