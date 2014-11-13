package root;

import java.util.Set;

/**
 * Created by MB on 10/30/2014.
 */
class GameRunner implements Runnable {

    private final ArtificialPlayer ai;

    private OutputObserver outputObserver= null;

    private final Grid grid;
    private final GridController controller;
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

            Set<Move> aiMoves = ai.getNextMoves(grid, thinkLimit);
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
            if(outputObserver ==null){
                System.out.println("observer null");
            }

        }while(!grid.gameFinish() && running && outputObserver!=null);


        /*After game*/
        if(grid.lost){
            SendMsg("Perdu!");
            outputObserver.updateLost();
           // grid.showAllCase();

        }else if(grid.win){
            SendMsg("Gagne!");
            outputObserver.updateWins();
        }
        if(!Thread.currentThread().isInterrupted()){
            try{
                Thread.sleep(200);
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
