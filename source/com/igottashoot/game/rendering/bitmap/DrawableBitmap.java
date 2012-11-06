package com.igottashoot.game.rendering.bitmap;

import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.DrawableObject;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.rendering.DrawableObjectInfo;
import com.igottashoot.game.rendering.DrawingCanvas;
import com.igottashoot.game.rendering.RendererInfo;

public class DrawableBitmap extends BaseObject implements DrawableObject
{
    // We use a static object here since its called on every draw iteration.
    // Give the GC a break!
    private static final DefaultDrawableObjectInfo DRAWABLE_INFO = new DefaultDrawableObjectInfo();
    
    private Texture mTexture;
    private float[] mTextureVertex;

    // Vertex information
    private float mPositionX;
    private float mPositionY;
    private int mHeight;
    private int mWidth;

    private float mOpacity;
    
    public DrawableBitmap()
    {
        this ( null );
    }
    
    public DrawableBitmap( Texture texture )
    {
        mTexture = texture;
        mTextureVertex = new float[4];
        
        reset();
    }

    @Override
    public void reset()
    {
        mOpacity = 1.0f;
        
        mTextureVertex[0] = 0f;
        mTextureVertex[1] = 0f;
        mTextureVertex[2] = 1f;
        mTextureVertex[3] = 1f;
    }
    
    @Override
    public void draw( DrawingCanvas canvas, RendererInfo rendererInfo )
    {
        if ( mTexture == null || ! mTexture.isLoaded() )
            return;
        
        if ( isCulled( rendererInfo ) )
            return;
        
//      float x = element.x;
//      float y = element.y;
//      if (element.cameraRelative) {
//          x = (x - mCameraX) + halfWidth;
//          y = (y - mCameraY) + halfHeight;
//      }
        
        canvas.draw( mTexture, getDrawableObjectInfo( rendererInfo ) );
    }

    public void setTextureVertex( float tx1, float ty1, float tx2, float ty2 )
    {
        mTextureVertex[0] = tx1;
        mTextureVertex[1] = ty1;
        mTextureVertex[2] = tx2;
        mTextureVertex[3] = ty2;        
    }
    
    public void setPosition( float x, float y )
    {
        mPositionX = x;
        mPositionY = y;
    }
    
    public void setSize( int width, int height )
    {
        mWidth = width;
        mHeight = height;
    }
    
    public void setTexture( Texture texture )
    {
        mTexture = texture;
    }
    
    public Texture getTexture()
    {
        return mTexture;
    }
    
    public void setOpacity( float opacity )
    {
        mOpacity = opacity;
    }
    
    public float getPositionY()
    {
        return mPositionY;
    }
    
    public float getPositionX()
    {
        return mPositionX;
    }

    public float getOpacity()
    {
        return mOpacity;
    }
    
    private boolean isCulled( RendererInfo rendererInfo )
    {
        // TODO: view width and height are equal to the game width and height
//      if ( viewWidth > 0)
//      {
//          if ( xPosition + width < 0.0f || xPosition > viewWidth || 
//               yPosition + height < 0.0f || yPosition > viewHeight || 
//               opacity == 0.0f )
//          {
//              return true;
//          }
//      }
        
        return false;
    }
    
    private DrawableObjectInfo getDrawableObjectInfo( RendererInfo rendererInfo )
    {
        final float snappedX = (int) mPositionX;
        final float snappedY = (int) mPositionY;
        
        DRAWABLE_INFO.set( snappedX * rendererInfo.getScaleX(),
                           snappedY * rendererInfo.getScaleY(),
                           mWidth * rendererInfo.getScaleX(),
                           mHeight * rendererInfo.getScaleY(),
                           mTextureVertex[0], mTextureVertex[1], mTextureVertex[2], mTextureVertex[3],
                           mOpacity );
        
        return DRAWABLE_INFO;
    }
}
