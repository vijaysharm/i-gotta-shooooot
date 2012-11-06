package com.igottashoot.game.managers;

import com.igottashoot.game.core.SystemRegistry;
import com.igottashoot.game.math.Vector2D;
import com.igottashoot.game.primitives.BaseObject;
import com.igottashoot.game.primitives.OrderedUpdateableObject;
import com.igottashoot.game.primitives.UpdateableObject;

public class GameObject extends BaseObject implements UpdateableObject<SystemRegistry>
{
    private OrderedUpdateableObjectManager<GameObject, OrderedUpdateableObject<GameObject>> mGameObjectComponents;
    private SystemRegistry mRegistry;
    
    private final Vector2D mPosition;
    private final Vector2D mVelocity;
    private final Vector2D mAcceleration;
    
    public GameObject()
    {
        mPosition = new Vector2D();
        mVelocity = new Vector2D();
        mAcceleration = new Vector2D();
        
        reset();
    }
    
    @Override
    public void reset()
    {
        if ( mGameObjectComponents != null )
        {
            mGameObjectComponents.commitUpdates();
            mGameObjectComponents.removeAll();
        }
        
        mPosition.zero();
        mVelocity.zero();
        mAcceleration.zero();
    }
    
    @Override
    public void update( float timeDelta, SystemRegistry parent )
    {
        mRegistry = parent;
        mGameObjectComponents.commitUpdates();
        mGameObjectComponents.update( timeDelta, this );
    }

    public void setMaximumNumberOfPropertyObjects( int size )
    {
        mGameObjectComponents = OrderedUpdateableObjectManager.newManager( size );   
    }
    
    public void addProperty( OrderedUpdateableObject<GameObject> property )
    {
        mGameObjectComponents.add( property );
    }
    
    public void removeProperty( OrderedUpdateableObject<GameObject> property )
    {
        mGameObjectComponents.remove( property );
    }
    
    public void commitChanges()
    {
        mGameObjectComponents.commitUpdates();
    }
    
    public SystemRegistry getRegistry()
    {
        return mRegistry;
    }

    public float getPositionX()
    {
        return mPosition.getX();
    }

    public float getPositionY()
    {
        return mPosition.getY();
    }
    
    public void setPositionX( float position )
    {
        mPosition.setX( position );
    }

    public void setPositionY( float position )
    {
        mPosition.setY( position );
    }
    
    public float getVelocityX()
    {
        return mVelocity.getX();
    }

    public float getVelocityY()
    {
        return mVelocity.getY();
    }
    
    public void setVelocityX( float position )
    {
        mVelocity.setX( position );
    }

    public void setVelocityY( float position )
    {
        mVelocity.setY( position );
    }    
    
    public float gettAccelerationX()
    {
        return mAcceleration.getX();
    }

    public float gettAccelerationY()
    {
        return mAcceleration.getY();
    }
    
    public void setAccelerationX( float position )
    {
        mAcceleration.setX( position );
    }

    public void setAccelerationY( float position )
    {
        mAcceleration.setY( position );
    }    
}
