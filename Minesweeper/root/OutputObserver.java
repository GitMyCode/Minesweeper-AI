package root;

public interface OutputObserver {

    public void message(String msg);

    public void updateLost();
    public void updateWins();

    public void callback();

}
