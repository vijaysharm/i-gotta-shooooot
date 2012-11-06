package com.igottashoot.game.utilities;

import com.igottashoot.game.primitives.RenderElement;

public class RenderElementPool extends ObjectPool<RenderElement>
{
    public RenderElementPool( int max )
    {
        super( max );
    }

    @Override
    public void release( RenderElement element )
    {
//        RenderElement renderable = (RenderElement)element;
//        // if this drawable came out of a pool, make sure it is returned to that pool.
//        final ObjectPool pool = renderable.mDrawable.getParentPool();
//        if (pool != null) {
//            pool.release(renderable.mDrawable);
//        }
//        // reset on release
//        renderable.reset();
        super.release(element);
    }

    @Override
    protected RenderElement create()
    {
        return new RenderElement();
    }
}
