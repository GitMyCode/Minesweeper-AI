package root;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by MB on 10/29/2014.
 */
public class WindowMinesweeper extends JFrame implements ActionListener{


    int ROW = 20;
    int COL = 20;
    private final int WIDTH = (COL*15) ; //pour expert : 480
    private final int HEIGHT = (ROW * 15); //poru expert :280

    JButton create;
    JLabel label_choice_row;
    JLabel label_choice_col;
    JLabel label_mine;

    JTextField choiceCol;
    JTextField choiceRow;
    JTextField choiceMines;
    JPanel panelCreation;



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

        panelCreation = new JPanel(new GridBagLayout());
        panelCreation.setBackground(Color.orange);
        Dimension panel_creation_dim = new Dimension(200,200);
        panelCreation.setPreferredSize(panel_creation_dim);
        panelCreation.setMinimumSize(panel_creation_dim);
        panelCreation.setMaximumSize(panel_creation_dim);

        GLOBAL.addItem(panelCreation, label_choice_row, 0, 0, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceRow, 1, 0, 1, 1, GridBagConstraints.EAST);
        GLOBAL.addItem(panelCreation, label_choice_col, 0, 1, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceCol, 1, 1, 1, 1, GridBagConstraints.EAST);
        GLOBAL.addItem(panelCreation, label_mine, 0, 2, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceMines, 1, 2, 1, 1, GridBagConstraints.EAST);
        GLOBAL.addItem(panelCreation, create, 0, 3, 0, 0, GridBagConstraints.WEST);


        add(panelCreation,BorderLayout.NORTH);
        add(create,BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }


    public static void main (String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WindowMinesweeper().setVisible(true);
            }
        });

    }

    public void createBoard(int lignes,int cols,int mines){
        GLOBAL.NBCOL= cols;
        GLOBAL.NBLIGNE = lignes;

        new BoardGameView(lignes,cols,mines).setVisible(true);

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


            createBoard(row,col,mines);
        }


    }
}
