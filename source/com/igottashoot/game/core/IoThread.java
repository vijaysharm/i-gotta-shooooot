package com.igottashoot.game.core;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.igottashoot.game.primitives.BaseObject;

public class IoThread extends BaseObject
{
    private final ConcurrentLinkedQueue<Runnable> mMessages;
    private final Thread mRunner;
    private boolean mIsRunning;
    
    public IoThread()
    {
        mMessages = new ConcurrentLinkedQueue<Runnable>();
        mRunner = new Thread( new Runner() );
        mRunner.setName( "Asset I/O Thread" );
        mIsRunning = false;
    }
    
    public void addMessage( Runnable runnable )
    {
        mMessages.add( runnable );
        
        if ( mIsRunning == false )
        {
            mRunner.start();
            mIsRunning = true;
        }
    }
    
    private final class Runner extends BaseObject implements Runnable
    {
        @Override
        public void run()
        {
            while ( true )
            {
                Runnable runnable = mMessages.poll();
                while ( runnable == null)
                {
                    runnable = mMessages.poll();
                    sleep( 1000 );
                }

                runnable.run();
            }
        }
        
        private void sleep( long millis )
        {
            try
            {
                Thread.sleep( millis );
            }
            catch ( InterruptedException ignore )
            {
                
            }
        }
    }
}
