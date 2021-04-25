package br.sergio.mcsc.model.elements;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.model.controls.ConsoleTextField;
import br.sergio.mcsc.utils.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class RGBFormatter implements ChangeListener<String>, SettingsListener {
	
	private ConsoleTextField source;
	private Alert alert;
	private Runnable runnable;
	
	public RGBFormatter(ConsoleTextField source, Stage alertOwner) {
		Utils.validationNull(source);
		this.source = source;
		Main.addSettingsListener(this);
		alert = new Alert(AlertType.WARNING);
		alert.initOwner(alertOwner);
		alert.setTitle(Main.getBundle().getString("warning"));
		alert.setHeaderText(Main.getBundle().getString("invalidRGBValue"));
		alert.setContentText(Main.getBundle().getString("rgbWarningText"));
		runnable = new Runnable() {
			
			@Override
			public void run() {
				alert.showAndWait();
				source.clear();
				source.textProperty().addListener(RGBFormatter.this);
			}
			
		};
	}
	
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		int length = newValue.length();
		if(length == 0) {
			return;
		} else if(length > 3) {
			alertAndClear();
			return;
		} else {
			try {
				int number = Integer.parseInt(newValue);
				if(number < 0 || number > 255) {
					alertAndClear();
				}
			} catch(NumberFormatException e) {
				alertAndClear();
			}
		}
	}
	
	private void alertAndClear() {
		source.textProperty().removeListener(this);
		Platform.runLater(runnable);
	}
	
	@Override
	public void call() {
		alert.setTitle(Main.getBundle().getString("warning"));
		alert.setHeaderText(Main.getBundle().getString("invalidRGBValue"));
		alert.setContentText(Main.getBundle().getString("rgbWarningText"));
	}
}
