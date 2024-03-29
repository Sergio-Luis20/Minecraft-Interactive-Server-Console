package br.sergio.mcsc.model.btnHandlers;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.ServerConsole;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.io.ConfigFile;
import br.sergio.mcsc.io.Styler;
import br.sergio.mcsc.model.SettingsWindow;
import br.sergio.mcsc.model.controls.ConsoleButton;
import br.sergio.mcsc.model.controls.ConsoleLabel;
import br.sergio.mcsc.model.controls.ConsoleRadioButton;
import br.sergio.mcsc.model.elements.Currents;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class BtnApplyHandler implements EventHandler<ActionEvent>, SettingsListener {
	
	private SettingsWindow settingsWindow;
	private ServerConsole console;
	private Alert alert;
	private String oldSpigot;
	private String oldJvmArgs;
	private ConsoleButton source;
	
	public BtnApplyHandler(SettingsWindow settingsWindow, ServerConsole console) {
		Main.addSettingsListener(this);
		this.settingsWindow = Objects.requireNonNull(settingsWindow);
		this.console = Objects.requireNonNull(console);
		alert = new Alert(AlertType.WARNING);
		alert.initOwner(settingsWindow.getStage());
		alert.setTitle(Main.getBundle().getString("warning"));
		oldSpigot = Currents.serverDir;
		oldJvmArgs = Currents.jvmArgs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handle(ActionEvent event) {
		source = (ConsoleButton) event.getSource();
		ToggleGroup group = settingsWindow.getRadioButtonsGroup();
		String colorType = ((ConsoleRadioButton) group.getSelectedToggle()).getText();
		String newColor;
		switch (colorType) {
			case "RGB" -> {
				String redText = settingsWindow.getRed().getText();
				String greenText = settingsWindow.getGreen().getText();
				String blueText = settingsWindow.getBlue().getText();
				if (redText.isEmpty() || greenText.isEmpty() || blueText.isEmpty()) {
					showAlert("invalidRGBValue", "rgbWarningText");
					return;
				}
				int red, green, blue;
				try {
					red = Integer.parseInt(redText);
					green = Integer.parseInt(greenText);
					blue = Integer.parseInt(blueText);
				} catch(NumberFormatException e) {
					showAlert("invalidRGBValue", "rgbWarningText");
					return;
				}
				newColor = "#" + Color.rgb(red, green, blue).toString().substring(2, 8);
			}
			case "Hexadecimal" -> {
				String hex = settingsWindow.getHex().getText();
				String validChars = "0123456789abcdefABCDEF";
				boolean invalidChar = false;
				char[] charArray = hex.toCharArray();
				for (int i = 1; i < charArray.length; i++) {
					if (!validChars.contains(String.valueOf(charArray[i]))) {
						invalidChar = true;
						break;
					}
				}
				if (!hex.startsWith("#") || hex.length() != 7 || invalidChar) {
					showAlert("invalidHexValue", "hexWarningText");
					return;
				}
				newColor = hex;
			}
			default -> {
				ConsoleLabel selected = settingsWindow.getBasicColors().getSelectionModel().getSelectedItem();
				if (selected == null) {
					showAlert("invalidBasicValue", "basicWarningText");
					return;
				}
				newColor = settingsWindow.getSortedColors().get(selected.getBundleText());
			}
		}
		newColor = newColor.toLowerCase();
		String fontFamily = settingsWindow.getFamilies().getSelectionModel().getSelectedItem().getText();
		String consoleSize = settingsWindow.getSizes().getSelectionModel().getSelectedItem().getText() + "pt";
		String serverDir = settingsWindow.getServerField().getText();
		String jvmArgs = settingsWindow.getJVMArgs().getText();
		String language = settingsWindow.getLang().getSelectionModel().getSelectedItem().getText();
		if(console.isServerRunning() && (!serverDir.equals(oldSpigot) || !jvmArgs.equals(oldJvmArgs))) {
			showAlert("cantChange", "cantChangeRuntime");
			return;
		}
		console.setServerDirectory(new File(serverDir));
		console.setJvmArgs(jvmArgs);
		Currents.colorType = colorType;
		Currents.colorTheme = newColor;
		Currents.fontFamily = fontFamily;
		Currents.consoleFontSize = consoleSize;
		Currents.serverDir = serverDir;
		Currents.jvmArgs = jvmArgs;
		Currents.language = language;
		Styler.TEXT_AREA.setStyle(Styler.createStyle("2px", newColor, "#000000", newColor, null, fontFamily, consoleSize, newColor, "#000000"));
		Styler.BUTTON.setStyle(Styler.createStyle("2px", newColor, "#000000", newColor, null, fontFamily, null, null, null));
		Styler.MOUSE_ENTERED_RELEASED_BUTTON.setStyle(Styler.createStyle("2px", "#ffffff", "#000000", "#ffffff", null, fontFamily, null, null, null));
		Styler.MOUSE_PRESSED_BUTTON.setStyle(Styler.createStyle("2px", "#ffffff", "#808080", "#ffffff", null, fontFamily, null, null, null));
		Styler.TEXT_FIELD.setStyle(Styler.createStyle("2px", newColor, "#000000", newColor, newColor, fontFamily, null, newColor, "#000000"));
		Styler.LABEL.setStyle(Styler.createStyle(null, null, "#000000", newColor, null, fontFamily, null, null, null));
		Styler.RADIO_BUTTON.setStyle(Styler.createStyle(null, null, "#000000", newColor, null, fontFamily, null, null, null));
		Styler.COMBO_BOX.setStyle(Styler.createStyle("2px", newColor, "#000000", newColor, null, fontFamily, null, null, null));
		try {
			if(!Main.isDefaultConfig()) {
				ConfigFile config = Main.getConfig();
				JSONObject saves = (JSONObject) config.getObject().get("Saves");
				saves.replace("ColorTypeSelected", colorType);
				saves.replace("ColorTheme", newColor);
				saves.replace("FontFamily", fontFamily);
				saves.replace("ConsoleFontSize", consoleSize);
				saves.replace("ServerDirectory", serverDir);
				saves.replace("JVMArguments", jvmArgs);
				saves.replace("Language", language);
				config.saveConfig();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.apply();
	}
	
	private void showAlert(String header, String text) {
		alert.setHeaderText(Main.getBundle().getString(header));
		alert.setContentText(Main.getBundle().getString(text));
		Toolkit.getDefaultToolkit().beep();
		alert.showAndWait();
		source.toNormalStyle();
	}
	
	@Override
	public void call() {
		alert.setTitle(Main.getBundle().getString("warning"));
	}
}
