package com.igottashoot.game.events;

import com.igottashoot.game.input.TouchScreenLocation;
import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.UpdateableObject;
import com.igottashoot.game.properties.GameObjectProperty;
import com.igottashoot.game.properties.SpriteProperty;

/**
 * Defines the transitions between loading -> main menu screen <-> about screen.
 * There are many magic numbers here, but they are just defining the location of
 * the various controls on screen. All of the numbers are with respect to the
 * screen size also, which is assumed to be 480 by 320. If this resolution
 * changes, then these values may no longer apply.
 * 
 * Most of these actions can be factored out into property objects which the
 * {@link GameObject} can react to. That would leave this object in charge of
 * changing the state based on some of the GameObjects state.
 */
public class MenuProperty extends BaseObject implements GameObjectProperty
{
    private static final float TWO_PI = (float) ( Math.PI * 2 );
    
    private final LoadingState mLoadingState;
    private final ToAboutState mAboutState;
    private final ToMainMenuState mMainMenuState;
    
    private final SpriteProperty mBackgroundSprite;
    private final SpriteProperty mLogoSprite;
    private final SpriteProperty mAboutButtonSprite;
    private final SpriteProperty mPlayButtonSprite;
    private final SpriteProperty mAboutTopBarSprite;
    private final SpriteProperty mBackButtonSprite;
    
    private UpdateableObject<GameObject> mCurrentState;
    
    public MenuProperty( SpriteProperty backgroundDrawable,
                         SpriteProperty logoDrawable,
                         SpriteProperty aboutButtonDrawable,
                         SpriteProperty playButtonDrawable,
                         SpriteProperty aboutTopBarDrawable,
                         SpriteProperty backButtonDrawable )
    {
        mBackgroundSprite = backgroundDrawable;
        mLogoSprite = logoDrawable;
        mAboutButtonSprite = aboutButtonDrawable;
        mPlayButtonSprite = playButtonDrawable;
        mAboutTopBarSprite = aboutTopBarDrawable;
        mBackButtonSprite = backButtonDrawable;
        
        mLoadingState = new LoadingState();
        mAboutState = new ToAboutState();
        mMainMenuState = new ToMainMenuState();
        
        reset();
    }
    
    @Override
    public void reset()
    {
        mLoadingState.reset();
        mCurrentState = mLoadingState;
    }
    
    @Override
    public void update( float timeDelta, GameObject parent )
    {
        mCurrentState.update( timeDelta, parent );
    }

    @Override
    public int getPriority()
    {
        return GameObjectProperty.PropertyExecutionPhase.THINK.getPhase();
    }

    private final class LoadingState extends BaseObject implements UpdateableObject<GameObject>
    {
        private float mBackgroundVelocityY;
        
        public LoadingState()
        {
            reset();
        }
        
        @Override
        public void reset()
        {
            mBackgroundVelocityY = -254;

            mBackgroundSprite.setOffset( 0f, 320 );        

            mLogoSprite.setOffset( 30f, 160f );
            mLogoSprite.setOpacity( 0.0f );
            
            mAboutButtonSprite.setOffset( 8f, 10f );
            mAboutButtonSprite.setOpacity( 0.0f );        
            
            mPlayButtonSprite.setOffset( 352, 10f );
            mPlayButtonSprite.setOpacity( 0.0f );
            
            mAboutTopBarSprite.setOffset( 0f, 320 );
            mBackButtonSprite.setOffset( 488, 10f );
        }

        @Override
        public void update( float timeDelta, GameObject parent )
        {
            float elapsedTime = timeDelta;

            checkButtonPressed( parent );
            updateBackground( elapsedTime );
            updateLogo( elapsedTime );
            updateButtons();
        }

        private void checkButtonPressed( GameObject parent )
        {
            if ( mPlayButtonSprite.getOpacity() != 1 || mAboutButtonSprite.getOpacity() != 1 )
                return;
            
            TouchScreenLocation touchScreenLocation = parent.getRegistry().getTouchScreenLocation();
            
            if ( touchScreenLocation.isPressed( 0, 0, 0, 0 ) )
            {
                mAboutState.reset();
                mCurrentState = mAboutState;
//                parent.getRegistry().load( new LoadLevelEvent() );
            }
        }

        private void updateBackground( float elapsedTime )
        {
            float velocityY = ( computeVelocity( mBackgroundVelocityY, 0, 100, 1000, elapsedTime ) - mBackgroundVelocityY ) / 2.0f;
            
            mBackgroundVelocityY += velocityY;
            float yd = mBackgroundVelocityY * elapsedTime;
            mBackgroundVelocityY += velocityY;
            float x = mBackgroundSprite.getOffsetX();
            float y = mBackgroundSprite.getOffsetY();
            y += yd;
            mBackgroundSprite.setOffset( x, y );
        }

        private void updateLogo( float elapsedTime )
        {
            float opacity = mLogoSprite.getOpacity();
            if ( mBackgroundVelocityY == 0 && opacity < 1 )
                mLogoSprite.setOpacity( opacity + elapsedTime );
        }

        private void updateButtons()
        {
            if ( mAboutButtonSprite.getOpacity() < 1 && mBackgroundVelocityY == 0 )
            {
                mPlayButtonSprite.setOpacity( 1.0f );
                mAboutButtonSprite.setOpacity( 1.0f );
            }
        }
    }

    private final class ToMainMenuState extends BaseObject implements UpdateableObject<GameObject>
    {
        private float mTimer;
        
        @Override
        public void reset()
        {
            mTimer = 0f;
            
            mBackgroundSprite.setOffset( 0f, 0f );        

            mLogoSprite.setOffset( -450f, 160f );
            mLogoSprite.setOpacity( 1.0f );
            
            mAboutButtonSprite.setOffset( -472f, 10f );
            mAboutButtonSprite.setOpacity( 1.0f );        
            
            mPlayButtonSprite.setOffset( -128f, 10f );
            mPlayButtonSprite.setOpacity( 1.0f );
            
            mAboutTopBarSprite.setOffset( 0f, 278f );
            mBackButtonSprite.setOffset( 8f, 10f );            
        }
        
        @Override
        public void update( float timeDelta, GameObject parent )
        {
            mTimer += timeDelta;
            float timer = mTimer;
            float duration = 1.0f;
            float delta = (float) ( timer / duration - Math.sin( timer / duration * TWO_PI ) / ( TWO_PI ) );
            
            checkButtonPressed( parent );
            move( mLogoSprite, delta, -450f, 160f, 30f, 160f );
            move( mAboutButtonSprite, delta, -472f, 10f, 8f, 10f );
            move( mAboutTopBarSprite, delta, 0f, 278f, 0f, 320f );
            move( mPlayButtonSprite, delta, -128, 10f, 352f, 10f );
            move( mBackButtonSprite, delta, 8f, 10f, 488f, 10f );
        }
        
        private void checkButtonPressed( GameObject parent )
        {
            float positionX = mAboutButtonSprite.getOffsetX();
            float positionY = mAboutButtonSprite.getOffsetY();
            if ( positionX != 8 || positionY != 10 )
                return;
            
            TouchScreenLocation touchScreenLocation = parent.getRegistry().getTouchScreenLocation();
            
            if ( touchScreenLocation.isPressed( 0, 0, 0, 0 ) )
            {
                mAboutState.reset();
                mCurrentState = mAboutState;
            }
        }        
    }
    
    private final class ToAboutState extends BaseObject implements UpdateableObject<GameObject>
    {
        private float mTimer;
        
        @Override
        public void reset()
        {
            mTimer = 0f;
            
            mBackgroundSprite.setOffset( 0f, 0f );

            mLogoSprite.setOffset( 30f, 160f );
            mLogoSprite.setOpacity( 1.0f );
            
            mAboutButtonSprite.setOffset( 8f, 10f );
            mAboutButtonSprite.setOpacity( 1.0f );        
            
            mPlayButtonSprite.setOffset( 352, 10f );
            mPlayButtonSprite.setOpacity( 1.0f );
            
            mAboutTopBarSprite.setOffset( 0f, 320f );
            mBackButtonSprite.setOffset( 488f, 10f );         
        }
        
        @Override
        public void update( float timeDelta, GameObject parent )
        {
            mTimer += timeDelta;
            float timer = mTimer;
            float duration = 1.0f;
            float delta = (float) ( timer / duration - Math.sin( timer / duration * TWO_PI ) / ( TWO_PI ) );
            
            checkButtonPressed( parent );
            move( mLogoSprite, delta, 30f, 160f, -450f, 160f );
            move( mAboutButtonSprite, delta, 8f, 10f, -472f, 10f );
            move( mAboutTopBarSprite, delta, 0f, 320f, 0f, 278f );
            move( mPlayButtonSprite, delta, 352f, 10f, -128, 10f );
            move( mBackButtonSprite, delta, 488f, 10f, 8f, 10f );
        }
        
        private void checkButtonPressed( GameObject parent )
        {
            float positionX = mBackButtonSprite.getOffsetX();
            float positionY = mBackButtonSprite.getOffsetY();
            if ( positionX != 8 || positionY != 10 )
                return;
            
            TouchScreenLocation touchScreenLocation = parent.getRegistry().getTouchScreenLocation();
            
            if ( touchScreenLocation.isPressed( 0, 0, 0, 0 ) )
            {
                mMainMenuState.reset();
                mCurrentState = mMainMenuState;
            }
        }          
    }

    private static void move( SpriteProperty sprite,
                              float delta,
                              float fromXPoint,
                              float fromYPoint,
                              float toXPoint,
                              float toYPoint )
    {
        
        float x;
        float y;
        
        if ( delta < 1.0 )
        {
            x = delta * ( toXPoint - fromXPoint ) + fromXPoint;
            y = delta * ( toYPoint - fromYPoint ) + fromYPoint;
        }
        else
        {
            x = toXPoint;
            y = toYPoint;
        }
        
        sprite.setOffset( x, y );
    }
    
    private static float computeVelocity( float velocity,
                                          float acceleration, 
                                          float drag,
                                          float max,
                                          float elapsedTime )
    {
        if ( acceleration != 0 )
        {
            velocity += acceleration * elapsedTime;
        }
        else if ( drag != 0 )
        {
            float d = drag * elapsedTime;
            if ( velocity - d > 0 )
                velocity -= d;
            else if ( velocity + d < 0 )
                velocity += d;
            else
                velocity = 0;
        }

        if ( ( velocity != 0 ) && ( max != 10000 ) )
        {
            if ( velocity > max )
                velocity = max;
            else if ( velocity < -max )
                velocity = -max;
        }
        
        return velocity;
    }
}
