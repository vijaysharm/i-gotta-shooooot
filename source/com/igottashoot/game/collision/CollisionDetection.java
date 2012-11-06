package com.igottashoot.game.collision;

import com.igottashoot.game.math.Vector2D;
import com.igottashoot.game.primitives.BaseObject;

public class CollisionDetection
{
    private static final DefaultCollisionData COLLISION_DATA = new DefaultCollisionData();
    
    public static CollisionData intersection( BoundingVolume sourceVolume, BoundingVolume destinationVolume )
    {
        AABoxBoundingVolume sourceBox = (AABoxBoundingVolume) sourceVolume;
        AABoxBoundingVolume destinationBox = (AABoxBoundingVolume) destinationVolume;

        float sourceMinX = sourceBox.getMinimumX();
        float sourceMinY = sourceBox.getMinimumY();
        float sourceMaxX = sourceBox.getMaximumX();
        float sourceMaxY = sourceBox.getMaximumY();
        
        float destinationMinX = destinationBox.getMinimumX();
        float destinationMinY = destinationBox.getMinimumY();
        float destinationMaxX = destinationBox.getMaximumX();
        float destinationMaxY = destinationBox.getMaximumY();

        float sourceRightSidePenetration = sourceMaxX - destinationMinX;
        float sourceLeftSidePenetration = sourceMinX - destinationMaxX;
        
        float sourceTopPenetration = sourceMaxY - destinationMinY;
        float sourceBottomPenetration = sourceMinY - destinationMaxY;
        
        if ( sourceRightSidePenetration < 0 || sourceLeftSidePenetration > 0 )
            return null;

        if ( sourceTopPenetration < 0 || sourceBottomPenetration > 0 )
            return null;
        
        float penetrationX = 0f;
        float penetrationY = 0f;
        
        float collisionNormalX = 0f;
        float collisionNormalY = 0f;
        
        // Check horizontal collision
        if ( sourceMinX < destinationMinX && destinationMinX < sourceMaxX &&
             sourceMinX < destinationMaxX && destinationMaxX < sourceMaxX )
        {
            penetrationX = 0f;
            collisionNormalX = 0f;
        }
        else if ( destinationMinX < sourceMinX && sourceMinX < destinationMaxX &&
                  destinationMinX < sourceMaxX && sourceMaxX < destinationMaxX )
        {
            penetrationX = 0f;
            collisionNormalX = 0f;
        }
        else if ( sourceMinX < destinationMinX && destinationMinX < sourceMaxX &&
                  sourceMinX < destinationMaxX && sourceMaxX < destinationMaxX )
        {
            // source collides from right
            penetrationX = sourceMaxX - destinationMinX;
            collisionNormalX = -1f;
        }
        else if ( destinationMinX < sourceMinX && sourceMinX < destinationMaxX &&
                  destinationMinX < sourceMaxX && destinationMaxX < sourceMaxX )
        {
            // source collides from left
            penetrationX = destinationMaxX - sourceMinX;
            collisionNormalX = 1f;
        }

        // Check vertical collision
        if ( sourceMinY < destinationMinY && destinationMinY < sourceMaxY &&
             sourceMinY < destinationMaxY && destinationMaxY < sourceMaxY )
        {
            penetrationY = 0f;
            collisionNormalY = 0f;
        }
        else if ( destinationMinY < sourceMinY && sourceMinY < destinationMaxY &&
                  destinationMinY < sourceMaxY && sourceMaxY < destinationMaxY )
        {
            penetrationY = 0f;
            collisionNormalY = 0f;
        }
        else if ( sourceMinY < destinationMinY && destinationMinY < sourceMaxY &&
                  sourceMinY < destinationMaxY && sourceMaxY < destinationMaxY )
        {
            // source collides from bottom
            penetrationY = sourceMaxY - destinationMinY;
            collisionNormalY = -1f;
        }
        else if ( destinationMinY < sourceMinY && sourceMinY < destinationMaxY &&
                  destinationMinY < sourceMaxY && destinationMaxY < sourceMaxY )
        {
            // source collides from top
            penetrationY = destinationMaxY - sourceMinY;
            collisionNormalY = 1f;
        }
        
        if ( penetrationX == 0f && penetrationY == 0 )
            return null;
        
        COLLISION_DATA.reset();
        
        COLLISION_DATA.setCollisionNormalX( collisionNormalX );
        COLLISION_DATA.setCollisionNormalY( collisionNormalY );
        
        COLLISION_DATA.setPenetrationX( penetrationX );
        COLLISION_DATA.setPenetrationY( penetrationY );
        
        return COLLISION_DATA;
    }
    
    private static class DefaultCollisionData extends BaseObject implements CollisionData
    {
        private float mPenetrationX;
        private float mPenetrationY;
        
        private final Vector2D mCollisionNormal;
        
        public DefaultCollisionData()
        {
            mCollisionNormal = new Vector2D();
        }
        
        @Override
        public void reset()
        {
            mPenetrationX = 0f;
            mPenetrationY = 0f;
            mCollisionNormal.zero();
        }
        
        public void setCollisionNormalX( float directionX )
        {
            mCollisionNormal.setX( directionX );
        }
        
        @Override
        public float getCollisionNormalX()
        {
            return mCollisionNormal.getX();
        }
        
        public void setCollisionNormalY( float directionY )
        {
            mCollisionNormal.setY( directionY );
        }
        
        @Override
        public float getCollisionNormalY()
        {
            return mCollisionNormal.getY();
        }
        
        public void setPenetrationX( float penetrationX )
        {
            mPenetrationX = penetrationX;
        }
        
        @Override
        public float getPenetrationX()
        {
            return mPenetrationX;
        }
        
        public void setPenetrationY( float penetrationY )
        {
            mPenetrationY = penetrationY;
        }

        @Override
        public float getPenetrationY()
        {
            return mPenetrationY;
        }
    }
    
    public static void main( String[] args )
    {
        AABoxBoundingVolume boxA = new AABoxBoundingVolume( 0, 0, 64, 64 );
        AABoxBoundingVolume boxB = new AABoxBoundingVolume( 0, 63, 64, 128 );
        
        CollisionData collisionData = CollisionDetection.intersection( boxA, boxB );
        if ( collisionData == null )
            System.err.println( "There is no collision" );
        else
            System.err.println( "Collision occured: penetration X= " + collisionData.getPenetrationX() + ", penetration Y= " + collisionData.getPenetrationY() );
    }
}
