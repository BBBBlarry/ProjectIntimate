
public class Filesystem{

	public boolean pictureOpen = false;

	//timer related methods
	//only accessable by opening a picture
	private double startT = 0.0;
	private double timer = 0.0; 
	private double[] weightFactor = new double[Image.GENRESIZE];
	private Image currentImage; 

	private void startTimer(){
		startT = (double) System.nanoTime()/((double)(1000000 * 1000)); 
	}

	private void stopTimer(){
		timer = (double) System.nanoTime()/((double)(1000000 * 1000)) - startT; 
		startT = 0.0;
	}

	private double readTimer(){
		return timer; 
	}



	// the idea is to take how distributed the data are in the set
	// the more distributed == the more important it is for seprating the picture-taste
	private void constructWeightFactors(){

		int imageCount = 0; 
		double[] average = new double[weightFactor.length];

		//reinit
		for(int i = 0; i < weightFactor.length; i++){
			weightFactor[i] = 0;
			average[i] = 0;
		}

		//take average
		for(Image i : imageFiles){
			for(int j = 0; j < i.GENRESIZE; j++){
				average[j] += i.genre[j];
			}
			imageCount++;
		}
		for(int i = 0; i < average.length; i++){
			average[i] /= imageCount;
		}

		//calculate st. dev
		int currentIndex = 0;
        for(Image i : imageFiles){
        	for(int j = 0; j < i.GENRESIZE; j++){
				weightFactor[j] += (average[j]-i.genre[j])*(average[j]-i.genre[j]);
			}
        }
        for(int i = 0; i < weightFactor.length; i++){
			weightFactor[i] /= imageCount;
			weightFactor[i] = Math.sqrt(weightFactor[i]);
		}

	}




	//file related methods
		//for the image genre scale
		// Fanciness || portrait || gender(0 - male, 0.5 - no charac 1 - female) || landscape (view) || simple || dark (concept) || light || worldly (news) || wordy || future
	Image[] imageFiles = new Image[]{
		new Image("Inspirational Quotes", new double[]{0.75, 0, 0.5, 0, 0.8, 0.1, 0.9, 0.32, 0.9, 0.6}),

		new Image("Playing Genius - Cumberbatch On the Times", new double[]{0.6, 0.9, 0, 0, 0.14, 0.3, 0.8, 0.9, 0.8, 0.2}),

		new Image("World At Night On a Cup", new double[]{0.9, 0, 0.5, 0.2, 0.8, 0.1, 0.2, 0.1, 0.1, 0.95}),

		new Image("Plank Hill Flats - Very Old", new double[]{0.7, 0, 0.5, 0.9, 0.3, 0.4, 0.1, 0.4, 0.3, 0}),

		new Image("LCO - Oval Space London", new double[]{0.7, 0, 0.6, 0.8, 0.7, 0.1, 0.3, 0.6, 0.7, 0.95}),

		new Image("Esther Men Wear", new double[]{0.9, 1.0, 0, 0.1, 0.8, 0.1, 0.7, 0.0, 0, 0.3}),

		new Image("BEAUTIFUL HOMES", new double[]{1, 0, 0.5, 0.9, 0.6, 0, 0.7, 0, 0, 0.9}),

		new Image("Brown Leather Shoes", new double[]{0.9, 0, 0.3, 0, 0.8, 0, 0.3, 0, 0, 0.2}),

		new Image("Futuristic House", new double[]{0.9, 0, 0.5, 0.7, 0.3, 0.2, 0.2, 0.1, 0, 1}),

		new Image("Mannequin - the Movie", new double[]{0.2, 0.9, 0.7, 0.1, 0.1, 0.7, 0.6, 0.1, 0.9, 0.1}),

	};


	
	public void showPictures(){
		System.out.println("\n********* All Pictures *********");
		for(Image i : imageFiles){
			System.out.println("\t"+i.name);
		}
		System.out.println("********************************\n");
	}



	public double openPicture(String name, User currentUser, Command userCommand){
		constructWeightFactors(); //reset the weight factors, no effects on the program yet

		if(pictureOpen){
			System.out.println("Image Already Opened :p");
			return 0; 
		}
		pictureOpen = true;

		for(Image i : imageFiles){
			//found a match
			if(i.name.toLowerCase().contains(name.toLowerCase())){
				System.out.println(i.name + " is opened");

				currentImage = i; 

				double[] weightedGenre = new double[i.GENRESIZE];
				double[] weightedUserGenre = new double[currentUser.genrePreference.length];

				//show how much da user wiil like the picture

				//add weight factor first
				for(int j = 0; j < i.GENRESIZE; j++){
					weightedGenre[j] = i.genre[j] * weightFactor[j]; 
					weightedUserGenre[j] = currentUser.genrePreference[j] * weightFactor[j]; 
					//it's soooo nice that everything's index match :D
				}

				//this is the the number for the result!
				double matchFactor;

				//some temp opertional values
				double numerator = 0;
				double tempWG = 0; //Absolute Value of weighted genre //used for denominator calcuation
				double tempWUG = 0; //Absolute Value of weighted user genre //used for denominator calcuation
				double denominator;

				//now time for cosine law!
				for(int j = 0; j < weightedUserGenre.length; j++){
					numerator += weightedUserGenre[j] * weightedGenre[j];
					tempWG += weightedGenre[j] * weightedGenre[j];
					tempWUG += weightedUserGenre[j] * weightedUserGenre[j];
				}

				tempWG = Math.sqrt(tempWG);
				tempWUG = Math.sqrt(tempWUG);
				denominator = tempWG * tempWUG;

				matchFactor = numerator / denominator; //classic!

				System.out.print("You are "+matchFactor*100+" likly to like this picture.\n");
				startTimer();

				currentUser.addCommand(userCommand);

				return matchFactor; 
			}

		}

		System.out.println("No Image Found :p");
		return 0; 
		
	}


	public void closePicture(User user, Command userCommand){

		if(!pictureOpen){
			System.out.println("No Image Already Opened :p");
			return; 
		}

		final double MICROAPPROACHFACTOR = 0.1; // how rapidly should the genre scales move

		stopTimer();

		//improve user's preference
		double targetProb = user.getProbability(readTimer()); 
		// 0 < targetProb < 1
		if(targetProb != -1){//bell curve is ready!
			if(readTimer() > user.averageTimeOfGaze){
				for(int i = 0; i < user.genrePreference.length; i++){ //all of user's stats approch to the new number
					user.genrePreference[i] = user.genrePreference[i] + ((currentImage.genre[i] - user.genrePreference[i]) * (targetProb - 0.5) * MICROAPPROACHFACTOR); // resulting the approach is from no moving to moving half way
					currentImage.genre[i] = currentImage.genre[i] + ((user.genrePreference[i] - currentImage.genre[i]) * (targetProb - 0.5) * MICROAPPROACHFACTOR);
				}
			}else{
				// TODO: should the stats be moving away???
			}
		}



		user.updateGazeTime(readTimer()); 

		pictureOpen = false;
		System.out.println("Image Closed");
		user.addCommand(userCommand);
	}

	
	


}