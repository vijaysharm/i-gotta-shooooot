package com.igottashoot.game.events;

import com.igottashoot.game.collision.AABoxBoundingVolume;
import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.properties.GameObjectProperty;
import com.igottashoot.game.properties.SpriteProperty;

public class PlatformProperty extends BaseObject implements GameObjectProperty
{
    private final SpriteProperty mPlatformB;
    private final SpriteProperty mPlatformA;
    private final Texture mGroundTexture;

    public PlatformProperty( SpriteProperty platformA, SpriteProperty platformB, Texture groundTexture )
    {
        mPlatformA = platformA;
        mPlatformB = platformB;
        
        mGroundTexture = groundTexture;
        
        set( mPlatformA, mGroundTexture, 0, 0, 480, 30 );
        set( mPlatformB, mGroundTexture, 500, 0, 480, 20 );
    }

    @Override
    public void update( float timeDelta, GameObject parent )
    {

    }

    @Override
    public int getPriority()
    {
        return PropertyExecutionPhase.THINK.getPhase();
    }
    
    private static void set( SpriteProperty platform,
                             Texture texture,
                             int offsetX,
                             int offsetY,
                             int width,
                             int height )
    {
        platform.setOffset( offsetX, offsetY );
        platform.setSize( width, height );
        platform.setTexture( texture );
        platform.setBoundingVolume( new AABoxBoundingVolume( 0, 0, width, height ) );
        platform.setCollisionHandler( null );
    }
}
