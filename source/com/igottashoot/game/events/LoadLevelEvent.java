package com.igottashoot.game.events;

import com.igottashoot.game.core.GameEvent;
import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.properties.RenderingProperty;
import com.igottashoot.game.properties.RenderingProperty.RenderPropertyPriority;
import com.igottashoot.game.properties.SpriteProperty;
import com.opengl.test.core.LevelLoader;
import com.opengl.test.core.Resources;

public class LoadLevelEvent extends BaseObject implements GameEvent
{
    private GameObject mBackgroundObject;
    
    private Texture mBackgroundTexture;
    private Texture mLogoTexture;
    private Texture mButtonTexture;
    private Texture mAboutTopBarTexture;
    private Texture mBackButtonTexture;
    
    private RenderingProperty mBackgroundRenderProperty;
    private RenderingProperty mLogoRenderProperty;
    private RenderingProperty mAboutButtonRenderProperty;
    private RenderingProperty mPlayButtonRenderProperty;
    private RenderingProperty mBackButtonRenderProperty;
    private RenderingProperty mAboutTopBarRenderProperty;
    private MenuProperty mMenuProperty;

    private SpriteProperty mBackgroundSprite;
    private SpriteProperty mLogoSprite;
    private SpriteProperty mAboutButtonSprite;
    private SpriteProperty mPlayButtonSprite;
    private SpriteProperty mBackButtonSprite;
    private SpriteProperty mAboutTopBarSprite;

    @Override
    public void create( final LevelLoader levelLoader )
    {
        mBackgroundTexture = levelLoader.allocateTexture( Resources.SPLASH_BACKGROUND.getId() );
        mLogoTexture = levelLoader.allocateTexture( Resources.SPLASH_LOGO.getId() );
        mButtonTexture = levelLoader.allocateTexture( Resources.GENERIC_BUTTON.getId() );
        mBackButtonTexture = levelLoader.allocateTexture( Resources.BACK_BUTTON.getId() );
        mAboutTopBarTexture = levelLoader.allocateTexture( Resources.SPLASH_TOP_BAR.getId() );
        
        mBackgroundRenderProperty = new RenderingProperty();
        mBackgroundRenderProperty.setDrawablePriority( RenderPropertyPriority.BACKGROUND_START );
        
        mLogoRenderProperty = new RenderingProperty();
        mLogoRenderProperty.setDrawablePriority( RenderPropertyPriority.FOREGROUND );
        
        mAboutButtonRenderProperty = new RenderingProperty();
        mAboutButtonRenderProperty.setDrawablePriority( RenderPropertyPriority.EFFECT );
        
        mPlayButtonRenderProperty = new RenderingProperty();
        mPlayButtonRenderProperty.setDrawablePriority( RenderPropertyPriority.EFFECT );
        
        mBackButtonRenderProperty = new RenderingProperty();
        mBackButtonRenderProperty.setDrawablePriority( RenderPropertyPriority.EFFECT );
        
        mAboutTopBarRenderProperty = new RenderingProperty();
        mAboutTopBarRenderProperty.setDrawablePriority( RenderPropertyPriority.EFFECT );
        
        mBackgroundSprite = new SpriteProperty();
        mBackgroundSprite.setTexture( mBackgroundTexture );
        mBackgroundSprite.setSize( 480, 320 );
        mBackgroundSprite.setRenderingProperty( mBackgroundRenderProperty );
        
        mLogoSprite = new SpriteProperty();
        mLogoSprite.setTexture( mLogoTexture );
        mLogoSprite.setSize( 409, 48 );
        mLogoSprite.setRenderingProperty( mLogoRenderProperty );
        
        mAboutButtonSprite = new SpriteProperty();
        mAboutButtonSprite.setTexture( mButtonTexture );
        mAboutButtonSprite.setSize( 120, 26 );
        mAboutButtonSprite.setRenderingProperty( mAboutButtonRenderProperty );
        
        mPlayButtonSprite = new SpriteProperty();
        mPlayButtonSprite.setTexture( mButtonTexture );
        mPlayButtonSprite.setSize( 120, 26 );
        mPlayButtonSprite.setRenderingProperty( mPlayButtonRenderProperty );
        
        mBackButtonSprite = new SpriteProperty();
        mBackButtonSprite.setTexture( mBackButtonTexture );
        mBackButtonSprite.setSize( 36, 30 );
        mBackButtonSprite.setRenderingProperty( mBackButtonRenderProperty );
        
        mAboutTopBarSprite = new SpriteProperty();
        mAboutTopBarSprite.setTexture( mAboutTopBarTexture );
        mAboutTopBarSprite.setSize( 480, 42 );
        mAboutTopBarSprite.setRenderingProperty( mAboutTopBarRenderProperty );
        
        mMenuProperty = new MenuProperty( mBackgroundSprite,
                                          mLogoSprite,
                                          mAboutButtonSprite,
                                          mPlayButtonSprite,
                                          mAboutTopBarSprite, 
                                          mBackButtonSprite );
        
        mBackgroundObject = levelLoader.allocateGameObject();
        
        mBackgroundObject.addProperty( mMenuProperty );
        
        mBackgroundObject.addProperty( mBackgroundSprite );
        mBackgroundObject.addProperty( mLogoSprite );
        mBackgroundObject.addProperty( mAboutButtonSprite );
        mBackgroundObject.addProperty( mPlayButtonSprite );
        mBackgroundObject.addProperty( mBackButtonSprite );
        mBackgroundObject.addProperty( mAboutTopBarSprite );
        
        mBackgroundObject.addProperty( mBackgroundRenderProperty );
        mBackgroundObject.addProperty( mLogoRenderProperty );
        mBackgroundObject.addProperty( mAboutButtonRenderProperty );
        mBackgroundObject.addProperty( mPlayButtonRenderProperty );
        mBackgroundObject.addProperty( mBackButtonRenderProperty );
        mBackgroundObject.addProperty( mAboutTopBarRenderProperty );
        
        mBackgroundObject.commitChanges();
    }
    
    @Override
    public void load( LevelLoader levelLoader )
    {
        // add game object to game object manager
        levelLoader.addGameObject( mBackgroundObject );
    }
    
    @Override
    public void unload( LevelLoader levelLoader )
    {
        levelLoader.removeGameObject( mBackgroundObject );
        
        mBackgroundObject.removeProperty( mBackgroundSprite );
        mBackgroundObject.removeProperty( mLogoSprite );
        mBackgroundObject.removeProperty( mAboutButtonSprite );
        mBackgroundObject.removeProperty( mPlayButtonSprite );
        mBackgroundObject.removeProperty( mBackButtonSprite );
        mBackgroundObject.removeProperty( mAboutTopBarSprite );
        
        mBackgroundObject.removeProperty( mBackgroundRenderProperty );
        mBackgroundObject.removeProperty( mLogoRenderProperty );
        mBackgroundObject.removeProperty( mAboutButtonRenderProperty );
        mBackgroundObject.removeProperty( mPlayButtonRenderProperty );
        mBackgroundObject.removeProperty( mBackButtonRenderProperty );
        mBackgroundObject.removeProperty( mAboutTopBarRenderProperty );
        
        mBackgroundObject.removeProperty( mMenuProperty );
        
        mBackgroundObject.commitChanges();
        
        levelLoader.releaseTexture( mBackgroundTexture );
        levelLoader.releaseTexture( mLogoTexture );
        levelLoader.releaseTexture( mButtonTexture );
        levelLoader.releaseTexture( mBackButtonTexture );
        levelLoader.releaseTexture( mAboutTopBarTexture );
        
        levelLoader.releaseGameObject( mBackgroundObject );
    }
}
