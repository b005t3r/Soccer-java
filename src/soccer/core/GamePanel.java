package soccer.core;

import gui.GameResultDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class GamePanel extends JPanel implements MouseListener {

    /*
        a boisko wyglada tak:
        +---------------------------------------+
        |                                       |
        |               +---+---+               |
        |               |   |   |               |
        |   +---+---+---+---+---+---+---+---+   |
        |   |   |   |   |   |   |   |   |   |   |
        |   +---+---+---+---+---+---+---+---+   |
        |   |   |   |   |   |   |   |   |   |   |
        |   +---+---+---+---+---+---+---+---+   |
        |   |   |   |   |   |   |   |   |   |   |
        |   +---+---+---+---+---+---+---+---+   | 4
        |   |   |   |   |   |   |   |   |   |   | 2
        |   +---+---+---+---+---+---+---+---+   | 0
        |   |   |   |   |   |   |   |   |   |   |
        |   +---+---+---+---+---+---+---+---+   | p
        |   |   |   |   |   |   |   |   |   |   | i
        |   +---+---+---+---o---+---+---+---+   | k
        |   |   |   |   |   |   |   |   |   |   | s
        |   +---+---+---+---+---+---+---+---+   | e
        |   |   |   |   |   |   |   |   |   |   | l
        |   +---+---+---+---+---+---+---+---+   | i
        |   |   |   |   |   |   |   |   |   |   |
        |   +---+---+---+---+---+---+---+---+   |
        |   |   |   |   |   |   |   |   |   |   |
        |   +---+---+---+---+---+---+---+---+   |
        |   |   |   |   |   |   |   |   |   |   |
        |   +---+---+---+---+---+---+---+---+   |
        |   |   |   |   |   |   |   |   |   |   |
        |   +---+---+---+---+---+---+---+---+   |
        |               |   |   |               |
        |               +---+---+               |
        |                                       |
        +---------------------------------------+
                       300 pikseli
    */

    // wymiary boiska, bez bramek
    public static final int COLUMNS = 8;
    public static final int ROWS    = 12;

    // dlugosc boku jednego kwadracika
    public static final int SQR_SIZE = 40;

    public static final int POINT_SIZE = SQR_SIZE / 4;

    public static final int WIDTH  = (COLUMNS + 2) * SQR_SIZE;  // 300 px
    public static final int HEIGHT = (ROWS + 4) * SQR_SIZE;     // 420 px

    public static final Color GREEN       = new Color(20, 170, 20);
    public static final Color DARK_GREEN  = new Color(5, 110, 5);
    public static final Color WHITE       = Color.WHITE;
    public static final Color BLUE        = new Color(80, 80, 255);
    public static final Color DARK_BLUE   = new Color(60, 60, 235);
    public static final Color BLACK       = Color.BLACK;
    public static final Color PLAYER1_COL = Color.YELLOW;
    public static final Color PLAYER2_COL = Color.RED;

    public static final Stroke THIN_STROKE   = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final Stroke NORMAL_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final Stroke THICK_STROKE  = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final Stroke LINE_STROKE   = new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public static final int LEFT        = 0x00000001;
    public static final int UP          = 0x00000010;
    public static final int RIGHT       = 0x00000100;
    public static final int DOWN        = 0x00001000;
    public static final int LEFT_UP     = 0x00010000;
    public static final int LEFT_DOWN   = 0x00100000;
    public static final int RIGHT_UP    = 0x01000000;
    public static final int RIGHT_DOWN  = 0x10000000;

    public static final int RESULT_PLAYER1_WINS = 0;
    public static final int RESULT_PLAYER2_WINS = 1;
    public static final int RESULT_GAME_GOES_ON = 2;

    public static final int PLAYER1 = 0;
    public static final int PLAYER2 = 1;

    private JFrame owner;

    private Controler cont;

    private BufferedImage buf;
    private Graphics2D bufGraphics;

    private BufferedImage lineBuf;
    private Graphics2D lineGraphics;

    private int prevCol;
    private int prevRow;
    private int[][] moves;

    private String player1;
    private String player2;
    private int currentPlayer;
    private boolean nextMove;
    private boolean lock;
    private boolean showAllowedMoves;

    private GameResultDialog gameResult;

    public GamePanel(Controler cont, JFrame owner) {
        this.owner = owner;
        this.cont = cont;
        cont.init(this);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        buf = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        bufGraphics = (Graphics2D) buf.getGraphics();
        paintBackground();

        lineBuf = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        clearLineBuf();
        lineGraphics = (Graphics2D) lineBuf.getGraphics();

        // na poczatku od srodka boiska
        prevCol = COLUMNS / 2;
        prevRow = ROWS / 2 + 1;     // bo sa dodatkowo dwie bramki

        initializeMoves();

        player1 = "";
        player2 = "";
        currentPlayer = PLAYER1;

        addMouseListener(this);

        showAllowedMoves = true;

        gameResult = new GameResultDialog(owner);
    }

    public void addMove(int dir) {
        int col = 0, row = 0;   // zeby nie krzyczalo

        nextMove = false;

        switch(dir) {
            case UP:         col = prevCol;     row = prevRow - 1;
                             if(moves[col][row] != 0) nextMove = true;
                             moves[prevCol][prevRow] |= UP;         moves[col][row] |= DOWN;
                             break;
            case RIGHT_UP:   col = prevCol + 1; row = prevRow - 1;
                             if(moves[col][row] != 0) nextMove = true;
                             moves[prevCol][prevRow] |= RIGHT_UP;   moves[col][row] |= LEFT_DOWN;
                             break;
            case RIGHT:      col = prevCol + 1; row = prevRow;
                             if(moves[col][row] != 0) nextMove = true;
                             moves[prevCol][prevRow] |= RIGHT;      moves[col][row] |= LEFT;
                             break;
            case RIGHT_DOWN: col = prevCol + 1; row = prevRow + 1;
                             if(moves[col][row] != 0) nextMove = true;
                             moves[prevCol][prevRow] |= RIGHT_DOWN; moves[col][row] |= LEFT_UP;
                             break;
            case DOWN:       col = prevCol;     row = prevRow + 1;
                             if(moves[col][row] != 0) nextMove = true;
                             moves[prevCol][prevRow] |= DOWN;       moves[col][row] |= UP;
                             break;
            case LEFT_DOWN:  col = prevCol - 1; row = prevRow + 1;
                             if(moves[col][row] != 0) nextMove = true;
                             moves[prevCol][prevRow] |= LEFT_DOWN;  moves[col][row] |= RIGHT_UP;
                             break;
            case LEFT:       col = prevCol - 1; row = prevRow;
                             if(moves[col][row] != 0) nextMove = true;
                             moves[prevCol][prevRow] |= LEFT;       moves[col][row] |= RIGHT;
                             break;
            case LEFT_UP:    col = prevCol - 1; row = prevRow - 1;
                             if(moves[col][row] != 0) nextMove = true;
                             moves[prevCol][prevRow] |= LEFT_UP;    moves[col][row] |= RIGHT_DOWN;
                             break;
        }

        lineGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        lineGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        lineGraphics.setColor(DARK_BLUE);
        lineGraphics.setStroke(LINE_STROKE);
        lineGraphics.drawLine((prevCol + 1) * SQR_SIZE, (prevRow + 1) * SQR_SIZE,
                              (col + 1) * SQR_SIZE, (row + 1) * SQR_SIZE);

        prevCol = col;
        prevRow = row;

        repaint();
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(buf,     null, 0, 0);
        g2.drawImage(lineBuf, null, 0, 0);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        paintTargetArea(g2);
        paintMovePoints(g2);
    }

    public boolean isMoveAllowed(int dir) {
        return (moves[prevCol][prevRow] & dir) == 0;
    }

    public boolean hasNextMove() { return nextMove; }

    public void setPlayer1(String player1) { this.player1 = player1; }
    public void setPlayer2(String player2) { this.player2 = player2; }

    public void switchPlayer() {
        if(currentPlayer == PLAYER1) currentPlayer = PLAYER2;
        else                         currentPlayer = PLAYER1;
    }

    public int getGameResult() {
             if(prevRow == 0)        return RESULT_PLAYER2_WINS;
        else if(prevRow == ROWS + 2) return RESULT_PLAYER1_WINS;
        else if(moves[prevCol][prevRow] == 0x11111111) {
            if(currentPlayer == PLAYER1)
                return RESULT_PLAYER2_WINS;
            else
                return RESULT_PLAYER1_WINS;
        }
        else
            return RESULT_GAME_GOES_ON;
    }

    public void setShowAllowedMoves(boolean showAllowedMoves) { this.showAllowedMoves = showAllowedMoves; }

    public void setLock(boolean lock) { this.lock = lock;}

    public void showResult() {
        String text;
        if(getGameResult() == RESULT_PLAYER1_WINS) text = "Congratulations! " + player1 + " wins!";
        else                                       text = "Congratulations! " + player2 + " wins!";

        gameResult.setText(text);
        gameResult.show();
    }

    public void restart(Controler c) {
        cont.onEnd();

        prevCol = COLUMNS / 2;
        prevRow = ROWS / 2 + 1;

        initializeMoves();
        clearLineBuf();

        player1 = "";
        player2 = "";
        currentPlayer = PLAYER1;

        lock = false;
        showAllowedMoves = true;

        repaint();

        cont = c;
        c.init(this);
    }

    public void mouseClicked(MouseEvent e) {
        if(! lock) {
            int x = e.getX(),
                y = e.getY();

            if(isMoveAllowed(DOWN) && isCircleClicked(DOWN, x, y)) {
                addMove(DOWN);
                cont.onMove(DOWN);
            }
            if(isMoveAllowed(LEFT_DOWN) && isCircleClicked(LEFT_DOWN, x, y)) {
                addMove(LEFT_DOWN);
                cont.onMove(LEFT_DOWN);
            }
            if(isMoveAllowed(LEFT) && isCircleClicked(LEFT, x, y)) {
                addMove(LEFT);
                cont.onMove(LEFT);
            }
            if(isMoveAllowed(LEFT_UP) && isCircleClicked(LEFT_UP, x, y)) {
                addMove(LEFT_UP);
                cont.onMove(LEFT_UP);
            }
            if(isMoveAllowed(UP) && isCircleClicked(UP, x, y)) {
                addMove(UP);
                cont.onMove(UP);
            }
            if(isMoveAllowed(RIGHT_UP) && isCircleClicked(RIGHT_UP, x, y)) {
                addMove(RIGHT_UP);
                cont.onMove(RIGHT_UP);
            }
            if(isMoveAllowed(RIGHT) && isCircleClicked(RIGHT, x, y)) {
                addMove(RIGHT);
                cont.onMove(RIGHT);
            }
            if(isMoveAllowed(RIGHT_DOWN) && isCircleClicked(RIGHT_DOWN, x, y)) {
                addMove(RIGHT_DOWN);
                cont.onMove(RIGHT_DOWN);
            }
        }
    }

    private boolean isCircleClicked(int dir, int x, int y) {
        int row = 0, col = 0;
        switch(dir) {
            case UP:         col = prevCol + 1; row = prevRow;
                             break;
            case RIGHT_UP:   col = prevCol + 2; row = prevRow;
                             break;
            case RIGHT:      col = prevCol + 2; row = prevRow + 1;
                             break;
            case RIGHT_DOWN: col = prevCol + 2; row = prevRow + 2;
                             break;
            case DOWN:       col = prevCol + 1; row = prevRow + 2;
                             break;
            case LEFT_DOWN:  col = prevCol;     row = prevRow + 2;
                             break;
            case LEFT:       col = prevCol;     row = prevRow + 1;
                             break;
            case LEFT_UP:    col = prevCol;     row = prevRow;
                             break;
        }

        return x >= col * SQR_SIZE - POINT_SIZE / 2
            && x <= col * SQR_SIZE - POINT_SIZE / 2 + POINT_SIZE
            && y >= row * SQR_SIZE - POINT_SIZE / 2
            && y <= row * SQR_SIZE - POINT_SIZE / 2 + POINT_SIZE;
    }

    private void paintMovePoints(Graphics2D g2) {
        Color col, col2;

        if(currentPlayer == PLAYER1) col  = PLAYER1_COL;
        else                         col  = PLAYER2_COL;

        col2 = BLUE;
        g2.setStroke(THIN_STROKE);
        paintPoint(g2, prevCol + 1, prevRow + 1, col2);

        if(showAllowedMoves) {
            if(isMoveAllowed(LEFT))       paintPoint(g2, prevCol,     prevRow + 1, col);
            if(isMoveAllowed(LEFT_UP))    paintPoint(g2, prevCol,     prevRow,     col);
            if(isMoveAllowed(UP))         paintPoint(g2, prevCol + 1, prevRow,     col);
            if(isMoveAllowed(RIGHT_UP))   paintPoint(g2, prevCol + 2, prevRow,     col);
            if(isMoveAllowed(RIGHT))      paintPoint(g2, prevCol + 2, prevRow + 1, col);
            if(isMoveAllowed(RIGHT_DOWN)) paintPoint(g2, prevCol + 2, prevRow + 2, col);
            if(isMoveAllowed(DOWN))       paintPoint(g2, prevCol + 1, prevRow + 2, col);
            if(isMoveAllowed(LEFT_DOWN))  paintPoint(g2, prevCol,     prevRow + 2, col);
        }
    }

    private void paintTargetArea(Graphics2D g2) {
        Color col;

        g2.setStroke(THIN_STROKE);

        if(currentPlayer == PLAYER1) {
            col  = PLAYER2_COL;
            paintPoint(g2, 4, 15, col);
            paintPoint(g2, 5, 15, col);
            paintPoint(g2, 6, 15, col);
        }
        else {
            col  = PLAYER1_COL;
            paintPoint(g2, 4, 1, col);
            paintPoint(g2, 5, 1, col);
            paintPoint(g2, 6, 1, col);
        }
    }

    private void paintBackground() {
        // murawa
        bufGraphics.setColor(GREEN);
        bufGraphics.fillRect(0, 0, WIDTH, HEIGHT);

        bufGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // kratka
        bufGraphics.setColor(DARK_GREEN);
        bufGraphics.setStroke(THIN_STROKE);
        for(int i = 0; i < COLUMNS + 1; i++)
            if(i > 2 && i < 6)
                bufGraphics.drawLine((i + 1) * SQR_SIZE, SQR_SIZE, (i + 1) * SQR_SIZE, HEIGHT - SQR_SIZE);
            else
                bufGraphics.drawLine((i + 1) * SQR_SIZE, 2 * SQR_SIZE, (i + 1) * SQR_SIZE, HEIGHT - 2 * SQR_SIZE);
        for(int i = 0; i < ROWS + 3; i++)
            if(i == 0 || i == ROWS + 2)
                bufGraphics.drawLine(4 * SQR_SIZE, (i + 1) * SQR_SIZE, WIDTH - 4 * SQR_SIZE, (i + 1) * SQR_SIZE);
            else
                bufGraphics.drawLine(SQR_SIZE, (i + 1) * SQR_SIZE, WIDTH - SQR_SIZE, (i + 1) * SQR_SIZE);

        // biale linie na murawie
        // srodek
        bufGraphics.setColor(WHITE);
        bufGraphics.setStroke(NORMAL_STROKE);
        bufGraphics.drawLine(SQR_SIZE, 8 * SQR_SIZE, WIDTH - SQR_SIZE, 8 * SQR_SIZE);
        bufGraphics.drawArc(4 * SQR_SIZE, 7 * SQR_SIZE, 2 * SQR_SIZE, 2 * SQR_SIZE, 0, 360);

        // gorne pole karne
        bufGraphics.drawLine(3 * SQR_SIZE, 2 * SQR_SIZE, 3 * SQR_SIZE, 4 * SQR_SIZE);
        bufGraphics.drawLine(7 * SQR_SIZE, 2 * SQR_SIZE, 7 * SQR_SIZE, 4 * SQR_SIZE);
        bufGraphics.drawLine(3 * SQR_SIZE, 4 * SQR_SIZE, 7 * SQR_SIZE, 4 * SQR_SIZE);
//        bufGraphics.drawLine(3 * SQR_SIZE, 2 * SQR_SIZE, 7 * SQR_SIZE, 2 * SQR_SIZE);
        bufGraphics.drawArc(4 * SQR_SIZE, 3 * SQR_SIZE, 2 * SQR_SIZE, 2 * SQR_SIZE, 180, 180);

        // dolne pole karne
        bufGraphics.drawLine(3 * SQR_SIZE, HEIGHT - 2 * SQR_SIZE, 3 * SQR_SIZE, HEIGHT - 4 * SQR_SIZE);
        bufGraphics.drawLine(7 * SQR_SIZE, HEIGHT - 2 * SQR_SIZE, 7 * SQR_SIZE, HEIGHT - 4 * SQR_SIZE);
        bufGraphics.drawLine(3 * SQR_SIZE, HEIGHT - 4 * SQR_SIZE, 7 * SQR_SIZE, HEIGHT - 4 * SQR_SIZE);
//        bufGraphics.drawLine(3 * SQR_SIZE, HEIGHT - 2 * SQR_SIZE, 7 * SQR_SIZE, HEIGHT - 2 * SQR_SIZE);
        bufGraphics.drawArc(4 * SQR_SIZE, HEIGHT - 5 * SQR_SIZE, 2 * SQR_SIZE, 2 * SQR_SIZE, 0, 180);

        // krawedzie i bramki
        bufGraphics.setStroke(THICK_STROKE);
        bufGraphics.drawLine(SQR_SIZE, 2 * SQR_SIZE, 4 * SQR_SIZE, 2 * SQR_SIZE);
        bufGraphics.drawLine(6 * SQR_SIZE, 2 * SQR_SIZE, 9 * SQR_SIZE, 2 * SQR_SIZE);
        bufGraphics.drawLine(4 * SQR_SIZE, SQR_SIZE, 6 * SQR_SIZE, SQR_SIZE);

        bufGraphics.drawLine(SQR_SIZE, HEIGHT - 2 * SQR_SIZE, 4 * SQR_SIZE, HEIGHT - 2 * SQR_SIZE);
        bufGraphics.drawLine(6 * SQR_SIZE, HEIGHT - 2 * SQR_SIZE, 9 * SQR_SIZE, HEIGHT - 2 * SQR_SIZE);
        bufGraphics.drawLine(4 * SQR_SIZE, HEIGHT - SQR_SIZE, 6 * SQR_SIZE, HEIGHT - SQR_SIZE);

        bufGraphics.drawLine(SQR_SIZE, 2 * SQR_SIZE, SQR_SIZE, HEIGHT - 2 * SQR_SIZE);
        bufGraphics.drawLine(WIDTH - SQR_SIZE, 2 * SQR_SIZE, WIDTH - SQR_SIZE, HEIGHT - 2 * SQR_SIZE);

        bufGraphics.drawLine(4 * SQR_SIZE, SQR_SIZE, 4 * SQR_SIZE, 2 * SQR_SIZE);
        bufGraphics.drawLine(6 * SQR_SIZE, SQR_SIZE, 6 * SQR_SIZE, 2 * SQR_SIZE);

        bufGraphics.drawLine(4 * SQR_SIZE, HEIGHT - SQR_SIZE, 4 * SQR_SIZE, HEIGHT - 2 * SQR_SIZE);
        bufGraphics.drawLine(6 * SQR_SIZE, HEIGHT - SQR_SIZE, 6 * SQR_SIZE, HEIGHT - 2 * SQR_SIZE);
    }

    private void paintPoint(Graphics2D g2, int c, int r, Color col) {
        g2.setColor(col);
        g2.fillArc(c * SQR_SIZE - POINT_SIZE / 2, r * SQR_SIZE - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE, 0, 360);
        g2.setColor(BLACK);
        g2.drawArc(c * SQR_SIZE - POINT_SIZE / 2, r * SQR_SIZE - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE, 0, 360);
    }

    private void clearLineBuf() {
        WritableRaster rast = lineBuf.getRaster();
        int[] col = { 0, 0, 0, 0};

        for(int i = 0; i < WIDTH; i++)
            for(int j = 0; j < HEIGHT; j++)
                rast.setPixel(i, j, col);
    }

    private void initializeMoves() {
        moves = new int[COLUMNS + 1][ROWS + 3];     // ROWS + 2, bo sa bramki
        for(int c = 0; c < COLUMNS + 1; c++)
            for(int r = 0; r < ROWS + 3; r++)
                if(r == 0) {                        // pierwszy wiersz, ten z linia bramki :)
                        if(c == 3) moves[c][r] |= DOWN | LEFT_DOWN | LEFT | LEFT_UP | UP | RIGHT_UP | RIGHT;
                   else if(c == 4) moves[c][r] |=                    LEFT | LEFT_UP | UP | RIGHT_UP | RIGHT;
                   else if(c == 5) moves[c][r] |= DOWN |             LEFT | LEFT_UP | UP | RIGHT_UP | RIGHT | RIGHT_DOWN;
                   else            moves[c][r] |= DOWN | LEFT_DOWN | LEFT | LEFT_UP | UP | RIGHT_UP | RIGHT | RIGHT_DOWN;
                }
                else
                if(r == 1) {                        // gorna krawedz boiska
                        if(c == 0) moves[c][r] |= DOWN | LEFT_DOWN | LEFT | LEFT_UP | UP | RIGHT_UP | RIGHT;
                   else if(c == 3) moves[c][r] |=                    LEFT | LEFT_UP | UP;
                   else if(c == 4) moves[c][r]  = 0;
                   else if(c == 5) moves[c][r] |=                                     UP | RIGHT_UP | RIGHT;
                   else if(c == 8) moves[c][r] |= DOWN |             LEFT | LEFT_UP | UP | RIGHT_UP | RIGHT | RIGHT_DOWN;
                   else            moves[c][r] |=                    LEFT | LEFT_UP | UP | RIGHT_UP | RIGHT;
                }
                else
                if(r == ROWS + 1) {                // dolna krawedz boiska
                        if(c == 0) moves[c][r] |= DOWN | LEFT_DOWN | LEFT | LEFT_UP | UP |            RIGHT | RIGHT_DOWN;
                   else if(c == 3) moves[c][r] |= DOWN | LEFT_DOWN | LEFT;
                   else if(c == 4) moves[c][r]  = 0;
                   else if(c == 5) moves[c][r] |= DOWN |                                              RIGHT | RIGHT_DOWN;
                   else if(c == 8) moves[c][r] |= DOWN | LEFT_DOWN | LEFT |           UP | RIGHT_UP | RIGHT | RIGHT_DOWN;
                   else            moves[c][r] |= DOWN | LEFT_DOWN | LEFT |                           RIGHT | RIGHT_DOWN;
                }
                else
                if(r == ROWS + 2) {                // dolna bramka
                        if(c == 3) moves[c][r] |= DOWN | LEFT_DOWN | LEFT | LEFT_UP | UP |            RIGHT | RIGHT_DOWN;
                   else if(c == 4) moves[c][r] |= DOWN | LEFT_DOWN | LEFT |                           RIGHT | RIGHT_DOWN;
                   else if(c == 5) moves[c][r] |= DOWN | LEFT_DOWN | LEFT |           UP | RIGHT_UP | RIGHT | RIGHT_DOWN;
                   else            moves[c][r] |= DOWN | LEFT_DOWN | LEFT | LEFT_UP | UP | RIGHT_UP | RIGHT | RIGHT_DOWN;
                }
                else {
                        if(c == 0) moves[c][r] |= DOWN | LEFT_DOWN | LEFT | LEFT_UP | UP;
                   else if(c == 8) moves[c][r] |= DOWN |                              UP | RIGHT_UP | RIGHT | RIGHT_DOWN;
                   else            moves[c][r]  = 0 ;
                }

    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}

}
