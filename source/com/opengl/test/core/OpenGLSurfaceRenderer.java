package com.opengl.test.core;

import java.awt.event.MouseListener;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.igottashoot.game.rendering.DrawThreadInterface;
import com.sun.opengl.util.FPSAnimator;

/**
 * All methods of this class are invoked on the drawing thread (in this case,
 * its on the EDT).
 */
public class OpenGLSurfaceRenderer extends GLCanvas implements GLEventListener
{
    private final FPSAnimator mAnimator;
    private final DrawThreadInterface mRenderer;

    public OpenGLSurfaceRenderer( DrawThreadInterface renderer, MouseListener mouseListener )
    {
        super( initializeCapabilities() );
        
        addMouseListener( mouseListener );
        addGLEventListener( this );
        
        mRenderer = renderer;
        mAnimator = new FPSAnimator( this, 60 );
    }

    /**
     * onSurfaceCreated( GL10 gl, EGLConfig config )
     */
    @Override
    public void init( GLAutoDrawable drawable )
    {
        mRenderer.onSurfaceCreated( OpenGLUtilities.getInstance( drawable ) );
        mAnimator.start();
    }
    
    /**
     * onSurfaceChanged( GL10 gl, int width, int height )
     */
    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height )
    {
        mRenderer.onSurfaceChanged( OpenGLUtilities.getInstance( drawable ), width, height );
    }
    
    /**
     * onDraw( GL10 gl )
     */
    @Override
    public void display( GLAutoDrawable drawable )
    {
        if ( !mAnimator.isAnimating() )
            return;

        mRenderer.onDraw( OpenGLUtilities.getInstance( drawable ) );
    }
    
    @Override
    public void displayChanged( GLAutoDrawable drawable, boolean arg1, boolean arg2 )
    {
        throw new IllegalStateException();
    }
    
    private static GLCapabilities initializeCapabilities()
    {
        GLCapabilities capabilities = new GLCapabilities();
        capabilities.setRedBits( 8 );
        capabilities.setBlueBits( 8 );
        capabilities.setGreenBits( 8 );
        capabilities.setAlphaBits( 8 );

        return capabilities;
    }    
}
