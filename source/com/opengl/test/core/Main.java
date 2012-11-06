package com.opengl.test.core;

import java.io.IOException;

import com.igottashoot.game.core.GameRunnable;
import com.igottashoot.game.events.CanabaltLoadEvent;

/**
 * This is executed on some Java thread. Its our main thread, and has to somehow
 * manage calls from the {@link OpenGLSurfaceRenderer} thread (EDT) and from the
 * {@link GameRunnable}
 */
public class Main
{
    // These values come from the hardware
    private static final int ACTUAL_SCREEN_HEIGHT = 320; // 480
    private static final int ACTUAL_SCREEN_WIDTH = 480; // 800
    
    // These are the desired aspect ratio
    private static final int DESIRED_HEIGHT = 320;
    private static final int DESIRED_WIDTH = 480;
    
    enum GameCommands
    {
        START,
        STOP,
        PAUSE,
        RESUME,
        LOADLEVEL,
        RESTARTLEVEL,
        ENABLESOUND,
        DISABLESOUND,
        
        QUIT
    }
    
    public static void main( String[] args ) throws IOException
    {
        Game game = new Game( DESIRED_WIDTH, DESIRED_HEIGHT, ACTUAL_SCREEN_WIDTH, ACTUAL_SCREEN_HEIGHT );
        game.start();
//        game.load( new LoadLevelEvent() );
        game.load( new CanabaltLoadEvent() );
        
//        BufferedReader stdin = new BufferedReader( new InputStreamReader( System.in ) );
//        while ( true )
//        {
//            String message;
//            System.out.print( "Enter the message : " );
//            System.out.flush();
//            message = stdin.readLine();
//            GameCommands command = parse( message );
//            
//            if ( command == null )
//                continue;
//            
//            switch( command )
//            {
//                case START:
//                    game.start();
//                case STOP:
//                    game.stop();
//                case LOADLEVEL:
//                    game.load( new LoadLevelEvent() );
//                case QUIT:
//                    break;
//            }
//        }
    }

//    private static GameCommands parse( String message )
//    {
//        for ( GameCommands command : GameCommands.values() )
//        {
//            if ( command.name().toLowerCase().equals( message.toLowerCase() ) )
//                return command;
//        }
//        return null;
//    }
}