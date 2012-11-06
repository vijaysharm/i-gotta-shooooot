package com.igottashoot.game.core;

import com.igottashoot.game.managers.OrderedUpdateableObjectManager;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.OrderedUpdateableObject;
import com.igottashoot.game.primitives.UpdateableObject;

public class MainLoop extends BaseObject implements UpdateableObject<SystemRegistry>
{
    public enum MainLoopOrder
    {
        INPUT( 1 ),
        GAME_OBJECT_MANAGER( 2 ),
        CAMERA( 3 ),
        COLLISION_HANDLDER( 4 );
        
        private final int mPriority;
        MainLoopOrder( int priority )
        {
            mPriority = priority;
        }
        
        public int getPriority()
        {
            return mPriority;
        }        
    };
        
    private final OrderedUpdateableObjectManager<SystemRegistry, OrderedUpdateableObject<SystemRegistry>> mObjectManager;
    private final TimeSystem mTimeSystem;
    
    public MainLoop( int maxNumberOfObjects, TimeSystem timeSystem )
    {
        mTimeSystem = timeSystem;
        mObjectManager = OrderedUpdateableObjectManager.newManager( maxNumberOfObjects );
    }
    
    @Override
    public void update( float timeDelta, SystemRegistry parent )
    {
        mTimeSystem.update( timeDelta, parent );
        final float frameDelta = mTimeSystem.getGameFrameDelta();
        mObjectManager.update( frameDelta, parent );
    }
    
    public void addUpdateableObject( OrderedUpdateableObject<SystemRegistry> object )
    {
        mObjectManager.add( object );
    }
}
