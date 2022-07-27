package br.sergio.mcsc;

import java.io.File;
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
	private static ConfigFile config = new ConfigFile();
	private static List<SettingsListener> styleListeners = new ArrayList<>();
	private static boolean defaultConfig;

	public static void main(String[] args) {
		try {
			config.createFileFromResource();
			config.loadObject();
		} catch(ConfigException e) {
			defaultConfig = true;
			System.err.println(Main.getBundle().getString("configException1"));
			System.err.println(Main.getBundle().getString("configException2"));
			System.err.println(Main.getBundle().getString("configException3"));
		}
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		ServerConsole serverConsole = new ServerConsole();
		serverConsole.setJvmArgs(Currents.jvmArgs);
		serverConsole.setServerDirectory(new File(Currents.serverDir));
		ConsoleWindow consoleWindow = new ConsoleWindow(serverConsole);
		serverConsole.setDisplay(consoleWindow.getDisplay());
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
	
	public static ConfigFile getConfig() {
		return config;
	}

	public static ResourceBundle getBundle() {
		return bundle;
	}

	public static boolean isDefaultConfig() {
		return defaultConfig;
	}
	
	public static void apply() {
		reloadBundle(Currents.language);
		callSettingsListeners();
	}
	
	public static void reloadBundle(String language) {
		switch (language) {
			case "Português" -> Locale.setDefault(new Locale("pt"));
			case "Español" -> Locale.setDefault(new Locale("es"));
			default -> Locale.setDefault(new Locale("en"));
		}
		bundle = ResourceBundle.getBundle("bundles.bundle");
	}
}
