package com.igottashoot.game.input;

import com.igottashoot.game.core.MainLoop.MainLoopOrder;
import com.igottashoot.game.core.SystemRegistry;
import com.igottashoot.game.core.TimeSystem;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.OrderedUpdateableObject;

public class InputInterface extends BaseObject implements OrderedUpdateableObject<SystemRegistry>, TouchScreenLocation
{
    private static final int MAX_NUMBER_OF_INPUTS = 1; 
    private final TimeSystem mTimeSystem;
    
    private final InputButton[] mPendingInputButton;
    private final InputButton[] mInputButton;
    private final Object mPendingInputLock;
    
    public InputInterface( TimeSystem timeSystem )
    {
        mTimeSystem = timeSystem;
        
        mPendingInputLock = new Object();
        
        mPendingInputButton = new InputButton[MAX_NUMBER_OF_INPUTS];
        mInputButton = new InputButton[MAX_NUMBER_OF_INPUTS];

        for ( int index = 0; index < MAX_NUMBER_OF_INPUTS; index++ )
        {
            mPendingInputButton[index] = new InputButton();
            mInputButton[index] = new InputButton();
        }
    }

    @Override
    public void update( float timeDelta, SystemRegistry parent )
    {
        // TODO: There's a problem here. What if the game thread takes a long
        // time, and we get a press and release before we enter this code? This
        // means that the game will never have known about a button press. This
        // can happen if it takes a long time to run the game logic. This is
        // very very bad.
        synchronized ( mPendingInputLock )
        {
            for ( int index = 0; index < MAX_NUMBER_OF_INPUTS; index++ )
                mInputButton[index].copy( mPendingInputButton[index] );
        }
    }

    @Override
    public int getPriority()
    {
        return MainLoopOrder.INPUT.getPriority();
    }

    @Override
    public boolean isPressed( float x, float y, float width, float height )
    {
        for ( int index = 0; index < MAX_NUMBER_OF_INPUTS; index++ )
        {
            if ( mInputButton[index].isPressed() )
                return true;
        }
        
        return false;
    }

    /**
     * Touch events come from a different thread, so we synchronize over a lock
     * to make sure its not being copied while an event is coming in.
     */
    public void addTouchDownEvent( int id, float x, float y )
    {
        checkTouchId( id );
        synchronized ( mPendingInputLock )
        {
            mPendingInputButton[id].press( mTimeSystem.getGameTime(), x, y );
        }
    }

    /**
     * Touch events come from a different thread, so we synchronize over a lock
     * to make sure its not being copied while an event is coming in.
     */    
    public void addTouchUpEvent( int id, float x, float y )
    {
        checkTouchId( id );
        synchronized ( mPendingInputLock )
        {
            mPendingInputButton[id].release();
        }
    }
    
    private void checkTouchId( int id )
    {
        if ( id < 0 || id > ( MAX_NUMBER_OF_INPUTS - 1 ) )
            throw new IllegalStateException();
    }
}
