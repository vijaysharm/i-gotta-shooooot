package com.igottashoot.game.controlfile;

/**
 * Thrown when there's an error in the data in a control file: missing section(s),
 * missing key(s), unset keys, or badly-formatted keys.
 */
class ControlFileDataException extends ControlFileException
{
    private String mSection;
    private String mKey;

    ControlFileDataException( String filename, String section, String key, String message )
    {
        super( filename, message );
        mSection = section;
        mKey = key;
    }

    @Override
    public String getMessage()
    {
        StringBuffer buf = new StringBuffer();
        writePrefix( buf );
        if ( mSection != null )
            buf.append( ", section '" ).append( mSection ).append( '\'' );
        if ( mKey != null )
            buf.append( ", key '" ).append( mKey ).append( '\'' );
        buf.append( ": " ).append( getRealMessage() );
        return buf.toString();
    }
}
