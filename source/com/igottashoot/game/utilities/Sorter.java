package com.igottashoot.game.utilities;

import java.util.Arrays;
import java.util.Comparator;

import com.igottashoot.game.primitives.BaseObject;

public abstract class Sorter<T> extends BaseObject
{
    public static <T> Sorter<T> createStandardSorter()
    {
        return new Sorter<T>()
        {
            @Override
            public void sort( T[] array, int count, Comparator<T> comparator )
            {
                Arrays.sort( array, 0, count, comparator );
            }
        };
    }
    
    public abstract void sort( T[] array,
                               int count,
                               Comparator<T> comparator );
}