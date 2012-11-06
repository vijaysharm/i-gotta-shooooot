package com.igottashoot.game.primitives;

public interface UpdateableObject<T> extends ResettableObject
{
    /**
     * Update this object.
     * 
     * @param timeDelta
     *            The duration since the last update (in seconds).
     * @param parent
     *            The parent of this object (may be NULL).
     */
    public void update( float timeDelta, T parent );
}
