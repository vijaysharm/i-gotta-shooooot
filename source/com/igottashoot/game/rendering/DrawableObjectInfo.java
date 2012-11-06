package com.igottashoot.game.rendering;

public interface DrawableObjectInfo
{
    /**
     * Return the vertex information. Values are in world space.
     *  
     * OpenGL Coordinates
     * (x,y+h) ----- (x+w,y+h)
     *   |               |
     *   |               | 
     *   |               |
     *   |               |
     *   |               |
     *   |               |
     * (x,y) ------- (x+w,y)
     * 
     */
    public float getVertexPositionX();
    public float getVertexPositionY();
    public float getVertexWidth();
    public float getVertexHeight();
    
    /**
     * Return the vertex information. Values are in texture space.
     * Values are mapped to the vertex coordinates above.
     * 
     * 0 <= x, y <= 1
     *  
     * Texture coordinates
     * (x1,y1) ------ (x2,y1)
     *   |               |
     *   |               | 
     *   |               |
     *   |               |
     *   |               |
     *   |               |
     * (x1,y2) ------ (x2,y2)
     * 
     */
    public float getTexturePositionX1();
    public float getTexturePositionY1();
    public float getTexturePositionX2();
    public float getTexturePositionY2();
    
    public float getOpacity();
}
