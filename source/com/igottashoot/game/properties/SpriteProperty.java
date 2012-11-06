package com.igottashoot.game.properties;

import com.igottashoot.game.collision.BoundingVolume;
import com.igottashoot.game.collision.CollisionHandler;
import com.igottashoot.game.core.GameCollisionHandler;
import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.math.Vector2D;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.rendering.bitmap.DrawableBitmap;

public class SpriteProperty extends BaseObject implements GameObjectProperty
{
    private final DrawableBitmap mBitmap;

    private Texture mTexture;
    private RenderingProperty mRenderProperty;
    private BoundingVolume mBoundingVolume;
    private CollisionHandler mCollisionHandler;
    
    private Vector2D mOffset;
    private int mHeight;
    private int mWidth;
    private float mOpacity;
    
    public SpriteProperty()
    {
        mBitmap = new DrawableBitmap();
        mOffset = new Vector2D();
        
        reset();
    }
    
    @Override
    public void reset()
    {
        mWidth = 0;
        mHeight = 0;
        mOpacity = 1.0f;
        mOffset.zero();
        mBitmap.reset();
        
        mCollisionHandler = null;
        mBoundingVolume = null;
        mRenderProperty = null;
        mTexture = null;
    }
    
    @Override
    public void update( float timeDelta, GameObject parent )
    {
        checkState();
        readyTheBitmap( parent );
        registerCollisionObject( parent, parent.getRegistry().getCollisionHandler() );
        mRenderProperty.setDrawable( mBitmap );
    }

    @Override
    public int getPriority()
    {
        return PropertyExecutionPhase.PRE_DRAW.getPhase();
    }
    
    public void setSize( int width, int height )
    {
        mWidth = width;
        mHeight = height;
    }
    
    public void setOpacity( float opacity )
    {
        mOpacity = opacity;
    }
    
    public float getOpacity()
    {
        return mOpacity;
    }
    
    public void setTexture( Texture texture )
    {
        mTexture = texture;
    }
    
    public void setRenderingProperty( RenderingProperty property )
    {
        mRenderProperty = property;
    }
    
    public void setOffset( float offsetX, float offsetY )
    {
        mOffset.setX( offsetX );
        mOffset.setY( offsetY );
    }
    
    public float getOffsetX()
    {
        return mOffset.getX();
    }
    
    public float getOffsetY()
    {
        return mOffset.getY();
    }
    
    public void setBoundingVolume( BoundingVolume boundingVolume )
    {
        mBoundingVolume = boundingVolume;
    }
    
    public void setCollisionHandler( CollisionHandler collisionHandler )
    {
        mCollisionHandler = collisionHandler;
    }
    
    private void registerCollisionObject( GameObject parent, GameCollisionHandler collisionHandler )
    {
        if ( mBoundingVolume == null )
            return;
        
        mBoundingVolume.setOffset( getOffsetX() + parent.getPositionX(), getOffsetY() + parent.getPositionY() );
        collisionHandler.registerCollisionObject( parent, mBoundingVolume, mCollisionHandler );
    }
    
    private void readyTheBitmap( GameObject parent )
    {
        mBitmap.setPosition( getOffsetX() + parent.getPositionX(), getOffsetY() + parent.getPositionY() );
        mBitmap.setSize( mWidth, mHeight );
        mBitmap.setOpacity( mOpacity );
        mBitmap.setTexture( mTexture );
        mBitmap.setTextureVertex( 0, 0, 1, 1 );
    }
    
    private void checkState()
    {
        if ( mRenderProperty == null )
            throw new IllegalStateException();
        
        if ( mTexture == null )
            throw new IllegalStateException();
    }
}
