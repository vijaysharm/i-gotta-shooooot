package com.igottashoot.game.rendering;

import com.igottashoot.game.animation.AnimationFrame;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.rendering.bitmap.DrawableBitmap;

public class DrawableTiledBitmap extends BaseObject implements DrawableBitmapAnimationResolver
{
    private int mNumberOfTileRows;
    private int mNumberOfTileColumns;
    private int mTileCount;

    public DrawableTiledBitmap( int rowTileSize, int columnTileSize )
    {
        mNumberOfTileRows = rowTileSize;
        mNumberOfTileColumns = columnTileSize;
        mTileCount = rowTileSize * columnTileSize;        
    }

    @Override
    public void resolve( DrawableBitmap bitmap, Texture texture, AnimationFrame animationFrame )
    {
        if ( texture == null || ! texture.isLoaded() )
            throw new IllegalStateException(); 
        
        int tileIndex = animationFrame.getTileIndex();
        
        if ( tileIndex == -1 )
            throw new IllegalStateException();

        if( tileIndex >= mTileCount )
            throw new IllegalStateException();
        
        setCurrentTileIndex( tileIndex % mNumberOfTileColumns, tileIndex / mNumberOfTileColumns, bitmap, texture );
    }

    private void setCurrentTileIndex( final int tileColumn, final int tileRow, final DrawableBitmap bitmap, Texture texture )
    {
        float tileWidth = texture.getWidth() / mNumberOfTileColumns;
        float tileHeight = texture.getHeight() / mNumberOfTileRows;
        
        float tileX1 = tileWidth * tileColumn;
        float tileY1 = tileHeight * tileRow;
        
        float tileX2 = tileX1 + tileHeight;
        float tileY2 = tileY1 + tileWidth;
        
        tileX1 /= texture.getWidth();
        tileX2 /= texture.getWidth();
        tileY1 /= texture.getHeight();
        tileY2 /= texture.getHeight();
        
        // TODO: You can change these values based on the direction the sprite
        // is facing. Moreover, we could cache these values (given that they
        // shouldn't really change).
       bitmap.setTextureVertex( tileX1, tileY1, tileX2, tileY2 );
       bitmap.setTexture( texture );
    }
}
