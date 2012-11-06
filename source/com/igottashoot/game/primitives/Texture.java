package com.igottashoot.game.primitives;

public class Texture extends BaseObject
{
    private int mResourceId;
    private int mBindingId;
    private int mWidth;
    private int mHeight;
    
    private volatile boolean mIsloaded;

    public Texture()
    {
        reset();
    }

    public void setResourceId( int resourceId )
    {
        mResourceId = resourceId;
    }

    public int getResourceId()
    {
        return mResourceId;
    }

    public int getBindingId()
    {
        return mBindingId;
    }

    public void setBindingName( int name )
    {
        mBindingId = name;
    }

    public void setSize( int width, int height )
    {
        mWidth = width;
        mHeight = height;
    }
    
    public int getWidth()
    {
        return mWidth;
    }

    public int getHeight()
    {
        return mHeight;
    }

    public boolean isLoaded()
    {
        return mIsloaded;
    }

    public void setLoaded( boolean loaded )
    {
        mIsloaded = loaded;
    }
    
    @Override
    public void reset()
    {
        mResourceId = -1;
        mBindingId = -1;
        mIsloaded = false;
        mHeight = 0;
        mWidth = 0;
    }    
}
