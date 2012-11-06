package com.igottashoot.game.managers;

import com.igottashoot.game.primitives.ResettableObject;
import com.igottashoot.game.primitives.UpdateableObject;
import com.igottashoot.game.utilities.FixedSizeArray;

class ManagerUtilities
{
    static <K, T extends UpdateableObject<K>> void update( FixedSizeArray<T> objects, float timeDelta, K baseObject )
    {
        final int size = objects.getCount();
        for ( int index = 0; index < size; index++ )
            objects.get( index ).update( timeDelta, baseObject );        
    }
    
    static <T extends ResettableObject> void resetObjects( FixedSizeArray<T> objects )
    {
        final int size = objects.getCount();
        for ( int index = 0; index < size; index++ )
            objects.get( index ).reset();        
    }
    
    static <T> void commitUpdates( FixedSizeArray<T> objects, FixedSizeArray<T> pendingAdditions, FixedSizeArray<T> pendingRemovals )
    {
        final int additionCount = pendingAdditions.getCount();
        if ( additionCount > 0 )
        {
            for ( int index = 0; index < additionCount; index++ )
                objects.add( pendingAdditions.get( index ) );

            pendingAdditions.clear();
        }

        final int removalCount = pendingRemovals.getCount();
        if ( removalCount > 0 )
        {
            for ( int index = 0; index < removalCount; index++ )
                objects.remove( pendingRemovals.get( index ), true );

            pendingRemovals.clear();
        }        
    }
    
    static <T> void removeAll( FixedSizeArray<T> objects, FixedSizeArray<T> pendingAdditions, FixedSizeArray<T> pendingRemovals )
    {
        final int size = objects.getCount();
        for ( int index = 0; index < size; index++ )
            pendingRemovals.add( objects.get( index ) );

        pendingAdditions.clear();        
    }
    
    static <T> int getConcreteCount( FixedSizeArray<T> objects, FixedSizeArray<T> pendingAdditions, FixedSizeArray<T> pendingRemovals )
    {
        return objects.getCount() + pendingAdditions.getCount() - pendingRemovals.getCount();
    }
}
