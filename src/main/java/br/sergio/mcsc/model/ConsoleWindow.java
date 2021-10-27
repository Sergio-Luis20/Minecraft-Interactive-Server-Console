package br.sergio.mcsc.model;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import br.sergio.math.Polynomial;
import br.sergio.mcsc.Main;
import br.sergio.mcsc.ServerConsole;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.model.btnHandlers.BtnSettingsHandler;
import br.sergio.mcsc.model.controls.ConsoleButton;
import br.sergio.mcsc.model.controls.ConsoleTextField;
import br.sergio.mcsc.utils.Pair;
import br.sergio.mcsc.utils.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ConsoleWindow implements SettingsListener {

	private static final Pair<Double, Double> SCREEN_DIMENSION;
	private static byte taSpacing = 100;
	private static byte buttonWidth = 100;
	private static byte buttonHeight = 25;
	private static double vSpacing = 50.0 / 3.0;
	private Stage stage;
	private Scene scene;
	private AnchorPane root;
	private DisplayServerConsole display;
	private ServerConsole console;
	private ConsoleTextField command;
	private ConsoleButton start, reload, stop, restart, clear, folder, settings;
	private SettingsWindow settingsWindow;
	
	public ConsoleWindow(ServerConsole console) {
		Utils.validationNull(console);
		this.console = console;
		stage = new Stage();
		root = new AnchorPane();
		stage.setMinWidth(800);
		stage.setMinHeight(600);
		scene = new Scene(root, 3 * SCREEN_DIMENSION.getMale() / 4, 3 * SCREEN_DIMENSION.getFemale() / 4);
		scene.getStylesheets().add("text-area-content.css");
		stage.setScene(scene);
		stage.setTitle(Main.getBundle().getString("title"));
		stage.getIcons().add(new Image("/console-icon.png"));
		scene.widthProperty().addListener(new ResizeListener(ResizeType.WIDTH));
		scene.heightProperty().addListener(new ResizeListener(ResizeType.HEIGHT));
		root.setStyle("-fx-background-color: #000000;");
		root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent event) {
				root.requestFocus();
			}
			
		});
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				if(console.isServerRunning()) {
					console.forceStop();
				}
				console.stopThreads();
				Platform.exit();
			}
			
		});
		Main.addSettingsListener(this);
		draw();
	}
	
	private void draw() {
		double sceneWidth = scene.getWidth();
		double sceneHeight = scene.getHeight();
		
		double hSpacing = (scene.getWidth() - 7 * buttonWidth) / 8;
		double buttonVerticalPos = sceneHeight - buttonHeight - vSpacing;
		
		Polynomial pol = new Polynomial(buttonWidth + hSpacing, hSpacing);
		
		// Display text area
		display = new DisplayServerConsole(stage);
		display.setPrefSize(sceneWidth, sceneHeight - taSpacing);
		display.relocate(0, 0);
		
		// Start button
		start = new ConsoleButton("start", true);
		start.setPrefSize(buttonWidth, buttonHeight);
		start.relocate(pol.f(0), buttonVerticalPos);
		start.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				console.startServer();
			}
			
		});
		
		// Reload button
		reload = new ConsoleButton("reload", true);
		reload.setPrefSize(buttonWidth, buttonHeight);
		reload.relocate(pol.f(1), buttonVerticalPos);
		reload.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				console.reloadServer();
			}
			
		});
		
		// Stop button
		stop = new ConsoleButton("stop", true);
		stop.setPrefSize(buttonWidth, buttonHeight);
		stop.relocate(pol.f(2), buttonVerticalPos);
		stop.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				console.stopServer();
			}
			
		});
		
		// Restart button
		restart = new ConsoleButton("restart", true);
		restart.setPrefSize(buttonWidth, buttonHeight);
		restart.relocate(pol.f(3), buttonVerticalPos);
		restart.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				console.restartServer();
			}
			
		});
		
		// Clear button
		clear = new ConsoleButton("clear", true);
		clear.setPrefSize(buttonWidth, buttonHeight);
		clear.relocate(pol.f(4), buttonVerticalPos);
		clear.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				display.clear();
			}
			
		});
		
		// Folder button
		folder = new ConsoleButton("folder", true);
		folder.setPrefSize(buttonWidth, buttonHeight);
		folder.relocate(pol.f(5), buttonVerticalPos);
		folder.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(console.serverDirValidation()) {
					File folder = console.getServerDirectory().getAbsoluteFile().getParentFile();
					try {
						Desktop.getDesktop().open(folder);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		
		// Settings button
		settings = new ConsoleButton("settings", true);
		settings.setPrefSize(buttonWidth, buttonHeight);
		settings.relocate(pol.f(6), buttonVerticalPos);
		
		// Command text field
		command = new ConsoleTextField();
		command.setPrefSize(sceneWidth - 2 * hSpacing, buttonHeight);
		command.relocate(hSpacing, sceneHeight - 2 * (vSpacing + buttonHeight));
		command.setOnKeyPressed(new CommandApplier(console));
		command.setPromptText("command", true);
		
		root.getChildren().addAll(display, start, reload, stop, restart, clear, folder, settings, command);
	}
	
	public void createSettingsWindow() {
		settingsWindow = new SettingsWindow(this);
		settings.setOnAction(new BtnSettingsHandler(this));
	}
	
	private enum ResizeType {
		WIDTH, HEIGHT;
	}
	
	private class ResizeListener implements ChangeListener<Number> {
		
		private ResizeType type;
		
		public ResizeListener(ResizeType type) {
			Utils.validationNull(type);
			this.type = type;
		}
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			double value = newValue.doubleValue();
			switch(type) {
				case WIDTH:
					double hSpacing = (value - 7 * buttonWidth) / 8;
					Polynomial pol = new Polynomial(buttonWidth + hSpacing, hSpacing);
					display.setPrefWidth(value);
					start.setLayoutX(pol.f(0));
					reload.setLayoutX(pol.f(1));
					stop.setLayoutX(pol.f(2));
					restart.setLayoutX(pol.f(3));
					clear.setLayoutX(pol.f(4));
					folder.setLayoutX(pol.f(5));
					settings.setLayoutX(pol.f(6));
					command.setPrefWidth(value - 2 * hSpacing);
					command.setLayoutX(hSpacing);
					break;
				case HEIGHT:
					double buttonVerticalPos = value - buttonHeight - vSpacing;
					display.setPrefHeight(value - taSpacing);
					start.setLayoutY(buttonVerticalPos);
					reload.setLayoutY(buttonVerticalPos);
					stop.setLayoutY(buttonVerticalPos);
					restart.setLayoutY(buttonVerticalPos);
					clear.setLayoutY(buttonVerticalPos);
					folder.setLayoutY(buttonVerticalPos);
					settings.setLayoutY(buttonVerticalPos);
					command.setLayoutY(value - 2 * (vSpacing + buttonHeight));
					break;
			}
		}
		
	}
	
	@Override
	public void call() {
		stage.setTitle(Main.getBundle().getString("title"));
	}
	
	public DisplayServerConsole getDisplay() {
		return display;
	}
	
	public void setDisplay(DisplayServerConsole display) {
		Utils.validationNull(display);
		this.display = display;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public SettingsWindow getSettingsWindow() {
		return settingsWindow;
	}
	
	public void setSettingsWindow(SettingsWindow settingsWindow) {
		Utils.validationNull(settingsWindow);
		this.settingsWindow = settingsWindow;
	}
	
	public ServerConsole getConsole() {
		return console;
	}
	
	static {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		double width = screenBounds.getWidth();
		double height = screenBounds.getHeight();
		SCREEN_DIMENSION = new Pair<>(width, height);
	}
	
}
