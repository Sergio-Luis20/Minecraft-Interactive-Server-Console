package br.sergio.mcsc.io;

import java.io.IOException;

import org.json.simple.JSONObject;

import br.sergio.mcsc.Main;

public enum Styler {
	
	TEXT_AREA("TextArea"),
	BUTTON("Button"),
	MOUSE_ENTERED_RELEASED_BUTTON("MouseEnteredReleasedButton"),
	MOUSE_PRESSED_BUTTON("MousePressedButton"),
	TEXT_FIELD("TextField"),
	LABEL("Label"),
	RADIO_BUTTON("RadioButton"),
	COMBO_BOX("ComboBox");
	
	private String style;
	private String configNodeStyle;
	
	private Styler(String configNodeStyle) {
		this.configNodeStyle = configNodeStyle;
		try {
			JSONObject styles = (JSONObject) Main.getConfig().getObject().get("NodeStyles");
			style = (String) styles.get(configNodeStyle);
		} catch(Exception e) {
			style = null;
			e.printStackTrace();
		}
	}
	
	public String getConfigNodeStyle() {
		return configNodeStyle;
	}
	
	public String getStyle() {
		return style;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void setStyle(String style) {
		try {
			if(style == null) {
				style = "";
			}
			if(!Main.isDefaultConfig()) {
				JSONObject styles = (JSONObject) Main.getConfig().getObject().get("NodeStyles");
				styles.replace(configNodeStyle, style);
				Main.getConfig().saveConfig();
			}
			this.style = style;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String createStyle(String borderWidth, String borderColor, String backgroundColor, String textFill, String promptTextFill, 
			String fontFamily, String fontSize, String highlightFill, String highlightTextFill) {
		StringBuilder sb = new StringBuilder();
		if(borderWidth != null) {
			sb.append("-fx-border-width: " + borderWidth + "; ");
		}
		if(borderColor != null) {
			sb.append("-fx-border-color: " + borderColor + "; ");
		}
		if(backgroundColor != null) {
			sb.append("-fx-background-color: " + backgroundColor + "; ");
		}
		if(textFill != null) {
			sb.append("-fx-text-fill: " + textFill + "; ");
		}
		if(promptTextFill != null) {
			sb.append("-fx-prompt-text-fill: " + promptTextFill + "; ");
		}
		if(fontFamily != null) {
			sb.append("-fx-font-family: \"" + fontFamily + "\"; ");
		}
		if(fontSize != null) {
			sb.append("-fx-font-size: " + fontSize + "; ");
		}
		if(highlightFill != null) {
			sb.append("-fx-highlight-fill: " + highlightFill + "; ");
		}
		if(highlightTextFill != null) {
			sb.append("-fx-highlight-text-fill: " + highlightTextFill + "; ");
		}
		return sb.toString().trim();
	}
}
