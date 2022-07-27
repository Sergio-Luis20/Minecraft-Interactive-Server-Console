package br.sergio.mcsc.model;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.io.Styler;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.Objects;

public class DisplayServerConsole extends TextArea implements SettingsListener {
	
	private String style;
	private Stage ownerStage;
	
	{
		try {
			if(!Main.isDefaultConfig()) {
				style = Styler.TEXT_AREA.getStyle();
			} else {
				style = "-fx-border-width: 1px; -fx-border-color: #00ff00; -fx-background-color: #00ff00; -fx-text-fill: #00ff00; "
						+ "-fx-font-family: \"Lucida Console\"; -fx-font-size: 11pt; -fx-highlight-fill: #00ff00; "
						+ "-fx-highlight-text-fill: #000000;";
			}
		} catch(Exception e) {
			style = "-fx-border-width: 1px; -fx-border-color: #00ff00; -fx-background-color: #00ff00; -fx-text-fill: #00ff00; "
					+ "-fx-font-family: \"Lucida Console\"; -fx-font-size: 11pt; -fx-highlight-fill: #00ff00; "
					+ "-fx-highlight-text-fill: #000000;";
		}
	}
	
	public DisplayServerConsole(Stage owner) {
		super();
		ownerStage = Objects.requireNonNull(owner);
		setStyle(style);
		setWrapText(true);
		setEditable(false);
		Main.addSettingsListener(this);
	}
	
	public Stage getOwnerStage() {
		return ownerStage;
	}
	
	@Override
	public void call() {
		style = Styler.TEXT_AREA.getStyle();
		setStyle(style);
	}
}
