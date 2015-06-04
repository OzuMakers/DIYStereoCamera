package videoprocessing.dstereo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ShortBuffer;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaTool;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class MainCamera implements Runnable{
	  private static final String inputFilename = "c:/myvideo.mp4";
	  private static final String outputFilename = "c:/myvideo.flv";
	public static boolean ready=false;
	public static boolean finished=false;
	public void run() {
		// create a media reader
	    IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
	    
	    // configure it to generate BufferImages
	    mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

	    IMediaWriter mediaWriter = 
	           ToolFactory.makeWriter(outputFilename, mediaReader);
	    
	    IMediaTool imageMediaTool = new StaticImageMediaTool();
	    IMediaTool audioVolumeMediaTool = new VolumeAdjustMediaTool(0.1);
	    
	 // create a tool chain:
	    // reader -> addStaticImage -> reduceVolume -> writer
	    mediaReader.addListener(imageMediaTool);
	    imageMediaTool.addListener(audioVolumeMediaTool);
	    audioVolumeMediaTool.addListener(mediaWriter);
	    
	    while (mediaReader.readPacket() == null) ;
	    System.out.println("Yay!");
		
	}
	
private static class StaticImageMediaTool extends MediaToolAdapter {
        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
        	 finished=false;
        	 BufferedImage screen = event.getImage();
        	 ready=true;
        	 while (!Camera.ready){
        		 //Wait for second camera.
        	 }
             BufferedImage newImage = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_RGB);
             Graphics g = newImage.createGraphics();
                g.drawImage(screen, 0, screen.getHeight()/4, screen.getWidth()/2, screen.getHeight()/2, null);
                g.drawImage(MainProgram.sidecamera.bufferedimage, screen.getWidth()/2, screen.getHeight()/4, screen.getWidth()/2, screen.getHeight()/2, null);
                g.dispose();
                
                
                // convert to the right image type
                BufferedImage bgrScreen = convertToType(newImage, 
                        BufferedImage.TYPE_3BYTE_BGR);
                screen.setData(bgrScreen.getData());
              //  screen.setData(bgrScreen.getData());

            // call parent which will pass the video onto next tool in chain
            super.onVideoPicture(event);
            finished=true;
            
            while(!Camera.finished){
       		 
            }
            ready=false;
        }
        
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
