package telegram.vbot.visitors;

import java.io.IOException;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.gson.JsonIOException;

import telegram.vbot.DataAccessObject;
import telegram.vbot.L;
import telegram.vbot.VConstants;
import telegram.vbot.json.VolunteerJSON;

public class PublicMenuVisitor {
	
	private final String correlationId;
	private final Update update;
	private final AbsSender sender;
	
	/**
	 * 
	 * @param correlationId
	 * @param update
	 * @param sender
	 */
	public PublicMenuVisitor(String correlationId, Update update, AbsSender sender) {
		this.correlationId = correlationId;
		this.update = update;
		this.sender = sender;
	}

	/**
	 * 
	 * @param volunteer
	 * @param update
	 * @param sender
	 * @throws TelegramApiException 
	 * @throws IOException 
	 * @throws JsonIOException 
	 */
	public void accept(VolunteerJSON volunteer) throws TelegramApiException, JsonIOException, IOException {
		
		// Checking Assist:
		if (VConstants.USERINPUT_CHECKIN.equalsIgnoreCase(update.getMessage().getText())) {
			checkinAssistMenu(volunteer);
		}
		// Leaving post:
		else if (VConstants.USERINPUT_CHECKOUT.equalsIgnoreCase(update.getMessage().getText())) {
			checkout(volunteer);
		}
		// Report Summary:
		else if (VConstants.USERINPUT_REPORTSUMMARY.equalsIgnoreCase(update.getMessage().getText())) {
			reportSummary(volunteer);
		}		
		// Register location.
		else if (VConstants.MENU_SUBMENU_CHECKIN == volunteer.currentMenuState) {
			checkin(volunteer);	
		}
		// Display Main Menu.
		else if ((VConstants.MENU_MAIN == volunteer.currentMenuState)) {
			mainMenu(volunteer);
		}
	}

	/**
	 * 
	 * @param volunteer
	 * @param update
	 * @param sender
	 * @throws TelegramApiException 
	 */
	private void checkinAssistMenu(VolunteerJSON volunteer) throws TelegramApiException {
		final StringBuilder textOutput = new StringBuilder();
		textOutput.append("Reply with V-Number and area, eg \"V123456 A\"\n");
		for (Map.Entry<String, String> entry : DataAccessObject.getInstance().getConfiguration().location.entrySet()) {
			textOutput.append(String.format("%s. %s%n", entry.getKey(), entry.getValue()));	
		}			
		volunteer.nextMenuState = VConstants.MENU_SUBMENU_CHECKIN;
		sendMessage(textOutput);
	}

	/**
	 * 
	 * @param volunteer
	 * @param update
	 * @param sender
	 * @throws TelegramApiException 
	 */
	private void mainMenu(VolunteerJSON volunteer) throws TelegramApiException {
		final StringBuilder textOutput = new StringBuilder();
		textOutput.append("Volunteer Menu\n");
		textOutput.append(String.format("%s. Sign In%n", VConstants.USERINPUT_CHECKIN));
		textOutput.append(String.format("%s. Sign Off%n", VConstants.USERINPUT_CHECKOUT));
		textOutput.append(String.format("%s. Summary Report%n", VConstants.USERINPUT_REPORTSUMMARY));
		sendMessage(textOutput);
	}

	/**
	 * 
	 * @param volunteer
	 * @param update
	 * @param sender
	 * @throws TelegramApiException 
	 * @throws IOException 
	 * @throws JsonIOException 
	 */
	private void checkin(VolunteerJSON volunteer) throws TelegramApiException, JsonIOException, IOException {
		final StringBuilder textOutput = new StringBuilder();
		final String[] parts = update.getMessage().getText().split(" ");
		final String vId = parts[VConstants.CHECKIN_INDEX_VID];
		final String checkpointId = parts[VConstants.CHECKIN_INDEX_CHECKPOINT];
		if (!DataAccessObject.getInstance().getConfiguration().location.containsKey(checkpointId)) {
			volunteer.nextMenuState = VConstants.MENU_MAIN;
			textOutput.append(String.format("Sorry %s, invalid area entered.%n", volunteer.firstname));
			sendMessage(textOutput);				
		}
		else if (!(vId.startsWith("v") || vId.startsWith("V"))) {
			volunteer.nextMenuState = VConstants.MENU_MAIN;
			textOutput.append(String.format("Sorry %s, invalid V-ID entered.%n", volunteer.firstname));
			sendMessage(textOutput);
		}
		else {
			volunteer.nextMenuState = VConstants.MENU_MAIN;
			textOutput.append(String.format("Got it.  Thanks %s!%n", volunteer.firstname));
			textOutput.append(String.format("%s and you are at %s", vId, DataAccessObject.getInstance().getConfiguration().location.get(checkpointId)));
			volunteer.active = true;
			volunteer.vId = vId;
			volunteer.checkpointId = checkpointId;
			volunteer.activeSince = System.currentTimeMillis();
			DataAccessObject.getInstance().flushVolunteers(correlationId);
			sendMessage(textOutput);
		}
	}
	
	/**
	 * 
	 * @param volunteer
	 * @param update
	 * @param sender
	 * @throws TelegramApiException 
	 * @throws IOException 
	 * @throws JsonIOException 
	 */
	private void checkout(VolunteerJSON volunteer) throws TelegramApiException, JsonIOException, IOException {
		final StringBuilder textOutput = new StringBuilder();
		if (!volunteer.active) {
			volunteer.nextMenuState = VConstants.MENU_MAIN;
			textOutput.append(String.format("Hi %s, no need to checkout, you are not avtive.%n", volunteer.firstname));
			sendMessage(textOutput);				
		}
		// Success Checkout:
		else if (volunteer.active) {
			volunteer.nextMenuState = VConstants.MENU_MAIN;
			textOutput.append(String.format("Got it.  Thanks %s!%n",volunteer.firstname));
			textOutput.append(String.format("%s has been checked out of %s", volunteer.vId, DataAccessObject.getInstance().getConfiguration().location.get(volunteer.checkpointId)));
			
			volunteer.active = false;
			volunteer.checkpointId = null;
			volunteer.activeSince = 0L;
			DataAccessObject.getInstance().flushVolunteers(correlationId);
			sendMessage(textOutput);
		}
	}
	
	/**
	 * 
	 * @param volunteer
	 * @param update
	 * @param sender
	 * @throws TelegramApiException 
	 */
	private void reportSummary(VolunteerJSON volunteer) throws TelegramApiException {
		volunteer.nextMenuState = VConstants.MENU_MAIN;
		final StringBuilder textOutput = new StringBuilder();
		textOutput.append(String.format("Area Summary%n"));
		for (Map.Entry<String, String> entry : DataAccessObject.getInstance().getConfiguration().location.entrySet()) {
			textOutput.append(String.format("%s : %d%n", entry.getValue(), DataAccessObject.getInstance().getVolunteers().values().stream().filter(v -> v.checkpointId != null && v.checkpointId.equalsIgnoreCase(entry.getKey())).count()));
		}
		sendMessage(textOutput);
	}

	/**
	 * 
	 * @param correlationId
	 * @param textOutput
	 * @param update
	 * @param sender
	 * @throws TelegramApiException 
	 */
	private void sendMessage(StringBuilder textOutput) throws TelegramApiException {
		final SendMessage message = new SendMessage();
		message.setChatId(update.getMessage().getChatId().toString());
		message.setText(textOutput.toString());
		message.setReplyToMessageId(update.getMessage().getMessageId());		
		sender.execute(message); // Call method to send the message
		L.getLogger(VConstants.LOG_PUBLIC).info(String.format("%s TX %s", correlationId, textOutput.toString().replace("\n", "").replace("\r", "")));
	}
}
