package com.igottashoot.game.rendering.bitmap;

import com.igottashoot.game.rendering.DrawableObjectInfo;

class DefaultDrawableObjectInfo implements DrawableObjectInfo
{
    private float mVertexXPosition;
    private float mVertexYPosition;
    private float mVertexWidth;
    private float mVertexHeight;
    private float mOpacity;

    private float mTextureX1;
    private float mTextureY1;
    private float mTextureX2;
    private float mTextureY2;

    @Override
    public float getVertexPositionX()
    {
        return mVertexXPosition;
    }

    @Override
    public float getVertexPositionY()
    {
        return mVertexYPosition;
    }

    @Override
    public float getVertexWidth()
    {
        return mVertexWidth;
    }

    @Override
    public float getVertexHeight()
    {
        return mVertexHeight;
    }

    @Override
    public float getOpacity()
    {
        return mOpacity;
    }

    @Override    
    public float getTexturePositionX1()
    {
        return mTextureX1;
    }
    
    @Override    
    public float getTexturePositionY1()
    {
        return mTextureY1;
    }

    @Override
    public float getTexturePositionX2()
    {
        return mTextureX2;
    }
    
    @Override
    public float getTexturePositionY2()
    {
        return mTextureY2;
    }    

    public void set( float xPosition,
                     float yPosition,
                     float width,
                     float height,
                     float textureX1,
                     float textureY1,
                     float textureX2,
                     float textureY2,
                     float opacity )
    {
        mVertexXPosition = xPosition;
        mVertexYPosition = yPosition;
        mVertexWidth = width;
        mVertexHeight = height;
        
        mTextureX1 = textureX1;
        mTextureY1 = textureY1;
        mTextureX2 = textureX2;
        mTextureY2 = textureY2;
        
        mOpacity = opacity;
    }
}
