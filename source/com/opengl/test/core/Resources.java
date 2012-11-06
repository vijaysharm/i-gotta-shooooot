package com.opengl.test.core;

import java.util.HashMap;
import java.util.Map;

public enum Resources
{
    SPLASH_BACKGROUND( "splash_background.png", 0 ),
    SPLASH_LOGO( "splash_logo.png", 1 ),
    SPLASH_TOP_BAR( "splash_about_bar.png", 2 ),
    GENERIC_BUTTON( "generic_button.png", 3 ),
    BACK_BUTTON( "back_button.png", 4 ),
    
    PLAYER_KEYFRAMES( "player.png", 5 );
    
    private static final String DRAWABLE_RESOURCE_PATH = "res/drawable/";
    private static final Map<Integer, Resources> ID_TO_RESOURCE = new HashMap();
    
    static
    {
        for ( Resources resource : Resources.values() )
            ID_TO_RESOURCE.put( resource.getId(), resource );
    }
    
    private final String mResourcePath;
    private final int mId;
    
    private Resources( String resourcePath, int id )
    {
        mResourcePath = createResourceString( resourcePath );
        mId = id;
    }
    
    public static Resources getResource( int id )
    {
        return ID_TO_RESOURCE.get( id );
    }
    
    public String getDrawablePath()
    {
        return mResourcePath;
    }
    
    public int getId()
    {
        return mId;
    }
    
    private static String createResourceString( String resourceName )
    {
        return DRAWABLE_RESOURCE_PATH + resourceName;
    }
}
