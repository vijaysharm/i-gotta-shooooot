package com.igottashoot.game.events;

import com.igottashoot.game.animation.AnimationFrame;
import com.igottashoot.game.animation.SpriteAnimation;
import com.igottashoot.game.collision.AABoxBoundingVolume;
import com.igottashoot.game.collision.BoundingVolume;
import com.igottashoot.game.core.GameEvent;
import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.properties.AnimatedSpriteProperty;
import com.igottashoot.game.properties.MovementProperty;
import com.igottashoot.game.properties.RenderingProperty;
import com.igottashoot.game.properties.SpriteProperty;
import com.igottashoot.game.properties.RenderingProperty.RenderPropertyPriority;
import com.igottashoot.game.rendering.DrawableBitmapAnimationResolver;
import com.igottashoot.game.rendering.DrawableTiledBitmap;
import com.opengl.test.core.LevelLoader;
import com.opengl.test.core.Resources;

public class CanabaltLoadEvent extends BaseObject implements GameEvent
{
    private Texture mPlayerTileTexture;
    private GameObject mPlayerObject;
    private RenderingProperty mPlayerRenderProperty;
    private AnimatedSpriteProperty mPlayerSprite;
    private MovementProperty mMovementProperty;
    private PlayerProperty mPlayerProperty;
    
    private GameObject mPlatformObject;
    private Texture mGroundTexture;
    private RenderingProperty mPlatformARenderProperty;
    private RenderingProperty mPlatformBRenderProperty;
    private SpriteProperty mPlatformASprite;
    private SpriteProperty mPlatformBSprite;
    private PlatformProperty mPlatformProperty;

    @Override
    public void create( LevelLoader levelLoader )
    {
        // Create the Player Object
        mPlayerTileTexture = levelLoader.allocateTexture( Resources.PLAYER_KEYFRAMES.getId() );
        
        mMovementProperty = new MovementProperty();
        mMovementProperty.setMaxVelocity( 1000f, 360f );
        mMovementProperty.setDrag( 640f, 0f );
        
        mPlayerRenderProperty = new RenderingProperty();
        mPlayerRenderProperty.setDrawablePriority( RenderPropertyPriority.PLAYER );

        BoundingVolume playerCollisionVolume = new AABoxBoundingVolume( 0, 0, 30, 30 );
        
        // run 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
        // f.r. 15, 28, 40
        SpriteAnimation runAnimation = new SpriteAnimation( 0, 16 );
        for ( int frameIndex = 0; frameIndex < 16; frameIndex++ )
            runAnimation.addAnimation( new AnimationFrame( frameIndex, 1.0f / 15f, playerCollisionVolume ) );
        runAnimation.setAnimationToLoop( true );
        
        // jump 16,17,18,19
        // f.r. = 12        
        SpriteAnimation jumpAnimation = new SpriteAnimation( 1, 4 );
        for ( int frameIndex = 16; frameIndex < 20; frameIndex++ )
            jumpAnimation.addAnimation( new AnimationFrame( frameIndex, 1f/ 12f, playerCollisionVolume ) );
        jumpAnimation.setAnimationToLoop( true );
        
        // fall 20,21,22,23,24,25,26
        // f.r. = 14
        SpriteAnimation fallAnimation = new SpriteAnimation( 2, 7 );
        for ( int frameIndex = 20; frameIndex < 27; frameIndex++ )
            fallAnimation.addAnimation( new AnimationFrame( frameIndex, 1f / 14f, playerCollisionVolume ) );
        fallAnimation.setAnimationToLoop( true );
        
        // stumble 27,28,29,30,31,32,33,34,35,36,37
        // f.r. = 14, 21, 28, 35
        SpriteAnimation stumbleAnimation = new SpriteAnimation( 3, 11 );
        for ( int frameIndex = 27; frameIndex < 38; frameIndex++ )
            stumbleAnimation.addAnimation( new AnimationFrame( frameIndex, 1f / 14f, playerCollisionVolume ) );
        stumbleAnimation.setAnimationToLoop( true );
        
        DrawableBitmapAnimationResolver resolver = new DrawableTiledBitmap( 2, 19 ); 
        
        mPlayerSprite = new AnimatedSpriteProperty();
        mPlayerSprite.setRenderingProperty( mPlayerRenderProperty );
        mPlayerSprite.setSize( 30, 30 );
        mPlayerSprite.setTexture( mPlayerTileTexture );
        mPlayerSprite.setTextureResolver( resolver );
        mPlayerSprite.setAllAnimations( runAnimation, jumpAnimation, fallAnimation, stumbleAnimation );
        mPlayerSprite.playAnimation( 0 );
        
        mPlayerProperty = new PlayerProperty();
        mPlayerProperty.setAnimatedSprite( mPlayerSprite );
        mPlayerSprite.setCollisionHandler( mPlayerProperty );
        
        mPlayerObject = levelLoader.allocateGameObject();
        mPlayerObject.addProperty( mPlayerRenderProperty );
        mPlayerObject.addProperty( mPlayerSprite );
        mPlayerObject.addProperty( mMovementProperty );
        mPlayerObject.addProperty( mPlayerProperty );

        mPlayerObject.setPositionX( 0 );
        mPlayerObject.setPositionY( 300 );
        mPlayerObject.setVelocityX( 125f );
        mPlayerObject.setVelocityY( 0f );
        mPlayerObject.setAccelerationX( 36f );
        mPlayerObject.setAccelerationY( -1200f );
        
        mPlayerObject.commitChanges();
        
        // Create Platform Objects
        mGroundTexture = levelLoader.allocateTexture( Resources.SPLASH_BACKGROUND.getId() );
        
        mPlatformARenderProperty = new RenderingProperty();
        mPlatformARenderProperty.setDrawablePriority( RenderPropertyPriority.BACKGROUND_START );
        
        mPlatformBRenderProperty = new RenderingProperty();
        mPlatformBRenderProperty.setDrawablePriority( RenderPropertyPriority.BACKGROUND_START );
        
        mPlatformASprite = new SpriteProperty();
        mPlatformASprite.setRenderingProperty( mPlatformARenderProperty );
        
        mPlatformBSprite = new SpriteProperty();
        mPlatformBSprite.setRenderingProperty( mPlatformBRenderProperty );
        
        mPlatformProperty = new PlatformProperty( mPlatformASprite, mPlatformBSprite, mGroundTexture );
        
        mPlatformObject = levelLoader.allocateGameObject();
        mPlatformObject.setPositionX( 0 );
        mPlatformObject.setPositionY( 0 );
        
        mPlatformObject.addProperty( mPlatformARenderProperty );
        mPlatformObject.addProperty( mPlatformASprite );
        mPlatformObject.addProperty( mPlatformProperty );
        mPlatformObject.addProperty( mPlatformBRenderProperty );
        mPlatformObject.addProperty( mPlatformBSprite );
        
        mPlatformObject.commitChanges();
    }

    @Override
    public void load( LevelLoader levelLoader )
    {
        levelLoader.addGameObject( mPlayerObject );
        levelLoader.addGameObject( mPlatformObject );
        levelLoader.setCameraTarget( mPlayerObject );
    }

    @Override
    public void unload( LevelLoader levelLoader )
    {
        levelLoader.removeGameObject( mPlayerObject );
        mPlayerObject.removeProperty( mPlayerSprite );
        mPlayerObject.removeProperty( mPlayerRenderProperty );
        mPlayerObject.removeProperty( mMovementProperty );
        mPlayerObject.removeProperty( mPlayerProperty );
        mPlayerObject.commitChanges();
        levelLoader.releaseTexture( mPlayerTileTexture );
        levelLoader.releaseGameObject( mPlayerObject );
        
        levelLoader.removeGameObject( mPlatformObject );
        mPlatformObject.removeProperty( mPlatformARenderProperty );
        mPlatformObject.removeProperty( mPlatformASprite );
        mPlatformObject.removeProperty( mPlatformBRenderProperty );        
        mPlatformObject.removeProperty( mPlatformProperty );
        mPlatformObject.removeProperty( mPlatformProperty );
        mPlatformObject.commitChanges();
        levelLoader.releaseTexture( mGroundTexture );
        levelLoader.releaseGameObject( mPlatformObject );
    }
}
