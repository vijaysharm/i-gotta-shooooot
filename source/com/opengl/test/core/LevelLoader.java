package com.opengl.test.core;

import com.igottashoot.game.managers.GameObject;
import com.igottashoot.game.primitives.Texture;

public interface LevelLoader
{
    Texture allocateTexture( int resourceId );
    void releaseTexture( Texture texture );
    
    GameObject allocateGameObject();
    void releaseGameObject( GameObject gameObject );
    
    void addGameObject( GameObject backgroundObject );
    void removeGameObject( GameObject backgroundObject );
    
    void setCameraTarget( GameObject target );
}
