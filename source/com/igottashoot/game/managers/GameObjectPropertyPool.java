package com.igottashoot.game.managers;

import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.properties.GameObjectProperty;
import com.igottashoot.game.utilities.FixedSizeArray;
import com.igottashoot.game.utilities.ObjectPool;

public class GameObjectPropertyPool extends BaseObject
{
    private final FixedSizeArray<GamePropertyPool> mPropertyPools;
    public GameObjectPropertyPool( PropertyPoolDescriptor[] list )
    {
        mPropertyPools = FixedSizeArray.newArray( list.length );
        for ( int index = 0; index < list.length; index++ )
            mPropertyPools.add( new GamePropertyPool( list[index].getClassType(), list[index].getSize() ) );
    }
    
    private static class GamePropertyPool extends BaseObject
    {
        private final ObjectPool<BaseObject> mPool;
        public GamePropertyPool( Class<? extends GameObjectProperty> clazz, int size )
        {
            mPool = createPool( size, clazz );
        }

        private ObjectPool<BaseObject> createPool( int size, final Class<? extends GameObjectProperty> clazz )
        {
            return new ObjectPool<BaseObject>( size )
            {
                @Override
                protected BaseObject create()
                {
                    try
                    {
                        GameObjectProperty newInstance = clazz.newInstance();
                        return (BaseObject) newInstance;
                    }
                    catch ( InstantiationException e )
                    {
                        e.printStackTrace();
                    }
                    catch ( IllegalAccessException e )
                    {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }
    }
}
