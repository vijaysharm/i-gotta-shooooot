package com.igottashoot.game.managers;

import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.rendering.DrawableTextureBitmap;
import com.igottashoot.game.rendering.bitmap.DrawableBitmap;
import com.igottashoot.game.utilities.ObjectPool;

/**
 * TO BE DELETED!
 */
public class DrawableObjectPool extends ObjectPool<DrawableTextureBitmap>
{
    public DrawableObjectPool( int maxBitmapCount )
    {
        super( maxBitmapCount );
    }

    public DrawableTextureBitmap allocateBitmap( Texture texture )
    {
        DrawableBitmap drawBitmap = new DrawableBitmap();
        drawBitmap.setTexture( texture );
        
        DrawableTextureBitmap bitmap = super.allocate();
        
        return bitmap;
    }
    
    @Override
    protected DrawableTextureBitmap create()
    {
        return new DrawableTextureBitmap();
    }
}
