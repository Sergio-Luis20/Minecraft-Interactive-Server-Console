package br.sergio.mcsc.utils;

import javafx.scene.paint.Color;

public class Utils {
	
	public static String parseColorWeb(Color color) {
		String string = color.toString();
		return "#" + string.substring(2, 8);
	}
	
	public static void validationNull(Object... obj) {
		if(obj == null) {
			throw new NullPointerException();
		}
		for(Object o : obj) {
			if(o == null) {
				throw new NullPointerException();
			}
		}
	}
}
