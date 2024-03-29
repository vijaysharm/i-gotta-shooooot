package com.igottashoot.game.utilities;

import java.util.Arrays;
import java.util.Comparator;

import com.igottashoot.game.primitives.BaseObject;

public class FixedSizeArray<T> extends BaseObject
{
    private final static int LINEAR_SEARCH_CUTOFF = 16;
    
    private final T[]        mContents;

    private Sorter<T>        mSorter;
    private int              mCount;
    private boolean          mSorted;
    private Comparator<T> mComparator;

    public static <T> FixedSizeArray<T> newArray( int size )
    {
        return new FixedSizeArray<T>( size );
    }
    
    public FixedSizeArray( int size )
    {
        this( size, null );
    }

    public FixedSizeArray( int size, Comparator<T> comparator )
    {
        this( size, comparator, Sorter.<T>createStandardSorter() );
    }
    
    private FixedSizeArray( int size, Comparator<T> comparator, Sorter<T> sorter )
    {
        mContents = (T[]) new Object[ size ];
        mCount = 0;
        mSorted = false;
        mComparator = comparator;

        mSorter = sorter;
    }

    public void setComparator( Comparator<T> comparator )
    {
        mComparator = comparator;
    }
    
    /**
     * Inserts a new object into the array. If the array is full, an assert is
     * thrown and the object is ignored.
     */
    public final void add( T object )
    {
        if ( mCount < mContents.length )
        {
            mContents[ mCount ] = object;
            mSorted = false;
            mCount++;
        }
        else
            throw new IllegalStateException( "Array exhausted!" );
    }

    /**
     * Searches for an object and removes it from the array if it is found.
     * Other indexes in the array are shifted up to fill the space left by the
     * removed object. Note that if ignoreComparator is set to true, a linear
     * search of object references will be performed. Otherwise, the comparator
     * set on this array (if any) will be used to find the object.
     */
    public void remove( T object, boolean ignoreComparator )
    {
        final int index = find( object, ignoreComparator );

        if ( index != -1 )
            remove( index );
    }

    /**
     * Removes the specified index from the array. Subsequent entries in the
     * array are shifted up to fill the space.
     */
    public void remove( int index )
    {
        if ( index < mCount )
        {
            for ( int x = index; x < mCount; x++ )
            {
                if ( x + 1 < mContents.length && x + 1 < mCount )
                    mContents[ x ] = mContents[ x + 1 ];
                else
                    mContents[ x ] = null;
            }

            mCount--;
        }
        else
            throw new IllegalStateException( "index >= mCount" );
            
    }

    /**
     * Removes the last element in the array and returns it. This method is
     * faster than calling remove(count -1);
     * 
     * @return The contents of the last element in the array.
     */
    public T removeLast()
    {
        T object = null;
        if ( mCount > 0 )
        {
            object = mContents[ mCount - 1 ];
            mContents[ mCount - 1 ] = null;
            mCount--;
        }
        
        return object;
    }

    /**
     * Swaps the element at the passed index with the element at the end of the
     * array. When followed by removeLast(), this is useful for quickly removing
     * array elements.
     */
    public void swapWithLast( int index )
    {
        if ( mCount > 0 && index < mCount - 1 )
        {
            T object = mContents[ mCount - 1 ];
            mContents[ mCount - 1 ] = mContents[ index ];
            mContents[ index ] = object;
            mSorted = false;
        }
    }

    /**
     * Sets the value of a specific index in the array. An object must have
     * already been added to the array at that index for this command to
     * complete.
     */
    public void set( int index, T object )
    {
        if ( index < mCount )
            mContents[ index ] = object;
        else
            throw new IllegalStateException( "index >= count" );
    }

    /**
     * Clears the contents of the array, releasing all references to objects it
     * contains and setting its count to zero.
     */
    public void clear()
    {
        for ( int x = 0; x < mCount; x++ )
            mContents[ x ] = null;

        mCount = 0;
        mSorted = false;
    }

    /**
     * Returns an entry from the array at the specified index.
     */
    public T get( int index )
    {
        T result = null;

        if ( index < mCount && index >= 0 )
            result = mContents[ index ];
        else
            throw new IllegalStateException( "index >= count" );

        return result;
    }

    /**
     * Returns the raw internal array. Exposed here so that tight loops can
     * cache this array and walk it without the overhead of repeated function
     * calls. Beware that changing this array can leave FixedSizeArray in an
     * undefined state, so this function is potentially dangerous and should be
     * used in read-only cases.
     * 
     * @return The internal storage array.
     */
//    public final T[] getArray()
//    {
//        return mContents;
//    }

    /**
     * Searches the array for the specified object. If the array has been sorted
     * with sort(), and if no other order-changing events have occurred since
     * the sort (e.g. add()), a binary search will be performed. If a comparator
     * has been specified with setComparator(), it will be used to perform the
     * search. If not, the default comparator for the object type will be used.
     * If the array is unsorted, a linear search is performed. Note that if
     * ignoreComparator is set to true, a linear search of object references
     * will be performed. Otherwise, the comparator set on this array (if any)
     * will be used to find the object.
     * 
     * @param object
     *            The object to search for.
     * @return The index of the object in the array, or -1 if the object is not
     *         found.
     */
    public int find( T object, boolean ignoreComparator )
    {
        int index = -1;
        final int count = mCount;
        final boolean sorted = mSorted;
        final Comparator<T> comparator = mComparator;
        final T[] contents = mContents;
        if ( sorted && !ignoreComparator && count > LINEAR_SEARCH_CUTOFF )
        {
            if ( comparator != null )
                index = Arrays.binarySearch( contents, object, comparator );
            else
                index = Arrays.binarySearch( contents, object );
            
            // Arrays.binarySearch() returns a negative insertion index if the
            // object isn't found,
            // but we just want a boolean.
            if ( index < 0 )
                index = -1;
        }
        else
        {
            // unsorted, linear search
            if ( comparator != null && !ignoreComparator )
            {
                for ( int x = 0; x < count; x++ )
                {
                    final int result = comparator.compare( contents[ x ], object );
                    if ( result == 0 )
                    {
                        index = x;
                        break;
                    }
                    else if ( result > 0 && sorted )
                    {
                        // we've passed the object, early out
                        break;
                    }
                }
            }
            else
            {
                for ( int x = 0; x < count; x++ )
                {
                    if ( contents[ x ] == object )
                    {
                        index = x;
                        break;
                    }
                }
            }
        }

        return index;
    }

    /**
     * Sorts the array. If the array is already sorted, no work will be
     * performed unless the forceResort parameter is set to true. If a
     * comparator has been specified with setComparator(), it will be used for
     * the sort; otherwise the object's natural ordering will be used.
     * 
     * @param forceResort
     *            If set to true, the array will be resorted even if the order
     *            of the objects in the array has not changed since the last
     *            sort.
     */
    public void sort( boolean forceResort )
    {
        if ( ! mSorted || forceResort )
        {
            if ( mComparator != null )
                mSorter.sort( mContents, mCount, mComparator );
            else
                Arrays.sort( mContents, 0, mCount );

            mSorted = true;
        }
    }

    /** 
     * Returns the number of objects in the array.
     * */
    public int getCount()
    {
        return mCount;
    }

    /**
     * Returns the maximum number of objects that can be inserted inot this
     * array.
     */
    public int getCapacity()
    {
        return mContents.length;
    }
}