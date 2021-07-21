package telegram.vbot;

/**
 * 
 */
public class VConstants {

	public static final int MENU_MAIN = 1;
	public static final int MENU_SUBMENU_CHECKIN = 2;
	
	public static final String USERINPUT_CHECKIN = "1";
	public static final String USERINPUT_CHECKOUT = "2";
	public static final String USERINPUT_REPORTSUMMARY = "3";

	// Parsing Text:
	public static final int CHECKIN_INDEX_VID = 0;
	public static final int CHECKIN_INDEX_CHECKPOINT = 1;
	
	public static final String CONFIGURATION_FILE_NAME = "configuration.json";
	public static final String VOLUNTEERSTATUS_FILE_NAME = "volunteerstatus.json";	
	
	// Logging:
	public static final String LOG_APPLICATION = "application";
	public static final String LOG_PUBLIC = "public";
	public static final String LOG_ADMINISTRATOR = "administrator";
	
	private VConstants() {
		
	}
}
