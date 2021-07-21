package telegram.vbot.json;

import java.util.Map;

public class ConfigurationJSON {

	public Telegram telegram;
	public Map<String, String> location;
	
	public static class Telegram {
		public BotConfiguration publicBot;
		public BotConfiguration administratorBot;
	}
	
	public static class BotConfiguration {
		public String botToken;
		public String botUsername;
	}
}
