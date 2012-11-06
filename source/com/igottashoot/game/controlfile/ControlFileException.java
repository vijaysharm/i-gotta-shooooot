package com.igottashoot.game.controlfile;

public class ControlFileException extends Exception
{
    private static final long serialVersionUID = 2786958213391082324L;
    protected String mFilename = null;
    protected int mLineNumber = -1;

    /** For use by application code -- never used by ControlFile itself. */
    public ControlFileException( String message )
    {
        super( message );
    }
    
    public ControlFileException( String message, Throwable e )
    {
        super( message, e );
    }

    ControlFileException( String filename, int lineNumber, String message )
    {
        this( filename, lineNumber, message, null );
    }

    ControlFileException( String filename, String message  )
    {
        this( filename, -1, message, null );
    }

    ControlFileException( String filename, String message, Throwable cause )
    {
        this( filename, -1, message, cause );
    }

    ControlFileException( String filename, int lineNumber, String message, Throwable cause )
    {
        super( message, cause );
        mFilename = filename;
        mLineNumber = lineNumber;
    }

    @Override
    public String getMessage()
    {
        StringBuffer buf = new StringBuffer();
        boolean prefix = writePrefix( buf );
        if ( prefix )
            buf.append( ": " );

        String realMessage = super.getMessage();
        if ( realMessage != null )
            buf.append( realMessage );

        return buf.toString();
    }

    protected String getRealMessage()
    {
        return super.getMessage();
    }
    
    protected boolean writePrefix( StringBuffer buf )
    {
        if ( mFilename == null )
        return false;

        buf.append( mFilename );
        if ( mLineNumber != -1 )
            buf.append( ", line " ).append( mLineNumber );
        return true;
    }
}
