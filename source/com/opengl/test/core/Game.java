package com.opengl.test.core;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.igottashoot.game.core.CameraViewInfo;
import com.igottashoot.game.core.GameCamera;
import com.igottashoot.game.core.GameCollisionHandler;
import com.igottashoot.game.core.GameEvent;
import com.igottashoot.game.core.GameRunnable;
import com.igottashoot.game.core.IoThread;
import com.igottashoot.game.core.MainLoop;
import com.igottashoot.game.core.SystemRegistry;
import com.igottashoot.game.core.TimeSystem;
import com.igottashoot.game.input.InputInterface;
import com.igottashoot.game.input.TouchScreenLocation;
import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.managers.GameObjectManager;
import com.igottashoot.game.managers.GameObjectPool;
import com.igottashoot.game.managers.GameObjectPropertyPool;
import com.igottashoot.game.managers.PropertyPoolDescriptor;
import com.igottashoot.game.managers.RenderingBufferManager;
import com.igottashoot.game.managers.TextureManager;
import com.igottashoot.game.primitives.RenderElement;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.properties.RenderingProperty;
import com.igottashoot.game.rendering.DrawingCanvas;
import com.igottashoot.game.rendering.GameRenderer;
import com.igottashoot.game.rendering.RendererInfo;
import com.igottashoot.game.rendering.TextureLoader;
import com.igottashoot.game.utilities.RenderElementPool;

public class Game implements LevelLoader
{
    private final static int DRAW_QUEUE_COUNT = RenderingBufferManager.DRAW_QUEUE_COUNT;
    private final static int MAX_RENDER_OBJECTS_PER_FRAME = RenderingBufferManager.MAX_RENDER_OBJECTS_PER_FRAME;
    private final static int MAX_RENDER_OBJECTS = MAX_RENDER_OBJECTS_PER_FRAME * DRAW_QUEUE_COUNT;
    
    private static final int MAX_NUMBER_OF_MAIN_LOOP_OBJECTS = 4;
    private static final int MAX_NUMBER_OF_GAME_OBJECTS = 2;
    private static final int MAX_TEXTURE_COUNT = 5;
    private static final int MAX_GAME_OBJECT_COUNT = 2;
    private static final int MAX_GAME_OBJECT_PROPERTY_COUNT = 21;
    private static final int MAX_COLLISION_OBJECTS = 5;
    
    private final GameRunnable mGameRunnable;    
    private final Thread mGameThread;
    private final Runnable mSurfaceRunnable;
    private final IoThread mIoThread;
    
    private final RenderingBufferManager mRenderingBufferManager;
    private final GameObjectManager mGameObjectManager;    
    private final RenderElementPool mRenderPool;
    private final GameRenderer mGameRenderer;    
    private final TextureManager mTextureManager;
    private final GameObjectPool mGameObjectPool;
    private final GameObjectPropertyPool mPropertyPool;
    private final GameCollisionHandler mCollisionHandler;
    private final InputInterface mInputInterface;
    private final TimeSystem mTimeSystem;
    private final GameCamera mCamera;
    private final SystemRegistry mRegistry;
    
    private GameEvent mEvent;
    
    public Game( int desiredWidth, int desiredHeight, int actualScreenWidth, int actualScreenHeight )
    {
        mRenderPool = new RenderElementPool( MAX_RENDER_OBJECTS );
        mRenderingBufferManager = new RenderingBufferManager( mRenderPool );
        mGameObjectManager = new GameObjectManager( MAX_NUMBER_OF_GAME_OBJECTS );
        mTextureManager = new TextureManager( MAX_TEXTURE_COUNT );
        mGameObjectPool = new GameObjectPool( MAX_GAME_OBJECT_COUNT );
        mPropertyPool = new GameObjectPropertyPool( createPoolDescriptors() );
        
        mTimeSystem = new TimeSystem();
        MainLoop loop = new MainLoop( MAX_NUMBER_OF_MAIN_LOOP_OBJECTS, mTimeSystem );
        mCamera = new GameCamera();
        mGameRenderer = new GameRenderer( desiredWidth, desiredHeight, actualScreenWidth, actualScreenHeight );
        mInputInterface = new InputInterface( mTimeSystem );
        mCollisionHandler = new GameCollisionHandler( MAX_COLLISION_OBJECTS );
        
        mRegistry = createGameRegistry();
        
        mGameRunnable = new GameRunnable( mGameRenderer,
                                          loop,
                                          mCamera,
                                          mRegistry,
                                          mRenderingBufferManager );
        
        OpenGLSurfaceRenderer canvas = new OpenGLSurfaceRenderer( mGameRenderer, createMouseListener() );
        
        mSurfaceRunnable = createDrawThread( canvas, actualScreenWidth, actualScreenHeight );
        mGameThread = new Thread( mGameRunnable );
        mGameThread.setName( "Game Thread" );
        mIoThread  = new IoThread();
        
        loop.addUpdateableObject( mInputInterface );
        loop.addUpdateableObject( mGameObjectManager );
        loop.addUpdateableObject( mCamera );
        loop.addUpdateableObject( mCollisionHandler );
        
        mEvent = GameEvent.NULL_EVENT;
    }

    public void start()
    {
        // start threads
        SwingUtilities.invokeLater( mSurfaceRunnable );
        mGameThread.start();
    }
    
    public void stop()
    {

    }
    
    public void load( GameEvent event )
    {
        mGameRunnable.addMessage( createLevelLoader( event ) );
    }

    public void handleTouchDown( int id, float x, float y )
    {
        RendererInfo renderInfo = mGameRenderer.getRenderInfo();
        
        float scaledX = x * renderInfo.getInverseScaleX();
        float scaledY = y * renderInfo.getInverseScaleY();
        
        scaledY = renderInfo.getRenderHeight() - scaledY - 1;
        
        CameraViewInfo cameraWorldInfo = renderInfo.getCameraViewInfo();
        
        float worldwidth = cameraWorldInfo.getWidth();
        float worldHeight = cameraWorldInfo.getHeight();
        
        float ratioX = worldwidth / renderInfo.getRenderWidth();
        float ratioY = worldHeight / renderInfo.getRenderHeight();
        
        scaledX = ( scaledX * ratioX ) + cameraWorldInfo.getCameraWorldLeft();
        scaledY = ( scaledY * ratioY ) + cameraWorldInfo.getCameraWorldBottom();
        
        mInputInterface.addTouchDownEvent( id, scaledX, scaledY );
    }
    
    public void handleTouchUp( int id, float x, float y )
    {
        RendererInfo renderInfo = mGameRenderer.getRenderInfo();
        
        float scaledX = x * renderInfo.getInverseScaleX();
        float scaledY = y * renderInfo.getInverseScaleY();
        
        scaledY = renderInfo.getRenderHeight() - scaledY - 1;
        
        CameraViewInfo cameraWorldInfo = renderInfo.getCameraViewInfo();
        
        float worldwidth = cameraWorldInfo.getWidth();
        float worldHeight = cameraWorldInfo.getHeight();
        
        float ratioX = worldwidth / renderInfo.getRenderWidth();
        float ratioY = worldHeight / renderInfo.getRenderHeight();
        
        scaledX = ( scaledX * ratioX ) + cameraWorldInfo.getCameraWorldLeft();
        scaledY = ( scaledY * ratioY ) + cameraWorldInfo.getCameraWorldBottom();

        mInputInterface.addTouchUpEvent( id, scaledX, scaledY );
    }

    /**
     * This is my super complex solution to loading a new level.
     * <ul>
     * <li>On the game thread, unload any created objects by the previous level
     * and create all objects required for new state ({@link GameObject}) and
     * identify any textures that are required by the level (by allocating for
     * them in the {@link TextureManager}).
     * <li>Schedule textures to be loaded on the I/O thread
     * <li>Once the textures are in memory, notify the rendering thread that
     * these textures are necessary for rendering
     * <li>Return to the game thread to load the game objects to the main loop.
     * </ul>
     */
    private Runnable createLevelLoader( final GameEvent event )
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                // enable the loading screen gameObject
                // mLoadingGameObject.enabled();

                // previous event unload
                mEvent.unload( Game.this );

                // load new event
                mEvent = event;
                mEvent.create( Game.this );
                
                // mMediator.readTextures( runnable )
                Runnable runOnIoThread = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        OpenGLUtilities handler = OpenGLUtilities.getInstance( null );
                        final Object[] loadedTextures = mTextureManager.loadTextures( handler );
                        TextureLoader runOnGameRenderThread = new TextureLoader()
                        {
                            @Override
                            public void load( DrawingCanvas canvas )
                            {
                                canvas.handleLoadedTextures( loadedTextures );
                                Runnable runOnGameThread = new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        mEvent.load( Game.this );
                                    }
                                };

                                mGameRunnable.addMessage( runOnGameThread );
                            }
                        };
                        
                        mGameRenderer.scheduleTextureLoad( runOnGameRenderThread );
                    }
                };
                
                mIoThread.addMessage( runOnIoThread );
            }
        };
    }

    @Override
    public void addGameObject( GameObject object )
    {
        mGameObjectManager.addGameObject( object );
    }

    @Override
    public Texture allocateTexture( int resourceId )
    {
        return mTextureManager.allocateTexture( resourceId );
    }

    @Override
    public void removeGameObject( GameObject backgroundObject )
    {
        mGameObjectManager.removeGameObject( backgroundObject );
    }
    
    @Override
    public void releaseTexture( Texture texture )
    {
        
    }

    @Override
    public GameObject allocateGameObject()
    {
        GameObject object = mGameObjectPool.allocate();
        object.setMaximumNumberOfPropertyObjects( MAX_GAME_OBJECT_PROPERTY_COUNT );
        
        return object;
    }

    @Override
    public void releaseGameObject( GameObject gameObject )
    {
        mGameObjectPool.release( gameObject );
    }        
    
    @Override
    public void setCameraTarget( GameObject target )
    {
        mCamera.setTarget( target );
    }
    
    private SystemRegistry createGameRegistry()
    {
        return new SystemRegistry()
        {
            @Override
            public TimeSystem getTimeSystem()
            {
                return mTimeSystem;
            }
            
            @Override
            public void scheduleForDraw( RenderElement element )
            {
                mRenderingBufferManager.scheduleForDraw( element );
            }
            
            @Override
            public RenderElement allocateRenderElement()
            {
                return mRenderPool.allocate();
            }
            
            @Override
            public RendererInfo getRenderInfo()
            {
                return mGameRenderer.getRenderInfo();
            }
            
            @Override
            public TouchScreenLocation getTouchScreenLocation()
            {
                return mInputInterface;
            }
            
            @Override
            public void load( GameEvent event )
            {
                Game.this.load( event );
            }
            
            @Override
            public GameCollisionHandler getCollisionHandler()
            {
                return mCollisionHandler;
            }
        };
    }
    
    private static Runnable createDrawThread( final OpenGLSurfaceRenderer renderer, int gameWidth, int gameHeight )
    {
        final JFrame frame = new JFrame( "Don't Shooooooot!" );
        frame.getContentPane().add( renderer, BorderLayout.CENTER );
        
        // Apparently the height of the toolbar for JFrames on MAC is 22 pixels... boo!
        if ( PlatformUtilities.isMacOSX() )
            frame.setSize( gameWidth, gameHeight + 22 );
        else
            frame.setSize( gameWidth + 10, gameHeight + 29 );
        
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        return new Runnable()
        {
            @Override
            public void run()
            {
                frame.setVisible( true );
                renderer.requestFocus();
            }
        };
    }

    private static PropertyPoolDescriptor[] createPoolDescriptors()
    {
        PropertyPoolDescriptor[] descriptor =
        {
             new PropertyPoolDescriptor( RenderingProperty.class, 10 )
        };
        
        return descriptor;
    }
    
    private MouseListener createMouseListener()
    {
        return new MouseAdapter()
        {
            @Override
            public void mousePressed( MouseEvent e )
            {
                Game.this.handleTouchDown( 0, e.getX(), e.getY() );
            };
            
            @Override
            public void mouseReleased( MouseEvent e )
            {
                Game.this.handleTouchUp( 0, e.getX(), e.getY() );
            }
        };
    }
}