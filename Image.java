public class Image{

	public final static int GENRESIZE = 10;
	//the genre vector fot an image

	// Fanciness || portrait || gender || landscape (view) || simple || dark (concept) || light || worldly (news) || wordy || future

	double[] genre; 
	String name; 

	Image(String name, double[] genre){
		this.name = name; 
		this.genre = genre; 
	}


}