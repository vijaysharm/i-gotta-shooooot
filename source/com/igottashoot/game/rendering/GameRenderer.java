package com.igottashoot.game.rendering;

import com.igottashoot.game.core.CameraViewInfo;
import com.igottashoot.game.managers.OrderedObjectManager;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.RenderElement;
import com.igottashoot.game.utilities.GameUtilities;

/**
 * This class is accessed by two threads, the drawing thread, and the game
 * thread, i.e. this is a shared resource.
 */
public class GameRenderer extends BaseObject implements GameThreadInterface, DrawThreadInterface
{
    private final DefaultRendererInfo mRendererInfo;
    private final Object mDrawQueueChangedLock;
    private final Object mRenderQueueLock;
    private final Object mTextureLoaderLock;
    private final RendererFpsTracker mFpsTracker;
    
    private volatile boolean mDrawQueueChanged;
    private OrderedObjectManager<RenderElement> mRenderQueue;
    private TextureLoader mTextureLoader;

    public GameRenderer( final int desiredWidth,
                         final int desiredHeight,
                         final int actualScreenWidth,
                         final int actualScreenHeight ) 
    {
        // Option 1
        // we're use the height given to us, but maintain the aspect ratio of
        // the screen drawn. Therefore we recalculate the width accordingly.
        // This means, in our scene, only the width ever grows.
//        final int renderHeight = desiredHeight;
//        int renderWidth = desiredWidth;
//        if ( actualScreenWidth != desiredWidth )
//        {
//            float ratio = ( (float) actualScreenWidth ) / actualScreenHeight;
//            renderWidth = (int)( desiredHeight * ratio );    
//        }
        
        // Option 2
//        final float desiredRatio = ( (float) desiredWidth ) / desiredHeight;
//        final float realRatio = ( (float)actualScreenWidth ) / actualScreenHeight;
//
//        int renderWidth;           
//        int renderHeight;
//        if( realRatio < desiredRatio )
//        {
//            renderWidth = actualScreenWidth;
//            renderHeight = Math.round( renderWidth / desiredRatio );
//        }
//        else
//        {
//            renderHeight = actualScreenHeight;
//            renderWidth = Math.round( renderHeight * desiredRatio );
//        }
        
        // Option 3
        final int renderWidth = actualScreenWidth;           
        final int renderHeight = actualScreenHeight;
        
        mRendererInfo = new DefaultRendererInfo( renderWidth, renderHeight );
        
        mDrawQueueChangedLock = new Object();
        mRenderQueueLock = new Object();
        mTextureLoaderLock = new Object();
        
        mDrawQueueChanged = true;
        mRenderQueue = null;
        mTextureLoader = null;

        mRendererInfo.getCameraViewInfo().setWorldSize( 0, mRendererInfo.getRenderWidth(), 0, mRendererInfo.getRenderHeight() );
        
        mFpsTracker = new RendererFpsTracker( 3000 );
    }
    
    // GameThreadInterface related methods
    
    public RendererInfo getRenderInfo()
    {
        return mRendererInfo;
    }

    @Override
    public void setDrawQueue( OrderedObjectManager<RenderElement> renderQueue, CameraViewInfo cameraViewInfo )
    {
        synchronized( mRenderQueueLock )
        {
            mRenderQueue = renderQueue;
            mRendererInfo.getCameraViewInfo().updateInfo( cameraViewInfo );
            
            synchronized( mDrawQueueChangedLock )
            {
                mDrawQueueChanged = true;
                mDrawQueueChangedLock.notify();
            }
        }
    }

    @Override
    public void waitForDrawingToComplete()
    {
        synchronized( mRenderQueueLock )
        {
            
        }
    }

    @Override
    public void scheduleTextureLoad( TextureLoader textureLoader )
    {
        synchronized ( mTextureLoaderLock )
        {
            mTextureLoader = textureLoader;
        }
    }
    
    // DrawingThreadInterface related methods
    
    @Override
    public void onSurfaceCreated( DrawInitializer surfaceCreatedCommand )
    {
        surfaceCreatedCommand.initialize();
    }

    /**
     * On Mobile devices, i hope this is only called once. It can be called a
     * number of times from a computer when the window is resized
     */
    @Override
    public void onSurfaceChanged( DrawResizer surfaceChangedCommand, int screenWidth, int screenHeight )
    {
        // Assuming the screen size never changes (like on mobile phones), this
        // could technically be done at construction time, and therefore would
        // be immutable.
        float scaleX = (float) screenWidth / mRendererInfo.getRenderWidth();
        float scaleY =  (float) screenHeight / mRendererInfo.getRenderHeight();
        mRendererInfo.setScaleX( scaleX );
        mRendererInfo.setScaleY( scaleY );
        
        surfaceChangedCommand.resize( screenWidth, screenHeight );
        
        // load textures here
//        synchronized ( mTextureLoaderLock )
//        {
//            loadTextures( canvas );
//        }       
    }

    @Override
    public void onDraw( DrawingCanvas canvas )
    {
        int renderedCount = 0;
        mFpsTracker.startRender();
        
        synchronized( mDrawQueueChangedLock )
        {
            waitForQueueToChange();
        }
        
        mFpsTracker.endWait();
        
        // load textures here
        synchronized ( mTextureLoaderLock )
        {
            loadTextures( canvas );
        }
        
        // prepare rendering
        canvas.start( mRendererInfo.getCameraViewInfo() );
        
        synchronized ( mRenderQueueLock )
        {
            canvas.clear();
            if ( mRenderQueue != null && mRenderQueue.getCount() > 0 )
                renderedCount = draw( canvas, mRenderQueue, mRendererInfo );
        }
        
        canvas.end();
        
        mFpsTracker.endRender( renderedCount );
    }

    /**
     * Executed while holding the texture lock
     */
    private void loadTextures( DrawingCanvas canvas )
    {
        if ( mTextureLoader != null )
            mTextureLoader.load( canvas );

        mTextureLoader = null;
    }

    /**
     * Executed with the render queue lock 
     * @param rendererInfo 
     * @param renderQueue 
     * @return the number of elements drawn
     */
    private static int draw( final DrawingCanvas canvas,
                             final OrderedObjectManager<RenderElement> renderQueue,
                             final DefaultRendererInfo rendererInfo )
    {
        final int count = renderQueue.getCount();
        
        for ( int index = 0; index < count; index++ )
        {
            RenderElement renderElement = renderQueue.get( index );
            renderElement.draw( canvas, rendererInfo );
        }
        return count;
    }

    /**
     * Executed with the draw queue changed lock
     */
    private void waitForQueueToChange()
    {
        if ( mDrawQueueChanged == false )
        {
            while ( mDrawQueueChanged == false )
            {
                try { mDrawQueueChangedLock.wait(); } catch ( InterruptedException ignore ) {}
            }
        }

        mDrawQueueChanged = false;
    }
    
    private static final class DefaultRendererInfo extends BaseObject implements RendererInfo
    {
        private final int mRenderHeight;
        private final int mRenderWidth;
        private final int mRenderHalfHeight;
        private final int mRenderHalfWidth;        
        private final CameraViewInfo mCameraViewInfo;
        private float mScaleX;
        private float mScaleY;
        private float mInverseScaleX;
        private float mInverseScaleY;
        
        public DefaultRendererInfo( int renderWidth, int renderHeight )
        {
            mRenderWidth = renderWidth;
            mRenderHeight = renderHeight;
            mRenderHalfWidth = renderWidth / 2;
            mRenderHalfHeight = renderHeight / 2;
            mCameraViewInfo = new CameraViewInfo();
            
            mScaleX = 1.0f;
            mScaleY = 1.0f;
            mInverseScaleX = 1.0f;
            mInverseScaleY = 1.0f;
        }
        
        @Override
        public float getScaleX()
        {
            return mScaleX;
        }

        @Override
        public float getInverseScaleX()
        {
            return mInverseScaleX;
        }
        
        public void setScaleX( float scaleX )
        {
            mScaleX = scaleX;
            mInverseScaleX = 1 / scaleX;
        }

        @Override
        public float getScaleY()
        {
            return mScaleY;
        }

        @Override
        public float getInverseScaleY()
        {
            return mInverseScaleY;
        }
        
        public void setScaleY( float scaleY )
        {
            mScaleY = scaleY;
            mInverseScaleY = 1 / scaleY; 
        }

        @Override
        public int getRenderHeight()
        {
            return mRenderHeight;
        }

        @Override
        public int getRenderWidth()
        {
            return mRenderWidth;
        }

        @Override
        public int getHalfHeight()
        {
            return mRenderHalfHeight;
        }

        @Override
        public int getHalfWidth()
        {
            return mRenderHalfWidth;
        }
        
        @Override
        public CameraViewInfo getCameraViewInfo()
        {
            return mCameraViewInfo;
        }
    }
    
    private static final class RendererFpsTracker extends BaseObject
    {
        private final int mReportDelayTime;
        private long mLastTime;
        private long mCurrentTIme;
        private long mCurrentTimeDelta;
        private long mCurrentWaitTime;
        private long mProfileFrameTime;
        private long mProfileSubmitTime;
        private long mProfileWaitTime;
        private int mProfileFrames;
        private int mProfileObjectCount;
        
        public RendererFpsTracker( int reportDelayTime )
        {
            mReportDelayTime = reportDelayTime;
            
            mLastTime = 0;
            mCurrentTIme = 0;
            mCurrentTimeDelta = 0;
            mCurrentWaitTime = 0;
            
            reset();
        }
        
        @Override
        public void reset()
        {
            mProfileFrames = 0;
            mProfileFrameTime = 0;
            mProfileSubmitTime = 0;
            mProfileWaitTime = 0;
            mProfileObjectCount = 0;
        }
        
        public void startRender()
        {
            mCurrentTIme = GameUtilities.getCurrentTimeInMillis();
            mCurrentTimeDelta = mCurrentTIme - mLastTime;
        }
        
        public void endWait()
        {
            mCurrentWaitTime = GameUtilities.getCurrentTimeInMillis();
        }
        
        public void endRender( int renderedCount )
        {
            long time = GameUtilities.getCurrentTimeInMillis();
            mLastTime = time;
            
            mProfileObjectCount += renderedCount;
            mProfileFrameTime += mCurrentTimeDelta;
            mProfileSubmitTime += time - mCurrentTIme;
            mProfileWaitTime += mCurrentWaitTime - mCurrentTIme;
            mProfileFrames++;
            
            if ( mProfileFrameTime > mReportDelayTime )
            {
                final int validFrames = mProfileFrames;
                final long averageFrameTime = mProfileFrameTime / validFrames;
                final long averageSubmitTime = mProfileSubmitTime / validFrames;
                final float averageObjectsPerFrame = (float)mProfileObjectCount / validFrames;
                final long averageWaitTime = mProfileWaitTime / validFrames;

                String message = "Average Submit: " + averageSubmitTime + "  Average Draw: "
                                 + averageFrameTime + " Objects/Frame: " + averageObjectsPerFrame
                                 + " Wait Time: " + averageWaitTime;
                
//                GameUtilities.print( "Render Profile", 0, message );
                
                reset();
            }
            
            mCurrentTIme = 0;
            mCurrentTimeDelta = 0;
            mCurrentWaitTime = 0;
        }
    }
}
