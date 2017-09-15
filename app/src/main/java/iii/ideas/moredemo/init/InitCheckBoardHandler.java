package iii.ideas.moredemo.init;

import android.content.Context;

import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/9/13.
 */

public class InitCheckBoardHandler extends BaseHandler
{
    public InitCheckBoardHandler(Context context)
    {
        super(context);
    }
    
    public void init()
    {
        Thread m = new Thread(new InitStateRunnable());
        m.start();
        
    }
    
    
    private class InitStateRunnable implements Runnable
    {
        
        
        @Override
        public void run()
        {
            try
            {
                while (true)
                {
                    Logs.showTrace("[InitCheckBoardHandler] check again");
                    if (InitCheckBoardParameters.getSpotifyInitState() == InitCheckBoardParameters.STATE_SUCCESS
                            && InitCheckBoardParameters.getTTSInitState() == InitCheckBoardParameters.STATE_SUCCESS)
                    {
                        //callback to let it know
                        HashMap<String, String> message = new HashMap<>();
                        message.put("message", "success");
                        callBackMessage(ResponseCode.ERR_SUCCESS, InitCheckBoardParameters.CLASS_INIT_CHECK_BOARD, InitCheckBoardParameters.METHOD_INIT, message);
                        
                        break;
                    }
                    else if (InitCheckBoardParameters.getSpotifyInitState() == InitCheckBoardParameters.STATE_FAIL)
                    {
                        HashMap<String, String> message = new HashMap<>();
                        message.put("message", "spotify have some error");
                        callBackMessage(ResponseCode.ERR_UNKNOWN, InitCheckBoardParameters.CLASS_INIT_CHECK_BOARD,
                                InitCheckBoardParameters.METHOD_SPOTIFY, message);
                        
                        break;
                    }
                    else if (InitCheckBoardParameters.getTTSInitState() == InitCheckBoardParameters.STATE_FAIL)
                    {
                        
                        HashMap<String, String> message = new HashMap<>();
                        message.put("message", "tts have some error");
                        
                        callBackMessage(ResponseCode.ERR_UNKNOWN, InitCheckBoardParameters.CLASS_INIT_CHECK_BOARD,
                                InitCheckBoardParameters.METHOD_TTS, message);
                        
                        break;
                    }
                    
                    
                    Thread.sleep(InitCheckBoardParameters.CHECK_TIME);
                    
                }
            }
            catch (InterruptedException e)
            {
                
            }
        }
    }
    
    
}
