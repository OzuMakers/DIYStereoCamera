package videoprocessing.dstereo;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import java.awt.image.DataBufferInt;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class ScreenRecordingExample {
    
    private static final double FRAME_RATE = 30;
    
    private static final int SECONDS_TO_RUN_FOR = 10;
    
    private static final String outputFilename = "c:/mydesktop.mp4";
    
    private static Dimension screenBounds;

    public static void main(String[] args) {
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // let's make a IMediaWriter to write the file.
        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);
        
        screenBounds = Toolkit.getDefaultToolkit().getScreenSize();

        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, 
                   screenBounds.width/2, screenBounds.height/2);

        long startTime = System.nanoTime();
        
        for (int index = 0; index < SECONDS_TO_RUN_FOR * FRAME_RATE; index++) {
          //////////////////////////////////////////////AS YOU CAPTURE///////////////////////
            // take the screen shot
            BufferedImage screen = getDesktopScreenshot();
         //   BufferedImage screen2 = getDesktopScreenshot();
            BufferedImage newImage = new BufferedImage(screen.getWidth()*2, screen.getHeight(), BufferedImage.TYPE_INT_RGB);
            
            Graphics g = newImage.createGraphics();
            g.drawImage(screen, 0, 0, screen.getWidth(), screen.getHeight(), null);
            g.drawImage(screen, screen.getWidth(), 0, screen.getWidth(), screen.getHeight(), null);
            g.dispose();
            
            // convert to the right image type
            BufferedImage bgrScreen = convertToType(newImage, 
                   BufferedImage.TYPE_3BYTE_BGR);

            // encode the image to stream #0
            writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, 
                   TimeUnit.NANOSECONDS);
////////////////SLEEPING PART IS PROBOBLY UNCESSARY IF CODE PART MOVED TO ANOTHERONE, BE CAREFUL.
            // sleep for frame rate milliseconds
            try {
                Thread.sleep((long) (1000 / FRAME_RATE));
            } 
            catch (InterruptedException e) {
                // ignore
            }
            
        }
        
        // tell the writer to close and write the trailer if  needed
        writer.close();
     /////////////////////////////////////////////////////////////////////////////////////////////////////
    }
    
    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        
        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(), 
                 sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
        
    }
    
    private static BufferedImage getDesktopScreenshot() {
        try {
            Robot robot = new Robot();
            Rectangle captureSize = new Rectangle(screenBounds);
            return robot.createScreenCapture(captureSize);
        } 
        catch (AWTException e) {
            e.printStackTrace();
            return null;
        }
        
    }

}
