package com.igottashoot.game.core;

import com.igottashoot.game.collision.BoundingVolume;
import com.igottashoot.game.collision.CollisionData;
import com.igottashoot.game.collision.CollisionDetection;
import com.igottashoot.game.collision.CollisionHandler;
import com.igottashoot.game.core.MainLoop.MainLoopOrder;
import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.OrderedUpdateableObject;
import com.igottashoot.game.utilities.FixedSizeArray;
import com.igottashoot.game.utilities.ObjectPool;

public class GameCollisionHandler extends BaseObject implements OrderedUpdateableObject<SystemRegistry>
{
    private final CollisionVolumeRecordPool mRecordPool;
    private final FixedSizeArray<CollisionVolumeRecord> mSortedRecordObjects;
    
    public GameCollisionHandler( int maxSize )
    {
        mRecordPool = new CollisionVolumeRecordPool( maxSize );
        mSortedRecordObjects = FixedSizeArray.newArray( maxSize );
    }
    
    @Override
    public void reset()
    {
        for ( int index = 0; index < mSortedRecordObjects.getCount(); index++ )
            mRecordPool.release( mSortedRecordObjects.get( index ) );
        
        mSortedRecordObjects.clear();
    }
    
    public void registerCollisionObject( GameObject object,
                                         BoundingVolume boundingVolume,
                                         CollisionHandler collisionHandler )
    {
        if ( boundingVolume == null )
            return;
        
        if ( object == null )
            return;
        
        CollisionVolumeRecord record = mRecordPool.allocate();
        if ( record == null )
            throw new IllegalStateException();
        
        record.setCollisionHandler( collisionHandler );
        record.setBoundingVolume( boundingVolume );
        record.setObject( object );
        mSortedRecordObjects.add( record );
    }
    
    @Override
    public void update( float timeDelta, SystemRegistry parent )
    {
//        mSortedRecordObjects.sort( false );
        
        for ( int index = 0; index < mSortedRecordObjects.getCount(); index++ )
        {
            CollisionVolumeRecord collisionRecord = mSortedRecordObjects.get( index );
            
            for ( int secondIndex = index + 1; secondIndex < mSortedRecordObjects.getCount(); secondIndex++ )
            {
                CollisionVolumeRecord compareRecord = mSortedRecordObjects.get( secondIndex );
                
                CollisionData collision = CollisionDetection.intersection( collisionRecord.getBoundingVolume(),
                                                                           compareRecord.getBoundingVolume() );
                
                if ( collision == null )
                    continue;
                
                if ( collisionRecord.getCollisionHandler() != null )
                    collisionRecord.getCollisionHandler().handleCollisionWith( collisionRecord.getObject(), compareRecord.getObject(), collision );
//                if ( compareRecord.getCollisionHandler() != null )
//                    compareRecord.getCollisionHandler().handleCollisionFrom( compareRecord.getObject(), collisionRecord.getObject(), collision );
            }
        }
        
        reset();
    }

    @Override
    public int getPriority()
    {
        return MainLoopOrder.COLLISION_HANDLDER.getPriority();
    }

    private class CollisionVolumeRecordPool extends ObjectPool<CollisionVolumeRecord>
    {
        public CollisionVolumeRecordPool( int maxSize )
        {
            super( maxSize );
        }

        @Override
        public void release( CollisionVolumeRecord entry )
        {
            entry.reset();
            super.release( entry );
        }
        
        @Override
        protected CollisionVolumeRecord create()
        {
            return new CollisionVolumeRecord();
        }
    }
    
    /** A record of a single game object and its associated collision info.  */
    private class CollisionVolumeRecord extends BaseObject
    {
        private GameObject mObject;
        private BoundingVolume mBoundingVolume;
        private CollisionHandler mCollisionHandler;
        
        public void reset()
        {
            mObject = null;
            mBoundingVolume = null;
        }
        
        public void setCollisionHandler( CollisionHandler collisionHandler )
        {
            mCollisionHandler = collisionHandler;
        }

        public CollisionHandler getCollisionHandler()
        {
            return mCollisionHandler;
        }
        
        public BoundingVolume getBoundingVolume()
        {
            return mBoundingVolume;
        }
        
        public void setBoundingVolume( BoundingVolume boundingVolume )
        {
            mBoundingVolume = boundingVolume;
        }
        
        public GameObject getObject()
        {
            return mObject;
        }
        
        public void setObject( GameObject object )
        {
            mObject = object;
        }
    }    
}
