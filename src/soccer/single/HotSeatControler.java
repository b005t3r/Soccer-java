package soccer.single;

import soccer.core.Controler;
import soccer.core.GamePanel;


public class HotSeatControler implements Controler {

    private GamePanel gamePanel;
    private String player1;
    private String player2;

    public HotSeatControler(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void onMove(int dir) {
        int result = gamePanel.getGameResult();
        if(result == GamePanel.RESULT_GAME_GOES_ON) {
            if(! gamePanel.hasNextMove())
                gamePanel.switchPlayer();
        }
        else {
            gamePanel.setLock(true);
            gamePanel.setShowAllowedMoves(false);
            gamePanel.showResult();
        }
    }

    public void onEnd() {/* tutaj nic nie robi, ale w sieci bedzie zamykal polaczenie */}

    public void init(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        gamePanel.setPlayer1(player1);
        gamePanel.setPlayer2(player2);
    }
}
