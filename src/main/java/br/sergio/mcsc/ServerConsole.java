package br.sergio.mcsc;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import br.sergio.mcsc.model.DisplayServerConsole;
import br.sergio.mcsc.model.elements.TextAppender;
import br.sergio.mcsc.utils.Utils;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ServerConsole implements Runnable {
	
	private Process server;
	private BufferedReader serverInput, serverThrowerInput;
	private BufferedWriter serverOutput;
	private PipedInputStream input, throwerInput;
	private DisplayServerConsole display;
	private Thread reader, thrower, serverReader, serverThrower;
	private Thread serverStopping, serverRestarting;
	private File spigotDir;
	private String jvmArgs;
	private TextAppender textAppender;
	private PrintStream out, err;
	private boolean running, serverRunning;
	private boolean restarting, stopping;
	private boolean builtInOut;
	
	public ServerConsole() {
		jvmArgs = "";
		textAppender = new TextAppender(display);
		
	}
	
	public ServerConsole(DisplayServerConsole display) {
		this();
		setDisplay(display);
	}
	
	public synchronized void startServer() {
		// Check if the server is able to run
		if(serverRunning || !spigotDirValidation()) {
			return;
		}
		
		// Construct command
		List<String> command = new ArrayList<String>();
		command.add("java");
		String[] args = jvmArgs.trim().split(" "); 
		for(int i = 0; i < args.length; i++) {
			if(args[i].trim().isEmpty()) {
				continue;
			}
			command.add(args[i]);
		}
		command.add("-jar");
		command.add(spigotDir.getName());
		command.add("-nogui");
		
		// Displays the command for user
		StringBuffer sb = new StringBuffer();
		int size = command.size();
		for(int i = 0; i < size; i++) {
			sb.append(i == size - 1 ? command.get(i) : command.get(i) + " ");
		}
		System.out.println(sb.toString());
		
		// Make the process
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(spigotDir.getAbsoluteFile().getParentFile());
		
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
		serverInput = new BufferedReader(new InputStreamReader(server.getInputStream()));
		serverOutput = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
		serverThrowerInput = new BufferedReader(new InputStreamReader(server.getErrorStream()));
		
		// Activate threads
		resetServerThreads();
		startServerThreads();
		serverRunning = true;
	}
	
	public synchronized void reloadServer() {
		dispatchCommand("reload");
	}
	
	public synchronized void stopServer() {
		if(stopping) {
			return;
		}
		stopping = true;
		serverStopping = new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(!serverRunning) {
					stopping = false;
					return;
				}
				dispatchCommand("stop");
				try {
					server.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				stopServerThreads();
				stopping = false;
				System.out.println(Main.getBundle().getString("serverClosed"));
			}
			
		});
		serverStopping.start();
	}
	
	public synchronized void restartServer() {
		if(restarting) {
			return;
		}
		restarting = true;
		serverRestarting = new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(!serverRunning) {
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
			}
			
		});
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
			try {
				serverOutput.write("stop");
				serverOutput.flush();
				server.destroyForcibly();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void constructInputsAndOutputs() {
		try {
			if(builtInOut) {
				return;
			}
			this.out = System.out;
			this.err = System.err;
			input = new PipedInputStream();
			throwerInput = new PipedInputStream();
			PipedOutputStream out = new PipedOutputStream(input);
			PipedOutputStream err = new PipedOutputStream(throwerInput);
			System.setOut(new PrintStream(out, true));
			System.setErr(new PrintStream(err, true));
			resetThreads();
			builtInOut = true;
		} catch (IOException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public boolean spigotDirValidation() {
		boolean validate = spigotDir == null || !spigotDir.exists() || !spigotDir.getAbsolutePath().endsWith(".jar") || !spigotDir.isFile();
		if(validate) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(display.getOwnerStage());
			alert.setTitle(Main.getBundle().getString("error"));
			alert.setHeaderText(Main.getBundle().getString("invalidSpigot"));
			alert.setContentText(Main.getBundle().getString("invalidSpigotText"));
			Toolkit.getDefaultToolkit().beep();
			alert.showAndWait();
		}
		return !validate;
	}
	
	public void startThreads() {
		running = true;
		try {
			reader.start();
			thrower.start();
		} catch(IllegalThreadStateException e) {
			e.printStackTrace();
		}
	}
	
	private void startServerThreads() {
		serverRunning = true;
		serverReader.start();
		serverThrower.start();
	}
	
	public synchronized void stopThreads() {
		System.setOut(out);
		System.setErr(err);
		if(serverRunning) {
			stopServerThreads();
		}
		running = false;
		notifyAll();
		try {
			reader.join();
			input.close();
			thrower.join();
			throwerInput.close();
		} catch(InterruptedException | IOException | NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void stopServerThreads() {
		serverRunning = false;
		notifyAll();
		try {
			serverReader.join();
			serverThrower.join();
			if(server != null && server.isAlive()) {
				serverInput.close();
				serverOutput.close();
				serverThrowerInput.close();
			}
		} catch(InterruptedException | IOException | NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private void resetThreads() {
		reader = new Thread(this);
		thrower = new Thread(this);
		reader.setDaemon(true);
		thrower.setDaemon(true);
	}
	
	private void resetServerThreads() {
		serverReader = new Thread(this);
		serverThrower = new Thread(this);
		serverReader.setDaemon(true);
		serverThrower.setDaemon(true);
	}
	
	@Override
	public void run() {
		try {
			Thread current = Thread.currentThread();
			if(current == reader || current == thrower) {
				while(running) {
					synchronized(display) {
						if(current == reader && input.available() != 0) {
							readText(input);
						} else if(current == thrower && throwerInput.available() != 0) {
							readText(throwerInput);
						}
						display.wait(1);
					}
				}
			} else if(current == serverReader || current == serverThrower) {
				while(serverRunning) {
					synchronized(display) {
						if(current == serverReader && serverInput.ready()) {
							readText(serverInput);
						} else if(current == serverThrower && serverThrowerInput.ready()) {
							readText(serverThrowerInput);
						}
						display.wait(1);
					}
				}
			}
		} catch (InterruptedException | IOException e) {
			if(serverRunning) {
				stopServer();
			}
			if(running) {
				stopThreads();
			}
			e.printStackTrace();
			Platform.exit();
		}
	}
	
	private synchronized void readText(BufferedReader reader) {
		try {
			textAppender.setText(reader.readLine() + "\n");
			Platform.runLater(textAppender);
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void readText(InputStream stream) {
		try {
			byte[] array = new byte[stream.available()];
			stream.read(array);
			textAppender.setText(new String(array));
			Platform.runLater(textAppender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isServerRunning() {
		return serverRunning;
	}
	
	public File getSpigotDirectory() {
		return spigotDir;
	}
	
	public void setSpigotDirectory(File spigotDir) {
		this.spigotDir = spigotDir;
	}
	
	public String getJvmArgs() {
		return jvmArgs;
	}
	
	public void setJvmArgs(String args) {
		jvmArgs = args == null ? "" : args;
	}
	
	public DisplayServerConsole getDisplay() {
		return display;
	}
	
	public void setDisplay(DisplayServerConsole display) {
		Utils.validationNull(display);
		this.display = display;
		textAppender.setDisplay(display);
	}
}
