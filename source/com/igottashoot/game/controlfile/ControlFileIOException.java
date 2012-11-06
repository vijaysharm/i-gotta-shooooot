package com.igottashoot.game.controlfile;

import java.io.IOException;

class ControlFileIOException extends ControlFileException
{
    ControlFileIOException( String filename, String message, IOException cause )
    {
        super( filename, message, cause );
    }
}

