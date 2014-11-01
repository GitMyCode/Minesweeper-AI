package root;

import javax.swing.*;
import java.awt.*;
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
    JLabel label_choice_row;
    JLabel label_choice_col;
    JLabel label_mine;
    JLabel labelAi;
    JLabel labelTimer;

    JTextField choiceCol;
    JTextField choiceRow;
    JTextField choiceMines;
    JTextField choiceTimer;
    JPanel panelCreation;
    JComboBox choixAI;
    List<Class<?>> classes;


    public WindowMinesweeper(){

        create = new JButton("Create");
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

        choiceTimer = new JTextField("100");
        choiceTimer.setPreferredSize(dim_jtext);
        choiceTimer.setMinimumSize(dim_jtext);
        labelTimer  = new JLabel("Time delay");

        panelCreation = new JPanel(new GridBagLayout());
        panelCreation.setBackground(Color.orange);
        Dimension panel_creation_dim = new Dimension(200,200);
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


        add(panelCreation,BorderLayout.NORTH);
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
                            final String aiName,final int timeDelay){
        GLOBAL.NBCOL= cols;
        GLOBAL.NBLIGNE = lignes;


        System.gc();
        new Thread(
                (new Runnable() {
                    @Override
                    public void run() {
                        new BoardGameView(lignes,cols,mines,aiName,timeDelay).setVisible(true);

                    }
                })

        ).start();

        System.gc();


    }

    @Override
    public void actionPerformed (ActionEvent actionEvent) {

        if(actionEvent.getActionCommand() == "Create"){
            String text_row = choiceRow.getText();
            String text_col = choiceCol.getText();
            int row = Integer.parseInt(text_row);
            int col = Integer.parseInt(text_col);
            String text_mine = choiceMines.getText();
            int mines = Integer.parseInt(text_mine) ;
            String aiName = (String)choixAI.getSelectedItem();
            int time = Integer.parseInt(choiceTimer.getText());


            createBoard(row, col, mines, aiName,time);
        }


    }
}
