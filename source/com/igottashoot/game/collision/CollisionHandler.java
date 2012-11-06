package com.igottashoot.game.collision;

import com.igottashoot.game.managers.GameObject;

public interface CollisionHandler
{
    void handleCollisionWith( GameObject source, GameObject destination, CollisionData collisionData );
}
