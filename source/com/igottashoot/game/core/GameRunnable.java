package com.igottashoot.game.core;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.igottashoot.game.managers.OrderedObjectManager;
import com.igottashoot.game.managers.RenderingBufferManager;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.RenderElement;
import com.igottashoot.game.rendering.GameThreadInterface;
import com.igottashoot.game.utilities.GameUtilities;

public class GameRunnable extends BaseObject implements Runnable
{
    private final Object mPauseLock;
    private final FpsTracker mFpsTracker;
    private final GameThreadInterface mGameRenderer;
    private final MainLoop mLoop;
    private final GameCamera mCamera;
    private final SystemRegistry mRegistry;
    private final RenderingBufferManager mRenderingBufferManager;
    private final ConcurrentLinkedQueue<Runnable> mMessages;
    
    private boolean mPaused;
    private boolean mFinished;
    private long mLastTime;
    
    public GameRunnable( GameThreadInterface renderer,
                         MainLoop loop,
                         GameCamera camera,
                         SystemRegistry registry,
                         RenderingBufferManager renderingBufferManager )
    {
        mGameRenderer = renderer;
        mLoop = loop;
        mCamera = camera;
        mRegistry = registry;
        mRenderingBufferManager = renderingBufferManager;
        
        mMessages = new ConcurrentLinkedQueue<Runnable>();
        mPauseLock = new Object();
        mFinished = false;
        mPaused = false;
        mLastTime = GameUtilities.getCurrentTimeInMillis();
        mFpsTracker = new FpsTracker( 3000 );
    }
    
    public void addMessage( Runnable event )
    {
        mMessages.add( event );
    }
    
    @Override
    public void run()
    {
        mLastTime = GameUtilities.getCurrentTimeInMillis();
        mFinished = false;

        while ( ! mFinished )
        {
            mGameRenderer.waitForDrawingToComplete();
            
            checkAndHandleMessages();
            
            final long time = GameUtilities.getCurrentTimeInMillis();
            final long timeDelta = time - mLastTime;
            long finalDelta = timeDelta;
            
            if ( timeDelta > 12 )
            {
                float secondsDelta = ( time - mLastTime ) * 0.001f;
                if ( secondsDelta > 0.1f )
                    secondsDelta = 0.1f;
                
                mLastTime = time;
                
                mLoop.update( secondsDelta, mRegistry );

                OrderedObjectManager<RenderElement> renderQueue = mRenderingBufferManager.getCurrentQueue();

                // This code will block if the previous queue is still being executed.
                mGameRenderer.setDrawQueue( renderQueue, mCamera.getCameraViewInfo() );
                mRenderingBufferManager.goToNextQueue();
                
                final long endTime = GameUtilities.getCurrentTimeInMillis();
                finalDelta = endTime - time;

                mFpsTracker.printFps( finalDelta );
            }
            
            lockFrameRate( finalDelta );
            checkPauseState();
        }
        
        mGameRenderer.setDrawQueue( null, null ); 
        mRenderingBufferManager.emptyQueues();
    }

    private void checkAndHandleMessages()
    {
        Runnable event = mMessages.poll();
        if ( event == null )
            return;
        
        event.run();
    }

    public void stopGame()
    {
        synchronized ( mPauseLock )
        {
            mPaused = false;
            mFinished = true;
            mPauseLock.notifyAll();
        }
    }

    public void pauseGame()
    {
        synchronized ( mPauseLock )
        {
            mPaused = true;
        }
    }

    public void resumeGame()
    {
        synchronized ( mPauseLock )
        {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }
    
    private void checkPauseState()
    {
        synchronized ( mPauseLock )
        {
            if ( mPaused )
            {
                while ( mPaused )
                {
                    try { mPauseLock.wait(); } catch ( InterruptedException e ) {}
                }
            }
        }
    }

    private void lockFrameRate( long finalDelta )
    {
        if ( finalDelta < 16 )
        {
            try { Thread.sleep( 16 - finalDelta ); } catch ( InterruptedException e ) {}
        }        
    }
    
    private static final class FpsTracker extends BaseObject
    {
        private final int mReportDelayTimeInMillis;

        private long mProfileTime;
        private int mProfileFrames;
        
        public FpsTracker( int reportDelayTime )
        {
            mReportDelayTimeInMillis = reportDelayTime;
            mProfileTime = 0;
            mProfileFrames = 0;
        }
        
        public void printFps( long finalDelta )
        {
            mProfileTime += finalDelta;
            mProfileFrames++;
            
            if ( mProfileTime > mReportDelayTimeInMillis )
            {
                final long averageFrameTime = mProfileTime / mProfileFrames;
                
                GameUtilities.print( "FpsTracker", 0, "Average: " + averageFrameTime );
                mProfileTime = 0;
                mProfileFrames = 0;
            }
        }
    }
}
