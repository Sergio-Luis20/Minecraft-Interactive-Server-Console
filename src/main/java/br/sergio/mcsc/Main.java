package br.sergio.mcsc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import br.sergio.mcsc.io.ConfigException;
import br.sergio.mcsc.io.ConfigFile;
import br.sergio.mcsc.model.ConsoleWindow;
import br.sergio.mcsc.model.elements.Currents;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static ResourceBundle bundle = ResourceBundle.getBundle("bundles.bundle");
	private static boolean defaultConfiguration = false;
	private static Main instance;
	private static ConfigFile config = new ConfigFile();
	private static List<SettingsListener> styleListeners = new ArrayList<>();
	private ConsoleWindow consoleWindow;
	private ServerConsole serverConsole;
	
	public static void main(String[] args) {
		try {
			config.createFileFromResource();
			config.loadObject();
		} catch(ConfigException e) {
			defaultConfiguration = true;
			System.err.println(Main.getBundle().getString("configException1"));
			System.err.println(Main.getBundle().getString("configException2"));
			System.err.println(Main.getBundle().getString("configException3"));
		}
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		instance = this;
		serverConsole = new ServerConsole();
		consoleWindow = new ConsoleWindow(serverConsole);
		consoleWindow.createSettingsWindow();
		serverConsole.setDisplay(consoleWindow.getDisplay());
		serverConsole.constructInputsAndOutputs();
		serverConsole.startThreads();
		primaryStage = consoleWindow.getStage();
		primaryStage.show();
	}
	
	@Override
	public void stop() {
		try {
			config.saveConfig();
			config.close();
		} catch(Throwable e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public ConsoleWindow getConsoleWindow() {
		return consoleWindow;
	}
	
	public static void addSettingsListener(SettingsListener listener) {
		styleListeners.add(listener);
	}
	
	public static void removeSettingsListener(SettingsListener listener) {
		styleListeners.remove(listener);
	}
	
	public static void callSettingsListeners() {
		for(SettingsListener listener : styleListeners) {
			if(listener != null) {
				listener.call();
			}
		}
	}
	
	public static boolean isDefaultConfig() {
		return defaultConfiguration;
	}
	
	public static ConfigFile getConfig() {
		return config;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public static ResourceBundle getBundle() {
		return bundle;
	}
	
	public static void apply() {
		Main.reloadBundle(Currents.language);
		Main.callSettingsListeners();
	}
	
	public static void reloadBundle(String language) {
		switch(language) {
			case "Português":
				Locale.setDefault(new Locale("pt"));
				break;
			case "Español":
				Locale.setDefault(new Locale("es"));
				break;
			default:
				Locale.setDefault(new Locale("en"));
				break;
		}
		bundle = ResourceBundle.getBundle("bundles.bundle");
	}
}
