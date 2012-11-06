package com.igottashoot.game.managers;

import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.UpdateableObject;
import com.igottashoot.game.utilities.FixedSizeArray;

public final class UpdateableObjectManager<K, T extends UpdateableObject<K>> extends BaseObject implements UpdateableObject<K>, ObjectManager<T>
{
    private final FixedSizeArray<T> mObjects;
    private final FixedSizeArray<T> mPendingAdditions;
    private final FixedSizeArray<T> mPendingRemovals;

    public static <K, T extends UpdateableObject<K>> UpdateableObjectManager<K,T> newManager( int size )
    {
        FixedSizeArray<T> objects = FixedSizeArray.newArray( size );
        FixedSizeArray<T> pendingAdditions = FixedSizeArray.newArray( size );
        FixedSizeArray<T> pendingRemovals = FixedSizeArray.newArray( size );
        return new UpdateableObjectManager<K,T>( objects, pendingAdditions, pendingRemovals );
    }
    
    private UpdateableObjectManager( FixedSizeArray<T> objects,
                                     FixedSizeArray<T> pendingAdditions,
                                     FixedSizeArray<T> pendingRemovals )
    {
        mObjects = objects;
        mPendingAdditions = pendingAdditions;
        mPendingRemovals = pendingRemovals;
    }
    
    @Override
    public void update( float timeDelta, K baseObject )
    {
        commitUpdates();
        ManagerUtilities.update( mObjects, timeDelta, baseObject );
    }

    @Override
    public void reset()
    {
        commitUpdates();
        ManagerUtilities.resetObjects( mObjects );
    }
    
    @Override
    public void commitUpdates()
    {
        ManagerUtilities.commitUpdates( mObjects, mPendingAdditions, mPendingRemovals );
    }

    @Override
    public final FixedSizeArray<T> getObjects()
    {
        return mObjects;
    }
    
    @Override
    public final int getCount()
    {
        return mObjects.getCount();
    }
    
    /** Returns the count after the next commitUpdates() is called. */
    @Override
    public final int getConcreteCount()
    {
        return ManagerUtilities.getConcreteCount( mObjects, mPendingAdditions, mPendingRemovals );
    }
    
    public final T get( int index )
    {
        return mObjects.get( index );
    }

    @Override
    public void add( T object )
    {
        mPendingAdditions.add( object );
    }

    @Override
    public void remove( T object )
    {
        mPendingRemovals.add( object );
    }
    
    @Override
    public void removeAll()
    {
        ManagerUtilities.removeAll( mObjects, mPendingAdditions, mPendingRemovals );
    }

    /** 
     * Finds a child object by its type.  Note that this may invoke the class loader and therefore
     * may be slow.
     * @param classObject The class type to search for (e.g. BaseObject.class).
     * @return
     */
    public T findByClass( Class<T> classObject )
    {
        T object = null;
        final int size = mObjects.getCount();
        for ( int index = 0; index < size; index++ )
        {
            UpdateableObject<K> currentObject = mObjects.get( index );
            if ( currentObject.getClass() == classObject )
            {
                object = classObject.cast( currentObject );
                break;
            }
        }
        return object;
    }
}
