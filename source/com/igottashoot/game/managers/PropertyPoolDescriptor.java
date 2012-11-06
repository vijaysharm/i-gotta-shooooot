package com.igottashoot.game.managers;

import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.properties.GameObjectProperty;

public class PropertyPoolDescriptor extends BaseObject
{
    private final int mSize;
    private final Class<? extends GameObjectProperty> mClazz;
    
    public PropertyPoolDescriptor( Class<? extends GameObjectProperty> clazz, int size )
    {
        mClazz = clazz;
        mSize = size;
    }
    
    public int getSize()
    {
        return mSize;
    }
    
    public Class<? extends GameObjectProperty> getClassType()
    {
        return mClazz;
    }
}