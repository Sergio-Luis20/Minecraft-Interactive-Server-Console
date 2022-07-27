package br.sergio.mcsc.model;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.ServerConsole;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.model.btnHandlers.BtnSettingsHandler;
import br.sergio.mcsc.model.controls.ConsoleButton;
import br.sergio.mcsc.model.controls.ConsoleTextField;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConsoleWindow implements SettingsListener {

	private Stage stage;
	private VBox root;
	private DisplayServerConsole display;
	private ServerConsole console;

	public ConsoleWindow(ServerConsole console) {
		this.console = Objects.requireNonNull(console);
		stage = new Stage();
		root = new VBox();
		stage.setMinWidth(800);
		stage.setMinHeight(600);
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Scene scene = new Scene(root, 3 * screenBounds.getWidth() / 4, 3 * screenBounds.getHeight() / 4);
		scene.getStylesheets().add("text-area-content.css");
		stage.setScene(scene);
		stage.setTitle(Main.getBundle().getString("title"));
		stage.getIcons().add(new Image("/console-icon.png"));
		root.setStyle("-fx-background-color: #000000;");
		root.setOnMouseClicked(event -> root.requestFocus());
		stage.setOnCloseRequest(event -> {
			if(console.isServerRunning()) {
				console.forceStop();
			}
			Platform.exit();
		});
		Main.addSettingsListener(this);
		draw();
	}
	
	private void draw() {
		double buttonWidth = 100;
		double buttonHeight = 25;

		// Display text area
		display = new DisplayServerConsole(stage);
		VBox.setVgrow(display, Priority.ALWAYS);
		root.getChildren().add(display);

		// Control box
		VBox controlBox = new VBox();
		controlBox.setSpacing(20);
		controlBox.setAlignment(Pos.CENTER);
		controlBox.setPrefHeight(110);
		VBox.setVgrow(controlBox, Priority.NEVER);
		root.getChildren().add(controlBox);

		// Command text field
		ConsoleTextField command = new ConsoleTextField();
		command.setPrefHeight(25);
		command.setOnKeyPressed(new CommandApplier(console));
		command.setPromptText("command", true);
		VBox.setMargin(command, new Insets(20, 20, 0, 20));
		VBox.setVgrow(command, Priority.ALWAYS);
		controlBox.getChildren().add(command);

		// Button box
		HBox buttonBox = new HBox();
		buttonBox.setFillHeight(true);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(20);
		VBox.setMargin(buttonBox, new Insets(0, 20, 20, 20));
		VBox.setVgrow(buttonBox, Priority.ALWAYS);
		controlBox.getChildren().add(buttonBox);

		// Start button
		ConsoleButton start = new ConsoleButton("start", true);
		start.setPrefSize(buttonWidth, buttonHeight);
		start.setOnAction(event -> console.startServer());
		HBox.setHgrow(start, Priority.NEVER);
		
		// Reload button
		ConsoleButton reload = new ConsoleButton("reload", true);
		reload.setPrefSize(buttonWidth, buttonHeight);
		reload.setOnAction(event -> console.reloadServer());
		HBox.setHgrow(reload, Priority.NEVER);
		
		// Stop button
		ConsoleButton stop = new ConsoleButton("stop", true);
		stop.setPrefSize(buttonWidth, buttonHeight);
		stop.setOnAction(event -> console.stopServer());
		HBox.setHgrow(stop, Priority.NEVER);
		
		// Restart button
		ConsoleButton restart = new ConsoleButton("restart", true);
		restart.setPrefSize(buttonWidth, buttonHeight);
		restart.setOnAction(event -> console.restartServer());
		HBox.setHgrow(restart, Priority.NEVER);
		
		// Clear button
		ConsoleButton clear = new ConsoleButton("clear", true);
		clear.setPrefSize(buttonWidth, buttonHeight);
		clear.setOnAction(event -> display.clear());
		HBox.setHgrow(clear, Priority.NEVER);
		
		// Folder button
		ConsoleButton folder = new ConsoleButton("folder", true);
		folder.setPrefSize(buttonWidth, buttonHeight);
		folder.setOnAction(event -> {
			if(console.serverDirValidation()) {
				File dir = console.getServerDirectory().getAbsoluteFile().getParentFile();
				try {
					Desktop.getDesktop().open(dir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		HBox.setHgrow(folder, Priority.NEVER);

		// Settings button
		ConsoleButton settings = new ConsoleButton("settings", true);
		settings.setPrefSize(buttonWidth, buttonHeight);
		settings.setOnAction(new BtnSettingsHandler(this));
		HBox.setHgrow(settings, Priority.NEVER);

		buttonBox.getChildren().addAll(start, reload, stop, restart, clear, folder, settings);
	}
	
	@Override
	public void call() {
		stage.setTitle(Main.getBundle().getString("title"));
	}
	
	public DisplayServerConsole getDisplay() {
		return display;
	}

	public Stage getStage() {
		return stage;
	}
	
	public ServerConsole getConsole() {
		return console;
	}
	
}
