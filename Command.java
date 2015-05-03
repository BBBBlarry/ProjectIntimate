
public class Command{


	public String parameter = "";
	public boolean isAdmin = false; //admin related commands

	public Type type = Type.NONE;
	public AdminType adminType = AdminType.NONE; 


	Command(Type type, AdminType adminType, boolean isAdmin, String parameter){
		this.type = type;
		this.adminType = adminType;
		this.isAdmin = isAdmin;
		this.parameter = parameter;
	}

	Command(){
		//defalt
	}

	

	public enum Type{
		NONE,
		SYSTEM_OPERATION_SIGN_IN, SYSTEM_OPERATION_SIGN_UP, SYSTEM_OPERATION_ADMIN,
		PICTURE_OPERATION_OPEN, PICTURE_OPERATION_CLOSE, PICTURE_OPERATION_SHOW,
		OTHER_HELP
	}

	public enum AdminType{
		NONE, 
		HELP,
		SHOW_TABLES, 
		SHOW_USER_ACTION, 
		QUIT
	}

}





