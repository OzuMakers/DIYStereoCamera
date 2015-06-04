package videoprocessing.dstereo;

public class MainProgram {
	public static MainCamera maincamera = new MainCamera();
	public static Camera sidecamera = new Camera();
	
	public static void main (String[] args) throws InterruptedException{
			Thread t1 = (new Thread(maincamera));
			Thread t2 = (new Thread(sidecamera));
					t1.start();
					t2.start();
			
			t1.join();
			t2.join();
	}
}
