package com.igottashoot.game.controlfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Intelerad's standard class for reading configuration files.
 *
 * Implements Map as a read-only, order-preserving mapping from section name
 * (String) to SectionData. (SectionData is in turn a read-only,
 * order-preserving Map from key (String) to value (String)).
 */
public class ControlFile implements Map<String, SectionData>
{
    private static final Pattern NORMAL_SECTION_RE = Pattern.compile( "^\\s*\\[(.+?)\\]\\s*$" );
    private static final String KEY_DELIMITER = "=";

    private static final List<String> BOOLEAN_TRUE = Arrays.asList( new String[] { "true", "yes", "on" } );
    private static final List<String> BOOLEAN_FALSE = Arrays.asList( new String[] { "false", "no", "off" } );

    private static final Object VALIDATION_FAILED = new Object();

    // Used by putValidValue() and getValidValue().
    private static Map<Class<?>, Class<?>> VALID_TYPES = new HashMap<Class<?>, Class<?>>();
    static
    {
        VALID_TYPES.put( Integer.class, Integer.TYPE );
        VALID_TYPES.put( Boolean.class, Boolean.TYPE );
        VALID_TYPES.put( Double.class, Double.TYPE );
    }

    private final String mFilename;
    private final Map<String, SectionData> mSectionMap;
    private final String mDefaultSection = "UNNAMED";
    
    /** 
     * Map from section name to Map; the inner Map is from key name to Object.
     * Presence of a key in the inner map indicates that the key has been validated:
     * if the value there is a String, Integer, Long, or Double, then the value was
     * successfully validated as the given type; if it's VALIDATION_FAILED, then
     * the key was not successfully validated. 
     */
    private Map<String, Map<String,Object>> mValidValues = new HashMap<String, Map<String,Object>>();
    private String mActiveSection = "";
    private long mLastModified;
    private int mLineNumber = -1;

    public ControlFile( final String filename ) throws ControlFileException
    {
        mFilename   = filename;
        mSectionMap = new LinkedHashMap<String, SectionData>();
        mLastModified = getLastModified();
    }

    public String getFilename()
    {
        return mFilename;
    }
    
    public boolean isFileModified()
    {
        return ( mLastModified != getLastModified() );
    }

    /**
     * @return the value of the key contained in the section. null if the
     *         section or the key don't exist.
     */
    public String getValue( final String section,
                            final String key )
    {
        SectionData sectionData = mSectionMap.get( section );
        if ( sectionData == null )
            return null;

        Map<String, String> sectionHash = sectionData.getSectionMap();
        if ( sectionHash == null )
            return null;

        return sectionHash.get( key );
    } // GetValue

    public List<String> getValueArray( final String section,
                                       final String key,
                                       final String delimiter )
    {
        final String value = getValue( section, key );
        if ( value == null )
            return null;

        return StringUtils.split( value, delimiter, true, true );
    }
    
    public int getIntValue( final String section, final String key )
    {
        String value = getValue( section, key );
        try
        {
            return Integer.parseInt( value );
        }
        catch ( NumberFormatException e )
        {
            throw createFormatException( section, key, "integer", value );
        }
    }

    public long getLongValue( final String section,
                              final String key )
    {
        String value = getValue( section, key );
        try
        {
            return Long.parseLong( value );
        }
        catch ( NumberFormatException e )
        {
            throw createFormatException( section, key, "long integer", value );
        }
    }
    
    /**
     * Return an integer key.  Integer keys must be decimal strings, since
     * they are parsed with "new Integer()".
     * 
     * @throw IllegalStateException if (section, key) has not been successfully 
     *   validated as an integer key by calling requireInt() 
     */
    public int getInt( String section, String key )
    {
        return getValidValue( section, key, Integer.class ).intValue();
    }

    /**
     * Return a boolean key.  Boolean keys must be one of the strings listed in
     * BOOLEAN_TRUE or BOOLEAN_FALSE (case insensitive). 
     *
     * @throw IllegalStateException if (section, key) has not been successfully 
     *   validated as a boolean key by calling requireBoolean() 
     */
    public boolean getBoolean( String section, String key )
    {
        return getValidValue( section, key, Boolean.class ).booleanValue();
    }
    
    /**
     * Return a double-precision floating-point key.  Double keys must be strings 
     * that can be parsed with "new Double()".
     * 
     * @throw IllegalStateException if (section, key) has not been successfully 
     *   validated as a double key by calling requireDouble() 
     */
    public double getDouble( String section, String key )
    {
        return getValidValue( section, key, Double.class ).doubleValue();
    }

    /**
     * Return the specified key after parsing it as an integer, or <code>defaultValue</code>
     * if the key does not exist or cannot be parsed as an integer.
     */
    public int getInt( String section, String key, int defaultValue )
    {
        try
        {
            return Integer.parseInt( getValue( section, key ) );
        }
        catch ( NumberFormatException err )
        {
            return defaultValue;
        }
    }

    public String getAndTestStringValue( final String section,
                                         final String key ) throws ControlFileException
    {
        requireKeysSet( section, new String[] { key } );
        return getStringValue( section, key );
    }

    public String getStringValue( final String section, final String key )
    {
        return getValue( section, key );
    }

    public boolean getBooleanValue( final String section, final String key )
    {
        String value = getValue( section, key );
        if ( value == null )
            throw createFormatException( section, key, "boolean", value );

        value = value.toLowerCase();
        if ( BOOLEAN_TRUE.contains( value ) )
            return true;
        else if ( BOOLEAN_FALSE.contains( value ) )
            return false;
        else
            throw createFormatException( section, key, "boolean", value );
    }

    public double getDoubleValue( final String section, final String key )
    {
        String type = "floating-point";
        String value = getValue( section, key );
        try
        {
            if ( value == null )
                throw createFormatException( section, key, type, value );
            return Double.parseDouble( value );
        }
        catch ( NumberFormatException e )
        {
            throw createFormatException( section, key, type, value );
        }
    }

    public Hashtable<String, Map<String, String>> getSections()
    {
        final Map<String, Map<String,String>> map = getSectionsOrdered();
        if ( map == null )
            return null;

        return new Hashtable<String, Map<String, String>>( getSectionsOrdered() );
    }

    public Map<String, Map<String, String>> getSectionsOrdered()
    {
        // Build a new Hashtable that consists of the (string, Hashtable).  This is being done
        // because the interface needs to stay the same, but internally the ControlFile object
        // consists of (string, SectionData)
        Map<String, Map<String, String>> oldInterfaceHash = new LinkedHashMap<String, Map<String, String>>();
        Map<String, String> pSectionHash = null;

        Iterator<String> it = mSectionMap.keySet().iterator();
        while ( it.hasNext() )
        {
            String sectionName = it.next();

            pSectionHash = getSectionOrdered( sectionName );
            if ( pSectionHash != null )
            {
                oldInterfaceHash.put( sectionName, pSectionHash );
            }
        }

        return oldInterfaceHash;
    }

    public Hashtable<String, String> getSection( final String section )
    {
        final Map<String, String> map = getSectionOrdered( section );
        if ( map == null )
            return null;

        return new Hashtable<String, String>( getSectionOrdered( section ) );
    }

    public Map<String, String> getSectionOrdered( final String section )
    {
        SectionData sectionData = mSectionMap.get( section );

        if ( sectionData == null )
            return null;

        return sectionData.getSectionMap();
    }

    /**
     * @return a copy of the SectionData object for sectionName, or null
     *   if no such section
     */
    public SectionData getSectionData( final String sectionName )
    {
        SectionData section = mSectionMap.get( sectionName );
        if ( section == null )
        {
            return null;
        }
        
        return new SectionData( section );
    }

    /**
     * This method reads in keys with the following format: 
     * value1 = server1,server2, server3 
     * value2 = server4, server5 
     * if the given exception (server1, server2) is found 
     * then that value is returned (value1)
     */
    public String getValueForException( final String section,
                                        final String exception )
    {
        try
        {
            return getAndTestValueForException( section, exception );
        }
        catch( NoSuchFieldException nsfe )
        {
            return null;
        }
    }

    public String getAndTestValueForException( final String section,
                                               final String exception ) throws NoSuchFieldException
    {
        Map<String, String> thisSection = getSectionOrdered( section );
        if ( thisSection == null )
        {
            throw new NoSuchFieldException( "in file (" + mFilename + ") section [" + section + "] does not exist." );
        }

        Iterator<String> it = thisSection.keySet().iterator();
        while ( it.hasNext() )
        {
            final String key = it.next();
            Set<String> availableValues = getAllValuesAvailable( thisSection.get( key ) );
            if ( availableValues.contains( exception ) )
                return key;
        }
        throw new NoSuchFieldException( "in file (" + mFilename + "), the value (" +
                                        exception + ") is missing from the section [" + section + "]" );
    }

    public List<String> getKeysStartingWith( final String section, final String start )
    {
        List<String> result = new ArrayList<String>();
        final Hashtable<String, String> sectionHash = getSection( section );
        if ( sectionHash == null )
            return null;

        for ( String key : sectionHash.keySet() )
        {
            if ( key.startsWith( start ) )
                result.add( key );
        }
        return result;
    }

    /**
     * @return a vector of section names (String)
     */
    public List<String> getSectionsStartingWith( final String start )
    {
        List<String> result = new ArrayList<String>();
        for ( String section : mSectionMap.keySet() )
        {
            if ( section.startsWith( start ) )
            {
                if ( getSection( section ) != null )
                    result.add( section );
            }
        }
        return result;
    }

    public void read() throws ControlFileException
    {
        BufferedReader reader = null;
        try
        {
            reader = openFile( mFilename );
            parseFile( reader );
        }
        finally
        {
            try { if ( reader != null ) reader.close(); } catch ( IOException ignore ) {}
        }
    }

    private Set<String> getAllValuesAvailable( final String values )
    {
        return StringUtils.setSplit( values, ",", true, false );
    }

    public String[] printMissingKeys( final String section, final String[] required )
    {
        String[] keysMissing = getMissingKeys( section, required );
        if ( keysMissing == null )
        {
            return null;
        }
        
        for ( int i = 0; i < keysMissing.length; i++ )
        {
            System.out.println( "Missing key: [" + section + ":" + keysMissing[i] + "]" );
        }
        
        return keysMissing;
    }

    // Same as GetMissingKeys, but this version also checks if
    // the value is empty, if it is then it's a returned in the
    // array.
    public String[] keysMissingOrEmpty( final String section,
                                        final String[] required )
    {
        Vector<String> missing = new Vector<String>();
        for ( int i = 0; i < required.length; i++ )
        {
            String value = getStringValue( section, required[i] );
            if ( StringUtils.isStringBlank( value ) )
                missing.addElement( required[i] );
        }

        return missing.toArray( new String[0] );
    }

    /** @return a String with the comma-separated list of missing keys */
    public String keysMissing( final String section,
                               final String[] required )
    {
        String[] missing = getMissingKeys( section, required );
        if ( missing == null )
            return "";
        
        return StringUtils.arrayJoin( missing, ", " );
    }

    public String[] getMissingKeys( final String section, final String[] required )
    {
        List<String> missing = new ArrayList<String>();
        for ( int idx = 0; idx < required.length; idx++ )
        {
            if ( getValue( section, required[idx] ) == null )
                missing.add( required[idx] );
        }

        if ( missing.isEmpty() )
        {
            return null;
        }
        
        return missing.toArray( new String[missing.size()] );
    }

    /**
     * Check that the specified section is present.
     *
     * @throws ControlFileDataException if not
     */
    public void requireSection( String sectionName ) throws ControlFileException
    {
        if ( !mSectionMap.containsKey( sectionName ) )
            throw dataError( null, null, "missing required section '" + sectionName + "'" );
    }

    /**
     * Check that all keys listed in <code>keys</code> are present in the named section.
     *
     * @throw ControlFileDataException (with a user-friendly error message) if any of
     *   the listed keys are not present
     */
    public void requireKeys( String sectionName,
                             String[] keys ) throws ControlFileException
    {
          String[] missing = getMissingKeys( sectionName, keys );
          if ( missing != null )
              throw dataError( sectionName, null,  "missing keys: " + StringUtils.arrayJoin( missing, ", " ) );
    }

    /**
     * Check that all keys listed in <code>keys</code> are present in the named
     * section and set to a non-blank value.
     *
     * @throw ControlFileDataException (with a user-friendly error message) if any of
     *   the listed keys are not present or not set
     */
    public void requireKeysSet( String sectionName,
                                String[] keys ) throws ControlFileException
    {
        requireSection( sectionName );
        SectionData section = mSectionMap.get( sectionName );

        List<String> badkeys = new ArrayList<String>();
        for ( int idx = 0; idx < keys.length; idx++ )
        {
            if ( StringUtils.isStringBlank( section.get( keys[idx] ) ) )
                badkeys.add( keys[idx] );
        }

        if ( !badkeys.isEmpty() )
            throw dataError( sectionName, null,  "missing or unset keys: " + StringUtils.arrayJoin( badkeys.toArray(), ", " ) );
    }

    /**
     * Verify that the specified config value can be converted to an integer value, mainly so
     * you can later call getInt() without worrying about errors.  Also happens to 
     * return the integer value in case it's useful.
     *  
     * @throws ControlFileException if the config value cannot be converted to an int 
     */
    public int requireInt( String sectionName, String key ) throws ControlFileException
    {
        requireSection( sectionName );
        SectionData section = mSectionMap.get( sectionName );

        String value = section.get( key );
        try
        {
            // This relies on the fact that "new Integer(null)" throws NumberFormatException
            // rather than NullPointerException.  If that ever changes, the unit tests
            // will fail.
            Integer validValue = new Integer( value );
            putValidValue( sectionName, key, validValue );
            return validValue.intValue();
        }
        catch ( NumberFormatException err )
        {
            putValidValue( sectionName, key, VALIDATION_FAILED );
            throw valueError( sectionName, key, "integer", value );
        }
    }

    /**
     * Verify that the specified config value can be converted to a boolean value,
     * mainly so you can later call getBoolean() without worrying about errors.  Also
     * happens to return the boolean value in case it's useful.
     * 
     * @throws ControlFileException if the config value cannot be converted to a boolean 
     */
    public boolean requireBoolean( String section, String key ) throws ControlFileException
    {
        requireSection( section );
        String value = getValue( section, key );

        ControlFileException error = null;
        Boolean validValue = null;
        if ( value == null )
        {
            error = valueError( section, key, "boolean", null );
        }
        else
        {
            value = value.toLowerCase();
            if ( BOOLEAN_TRUE.contains( value ) )
                validValue = Boolean.TRUE;
            else if ( BOOLEAN_FALSE.contains( value ) )
                validValue = Boolean.FALSE;
            else
                error = valueError( section, key, "boolean", value );
        }
        
        if ( error != null )
        {
            putValidValue( section, key, VALIDATION_FAILED );
            throw error;
        }
        putValidValue( section, key, validValue );
        return validValue.booleanValue();
    }
    
    /**
     * Verify that the specified config value can be converted to a double value, mainly so
     * you can later call getDouble() without worrying about errors.  Also happens to 
     * return the double value in case it's useful.
     *  
     * @throws ControlFileException if the config value cannot be converted to a double 
     */
    public double requireDouble( String sectionName, String key ) throws ControlFileException
    {
        requireSection( sectionName );
        String value = getValue( sectionName, key );
        
        Double validValue = null;
        ControlFileException error = null;
        if ( value == null )
        {
            error = valueError( sectionName, key, "floating-point", value );
        }
        else
        {
            try
            {
                validValue = new Double( value );
            }
            catch ( NumberFormatException err )
            {
                error = valueError( sectionName, key, "floating-point", value );
            }
        }

        if ( error != null )
        {
            putValidValue( sectionName, key, VALIDATION_FAILED );
            throw error;
        }
        
        putValidValue( sectionName, key, validValue );
        return validValue.doubleValue();
    }

    /**
     * Ensure that the value specified by section and key has been successfully 
     * validated for the type specified klass, and returns an Object of type klass.
     * 
     * @throw IllegalStateException if the specified key has not been validated, 
     *   or was validated for a different type, or validation failed
     */
    private <T> T getValidValue( String section, String key, Class<T> klass )
    {
        if ( !VALID_TYPES.containsKey( klass ) )
            throw new IllegalArgumentException( "cannot fetch values of type " + klass + 
                                                " from valid values map" );
        String prefix = "key '" + key + "' (section '" + section + "')";
        Map<String, Object> sectionMap = mValidValues.get( section );
        if ( sectionMap == null )
            throw new IllegalStateException( prefix + " not validated yet" );
        Object value = sectionMap.get( key );
        
        if ( value == null )
            throw new IllegalStateException( prefix + " not validated yet" );
        if ( value == VALIDATION_FAILED )
            throw new IllegalStateException( prefix + " failed validation" );
        
        if ( ! value.getClass().equals( klass ) )
        {
            Class<?> validatedType = VALID_TYPES.get( value.getClass() );
            Class<?> desiredType = VALID_TYPES.get( klass );
            throw new IllegalStateException( prefix + " validated as " + validatedType + 
                                             ", not " + desiredType );
        }

        return klass.cast( value );
    }

    private void putValidValue( String section, String key, Object value )
    {
        if ( !VALID_TYPES.containsKey( value.getClass() ) && value != VALIDATION_FAILED )
            throw new IllegalArgumentException( "cannot put object of type " + value.getClass() +
                                                " in valid values map" );
        Map<String, Object> sectionMap = mValidValues.get( section );
        if ( sectionMap == null )
        {
            sectionMap = new HashMap<String, Object>();
            mValidValues.put( section, sectionMap );
        }

        Object previousValue = sectionMap.get( key );
        if ( previousValue != null &&
             previousValue.getClass() != value.getClass() )
        {
            throw new IllegalStateException( mFilename + ", section '" + section + "': " +
                                             "key '" + key + "' has already been validated " +
                                             "as " + VALID_TYPES.get( previousValue.getClass() ) );
        }
        sectionMap.put( key, value );
    }

    /**
     * Add the supplied SectionData to this ControlFile object.  Handy
     * if you need to fool other code about the contents of some config file.
     *
     * @throws IllegalArgumentException if there's already a section named sectionName
     */
    public void addSection( String sectionName, SectionData sectionData )
    {
        if ( mSectionMap.containsKey( sectionName ) )
            throw new IllegalArgumentException( "already have section [" + sectionName + "]" );
        mSectionMap.put( sectionName, sectionData );
    }

    ControlFileException syntaxError( String message )
    {
        return new ControlFileException( mFilename, mLineNumber, message );
    }

    ControlFileException redirectError( String message )
    {
        return new ControlFileException( mFilename, message );
    }

    public ControlFileException dataError( String section, String key, String message )
    {
        return new ControlFileDataException( mFilename, section, key, message );
    }

    private NumberFormatException createFormatException( String section,
                                                         String key,
                                                         String type,
                                                         String value )
    {
        String message;
        if ( value == null )
            message = ": no such key '" + key + "'";
        else
            message = ", key '" + key + "': invalid " + type + " value '" + value + "'";
        return new NumberFormatException(
            mFilename + ", section '" + section + "'" + message );
    }

    private ControlFileException valueError( String section,
                                             String key,
                                             String type,
                                             String value )
    {
        if ( value == null )
            return new ControlFileDataException( mFilename, section, null, "missing required key '" + key + "'" );
        
        return new ControlFileDataException( mFilename, section, key, "invalid " + type + " value '" + value + "'" );
    }

    private void parseFile( BufferedReader reader ) throws ControlFileException
    {
        String line, key, value;
        mLineNumber = 0;
        while ( ( line = readLine( reader ) ) != null )
        {
            mLineNumber++;

            line = line.trim();

            // try to skip all the lines which start with an '#'
            if ( line.length() > 0 && !line.startsWith( "#" ) )
            {
                line = getRidOfComments( line );

                if ( sectionStarts( line ) )
                    continue;

                int index = line.indexOf( KEY_DELIMITER );
                if ( index == -1 )
                    throw syntaxError( "syntax error: delimiter not found" );

                key   = line.substring( 0, index ).trim();
                value = line.substring( index + 1, line.length() ).trim();

                storeKeyValue( key, value );
            }
        }
    }

    private String readLine( BufferedReader reader ) throws ControlFileIOException
    {
        try
        {
            return reader.readLine();
        }
        catch ( IOException err )
        {
            throw new ControlFileIOException( mFilename, "error reading from file", err );
        }
    }

    private long getLastModified()
    {
        File f = new File( mFilename );
        return f.lastModified();
    }

    private BufferedReader openFile( String filename ) throws ControlFileIOException
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader( new FileReader( filename ) );
        }
        catch ( FileNotFoundException err )
        {
            throw new ControlFileIOException( mFilename, "file not found", err );
        }
        
        return reader;
    }

    private void storeKeyValue( final String key,
                                final String value ) throws ControlFileException
    {
        if ( mActiveSection.equals( "" ) )
        {
            // This means this control file doesn't have sections.
            // Use the mDefaultSection section name then for these key/value pairs.
            mSectionMap.put( mDefaultSection, new SectionData( this ) );
            mActiveSection = mDefaultSection;
        }

        SectionData section = mSectionMap.get( mActiveSection );
        if ( section == null )
            return;

        section.store( key, value );
    }

    private void storeActiveSection( final String activeSection,
                                     final SectionData sectionData )
    {
        mActiveSection = activeSection;
        if ( mSectionMap.containsKey( mActiveSection ) )
        {
            System.err.println( "Duplicated section found while parsing control file: " +
                                mFilename + ", section is: " + mActiveSection );
        }
        mSectionMap.put( mActiveSection, sectionData );
    }

    private boolean sectionStarts( String line )
    {
        line = getRidOfComments( line );
        
        final Pattern regexRegularSection = NORMAL_SECTION_RE;
        final Matcher regexRegularSectionMatcher = regexRegularSection.matcher( line );
        if ( regexRegularSectionMatcher.find() )
        {
            storeActiveSection( regexRegularSectionMatcher.group( 1 ), new SectionData( this ) );
            return true;
        }
        
        // Not a section
        return false;
    }

    public static String getRidOfComments( final String line )
    {
        String result = line.trim();
        final int index = result.indexOf( '#' );
        if ( index != -1 )
        {
            result = result.substring( 0, index );
            result = result.trim();
        }
        return result;
    }
    
    @Override
    public String toString()
    {
        return toString( true );
    }

    /**
     * @param showKeysWithEmptyValue
     *            false if you don't want to print keys that have an
     *            empty value. true if you to print everything.
     * @return the control file content expressed as a String
     */
    public String toString( boolean showKeysWithEmptyValue )
    {
        StringBuffer result = new StringBuffer();

        // get all sections
        Iterator<String> it = this.getSections().keySet().iterator();
        while ( it.hasNext() )
        {
            String sectionName = it.next();
            result.append( "[" + sectionName + "]\n" );
            Iterator<String> it2 = this.getSectionOrdered( sectionName ).keySet().iterator();
            while ( it2.hasNext() )
            {
                String key = it2.next();
                String value = getStringValue( sectionName, key );
                
                if ( StringUtils.isStringBlank( value ) && ! showKeysWithEmptyValue )
                    continue;
                
                result.append( key + " = " + value + "\n" );
            }
        }

        return result.toString();
    }
    
    /**
     * Does essentially the same thing as toString() but in a modern way and introduces extra
     * characters to make it more readable in the logs.
     * 
     * @param title
     *            the title of the control file.
     * @param showKeysWithEmptyValue
     *            false to print keys that have an empty value. 
     *            true to print everything regardless.
     * @return the control file content expressed as a String formatted for logging purposes
     */
    public String logString( String title, boolean showKeysWithEmptyValue )
    {
        StringBuilder result = new StringBuilder();
        
        result.append( "\r\n ___________________[" + title +  
                       " CONTROL FILE]___________________\r\n" );
        
        for ( String sectionName : getSectionsOrdered().keySet() )
        {
            result.append( "|\r\n|  __________[" + sectionName + "]__________\r\n" );
            
            for ( String key : getSectionOrdered( sectionName ).keySet() )
            {
                String value = getStringValue( sectionName, key );
                if ( StringUtils.isStringBlank( value ) && ! showKeysWithEmptyValue )
                    continue;
                
                result.append( "| | " + key + " = " + value + "\r\n" );
            }
            result.append( "| |__________\r\n" );
        }
        
        result.append( "|__________________________________________________" );

        return result.toString();
    }

    // -- Map methods -------------------------------------------------------------------

    private UnsupportedOperationException immutable()
    {
        return new UnsupportedOperationException( "ControlFile is an immutable map" );
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
    public boolean containsKey( Object section )
    {
        return mSectionMap.containsKey( section );
    }

    /**
     * @return true if the section exists and that the key exists in the section
     */
    public boolean containsKey( String section, String key )
    {
        return ( getValue( section, key ) != null );
    }
    
    @Override    
    public boolean containsValue( Object value )
    {
        return mSectionMap.containsValue( value );
    }

    @Override
    public Collection<SectionData> values()
    {
        return mSectionMap.values();
    }

    @Override
    public Set<Map.Entry<String, SectionData>> entrySet()
    {
        return mSectionMap.entrySet();
    }

    @Override
    public Set<String> keySet()
    {
        return mSectionMap.keySet();
    }

    @Override
    public void putAll( @SuppressWarnings( "unused" ) Map<? extends String, ? extends SectionData> m )
    {
        throw immutable();
    }

    @Override
    public SectionData get( Object key )
    {
        return mSectionMap.get( key );
    }

    @Override
    public SectionData put( @SuppressWarnings( "unused" ) String key,
                            @SuppressWarnings( "unused" ) SectionData value )
    {
        throw immutable();
    }

    @Override
    public SectionData remove( @SuppressWarnings( "unused" ) Object key )
    {
        throw immutable();
    }

}
