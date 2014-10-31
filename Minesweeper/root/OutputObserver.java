package root;

/**
 * Created by MB on 10/31/2014.
 */
public interface OutputObserver {

    public void message(String msg);

    public void updateLost();
    public void updateWins();

    public void callback();


}
