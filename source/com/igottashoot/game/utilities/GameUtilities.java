package com.igottashoot.game.utilities;

public class GameUtilities
{
    public static long getCurrentTimeInMillis()
    {
        return System.currentTimeMillis();
    }

    public static void print( String source, int printLevel, String message )
    {
        System.err.println( source + "-> " + message );
    }
}
