package root;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;

/**
 * Created by MB on 10/29/2014.
 */
public class BoardGameView extends JFrame implements ActionListener, OutputObserver{


    private JPanel gridPanel;
    private Rule les_y;
    private Rule les_x;
    private Box cadre;
    private JPanel containterField;
    private JLabel flagRemaining;
    private JPanel menu;

    private JButton reset;
    private JButton start;
    GridView gv;
    GridController gridController;
    public Grid grid;
    private ArtificialPlayer ai;

    private JTextArea messageTextArea ;

    /*Object for running game*/
    private GameRunner runner = null;
    private Runnable task = null;
    private Thread t = null;


    int nbligne=0;
    int nbcol=0;
    int nbMines;
    int deplayTime = 100;



    public BoardGameView (int nbligne, int nbcol, int nbMine,String aiName,int delay){
        setSize(WIDTH + 300, HEIGHT + 100);

        ai=getAI(aiName);
        setTitle(ai.getAiName());
        this.deplayTime = delay;
        this.nbcol = nbcol;
        this.nbligne = nbligne;
        this.nbMines = nbMine;
        cadre = new Box(BoxLayout.Y_AXIS);
        cadre.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        cadre.add(Box.createVerticalGlue());
        cadre.add(Box.createHorizontalGlue());


        createGridView(nbligne, nbcol);
        flagRemaining = new JLabel(""+nbMine);

        grid = new Grid(nbligne,nbcol,nbMine);
        gv.setGrid(grid);
        gridController = new GridControllerImpl(grid,gv,flagRemaining);
        gv.setController(gridController);

        menu = new JPanel(new GridBagLayout());
        Dimension menuDim = new Dimension(100,100);
        menu.setPreferredSize(menuDim);
        menu.setMaximumSize(menuDim);
        reset = new JButton("Reset");
        start = new JButton("Start");
        reset.addActionListener(this);
        start.addActionListener(this);
        GLOBAL.addItem(menu, reset, 0, 0, 1, 1, GridBagConstraints.EAST);
        GLOBAL.addItem(menu, start, 0, 1, 1, 1, GridBagConstraints.EAST);
        add(menu,BorderLayout.EAST);


        JPanel buttomPanel = new JPanel();

        JScrollPane pane = new JScrollPane();
        //buttomPanel.setBackground(Color.cyan);
        messageTextArea = new JTextArea();
        messageTextArea.setColumns(nbcol + GLOBAL.CELL_SIZE);
        messageTextArea.setEditable(false);
        messageTextArea.setRows(5);
        pane.setViewportView(messageTextArea);

        GLOBAL.addItem(buttomPanel,flagRemaining,0,0,1,1,GridBagConstraints.NORTH);
        GLOBAL.addItem(buttomPanel,pane,1,0,1,1,GridBagConstraints.SOUTH);
        add(buttomPanel,BorderLayout.SOUTH);



        message("Start");
       pack();



    }

    public ArtificialPlayer getAI(String name){
        ArtificialPlayer returnAi = null;
        try{
            Class c = Class.forName(name);
            Constructor<?> constructor = c.getConstructor();
            returnAi =(ArtificialPlayer) constructor.newInstance();

        }catch (Exception e){
            System.out.println(e);
        }
        return returnAi;
    }


    public void createGridView(int row,int col){


        int width = (col*GLOBAL.CELL_SIZE) ; //pour expert : 480
        int height = (row * GLOBAL.CELL_SIZE); //pour expert :280

        les_y = new Rule(1,col);
        Dimension dim_y = new Dimension(width+20,7);
        les_y.setPreferredSize(dim_y);
        les_y.setMinimumSize(dim_y);
        les_y.setMaximumSize(dim_y);
        les_y.setLayout(new GridLayout(1,col));

        les_x = new Rule(0,row);
        Dimension dim_x = new Dimension(10,height);
        les_x.setPreferredSize(dim_x);
        les_x.setMinimumSize(dim_x);
        les_x.setMaximumSize(dim_x);
        // les_x.setBackground(Color.CYAN);
        les_x.setLayout(new GridLayout(row,1));



        containterField = new JPanel(new GridBagLayout());
        Dimension dim_container = new Dimension(width+40,height+30);
        containterField.setMaximumSize(dim_container);
        containterField.setMinimumSize(dim_container);
        containterField.setPreferredSize(dim_container);
        //containterField.setBackground(Color.red);


        GLOBAL.addItem(containterField, les_y, 1, 0, 0, 7, GridBagConstraints.NORTHWEST);

        GLOBAL.addItem(containterField, les_x, 0, 1, 0, 7, GridBagConstraints.WEST);


        gv = new GridView(row,col,width,height);
        gv.repaint();
        gv.setBackground(Color.cyan);



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


    @Override
    public void actionPerformed (ActionEvent actionEvent) {

        if(actionEvent.getActionCommand() == "Start"){
            startGame();
        }else if(actionEvent.getActionCommand() == "Reset") {
            grid.resetGrid();

            if(t !=null){
                t.interrupt();
            }
            runner = null;
            gv.repaint();
            flagRemaining.setText(String.valueOf(nbMines));
            message("Reset");


        }



    }

    private void startGame(){
        if(runner ==null){
            runner = new GameRunner(ai,grid,gridController,deplayTime);
            runner.setOutputObserver(this);
            task = new Runnable(){
                @Override
                public void run(){
                        try{
                            System.gc();
                            runner.run();
                            System.gc();

                        }catch(Exception e){
                            e.printStackTrace();
                        }


                }
            };
            t = new Thread(task);
            t.start();
        }


    }

    /* De connect5 auteur Éric beaudry */
    public void message(String msg) {
        messageTextArea.append(msg + "\n");
        messageTextArea.setCaretPosition(messageTextArea.getText().length());
    }


}
