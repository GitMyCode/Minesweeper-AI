package root;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * Created by MB on 10/30/2014.
 */
public class GameRunner implements Runnable {

    private ArtificialPlayer ai;
    protected OutputObserver outputObserver= null;

    private Grid grid;
    private GridController controller;
    private int delayTime = 100;

    public GameRunner(ArtificialPlayer ai,Grid g,GridController controller,int delay){
        this.ai = ai;
        this.grid = g;
        this.controller = controller;
        this.delayTime = delay;
    }


    @Override
    public void run () {
        final Timer timer = new Timer(delayTime,null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent actionEvent) {


                Set<Move> aiMoves = ai.getAiPlay(grid);
                controller.movesSetPlay(aiMoves);
                if(grid.gameFinish()){
                    if(grid.lost){
                        outputObserver.message("Lost!");
                        outputObserver.updateLost();
                        grid.showAllCase();

                    }else if(grid.win){
                        outputObserver.message("Win!");
                        outputObserver.updateWins();
                    }
                    outputObserver.callback();
                    timer.stop();
                }

            }
        });

        timer.start();


    }
    public void setOutputObserver(OutputObserver outputObserver){
        this.outputObserver = outputObserver;
    }

}
