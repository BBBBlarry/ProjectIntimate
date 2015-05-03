import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;




public class UserInterface{

	//fileSystem
	Filesystem filesys = new Filesystem();


	//user command input
	private Scanner input;
	private Command userCommand; 


	//local varibles
	private boolean isInit;
	private boolean isAdmin; 
	private String helpMess;
	private String adminHelpMess;
	

	//user accounts
	ArrayList<User> users = new ArrayList<User>();
	User currentUser; 


	//it looks like an enum switch must acess local enums
	//TODO: find out how to get rid of "an enum switch case label must be the unqualified name of an enumeration constant"

	public enum Type{
		NONE,
		SYSTEM_OPERATION_SIGN_IN, SYSTEM_OPERATION_SIGN_UP, SYSTEM_OPERATION_ADMIN,
		PICTURE_OPERATION_OPEN, PICTURE_OPERATION_CLOSE, PICTURE_OPERATION_SHOW,
		OTHER_HELP, OTHER_QUIT
	}

	public enum AdminType{
		NONE, 
		HELP,
		SHOW_TABLES, 
		SHOW_USER_ACTION, 
		QUIT
	}



	private void init(){
		input = new Scanner(System.in);
		userCommand = new Command();
		isInit = true; 
		isAdmin = false;
		helpMess = "********** Help **********\nSystem:\n\tsign in [username]\n\tsign up [username]\n\tadmin\nFile System:\n\tshow pictures\n\topen picture [title]\n\tclose picture\n\n";
		adminHelpMess = "********** Help **********\nSystem:\n\tshow user tables\n\tshow actions[username]\n\n";
		currentUser = new User("guest", new double[]{0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5});
		users.add(currentUser);
	}




	public void processInterface(){
		if(!isInit) return; //must be inited before using

		if(!isAdmin) {
			System.out.print(currentUser.username+": ");
		}else{
			System.out.print("Admin: ");
		}
		userCommand = textToCommand(input.nextLine());

		if(userCommand == null) {
			System.out.print("Command Can't Be Recognized\n");
			return;
		}

		
		
		
		if(!isAdmin){ //non admin codes
			switch(userCommand.type){
				case NONE:
					//very unlikely to happen
					break;
				case SYSTEM_OPERATION_SIGN_IN:
					signIn(userCommand.parameter);
					break;
				case SYSTEM_OPERATION_SIGN_UP:
					signUp(userCommand.parameter);
					break;
				case SYSTEM_OPERATION_ADMIN:
					isAdmin = true;
					break;
				case PICTURE_OPERATION_OPEN:
					filesys.openPicture(userCommand.parameter, currentUser, userCommand); 
					break;
				case PICTURE_OPERATION_CLOSE:
					filesys.closePicture(currentUser, userCommand);
					break;
				case PICTURE_OPERATION_SHOW:
					filesys.showPictures();
					break;
				case OTHER_HELP:
					System.out.print(helpMess);
					break;
				default:
					//very unlikely to happen
					break;
			}
		}

		else{ //admin code
			switch(userCommand.adminType){
				case NONE:
					//very unlikely to happen
					break;
				case HELP:
					System.out.print(adminHelpMess);
					break;
				case SHOW_TABLES:
					showUserGenre();
					break;
				case SHOW_USER_ACTION:
					showAllActions(userCommand.parameter);
					break;
				case QUIT:
					isAdmin = false;
					break;
				default:
					//very unlikely to happen
					break;
			}
		}

		//very informative update
		//System.out.printf("\nType: %s\nAdminType: %s\nParameter: %s\nTime: %f\n\n", userCommand.type, userCommand.adminType, userCommand.parameter, ((double)System.nanoTime()/((double)1000000 * (double)1000)));




	}




	public static void main(String[] args){
		UserInterface interf = new UserInterface();
		interf.init();
		while(true){
			interf.processInterface();
		}

	}
















	private Command textToCommand(String text){

		text = text.toLowerCase();
		int COMMAND_SIZE = 10; 

		int wordCount; // the total words
		int letterCount; // letter count with no spaces
		String[] textArray = new String[COMMAND_SIZE];
		Command command = new Command();




		//get wordCount and letterCount, and put text into array
		int numberOfSpaces = 0; 
		int currentArrayIndex = 0;

		for(int i = 0; i < COMMAND_SIZE; i++){ //init
			textArray[i] = "";
		}


		for(int i = 0; i < text.length(); i++){
			if (text.charAt(i) == ' '){
				if(currentArrayIndex >= COMMAND_SIZE){
					return null; // noooooo, don't get out of index error!
				}
				numberOfSpaces++;
				currentArrayIndex++;
			}else{
				textArray[currentArrayIndex] += text.charAt(i);
			}
		}
		wordCount = currentArrayIndex + 1; // well, if the user is not so anti-author
		letterCount = text.length() - numberOfSpaces;




		//convert string into commands
		//special cases
		if (wordCount == 0) return null; // no word
		if (wordCount > COMMAND_SIZE) return null; //hua lao



		//admin command
		if (isAdmin){
			command.isAdmin = true; 

			//one word commands
			if (wordCount == 1){
				switch(text){
					case "help": 
						command.adminType = Command.AdminType.HELP;
						break;
					case "quit": 
						command.adminType = Command.AdminType.QUIT; 
						break;
					default: 
						return null;
						
				}
				
			}
			else if(text.contains("show")){
				if(text.contains("table")){
					command.adminType = Command.AdminType.SHOW_TABLES;
				}

				else if(text.contains("actions")){
					command.adminType = Command.AdminType.SHOW_USER_ACTION;
					command.parameter = getNextWord(textArray, "actions", false, wordCount);
					if (command.parameter == null) return null; 
				}

				else{
					return null;
				}
			}



		}else{ //not admin command
			if (wordCount == 1){
				switch(text){
					case "admin": 
						command.isAdmin = true;
						command.type = Command.Type.SYSTEM_OPERATION_ADMIN;
						break;

					case "help": 
						command.type = Command.Type.OTHER_HELP;
						break;
					default:
						return null;
				}
			}


			else if (text.contains("picture")){ // picture operations
				if (text.contains("open")){
					command.type = Command.Type.PICTURE_OPERATION_OPEN;
					command.parameter = getNextWord(textArray, "picture", false, wordCount);
					if (command.parameter == null) return null; 

				}else if(text.contains("close")){
					command.type = Command.Type.PICTURE_OPERATION_CLOSE;
				}else if(text.contains("show")){
					command.type = Command.Type.PICTURE_OPERATION_SHOW;
				}
			}


			else if (text.contains("sign")){ // picture operations
				if (text.contains("in")){
					command.type = Command.Type.SYSTEM_OPERATION_SIGN_IN;
					command.parameter = getNextWord(textArray, "in", false, wordCount);
					if (command.parameter == null) return null; 

				}else if(text.contains("up")){
					command.type = Command.Type.SYSTEM_OPERATION_SIGN_UP;
					command.parameter = getNextWord(textArray, "up", false, wordCount);
					if (command.parameter == null) return null; 
				}

			}

			else{
				return null;
			}


		}


		return command;

	}



	private String getNextWord(String[] theArray, String baseWord, boolean caseSensitive, int validWordCount){
		if(!caseSensitive){//only switch base for now, because later it's gonna take the next word with no case problem
			baseWord = baseWord.toLowerCase();
		}
		
		for (int i = 0; i < validWordCount-1; i++){
			
			if(!caseSensitive){
				if (theArray[i].toLowerCase().equals(baseWord)){
					
					return theArray[i+1];
				}
			}else{
				if (theArray[i].equals(baseWord)){
					return theArray[i+1];
				}
			}
		}

		
		return null; 
	}




	private void signIn(String username){
		if(currentUser.username.equals(username)){
			System.out.println("You are already in!");
			return; 
		}

		if(filesys.pictureOpen){ //switch user must close picture
			filesys.closePicture(currentUser, new Command(Command.Type.PICTURE_OPERATION_CLOSE, Command.AdminType.NONE, false, ""));
		}


		for(User u : users){
			if(u.username.equals(username)){
				currentUser = u; 
				System.out.println("Welcome " + currentUser.username + "!");
				return; 
			}
		}
		System.out.println("No such user :p");

	}

	private void signUp(String username){

		for(User u : users){
			if(u.username.equals(username)){
				System.out.println("Username already exist :p");
				return;
			}
		}


		if(filesys.pictureOpen){ //switch user must close picture
			filesys.closePicture(currentUser, new Command(Command.Type.PICTURE_OPERATION_CLOSE, Command.AdminType.NONE, false, ""));
		}

		currentUser = new User(username, new double[]{0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5}); 
		users.add(currentUser);
		
		System.out.println("Welcome " + currentUser.username + "!");
	}


	private void showAllActions(String username){
		for(User u : users){
			if(u.username.equals(username)){
				System.out.println(username+"'s Actions:");

				//read actions in order
				Set<Entry> entries = u.actions.entrySet();
        		Iterator<Entry> iter = entries.iterator();
        		System.out.println("\tTime\t\tType\t\t\tParameter");
		        while(iter.hasNext()) {
		            Entry entry = iter.next();
		            Command historyCommand = (Command)entry.getValue();
		            System.out.println("\t" + entry.getKey() + "\t" + historyCommand.type + "\t" + historyCommand.parameter);
		        }

			}
		}
		System.out.print('\n');
	}

	private void showUserGenre(){
		for(User u : users){
			System.out.print(u.username + ": ");
			for(double g : u.genrePreference){
				System.out.print(g + " ");
			}
			System.out.print('\n');
		}

		System.out.print('\n');
	}


}









