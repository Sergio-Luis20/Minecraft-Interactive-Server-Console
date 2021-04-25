package br.sergio.mcsc.model;

import br.sergio.mcsc.ServerConsole;
import br.sergio.mcsc.model.controls.ConsoleTextField;
import br.sergio.mcsc.utils.Utils;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CommandApplier implements EventHandler<KeyEvent> {
	
	private ServerConsole console;
	
	public CommandApplier(ServerConsole console) {
		Utils.validationNull(console);
		this.console = console;
	}
	
	@Override
	public void handle(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			ConsoleTextField source = (ConsoleTextField) event.getSource();
			console.dispatchCommand(source.getText());
			source.clear();
		}
	}
	
}
