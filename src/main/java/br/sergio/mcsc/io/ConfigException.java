package br.sergio.mcsc.io;

public class ConfigException extends Exception {
	
	private static final long serialVersionUID = -6411916960568368816L;
	
	public ConfigException() {
		super();
	}
	
	public ConfigException(String message) {
		super(message);
	}
	
	public ConfigException(Throwable cause) {
		super(cause);
	}
	
	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
