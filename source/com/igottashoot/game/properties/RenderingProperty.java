package com.igottashoot.game.properties;

import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.DrawableObject;
import com.igottashoot.game.primitives.RenderElement;

public class RenderingProperty extends BaseObject implements GameObjectProperty
{
    public enum RenderPropertyPriority
    {
        BACKGROUND_START( -100 ),
        THE_SOURCE_START( -5 ),
        FOREGROUND( 0 ),
        EFFECT( 5 ),
        GENERAL_OBJECT( 10 ),
        GENERAL_ENEMY( 15 ),
        NPC( 15 ),
        PLAYER( 20 ),
        FOREGROUND_EFFECT( 30 ),
        PROJECTILE( 40 ),
        FOREGROUND_OBJECT( 50 ),
        OVERLAY( 70 ),
        HUD( 100 ),
        FADE( 200 );
       
        private final int mPriority;
        RenderPropertyPriority( int priority )
        {
            mPriority = priority;
        }
        
        public int getPriority()
        {
            return mPriority;
        }
    }
    
    private static final int TEXTURE_SORT_BUCKET_SIZE = 1000;
    
    private DrawableObject mDrawable;
    private RenderPropertyPriority mDrawablePriority;
    
    public RenderingProperty()
    {
        mDrawablePriority = RenderPropertyPriority.BACKGROUND_START;
    }
    
    @Override
    public void update( float timeDelta, GameObject parent )
    {
        if ( mDrawable == null )
            return;

        RenderElement renderElement = parent.getRegistry().allocateRenderElement();
        if ( renderElement == null )
            return;

        renderElement.setPriority( calculateRenderElementPriority( mDrawablePriority ) );
        renderElement.setDrawable( mDrawable );
        parent.getRegistry().scheduleForDraw( renderElement );
    }

    @Override
    public int getPriority()
    {
        return PropertyExecutionPhase.DRAW.getPhase();
    }
    
    public void setDrawablePriority( RenderPropertyPriority priority )
    {
        mDrawablePriority = priority;
    }
    
    void setDrawable( DrawableObject drawable )
    {
        mDrawable = drawable;
    }
    
    private static int calculateRenderElementPriority( RenderPropertyPriority priority )
    {
        final int sortBucket = priority.getPriority() * TEXTURE_SORT_BUCKET_SIZE;
        int sortOffset = 0;
//        if (drawable != null) {
//            Texture tex = drawable.getTexture();
//            if (tex != null) {
//                sortOffset = (tex.resource % TEXTURE_SORT_BUCKET_SIZE) * Utils.sign(priority);
//            }
//        }
        
        return sortBucket + sortOffset;
    }
}