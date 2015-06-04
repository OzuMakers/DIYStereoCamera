package videoprocessing.dstereo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ShortBuffer;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaTool;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class ModifyMediaExample {

    private static final String inputFilename = "c:/myvideo.mp4";
    private static final String outputFilename = "c:/myvideo.flv";
    private static final String inputFilename2 = "c:/myvideo.flv";
    private static final String outputFilename2 = "c:/myvideo2.flv";
    private static final String imageFilename = "c:/jcg_logo_small.png";

    public static void main(String[] args) {

        // create a media reader
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        
        // configure it to generate BufferImages
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

        IMediaWriter mediaWriter = 
               ToolFactory.makeWriter(outputFilename, mediaReader);
        
        IMediaTool imageMediaTool = new StaticImageMediaTool(imageFilename);
        IMediaTool audioVolumeMediaTool = new VolumeAdjustMediaTool(0.1);
        
        // create a tool chain:
        // reader -> addStaticImage -> reduceVolume -> writer
        mediaReader.addListener(imageMediaTool);
        imageMediaTool.addListener(audioVolumeMediaTool);
        audioVolumeMediaTool.addListener(mediaWriter);
        
        while (mediaReader.readPacket() == null) ;
        System.out.println("Yay!");
        
        ////////////////////////////////////////////////////////////////////////
IMediaReader mediaReader2 = ToolFactory.makeReader(inputFilename2);
        
        // configure it to generate BufferImages
        mediaReader2.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

        IMediaWriter mediaWriter2 = 
               ToolFactory.makeWriter(outputFilename2, mediaReader);
        
        IMediaTool imageMediaTool2 = new StaticImageMediaTool2(imageFilename);
        IMediaTool audioVolumeMediaTool2 = new VolumeAdjustMediaTool(0.1);
        
        // create a tool chain:
        // reader -> addStaticImage -> reduceVolume -> writer
        mediaReader2.addListener(imageMediaTool2);
        imageMediaTool2.addListener(audioVolumeMediaTool2);
        audioVolumeMediaTool2.addListener(mediaWriter2);
        
        while (mediaReader2.readPacket() == null) ;
        System.out.println("Lol!");

    }
    
    private static class StaticImageMediaTool extends MediaToolAdapter {
        
        private BufferedImage logoImage;
        
        public StaticImageMediaTool(String imageFile) {
            
            try {
                logoImage = ImageIO.read(new File(imageFile));
            } 
            catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not open file");
            }
            
        }

        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
        	
        	 BufferedImage screen = event.getImage();
             //   BufferedImage screen2 = getDesktopScreenshot();
                BufferedImage newImage = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_RGB);
                
                Graphics g = newImage.createGraphics();
                g.drawImage(screen, 0, screen.getHeight()/4, screen.getWidth()/2, screen.getHeight()/2, null);
               //g.drawImage(screen, 0, 0, screen.getWidth(), screen.getHeight(), null);
             //  g.drawImage(screen, screen.getWidth()/2, screen.getHeight()/4, screen.getWidth()/2, screen.getHeight()/2, null);
                g.dispose();
                
                
                // convert to the right image type
                BufferedImage bgrScreen = convertToType(newImage, 
                        BufferedImage.TYPE_3BYTE_BGR);
                screen.setData(bgrScreen.getData());
              //  screen.setData(bgrScreen.getData());

          /*  
            BufferedImage image = event.getImage();
            
            // get the graphics for the image
            Graphics2D g = image.createGraphics();
            
            Rectangle2D bounds = new 
              Rectangle2D.Float(0, 0, logoImage.getWidth(), logoImage.getHeight());

            // compute the amount to inset the time stamp and 
            // translate the image to that position
            double inset = bounds.getHeight();
            g.translate(inset, event.getImage().getHeight() - inset);

            g.setColor(Color.WHITE);
            g.fill(bounds);
            g.setColor(Color.BLACK);
            g.drawImage(logoImage, 0, 0, null);
            */
            // call parent which will pass the video onto next tool in chain
            super.onVideoPicture(event);
            
        }
        
    }
    
    //////////
private static class StaticImageMediaTool2 extends MediaToolAdapter {
        
        private BufferedImage logoImage;
        
        public StaticImageMediaTool2(String imageFile) {
            
            try {
                logoImage = ImageIO.read(new File(imageFile));
            } 
            catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not open file");
            }
            
        }

        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
        	
        	 BufferedImage screen = event.getImage();
             //   BufferedImage screen2 = getDesktopScreenshot();
                BufferedImage newImage = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_RGB);
                
                Graphics g = newImage.createGraphics();
            //    g.drawImage(screen, 0, screen.getHeight()/4, screen.getWidth()/2, screen.getHeight()/2, null);
               g.drawImage(screen, 0, 0, screen.getWidth(), screen.getHeight(), null);
               g.drawImage(screen, screen.getWidth()/2, screen.getHeight()/4, screen.getWidth()/2, screen.getHeight()/2, null);
                g.dispose();
                
                
                // convert to the right image type
                BufferedImage bgrScreen = convertToType(newImage, 
                        BufferedImage.TYPE_3BYTE_BGR);
                screen.setData(bgrScreen.getData());
              //  screen.setData(bgrScreen.getData());

          /*  
            BufferedImage image = event.getImage();
            
            // get the graphics for the image
            Graphics2D g = image.createGraphics();
            
            Rectangle2D bounds = new 
              Rectangle2D.Float(0, 0, logoImage.getWidth(), logoImage.getHeight());

            // compute the amount to inset the time stamp and 
            // translate the image to that position
            double inset = bounds.getHeight();
            g.translate(inset, event.getImage().getHeight() - inset);

            g.setColor(Color.WHITE);
            g.fill(bounds);
            g.setColor(Color.BLACK);
            g.drawImage(logoImage, 0, 0, null);
            */
            // call parent which will pass the video onto next tool in chain
            super.onVideoPicture(event);
            
        }
        
    }
    
    //////////

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
    
    private static class VolumeAdjustMediaTool extends MediaToolAdapter {
        
        // the amount to adjust the volume by
        private double mVolume;
        
        public VolumeAdjustMediaTool(double volume) {
            mVolume = volume;
        }

        @Override
        public void onAudioSamples(IAudioSamplesEvent event) {
            
            // get the raw audio bytes and adjust it's value
            ShortBuffer buffer = 
               event.getAudioSamples().getByteBuffer().asShortBuffer();
            
            for (int i = 0; i < buffer.limit(); ++i) {
                buffer.put(i, (short) (buffer.get(i) * mVolume));
            }

            // call parent which will pass the audio onto next tool in chain
            super.onAudioSamples(event);
            
        }
        
    }

}
