package br.sergio.mcsc.model.elements;

import br.sergio.mcsc.model.DisplayServerConsole;

public class TextAppender implements Runnable {
	
	private String text;
	private DisplayServerConsole display;
	
	public TextAppender(DisplayServerConsole display) {
		this.display = display;
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
		this.display = display;
	}
}
