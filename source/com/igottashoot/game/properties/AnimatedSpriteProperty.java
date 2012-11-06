package com.igottashoot.game.properties;

import com.igottashoot.game.animation.AnimationFrame;
import com.igottashoot.game.animation.SpriteAnimation;
import com.igottashoot.game.collision.BoundingVolume;
import com.igottashoot.game.collision.CollisionHandler;
import com.igottashoot.game.core.GameCollisionHandler;
import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.managers.OrderedObjectManager;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.rendering.DrawableBitmapAnimationResolver;
import com.igottashoot.game.rendering.bitmap.DrawableBitmap;

public class AnimatedSpriteProperty extends BaseObject implements GameObjectProperty
{
    private final CurrentAnimationInformation mCurrentAnimation;
    private final DrawableBitmap mBitmap;

    private OrderedObjectManager<SpriteAnimation> mAnimations;
    private DrawableBitmapAnimationResolver mResolver;
    private Texture mTexture;
    private RenderingProperty mRenderProperty;
    private CollisionHandler mCollisionHandler;
    
    private int mHeight;
    private int mWidth;
    private float mOpacity;
    
    private float mAnitmationTime;
    
    public AnimatedSpriteProperty()
    {
        mCurrentAnimation = new CurrentAnimationInformation();
        mAnimations = OrderedObjectManager.newManager( 0 );
        mBitmap = new DrawableBitmap();
        
        reset();
    }
    
    @Override
    public void reset()
    {
        mWidth = 0;
        mHeight = 0;
        mOpacity = 1.0f;
        
        mAnitmationTime = 0f;
        mCurrentAnimation.reset();
        mRenderProperty = null;
        mTexture = null;
        mResolver = null;
        mCollisionHandler = null;
        
        mAnimations.removeAll();
        mAnimations.commitUpdates();
    }
    
    @Override
    public void update( float timeDelta, GameObject parent )
    {
        mAnitmationTime += timeDelta;
        mAnimations.commitUpdates();
        
        checkState();
        
        if ( mAnimations.getCount() <= 0 )
        {
            clearAssociatedStates();
            return;
        }
        
        SpriteAnimation currentAnitmation = mCurrentAnimation.getAnimation();
        if ( currentAnitmation == null )
        {
            currentAnitmation = mAnimations.get( mCurrentAnimation.getAnimationId() );
            if ( currentAnitmation == null )
                currentAnitmation = mAnimations.get( 0 );
            
            mCurrentAnimation.setAnimation( currentAnitmation );
        }
        
        if ( currentAnitmation == null )
        {
            clearAssociatedStates();
            return;
        }
        
        AnimationFrame animationFrame = currentAnitmation.getAnimationFrame( mAnitmationTime );
        if ( animationFrame == null )
        {
            clearAssociatedStates();
            return;            
        }
        
        readyTheBitmap( parent, animationFrame );
        registerCollisionObject( parent, animationFrame, parent.getRegistry().getCollisionHandler() );
        
        mRenderProperty.setDrawable( mBitmap );
    }

    @Override
    public int getPriority()
    {
        return PropertyExecutionPhase.PRE_DRAW.getPhase();
    }
    
    public void playAnimation( int id )
    {
        if ( mCurrentAnimation.getAnimationId() == id )
            return;
        
        mAnitmationTime = 0f;
        mCurrentAnimation.setAnimationId( id );
        mCurrentAnimation.setAnimation( null );
    }
    
    public void setSize( int width, int height )
    {
        mWidth = width;
        mHeight = height;
    }
    
    public void setTextureResolver( DrawableBitmapAnimationResolver resolver )
    {
        mResolver = resolver;
    }
    
    public void setTexture( Texture texture )
    {
        mTexture = texture;
    }
    
    public void setRenderingProperty( RenderingProperty property )
    {
        mRenderProperty = property;
    }
    
    public void setAllAnimations( SpriteAnimation...animations )
    {
        if ( animations.length == 0 )
            throw new IllegalArgumentException();
        
        mAnimations = OrderedObjectManager.newManager( animations.length );

        for ( SpriteAnimation anitmation : animations )
            mAnimations.add( anitmation );
    }

    public void setCollisionHandler( CollisionHandler collisionHandler )
    {
        mCollisionHandler = collisionHandler;
    }
    
    private void registerCollisionObject( GameObject parent, AnimationFrame animationFrame, GameCollisionHandler collisionHandler )
    {
        BoundingVolume collisionVolume = animationFrame.getCollisionVolume();
        if ( collisionVolume == null )
            return;
        
        collisionVolume.setOffset( parent.getPositionX(), parent.getPositionY() );
        collisionHandler.registerCollisionObject( parent, collisionVolume, mCollisionHandler );
    }
    
    private void readyTheBitmap( GameObject parent, AnimationFrame animationFrame )
    {
        mBitmap.setPosition( parent.getPositionX(), parent.getPositionY() );
        mBitmap.setSize( mWidth, mHeight );
        mBitmap.setOpacity( mOpacity );
        
        mResolver.resolve( mBitmap, mTexture, animationFrame );
    }
    
    private void checkState()
    {
        if ( mRenderProperty == null )
            throw new IllegalStateException();
        
        if ( mCurrentAnimation.getAnimationId() == -1 )
            throw new IllegalStateException();
    }

    private void clearAssociatedStates()
    {
        mRenderProperty.setDrawable( null );
        throw new IllegalStateException();
    }
    
    private static final class CurrentAnimationInformation extends BaseObject
    {
        private SpriteAnimation mAnimation;
        private int mAnimationId;
        
        public CurrentAnimationInformation()
        {
            reset();
        }
        
        @Override
        public void reset()
        {
            mAnimation = null;
            mAnimationId = -1;
        }
        
        public SpriteAnimation getAnimation()
        {
            return mAnimation;
        }
        
        public void setAnimation( SpriteAnimation animation )
        {
            mAnimation = animation;
        }
        
        public int getAnimationId()
        {
            return mAnimationId;
        }
        
        public void setAnimationId( int animationIndex )
        {
            mAnimationId = animationIndex;
        }
    }
}
