package br.sergio.mcsc.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributeView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConfigFile {
	
	private File file;
	private InputStream inputStream;
	private JSONObject object;
	
	public ConfigFile() {
		file = new File("config.json");
		inputStream = getClass().getResourceAsStream("/config.json");
	}
	
	public void loadObject() throws ConfigException {
		JSONParser parser = new JSONParser();
		try {
			FileReader reader = new FileReader(file);
			object = (JSONObject) parser.parse(reader);
			reader.close();
		} catch(IOException | ParseException e) {
			throw new ConfigException(e);
		}
	}
	
	public void createFileFromResource() throws ConfigException {
		if(!file.exists()) {
			try {
				createFile();
				byte[] array = new byte[inputStream.available()];
				inputStream.read(array);
				OutputStream outputStream = new FileOutputStream(file);
				outputStream.write(array);
				outputStream.close();
			} catch(IOException e) {
				throw new ConfigException(e);
			}
		}
	}
	
	public JSONObject getObject() {
		return object;
	}
	
	public void saveConfig() throws IOException {
		Files.write(file.toPath(), object.toJSONString().getBytes());
	}
	
	public void saveDefaultConfig() throws ConfigException {
		createFileFromResource();
	}
	
	private void createFile() throws IOException {
		File here = new File("");
		DosFileAttributeView view = Files.getFileAttributeView(here.toPath(), DosFileAttributeView.class);
		view.setReadOnly(false);
		Files.deleteIfExists(file.toPath());
		file.createNewFile();
	}
	
	public void close() throws IOException {
		inputStream.close();
	}
}
