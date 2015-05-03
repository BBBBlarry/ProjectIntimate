import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.commons.math3.distribution.NormalDistribution;

public class User{

	final static int ACTIVEGAZETHREDHOLD = 3; 

	String username; 
	double[] genrePreference;
	ArrayList<Double> gazeCollection; 
	double averageTimeOfGaze; 
	int gazeCount; 
	NormalDistribution bellcurve;
	boolean bellcurveReady; 


	
	LinkedHashMap actions = new LinkedHashMap();

	void addCommand(Command command){
		actions.put((double) System.nanoTime()/((double)(1000000 * 1000)), command);
	}

	User(String username, double[] genrePreference){
		averageTimeOfGaze = 0;
		gazeCount = 0; 
		this.username = username;
		this.genrePreference = genrePreference;
		gazeCollection = new ArrayList<Double>();
		bellcurveReady = false; 
	}

	public double getProbability(double n_GazeTime){
		if(bellcurveReady){
			return bellcurve.cumulativeProbability(n_GazeTime);
		}else{
			return -1; 
		}
	}


	public void updateGazeTime(double gazeTime){
		gazeCount++;

		if(averageTimeOfGaze == 0){ //ya know, it's the first time
			averageTimeOfGaze = gazeTime;
		}else{
			averageTimeOfGaze = (averageTimeOfGaze * (gazeCount - 1) + gazeTime)/gazeCount;
		}

		gazeCollection.add(gazeTime);


		if(gazeCount >= ACTIVEGAZETHREDHOLD){
			bellcurveReady = true; 
			double temp = 0;
			for(double d: gazeCollection){
				temp += (averageTimeOfGaze - d) * (averageTimeOfGaze - d);
			}
			bellcurve = new NormalDistribution(averageTimeOfGaze, Math.sqrt(temp/gazeCount));//wooohooo
		}

	}
}