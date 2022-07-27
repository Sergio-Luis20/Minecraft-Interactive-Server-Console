package br.sergio.mcsc.model.btnHandlers;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.model.ConsoleWindow;
import br.sergio.mcsc.model.SettingsWindow;
import br.sergio.mcsc.model.controls.ConsoleButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

import java.util.Objects;

public class BtnSettingsHandler implements EventHandler<ActionEvent> {
	
	private ConsoleWindow consoleWindow;
	private SettingsWindow settingsWindow;
	
	public BtnSettingsHandler(ConsoleWindow consoleWindow) {
		this.consoleWindow = Objects.requireNonNull(consoleWindow);
	}
	
	@Override
	public void handle(ActionEvent event) {
		Main.removeSettingsListener(settingsWindow);
		settingsWindow = new SettingsWindow(consoleWindow);
		Stage stage = settingsWindow.getStage();
		if(!stage.isShowing()) {
			stage.show();
			((ConsoleButton) event.getSource()).toNormalStyle();
		}
	}
}
