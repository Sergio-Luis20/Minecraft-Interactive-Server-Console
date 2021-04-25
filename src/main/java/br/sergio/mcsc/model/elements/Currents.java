package br.sergio.mcsc.model.elements;

import java.util.Locale;

import org.json.simple.JSONObject;

import br.sergio.mcsc.Main;

public class Currents {
	
	public static String colorType;
	public static String colorTheme;
	public static String fontFamily;
	public static String consoleFontSize;
	public static String spigotDir;
	public static String jvmArgs;
	public static String language;
	
	static {
		try {
			JSONObject saves = (JSONObject) Main.getConfig().getObject().get("Saves");
			colorType = (String) saves.get("ColorTypeSelected");
			colorTheme = (String) saves.get("ColorTheme");
			fontFamily = (String) saves.get("FontFamily");
			consoleFontSize = (String) saves.get("ConsoleFontSize");
			spigotDir = (String) saves.get("SpigotDirectory");
			jvmArgs = (String) saves.get("JVMArguments");
			String lang = (String) saves.get("Language");
			if(lang.isEmpty()) {
				switch(Locale.getDefault().getLanguage()) {
					case "pt":
						language = "Português";
						break;
					case "es":
						language = "Español";
						break;
					default:
						language = "English";
						break;
				}
			} else {
				language = lang;
			}
		} catch(Exception e) {
			colorType = "Basics";
			colorTheme = "#00ff00";
			fontFamily = "\"Lucida Console\"";
			consoleFontSize = "11pt";
			spigotDir = "";
			jvmArgs = "";
			language = "English";
		}
	}
}
