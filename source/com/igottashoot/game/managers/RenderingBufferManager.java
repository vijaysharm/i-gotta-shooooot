package com.igottashoot.game.managers;

import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.RenderElement;
import com.igottashoot.game.utilities.FixedSizeArray;
import com.igottashoot.game.utilities.RenderElementPool;

public class RenderingBufferManager extends BaseObject
{
    public static final int DRAW_QUEUE_COUNT = 2;
    public static final int MAX_RENDER_OBJECTS_PER_FRAME = 256;
    
    private final RenderElementPool mElementPool;
    private final OrderedObjectManager<RenderElement>[] mRenderingQueues;
    
    private int mCurrentQueueIndex = 0;
    
    public RenderingBufferManager( RenderElementPool pool )
    {
        mElementPool = pool;
        mRenderingQueues = new OrderedObjectManager[ DRAW_QUEUE_COUNT ];
        for ( int index = 0; index < DRAW_QUEUE_COUNT; index++ )
            mRenderingQueues[index] = OrderedObjectManager.newManager( MAX_RENDER_OBJECTS_PER_FRAME );
        
        mCurrentQueueIndex = 0;
    }
    
    public void scheduleForDraw( RenderElement element )
    {
        if ( element == null )
            throw new IllegalStateException( "Can't schedule a null element for drawing" );
        
        mRenderingQueues[mCurrentQueueIndex].add( element );
    }

    public OrderedObjectManager<RenderElement> getCurrentQueue()
    {
        mRenderingQueues[mCurrentQueueIndex].commitUpdates();
        return mRenderingQueues[mCurrentQueueIndex];
    }

    public void goToNextQueue()
    {
        final int lastQueue = ( mCurrentQueueIndex == 0 ) ? DRAW_QUEUE_COUNT - 1 : mCurrentQueueIndex - 1;
    
        // Clear the old queue.
        clearQueue( mRenderingQueues[ lastQueue ] );
    
        mCurrentQueueIndex = (mCurrentQueueIndex + 1) % DRAW_QUEUE_COUNT;
    }

    public void emptyQueues()
    {
        for ( int index = 0; index < DRAW_QUEUE_COUNT; index++ )
        {
            mRenderingQueues[ index ].commitUpdates();
            clearQueue( mRenderingQueues[ index ] );
        }        
    }
    
    private void clearQueue( OrderedObjectManager<RenderElement> orderedObjectManager )
    {
        FixedSizeArray<RenderElement> objects = orderedObjectManager.getObjects();
        final int size = objects.getCount() - 1;
        for ( int index = size; index >= 0 ; index-- )
        {
            RenderElement object = objects.get( index );
            mElementPool.release( object );
            objects.removeLast();
        }
    }
}
