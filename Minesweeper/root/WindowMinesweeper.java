package root;

import root.util.ClassFinder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by MB on 10/29/2014.
 */
public class WindowMinesweeper extends JFrame implements ActionListener, ChangeListener{

    /*
    * TODO
    * FAIRE LE MENAGE
    * */

    int nbMines = GLOBAL.NBMINES;
    int nbLignes = GLOBAL.NBLIGNE;
    int col = GLOBAL.NBCOL;
    String choosedAi = GLOBAL.DEFAULT_AI;
    String choosedDesign = GLOBAL.DEFAULT_DESIGN;
    int timeDelay = GLOBAL.DEFAULT_DELAY;
    int thinkLimit = GLOBAL.DEFAULT_MAXTHINK;
    int caseSize =GLOBAL.CELL_SIZE;


    private final TextListener textListener = new TextListener();

    JButton create;
    JButton importGrid;
    JLabel importLabel;
    JLabel label_choice_row;
    JLabel label_choice_col;
    JLabel label_mine;
    JLabel labelAi;
    JLabel labelTimer;


    JLabel slideMineLabel;
    JSlider sliderMines;
    JTextField choiceCol;
    JTextField choiceRow;
    JTextField choiceMines;
    JTextField choiceTimer;
    JTextField choiceMaxTime;
    JTextField choiceSizeCase;
    JPanel panelCreation;
    JComboBox choixAI;
    JComboBox choixDesign;
    JFileChooser chooser;
    List<Class<?>> classes;
    List<String> allDesign;

    File savedGridToPlay =null;
    String emptyLabelName = "Charger une grille enregistree precedemment";

    public WindowMinesweeper(){

        create = new JButton("Creer une nouvelle partie");
        create.addActionListener(this);


        label_choice_row = new JLabel("Nb lignes");
        label_choice_col = new JLabel("Nb col");
        label_mine       = new JLabel("Nb mines: " + nbMines);

        Dimension dim_jtext = new Dimension(120,20);
        choiceRow = new JTextField(""+ nbLignes);
        choiceRow.setPreferredSize(dim_jtext);
        choiceRow.setMinimumSize(dim_jtext);
        choiceRow.getDocument().addDocumentListener(textListener);

        choiceCol = new JTextField(""+col);
        choiceCol.setPreferredSize(dim_jtext);
        choiceCol.setMinimumSize(dim_jtext);
        choiceCol.getDocument().addDocumentListener(textListener);


        sliderMines = new JSlider(JSlider.HORIZONTAL,0,35,20);
        sliderMines.addChangeListener(this);
        sliderMines.setMajorTickSpacing(5);
        sliderMines.setMinorTickSpacing(1);
        sliderMines.setPaintTicks(true);
        sliderMines.setPaintLabels(true);
        sliderMines.setSize(new Dimension(280, 50));
        sliderMines.setMinimumSize(new Dimension(220, 50));



        choiceSizeCase = new JTextField(""+caseSize);
        choiceSizeCase.setPreferredSize(new Dimension(40,20));
        choiceSizeCase.setMinimumSize(new Dimension(40,20));

        choiceTimer = new JTextField(""+GLOBAL.DEFAULT_DELAY);
        choiceTimer.setPreferredSize(dim_jtext);
        choiceTimer.setMinimumSize(dim_jtext);
        labelTimer  = new JLabel("Delai (ms)");

        choiceMaxTime = new JTextField(""+GLOBAL.DEFAULT_MAXTHINK);
        choiceMaxTime.setPreferredSize(dim_jtext);
        choiceMaxTime.setMinimumSize(dim_jtext);

        panelCreation = new JPanel(new GridBagLayout());
        panelCreation.setBackground(Color.orange);
        Dimension panel_creation_dim = new Dimension(390,280);
        panelCreation.setPreferredSize(panel_creation_dim);
        panelCreation.setMinimumSize(panel_creation_dim);
        panelCreation.setMaximumSize(panel_creation_dim);

        choixAI = new JComboBox();
        labelAi = new JLabel("AI");
        classes = ClassFinder.find("root.ai");
        for(Class<?> c : classes){
            String name = c.getName();
            choixAI.addItem(name);
        }

        choixDesign = new JComboBox();
        allDesign = ClassFinder.findFolder("root.design");
        for(String s : allDesign){
            choixDesign.addItem(s);
        }



        GLOBAL.addItem(panelCreation, label_choice_row, 0, 0, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceRow, 1, 0, 1, 1, GridBagConstraints.EAST);
        GLOBAL.addItem(panelCreation, label_choice_col, 0, 1, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceCol, 1, 1, 1, 1, GridBagConstraints.EAST);


        GLOBAL.addItem(panelCreation, label_mine, 0, 2, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, sliderMines, 1, 2, 1, 1, GridBagConstraints.EAST);



        GLOBAL.addItem(panelCreation, create, 0, 3, 0, 0, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, labelAi, 0, 4, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choixAI, 1, 4, 1, 1, GridBagConstraints.EAST);

        GLOBAL.addItem(panelCreation, labelTimer, 0, 5, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceTimer, 1, 5, 1, 1, GridBagConstraints.EAST);

        GLOBAL.addItem(panelCreation, new JLabel("Limite reflexion"), 0, 6, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceMaxTime, 1, 6, 1, 1, GridBagConstraints.EAST);

        GLOBAL.addItem(panelCreation, new JLabel("Taille des cases"), 0, 7, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choiceSizeCase, 1, 7, 1, 1, GridBagConstraints.EAST);

        GLOBAL.addItem(panelCreation, new JLabel("Design: "), 0, 8, 1, 1, GridBagConstraints.WEST);
        GLOBAL.addItem(panelCreation, choixDesign, 1, 8, 1, 1, GridBagConstraints.EAST);

        importLabel = new JLabel(emptyLabelName);
        importGrid = new JButton("Importer");
        importGrid.addActionListener(this);
        GLOBAL.addItem(panelCreation,importGrid, 1, 9, 1, 1, GridBagConstraints.EAST);
        GLOBAL.addItem(panelCreation,importLabel, 0, 9, 3, 1, GridBagConstraints.WEST);


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


    public void createBoard(){

        System.gc();
        new Thread(
                (new Runnable() {
                    @Override
                    public void run() {
                        BoardGameView bv = new BoardGameView.GameBuilder().
                                row(nbLignes).
                                col(col).
                                mines(nbMines).
                                delay(timeDelay).
                                think(thinkLimit).
                                aiName(choosedAi).
                                caseSize(caseSize).
                                design(choosedDesign).
                                build();
                        bv.setVisible(true);
                        bv.setLocationRelativeTo(null);

                        /*BoardGameView bv = new BoardGameView(lignes,cols,mines,aiName,timeDelay,thinkLimit,caseSize);
                        bv.setVisible(true);
                        bv.setLocationRelativeTo(null);*/

                    }
                })
        ).start();
        System.gc();
    }
    public void loadGridToBoard(){
        System.out.println("Charger une grille");
        System.gc();
        Thread t = new Thread(
                (new Runnable() {
                    @Override
                    public void run() {
                        //BoardGameView bv = new BoardGameView(new Grid(savedGridToPlay),aiName,timeDelay,thinkLimit,caseSize);
                        BoardGameView bv = new BoardGameView.GameBuilder().
                                loadGrid(savedGridToPlay).
                                aiName(choosedAi).
                                caseSize(caseSize).
                                delay(timeDelay).
                                think(thinkLimit).
                                design(choosedDesign).
                                build();
                        bv.setVisible(true);
                        bv.setLocationRelativeTo(null);
                        bv.message("Load file: "+savedGridToPlay.getName());
                    }
                })
        );
        t.start();



        System.gc();

    }

    @Override
    public void actionPerformed (ActionEvent actionEvent) {

        if(actionEvent.getActionCommand() == "Creer une nouvelle partie"){
            updateParameter();
            createBoard();

        }else if(actionEvent.getActionCommand() =="Importer") {
            chooser = new JFileChooser(".");
                /*To keep the last selected as default*/
                if (importLabel.getText() != emptyLabelName){
                    chooser.setSelectedFile(new File(importLabel.getText()));
                }
                if (chooser.showDialog(new JFrame("Choisir un fichier"),"Ok") == JFileChooser.APPROVE_OPTION) {
                    if (chooser.getSelectedFile() != null) {
                        readFile(chooser.getSelectedFile());

                        updateParameter();

                        loadGridToBoard();
                        importLabel.setText(chooser.getSelectedFile().getName());

                    } else {
                        importLabel.setText(emptyLabelName);
                    }
                }

        }


    }

    public void readFile(File f){
        savedGridToPlay = f;
    }


    @Override
    public void stateChanged (ChangeEvent e) {

        JSlider source =(JSlider) e.getSource();
        if(!source.getValueIsAdjusting()){
            updateMine();
        }
    }

    public class TextListener implements DocumentListener{
        @Override
        public void insertUpdate (DocumentEvent e) {
            updateMine();
        }

        @Override
        public void removeUpdate (DocumentEvent e) {
            updateMine();
        }

        @Override
        public void changedUpdate (DocumentEvent e) {
            updateMine();
        }
    }


    public void updateParameter(){
        String text_row = choiceRow.getText();
        String text_col = choiceCol.getText();
        nbLignes = Integer.parseInt(text_row);
        col = Integer.parseInt(text_col);
        choosedAi = (String)choixAI.getSelectedItem();
        timeDelay= Integer.parseInt(choiceTimer.getText());
        thinkLimit= Integer.parseInt(choiceMaxTime.getText());
        choosedDesign =(String) choixDesign.getSelectedItem();

        caseSize = Integer.parseInt(choiceSizeCase.getText());

    }

    public void updateMine(){
        String text_row = choiceRow.getText();
        String text_col = choiceCol.getText();
        int lignes = (text_row.equals("") || text_row ==null)? 0 :Integer.parseInt(text_row);
        int col = (text_col.equals("") || text_col == null)? 0 :Integer.parseInt(text_col);

        float minePercentage = sliderMines.getValue();

        nbMines = (int)((lignes*col) * (minePercentage/100));
        label_mine.setText("Nb mines: " + nbMines);

    }
}
