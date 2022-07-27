package br.sergio.mcsc.model.controls;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.io.Styler;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;

public class ConsoleRadioButton extends RadioButton implements SettingsListener {
	
	private String style;
	private String bundleText;
	private boolean bundle;
	
	{
		try {
			if(!Main.isDefaultConfig()) {
				style = Styler.RADIO_BUTTON.getStyle();
			} else {
				style = "-fx-background-color: #000000; -fx-text-fill: #00ff00; -fx-font-family: \"Lucida Console\";";
			}
		} catch(Exception e) {
			style = "-fx-background-color: #000000; -fx-text-fill: #00ff00; -fx-font-family: \"Lucida Console\";";
		}
	}
	
	public ConsoleRadioButton(String text, boolean bundle) {
		super(bundle ? Main.getBundle().getString(text) : text);
		this.bundle = bundle;
		bundleText = text;
		setStyle(style);
		Main.addSettingsListener(this);
		setAlignment(Pos.CENTER_LEFT);
		setPrefHeight(25);
	}
	
	@Override
	public void call() {
		style = Styler.RADIO_BUTTON.getStyle();
		setStyle(style);
		if(bundle) {
			setText(Main.getBundle().getString(bundleText));
		}
	}
}
