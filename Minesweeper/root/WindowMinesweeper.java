package root;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by MB on 10/29/2014.
 */
public class WindowMinesweeper extends JFrame implements ActionListener{


    int ROW = 20;
    int COL = 20;
    private final int WIDTH = (COL*GLOBAL.CELL_SIZE) ; //pour expert : 480
    private final int HEIGHT = (ROW * GLOBAL.CELL_SIZE); //poru expert :280

    JButton create;
    JButton importGrid;
    JLabel importLabel;
    JLabel label_choice_row;
    JLabel label_choice_col;
    JLabel label_mine;
    JLabel labelAi;
    JLabel labelTimer;

    JTextField choiceCol;
    JTextField choiceRow;
    JTextField choiceMines;
    JTextField choiceTimer;
    JTextField choiceMaxTime;
    JPanel panelCreation;
    JComboBox choixAI;
    JFileChooser chooser;
    List<Class<?>> classes;

    File savedGridToPlay =null;
    String emptyLabelName = "Aucun";

    public WindowMinesweeper(){

        create = new JButton("Create new game");
        create.addActionListener(this);


        label_choice_row = new JLabel("Nb row");
        label_choice_col = new JLabel("Nb col");
        label_mine       = new JLabel("Nb mines");

        Dimension dim_jtext = new Dimension(120,20);
        choiceRow = new JTextField("20");
        choiceRow.setPreferredSize(dim_jtext);
        choiceRow.setMinimumSize(dim_jtext);

        choiceCol = new JTextField("20");
        choiceCol.setPreferredSize(dim_jtext);
        choiceCol.setMinimumSize(dim_jtext);

        choiceMines       = new JTextField("80");
        choiceMines.setPreferredSize(dim_jtext);
        choiceMines.setMinimumSize(dim_jtext);

        choiceTimer = new JTextField(""+GLOBAL.DEFAULT_DELAY);
        choiceTimer.setPreferredSize(dim_jtext);
        choiceTimer.setMinimumSize(dim_jtext);
        labelTimer  = new JLabel("Time delay");

        choiceMaxTime = new JTextField(""+GLOBAL.DEFAULT_MAXTHINK);
        choiceMaxTime.setPreferredSize(dim_jtext);
        choiceMaxTime.setMinimumSize(dim_jtext);

        panelCreation = new JPanel(new GridBagLayout());
        panelCreation.setBackground(Color.orange);
        Dimension panel_creation_dim = new Dimension(280,240);
        panelCreation.setPreferredSize(panel_creation_dim);
        panelCreation.setMinimumSize(panel_creation_dim);
        panelCreation.setMaximumSize(panel_creation_dim);

        choixAI = new JComboBox();
        labelAi = new JLabel("Ai");
        classes = ClassFinder.find("root.ai");
        for(Class<?> c : classes){
            String name = c.getName();
            choixAI.addItem(name);
        }

        GLOBAL.addItem(panelCreation, label_choice_row, 0, 0, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceRow, 1, 0, 1, 1, GridBagConstraints.EAST);
        GLOBAL.addItem(panelCreation, label_choice_col, 0, 1, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceCol, 1, 1, 1, 1, GridBagConstraints.EAST);
        GLOBAL.addItem(panelCreation, label_mine, 0, 2, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceMines, 1, 2, 1, 1, GridBagConstraints.EAST);
        GLOBAL.addItem(panelCreation, create, 0, 3, 0, 0, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, labelAi, 0, 4, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choixAI, 1, 4, 1, 1, GridBagConstraints.EAST);

        GLOBAL.addItem(panelCreation, labelTimer, 0, 5, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceTimer, 1, 5, 1, 1, GridBagConstraints.EAST);

        GLOBAL.addItem(panelCreation, new JLabel("Think limite"), 0, 6, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceMaxTime, 1, 6, 1, 1, GridBagConstraints.EAST);


        importLabel = new JLabel(emptyLabelName);
        importGrid = new JButton("Import");
        importGrid.addActionListener(this);
        GLOBAL.addItem(panelCreation,importGrid, 1, 7, 1, 1, GridBagConstraints.EAST);


        GLOBAL.addItem(panelCreation,importLabel, 0, 7, 1, 1, GridBagConstraints.WEST);
        //GLOBAL.addItem(panelCreation, (chooser = new JFileChooser()), 1, 8, 1, 1, GridBagConstraints.EAST);


        add(panelCreation, BorderLayout.NORTH);
        add(create,BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }


    public static void main (String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                System.gc();
                new WindowMinesweeper().setVisible(true);
                System.gc();
            }
        });

    }

    public void createBoard(final int lignes,final int cols,final int mines,
                            final String aiName,final int timeDelay, final int thinkLimit){
        GLOBAL.NBCOL= cols;
        GLOBAL.NBLIGNE = lignes;
        System.gc();
        new Thread(
                (new Runnable() {
                    @Override
                    public void run() {
                        BoardGameView bv = new BoardGameView(lignes,cols,mines,aiName,timeDelay,thinkLimit);
                        bv.setVisible(true);
                        bv.setLocationRelativeTo(null);

                    }
                })
        ).start();
        System.gc();
    }
    public void loadGridToBoard(final String aiName, final int timeDelay, final int thinkLimit){
        System.out.println("Load grid");
        System.gc();
        new Thread(
                (new Runnable() {
                    @Override
                    public void run() {
                        BoardGameView bv = new BoardGameView(new Grid(savedGridToPlay),aiName,timeDelay,thinkLimit);
                        bv.setVisible(true);
                        bv.setLocationRelativeTo(null);
                        bv.message("Load file: "+savedGridToPlay.getName());
                    }
                })
        ).start();

        System.gc();

    }

    @Override
    public void actionPerformed (ActionEvent actionEvent) {

        if(actionEvent.getActionCommand() == "Create new game"){
            String text_row = choiceRow.getText();
            String text_col = choiceCol.getText();
            int row = Integer.parseInt(text_row);
            int col = Integer.parseInt(text_col);
            String text_mine = choiceMines.getText();
            int mines = Integer.parseInt(text_mine) ;
            String aiName = (String)choixAI.getSelectedItem();
            int time = Integer.parseInt(choiceTimer.getText());
            int thinkLimite = Integer.parseInt(choiceMaxTime.getText());

            createBoard(row, col, mines, aiName,time,thinkLimite);

        }else if(actionEvent.getActionCommand() =="Import") {
            chooser = new JFileChooser(".");
                /*To keep the last selected as default*/
                if(importLabel.getText() != emptyLabelName){
                    chooser.setSelectedFile(new File(importLabel.getText()));
                }
                chooser.showDialog(new JFrame("choose file"),"Ok");
                if(chooser.getSelectedFile() != null){
                    readFile(chooser.getSelectedFile());
                    String aiName = (String)choixAI.getSelectedItem();
                    int time = Integer.parseInt(choiceTimer.getText());
                    int thinkLimite = Integer.parseInt(choiceMaxTime.getText());
                    loadGridToBoard(aiName,time,thinkLimite);
                    importLabel.setText(chooser.getSelectedFile().getName());

                }else{
                    importLabel.setText(emptyLabelName);
                }


        }


    }

    public void readFile(File f){
        savedGridToPlay = f;
    }


}
