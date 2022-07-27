package br.sergio.mcsc.model;

import br.sergio.mcsc.ServerConsole;
import br.sergio.mcsc.model.controls.ConsoleTextField;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Objects;

public class CommandApplier implements EventHandler<KeyEvent> {
	
	private ServerConsole console;
	
	public CommandApplier(ServerConsole console) {
		this.console = Objects.requireNonNull(console);
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
