package com.igottashoot.game.primitives;

import com.igottashoot.game.rendering.DrawingCanvas;
import com.igottashoot.game.rendering.RendererInfo;


public class RenderElement extends BaseObject implements OrderedObject 
{
    private int mPhase;
    private DrawableObject mDrawable;
    
//    public float x;
//    public float y;
//    public boolean cameraRelative;

    public RenderElement()
    {
        mPhase = Integer.MAX_VALUE;
    }

//    private void set(DrawableObject drawable, Vector2 position, int priority, boolean isCameraRelative) {
//        mDrawable = drawable;
//        x = position.x;
//        y = position.y;
//        cameraRelative = isCameraRelative;
//        final int sortBucket = priority * TEXTURE_SORT_BUCKET_SIZE;
//        int sortOffset = 0;
//        if (drawable != null) {
//            Texture tex = drawable.getTexture();
//            if (tex != null) {
//                sortOffset = (tex.resource % TEXTURE_SORT_BUCKET_SIZE) * Utils.sign(priority);
//            }
//        }
//        setPhase(sortBucket + sortOffset);
//    }


    @Override
    public void reset()
    {
//        mDrawable = null;
//        x = 0.0f;
//        y = 0.0f;
//        cameraRelative = false;
    }

    @Override
    public int getPriority()
    {
        if ( mPhase == Integer.MAX_VALUE )
            throw new IllegalStateException();
        
        return mPhase;
    }

    public void setPriority( int phase )
    {
        mPhase = phase;
    }

    public void setDrawable( DrawableObject drawable )
    {
        mDrawable = drawable;
    }
    
    /**
     * TODO: This will eventually have to take camera information. 
     */
    public void draw( DrawingCanvas canvas, RendererInfo rendererInfo )
    {
        mDrawable.draw( canvas, rendererInfo );       
    }
}