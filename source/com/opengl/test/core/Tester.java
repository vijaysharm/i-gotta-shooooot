package com.opengl.test.core;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.sun.opengl.util.FPSAnimator;

public class Tester
{
    private static Runnable createDrawThread( final OpenGLSurfaceRenderer renderer, DrawingParameters paramters )
    {
        int gameWidth = paramters.getActualWidth();
        int gameHeight = paramters.getActualHeight();
        
        final JFrame frame = new JFrame( "Don't Shooooooot!" );
        frame.getContentPane().add( renderer, BorderLayout.CENTER );
        
        // Apparently the height of the toolbar for JFrames on MAC is 22 pixels... boo!
        if ( PlatformUtilities.isMacOSX() )
            frame.setSize( gameWidth, gameHeight + 22 );
        else
            frame.setSize( gameWidth + 10, gameHeight + 29 );
        
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        return new Runnable()
        {
            @Override
            public void run()
            {
                frame.setVisible( true );
                renderer.requestFocus();
            }
        };
    }
    
    private static DrawingParameters DRAWPARAMETERS = new DrawingParameters( 480, 320, 
                                                                             480, 320,
                                                                             0, 100, 0, 100 );
    
    private static class OpenGLSurfaceRenderer extends GLCanvas implements GLEventListener
    {
        private final FPSAnimator mAnimator;
        private final MouseListener mMouseListener;
        private MouseEvent mouse;
        private GLU glu;
        
        public OpenGLSurfaceRenderer()
        {
            super( initializeCapabilities() );
            
            mAnimator = new FPSAnimator( this, 60 );
            mMouseListener = new MouseAdapter()
            {
                public void mousePressed( MouseEvent e )
                {
                    OpenGLSurfaceRenderer.this.mousePressed( e );
                };
                
                @Override
                public void mouseReleased( MouseEvent e )
                {
                    OpenGLSurfaceRenderer.this.mouseReleased( e );
                }
            };
            
            mouse = null;
            addMouseListener( mMouseListener );
            addGLEventListener( this );
        }

        protected void mouseReleased( MouseEvent e )
        {
            mouse = null;
        }

        protected void mousePressed( MouseEvent e )
        {
            mouse = e;
            display();
        }

        /**
         * onSurfaceCreated( GL10 gl, EGLConfig config )
         */
        @Override
        public void init( GLAutoDrawable drawable )
        {
            mAnimator.start();
            glu = new GLU();
        }
        
        /**
         * onSurfaceChanged( GL10 gl, int width, int height )
         */
        @Override
        public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height )
        {
            System.err.println( "Creating a surface with (width, height) = (" + width + ", " + height + ")" );
            GL gl = drawable.getGL();
            gl.glViewport ( 0, 0, width, height );
            
            gl.glMatrixMode( GL.GL_PROJECTION );
            gl.glLoadIdentity();
            
            int left = DRAWPARAMETERS.getLeft();
            int right = DRAWPARAMETERS.getRight();
            int top = DRAWPARAMETERS.getTop();
            int bottom = DRAWPARAMETERS.getBottom();
            
            gl.glOrtho( left, right, bottom, top, -1, 1 );
            
            gl.glMatrixMode( GL.GL_MODELVIEW );
            gl.glLoadIdentity();            
        }
        
        /**
         * onDraw( GL10 gl )
         */
        @Override
        public void display( GLAutoDrawable drawable )
        {
            if ( !mAnimator.isAnimating() )
                return;
            
            GL gl = drawable.getGL();
            
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
            if ( mouse != null )
            {
                int x = mouse.getX(), y = mouse.getY();
                switch ( mouse.getButton() )
                {
                    case MouseEvent.BUTTON1:
//                        System.out.println( "Coordinates at cursor are ( " + x + ", " + y + " )");
                        
                        useMyOwnUprojectToVerify( x, y );
                        useUnprojectToDetermineWorldLocation( gl, x, y );
                        break;
                    default:
                        break;
                }
            }
            mouse = null;
            gl.glFlush();        
        }

        private void useMyOwnUprojectToVerify( int x, int y )
        {
            float scaledX = x * ( 1 / DRAWPARAMETERS.getScaleX() );
            float scaledY = y * ( 1 / DRAWPARAMETERS.getScaleY() );
            
            scaledY = DRAWPARAMETERS.getActualHeight() - scaledY - 1;
            
            float worldwidth = DRAWPARAMETERS.getRight() - DRAWPARAMETERS.getLeft();
            float worldHeight = DRAWPARAMETERS.getTop() - DRAWPARAMETERS.getBottom();
            
            float ratioX = worldwidth / DRAWPARAMETERS.getActualWidth();
            float ratioY = worldHeight / DRAWPARAMETERS.getActualHeight();
            
            scaledX = ( scaledX * ratioX ) + DRAWPARAMETERS.getLeft();
            scaledY = ( scaledY * ratioY ) + DRAWPARAMETERS.getBottom();
            
            System.out.println( "VIJAY: Modified for OGL Coordinates at cursor are ( " + scaledX + ", " + scaledY + " )" );
        }

        private void useUnprojectToDetermineWorldLocation( GL gl, int x, int y )
        {
            int viewport[] = new int[4];
            double mvmatrix[] = new double[16];
            double projmatrix[] = new double[16];
            double wcoord[] = new double[4];            
            int realy = 0;
            
            // gl.glViewport ( 0, 0, width, height );
            gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

            // gl.glOrtho( xOffset, width + xOffset, yOffset, height + yOffset, -1, 1 );
            gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);

            //gl.glLoadIdentity();
            gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);

            /* note viewport[3] is height of window in pixels */
            realy = viewport[3] - (int) y - 1;
//            System.out.println( "GLUT: Modified for OGL Coordinates at cursor are ( " + x + ", " + realy + " )" );
            
            glu.gluUnProject( (double) x, (double) realy, 0.0,
                              mvmatrix, 0,
                              projmatrix, 0, 
                              viewport, 0, 
                              wcoord, 0 );

            System.out.println( "GLUT: World coords ( " + wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2] + " )" );
//            glu.gluUnProject((double) x, (double) realy, 1.0, //
//                             mvmatrix, 0,
//                             projmatrix, 0,
//                             viewport, 0, 
//                             wcoord, 0);
//
//            System.out.println("World coords at z=1.0 are ( " //
//                               + wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]
//                                                                              + " )");
        }
        
        @Override
        public void displayChanged( GLAutoDrawable drawable, boolean arg1, boolean arg2 )
        {
            throw new IllegalStateException();
        }    
        
        private static GLCapabilities initializeCapabilities()
        {
            GLCapabilities capabilities = new GLCapabilities();
            capabilities.setRedBits( 8 );
            capabilities.setBlueBits( 8 );
            capabilities.setGreenBits( 8 );
            capabilities.setAlphaBits( 8 );

            return capabilities;
        } 
    }
    
    private static class DrawingParameters
    {
        private final int mDesiredWidth;
        private final int mDesiredHeight;
        
        private final int mActualWidth;
        private final int mActualHeight;
        
        private final int mLeft;
        private final int mRight;
        private final int mBottom;
        private final int mTop;
        
        public DrawingParameters( int desiredWidth,
                                  int desiredHeight,
                                  int actualWidth,
                                  int actualHeight,
                                  int left,
                                  int right,
                                  int bottom,
                                  int top )
        {
            mDesiredWidth = desiredWidth;
            mDesiredHeight = desiredHeight;
            mActualWidth = actualWidth;
            mActualHeight = actualHeight;
            
            mLeft = left;
            mRight = right;
            mBottom = bottom;
            mTop = top;
        }
        
        public int getDesiredWidth()
        {
            return mDesiredWidth;
        }

        public int getDesiredHeight()
        {
            return mDesiredHeight;
        }

        public int getActualWidth()
        {
            return mActualWidth;
        }

        public int getActualHeight()
        {
            return mActualHeight;
        }
        
        public float getScaleX()
        {
            return ( (float) mActualWidth / mDesiredWidth ); 
        }
        
        public float getScaleY()
        {
            return ( (float) mActualHeight / mDesiredHeight ); 
        }

        public int getLeft()
        {
            return mLeft;
        }

        public int getRight()
        {
            return mRight;
        }

        public int getBottom()
        {
            return mBottom;
        }

        public int getTop()
        {
            return mTop;
        }
    }
    
    public final static int byteArrayToInt(byte[] b) {
        if (b.length != 4) {
            return 0;
        }

        // Same as DataInputStream's 'readInt' method
        /*int i = (((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16) | ((b[2] & 0xff) << 8) 
                | (b[3] & 0xff));*/
        
        // little endian
        int i = (((b[3] & 0xff) << 24) | ((b[2] & 0xff) << 16) | ((b[1] & 0xff) << 8) 
                | (b[0] & 0xff));
    
        return i;
    }
    
    public final static float byteArrayToFloat(byte[] b) {
        
        // intBitsToFloat() converts bits as follows:
        /*
        int s = ((i >> 31) == 0) ? 1 : -1;
        int e = ((i >> 23) & 0xff);
        int m = (e == 0) ? (i & 0x7fffff) << 1 : (i & 0x7fffff) | 0x800000;
        */
    
        return Float.intBitsToFloat(byteArrayToInt(b));
    }

    public static void main( String[] args )
    {
        byte [] a = new byte[4];
        a[0] = 0x7F;
        a[1] = 0x3f;
        a[2] = 0x0;
        a[3] = 0x00;
//        System.err.println( "3f 80 00 00 = " + byteArrayToFloat( a ) );
        
        System.err.println( Float.intBitsToFloat( 1041301503 ) );
    }
    
//    public static void main( String[] args )
//    {
//        OpenGLSurfaceRenderer surface = new OpenGLSurfaceRenderer();
//        Runnable run = createDrawThread( surface, Tester.DRAWPARAMETERS );
//        SwingUtilities.invokeLater( run );
//    }    
}
