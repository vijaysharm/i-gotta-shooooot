package com.opengl.test.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.igottashoot.game.core.CameraViewInfo;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.rendering.DrawInitializer;
import com.igottashoot.game.rendering.DrawResizer;
import com.igottashoot.game.rendering.DrawableObjectInfo;
import com.igottashoot.game.rendering.DrawingCanvas;
import com.igottashoot.game.rendering.TextureLoadingHandler;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

/**
 * These methods should probably not return a new object every time. Giving the
 * GC too much work to do.
 */
public class OpenGLUtilities implements DrawInitializer, DrawResizer, DrawingCanvas, TextureLoadingHandler
{
    private static final OpenGLUtilities INSTANCE = new OpenGLUtilities();

    private final Map<Integer, com.sun.opengl.util.texture.Texture> mTextureLookup = new HashMap();
    private GLAutoDrawable mDrawable;
    
    public static OpenGLUtilities getInstance( GLAutoDrawable drawable )
    {
        if ( drawable != null )
            INSTANCE.setDrawable( drawable );

        return INSTANCE;
    }

    @Override
    public void initialize()
    {
        GL gl = mDrawable.getGL();
        mDrawable.setGL( new DebugGL( gl ) );
        
        // We use the fastest perspective correction
        gl.glHint( GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST );
        
        // Define "clear" color.
        gl.glClearColor( 0f, 0f, 0f, 0f );
        
        // Enable flat shading.
        gl.glShadeModel( GL.GL_FLAT );
        gl.glDisable( GL.GL_DEPTH_TEST );
        gl.glEnable( GL.GL_TEXTURE_2D );
        
        gl.glDisable( GL.GL_DITHER );
        gl.glDisable( GL.GL_LIGHTING );
        
        gl.glTexEnvf( GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE );
        gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
    }

    @Override
    public void resize( int width, int height )
    {
        GL gl = mDrawable.getGL();
        
        /*
         * Set our projection matrix. This doesn't have to be done each time we
         * draw, but usually a new projection needs to be set when the viewport
         * is resized.
         */
        gl.glViewport( 0, 0, width, height );
    }

    /**
     * Executed on the IoThread
     */
    @Override
    public Object[] loadTextures( Texture[] textures )
    {
        Object[] result = new Object[ textures.length * 2 ];
        for ( int index = 0; index < textures.length; index++ )
        {
            final Texture texture = textures[ index ];
            
            // TODO: This check might not be thread safe
            if ( ! texture.isLoaded() && texture.getResourceId() != -1 )
            {
                TextureData data = load( texture );
                if ( data == null )
                {
                    texture.setLoaded( false );
                    continue;
                }
                
                int resultIndex = index * 2;
                result[ resultIndex ] = texture;
                result[ resultIndex+1 ] = data;
            }
        }
        
        return result;
    }
    
    @Override
    public void handleLoadedTextures( Object[] loadTextures )
    {
        for ( int index = 0; index < loadTextures.length; index+=2 )
        {
            if ( loadTextures[ index ] == null )
                continue;
            
            Texture texture = (Texture) loadTextures[ index ];
            TextureData data = (TextureData) loadTextures[ index+1 ];
            com.sun.opengl.util.texture.Texture newTexture = TextureIO.newTexture( data );
            
            mTextureLookup.put( texture.getResourceId(), newTexture );
            
            texture.setBindingName( newTexture.getTextureObject() );
            texture.setSize( newTexture.getWidth(), newTexture.getHeight() );
            texture.setLoaded( true );
        }
    }
    
//    @Override
//    public void releaseTexture( Texture texture )
//    {
//        com.sun.opengl.util.texture.Texture glTexture = mLoadedTextures.get( texture.getResourceId() );
//        if ( glTexture == null )
//            return;
//        
//        mLoadedTextures.remove( glTexture );
//        glTexture.dispose();
//        texture.setBindingName( -1 );
//        texture.setLoaded( false );
//    }
    
    @Override
    public void start( CameraViewInfo cameraViewInfo )
    {
        int left = cameraViewInfo.getCameraWorldLeft();
        int bottom = cameraViewInfo.getCameraWorldBottom();
        int right = cameraViewInfo.getCameraWorldRight();
        int top = cameraViewInfo.getCameraWorldTop();
        
        GL gl = mDrawable.getGL();
        
        gl.glShadeModel( GL.GL_FLAT );
        gl.glEnable( GL.GL_BLEND );
        gl.glBlendFunc( GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA );
        gl.glColor4i( 255, 255, 255, 255 );

        gl.glMatrixMode( GL.GL_PROJECTION );
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho( left, right, bottom, top, 0.0f, 1.0f );
        
        gl.glMatrixMode( GL.GL_MODELVIEW );
        gl.glPushMatrix();
        gl.glLoadIdentity();
       
        gl.glEnable( GL.GL_TEXTURE_2D );
        
        // we initialize the color to white with full alpha for the background
        // texture. This is important for the GL_BLEND function which uses the
        // color and alpha to determine what the blending will be.
        gl.glColor4f( 1.0f, 1.0f, 1.0f, 1.0f );
    }

    @Override
    public void draw( Texture texture, DrawableObjectInfo textureInfo )
    {
        // Apply texture.
        final com.sun.opengl.util.texture.Texture loadedTexture = 
            mTextureLookup.get( texture.getResourceId() );
        
        if ( loadedTexture == null || texture.isLoaded() == false )
            return;
                
        final float opacity = textureInfo.getOpacity();
        
        GL gl = mDrawable.getGL();
        
        // TODO: Some optimization can be done here so that we don't bind the
        // same texture two times in a row.
        loadedTexture.enable();
        loadedTexture.bind();
        
        if ( opacity < 1.0f )
            gl.glColor4f( opacity, opacity, opacity, opacity );
        
        glDrawTexture( gl, textureInfo );
        
        if ( opacity < 1.0f )
            gl.glColor4f( 1.0f, 1.0f, 1.0f, 1.0f );
    }

    /**
     * Texture coordinates        OpenGL Coordinates
     * (0,0) --------- (1,0)      (x,y+h) ------ (x+w,y+h)
     *   |               |           |               |
     *   |               |           |               | 
     *   |               |    ==>    |               |
     *   |               |           |               |
     *   |               |           |               |
     *   |               |           |               |
     * (0,1) --------- (1,1)       (x,y) ------- (x+w,y)
     * 
     * Texture coordinates are from top left to bottom right
     * OpenGL coordinates are from bottom left to top right.
     * 
     * We use glVertex4f here for the blending calculations. 
     * if they are not defined, i believe they are assumed to 
     * be 0, which leads to a black background when blending 
     * the first texture.
     */
    private void glDrawTexture( GL gl, DrawableObjectInfo drawableInfo )
    {
        final float x = drawableInfo.getVertexPositionX();
        final float y = drawableInfo.getVertexPositionY();
        final float width = drawableInfo.getVertexWidth();
        final float height = drawableInfo.getVertexHeight();
        
        final float tx1 = drawableInfo.getTexturePositionX1();
        final float ty1 = drawableInfo.getTexturePositionY1();
        final float tx2 = drawableInfo.getTexturePositionX2();
        final float ty2 = drawableInfo.getTexturePositionY2();
        
        float z = 0f;
        gl.glBegin( GL.GL_QUADS );
        {
            gl.glTexCoord3f( tx1, ty1, z );
            gl.glVertex4f( x, y + height, z, 1f );
            
            gl.glTexCoord3f( tx1, ty2, z );
            gl.glVertex4f( x, y, z, 1f );
            
            gl.glTexCoord3f( tx2, ty2, z );
            gl.glVertex4f( x + width, y, z, 1f );
            
            gl.glTexCoord3f( tx2, ty1, z );
            gl.glVertex4f( x + width, y + height, z, 1f );
        }
        gl.glEnd();
    }

    @Override
    public void end()
    {
        GL gl = mDrawable.getGL();
        
        gl.glDisable( GL.GL_BLEND );
        gl.glMatrixMode( GL.GL_PROJECTION );
        gl.glPopMatrix();
        gl.glMatrixMode( GL.GL_MODELVIEW );
        gl.glPopMatrix();
    }
    
    @Override
    public void clear()
    {
        GL gl = mDrawable.getGL();
        
        gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
    }
    
    private void setDrawable( GLAutoDrawable drawable )
    {
        mDrawable = drawable;
    }

    private TextureData load( Texture texture )
    {
        try
        {
            Resources resource = Resources.getResource( texture.getResourceId() );
            InputStream stream = new BufferedInputStream( new FileInputStream( resource.getDrawablePath() ) );
            TextureData data = TextureIO.newTextureData( stream, false, null );

            return data;
        }
        catch ( IOException ioe )
        {
            ioe.printStackTrace();
            return null;
        }
    }
}
