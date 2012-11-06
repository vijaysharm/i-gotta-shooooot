package com.igottashoot.game.controlfile;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * All the key/value pairs in a single configuration section.
 *
 * Acts as a read-only, order-preserving Map from key (String) to value (String).
 */
public class SectionData implements Map<String, String>
{
    /** an ordered map from key (String) to value (String) */
    private LinkedHashMap<String, String> mSectionMap = new LinkedHashMap<String, String>();
    private boolean       mOptional;

    /** the ControlFile that this section belongs to (for error-reporting) */
    private ControlFile   mControlFile;

    /** Create a non-optional section */
    SectionData( ControlFile cfile )
    {
        this( cfile, false );
    }

    SectionData( ControlFile cfile, boolean optional )
    {
        mControlFile = cfile;
        mOptional = optional;
    }

    /** copy constructor **/
    SectionData( SectionData other )
    {
        this.mSectionMap.putAll( other.mSectionMap );
        this.mOptional = other.mOptional;
        this.mControlFile = other.mControlFile;
    }

    /**
     * Store a (key, value) pair.
     * @throws ControlFileException on duplicate keys
     */
    void store( final String key, final String value ) throws ControlFileException
    {
        if ( mSectionMap.containsKey( key ) )
            throw mControlFile.syntaxError( "key already exists: " + key );

        mSectionMap.put( key, value );
    }

    /**
     * @return an ordered map from key (String) to value (String), or null if this
     *   section is both optional and empty
     */
    Map<String, String> getSectionMap()
    {
        if ( mOptional && mSectionMap.isEmpty() )
        {
            return null;
        }
        
        return mSectionMap;
    }


    // -- Map methods ---------------------------

    private UnsupportedOperationException immutable()
    {
        return new UnsupportedOperationException( "SectionData is an immutable map" );
    }

    @Override
    public int size()
    {
        return mSectionMap.size();
    }

    @Override
    public void clear()
    {
        throw immutable();
    }

    @Override
    public boolean isEmpty()
    {
        return mSectionMap.isEmpty();
    }

    @Override
    public boolean containsKey( Object key )
    {
        return mSectionMap.containsKey( key );
    }

    @Override
    public boolean containsValue( Object value )
    {
        return mSectionMap.containsValue( value );
    }

    @Override
    public Collection<String> values()
    {
        return mSectionMap.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet()
    {
        return mSectionMap.entrySet();
    }

    @Override
    public Set<String> keySet()
    {
        return mSectionMap.keySet();
    }

    @Override
    public String get( Object key )
    {
        return mSectionMap.get( key );
    }

    @Override
    public String remove( @SuppressWarnings( "unused" ) Object key )
    {
        throw immutable();
    }

    @Override
    public String put( @SuppressWarnings( "unused" ) String key, @SuppressWarnings( "unused" ) String value )
    {
        throw immutable();
    }

    @Override
    public void putAll( @SuppressWarnings( "unused" ) Map<? extends String, ? extends String> m )
    {
        throw immutable();
    }
}
