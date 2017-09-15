package iii.ideas.moredemo.init;

/**
 * Created by joe on 2017/9/13.
 */

public abstract class InitCheckBoardParameters
{
    public static final int CLASS_INIT_CHECK_BOARD = 5586;
    
    public static final int METHOD_INIT = 0;
    public static final int METHOD_SPOTIFY = 1;
    public static final int METHOD_TTS = 2;
    
    public static final int CHECK_TIME = 1000;
    
    public static final int STATE_UNKNOWN = 0;
    public static final int STATE_FAIL = -1;
    public static final int STATE_SUCCESS = 1;
    
    private static int TTS_STATE = STATE_UNKNOWN;
    
    private static int SPOTIFY_STATE = STATE_UNKNOWN;
    
    public static int getSpotifyInitState()
    {
        return SPOTIFY_STATE;
    }
    
    public static int getTTSInitState()
    {
        return TTS_STATE;
    }
    
    public static void setSpotifyInitState(int state)
    {
        SPOTIFY_STATE = state;
    }
    
    public static void setTTSInitState(int state)
    {
        TTS_STATE = state;
    }
    
    
}
