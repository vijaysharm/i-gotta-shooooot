package com.igottashoot.game.rendering;

import com.igottashoot.game.animation.AnimationFrame;
import com.igottashoot.game.primitives.Texture;
import com.igottashoot.game.rendering.bitmap.DrawableBitmap;

public interface DrawableBitmapAnimationResolver
{
    void resolve( DrawableBitmap bitmap, Texture texture, AnimationFrame animationFrame );
}
