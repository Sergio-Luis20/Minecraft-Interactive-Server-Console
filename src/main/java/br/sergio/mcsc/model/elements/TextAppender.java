package br.sergio.mcsc.model.elements;

import br.sergio.mcsc.model.DisplayServerConsole;

import java.util.Objects;

public class TextAppender implements Runnable {
	
	private String text = "";
	private DisplayServerConsole display;
	
	public TextAppender() {
	}
	
	@Override
	public void run() {
		display.appendText(text);
		synchronized(display) {
			display.notify();
		}
	}
	
	public void setText(String text) {
		this.text = text == null ? "" : text;
	}
	
	public void setDisplay(DisplayServerConsole display) {
		this.display = Objects.requireNonNull(display);
	}
}
