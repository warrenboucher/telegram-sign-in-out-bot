package telegram.vbot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;

import telegram.vbot.json.AbstractPersonJSON;
import telegram.vbot.json.Administrator;
import telegram.vbot.json.ConfigurationJSON;
import telegram.vbot.json.VolunteerJSON;
import telegram.vbot.json.VolunteerStatusJSON;

public class DataAccessObject {

	private final Map<Long, AbstractPersonJSON> administrators = new HashMap<>();	
	
	private ConfigurationJSON configuration;
	private VolunteerStatusJSON volunteerStatus;
	
	private static final Gson gson = new Gson();
	private static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + File.separator + VConstants.CONFIGURATION_FILE_NAME;
	private static final String VOLUNTEER_FILE_PATH = System.getProperty("user.dir") + File.separator + VConstants.VOLUNTEERSTATUS_FILE_NAME;
	private static final DataAccessObject INSTANCE = new DataAccessObject();
		
	/**
	 * 
	 */
	private DataAccessObject() {
		
		try (final JsonReader configReader = new JsonReader(new FileReader(CONFIG_FILE_PATH))) {
			setConfiguration(gson.fromJson(configReader, ConfigurationJSON.class));	
		} catch (IOException e) {
			L.getLogger(VConstants.LOG_APPLICATION).error(String.format("Exception while loading %s.  %s", CONFIG_FILE_PATH, e.getMessage()), e);
		}
				
		try (final JsonReader volunteerReader = new JsonReader(new FileReader(VOLUNTEER_FILE_PATH))) {
			setVolunteerStatus(gson.fromJson(volunteerReader, VolunteerStatusJSON.class));
		} catch (IOException e) {
			L.getLogger(VConstants.LOG_APPLICATION).error(String.format("Exception while loading %s.  %s", VOLUNTEER_FILE_PATH, e.getMessage()), e);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static DataAccessObject getInstance() {
		return INSTANCE;
	}	
	
	/**
	 * flush volunteers to file system.
	 * @throws JsonIOException
	 * @throws IOException
	 */
	public synchronized void flushVolunteers(String correlationId) throws JsonIOException, IOException {
		final long startTime = System.currentTimeMillis();
		try (FileWriter writer = new FileWriter(VOLUNTEER_FILE_PATH)) {
			gson.toJson(volunteerStatus, writer);
		}
		L.getLogger(VConstants.LOG_APPLICATION).info(String.format("%s Time taken to flush volunteers %dms", correlationId, (System.currentTimeMillis()-startTime)));
	}
	
	public boolean isRegisteredAdmin(Long adminId) {
		return getAdministrators().containsKey(adminId);
	}
	
	public void addAdmin(Long adminId, Administrator admin) {
		getAdministrators().put(adminId, admin);
	}
	
	public boolean isRegisteredVolunteer(Long userId) {
		return getVolunteers().containsKey(userId);
	}
	
	public void addVolunteer(Long userId, VolunteerJSON volunteer) {
		getVolunteers().put(userId, volunteer);
	}
	
	public AbstractPersonJSON getVolunteer(Long userId) {
		return getVolunteers().get(userId);
	}

	public Map<Long, VolunteerJSON> getVolunteers() {
		return volunteerStatus.volunteers;
	}

	public Map<Long, AbstractPersonJSON> getAdministrators() {
		return administrators;
	}

	public ConfigurationJSON getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ConfigurationJSON configuration) {
		this.configuration = configuration;
	}

	public VolunteerStatusJSON getVolunteerStatus() {
		return volunteerStatus;
	}

	public void setVolunteerStatus(VolunteerStatusJSON volunteerStatus) {
		this.volunteerStatus = volunteerStatus;
	}

}