package br.sergio.mcsc.model.controls;

import br.sergio.mcsc.Main;
import br.sergio.mcsc.SettingsListener;
import br.sergio.mcsc.io.Styler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ConsoleLabel extends Label implements SettingsListener {
	
	private String style;
	private String url;
	private String bundleText;
	private boolean bundle;
	
	{
		try {
			if(!Main.isDefaultConfig()) {
				style = Styler.LABEL.getStyle();
			} else {
				style = "-fx-background-color: #000000; -fx-text-fill: #00ff00; -fx-font-family: \"Lucida Console\";";
			}
		} catch(Exception e) {
			style = "-fx-background-color: #000000; -fx-text-fill: #00ff00; -fx-font-family: \"Lucida Console\";";
		}
	}
	
	public ConsoleLabel(String text, boolean bundle) {
		this(text, bundle, (Node) null);
	}
	
	public ConsoleLabel(String text, boolean bundle, String url) {
		this(text, bundle, url == null ? null : new ImageView(url));
		this.url = url;
	}
	
	public ConsoleLabel(String text, boolean bundle, Node graphic) {
		super(bundle ? Main.getBundle().getString(text) : text, graphic);
		this.bundle = bundle;
		bundleText = text;
		setStyle(style);
		Main.addSettingsListener(this);
		setAlignment(Pos.CENTER_LEFT);
		setPrefHeight(25);
	}
	
	public String getURL() {
		return url;
	}
	
	@Override
	public void call() {
		style = Styler.LABEL.getStyle();
		setStyle(style);
		if(bundle) {
			setText(Main.getBundle().getString(bundleText));
		}
	}
	
	public String getBundleText() {
		return bundleText;
	}
	
	public boolean isBundle() {
		return bundle;
	}
}
