package br.sergio.mcsc.model;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.ServerConsole;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.model.btnHandlers.BtnApplyHandler;
import br.sergio.mcsc.model.controls.ConsoleButton;
import br.sergio.mcsc.model.controls.ConsoleComboBox;
import br.sergio.mcsc.model.controls.ConsoleLabel;
import br.sergio.mcsc.model.controls.ConsoleRadioButton;
import br.sergio.mcsc.model.controls.ConsoleTextField;
import br.sergio.mcsc.model.elements.ConsoleComboBoxCellFactory;
import br.sergio.mcsc.model.elements.Currents;
import br.sergio.mcsc.model.elements.RGBFormatter;
import br.sergio.mcsc.utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsWindow implements SettingsListener {
	
	private Stage stage;
	private Scene scene;
	private AnchorPane root;
	private ToggleGroup group;
	private ConsoleComboBox basicColors, families, sizes, lang;
	private ConsoleTextField red, green, blue, hex, serverField, jvmArgs;
	private ServerConsole console;
	private Map<String, String> sortedColors;
	
	public SettingsWindow(ConsoleWindow consoleWindow) {
		Utils.validationNull(consoleWindow);
		this.console = consoleWindow.getConsole();
		stage = new Stage();
		root = new AnchorPane();
		scene = new Scene(root, 430, 480);
		stage.setResizable(false);
		stage.setTitle(Main.getBundle().getString("settings"));
		stage.getIcons().add(new Image("/gear-icon.png"));
		stage.setScene(scene);
		root.setStyle("-fx-background-color: #000000;");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(consoleWindow.getStage());
		Main.addSettingsListener(this);
		draw();
	}
	
	private void draw() {
		// Variables
		
		// Color theme options
		// Font label
		ConsoleLabel theme = new ConsoleLabel("theme", true);
		theme.relocate(30, 30);
		
		group = new ToggleGroup();
		
		// Create toggle buttons
		ConsoleRadioButton basics = new ConsoleRadioButton("basics", true);
		basics.relocate(30, 60);
		basics.setToggleGroup(group);
		
		ConsoleRadioButton rgb = new ConsoleRadioButton("RGB", false);
		rgb.relocate(30, 100);
		rgb.setToggleGroup(group);
		
		ConsoleRadioButton hexadecimal = new ConsoleRadioButton("Hexadecimal", false);
		hexadecimal.relocate(30, 140);
		hexadecimal.setToggleGroup(group);
		
		// Create the basic colors combo box
		basicColors = new ConsoleComboBox();
		basicColors.setPrefSize(200, 25);
		basicColors.relocate(200, 60);
		sortedColors = new LinkedHashMap<>();
		try {
			JSONObject colors = (JSONObject) Main.getConfig().getObject().get("DefaultColors");
			List<String> sortKeys = new ArrayList<>();
			for(Object obj : colors.keySet()) {
				sortKeys.add((String) obj);
			}
			sortKeys.sort(null);
			for(String color : sortKeys) {
				String hexValue = (String) colors.get(color);
				sortedColors.put(color, hexValue);
				ConsoleLabel label = new ConsoleLabel(color, true, "/color-samples/" + color + ".png");
				basicColors.getItems().add(label);
				if(Currents.colorTheme.equals(hexValue)) {
					basicColors.getSelectionModel().select(label);
				}
			}
		} catch(Exception e) {
			sortedColors.clear();
			e.printStackTrace();
		}
		basicColors.setCellFactory(new ConsoleComboBoxCellFactory());
		
		// Create the rgb fields
		red = new ConsoleTextField();
		red.setPrefSize(45, 25);
		red.relocate(200, 100);
		red.setPromptText("R", false);
		red.textProperty().addListener(new RGBFormatter(red, stage));
		
		green = new ConsoleTextField();
		green.setPrefSize(45, 25);
		green.relocate(250, 100);
		green.setPromptText("G", false);
		green.textProperty().addListener(new RGBFormatter(green, stage));
		
		blue = new ConsoleTextField();
		blue.setPrefSize(45, 25);
		blue.relocate(300, 100);
		blue.setPromptText("B", false);
		blue.textProperty().addListener(new RGBFormatter(blue, stage));
		
		// Create the hexadecimal field
		hex = new ConsoleTextField();
		hex.setPrefSize(100, 25);
		hex.relocate(200, 140);
		hex.setPromptText("Hex");
		
		// Add the switch listener
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				switch(((ConsoleRadioButton) newValue).getText()) {
					case "RGB":
						basicColors.setDisable(true);
						hex.setDisable(true);
						red.setDisable(false);
						green.setDisable(false);
						blue.setDisable(false);
						hex.clear();
						break;
					case "Hexadecimal":
						basicColors.setDisable(true);
						hex.setDisable(false);
						red.setDisable(true);
						green.setDisable(true);
						blue.setDisable(true);
						red.clear();
						green.clear();
						blue.clear();
						break;
					default:
						basicColors.setDisable(false);
						hex.setDisable(true);
						red.setDisable(true);
						green.setDisable(true);
						blue.setDisable(true);
						red.clear();
						green.clear();
						blue.clear();
						hex.clear();
						break;
				}
			}
			
		});
		
		// Select the toggle which is in config.json and put the corresponding value in its node(s)
		switch(Currents.colorType) {
			case "RGB":
				group.selectToggle(rgb);
				Color color = Color.web(Currents.colorTheme);
				red.setText(String.valueOf((int) (color.getRed() * 255)));
				green.setText(String.valueOf((int) (color.getGreen() * 255)));
				blue.setText(String.valueOf((int) (color.getBlue() * 255)));
				break;
			case "Hexadecimal":
				group.selectToggle(hexadecimal);
				hex.setText(Currents.colorTheme);
				break;
			default:
				group.selectToggle(basics);
				loop:
				for(String colorName : sortedColors.keySet()) {
					if(Currents.colorTheme.equals(sortedColors.get(colorName))) {
						for(ConsoleLabel label : basicColors.getItems()) {
							if(label.getText().equals(Main.getBundle().getString(colorName))) {
								basicColors.getSelectionModel().select(label);
								break loop;
							}
						}
					}
				}
				break;
		}
		
		// Font options
		// Font label
		ConsoleLabel font = new ConsoleLabel("font", true);
		font.relocate(30, 180);
		
		// Font family label
		ConsoleLabel family = new ConsoleLabel("family", true);
		family.relocate(30, 220);
		
		// Console font size label
		ConsoleLabel consoleSize = new ConsoleLabel("consoleSize", true);
		consoleSize.relocate(30, 260);
		
		// Family combo box
		families = new ConsoleComboBox();
		families.setPrefSize(200, 25);
		families.relocate(200, 220);
		for(String fam : Font.getFamilies()) {
			ConsoleLabel label = new ConsoleLabel(fam, false);
			families.getItems().add(label);
			if(Currents.fontFamily.equals(fam)) {
				families.getSelectionModel().select(label);
			}
		}
		families.setCellFactory(new ConsoleComboBoxCellFactory());
		
		// Console font size combo box
		sizes = new ConsoleComboBox();
		sizes.setPrefSize(60, 25);
		sizes.relocate(200, 260);
		for(int i = 8; i <= 72; i++) {
			ConsoleLabel label = new ConsoleLabel(String.valueOf(i), false);
			sizes.getItems().add(label);
			if(Currents.consoleFontSize.equals(i + "pt")) {
				sizes.getSelectionModel().select(label);
			}
		}
		sizes.setCellFactory(new ConsoleComboBoxCellFactory());
		
		// Spigot options
		// Spigot directory label
		ConsoleLabel serverLabel = new ConsoleLabel("serverDir", true);
		serverLabel.relocate(30, 300);
		
		// Spigot directory text field
		serverField = new ConsoleTextField();
		serverField.setPrefSize(156, 25);
		serverField.relocate(200, 300);
		serverField.setText(Currents.spigotDir);
		
		// Sets the spigot directory on console
		console.setSpigotDirectory(new File(Currents.spigotDir));
		
		ImageView folderIcon = new ImageView("/folder-icon.png");
		folderIcon.setFitWidth(14);
		folderIcon.setFitHeight(14);
		ConsoleButton search = new ConsoleButton(null, false, folderIcon);
		search.relocate(366, 300);
		search.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				FileChooser chooser = new FileChooser();
				FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(Main.getBundle().getString("jarFile"), "*.jar");
				chooser.setTitle(Main.getBundle().getString("serverDir"));
				chooser.getExtensionFilters().add(filter);
				chooser.setInitialDirectory(new File("").getAbsoluteFile());
				File spigot = chooser.showOpenDialog(stage);
				if(spigot != null) {
					serverField.setText(spigot.getAbsolutePath());
				}
				search.toNormalStyle();
			}
			
		});
		
		// JVM arguments label
		ConsoleLabel jvmArgsLabel = new ConsoleLabel("jvmArgs", true);
		jvmArgsLabel.relocate(30, 340);
		
		// JVM arguments text field
		jvmArgs = new ConsoleTextField();
		jvmArgs.setPrefSize(200, 25);
		jvmArgs.relocate(200, 340);
		jvmArgs.setText(Currents.jvmArgs);
		
		// Language options
		// Language label
		ConsoleLabel langLabel = new ConsoleLabel("language", true);
		langLabel.relocate(30, 380);
		
		// Language combo box
		lang = new ConsoleComboBox();
		lang.setPrefSize(200, 25);
		lang.relocate(200, 380);
		try {
			JSONArray array = (JSONArray) Main.getConfig().getObject().get("Languages");
			boolean hasLang = false;
			for(Object obj : array) {
				String language = new String(((String) obj).getBytes(), StandardCharsets.UTF_8);
				ConsoleLabel label = new ConsoleLabel(language, false);
				lang.getItems().add(label);
				if(Currents.language.equals(language)) {
					lang.getSelectionModel().select(label);
					hasLang = true;
				}
			}
			if(!hasLang) {
				lang.getSelectionModel().select(new ConsoleLabel(Currents.language, false));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		lang.setCellFactory(new ConsoleComboBoxCellFactory());
		
		// General
		// Apply button
		ConsoleButton apply = new ConsoleButton("apply", true);
		apply.setOnAction(new BtnApplyHandler(this, console));
		apply.setPrefSize(100, 25);
		apply.relocate(300, 425);
		
		// Add nodes
		root.getChildren().addAll(theme, basics, rgb, hexadecimal, basicColors, red, green, blue, hex, font, family, consoleSize, families, 
				sizes, serverLabel, serverField, search, jvmArgsLabel, jvmArgs, langLabel, lang, apply);
		
		// Update pending settings
		Main.apply();
	}
	
	@Override
	public void call() {
		stage.setTitle(Main.getBundle().getString("settings"));
	}
	
	// Getters
	
	public Stage getStage() {
		return stage;
	}
	
	public ToggleGroup getRadioButtonsGroup() {
		return group;
	}

	public ConsoleComboBox getBasicColors() {
		return basicColors;
	}

	public ConsoleComboBox getFamilies() {
		return families;
	}

	public ConsoleComboBox getSizes() {
		return sizes;
	}
	
	public ConsoleComboBox getLang() {
		return lang;
	}

	public ConsoleTextField getRed() {
		return red;
	}

	public ConsoleTextField getGreen() {
		return green;
	}

	public ConsoleTextField getBlue() {
		return blue;
	}

	public ConsoleTextField getHex() {
		return hex;
	}

	public ConsoleTextField getServerField() {
		return serverField;
	}
	
	public ConsoleTextField getJVMArgs() {
		return jvmArgs;
	}

	public Map<String, String> getSortedColors() {
		return sortedColors;
	}
}
