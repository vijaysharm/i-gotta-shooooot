package com.igottashoot.game.managers;

import com.igottashoot.game.utilities.ObjectPool;

public class GameObjectPool extends ObjectPool<GameObject>
{
    public GameObjectPool( int max )
    {
        super( max );
    }
    
    @Override
    protected GameObject create()
    {
        return new GameObject();
    }
}
