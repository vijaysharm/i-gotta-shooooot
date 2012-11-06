package com.igottashoot.game.rendering;

import com.igottashoot.game.animation.AnimationFrame;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.rendering.bitmap.DrawableBitmap;

public class DrawableTextureBitmap extends BaseObject implements DrawableBitmapAnimationResolver
{
    @Override
    public void resolve( DrawableBitmap bitmap, Texture texture, AnimationFrame animationFrame )
    {
        if ( animationFrame == null )
            throw new IllegalStateException();
        
        Texture animatedTexture = animationFrame.getTexture();
        
        if ( animatedTexture == null )
            throw new IllegalStateException();
        
        bitmap.setTextureVertex( 0, 0, 1, 1 );
        bitmap.setTexture( animatedTexture );        
    }
}
