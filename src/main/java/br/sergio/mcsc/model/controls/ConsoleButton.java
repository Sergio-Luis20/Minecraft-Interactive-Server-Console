package br.sergio.mcsc.model.controls;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.io.Styler;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class ConsoleButton extends Button implements SettingsListener {
	
	private String normalExitedStyle;
	private String enteredReleasedStyle;
	private String pressedStyle;
	private String bundleText;
	private boolean bundle;
	
	{
		try {
			if(!Main.isDefaultConfig()) {
				normalExitedStyle = Styler.BUTTON.getStyle();
				enteredReleasedStyle = Styler.MOUSE_ENTERED_RELEASED_BUTTON.getStyle();
				pressedStyle = Styler.MOUSE_PRESSED_BUTTON.getStyle();
			} else {
				setDefaults();
			}
		} catch(Exception e) {
			setDefaults();
		}
	}
	
	public ConsoleButton(String text, boolean bundle) {
		this(text, bundle, null);
	}
	
	public ConsoleButton(String text, boolean bundle, Node icon) {
		super(bundle ? Main.getBundle().getString(text) : text, icon);
		this.bundle = bundle;
		bundleText = text;
		setFocusTraversable(false);
		setStyle(normalExitedStyle);
		addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent event) {
				EventType<? extends MouseEvent> type = event.getEventType();
				if(type == MouseEvent.MOUSE_ENTERED || type == MouseEvent.MOUSE_RELEASED) {
					setStyle(enteredReleasedStyle);
				} else if(type == MouseEvent.MOUSE_PRESSED) {
					setStyle(pressedStyle);
				} else if(type == MouseEvent.MOUSE_EXITED) {
					setStyle(normalExitedStyle);
				}
			}
			
		});
		Main.addSettingsListener(this);
	}
	
	private void setDefaults() {
		normalExitedStyle = "-fx-border-width: 2px; -fx-border-color: #00ff00; -fx-background-color: #000000; -fx-text-fill: #00ff00; "
				+ "-fx-font-family: \"Lucida Console\";";
		enteredReleasedStyle = "-fx-border-width: 2px; -fx-border-color: #ffffff; -fx-background-color: #000000; -fx-text-fill: #ffffff; "
				+ "-fx-font-family: \"Lucida Console\";";
		pressedStyle = "-fx-border-width: 2px; -fx-border-color: #ffffff; -fx-background-color: #808080; -fx-text-fill: #ffffff; "
				+ "-fx-font-family: \"Lucida Console\";";
	}
	
	public void toNormalStyle() {
		setStyle(normalExitedStyle);
	}
	
	@Override
	public void call() {
		normalExitedStyle = Styler.BUTTON.getStyle();
		enteredReleasedStyle = Styler.MOUSE_ENTERED_RELEASED_BUTTON.getStyle();
		pressedStyle = Styler.MOUSE_PRESSED_BUTTON.getStyle();
		setStyle(normalExitedStyle);
		if(bundle) {
			setText(Main.getBundle().getString(bundleText));
		}
	}
}
