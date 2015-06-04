package videoprocessing.dstereo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ShortBuffer;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaTool;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class Camera implements Runnable{
	  private static final String inputFilename = "c:/myvideo3.mp4";
	  public static boolean ready=false;
	  public static boolean finished=false;
	  public static BufferedImage bufferedimage;
	public void run() {
		// create a media reader
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        
        // configure it to generate BufferImages
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        
        IMediaTool imageMediaTool = new StaticImageMediaTool();
        
        mediaReader.addListener(imageMediaTool);
        
        while (mediaReader.readPacket() == null) ;
        System.out.println("Program is over.");
	}
	
    private static class StaticImageMediaTool extends MediaToolAdapter {

        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
        	 finished=false;
        	 bufferedimage = event.getImage();
        	 
        	 ready=true;
        	 
        	 while(!MainCamera.ready){ 
        		 //Do nothing
        	 }        	 
        	 super.onVideoPicture(event);    
        	 finished=true;
        	 
        	 while(!MainCamera.finished){
        		 
        	 }
        	 ready=false;
        }
        
    }
    

}
