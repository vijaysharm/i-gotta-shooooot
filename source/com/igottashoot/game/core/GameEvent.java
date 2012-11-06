package com.igottashoot.game.core;

import com.opengl.test.core.LevelLoader;

public interface GameEvent
{
    GameEvent NULL_EVENT = new GameEvent()
    {
        @Override public void unload( LevelLoader levelLoader ){}
        @Override public void load( LevelLoader levelLoader ){}
        @Override public void create( LevelLoader levelLoader ){}
    };
    
    void create( LevelLoader levelLoader );
    void load( LevelLoader levelLoader );
    void unload( LevelLoader levelLoader );
}
