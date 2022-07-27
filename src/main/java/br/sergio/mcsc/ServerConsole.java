package br.sergio.mcsc;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.sergio.mcsc.model.DisplayServerConsole;
import br.sergio.mcsc.model.elements.TextAppender;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ServerConsole {
	
	private Process server;
	private BufferedReader serverInput, serverThrowerInput;
	private BufferedWriter serverOutput;
	private DisplayServerConsole display;
	private Thread serverReader, serverThrower;
	private Thread serverStopping;
    private File serverDir;
	private String jvmArgs;
	private TextAppender textAppender;
	private boolean serverStarting, serverRunning;
	private boolean restarting, stopping;

	public ServerConsole() {
		jvmArgs = "";
		textAppender = new TextAppender();
	}

	public synchronized void startServer() {
		// Check if the server is able to run
		if(serverRunning || serverStarting || !serverDirValidation()) {
			return;
		}
        serverStarting = true;
		
		// Construct command
		List<String> command = new ArrayList<>();
		command.add("java");
		String[] args = jvmArgs.trim().split(" ");
		for (String arg : args) {
			if (arg.isBlank()) {
				continue;
			}
			command.add(arg);
		}
		command.add("-jar");
		command.add(serverDir.getName());
		command.add("-nogui");
		
		// Displays the command for user
		StringBuilder sb = new StringBuilder();
		int size = command.size();
		for(int i = 0; i < size; i++) {
			sb.append(i == size - 1 ? command.get(i) : command.get(i) + " ");
		}
		display.appendText(sb + "\n");
		
		// Make the process
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(serverDir.getAbsoluteFile().getParentFile());
		
		try {
			if(server != null && server.isAlive()) {
				server.destroy();
				serverRunning = false;
			}
			server = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		serverInput = server.inputReader();
		serverOutput = server.outputWriter();
		serverThrowerInput = server.errorReader();
		
		// Activate threads
		resetServerThreads();
		startServerThreads();
		serverRunning = true;
        serverStarting = false;
	}
	
	public synchronized void reloadServer() {
		dispatchCommand("reload");
	}
	
	public synchronized void stopServer() {
		if(stopping) {
			return;
		}
		stopping = true;
		serverStopping = new Thread(() -> {
			if(!serverRunning) {
				stopping = false;
				return;
			}
			dispatchCommand("stop");
			try {
				server.waitFor();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			stopServerThreads();
			print(Main.getBundle().getString("serverClosed"));
			stopping = false;
		});
        serverStopping.setDaemon(true);
		serverStopping.start();
	}
	
	public synchronized void restartServer() {
		if(restarting) {
			return;
		}
		restarting = true;
        Thread serverRestarting = new Thread(() -> {
			if (!serverRunning) {
				restarting = false;
				return;
			}
			stopServer();
			try {
				serverStopping.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			startServer();
			restarting = false;
		});
        serverRestarting.setDaemon(true);
		serverRestarting.start();
	}
	
	public synchronized void dispatchCommand(String command) {
		if(server != null && server.isAlive()) {
			String formattedCommand = command + "\n";
			display.appendText(formattedCommand);
			try {
				serverOutput.write(formattedCommand);
				serverOutput.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void forceStop() {
		if(server != null && server.isAlive()) {
			server.destroyForcibly();
		}
	}
	
	public boolean serverDirValidation() {
		boolean validate = serverDir == null || !serverDir.exists() 
				|| !serverDir.getAbsolutePath().endsWith(".jar") || !serverDir.isFile();
		if(validate) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(display.getOwnerStage());
			alert.setTitle(Main.getBundle().getString("error"));
			alert.setHeaderText(Main.getBundle().getString("invalidServer"));
			alert.setContentText(Main.getBundle().getString("invalidServerText"));
			Toolkit.getDefaultToolkit().beep();
			alert.showAndWait();
		}
		return !validate;
	}
	
	private void startServerThreads() {
		serverRunning = true;
		serverReader.start();
		serverThrower.start();
	}
	
	private synchronized void stopServerThreads() {
		serverRunning = false;
	}
	
	private void resetServerThreads() {
		serverReader = new Thread(() -> {
			while(serverRunning) {
				readText(serverInput);
			}
		});
		serverThrower = new Thread(() -> {
			while(serverRunning) {
				readText(serverThrowerInput);
			}
		});
		serverReader.setDaemon(true);
		serverThrower.setDaemon(true);
	}
	
	private void readText(BufferedReader reader) {
		try {
			String text = reader.readLine();
			if(text == null) {
				return;
			}
			print(text);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void print(String text) {
		synchronized(display) {
			try {
				textAppender.setText(text + "\n");
				Platform.runLater(textAppender);
				display.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isServerRunning() {
		return serverRunning;
	}
	
	public File getServerDirectory() {
		return serverDir;
	}
	
	public void setServerDirectory(File serverDir) {
		this.serverDir = serverDir;
	}
	
	public void setJvmArgs(String args) {
		jvmArgs = args == null ? "" : args;
	}
	
	public void setDisplay(DisplayServerConsole display) {
		this.display = Objects.requireNonNull(display);
		textAppender.setDisplay(display);
	}
}
