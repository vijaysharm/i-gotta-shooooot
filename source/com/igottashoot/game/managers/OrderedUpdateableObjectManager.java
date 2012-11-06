package com.igottashoot.game.managers;

import java.util.Comparator;

import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.OrderedObject;
import com.igottashoot.game.primitives.OrderedUpdateableObject;
import com.igottashoot.game.primitives.UpdateableObject;
import com.igottashoot.game.utilities.FixedSizeArray;

public class OrderedUpdateableObjectManager<K, T extends OrderedUpdateableObject<K>> extends BaseObject implements UpdateableObject<K>, ObjectManager<T>
{
    private boolean mIsDirty;
    private final FixedSizeArray<T> mObjects;
    private final FixedSizeArray<T> mPendingAdditions;
    private final FixedSizeArray<T> mPendingRemovals;
    
    public static <K, T extends OrderedUpdateableObject<K>> OrderedUpdateableObjectManager<K,T> newManager( int size )
    {
        FixedSizeArray<T> objects = FixedSizeArray.newArray( size );
        FixedSizeArray<T> pendingAdditions = FixedSizeArray.newArray( size );
        FixedSizeArray<T> pendingRemovals = FixedSizeArray.newArray( size );
        return new OrderedUpdateableObjectManager<K,T>( objects, pendingAdditions, pendingRemovals );
    }
    
    private OrderedUpdateableObjectManager( FixedSizeArray<T> objects,
                                            FixedSizeArray<T> pendingAdditions,
                                            FixedSizeArray<T> pendingRemovals )
    {
        mObjects = objects;
        mPendingAdditions = pendingAdditions;
        mPendingRemovals = pendingRemovals;
        
        Comparator<T> compartor = new OrderedObjectComparator<T>();
        mObjects.setComparator( compartor );
        mPendingAdditions.setComparator( compartor );
        
        mIsDirty = false;
    }
    
    @Override
    public void update( float timeDelta, K parent )
    {
        commitUpdates();
        ManagerUtilities.update( mObjects, timeDelta, parent );        
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
        if ( mIsDirty )
        {
            ManagerUtilities.commitUpdates( mObjects, mPendingAdditions, mPendingRemovals );
            mObjects.sort( true );
            mIsDirty = false;
        }
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
        mIsDirty = true;
    }

    @Override
    public void remove( T object )
    {
        mPendingRemovals.add( object );
        mIsDirty = true;
    }
    
    @Override
    public void removeAll()
    {
        ManagerUtilities.removeAll( mObjects, mPendingAdditions, mPendingRemovals );
        mIsDirty = true;
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
            OrderedObject currentObject = mObjects.get( index );
            if ( currentObject.getClass() == classObject )
            {
                object = classObject.cast( currentObject );
                break;
            }
        }
        return object;
    }
    
    private static final class OrderedObjectComparator<T extends OrderedObject> implements Comparator<T>
    {
        @Override
        public int compare( OrderedObject object1, OrderedObject object2 )
        {
            int result = 0;
            
            if (object1 != null && object2 != null)
                result =  object1.getPriority() - object2.getPriority();
            else if (object1 == null && object2 != null)
                result = 1;
            else if (object2 == null && object1 != null)
                result = -1;
            
            return result;
        }
    }
}
