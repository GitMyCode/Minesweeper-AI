package root;

import root.ENUM.CASE;
import root.ENUM.COUP;
import root.ai.CSP;

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
    private int thinkLimit = 1000;
    private volatile boolean running = true;

    public void terminate() {

        running = false;
    }

    public boolean isRunning(){
        return running;
    }

    public GameRunner(ArtificialPlayer ai,Grid g,GridController controller,int delay,int thinkLimit){
        running = true;
        this.ai = ai;
        this.grid = g;
        this.controller = controller;
        this.delayTime = delay;
        this.thinkLimit = thinkLimit;
    }


    @Override
    public void run () {
        System.out.println("start gamerunner");
        do{
           //

            Set<Move> aiMoves = ai.getAiPlay(grid,thinkLimit);
            controller.movesSetPlay(aiMoves);
            System.gc();
            try{
                if(delayTime != 0 && !Thread.currentThread().isInterrupted()){
                    Thread.sleep(delayTime);
                }
            }catch(InterruptedException ie){
                Thread.currentThread().interrupt();
                return;
            }
        }while(!grid.gameFinish() && running);


        /*After game*/
        if(grid.lost){
            SendMsg("Lost!");
            outputObserver.updateLost();
           // grid.showAllCase();

        }else if(grid.win){
            SendMsg("Win!");
            outputObserver.updateWins();
        }
        if(!Thread.currentThread().isInterrupted()){
            try{
                Thread.sleep(100);
            }catch(InterruptedException ie){
                Thread.currentThread().interrupt();
                outputObserver.callback();
                return;
            }
        }

        outputObserver.callback();


    }



    private synchronized void SendMsg(String msg){
        outputObserver.message(msg);
    }
    public void setOutputObserver(OutputObserver outputObserver){
        this.outputObserver = outputObserver;
    }

}
