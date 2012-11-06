package com.igottashoot.game.controlfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringUtils
{
    /**
     * Split 'input' on 'separator', stripping empty elements but not trimming
     * whitespace.
     */
    public static List<String> split( final String input,
                                      final String separator )
    {
        return split( input,
                      separator,
                      true,
                      false );
    }

    /**
     * Split 'input' on 'separator' without trimming whitespace, optionally
     * stripping empty elements.
     */
    public static List<String> split( final String input,
                                      final String separator,
                                      final boolean stripEmpty )
    {
        return split( input,
                      separator,
                      stripEmpty,
                      false );
    }

    /**
     * Split 'input' on 'separator', optionally trimming whitespace and
     * stripping empty elements. (Whitespace is trimmed *before* stripping empty
     * elements.)
     */
    public static List<String> split( final String input,
                                      final String separator,
                                      final boolean stripEmpty,
                                      final boolean trim )
    {
        List<String> result = new ArrayList<String>();
        tokenize( input,
                  separator,
                  stripEmpty,
                  trim,
                  result );
        return result;
    }

    /**
     * Split 'input' on 'separator' without trimming whitespace or stripping
     * empty elements. Return a Set of the unique sub-strings in 'input'.
     */
    public static Set<String> setSplit( final String input,
                                        final String separator )
    {
        return setSplit( input,
                         separator,
                         false,
                         false );
    }

    /**
     * Split 'input' on 'separator', optionally trimming whitespace and/or
     * stripping empty elements. Return a Set of the unique sub-strings in
     * 'input'.
     */
    public static Set<String> setSplit( final String input,
                                        final String separator,
                                        final boolean stripEmpty,
                                        final boolean trim )
    {
        Set<String> result = new HashSet<String>();
        tokenize( input,
                  separator,
                  stripEmpty,
                  trim,
                  result );
        return result;
    }

    /**
     * Split a string into a List of equal-length Strings. The last item in the
     * List can be shorter, depending on the length of the input. For the empty
     * string, an empty List is returned.
     * 
     * @param input
     *            String to split
     * @param maximumSplitLength
     *            - maximum length for split Strings > 0
     * @return List of Strings each with length <= maximumSplitLength
     */
    public static List<String> splitFixedLength( String input,
                                                 int maximumSplitLength )
    {
        if ( maximumSplitLength <= 0 )
            throw new IllegalArgumentException( "Cannot split into zero-length strings" );

        if ( isStringBlank( input ) )
            return Collections.emptyList();

        List<String> result = new ArrayList<String>( ( input.length() - 1 ) / maximumSplitLength + 1 );
        for ( int currentIndex = 0; currentIndex < input.length(); currentIndex += maximumSplitLength )
        {
            int last = currentIndex + maximumSplitLength;
            if ( last > input.length() )
                last = input.length();
            
            result.add( input.substring( currentIndex, last ) );
        }

        return result;
    }

    private static void tokenize( final String input,
                                  final String separator,
                                  final boolean stripEmpty,
                                  final boolean trim,
                                  Collection<String> target )
    {
        if ( input == null || ( input.trim() ).length() == 0 )
            return;
        if ( separator == null || separator.length() == 0 )
            throw new IllegalArgumentException( "split delimiter must not be empty or null" );

        int index = 0;
        int num;
        while ( ( num = input.indexOf( separator, index ) ) != -1 )
        {
            if ( index != num )
            {
                String val = input.substring( index, num );

                if ( trim )
                    val = val.trim();

                if ( val.length() > 0 || !stripEmpty )
                    target.add( val );
            }
            else if ( !stripEmpty )
            {
                target.add( "" );
            }

            index = num + separator.length();
        }

        num = input.length();
        if ( num >= index )
        {
            String val;
            if ( trim )
                val = input.substring( index, num ).trim();
            else
                val = input.substring( index, num );

            if ( val.length() > 0 || !stripEmpty )
                target.add( val );
        }
    }
    
    /**
     * @return true if string is null or empty (length zero)
     */
    private static boolean isStringEmpty( final String string )
    {
        return ( string == null || string.length() == 0 );
    }

    /**
     * @return true if string is null, empty, or contains only whitespace
     */
    public static boolean isStringBlank( final String string )
    {
        if ( isStringEmpty( string ) )
            return true;

        for ( int i = 0; i < string.length(); i++ )
        {
            if ( ( string.charAt( i ) != ' ' ) && 
                 ( string.charAt( i ) != '\t' ) )
                return false;
        }
        return true;
    }
    
    public static String arrayJoin( final Object input[],
                                    final String separator )
    {
        return arrayJoin( input, separator, 0, input.length );
    } // arrayJoin

    public static String arrayJoin( final Object input[],
                                    final String separator,
                                    final int offset,
                                    final int count )
    {
        StringBuffer joined = new StringBuffer( count * 10 );
        int max = offset + count;
        for ( int i = offset; i < max; i++ )
        {
            joined.append( input[i] );
            if ( i < max - 1 )
                joined.append( separator );
        }
        return joined.toString();
    } // arrayJoin    
}
