package com.igottashoot.game.animation;

import com.igottashoot.game.collision.BoundingVolume;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.Texture;

public class AnimationFrame extends BaseObject
{
    private final float mAnitmationFrameTime;
    private final int mTextureIndex;
    private final Texture mTexture;
    private final BoundingVolume mCollisionVolume;
    
    public AnimationFrame( int textureIndex, float anitmationFrameTime, BoundingVolume boundingVolume )
    {
        this( textureIndex, null, anitmationFrameTime, boundingVolume );
    }
    
    public AnimationFrame( Texture texture, float anitmationFrameTime, BoundingVolume boundingVolume )
    {
        this( -1, texture, anitmationFrameTime, boundingVolume );
    }
    
    private AnimationFrame( int textureIndex, Texture texture, float anitmationFrameTime, BoundingVolume boundingVolume )
    {
        mTextureIndex = textureIndex;
        mAnitmationFrameTime = anitmationFrameTime;
        mTexture = texture;
        mCollisionVolume = boundingVolume;
    }
    
    float getAnitmationFrameTime()
    {
        return mAnitmationFrameTime;
    }
    
    public int getTileIndex()
    {
        return mTextureIndex;
    }
    
    public Texture getTexture()
    {
        return mTexture;
    }
    
    public BoundingVolume getCollisionVolume()
    {
        return mCollisionVolume;
    }
}
