package com.igottashoot.game.utilities;

import com.igottashoot.game.primitives.BaseObject;

public abstract class ObjectPool<T> extends BaseObject
{
    private final FixedSizeArray<T> mAvailable;
    
    public ObjectPool( int size )
    {
        this( new FixedSizeArray<T>( size ) );
    }
    
    public ObjectPool( FixedSizeArray<T> available )
    {
        mAvailable = available;
        fill( mAvailable );
    }
    
    protected abstract T create();
    
    public T allocate()
    {
        T last = mAvailable.removeLast();
        if ( last == null )
            throw new IllegalStateException();
        
        return last;
    }
    
    public void release( T entry )
    {
        mAvailable.add( entry );
    }
    
    /**
     * Returns the number of pooled elements that have been allocated but not
     * released.
     */
    public int getAllocatedCount()
    {
        return mAvailable.getCapacity() - mAvailable.getCount();
    }

    private void fill( final FixedSizeArray<T> available )
    {
        for ( int x = 0; x < getSize(); x++ )
            available.add( create() );
    }
    
    private int getSize()
    {
        return mAvailable.getCapacity();
    }    
}
