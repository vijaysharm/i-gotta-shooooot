package com.igottashoot.game.managers;

import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.rendering.TextureLoadingHandler;

public class TextureManager extends BaseObject
{
    private final Texture[] mTextures;
    
    public TextureManager( int maxSize )
    {
        mTextures = new Texture[ maxSize ];
        for ( int x = 0; x < mTextures.length; x++ )
            mTextures[x] = new Texture();
    }
    
    public Texture allocateTexture( int resourceID )
    {
        int textureIndex = findTextureIndex( resourceID );
        if ( textureIndex == -1 )
            return null; // "There is no more room to add textures"
        
        Texture texture = mTextures[ textureIndex ];
        if ( texture.getResourceId() == - 1 )
        {
            texture.setResourceId( resourceID );
            texture.setBindingName( -1 );
            texture.setLoaded( false );
            texture.setSize( 0, 0 );
        }
        
        return texture;
    }
    
    public Object[] loadTextures( TextureLoadingHandler handler )
    {
        return handler.loadTextures( mTextures );
    }
    
//    public void flushTextures( DrawingCanvas canvas )
//    {
//        for ( int index = 0; index < mTextures.length; index++ )
//        {
//            final Texture texture = mTextures[ index ];
//            if ( texture.isLoaded() )
//                canvas.releaseTexture( texture );
//        }
//    }
    
    /**
     * Returns the texture associated with the passed resource ID.
     * 
     * @param resourceID
     *            The resource ID of a bitmap.
     * @return An associated Texture object, or null if there is no associated
     *         texture in the library.
     */
    public Texture getTextureByResource( int resourceID )
    {
        int realIndex = findTextureIndex( resourceID );
        if ( realIndex == -1 )
            return null;

        return ( ( mTextures[realIndex].getResourceId() != resourceID ) ? null : mTextures[ realIndex ] );
    }

    /**
     * Returns the texture associated with the passed resource ID.
     * 
     * @return the expected index of this key or -1 if the cache is full.
     */    
    private int findTextureIndex( int key )
    {
        int startIndex = key % mTextures.length;
        for ( int index = 0; index < mTextures.length; index++ )
        {
            final int actualIndex = ( startIndex + index ) % mTextures.length;
            Texture texture = mTextures[ actualIndex ];

            if ( ( texture.getResourceId() == key ) || ( texture.getResourceId() == -1 ) )
                return actualIndex;
        }

        return -1;
    }
}
