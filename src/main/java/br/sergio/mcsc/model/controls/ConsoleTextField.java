package br.sergio.mcsc.model.controls;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.io.Styler;
import javafx.scene.control.TextField;

public class ConsoleTextField extends TextField implements SettingsListener {
	
	private String style;
	private String bundleText;
	private boolean bundle;
	
	{
		try {
			if(!Main.isDefaultConfig()) {
				style = Styler.TEXT_FIELD.getStyle();
			} else {
				style = "-fx-border-width: 2px; -fx-border-color: #00ff00; -fx-background-color: #000000; -fx-text-fill: #00ff00; -fx-font-family: \"Lucida Console\"; -fx-prompt-text-fill: #00ff00; -fx-highlight-fill: #00ff00; -fx-highlight-text-fill: #000000;";
			}
		} catch(Exception e) {
			style = "-fx-border-width: 2px; -fx-border-color: #00ff00; -fx-background-color: #000000; -fx-text-fill: #00ff00; -fx-font-family: \"Lucida Console\"; -fx-prompt-text-fill: #00ff00; -fx-highlight-fill: #00ff00; -fx-highlight-text-fill: #000000;";
		}
	}
	
	public ConsoleTextField() {
		super();
		setStyle(style);
		Main.addSettingsListener(this);
	}
	
	public void setPromptText(String text, boolean bundle) {
		this.bundle = bundle;
		bundleText = text;
		setPromptText(bundle ? Main.getBundle().getString(bundleText) : bundleText);
	}
	
	@Override
	public void call() {
		style = Styler.TEXT_FIELD.getStyle();
		setStyle(style);
		if(bundle) {
			setPromptText(Main.getBundle().getString(bundleText));
		}
	}
}
