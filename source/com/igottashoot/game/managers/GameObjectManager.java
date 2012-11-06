package com.igottashoot.game.managers;

import java.util.Comparator;

import com.igottashoot.game.core.MainLoop.MainLoopOrder;
import com.igottashoot.game.core.SystemRegistry;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.OrderedUpdateableObject;
import com.igottashoot.game.utilities.FixedSizeArray;

public class GameObjectManager extends BaseObject implements OrderedUpdateableObject<SystemRegistry>
{
    private static final DistancePositionComparator DISTANCE_COMPARATOR = new DistancePositionComparator();

    private final UpdateableObjectManager<SystemRegistry, GameObject> mGameObjects;
    private final FixedSizeArray<GameObject> mInactiveGameObjects;
    private final FixedSizeArray<GameObject> mMarkedForDeathGameObjects;

    public GameObjectManager( int maxNumberOfGameObjects )
    {
        mGameObjects = UpdateableObjectManager.newManager( maxNumberOfGameObjects );
        
        mInactiveGameObjects = FixedSizeArray.newArray( 1 );
        mInactiveGameObjects.setComparator( DISTANCE_COMPARATOR );
        mMarkedForDeathGameObjects = new FixedSizeArray<GameObject>( 1 );
    }
    
    @Override
    public int getPriority()
    {
        return MainLoopOrder.GAME_OBJECT_MANAGER.getPriority();
    }
    
    @Override
    public void update( float timeDelta, SystemRegistry registry )
    {
        // TODO: Get the right pools from the parent to clean up any objects
        // that are set for death.
        mGameObjects.update( timeDelta, registry );
    }

    public void addGameObject( GameObject object )
    {
        mGameObjects.add( object );
    }
    
    public void removeGameObject( GameObject object )
    {
        mGameObjects.remove( object );
    }
    
    private final static class DistancePositionComparator implements Comparator<GameObject>
    {
        @Override
        public int compare( GameObject o1, GameObject o2 )
        {
            // TODO: implement a distance comparator on game objects
            return 0;
        }
    }
}
