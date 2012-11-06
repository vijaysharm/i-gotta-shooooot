package com.igottashoot.game.properties;

import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.OrderedUpdateableObject;

public interface GameObjectProperty extends OrderedUpdateableObject<GameObject>
{
    public enum PropertyExecutionPhase
    {
        THINK( 1 ),                  // decisions are made
        PHYSICS( 2 ),                // impulse velocities are summed
        POST_PHYSICS( 3 ),           // inertia, friction, and bounce
        MOVEMENT( 4 ),               // position is updated
        COLLISION_DETECTION( 5 ),    // intersections are detected
        COLLISION_RESPONSE( 6 ),     // intersections are resolved
        POST_COLLISION( 7 ),         // position is now final for the frame
        ANIMATION( 8 ),              // animations are selected
        PRE_DRAW( 9 ),               // drawing state is initialized
        DRAW( 10 ),                   // drawing commands are scheduled.
        FRAME_END( 11 );              // final cleanup before the next update
        
        private final int mPhase;
        PropertyExecutionPhase( int phase )
        {
            mPhase = phase;
        }
        
        public int getPhase()
        {
            return mPhase;
        }
    }
}
