package soccer.core;

public interface Controler {

    public void init(GamePanel gamePanel);

    // tzw. callback, wywolywany przez GamePanel
    public void onMove(int dir);
    public void onEnd();
}
