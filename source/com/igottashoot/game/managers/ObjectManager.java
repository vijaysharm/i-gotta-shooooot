package com.igottashoot.game.managers;

import com.igottashoot.game.utilities.FixedSizeArray;

public interface ObjectManager<T>
{
    public void commitUpdates();
    public FixedSizeArray<T> getObjects();
    public int getCount();
    public int getConcreteCount();
    public void add( T object );
    public void remove( T object );
    public void removeAll();
}
