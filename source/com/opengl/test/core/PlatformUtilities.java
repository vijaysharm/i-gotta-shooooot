package com.opengl.test.core;

public class PlatformUtilities
{
    /** The unique identifier for the current platform */
    private static final PlatformIdentifier CURRENT_PLATFORM = determineCurrentPlatform();
    
    /** Prefix to use to identify the Linux operating system by name */
    private static final String OS_NAME_PREFIX_LINUX = "linux";
    
    /** Prefix to use to identify the Windows operating system by name */
    private static final String OS_NAME_PREFIX_WINDOWS = "windows";

    /** Prefix to use to identify the Windows operating system by name */
    private static final String OS_NAME_PREFIX_MACOSX = "mac os x";

    /**
     * The value returned by <code>System.getProperty( "os.arch")</code> ) for a
     * 64-bit Windows or Linux application system (x86_x64)
     */
    private static final String OS_ARCH_AMD64 = "amd64";
    
    /** The unique identifiers of the possible platforms */
    public enum PlatformIdentifier 
    {
        LINUX32,
        LINUX64,
        WINDOWS32,
        WINDOWS64,
        MACOSX,
        UNKNOWN;
    }

    /**
     * The unique identifier for the current platform. Note that the indicated
     * platform reflects the one of the executing application and not
     * necessarily the one of the host operating system. For example, if IV
     * 32-bit run on Windows 64-bit, {@link ApplicationIdentifier.WINDOWS32}
     * will be returned.
     * 
     * @return the unique identifier for the current platform
     */
    public static PlatformIdentifier getCurrentPlatform()
    {
        return CURRENT_PLATFORM;
    }

    /**
     * @return true only if the current platform is Windows (32-bit or 64-bit).
     */
    public static boolean isWindows()
    {
        return isWindows32() || isWindows64(); 
    }

    /**
     * @return true only if the current platform is Windows 32-bit.
     */
    public static boolean isWindows32()
    {
        return isPlatform( PlatformIdentifier.WINDOWS32 );
    }

    /**
     * @return true only if the current platform is Windows 64-bit.
     */
    public static boolean isWindows64()
    {
        return isPlatform( PlatformIdentifier.WINDOWS64 ); 
    }

    /**
     * @return true only if the current platform is Mac OS X (Intel 64-bit)
     */
    public static boolean isMacOSX()
    {
        return isPlatform( PlatformIdentifier.MACOSX ); 
    }
    
    public static boolean isUnknownPlatform()
    {
        return isPlatform( PlatformIdentifier.UNKNOWN );
    }
    
    private static boolean isPlatform( PlatformIdentifier platformIdentifier )
    {
        return getCurrentPlatform() == platformIdentifier;
    }

    /**
     * 
     * @return the unique identifier of the current platform or
     *         {@link PlatformIdentifier.UNKNOWN} if unrecognized.
     */
    private static PlatformIdentifier determineCurrentPlatform()
    {
        if ( isOperatingSystem( OS_NAME_PREFIX_LINUX ) )
        {
            return isAmd64Arch() ? PlatformIdentifier.LINUX64
                                 : PlatformIdentifier.LINUX32;
        }

        if ( isOperatingSystem( OS_NAME_PREFIX_WINDOWS ) )
        {
            return isAmd64Arch() ? PlatformIdentifier.WINDOWS64
                                 : PlatformIdentifier.WINDOWS32;
        }

        if ( isOperatingSystem( OS_NAME_PREFIX_MACOSX) )
        {
            // Assuming Mac OS X ruuning on Intel 64-bit (amd64) CPUs.
            return PlatformIdentifier.MACOSX;
        }

        return PlatformIdentifier.UNKNOWN;
    }

    /**
     * @param osName
     *            identifier of the operating system, in upper case
     * @return true if the operating system name begins with the specified name,
     *         false otherwise
     */
    private static boolean isOperatingSystem( String osNamePrefix )
    {
        String name = System.getProperty( "os.name" );
        
        return ( name != null ) && name.toLowerCase().startsWith( osNamePrefix );
    }
    
    /**
     * Another way to identify the architecture (32 vs 64 bit) is by querying
     * the "os.arch.data.model" property. However, it is not documented in
     * {@link System}.
     **/
    private static boolean isAmd64Arch()
    {
        return OS_ARCH_AMD64.equals( System.getProperty( "os.arch" ) );
    }
}
