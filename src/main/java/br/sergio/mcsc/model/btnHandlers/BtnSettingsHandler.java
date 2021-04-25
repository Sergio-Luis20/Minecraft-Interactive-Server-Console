package br.sergio.mcsc.model.btnHandlers;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.model.ConsoleWindow;
import br.sergio.mcsc.model.SettingsWindow;
import br.sergio.mcsc.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class BtnSettingsHandler implements EventHandler<ActionEvent> {
	
	private ConsoleWindow consoleWindow;
	
	public BtnSettingsHandler(ConsoleWindow consoleWindow) {
		Utils.validationNull(consoleWindow);
		this.consoleWindow = consoleWindow;
	}
	
	@Override
	public void handle(ActionEvent event) {
		Main.removeSettingsListener(consoleWindow.getSettingsWindow());
		SettingsWindow settingsWindow = new SettingsWindow(consoleWindow);
		consoleWindow.setSettingsWindow(settingsWindow);
		Stage stage = settingsWindow.getStage();
		if(!stage.isShowing()) {
			stage.show();
		}
	}
}
