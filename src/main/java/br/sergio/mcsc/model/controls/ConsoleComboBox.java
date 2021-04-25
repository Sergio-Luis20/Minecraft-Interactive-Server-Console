package br.sergio.mcsc.model.controls;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.io.Styler;
import javafx.scene.control.ComboBox;

public class ConsoleComboBox extends ComboBox<ConsoleLabel> implements SettingsListener {
	
	private String style;
	
	{
		try {
			if(!Main.isDefaultConfig()) {
				style = Styler.COMBO_BOX.getStyle();
			} else {
				style = "-fx-border-width: 2px; -fx-border-color: #00ff00; -fx-background-color: #000000; -fx-text-fill: #00ff00; "
						+ "-fx-font-family: \"Lucida Console\";";
			}
		} catch(Exception e) {
			style = "-fx-border-width: 2px; -fx-border-color: #00ff00; -fx-background-color: #000000; -fx-text-fill: #00ff00; "
					+ "-fx-font-family: \"Lucida Console\";";
		}
	}
	
	public ConsoleComboBox() {
		super();
		setStyle(style);
		Main.addSettingsListener(this);
	}
	
	@Override
	public void call() {
		style = Styler.COMBO_BOX.getStyle();
		setStyle(style);
	}
}
