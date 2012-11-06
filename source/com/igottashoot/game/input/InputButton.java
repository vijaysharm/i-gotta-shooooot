package com.igottashoot.game.input;

import com.igottashoot.game.primitives.BaseObject;

public class InputButton extends BaseObject
{
    private final Button mXAxis;
    private final Button mYAxis;
    
    public InputButton()
    {
        mXAxis = new Button();
        mYAxis = new Button();
    }
    
    public void press( float gameTime, float x, float y )
    {
        mXAxis.press( x, gameTime );
        mYAxis.press( y, gameTime );
    }
    
    public void release()
    {
        mXAxis.release();
        mYAxis.release();        
    }
    
    public void copy( InputButton inputButton )
    {
        mXAxis.copy( inputButton.mXAxis );
        mYAxis.copy( inputButton.mYAxis );
    }
    
    public boolean isPressed()
    {
        return mXAxis.isPressed() && mYAxis.isPressed();
    }
    
    private static class Button extends BaseObject
    {
        private boolean mIsDown;
        private float mLastPressedTime;
        private float mFirstPressedTime;
        private float mValue;
        
        public Button()
        {
            reset();
        }
        
        public boolean isPressed()
        {
            return mIsDown;
        }

        @Override
        public void reset()
        {
            mIsDown = false;
            mLastPressedTime = 0f;
            mFirstPressedTime = 0f;
            mValue = 0f;
        };
        
        public void press( float value, float gameTime )
        {
            if ( ! mIsDown )
            {
                mIsDown = true;
                mFirstPressedTime = gameTime;
            }
            
            mValue = value;
            mLastPressedTime = gameTime;
        }
        
        public void release()
        {
            mIsDown = false;
        }
        
        public void copy( Button button )
        {
            mIsDown = button.mIsDown;
            mLastPressedTime = button.mLastPressedTime;
            mFirstPressedTime = button.mFirstPressedTime;
            mValue = button.mValue;
        }        
    }
}
