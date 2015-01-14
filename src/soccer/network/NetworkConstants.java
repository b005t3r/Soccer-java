package soccer.network;

public interface NetworkConstants {

    public static final int RES_GAME_OVER = 0x00000001;
    public static final int DATA_MOVES    = 0x00000010;
    public static final int DATA_TEXT     = 0x00000020;
    public static final int CONN_CLOSE    = 0x00000100;

    public static final int RES_MASK      = 0x0000000f;
    public static final int DATA_MASK     = 0x000000f0;
    public static final int CONN_MASK     = 0x00000f00;

}
